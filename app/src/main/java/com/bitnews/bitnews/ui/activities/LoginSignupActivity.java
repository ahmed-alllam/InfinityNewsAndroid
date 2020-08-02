package com.bitnews.bitnews.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.bitnews.bitnews.R;
import com.bitnews.bitnews.callbacks.OnUserAuthRequestListener;
import com.bitnews.bitnews.callbacks.UserAuthFragmentListener;
import com.bitnews.bitnews.data.models.AuthToken;
import com.bitnews.bitnews.data.models.User;
import com.bitnews.bitnews.data.network.APIResponse;
import com.bitnews.bitnews.ui.fragments.LoginFragment;
import com.bitnews.bitnews.ui.fragments.SignupFragment;
import com.bitnews.bitnews.ui.viewmodels.UserViewModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class LoginSignupActivity extends AppCompatActivity implements OnUserAuthRequestListener {
    private SignupFragment signupFragment;
    private LoginFragment loginFragment;
    private UserViewModel userViewModel;
    private UserAuthFragmentListener currentAuthFragment;
    private Button signupButton;
    private Button loginButton;
    private ProgressBar progressBar;
    private boolean isCalledFromMainActivity;

    private String guestUsername = "";
    private boolean isRequestPending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getUser().observe(this, (this::processGuestSignupResponse));
        userViewModel.getToken().observe(this, (response -> {
            onRequestFinished();
            processGuestLoginResponse(response);
        }));

        isCalledFromMainActivity = getIntent().getBooleanExtra("fromMainActivity",
                false);

        signupButton = findViewById(R.id.signupButton);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);

        showSignupFragment();

        signupButton.setOnClickListener((v) -> showSignupFragment());

        loginButton.setOnClickListener((v) -> showLoginFragment());

        FloatingActionButton nextButton = findViewById(R.id.nextFab);
        nextButton.setOnClickListener((v) -> {
            if (!isRequestPending) {
                if (currentAuthFragment != null)
                    currentAuthFragment.sendAuthRequest();
            }
        });

        ExtendedFloatingActionButton skipButton = findViewById(R.id.skipFab);
        skipButton.setOnClickListener((v -> {
            if (!isRequestPending) {
                if (isCalledFromMainActivity) {
                    finish();
                } else {
                    if (guestUsername.isEmpty())
                        signupAsGuest();
                    else
                        loginAsGuest();
                    onRequestPending();
                }
            }
        }));
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

        currentAuthFragment = (UserAuthFragmentListener) fragmentToShow;
    }

    @Override
    public void onRequestPending() {
        isRequestPending = true;
        progressBar.setVisibility(View.VISIBLE);
        currentAuthFragment.setErrorMessageInvisible();
    }

    @Override
    public void onRequestFinished() {
        isRequestPending = false;
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onRequestSuccessful() {
        Intent intent = new Intent(this, ChooseCategoriesActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    private void signupAsGuest() {
        userViewModel.signupAsGuest(getApplicationContext());
    }

    private void loginAsGuest() {
        userViewModel.loginUser(getApplicationContext(), guestUsername, "");
    }

    private void processGuestSignupResponse(APIResponse<User> response) {
        switch (response.getStatus()) {
            case SUCCESFUL:
                guestUsername = response.getitem().getUsername();
                loginAsGuest();
                break;
            case NETWORK_FAILED:
                guestUsername = "";
                onRequestFinished();
                currentAuthFragment.setErrorMessage(R.string.network_error);
        }
    }

    private void processGuestLoginResponse(APIResponse<AuthToken> response) {
        switch (response.getStatus()) {
            case SUCCESFUL:
                onRequestSuccessful();
                break;
            case NETWORK_FAILED:
                currentAuthFragment.setErrorMessage(R.string.network_error);
        }
    }
}
