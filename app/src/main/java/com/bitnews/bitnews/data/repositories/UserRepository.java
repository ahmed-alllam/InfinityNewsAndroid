package com.bitnews.bitnews.data.repositories;


import android.content.Context;

import androidx.lifecycle.LiveData;

import com.bitnews.bitnews.data.db.AppDatabase;
import com.bitnews.bitnews.data.db.dao.AuthTokenDao;
import com.bitnews.bitnews.data.db.dao.UserDao;
import com.bitnews.bitnews.data.models.AuthToken;
import com.bitnews.bitnews.data.models.User;
import com.bitnews.bitnews.data.network.APIEndpoints;
import com.bitnews.bitnews.data.network.APIResponse;
import com.bitnews.bitnews.data.network.APIService;
import com.bitnews.bitnews.data.network.NetworkBoundResource;
import com.bitnews.bitnews.utils.AppExecutors;

import retrofit2.Call;

public class UserRepository {
    private static UserRepository instance;
    private AppExecutors appExecutors = AppExecutors.getInstance();
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

    public LiveData<APIResponse<User>> getCurrentUser() {
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
            protected User fetchFromDB() {
                return userDao.getCurrentUser();
            }

            @Override
            protected Call<User> getCall() {
                return apiEndpoints.getCurrentUser();
            }

            @Override
            protected boolean shouldFetchFromDB() {
                return true;
            }
        }.asLiveData();
    }

    public LiveData<APIResponse<User>> signupUser(String firstName, String lastName,
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
            protected User fetchFromDB() {
                return null;
            }

            @Override
            protected boolean shouldFetchFromDB() {
                return false;
            }

            @Override
            protected Call<User> getCall() {
                return apiEndpoints.signUp(firstName, lastName, userName, password);
            }
        }.asLiveData();
    }

    public LiveData<APIResponse<User>> signupAsGuest() {
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
            protected User fetchFromDB() {
                return null;
            }

            @Override
            protected Call<User> getCall() {
                return apiEndpoints.sinUpAsGuest(true);
            }
        }.asLiveData();
    }


    public LiveData<APIResponse<AuthToken>> loginUser(String userName, String password) {
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
            protected AuthToken fetchFromDB() {
                return null;
            }

            @Override
            protected boolean shouldFetchFromDB() {
                return false;
            }

            @Override
            protected Call<AuthToken> getCall() {
                if (password.isEmpty())
                    return apiEndpoints.logIn(userName);
                return apiEndpoints.logIn(userName, password);
            }
        }.asLiveData();
    }

    public void logoutUser() {
        appExecutors.getDiskIO().execute(() -> {
            userDao.deleteCurrentUser();
            authTokenDao.deleteAuthToken();
        });
    }

    public LiveData<APIResponse<Boolean>> isUserAuthenticated() {
        return new NetworkBoundResource<Boolean>() {
            @Override
            protected void saveToDB(Boolean item, boolean isUpdate) {
            }

            @Override
            protected boolean shouldFetchFromDB() {
                return true;
            }

            @Override
            protected boolean shouldFetchFromAPI(Boolean data) {
                return false;
            }

            @Override
            protected Boolean fetchFromDB() {
                return authTokenDao.getAuthToken() != null;
            }

            @Override
            protected Call<Boolean> getCall() {
                return null;
            }
        }.asLiveData();
    }

    public LiveData<APIResponse<Boolean>> isUserAuthenticatedAndNotGuest() {
        return new NetworkBoundResource<Boolean>() {
            @Override
            protected void saveToDB(Boolean item, boolean isUpdate) {
            }

            @Override
            protected boolean shouldFetchFromDB() {
                return true;
            }

            @Override
            protected boolean shouldFetchFromAPI(Boolean data) {
                return false;
            }

            @Override
            protected Boolean fetchFromDB() {
                return userDao.isUserAuthenticatedAndNotGuest();
            }

            @Override
            protected Call<Boolean> getCall() {
                return null;
            }
        }.asLiveData();
    }
}
