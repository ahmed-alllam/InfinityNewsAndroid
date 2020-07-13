package com.bitnews.bitnews.data.repositories;


import android.content.Context;

import com.bitnews.bitnews.data.db.AppDatabase;
import com.bitnews.bitnews.data.db.dao.AuthTokenDao;
import com.bitnews.bitnews.data.db.dao.UserDao;
import com.bitnews.bitnews.data.models.AuthToken;
import com.bitnews.bitnews.data.models.User;
import com.bitnews.bitnews.data.network.APIEndpoints;
import com.bitnews.bitnews.data.network.APIResponse;
import com.bitnews.bitnews.data.network.APIService;
import com.bitnews.bitnews.data.network.NetworkBoundResource;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.Subject;

public class UserRepository {
    private static UserRepository instance;
    private APIEndpoints apiEndpoints = APIService.getService();
    private UserDao userDao;
    private AuthTokenDao authTokenDao;

    public static UserRepository getInstance(Context context) {
        if (instance == null) {
            instance = new UserRepository();
            AppDatabase appDatabase = AppDatabase.getInstance(context);
            instance.userDao = appDatabase.getUserDao();
            instance.authTokenDao = appDatabase.getAuthTokenDao();
        }
        return instance;
    }

    public Subject<APIResponse<User>> getCurrentUser() {
        return new NetworkBoundResource<User>() {
            @Override
            protected void saveToDB(User user, boolean isUpdate) {
                if (isUpdate) {
                    userDao.updateUser(user);
                } else {
                    user.setCurrentUser(true);
                    userDao.addUser(user);
                }
            }

            @Override
            protected boolean shouldFetchFromAPI(User data) {
                return true;
            }

            @Override
            protected Single<User> fetchFromDB() {
                return userDao.getCurrentUser();
            }

            @Override
            protected Single<User> getAPICall() {
                return apiEndpoints.getCurrentUser();
            }

            @Override
            protected boolean shouldFetchFromDB() {
                return true;
            }
        }.asSubject();
    }

    public Subject<APIResponse<User>> signupUser(String firstName, String lastName,
                                                 String userName, String password) {
        return new NetworkBoundResource<User>() {
            @Override
            protected void saveToDB(User user, boolean isUpdate) {
                user.setCurrentUser(true);
                userDao.addUser(user);
            }

            @Override
            protected boolean shouldFetchFromAPI(User data) {
                return true;
            }

            @Override
            protected Single<User> fetchFromDB() {
                return null;
            }

            @Override
            protected boolean shouldFetchFromDB() {
                return false;
            }

            @Override
            protected Single<User> getAPICall() {
                return apiEndpoints.signUp(firstName, lastName, userName, password);
            }
        }.asSubject();
    }

    public Subject<APIResponse<User>> signupAsGuest() {
        return new NetworkBoundResource<User>() {
            @Override
            protected void saveToDB(User user, boolean isUpdate) {
                user.setGuest(true);
                user.setCurrentUser(true);
                userDao.addUser(user);
            }

            @Override
            protected boolean shouldFetchFromDB() {
                return false;
            }

            @Override
            protected boolean shouldFetchFromAPI(User data) {
                return true;
            }

            @Override
            protected Single<User> fetchFromDB() {
                return null;
            }

            @Override
            protected Single<User> getAPICall() {
                return apiEndpoints.sinUpAsGuest(true);
            }
        }.asSubject();
    }


    public Subject<APIResponse<AuthToken>> loginUser(String userName, String password) {
        return new NetworkBoundResource<AuthToken>() {
            @Override
            protected void saveToDB(AuthToken token, boolean isUpdate) {
                authTokenDao.addAuthToken(token);
            }

            @Override
            protected boolean shouldFetchFromAPI(AuthToken data) {
                return true;
            }

            @Override
            protected Single<AuthToken> fetchFromDB() {
                return null;
            }

            @Override
            protected boolean shouldFetchFromDB() {
                return false;
            }

            @Override
            protected Single<AuthToken> getAPICall() {
                if (password.isEmpty())
                    return apiEndpoints.logIn(userName);
                return apiEndpoints.logIn(userName, password);
            }
        }.asSubject();
    }

    public void logoutUser() {
        Subject.fromCallable(() -> {
            userDao.deleteCurrentUser();
            authTokenDao.deleteAuthToken();

            return null;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public Single<Boolean> isUserAuthenticated() {
        return authTokenDao.isUserAuthenticated()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Boolean> isUserAuthenticatedAndNotGuest() {
        return userDao.isUserAuthenticatedAndNotGuest()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
