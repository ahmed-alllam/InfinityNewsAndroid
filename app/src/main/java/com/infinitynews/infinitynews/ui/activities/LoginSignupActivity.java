package com.infinitynews.infinitynews.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.infinitynews.infinitynews.R;
import com.infinitynews.infinitynews.callbacks.OnUserAuthRequestListener;
import com.infinitynews.infinitynews.ui.fragments.LoginFragment;
import com.infinitynews.infinitynews.ui.fragments.SignupFragment;

public class LoginSignupActivity extends BaseActivity implements OnUserAuthRequestListener {
    private SignupFragment signupFragment;
    private LoginFragment loginFragment;
    private Fragment currentFragment;
    private Button signupButton;
    private Button loginButton;
    private ProgressBar progressBar;
    private boolean isCalledFromMainActivity;
    private boolean isRequestPending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);

        isCalledFromMainActivity = getIntent().getBooleanExtra("fromMainActivity",
                false);

        signupButton = findViewById(R.id.signupButton);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);

        showSignupFragment();

        signupButton.setOnClickListener((v) -> showSignupFragment());

        loginButton.setOnClickListener((v) -> showLoginFragment());

        FloatingActionButton nextButton = findViewById(R.id.nextFab);
        nextButton.setOnClickListener(v -> {
            if (currentFragment == signupFragment)
                signupFragment.sendAuthRequest();
            else if (currentFragment == loginFragment)
                loginFragment.sendAuthRequest();
        });

        ExtendedFloatingActionButton skipButton = findViewById(R.id.skipFab);
        skipButton.setOnClickListener(v -> {
            if (!isRequestPending) {
                if (!isCalledFromMainActivity)
                    startCategoriesActivity();
                else
                    finish();
            }
        });
    }

    private void showSignupFragment() {
        boolean isNew = false;

        if (signupFragment == null) {
            isNew = true;
            signupFragment = new SignupFragment(this);
        }

        switchFragments(signupFragment, loginFragment, signupButton,
                loginButton, "signup", isNew);
    }

    private void showLoginFragment() {
        boolean isNew = false;

        if (loginFragment == null) {
            isNew = true;
            loginFragment = new LoginFragment(this);
        }

        switchFragments(loginFragment, signupFragment, loginButton,
                signupButton, "login", isNew);
    }

    private void switchFragments(Fragment fragmentToShow, Fragment fragmentToHide,
                                 View viewToshow, View viewTohide, String fragmentTag,
                                 boolean isNewFragment) {
        if (isRequestPending) {
            return;
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        viewToshow.setAlpha(1);
        viewTohide.setAlpha(0.5f);

        if (isNewFragment)
            ft.add(R.id.fragmentsView, fragmentToShow, fragmentTag);
        else
            ft.show(fragmentToShow);

        if (fragmentToHide != null)
            ft.hide(fragmentToHide);

        ft.commit();

        currentFragment = fragmentToShow;
    }

    @Override
    public void onRequestPending() {
        isRequestPending = true;
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestFinished() {
        isRequestPending = false;
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onRequestSuccessful() {
        startCategoriesActivity();
    }

    private void startCategoriesActivity() {
        Intent intent = new Intent(this, ChooseCategoriesActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}
