package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;



public class LostFoundmainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_foundmain);

        Button buttonforchangepassword = findViewById(R.id.updatepass);
        buttonforchangepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LostFoundmainActivity.this,changepasswordactivity.class);
                startActivity(intent);
                finish();
            }
        });







        Button checkforlost = findViewById(R.id.lostfeed);

        checkforlost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LostFoundmainActivity.this,dataforlost.class);
                startActivity(intent);
            }
        });

        Button checkforfound = findViewById(R.id.foundfeed);

        checkforfound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( LostFoundmainActivity.this,dataforfound.class);
                startActivity(intent);
            }
        });
        Button mypostsbutton = findViewById(R.id.userposts);

        mypostsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( LostFoundmainActivity.this,myposts.class);
                startActivity(intent);
            }
        });



    }


}