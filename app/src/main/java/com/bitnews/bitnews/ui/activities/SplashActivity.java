package com.bitnews.bitnews.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.bitnews.bitnews.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            Intent mainIntent = new Intent(SplashActivity.this, getNextActivityClass());
            startActivity(mainIntent);
            finish();
        }, 1500);
    }

    private Class getNextActivityClass() {
        // if ()
        // return MainActivity.class;
        // else
        //    return LoginActivity.class;
        return null;
    }
}
