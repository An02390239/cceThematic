package com.example.layout1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

public class BillBoardContent_Activity extends AppCompatActivity {
    TextView BBCA_boardcontent, BBCA_boardtitle;
    String BBCA_title, BBCA_content, BBCA_url = "file:///android_asset";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.billboardcontent_activity);
        BBCA_boardtitle = findViewById(R.id.boardtitle_TV);
        BBCA_boardcontent = findViewById(R.id.boardcontent_TV);
        Intent intent = getIntent();
        BBCA_title = intent.getStringExtra("boardtitle");
        BBCA_content = intent.getStringExtra("boardcontent");

        Intent intent1 = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://c108110158.neocities.org/1234.pdf"));
        startActivity(intent1);


        BBCA_boardtitle.setText(BBCA_title);
        BBCA_boardcontent.setText(BBCA_content);
    }
}