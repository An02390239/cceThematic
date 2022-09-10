package com.example.layout1;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class ResetMail_Fragment extends Fragment {
    private Button RMF_reset_mail_button;
    private EditText RMF_reset_mail_edit;


    public ResetMail_Fragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.resetmail_fragment, container, false);
        RMF_reset_mail_button = view.findViewById(R.id.RMF_reset_mail_button);
        RMF_reset_mail_edit = view.findViewById(R.id.RMF_reset_mail_edit);



        OnClickListener onClickListener = new OnClickListener();
        RMF_reset_mail_button.setOnClickListener(onClickListener);
        return view;
    }

    private class OnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.RMF_reset_mail_button:
                    String newmail = RMF_reset_mail_edit.getText().toString();

                    break;
            }
        }
    }
}