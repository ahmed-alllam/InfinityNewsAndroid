package com.bitnews.bitnews.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.bitnews.bitnews.data.models.AuthToken;

import io.reactivex.Single;

@Dao
public abstract class AuthTokenDao {
    private static String token;

    @Insert
    public abstract void addAuthToken(AuthToken authToken);

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        AuthTokenDao.token = token;
    }

    @Query("DELETE FROM authtoken")
    public abstract void deleteAuthToken();

    @Query("SELECT * FROM authtoken")
    public abstract Single<AuthToken> getAuthToken();
}
