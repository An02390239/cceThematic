package com.example.layout1;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.messaging.FirebaseMessaging;

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

public class Login_Fragment extends Fragment {

    static String My_PostURL = "http://192.168.43.66/PHP_cceapp/call.php";
    static String Downlink = "http://192.168.43.66/PHP_cceapp/";
    static String splitword = "u,4t;4e04";
    private String LF_result, LF_account, LF_password, LF_ALL = "all",LF_postresult;
    private String[] LF_persondata_aftersplit;
    private int warning = 0;
    private boolean eye_show = true;
    private EditText LF_accountEdit, LF_passwordEdit;
    private CheckBox LF_check_pwd;
    private SharedPreferences get_pwd,get_ac,islogin,set_personname,set_personsex;
    private AlertDialog.Builder LF_dia;
    private Drawable eye_on,eye_off;
    private Button LF_login_btn;
    private TextView LF_forget_pwd,LF_register;
    private GlobalVariable LF_setusername,LF_setpassword;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view =  inflater.inflate(R.layout.login_fragment, container,  false );

        islogin = requireActivity().getSharedPreferences("islogin",Context.MODE_PRIVATE);
        set_personname = requireActivity().getSharedPreferences("Personname",Context.MODE_PRIVATE);

        LF_setusername = (GlobalVariable) requireActivity().getApplicationContext();
        LF_setpassword = (GlobalVariable) requireActivity().getApplicationContext();
        get_ac = requireActivity().getSharedPreferences("AC",Context.MODE_PRIVATE);
        get_pwd = requireActivity().getSharedPreferences("PWD", Context.MODE_PRIVATE);
        set_personsex = requireActivity().getSharedPreferences("SEX", Context.MODE_PRIVATE);
        LF_accountEdit = view.findViewById(R.id.LF_acct_edit);
        LF_passwordEdit = view.findViewById(R.id.LF_pwd_edit);
        LF_check_pwd = view.findViewById(R.id.LF_ch_pwd);
        LF_register = view.findViewById(R.id.LF_register_text);
        //eye圖片
        eye_on = ResourcesCompat.getDrawable(getResources(),R.drawable.eye_on,null);
        eye_on.setBounds(0,0,70,70);//設定大小
        eye_off = ResourcesCompat.getDrawable(getResources(),R.drawable.eye_off,null);
        eye_off.setBounds(0,0,70,70);//設定大小
        LF_passwordEdit.setCompoundDrawables(null,null,eye_off,null);//設定位於pwd_PW的何處
        //記住密碼
        LF_passwordEdit.setText(get_pwd.getString("pwd",""));
        LF_dia = new AlertDialog.Builder(requireActivity());
        LF_login_btn = view.findViewById(R.id.LF_login_button);
        LF_forget_pwd = view.findViewById(R.id.LF_forget_text);



        //跳出視窗通知
        LF_dia.setPositiveButton("確定", (dialog, which) -> {
            if(LF_result.equals("登入成功")){
                PersonalInformationRunnable p = new PersonalInformationRunnable();
                Thread GetPersonalInformation = new Thread(p);
                GetPersonalInformation.start();
                //判斷是否點選忘記密碼，有則記錄當前密碼，沒有則清除
                if (LF_check_pwd.isChecked()){
                    get_pwd.edit()
                            .putInt("rmb",1)
                            .putString("pwd", LF_passwordEdit.getText().toString())
                            .apply();
                }else{
                    get_pwd.edit()
                            .putInt("rmb",0)
                            .putString("pwd","")
                            .apply();
                }
                warning=0;//初始化
                LF_setusername.setUsername(LF_account);
                LF_setpassword.setPassword(LF_password);
                islogin.edit()
                        .putBoolean("check",true)
                        .apply();
                get_ac.edit()
                        .putString("ac",LF_account)
                        .apply();


                /*特定的主題，讓Firebase可以依據主題來選擇要傳送訊息的對象*/
                FirebaseMessaging.getInstance().subscribeToTopic(LF_ALL);

                //切換至下一個Activity(測試登入用)
                Intent intent = new Intent(requireActivity(),Article_First_Page_Activity.class);
                startActivity(intent);
                requireActivity().finish();
            }else if(warning==3){
                LF_dia.setTitle("警告");
                LF_dia.setMessage("錯誤三次");
                LF_dia.show();
                warning=0;
            }
        });
        LF_dia.setCancelable(false);





        OnClickListener onClickListener = new OnClickListener();
        //登入按鈕
        LF_login_btn.setOnClickListener(onClickListener);
        //移動至忘記密碼頁面
        LF_forget_pwd.setOnClickListener(onClickListener);
        //移動至註冊畫面
        LF_register.setOnClickListener(onClickListener);
        //設定EditDrawable的Click觸發事件
        OnTouchListener onTouchListener = new OnTouchListener();
        LF_passwordEdit.setOnTouchListener(onTouchListener);

