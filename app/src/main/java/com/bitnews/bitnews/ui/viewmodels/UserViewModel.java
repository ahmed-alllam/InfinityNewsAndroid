package com.bitnews.bitnews.ui.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.bitnews.bitnews.data.models.AuthToken;
import com.bitnews.bitnews.data.models.User;
import com.bitnews.bitnews.data.network.APIResponse;
import com.bitnews.bitnews.data.repositories.UserRepository;

public class UserViewModel extends ViewModel {
    private UserRepository userRepository;
    private MediatorLiveData<APIResponse<User>> user = new MediatorLiveData<>();
    private MediatorLiveData<APIResponse<AuthToken>> token = new MediatorLiveData<>();

    public UserRepository getUserRepository(Context context) {
        if (userRepository == null)
            userRepository = UserRepository.getInstance(context);
        return userRepository;
    }

    public void signupUser(Context context, String firstName, String lastName,
                           String userName, String password) {
        LiveData<APIResponse<User>> repositoryLiveData = getUserRepository(context)
                .signupUser(firstName, lastName, userName, password);

        user.addSource(repositoryLiveData, (response) -> {
            user.removeSource(repositoryLiveData);
            user.setValue(response);
        });
    }

    public void loginUser(Context context, String userName, String password) {
        LiveData<APIResponse<AuthToken>> repositoryLiveData = getUserRepository(context)
                .loginUser(userName, password);

        token.addSource(repositoryLiveData, (response) -> {
            token.removeSource(repositoryLiveData);
            token.setValue(response);
        });
    }

    public MediatorLiveData<APIResponse<User>> getUser() {
        return user;
    }

    public MediatorLiveData<APIResponse<AuthToken>> getToken() {
        return token;
    }
}
