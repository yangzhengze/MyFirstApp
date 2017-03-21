package com.songhaoxiang.myfirstapp;


import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;


public class SplashActivity extends Activity {


    private static final int START_ACTIVITY = 0X1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler.sendEmptyMessageDelayed(START_ACTIVITY, 3000);
        Intent intent = new Intent(this, PlayService.class);
        startService(intent);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_ACTIVITY:
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };

}