        return view;
    }
    //Post Login
    private final Runnable LF_post = new Runnable() {
        @Override
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost myPost = new HttpPost(My_PostURL);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("username", LF_account));
            params.add(new BasicNameValuePair("password", LF_password));
            params.add(new BasicNameValuePair("function","login"));
            //將參數加入params這個List中等待post
            try {
                myPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                //帶上post參數且將參數設置為UTF-8的格式
                HttpResponse response = new DefaultHttpClient().execute(myPost);
                //執行
                LF_result = EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
                if (LF_result.contains("\n")){
                    String[] split = LF_result.split("\n");
                    LF_result = split[1];
                }
                //把結果(php的echo內容)取出轉換成String型態且為UTF-8的格式
                if(!(LF_result.equals("登入成功"))){
                    warning++;
                }
                //顯示取出結果
                LF_dia.setTitle("登入訊息");
                LF_dia.setMessage(LF_result);
                Looper.prepare();
                LF_dia.show();
                Looper.loop();
            }catch (IOException e){
                e.printStackTrace();// 如果出事，回傳錯誤訊息
            }finally {
                client.getConnectionManager().shutdown();
            }
        }
    };


    //藉由Post從資料庫拿出該使用者的個人資料
    private class PersonalInformationRunnable implements Runnable{
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost myPost = new HttpPost(My_PostURL);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("function","personalInformation"));
            params.add(new BasicNameValuePair("username",LF_account));
            //將參數加入params這個List中等待post
            try {
                myPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                //帶上post參數且將參數設置為UTF-8的格式
                HttpResponse response = new DefaultHttpClient().execute(myPost);
                //執行
                LF_postresult = EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
                //把結果(php的echo內容)取出轉換成String型態且為UTF-8的格式
                LF_persondata_aftersplit = LF_postresult.split("u,4t;4e04");
            }catch (IOException e){
                e.printStackTrace();// 如果出事，回傳錯誤訊息
            }finally {
                client.getConnectionManager().shutdown();
            }
            set_personname.edit()
                    .putString("personname",LF_persondata_aftersplit[3])
                    .apply();
            set_personsex.edit()
                    .putString("personsex",LF_persondata_aftersplit[4])
                    .apply();
            Log.e("LF_personsex",LF_persondata_aftersplit[4]+"");
        }// run()
    }

    private class OnClickListener implements View.OnClickListener{
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.LF_login_button:
                    LF_account = LF_accountEdit.getText().toString();
                    LF_password = LF_passwordEdit.getText().toString();
                    Thread thread = new Thread(LF_post);
                    thread.start(); // 開始執行
                    break;
                case R.id.LF_forget_text:
                    //宣告物件
                    ForgetPwd_Fragment FP = new ForgetPwd_Fragment();
                    //創造ForgetPwd_Fragment物件
                    FragmentManager forget_fm = requireActivity().getSupportFragmentManager();
                    FragmentTransaction forget_ft = forget_fm.beginTransaction();
                    //創造Fragment物件並取得其內容
                    forget_ft.replace(R.id.fLayout,FP);//將main中原本的畫面(空白)改成FP
                    forget_ft.addToBackStack(null);//加入堆疊，使他可以在下一個頁面按下返回鍵後回到這個頁面
                    forget_ft.commit();//類似執行的動作
                    //清空記住密碼文件中的資料使得pwd的EditText為空
                    get_pwd.edit()
                            .putInt("rmb",0)
                            .putString("pwd","")
                            .apply();
                    break;
                case R.id.LF_register_text:
                    //宣告物件
                    Register_Fragment RF = new Register_Fragment();
                    //創造Register_Fragment物件
                    FragmentManager register_fm = requireActivity().getSupportFragmentManager();
                    FragmentTransaction register_ft = register_fm.beginTransaction();
                    //創造Fragment物件並取得其內容
                    register_ft.replace(R.id.fLayout,RF);//將main中原本的畫面(空白)改成FP
                    register_ft.addToBackStack(null);//加入堆疊，使他可以在下一個頁面按下返回鍵後回到這個頁面
                    register_ft.commit();//類似執行的動作

                    //清空記住密碼文件中的資料使得pwd的EditText為空
                    get_pwd.edit()
                            .putInt("rmb",0)
                            .putString("pwd","")
                            .apply();
                    break;
            }
        }
    }
    //設定眼睛圖案點擊事件
    private class OnTouchListener implements View.OnTouchListener{

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final int DRAWABLE_RIGHT = 2;

            if(event.getAction() == MotionEvent.ACTION_UP) {
                //設定Drawable位置並判斷Click
                if(event.getRawX() >= (LF_passwordEdit.getRight() - LF_passwordEdit.getCompoundDrawables()
                        [DRAWABLE_RIGHT].getBounds().width())) {
                    if (eye_show){
                        LF_passwordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());//隱藏文字
                        LF_passwordEdit.setCompoundDrawables(null,null,eye_on,null);//設定位於pwd_PW的何處
                        eye_show = false;
                    }
                    else {
                        LF_passwordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());//顯示文字
                        LF_passwordEdit.setCompoundDrawables(null,null,eye_off,null);//設定位於pwd_PW的何處
                        eye_show = true;
                    }
                    return true;
                }
            }
            return false;
        }
    }
}