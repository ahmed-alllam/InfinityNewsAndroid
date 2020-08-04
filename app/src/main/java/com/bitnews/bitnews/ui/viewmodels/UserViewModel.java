package com.bitnews.bitnews.ui.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bitnews.bitnews.data.models.AuthToken;
import com.bitnews.bitnews.data.models.User;
import com.bitnews.bitnews.data.network.APIResponse;
import com.bitnews.bitnews.data.repositories.UserRepository;

import io.reactivex.disposables.CompositeDisposable;

public class UserViewModel extends ViewModel {
    private UserRepository userRepository;
    private MediatorLiveData<APIResponse<User>> user = new MediatorLiveData<>();
    private MediatorLiveData<APIResponse<AuthToken>> token = new MediatorLiveData<>();
    private CompositeDisposable disposable = new CompositeDisposable();

    private UserRepository getUserRepository(Context context) {
        if (userRepository == null)
            userRepository = new UserRepository(context);
        return userRepository;
    }

    public void signupUser(Context context, String firstName, String lastName,
                           String userName, String password) {
        disposable.add(getUserRepository(context)
                .signupUser(firstName, lastName, userName, password)
                .subscribe(user::setValue));
    }

    public void signupAsGuest(Context context) {
        disposable.add(getUserRepository(context)
                .signupAsGuest()
                .subscribe(user::setValue));
    }

    public void loginUser(Context context, String userName, String password) {
        disposable.add(getUserRepository(context)
                .loginUser(userName, password)
                .subscribe(token::setValue));
    }

    public LiveData<Object> logoutUser(Context context) {
        MutableLiveData<Object> liveData = new MutableLiveData<>();
        disposable.add(getUserRepository(context).logoutUser().subscribe(liveData::setValue));
        return liveData;
    }

    public LiveData<Boolean> isUserAuthenticated(Context context) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        disposable.add(getUserRepository(context).isUserAuthenticated()
                .onErrorReturn(t -> false)
                .subscribe(liveData::setValue));
        return liveData;
    }

    public LiveData<Boolean> hasUserProfile(Context context) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        disposable.add(getUserRepository(context).hasUserProfile()
                .subscribe(liveData::setValue));
        return liveData;
    }

    public void getCurrentUser(Context context) {
        disposable.add(getUserRepository(context).getCurrentUser().subscribe(user::setValue));
    }

    public MediatorLiveData<APIResponse<User>> getUser() {
        return user;
    }

    public MediatorLiveData<APIResponse<AuthToken>> getToken() {
        return token;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.dispose();
    }
}
