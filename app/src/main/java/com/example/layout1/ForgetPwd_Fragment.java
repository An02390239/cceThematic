package com.example.layout1;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

public class ForgetPwd_Fragment extends Fragment {
    private final String My_PostURL = Login_Fragment.My_PostURL;
    private String FF_email, FF_account, FF_result;
    private int FF_warning = 0;
    private AlertDialog.Builder FF_dialog;
    private EditText FF_email_edit, FF_account_edit;
    private Button FF_button;
    private GlobalVariable globalVariable;
    private Verification_Fragment verification_fragment;
    private FragmentTransaction FT1;
    private ProgressDialog FF_progress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view =  inflater.inflate(R.layout.forgetpwd_fragment, container,  false );



        verification_fragment = new Verification_Fragment();
        FragmentManager FM1 = requireActivity().getSupportFragmentManager();
        FT1 = FM1.beginTransaction();
        FF_email_edit = view.findViewById(R.id.for_Email);
        FF_email_edit.setSelection(FF_email_edit.getText().length());
        FF_account_edit = view.findViewById(R.id.for_acct);
        FF_account_edit.setSelection(FF_account_edit.getText().length());
        FF_button = view.findViewById(R.id.for_check);
        FF_dialog = new AlertDialog.Builder(requireActivity());
        globalVariable = (GlobalVariable) requireActivity().getApplicationContext();
        FF_progress = new ProgressDialog(requireActivity());



        FF_dialog.setPositiveButton("確定", (dialog, which) -> {
            if (FF_result.equals("寄送成功")){
                FF_warning = 0;
                FT1.replace(R.id.fLayout,verification_fragment);//將main中原本的畫面(空白)改成(加入)LF
                FT1.addToBackStack(null);//加入堆疊，使他可以在下一個頁面按下返回鍵後回到這個頁面
                FT1.commit();//類似執行的動作
            }else if (FF_warning == 3){
                FF_dialog.setTitle("警告");
                FF_dialog.setMessage("錯誤三次");
                FF_dialog.show();
                FF_warning =0;
            }
        });
        FF_dialog.setCancelable(false);
        OnClickListener onClickListener = new OnClickListener();
        FF_button.setOnClickListener(onClickListener);
        return view;
    }
    private final Runnable FP_post = new Runnable() {
        @Override
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost myPost = new HttpPost(My_PostURL);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("username", FF_account));
            params.add(new BasicNameValuePair("email", FF_email));
            params.add(new BasicNameValuePair("function","forgotpwd"));
            //將參數加入params這個List中等待post
            try {
                myPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                //帶上post參數且將參數設置為UTF-8的格式
                HttpResponse response = new DefaultHttpClient().execute(myPost);
                //執行
                FF_result = EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
                //把結果(php的echo內容)取出轉換成String型態且為UTF-8的格式
                if (FF_result.contains("\n")){
                    String[] split = FF_result.split("\n");
                    FF_result = split[1];
                }
                if(!(FF_result.equals("寄送成功"))) FF_warning++;
                FF_progress.dismiss();
                FF_dialog.setTitle("");
                FF_dialog.setMessage(FF_result);
                Looper.prepare();
                FF_dialog.show();
                Looper.loop();
                //顯示取出結果
            }catch (IOException e){
                e.printStackTrace();// 如果出事，回傳錯誤訊息
            }finally {
                client.getConnectionManager().shutdown();
            }

        }
    };

    private class OnClickListener implements View.OnClickListener{
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.for_check:
                    FF_account = FF_account_edit.getText().toString();
                    FF_email = FF_email_edit.getText().toString();
                    globalVariable.setAC(FF_account);
                    Thread thread = new Thread(FP_post);
                    thread.start();
                    FF_progress.setMessage("請稍等...");
                    FF_progress.setCancelable(false);
                    FF_progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    FF_progress.show();
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
        Intent RE = new Intent();
        RE.setClass(requireActivity(),Login_FragmentActivity.class);
        startActivity(RE);
        requireActivity().finish();
    }
}