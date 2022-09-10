package com.example.layout1;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Looper;
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

public class Verification_Fragment extends Fragment {
    private final String My_PostURL = Login_Fragment.My_PostURL;
    private String VF_code,VF_result, VF_account;
    private int VF_warning = 0;
    private AlertDialog.Builder VF_dialog;
    private Button VF_press;
    private EditText VF_edittext;
    private ResetPwd_Fragment resetpassword_fragment;
    private FragmentTransaction FT1;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.verification_fragment, container,  false );


        resetpassword_fragment = new ResetPwd_Fragment();
        FragmentManager FM1 = requireActivity().getSupportFragmentManager();
        FT1 = FM1.beginTransaction();
        VF_press = view.findViewById(R.id.vf_send);
        VF_edittext = view.findViewById(R.id.vf_edittext);
        VF_dialog = new AlertDialog.Builder(requireActivity());
        GlobalVariable globalVariable = (GlobalVariable) requireActivity().getApplicationContext();
        VF_account = globalVariable.getAC();//取得全域變數


        VF_dialog.setPositiveButton("確認", (dialog, which) -> {
            if (VF_result.equals("驗證碼正確")){
                VF_warning = 0;
                FT1.replace(R.id.fLayout, resetpassword_fragment);//將main中原本的畫面(空白)改成(加入)LF
                FT1.addToBackStack(null);//加入堆疊，使他可以在下一個頁面按下返回鍵後回到這個頁面
                FT1.commit();//類似執行的動作
            }else if (VF_warning == 3){
                VF_dialog.setTitle("警告");
                VF_dialog.setMessage("錯誤三次");
                VF_dialog.show();
                VF_warning=0;
            }
        });
        OnClickListener onClickListener = new OnClickListener();
        VF_press.setOnClickListener(onClickListener);

        return view;
    }
    private final Runnable VF_post = new Runnable() {
        @Override
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost myPost = new HttpPost(My_PostURL);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("username", VF_account));
            params.add(new BasicNameValuePair("captcha",VF_code));
            params.add(new BasicNameValuePair("function","checkcode"));
            //將參數加入params這個List中等待post
            try {
                myPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                //帶上post參數且將參數設置為UTF-8的格式
                HttpResponse response = new DefaultHttpClient().execute(myPost);
                //執行
                VF_result = EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
                //把結果(php的echo內容)取出轉換成String型態且為UTF-8的格式
                if (VF_result.contains("\n")){
                    String[] split = VF_result.split("\n");
                    VF_result = split[1];
                }
                if(!(VF_result.equals("驗證碼正確")))VF_warning++;
                VF_dialog.setTitle("");
                VF_dialog.setMessage(VF_result);
                Looper.prepare();
                VF_dialog.show();
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
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.vf_send:
                    VF_code = VF_edittext.getText().toString();
                    Thread thread = new Thread(VF_post);
                    thread.start();
                    break;
            }
        }
    }
}