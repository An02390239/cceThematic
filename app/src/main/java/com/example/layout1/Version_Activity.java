package com.example.layout1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Version_Activity extends AppCompatActivity {
    private AlertDialog.Builder VA_chooseversion;
    private Button VA_societyButton, VA_schoolButton,VA_enterpriseButton;
    private SharedPreferences islogin,VA_getaccount;
    private GlobalVariable  VA_setusername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.version_activity);
        islogin = Version_Activity.this.getSharedPreferences("islogin", Context.MODE_PRIVATE);


        VA_setusername = (GlobalVariable) Version_Activity.this.getApplicationContext();
        VA_getaccount = Version_Activity.this.getSharedPreferences("AC",Context.MODE_PRIVATE);

        if (islogin.getBoolean("check",false)){
            VA_setusername.setUsername(VA_getaccount.getString("ac",""));
            Intent intent = new Intent(Version_Activity.this,Article_First_Page_Activity.class);
            startActivity(intent);
            Version_Activity.this.finish();
        }


        init();


        VA_chooseversion.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        VA_chooseversion.setCancelable(false);


        OnClickListener onClickListener = new OnClickListener();
        VA_societyButton.setOnClickListener(onClickListener);
        VA_schoolButton.setOnClickListener(onClickListener);
        VA_enterpriseButton.setOnClickListener(onClickListener);


    }

    private void init(){
        VA_chooseversion = new AlertDialog.Builder(Version_Activity.this);
        VA_societyButton = findViewById(R.id.societyButton);
        VA_schoolButton = findViewById(R.id.schoolButton);
        VA_enterpriseButton = findViewById(R.id.enterpriseButton);
    }

    private class OnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.societyButton:
                case R.id.enterpriseButton:
                    VA_chooseversion.setMessage("正在開發中\n敬請期待!!!");
                    VA_chooseversion.show();
                    break;
                case R.id.schoolButton:
                    Intent toLFA = new Intent(Version_Activity.this,Login_FragmentActivity.class);
                    startActivity(toLFA);
                    Version_Activity.this.finish();
                    break;
            }
        }
    }
}