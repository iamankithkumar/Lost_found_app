package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class myposts extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myposts);


        Button myfoundposts = findViewById(R.id.userfoundposts);
        myfoundposts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(myposts.this , myfoundposts.class);
                startActivity(intent);
            }
        });

        Button mylostposts = findViewById(R.id.userlostposts);
        mylostposts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(myposts.this , mylostposts.class);
                startActivity(intent);
            }
        });
    }
}