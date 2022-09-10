package com.example.layout1;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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
import java.util.List;

public class ResetPwd_Fragment extends Fragment {

    private final String My_PostURL = Login_Fragment.My_PostURL;
    private String RPF_username, RPF_newpwd, RPF_checknewpwd, RPF_result;
    private AlertDialog.Builder RPF_dialog;
    private EditText newpwd, checknewpwd;
    private Button RPF_Button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.reset_pwd_fragment, container, false);

        GlobalVariable globalVariable = (GlobalVariable) requireActivity().getApplicationContext();
        RPF_username = globalVariable.getAC();
        newpwd = view.findViewById(R.id.newpwd);
        checknewpwd = view.findViewById(R.id.checknewpwd);
        RPF_Button = view.findViewById(R.id.rpf_send);
        RPF_dialog = new AlertDialog.Builder(requireActivity());


        RPF_dialog.setPositiveButton("確定", (dialog, which) -> {
            if (RPF_result.equals("重設密碼成功")){
                Intent re_to_B = new Intent(requireActivity(),Login_FragmentActivity.class);
                startActivity(re_to_B);
            }
        });
        OnClickListener onClickListener = new OnClickListener();
        RPF_Button.setOnClickListener(onClickListener);

        return view;

    }
    private final Runnable RP_post = new Runnable() {
        @Override
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost myPost = new HttpPost(My_PostURL);

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("username", RPF_username));
            params.add(new BasicNameValuePair("newpwd", RPF_newpwd));
            params.add(new BasicNameValuePair("checknewpwd", RPF_checknewpwd));
            params.add(new BasicNameValuePair("function","resetpwd"));
            try {
                myPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                //帶上post參數且將參數設置為UTF-8的格式
                HttpResponse response = new DefaultHttpClient().execute(myPost);
                //執行
                RPF_result = EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
                //把結果(php的echo內容)取出轉換成String型態且為UTF-8的格式
                if (RPF_result.contains("\n")){
                    String[] split = RPF_result.split("\n");
                    RPF_result = split[1];
                }
                RPF_dialog.setTitle("");
                RPF_dialog.setMessage(RPF_result);
                Looper.prepare();
                RPF_dialog.show();
                Looper.loop();
            }catch (IOException e){
                e.printStackTrace();// 如果出事，回傳錯誤訊息
            }finally {
                client.getConnectionManager().shutdown();
            }
        }
    };

    private class OnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.rpf_send:
                    RPF_newpwd = newpwd.getText().toString();
                    RPF_checknewpwd = checknewpwd.getText().toString();
                    Thread thread = new Thread(RP_post);
                    thread.start();
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                ConfirmExit();//按返回鍵，則執行退出確認
                return  true;
            }
            return super.requireActivity().onKeyDown(keyCode, event);
        });
    }
    public void ConfirmExit(){//退出確認
        AlertDialog.Builder ad = new AlertDialog.Builder(requireActivity());
        ad.setTitle("離開");
        ad.setMessage("確定要離開此頁面嗎?");
        //退出按鈕
        ad.setPositiveButton("是", (dialog, i) -> {
            // TODO Auto-generated method stub
            Intent RE = new Intent();
            RE.setClass(requireActivity(),Login_FragmentActivity.class);
            startActivity(RE);
            requireActivity().finish();
        });
        ad.setNegativeButton("否", (dialog, i) -> {
            //不退出不用執行任何操作
        });
        ad.show();//顯示對話框
    }
}