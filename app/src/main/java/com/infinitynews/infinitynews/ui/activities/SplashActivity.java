package com.infinitynews.infinitynews.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.infinitynews.infinitynews.R;
import com.infinitynews.infinitynews.ui.viewmodels.CategoryViewModel;
import com.infinitynews.infinitynews.ui.viewmodels.UserViewModel;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class SplashActivity extends AppCompatActivity {
    private Class<? extends Activity> nextActivityClass;
    private boolean isAnimationFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        GifImageView gifImageView = findViewById(R.id.splashScreen);
        GifDrawable gifDrawable = (GifDrawable) gifImageView.getDrawable();
        gifDrawable.addAnimationListener(n -> {
            isAnimationFinished = true;

            if (nextActivityClass != null)
                startActivity(nextActivityClass);
        });

        CategoryViewModel categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        categoryViewModel.hasFavouriteCategories(getApplicationContext()).observe(this, this::checkIfAuthenticated);
    }

    private void checkIfAuthenticated(boolean hasFavouriteCategories) {
        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.isUserAuthenticated(getApplicationContext()).observe(this, (isAuthenticated) -> {
            if (hasFavouriteCategories)
                setNextActivity(MainActivity.class);
            else {
                if (isAuthenticated) {
                    setNextActivity(ChooseCategoriesActivity.class);
                } else
                    setNextActivity(TutorialActivity.class);
            }
        });
    }

    private void setNextActivity(Class<? extends Activity> nextActivityClass) {
        if (isAnimationFinished)
            startActivity(nextActivityClass);
        else
            this.nextActivityClass = nextActivityClass;
    }

    private void startActivity(Class<? extends Activity> nextActivityClass) {
        Intent intent = new Intent(this, nextActivityClass);
        startActivity(intent);
        finish();
    }
}
