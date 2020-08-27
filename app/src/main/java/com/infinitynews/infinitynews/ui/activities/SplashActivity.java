package com.infinitynews.infinitynews.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.infinitynews.infinitynews.R;
import com.infinitynews.infinitynews.ui.viewmodels.CategoryViewModel;
import com.infinitynews.infinitynews.ui.viewmodels.UserViewModel;

public class SplashActivity extends AppCompatActivity {
    public static final int TIME_TO_WAIT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        CategoryViewModel categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        categoryViewModel.hasFavouriteCategories(getApplicationContext()).observe(this, hasFavourites -> {
            if (!hasFavourites)
                checkIfAuthenticated();
            else
                addStartActivityDelayToHandler(MainActivity.class);
        });
    }

    private void checkIfAuthenticated() {
        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.isUserAuthenticated(getApplicationContext()).observe(this, (isAuthenticated) -> {
            if (isAuthenticated) {
                addStartActivityDelayToHandler(ChooseCategoriesActivity.class);
            } else
                addStartActivityDelayToHandler(TutorialActivity.class);
        });
    }

    private void addStartActivityDelayToHandler(Class<? extends Activity> nextActivityClass) {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, nextActivityClass);
            startActivity(intent);
            finish();
        }, TIME_TO_WAIT);
    }
}
