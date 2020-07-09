package com.bitnews.bitnews.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bitnews.bitnews.R;
import com.bitnews.bitnews.callbacks.OnUserAuthRequestListener;
import com.bitnews.bitnews.callbacks.UserAuthFragmentListener;
import com.bitnews.bitnews.ui.fragments.LoginFragment;
import com.bitnews.bitnews.ui.fragments.SignupFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class LoginSignupActivity extends AppCompatActivity implements OnUserAuthRequestListener {
    private SignupFragment signupFragment;
    private LoginFragment loginFragment;
    private UserAuthFragmentListener currentAuthFragment;
    private Button signupButton;
    private Button loginButton;
    private ProgressBar progressBar;

    private boolean isRequestPending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);

        signupButton = findViewById(R.id.signupButton);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);

        showSignupFragment();

        signupButton.setOnClickListener((v) -> showSignupFragment());

        loginButton.setOnClickListener((v) -> showLoginFragment());

        FloatingActionButton nextButton = findViewById(R.id.nextFab);
        nextButton.setOnClickListener((v) -> {
            if (!isRequestPending)
                currentAuthFragment.sendAuthRequest();
        });
    }

    private void showSignupFragment() {
        boolean isNew = false;

        if (signupFragment == null) {
            isNew = true;
            signupFragment = new SignupFragment();
        }

        switchFragments(signupFragment, loginFragment, signupButton,
                loginButton, "signup", isNew);
    }

    private void showLoginFragment() {
        boolean isNew = false;

        if (loginFragment == null) {
            isNew = true;
            loginFragment = new LoginFragment();
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

        currentAuthFragment = (UserAuthFragmentListener) fragmentToShow;
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
}
