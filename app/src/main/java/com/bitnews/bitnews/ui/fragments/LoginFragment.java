package com.bitnews.bitnews.ui.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.bitnews.bitnews.R;
import com.bitnews.bitnews.callbacks.UserAuthFragmentListener;

public class LoginFragment extends Fragment implements UserAuthFragmentListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void sendAuthRequest() {
        // TODO: 2020-07-09
    }
}
