package com.bitnews.bitnews.ui.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bitnews.bitnews.R;
import com.bitnews.bitnews.callbacks.UserAuthFragmentListener;
import com.bitnews.bitnews.ui.viewmodels.UserViewModel;


public class SignupFragment extends Fragment implements UserAuthFragmentListener {
    private UserViewModel userViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getUser().observe(getViewLifecycleOwner(), (response -> {
            System.out.println(response.getError().getMessage());
        }));
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void sendAuthRequest() {
        // TODO: 2020-07-09
        signup("", "", "", "");
    }

    public void signup(String firstName, String lastName,
                       String userName, String password) {
        userViewModel.signupUser(getActivity().getApplicationContext(),
                firstName, lastName, userName, password);
    }
}
