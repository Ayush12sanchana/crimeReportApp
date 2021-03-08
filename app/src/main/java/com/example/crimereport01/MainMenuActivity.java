package com.example.crimereport01;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class MainMenuActivity extends AppCompatActivity {

    public String UID;
    public ImageView crimeReportBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        crimeReportBtn = findViewById(R.id.crime_report_btn);

        Intent intent = getIntent();
        UID = intent.getSerializableExtra("uid").toString();
        Log.d("TAG", "||||||||||||||||------------->>>>>>>>>>>>>>>>>>>>>>" + UID);


        crimeReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("uid",UID);

                Intent intent = new Intent(MainMenuActivity.this, MakeEnqueryActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
