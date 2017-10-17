package com.example.anirban.tripplanner.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.anirban.tripplanner.R;
import com.example.anirban.tripplanner.helper.PreferenceHelper;
import com.example.anirban.tripplanner.home.HomeMapActivity;
import com.example.anirban.tripplanner.prelogin.LogInActivity;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceHelper.setInstance(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(SPLASH_TIME_OUT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (!PreferenceHelper.getInstance().isUserPresent()) {
                        Intent splashIntentLogIn = new Intent(SplashActivity.this, LogInActivity.class);
                        splashIntentLogIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(splashIntentLogIn);
                    } else {
                        Intent splashIntentHome = new Intent(SplashActivity.this, HomeMapActivity.class);
                        splashIntentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(splashIntentHome);

                    }
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
