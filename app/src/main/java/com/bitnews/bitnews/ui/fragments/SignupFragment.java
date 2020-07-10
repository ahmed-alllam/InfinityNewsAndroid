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
import com.bitnews.bitnews.data.models.User;
import com.bitnews.bitnews.data.network.APIResponse;
import com.bitnews.bitnews.ui.viewmodels.UserViewModel;


public class SignupFragment extends Fragment implements UserAuthFragmentListener {
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

    @Override
    public void sendAuthRequest() {
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
                    errorLabel.setText(R.string.invalid_name);
                else if (!usernameValid)
                    errorLabel.setText(R.string.invalid_username);
                else
                    errorLabel.setText(R.string.invalid_password);
            }
        } else {
            login();
        }
    }

    @Override
    public void setErrorMessage(int messageID) {
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
        String regexPattern = "^(?=.{8,36}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$";
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
                errorLabel.setText(R.string.username_taken);
                break;
            case NETWORK_FAILED:
                errorLabel.setText(R.string.network_error);
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
                // TODO: 2020-07-10: start next activity
                errorLabel.setText("success");
                break;
            case NETWORK_FAILED:
                errorLabel.setText(R.string.network_error);
        }
    }
}
