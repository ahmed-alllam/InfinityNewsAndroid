package com.bitnews.bitnews.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bitnews.bitnews.R;
import com.bitnews.bitnews.ui.viewmodels.UserViewModel;

public class SplashActivity extends AppCompatActivity {
    private Class nextActivityClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.isUserAuthenticated(getApplicationContext()).observe(this, (result) -> {
            boolean isAuthenticated = result;
            if (isAuthenticated)
                nextActivityClass = MainActivity.class;
            else
                nextActivityClass = TutorialActivity.class;
        });

        new Handler().postDelayed(() -> {
            Intent mainIntent = new Intent(SplashActivity.this, getNextActivityClass());
            startActivity(mainIntent);
            finish();
        }, 1500);
    }

    private Class getNextActivityClass() {
        return nextActivityClass;
    }
}
