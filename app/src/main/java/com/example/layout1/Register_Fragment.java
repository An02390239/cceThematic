package com.example.layout1;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Register_Fragment extends Fragment {
    private String My_PostURL = Login_Fragment.My_PostURL;
    private String RF_name, RF_account, RF_password, RF_checkpassword, RF_sex, RF_position,RF_email;
    private String RF_postresult;
    private EditText RF_name_edit, RF_account_edit,RF_password_edit,RF_checkpassword_edit,
            RF_email_edit;
    private RadioButton RF_sex_man,RF_sex_woman;
    private RadioButton RF_position_student,RF_position_teacher,RF_position_staff;
    private Button RF_press_button,RF_checkaccount_button;
    private AlertDialog.Builder RF_postdia;
    private ProgressDialog RF_postloding;
    private boolean RF_sex_selected = false,RF_position_selected = false,RF_registercheck = false;



    public Register_Fragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_fragment, container, false);
        //???????????????
        RF_name_edit = view.findViewById(R.id.RF_register_name);
        RF_account_edit = view.findViewById(R.id.RF_register_account);
        RF_checkaccount_button = view.findViewById(R.id.RF_register_checkaccount_button);
        RF_password_edit = view.findViewById(R.id.RF_register_password);
        RF_checkpassword_edit = view.findViewById(R.id.RF_register_checkpassword);
        RF_email_edit = view.findViewById(R.id.RF_register_Email);
        RF_sex_man = view.findViewById(R.id.RF_register_sex_man);
        RF_sex_woman = view.findViewById(R.id.RF_register_sex_woman);
        RF_position_student = view.findViewById(R.id.RF_register_position_student);
        RF_position_teacher = view.findViewById(R.id.RF_register_position_teacher);
        RF_position_staff = view.findViewById(R.id.RF_register_position_staff);
        RF_press_button = view.findViewById(R.id.RF_register_press);
        RF_postdia = new AlertDialog.Builder(requireActivity());
        RF_postloding = new ProgressDialog(requireActivity());


        setEnabledfalse();

        RF_postdia.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //????????????
                if (RF_registercheck){
                    //??????????????????????????????
                }
            }
        });
        RF_postdia.setCancelable(false);






        OnClickListener onClickListener = new OnClickListener();
        RF_press_button.setOnClickListener(onClickListener);
        RF_checkaccount_button.setOnClickListener(onClickListener);
        RF_sex_man.setOnClickListener(onClickListener);
        RF_sex_woman.setOnClickListener(onClickListener);
        RF_position_student.setOnClickListener(onClickListener);
        RF_position_teacher.setOnClickListener(onClickListener);
        RF_position_staff.setOnClickListener(onClickListener);
        return view;
    }
    private void setEnabledfalse(){
        RF_password_edit.setEnabled(false);
        RF_checkpassword_edit.setEnabled(false);
        RF_email_edit.setEnabled(false);
        RF_sex_man.setEnabled(false);
        RF_sex_woman.setEnabled(false);
        RF_position_student.setEnabled(false);
        RF_position_teacher.setEnabled(false);
        RF_position_staff.setEnabled(false);
        RF_press_button.setEnabled(false);
    }
    private void setEnabledtrue(){
        RF_password_edit.setEnabled(true);
        RF_checkpassword_edit.setEnabled(true);
        RF_email_edit.setEnabled(true);
        RF_sex_man.setEnabled(true);
        RF_sex_woman.setEnabled(true);
        RF_position_student.setEnabled(true);
        RF_position_teacher.setEnabled(true);
        RF_position_staff.setEnabled(true);
        RF_press_button.setEnabled(true);
    }

    //??????????????????????????????????????????????????????????????????
    public static boolean isEmail(String eamil){
        String strPattern ="^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9]" +
                "[\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(eamil);

        return m.matches();
    }


    //????????????????????????
    private class OnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                //??????
                case R.id.RF_register_press:
                    RF_name = RF_name_edit.getText().toString();
                    RF_account = RF_account_edit.getText().toString();
                    RF_password = RF_password_edit.getText().toString();
                    RF_checkpassword = RF_checkpassword_edit.getText().toString();
                    RF_email = RF_email_edit.getText().toString();
                    if (isEmail(RF_email)){
                        if (RF_sex_selected){
                            if (RF_position_selected){
                                RegisterRunnable registerRunnable = new RegisterRunnable();
                                Thread register = new Thread(registerRunnable);
                                register.start();
                                RF_postloding.setMessage("Loading...");
                                RF_postloding.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                RF_postloding.setCancelable(false);
                                RF_postloding.show();
                            }else {
                                Toast.makeText(requireActivity(), "???????????????", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(requireActivity(), "???????????????", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(requireActivity(), "????????????????????????!!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                    //????????????
                case R.id.RF_register_checkaccount_button:
                    RF_account = RF_account_edit.getText().toString();
                    CheckAccountRunnable checkAccountRunnable = new CheckAccountRunnable();
                    Thread checkaccount = new Thread(checkAccountRunnable);
                    checkaccount.start();
                    RF_postloding.setMessage("Loading...");
                    RF_postloding.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    RF_postloding.setCancelable(false);
                    RF_postloding.show();
                    break;
                    //RadioButton???
                case R.id.RF_register_sex_man:
                    //Toast.makeText(requireActivity(), "man", Toast.LENGTH_SHORT).show();
                    RF_sex_selected = true;
                    RF_sex = "???";
                    break;
                //RadioButton???
                case R.id.RF_register_sex_woman:
                    //Toast.makeText(requireActivity(), "woman", Toast.LENGTH_SHORT).show();
                    RF_sex_selected = true;
                    RF_sex = "???";
                    break;
                //RadioButton??????
                case R.id.RF_register_position_student:
                    //Toast.makeText(requireActivity(), "woman", Toast.LENGTH_SHORT).show();
                    RF_position_selected = true;
                    RF_position = "1";
                    break;
                //RadioButton??????
                case R.id.RF_register_position_teacher:
                    //Toast.makeText(requireActivity(), "woman", Toast.LENGTH_SHORT).show();
                    RF_position_selected = true;
                    RF_position = "2";
                    break;
                //RadioButton??????
                case R.id.RF_register_position_staff:
                    //Toast.makeText(requireActivity(), "woman", Toast.LENGTH_SHORT).show();
                    RF_position_selected = true;
                    RF_position = "3";
                    break;
            }
        }
    }

    private class CheckAccountRunnable implements Runnable{

        @Override
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost myPost = new HttpPost(My_PostURL);
            List<NameValuePair> params = new ArrayList<>();
            //???????????????params??????List?????????post
            params.add(new BasicNameValuePair("function","checkuser"));
            params.add(new BasicNameValuePair("username", RF_account));
            try {
                //??????post???????????????????????????UTF-8?????????
                myPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                //??????
                HttpResponse response = new DefaultHttpClient().execute(myPost);
                //?????????(php???echo??????)???????????????String????????????UTF-8?????????
                RF_postresult = EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
                if (RF_postresult.contains("\n")){
                    String[] split = RF_postresult.split("\n");
                    RF_postresult = split[1];
                }
                if (RF_postresult.equals("?????????????????????")){
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setEnabledtrue();
                            RF_account_edit.setEnabled(false);
                            Log.e("RF_account",RF_account_edit.getText().toString()+"");
                        }
                    });
                }
                Looper.prepare();
                RF_postloding.dismiss();
                //??????????????????
                RF_postdia.setTitle("");
                RF_postdia.setMessage(RF_postresult);
                RF_postdia.show();
                Looper.loop();
            }catch (IOException e){
                e.printStackTrace();// ?????????????????????????????????
            }finally {
                client.getConnectionManager().shutdown();
            }
        }
    }

    //?????????post
    private class RegisterRunnable implements Runnable{

        @Override
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost myPost = new HttpPost(My_PostURL);
            List<NameValuePair> params = new ArrayList<>();
            //???????????????params??????List?????????post
            params.add(new BasicNameValuePair("function","register"));
            params.add(new BasicNameValuePair("name", RF_name));
            params.add(new BasicNameValuePair("username", RF_account));
            params.add(new BasicNameValuePair("password", RF_password));
            params.add(new BasicNameValuePair("checkpwd", RF_checkpassword));
            params.add(new BasicNameValuePair("sex", RF_sex));
            params.add(new BasicNameValuePair("position", RF_position));
            params.add(new BasicNameValuePair("email", RF_email));
            try {
                //??????post???????????????????????????UTF-8?????????
                myPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                //??????
                HttpResponse response = new DefaultHttpClient().execute(myPost);
                //?????????(php???echo??????)???????????????String????????????UTF-8?????????
                RF_postresult = EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
                if (RF_postresult.contains("\n")){
                    String[] split = RF_postresult.split("\n");
                    RF_postresult = split[1];
                }
                if (RF_postresult.equals("????????????"))RF_registercheck = true;
                Looper.prepare();
                RF_postloding.dismiss();
                //??????????????????
                RF_postdia.setTitle("????????????");
                RF_postdia.setMessage(RF_postresult);
                RF_postdia.show();
                Looper.loop();
            }catch (IOException e){
                e.printStackTrace();// ?????????????????????????????????
            }finally {
                client.getConnectionManager().shutdown();
            }
        }
    }
}