package com.example.layout1;

import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PeopleSub_Fragment extends Fragment {
    //訂閱列表
    private final String My_PostURL = Login_Fragment.My_PostURL;
    private ListView PSF_subList;
    private ArrayList<String> PSF_userlist;
    private String PSF_username,PSF_result;
    private String[] PSF_data;
    private SubUserListHandler subUserListHandler = new SubUserListHandler();
    private GlobalVariable PSF_getusername;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.people_sub_fragment, container, false);
        requireActivity().setTitle("訂閱列表");
        PSF_getusername = (GlobalVariable) requireActivity().getApplicationContext();
        PSF_subList = v.findViewById(R.id.sublist);
        PSF_userlist = new ArrayList<>();
        PSF_username = PSF_getusername.getUsername();
        SubUserListRunnable subUserListRunnable = new SubUserListRunnable();
        Thread subuserlist = new Thread(subUserListRunnable);
        subuserlist.start();



        return v;
    }
    public class SubUserListHandler extends Handler{
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            PSF_userlist.addAll(Arrays.asList(bundle.getStringArray("subuserlist")));

            PeopleSubItem peopleSubItem = null;
            ArrayList<PeopleSubItem> peopleSubItemList = new ArrayList<>();
            PeopleSubAdapter arrayAdapter = null;
            for (int i=0;i<PSF_userlist.size();i++){
                peopleSubItem = new PeopleSubItem(PSF_userlist.get(i),R.drawable.eye_off);
                peopleSubItemList.add(peopleSubItem);
            }
            arrayAdapter = new PeopleSubAdapter(requireActivity(),
                    R.layout.personal_sub_item,peopleSubItemList);
            PSF_subList.setAdapter(arrayAdapter);
        }
    }

    //抓取訂閱者列表
    public class SubUserListRunnable implements Runnable{
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost myPost = new HttpPost(My_PostURL);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("function","subscribeUList"));
            params.add(new BasicNameValuePair("username",PSF_username));
            //將參數加入params這個List中等待post
            try {
                myPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                //帶上post參數且將參數設置為UTF-8的格式
                HttpResponse response = new DefaultHttpClient().execute(myPost);
                //執行
                PSF_result = EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
                //把結果(php的echo內容)取出轉換成String型態且為UTF-8的格式
                PSF_data = PSF_result.split("u,4t;4e04");
                if (PSF_data[0].contains("\n")){
                    String[] split = PSF_data[0].split("\n");
                    PSF_data[0] = split[1];
                }
            }catch (IOException e){
                e.printStackTrace();// 如果出事，回傳錯誤訊息
            }finally {
                client.getConnectionManager().shutdown();
            }
            Message msg = subUserListHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putStringArray("subuserlist", PSF_data);
            msg.setData(bundle);
            subUserListHandler.sendMessage(msg);
        }// run()
    }

    @Override
    public void onPause(){
        super.onPause();
        requireActivity().setTitle("個人");
    }
}