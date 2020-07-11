package com.bitnews.bitnews.ui.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bitnews.bitnews.R;
import com.bitnews.bitnews.callbacks.OnUserAuthRequestListener;
import com.bitnews.bitnews.callbacks.UserAuthFragmentListener;
import com.bitnews.bitnews.data.models.AuthToken;
import com.bitnews.bitnews.data.network.APIResponse;
import com.bitnews.bitnews.ui.viewmodels.UserViewModel;

public class LoginFragment extends Fragment implements UserAuthFragmentListener {
    private UserViewModel userViewModel;
    private OnUserAuthRequestListener onUserAuthRequestListener;
    private TextView errorLabel;

    public LoginFragment(OnUserAuthRequestListener onUserAuthRequestListener) {
        this.onUserAuthRequestListener = onUserAuthRequestListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        errorLabel = view.findViewById(R.id.errorLabel);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getToken().observe(getViewLifecycleOwner(), (response -> {
            onUserAuthRequestListener.onRequestFinished();
            processResponse(response);
        }));
    }

    @Override
    public void sendAuthRequest() {
        String username = getUsername();
        String password = getPassword();

        if (isUsernameValid(username) && isPasswordValid(password)) {
            login(username, password);
        } else {
            errorLabel.setText(R.string.wrong_username_or_password);
        }
    }

    @Override
    public void setErrorMessage(int messageID) {
        errorLabel.setText(messageID);
    }

    private String getUsername() {
        TextView usernameTextView = getView().findViewById(R.id.usernameField);
        return usernameTextView.getText().toString();
    }

    private String getPassword() {
        TextView passwordTextView = getView().findViewById(R.id.passwordField);
        return passwordTextView.getText().toString();
    }

    private boolean isUsernameValid(String username) {
        String regexPattern = "^(?=.{8,36}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$";
        return username.matches(regexPattern);
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 8;
    }

    private void login(String username, String password) {
        onUserAuthRequestListener.onRequestPending();
        errorLabel.setText("");
        userViewModel.loginUser(getActivity().getApplicationContext(), username, password);
    }

    private void processResponse(APIResponse<AuthToken> response) {
        switch (response.getStatus()) {
            case SUCCESFUL:
                onUserAuthRequestListener.onRequestSuccessful();
                break;
            case BAD_REQUEST:
                errorLabel.setText(R.string.wrong_username_or_password);
                break;
            case NETWORK_FAILED:
                errorLabel.setText(R.string.network_error);
        }
    }
}
