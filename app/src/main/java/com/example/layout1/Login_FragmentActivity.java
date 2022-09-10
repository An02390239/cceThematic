package com.example.layout1;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class Login_FragmentActivity extends FragmentActivity {
    public static final String LFA_TAG = FCMService.TAG;

    private Login_Fragment login_fragment;
    private FragmentTransaction FT1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_fragmentactivity);



        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()){
                    return;
                }
                String token = task.getResult();
                Log.e(LFA_TAG, "onComplete: "+token);
                Log.d(LFA_TAG, "onComplete: "+token);
            }
        });

        init();
        FT1.add(R.id.fLayout, login_fragment);//將main中原本的畫面(空白)改成(加入)LF
        FT1.commit();//類似執行的動作
    }

    private void init(){
        login_fragment = new Login_Fragment();
        FragmentManager FM1 = getSupportFragmentManager();
        FT1 = FM1.beginTransaction();
    }

}