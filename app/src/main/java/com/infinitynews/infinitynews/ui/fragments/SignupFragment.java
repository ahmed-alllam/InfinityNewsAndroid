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
import com.infinitynews.infinitynews.data.models.User;
import com.infinitynews.infinitynews.data.network.APIResponse;
import com.infinitynews.infinitynews.ui.viewmodels.UserViewModel;


public class SignupFragment extends Fragment {
    private UserViewModel userViewModel;
    private OnUserAuthRequestListener onUserAuthRequestListener;
    private TextView errorLabel;

    private String username = "";
    private String password = "";

    public SignupFragment(OnUserAuthRequestListener onUserAuthRequestListener) {
        this.onUserAuthRequestListener = onUserAuthRequestListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        errorLabel = view.findViewById(R.id.errorLabel);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getUser().observe(getViewLifecycleOwner(), (this::processSignupResponse));
        userViewModel.getToken().observe(getViewLifecycleOwner(), (response -> {
            onUserAuthRequestListener.onRequestFinished();
            processLoginResponse(response);
        }));
    }

    public void sendAuthRequest() {
        errorLabel.setVisibility(View.INVISIBLE);

        if (username.isEmpty() && password.isEmpty()) {
            String firstName = getFirstName();
            String lastName = getLastName();
            String username = getUsername();
            String password = getPassword();
            String confirmedPassword = getConfirmedPassword();

            boolean nameValid = isNameValid(firstName, lastName);
            boolean usernameValid = isUsernameValid(username);
            boolean passwordValid = isPasswordValid(password, confirmedPassword);

            if (nameValid && usernameValid && passwordValid) {
                this.username = username;
                this.password = password;
                signUp(firstName, lastName, username, password);
            } else {
                if (!nameValid)
                    setErrorMessage(R.string.invalid_name);
                else if (!usernameValid)
                    setErrorMessage(R.string.invalid_username);
                else
                    setErrorMessage(R.string.invalid_password);
            }
        } else {
            login();
        }
    }

    public void setErrorMessage(int messageID) {
        errorLabel.setVisibility(View.VISIBLE);
        errorLabel.setText(messageID);
    }

    private String getFirstName() {
        TextView firstNameTextView = getView().findViewById(R.id.firstNameField);
        return firstNameTextView.getText().toString();
    }

    private String getLastName() {
        TextView lastNameTextView = getView().findViewById(R.id.lastNameField);
        return lastNameTextView.getText().toString();
    }

    private String getUsername() {
        TextView usernameTextView = getView().findViewById(R.id.usernameField);
        return usernameTextView.getText().toString();
    }

    private String getPassword() {
        TextView passwordTextView = getView().findViewById(R.id.passwordField);
        return passwordTextView.getText().toString();
    }

    private String getConfirmedPassword() {
        TextView confirmPasswordTextView = getView().findViewById(R.id.confirmPasswordField);
        return confirmPasswordTextView.getText().toString();
    }

    private boolean isNameValid(String firstName, String lastName) {
        return firstName.length() >= 3 && lastName.length() >= 3;
    }

    private boolean isUsernameValid(String username) {
        String regexPattern = "[\\w.@+-]{8,}\\Z";
        return username.matches(regexPattern);
    }

    private boolean isPasswordValid(String password, String confirmedPassword) {
        return password.length() >= 8 && password.equals(confirmedPassword);
    }

    private void signUp(String firstName, String lastName,
                        String userName, String password) {
        onUserAuthRequestListener.onRequestPending();
        userViewModel.signupUser(getActivity().getApplicationContext(),
                firstName, lastName, userName, password);
    }

    private void login() {
        userViewModel.loginUser(getActivity().getApplicationContext(), username, password);
    }

    private void processSignupResponse(APIResponse<User> response) {
        switch (response.getStatus()) {
            case SUCCESFUL:
                login();
                break;
            case BAD_REQUEST:
                setErrorMessage(R.string.username_taken);
                break;
            case NETWORK_FAILED:
                setErrorMessage(R.string.network_error);
        }

        if (response.getStatus() != APIResponse.Status.SUCCESFUL) {
            username = "";
            password = "";
            onUserAuthRequestListener.onRequestFinished();
        }
    }

    private void processLoginResponse(APIResponse<AuthToken> response) {
        switch (response.getStatus()) {
            case SUCCESFUL:
                onUserAuthRequestListener.onRequestSuccessful();
                break;
            case NETWORK_FAILED:
                setErrorMessage(R.string.network_error);
        }
    }
}
