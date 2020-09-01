package com.infinitynews.infinitynews.ui.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.infinitynews.infinitynews.R;
import com.infinitynews.infinitynews.callbacks.OnUserAuthRequestListener;
import com.infinitynews.infinitynews.data.models.AuthToken;
import com.infinitynews.infinitynews.data.network.APIResponse;
import com.infinitynews.infinitynews.ui.viewmodels.UserViewModel;

public class LoginFragment extends Fragment {
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

    public void sendAuthRequest() {
        errorLabel.setVisibility(View.INVISIBLE);

        String username = getUsername();
        String password = getPassword();

        if (isUsernameValid(username) && isPasswordValid(password)) {
            login(username, password);
        } else {
            setErrorMessage(R.string.wrong_username_or_password);
        }
    }

    public void setErrorMessage(int messageID) {
        errorLabel.setVisibility(View.VISIBLE);
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
        String regexPattern = "[\\w.@+-]{8,}\\Z";
        return username.matches(regexPattern);
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 8;
    }

    private void login(String username, String password) {
        onUserAuthRequestListener.onRequestPending();
        userViewModel.loginUser(getActivity().getApplicationContext(), username, password);
    }

    private void processResponse(APIResponse<AuthToken> response) {
        switch (response.getStatus()) {
            case SUCCESFUL:
                onUserAuthRequestListener.onRequestSuccessful();
                break;
            case BAD_REQUEST:
                setErrorMessage(R.string.wrong_username_or_password);
                break;
            case NETWORK_FAILED:
                setErrorMessage(R.string.network_error);
        }
    }
}
