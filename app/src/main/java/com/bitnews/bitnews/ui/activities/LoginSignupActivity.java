package com.bitnews.bitnews.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bitnews.bitnews.R;
import com.bitnews.bitnews.ui.fragments.LoginFragment;
import com.bitnews.bitnews.ui.fragments.SignupFragment;

public class LoginSignupActivity extends AppCompatActivity {
    private SignupFragment signupFragment;
    private LoginFragment loginFragment;
    private Button signupButton;
    private Button loginButton;

    private boolean isRequestPending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);

        signupButton = findViewById(R.id.signupButton);
        loginButton = findViewById(R.id.loginButton);

        showSignupFragment();

        signupButton.setOnClickListener((v) -> showSignupFragment());

        loginButton.setOnClickListener((v) -> showLoginFragment());
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
    }
}
