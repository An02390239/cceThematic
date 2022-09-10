package com.example.layout1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BillBoardList_Activity extends AppCompatActivity {
    private final String My_PostURL = Login_Fragment.My_PostURL;
    private ListView BBLA_listview;
    private ArrayList<String> dataSource;
    private ArrayAdapter<String> arrayAdapter;
    private String[] BBLA_getlist;
    private String BBLA_text, BBLA_date, BBLA_name, BBLA_category, BBLA_result;
    private final String[] B = {};
    private GlobalVariable globalVariable;
    private AlertDialog.Builder BBLA_error, BBLA_boardtest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.billboardlist_activity);
        init();

        BBLA_error.setPositiveButton("確認", (dialogInterface, i) -> {

        });
        BBLA_boardtest.setPositiveButton("確認", (dialogInterface, i) -> {

        });
        if (BBLA_getlist != null) {
            dataSource.addAll(Arrays.asList(BBLA_getlist));
            arrayAdapter = new ArrayAdapter<String>(this, R.layout.billboardlist_activity, dataSource);
            BBLA_listview.setAdapter(arrayAdapter);
        }else{
            BBLA_error.setTitle("錯誤");
            BBLA_error.setMessage("資料為空");
            BBLA_error.show();
        }
        setToolbar();
        BBLA_listview.setOnItemClickListener((adapterView, view, index, l) -> {

            BBLA_text = BBLA_getlist[index];
            segmentation();
            Thread thread = new Thread(Board_post);
            thread.start();


        });

    }
    private void segmentation(){
        //匹配指定範圍內的數字
        String regEx = "[^0-9,-]";
        String regEx1 = "[0-9,-]";
        //Pattern是一個正則表示式經編譯後的表現模式
        Pattern p = Pattern.compile(regEx);
        Pattern p1 = Pattern.compile(regEx1);
        // 一個Matcher物件是一個狀態機器，它依據Pattern物件做為匹配模式對字串展開匹配檢查。
        Matcher m = p.matcher(BBLA_text);
        Matcher m1 = p1.matcher(BBLA_text);
        //將輸入的字串中非數字部分用""取代並存入一個字串
        BBLA_date = m.replaceAll("").trim();
        BBLA_name = m1.replaceAll("").trim();
    }

    private void init(){
        BBLA_listview = findViewById(R.id.LV);
        dataSource = new ArrayList<String>();
        globalVariable = (GlobalVariable) this.getApplicationContext();
        BBLA_getlist = globalVariable.getBoardList();
        BBLA_category = globalVariable.get_category();
        BBLA_error = new AlertDialog.Builder(this);
        BBLA_boardtest = new AlertDialog.Builder(this);
    }
    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.boardlist_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        toolbar.setTitleTextColor(Color.BLACK);
        //toolbar.setBackgroundColor(Color.WHITE);
        /*設置標題前方的Icon圖樣*/
        toolbar.setNavigationIcon(R.drawable.back);
        //設置前方Icon與Title之距離
        //toolbar.setContentInsetStartWithNavigation(width/3);
        //toolbar.setTitleMarginStart(0);

        //設置Icon圖樣的點擊事件
        toolbar.setNavigationOnClickListener(v-> {
            Intent back = new Intent();
            back.setClass(BillBoardList_Activity.this,BillBoard_Activity.class);
            startActivity(back);
            BillBoardList_Activity.this.finish();
        });
    }
    private final Runnable Board_post = new Runnable() {
        @Override
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost myPost = new HttpPost(My_PostURL);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("function","board"));
            params.add(new BasicNameValuePair("category", BBLA_category));
            params.add(new BasicNameValuePair("date", BBLA_date));
            params.add(new BasicNameValuePair("name", BBLA_name));
            //將參數加入params這個List中等待post
            try {
                //帶上post參數且將參數設置為UTF-8的格式
                myPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                //執行
                HttpResponse response = new DefaultHttpClient().execute(myPost);
                //把結果(php的echo內容)取出轉換成String型態且為UTF-8的格式
                BBLA_result = EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
                String[] content= BBLA_result.split("\n");
                Intent intent = new Intent(BillBoardList_Activity.this,BillBoardContent_Activity.class);
                intent.putExtra("boardtitle",content[0]);
                intent.putExtra("boardcontent",content[1]);
                startActivity(intent);
            }catch (IOException e){
                e.printStackTrace();// 如果出事，回傳錯誤訊息
            }finally {
                client.getConnectionManager().shutdown();
            }
        }
    };
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            ConfirmExit();//按返回鍵，則執行退出確認
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public void ConfirmExit(){//退出確認
        Intent RE = new Intent();
        RE.setClass(BillBoardList_Activity.this,BillBoard_Activity.class);
        startActivity(RE);
        globalVariable.setBoardList(B);//用空陣列來做清空
        this.finish();
    }
}