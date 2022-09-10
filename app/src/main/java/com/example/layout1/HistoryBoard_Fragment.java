package com.example.layout1;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class HistoryBoard_Fragment extends Fragment {
//瀏覽紀錄 這裡想說應該不用圖片

    private final String My_PostURL = Login_Fragment.My_PostURL;
    private ListView historyList;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> HBF_content,HBF_data,HBF_alldata,HBF_alltitle,HBF_allusername;
    private String HBF_username,HBF_result,HBF_title,HBF_entrytime;
    private String[] HBF_history,HBF_persondata_aftersplit;
    private HistoryBoardHandler historyBoardHandler = new HistoryBoardHandler();
    private GlobalVariable HBF_getdata,HBF_setpersonaldata;
    private boolean change = true,checkhistory = false;
    private int data_index = 0;
    private AlertDialog.Builder HBF_delhistorydia;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.history_board_fragment, container, false);
        requireActivity().setTitle("瀏覽紀錄");
        HBF_getdata = (GlobalVariable)requireActivity().getApplicationContext();
        HBF_setpersonaldata = (GlobalVariable)requireActivity().getApplicationContext();
        HBF_username = HBF_getdata.getUsername();
        HBF_alldata = HBF_getdata.getArrayList();


        historyList = v.findViewById(R.id.historyList);
        HBF_data = new ArrayList<String>();
        HBF_content = new ArrayList<String>();
        HBF_allusername = new ArrayList<>();
        HBF_delhistorydia = new AlertDialog.Builder(requireActivity());
        HistoryBoardRunnable historyBoardHandler = new HistoryBoardRunnable();
        Thread historyb = new Thread(historyBoardHandler);
        historyb.start();
        OnItemClickListener onItemClickListener = new OnItemClickListener();
        historyList.setOnItemClickListener(onItemClickListener);


        HBF_delhistorydia.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Thread delhistory = new Thread(new HistoryBoardDelRunnable());
                delhistory.start();
                //requireActivity().recreate();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setCancelable(false);

        return v;
    }

    private class OnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int i = (int)id;

            String[] split = HBF_content.get(i).split("\n");
            HBF_title = split[0];
            String[] add = null;
            ArrayList<String> alltitle = new ArrayList<>();
            //從所有資料裡面抓出文章標題
            for (int j = 0;j < HBF_alldata.size();j++){
                add = HBF_alldata.get(j).split("u,4t;4e04");
                alltitle.add(add[1]);
                Log.e("add[6]",add[6]+"");
                HBF_allusername.add(add[6]);
            }
            //比對點選項目的標題與所有文章標題中何者相符並記錄其所在位置
            for (int k = 0;k < alltitle.size();k++){
                if (alltitle.get(k).contains(HBF_title)){
                    data_index = k;
                }
            }
            PersonalInformationRunnable p = new PersonalInformationRunnable();
            Thread getpersondata = new Thread(p);
            getpersondata.start();





        }
    }




    //藉由Post從資料庫拿出該使用者的個人資料
    private class PersonalInformationRunnable implements Runnable{
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost myPost = new HttpPost(My_PostURL);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("function","personalInformation"));
            params.add(new BasicNameValuePair("username",HBF_allusername.get(data_index)));
            //將參數加入params這個List中等待post
            try {
                myPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                //帶上post參數且將參數設置為UTF-8的格式
                HttpResponse response = new DefaultHttpClient().execute(myPost);
                //執行
                HBF_result = EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
                //把結果(php的echo內容)取出轉換成String型態且為UTF-8的格式
                HBF_persondata_aftersplit = HBF_result.split("u,4t;4e04");
            }catch (IOException e){
                e.printStackTrace();// 如果出事，回傳錯誤訊息
            }finally {
                client.getConnectionManager().shutdown();
            }
            HBF_setpersonaldata.setPersonaldata(HBF_persondata_aftersplit);
            getTime();
            //點選紀錄跳轉至該公告頁面
            Intent toAA = new Intent(requireActivity(),Article_Activity.class);
            toAA.putExtra("context_toAA",HBF_alldata.get(data_index));
            toAA.putExtra("entrytime",HBF_entrytime);
            startActivity(toAA);
        }// run()
    }

    private void getTime(){
        SimpleDateFormat nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.TAIWAN);
        nowDate.setTimeZone(TimeZone.getTimeZone("GMT+08"));//台灣為GMT+8時區
        Date curDate = new Date(System.currentTimeMillis()) ;
        HBF_entrytime = nowDate.format(curDate);
    }

    public class HistoryBoardHandler extends Handler{
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            HBF_data.addAll(Arrays.asList(bundle.getStringArray("historyboard")));
            if (HBF_data.get(0).contains("\n")){
                String[] split = HBF_data.get(0).split("\n");
                HBF_data.set(0,split[1]);
            }
            for (int i=0;i<HBF_data.size()-1;i+=3){
                HBF_content.add(/*HBF_data.get(i)+"\n"+*/HBF_data.get(i+1)+"\n"+HBF_data.get(i+2));
            }
            arrayAdapter = new ArrayAdapter<String>(requireActivity(),R.layout.listitem, HBF_content);
            historyList.setAdapter(arrayAdapter);
        }
    }

    public class HistoryBoardRunnable implements Runnable{
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost myPost = new HttpPost(My_PostURL);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("function","historyB"));
            params.add(new BasicNameValuePair("username",HBF_username));
            //將參數加入params這個List中等待post
            try {
                myPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                //帶上post參數且將參數設置為UTF-8的格式
                HttpResponse response = new DefaultHttpClient().execute(myPost);
                //執行
                HBF_result = EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
                if (HBF_result.contains("\n")){
                    String[] split = HBF_result.split("\n");
                    HBF_result = split[1];
                }
                //把結果(php的echo內容)取出轉換成String型態且為UTF-8的格式
                HBF_history = HBF_result.split("u,4t;4e04");
                if (HBF_result.equals("失敗")){
                    checkhistory = false;
                }else {
                    checkhistory = true;
                }
            }catch (IOException e){
                e.printStackTrace();// 如果出事，回傳錯誤訊息
            }finally {
                client.getConnectionManager().shutdown();
            }
            Message msg = historyBoardHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putStringArray("historyboard", HBF_history);
            msg.setData(bundle);
            historyBoardHandler.sendMessage(msg);
        }// run()
    }


    public class HistoryBoardDelRunnable implements Runnable{
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost myPost = new HttpPost(My_PostURL);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("function","historyDel"));
            params.add(new BasicNameValuePair("username",HBF_username));
            //將參數加入params這個List中等待post
            try {
                myPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                //帶上post參數且將參數設置為UTF-8的格式
                HttpResponse response = new DefaultHttpClient().execute(myPost);
                //執行
                HBF_result = EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
                //把結果(php的echo內容)取出轉換成String型態且為UTF-8的格式
                if (HBF_result.contains("\n")){
                    String[] split = HBF_result.split("\n");
                    HBF_result = split[1];
                }
                Looper.prepare();
                Toast.makeText(requireActivity(), HBF_result+"", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }catch (IOException e){
                e.printStackTrace();// 如果出事，回傳錯誤訊息
            }finally {
                client.getConnectionManager().shutdown();
            }
        }// run()
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();
        if(id == 0){
            if (checkhistory){
                HBF_delhistorydia.setMessage("確定要清空瀏覽紀錄?");
                HBF_delhistorydia.show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onPrepareOptionsMenu(Menu menu){
        menu.clear();

        if(change){
            menu.add(0,0,0,"刪除").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onResume(){
        super.onResume();
        requireActivity().setTitle("瀏覽紀錄");

    }
    @Override
    public void onPause(){
        super.onPause();
        requireActivity().setTitle("瀏覽紀錄");

    }


    @Override
    public void onStop(){
        super.onStop();
        requireActivity().setTitle("個人");

    }
}