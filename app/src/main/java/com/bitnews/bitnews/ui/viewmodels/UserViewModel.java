package com.bitnews.bitnews.ui.viewmodels;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
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

    @SuppressLint("CheckResult")
    public void signupUser(Context context, String firstName, String lastName,
                           String userName, String password) {
        getUserRepository(context)
                .signupUser(firstName, lastName, userName, password)
                .subscribe(user::setValue);
    }

    @SuppressLint("CheckResult")
    public void signupAsGuest(Context context) {
        getUserRepository(context)
                .signupAsGuest()
                .subscribe(user::setValue);
    }

    @SuppressLint("CheckResult")
    public void loginUser(Context context, String userName, String password) {
        getUserRepository(context)
                .loginUser(userName, password)
                .subscribe(token::setValue);
    }

    public void logoutUser(Context context) {
        getUserRepository(context).logoutUser();
    }

    @SuppressLint("CheckResult")
    public LiveData<Boolean> isUserAuthenticated(Context context) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        getUserRepository(context).isUserAuthenticated().subscribe(liveData::setValue);
        return liveData;
    }

    @SuppressLint("CheckResult")
    public LiveData<Boolean> isUserAuthenticatedAndNotGuest(Context context) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        getUserRepository(context).isUserAuthenticated().subscribe(liveData::setValue);
        return liveData;
    }

    public MediatorLiveData<APIResponse<User>> getUser() {
        return user;
    }

    public MediatorLiveData<APIResponse<AuthToken>> getToken() {
        return token;
    }
}
