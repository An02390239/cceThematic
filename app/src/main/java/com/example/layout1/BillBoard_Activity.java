package com.example.layout1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

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
import java.util.List;

public class BillBoard_Activity extends AppCompatActivity {
    private final String My_PostURL = Login_Fragment.My_PostURL;
    private ImageView BBA_small_login;
    private TextView BBA_exam, BBA_addorquit, BBA_sup;
    private String BBA_post_category = "";
    private AlertDialog.Builder BBA_setmessage;
    private FloatingActionButton BBA_fab;
    private SubActionButton BBA_sab1, BBA_sab2, BBA_sab3;
    private GlobalVariable globalVariable;
    private ViewPager2 vp;
    int []images ={R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d,R.drawable.e};//可用arraylist

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.billboard_activity);

        init();//宣告物件(初始化)

        BBA_setmessage.setPositiveButton("確認", (dialogInterface, i) -> {

        });
        imagepager();//系活動圖片

        new FloatingActionMenu.Builder(this)
                .addSubActionView(BBA_sab1) //新增按鈕到menu
                .addSubActionView(BBA_sab2)
                .addSubActionView(BBA_sab3)
                .setStartAngle(-45) //展開的角度
                .setEndAngle(-135)
                .attachTo(BBA_fab) //附加到宣告的fab按鍵
                .build();
        BBA_sab1.setOnClickListener(v ->
                Toast.makeText(this,"test",Toast.LENGTH_LONG).show());//測試用

        OnClickListener onClickListener = new OnClickListener();
        BBA_exam.setOnClickListener(onClickListener);
        BBA_addorquit.setOnClickListener(onClickListener);
        BBA_sup.setOnClickListener(onClickListener);
        BBA_small_login.setOnClickListener(onClickListener);

    }

    private class OnClickListener implements View.OnClickListener{
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {

            Thread thread = new Thread(HV_post);
            switch (v.getId()){
                case R.id.text_exam:
                    BBA_post_category = "考試";
                    thread.start();
                    break;
                case R.id.text_add_or_quit:
                    BBA_post_category = "加退選";
                    thread.start();
                    break;
                case R.id.text_sup:
                    Toast.makeText(BillBoard_Activity.this,"SUP",Toast.LENGTH_SHORT).show();
                    BBA_post_category = "補助";
                    //thread.start();
                    break;
                case R.id.small_login_button:
                    Intent intent1 = new Intent();
                    intent1.setClass(BillBoard_Activity.this,Login_FragmentActivity.class);
                    startActivity(intent1);
                    BillBoard_Activity.this.finish();
                    break;
            }
            globalVariable.set_category(BBA_post_category);//存取category讓他銜接至BoardList
        }
    }


    private final Runnable HV_post = new Runnable() {
        @Override
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost myPost = new HttpPost(My_PostURL);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("function","boardList"));
            params.add(new BasicNameValuePair("category", BBA_post_category));
            //將參數加入params這個List中等待post
            try {
                //帶上post參數且將參數設置為UTF-8的格式
                myPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                //執行
                HttpResponse response = new DefaultHttpClient().execute(myPost);
                //把結果(php的echo內容)取出轉換成String型態且為UTF-8的格式
                String result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
                //將result用\n分割
                if (result.equals("錯誤")|| result.equals("請輸入功能")|| result.equals("資料庫連線錯誤")){
                    BBA_setmessage.setMessage(result);
                    Looper.prepare();
                    BBA_setmessage.show();
                    Looper.loop();
                }else{
                    String[] boardList = result.split("[\n]");
                    globalVariable.setBoardList(boardList);
                    Intent intent = new Intent(BillBoard_Activity.this, BillBoardList_Activity.class);
                    startActivity(intent);
                    BillBoard_Activity.this.finish();
                }
            }catch (IOException e){
                e.printStackTrace();// 如果出事，回傳錯誤訊息
            }finally {
                client.getConnectionManager().shutdown();
            }
        }
    };
    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init(){
        BBA_exam = findViewById(R.id.text_exam);
        BBA_addorquit = findViewById(R.id.text_add_or_quit);
        BBA_sup = findViewById(R.id.text_sup);
        BBA_small_login = findViewById(R.id.small_login_button);
        vp = findViewById(R.id.pager);
        BBA_setmessage = new AlertDialog.Builder(BillBoard_Activity.this);
        BBA_fab = findViewById(R.id.fab);
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        BBA_sab1 = itemBuilder.setBackgroundDrawable(getDrawable(R.drawable.searchcir)).build();
        BBA_sab2 = itemBuilder.build();
        BBA_sab3 = itemBuilder.build();
        globalVariable = (GlobalVariable) this.getApplicationContext();
    }

    private void imagepager(){

        MainAdapter adapter;
        adapter = new MainAdapter(images);
        vp.setAdapter(adapter);

        vp.setClipToPadding(false);
        vp.setClipChildren(false);
        vp.setOffscreenPageLimit(3);
        vp.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(8));
        transformer.addTransformer((page, position) -> {
            float v = 1 -Math.abs(position);
            page.setScaleY(0.85f + v * 0.15f);
        });
        vp.setPageTransformer(transformer);

    }
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
        RE.setClass(BillBoard_Activity.this,Login_FragmentActivity.class);
        startActivity(RE);
        BillBoard_Activity.this.finish();
    }
}