package com.infinitynews.infinitynews.data.repositories;


import android.content.Context;
import android.text.format.DateUtils;

import com.infinitynews.infinitynews.data.db.AppDatabase;
import com.infinitynews.infinitynews.data.db.dao.AuthTokenDao;
import com.infinitynews.infinitynews.data.db.dao.CategoryDao;
import com.infinitynews.infinitynews.data.db.dao.UserDao;
import com.infinitynews.infinitynews.data.models.AuthToken;
import com.infinitynews.infinitynews.data.models.User;
import com.infinitynews.infinitynews.data.network.APIEndpoints;
import com.infinitynews.infinitynews.data.network.APIResponse;
import com.infinitynews.infinitynews.data.network.APIService;
import com.infinitynews.infinitynews.data.network.NetworkBoundResource;

import java.util.Date;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class UserRepository {
    private APIEndpoints apiEndpoints = APIService.getService();
    private UserDao userDao;
    private CategoryDao categoryDao;
    private AuthTokenDao authTokenDao;

    public UserRepository(Context context) {
        AppDatabase appDatabase = AppDatabase.getInstance(context);
        userDao = appDatabase.getUserDao();
        authTokenDao = appDatabase.getAuthTokenDao();
        categoryDao = appDatabase.getCategoryDao();
    }

    public Single<APIResponse<User>> getCurrentUser() {
        if (AuthTokenDao.getToken().isEmpty())
            return Single.just(APIResponse.failed(null));

        return new NetworkBoundResource<User>() {
            @Override
            protected void saveToDB(User user, boolean isUpdate) {
                user.setLastUpdated(new Date());
                user.setCurrentUser(true);
                if (isUpdate) {
                    userDao.updateUser(user);
                } else {
                    userDao.addUser(user);
                }
            }

            @Override
            protected boolean shouldFetchFromAPI(User user) {
                if (user == null)
                    return true;

                long timeDifference = new Date().getTime() - user.getLastUpdated().getTime();

                return timeDifference > DateUtils.DAY_IN_MILLIS * 4;
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
        }.asSingle();
    }

    public Single<APIResponse<User>> signupUser(String firstName, String lastName,
                                                String userName, String password) {
        return new NetworkBoundResource<User>() {
            @Override
            protected void saveToDB(User user, boolean isUpdate) {
                if (!AuthTokenDao.getToken().isEmpty()) {
                    authTokenDao.deleteAuthToken();
                    AuthTokenDao.setToken("");
                    userDao.deleteCurrentUser();
                }
                user.setLastUpdated(new Date());
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
        }.asSingle();
    }

    public Single<APIResponse<AuthToken>> loginUser(String userName, String password) {
        return new NetworkBoundResource<AuthToken>() {
            @Override
            protected void saveToDB(AuthToken token, boolean isUpdate) {
                authTokenDao.addAuthToken(token);
                AuthTokenDao.setToken(token.getToken());
                categoryDao.removeFavouriteCategories();
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
                return apiEndpoints.logIn(userName, password);
            }
        }.asSingle();
    }

    public Completable logoutUser() {
        return Completable.fromAction(() -> {
            userDao.deleteCurrentUser();
            authTokenDao.deleteAuthToken();
            categoryDao.removeFavouriteCategories();
            AuthTokenDao.setToken("");
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Boolean> isUserAuthenticated() {
        return authTokenDao.getAuthTokenFromDB()
                .doOnSuccess((authToken -> AuthTokenDao.setToken(authToken.getToken())))
                .map(token -> !token.getToken().isEmpty())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
