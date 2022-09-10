package com.example.layout1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

//編輯文章頁面
public class EditArticle_Activity extends AppCompatActivity {

    private final String My_PostURL = Login_Fragment.My_PostURL;
    private String EAA_username = "C108110158" , EAA_retitle, EAA_recontext, EAA_retheme, EAA_retime,EAA_postresult;
    private String EAA_articleid,EAA_title,EAA_theme,EAA_context,EAA_time,EAA_poster;
    private ImageView avatarImage;
    private EditText EAA_contextedit,EAA_titleedit,EAA_themeedit;
    private ImageButton pictureButton;
    private Intent fromAA;
    private AlertDialog.Builder EAA_postdia;
    private SimpleDateFormat nowDate;
    private boolean EAA_check = false;
    private TextView EAA_postertext;
    private GlobalVariable EAA_getusername,EAA_refresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editarticle_activity);
        init();
        EAA_username = EAA_getusername.getUsername();
        fromAA = getIntent();
        EAA_title = fromAA.getStringExtra("titletoEAA");
        EAA_titleedit.setText(EAA_title);
        EAA_context = fromAA.getStringExtra("contexttoEAA");
        EAA_contextedit.setText(EAA_context);
        EAA_theme = fromAA.getStringExtra("themetoEAA");
        EAA_themeedit.setText(EAA_theme);
        EAA_articleid = fromAA.getStringExtra("articleidtoEAA");
        EAA_time = fromAA.getStringExtra("timetoEAA");
        EAA_poster = fromAA.getStringExtra("postertoEAA");
        EAA_postertext.setText(EAA_poster);

        EAA_postdia.setPositiveButton("確認", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //編輯成功
                if (EAA_check){
                    EAA_refresh.setRefresh(true);
                    Intent edit = new Intent(EditArticle_Activity.this, Article_Activity.class);

                    edit.putExtra("titletoAA",EAA_retitle);
                    edit.putExtra("timetoAA",EAA_retime);
                    edit.putExtra("contexttoAA",EAA_recontext);
                    edit.putExtra("themetoAA",EAA_retheme);
                    edit.putExtra("articleidtoAA",EAA_articleid);
                    edit.putExtra("postertoAA",EAA_poster);
                    startActivity(edit);

                    EditArticle_Activity.this.finish();


                }
            }
        });

    }


    private void init(){
        EAA_getusername = (GlobalVariable)this.getApplicationContext();
        EAA_refresh = (GlobalVariable)this.getApplicationContext();
        avatarImage = findViewById(R.id.AA_avatarImage);
        EAA_contextedit = findViewById(R.id.EAA_contentEdit);
        EAA_titleedit = findViewById(R.id.EAA_titleEdit);
        EAA_themeedit = findViewById(R.id.EAA_themeEdit);
        pictureButton = findViewById(R.id.EAA_pictureButton);
        EAA_postertext = findViewById(R.id.EAA_posterText);

        EAA_contextedit.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        EAA_contextedit.setSingleLine(false);
        EAA_contextedit.setHorizontallyScrolling(false);

        EAA_titleedit.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        EAA_titleedit.setSingleLine(false);
        EAA_titleedit.setHorizontallyScrolling(false);

        EAA_themeedit.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        EAA_themeedit.setSingleLine(false);
        EAA_themeedit.setHorizontallyScrolling(false);

        EAA_postdia = new AlertDialog.Builder(EditArticle_Activity.this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 填充menu的main.xml檔案; 給action bar新增條目
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        //按下編輯
        if(id == R.id.finish){
            nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.TAIWAN);
            nowDate.setTimeZone(TimeZone.getTimeZone("GMT+08"));//台灣為GMT+8時區
            Date curDate = new Date(System.currentTimeMillis()) ;
            EAA_retime = nowDate.format(curDate);
            EAA_retitle = EAA_titleedit.getText().toString();
            EAA_recontext = EAA_contextedit.getText().toString();
            EAA_retheme = EAA_themeedit.getText().toString();
            ModifyRunnable modifyRunnable = new ModifyRunnable();
            Thread modify = new Thread(modifyRunnable);
            modify.start();
        }

        return super.onOptionsItemSelected(item);
    }

    //藉由Post編輯當前公告
    public class ModifyRunnable implements Runnable{
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost myPost = new HttpPost(My_PostURL);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("function","announcementModify"));
            params.add(new BasicNameValuePair("articleid",EAA_articleid));
            params.add(new BasicNameValuePair("username",EAA_username));
            params.add(new BasicNameValuePair("retitle", EAA_retitle));
            params.add(new BasicNameValuePair("recontext", EAA_recontext));
            params.add(new BasicNameValuePair("retheme", EAA_retheme));
            params.add(new BasicNameValuePair("retime", EAA_retime));
            //將參數加入params這個List中等待post
            try {
                myPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                //帶上post參數且將參數設置為UTF-8的格式
                HttpResponse response = new DefaultHttpClient().execute(myPost);
                //執行
                EAA_postresult = EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
                if ( EAA_postresult.contains("\n")){
                    String[] split = EAA_postresult.split("\n");
                    EAA_postresult = split[1];
                }
                if (EAA_postresult.equals("更新成功"))EAA_check = true;
                //把結果(php的echo內容)取出轉換成String型態且為UTF-8的格式
                EAA_postdia.setTitle("");
                EAA_postdia.setMessage(EAA_postresult);
                Looper.prepare();
                EAA_postdia.show();
                Looper.loop();

            }catch (IOException e){
                e.printStackTrace();// 如果出事，回傳錯誤訊息
            }finally {
                client.getConnectionManager().shutdown();
            }
        }// run()
    }// class TimerRunnable

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            ConfirmExit();//按返回鍵，則執行退出確認
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public void ConfirmExit(){//退出確認
            Intent backtoAA = new Intent(EditArticle_Activity.this,
                    Article_Activity.class);
            backtoAA.putExtra("titletoAA",EAA_title);
            backtoAA.putExtra("timetoAA",EAA_time);
            backtoAA.putExtra("contexttoAA",EAA_context);
            backtoAA.putExtra("themetoAA",EAA_theme);
            backtoAA.putExtra("articleidtoAA",EAA_articleid);
            backtoAA.putExtra("postertoAA",EAA_poster);
            startActivity(backtoAA);
            EditArticle_Activity.this.finish();
    }

}