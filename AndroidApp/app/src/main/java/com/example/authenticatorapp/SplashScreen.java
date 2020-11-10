package com.example.authenticatorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


TimerTask task = new TimerTask() {

            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, Login.class);
                startActivity(intent);
                finishScreen();
            }
        };
        Timer t = new Timer();
        t.schedule(task,3000);



    }
    private void finishScreen() {
        this.finish();
    }

}
