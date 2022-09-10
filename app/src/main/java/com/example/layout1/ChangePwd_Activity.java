package com.example.layout1;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

public class ChangePwd_Activity extends AppCompatActivity {
    private final String My_PostURL = Login_Fragment.My_PostURL;
    private String CPA_result, CPA_account, CPA_oldpwd, CPA_newpwd, CPA_checknewpwd;
    private int CPA_warning = 0;
    private AlertDialog.Builder CPA_dialog;
    private EditText CPA_account_edit, CPA_oldpwd_edit, CPA_newpwd_edit, CPA_checknewpwd_edit;
    private Button CPA_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_pwd_activity);

        init();
        CPA_dialog.setPositiveButton("確定", (dialog, which) -> {
            if (CPA_result.equals("修改密碼成功")){
                Intent re_to_B = new Intent(ChangePwd_Activity.this,Login_FragmentActivity.class);
                startActivity(re_to_B);
                CPA_warning =0;
            }else if(CPA_warning ==3){
                CPA_dialog.setTitle("警告");
                CPA_dialog.setMessage("錯誤三次");
                CPA_dialog.show();
                CPA_warning =0;
            }
        });
        OnClickListener onClickListener = new OnClickListener();
        CPA_button.setOnClickListener(onClickListener);
    }
    private final Runnable CPA_post = new Runnable() {
        @Override
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost myPost = new HttpPost(My_PostURL);

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("username", CPA_account));
            params.add(new BasicNameValuePair("oldpwd", CPA_oldpwd));
            params.add(new BasicNameValuePair("newpwd", CPA_newpwd));
            params.add(new BasicNameValuePair("checknewpwd", CPA_checknewpwd));
            params.add(new BasicNameValuePair("function","changepwd"));
            try {
                myPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                //帶上post參數且將參數設置為UTF-8的格式
                HttpResponse response = new DefaultHttpClient().execute(myPost);
                //執行
                CPA_result = EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
                //把結果(php的echo內容)取出轉換成String型態且為UTF-8的格式
                if (CPA_result.contains("\n")){
                    String[] split = CPA_result.split("\n");
                    CPA_result = split[1];
                }
                if(!(CPA_result.equals("修改密碼成功"))) CPA_warning++;
                CPA_dialog.setTitle("");
                CPA_dialog.setMessage(CPA_result);
                Looper.prepare();
                CPA_dialog.show();
                Looper.loop();
            }catch (IOException e){
                e.printStackTrace();// 如果出事，回傳錯誤訊息
            }finally {
                client.getConnectionManager().shutdown();
            }
        }
    };
    private void init(){
        CPA_account_edit = findViewById(R.id.cpa_account);
        CPA_oldpwd_edit = findViewById(R.id.cpa_oldpwd);
        CPA_newpwd_edit = findViewById(R.id.cpa_newpwd);
        CPA_checknewpwd_edit = findViewById(R.id.cpa_checknewpwd);
        CPA_dialog = new AlertDialog.Builder(ChangePwd_Activity.this);
        CPA_button = findViewById(R.id.cpa_send);
    }
    private class OnClickListener implements View.OnClickListener{
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.cpa_send:
                    CPA_account = CPA_account_edit.getText().toString();
                    CPA_oldpwd = CPA_oldpwd_edit.getText().toString();
                    CPA_newpwd = CPA_newpwd_edit.getText().toString();
                    CPA_checknewpwd = CPA_checknewpwd_edit.getText().toString();
                    Thread thread = new Thread(CPA_post);
                    thread.start();
                    break;
            }
        }
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
        AlertDialog.Builder ad = new AlertDialog.Builder(ChangePwd_Activity.this);
        ad.setTitle("離開");
        ad.setMessage("確定要離開此頁面嗎?");
        //退出按鈕
        ad.setPositiveButton("是", (dialog, i) -> {
            // TODO Auto-generated method stub
            ChangePwd_Activity.this.finish();//關閉activity
        });
        ad.setNegativeButton("否", (dialog, i) -> {
            //不退出不用執行任何操作
        });
        ad.show();//顯示對話框
    }

}