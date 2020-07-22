package com.betalabs.factcheck;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class login extends AppCompatActivity {

    Button j_login;
    Button r_login;


    @Override
    public void onBackPressed(){
        Intent login = new Intent(this, login.class);
        startActivity(login);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        j_login = findViewById(R.id.j_login);
        r_login = findViewById(R.id.r_login);



        //move to invite page
        j_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, invite.class);
                startActivity(intent);
            }
        });

        //move to readers login
        r_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, rea_login.class);
                startActivity(intent);
            }
        });

    }

}
