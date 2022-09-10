package com.example.layout1;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;


public class PersonalPage_Activity extends AppCompatActivity {

    private PersonalPage_Fragment personal_page_Fragment;
    private FragmentTransaction FT3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_page_activity);

        personal_page_Fragment = new PersonalPage_Fragment();
        FragmentManager FM3 = getSupportFragmentManager();
        FT3 = FM3.beginTransaction();
        FT3.add(R.id.PPAflayout, personal_page_Fragment);
        FT3.commit();

    }

}
