package com.bitnews.bitnews.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.bitnews.bitnews.data.models.AuthToken;

import io.reactivex.Single;

@Dao
public abstract class AuthTokenDao {

    @Insert
    public abstract void addAuthToken(AuthToken authToken);

    @Query("DELETE FROM authtoken;")
    public abstract void deleteAuthToken();

    @Query("SELECT * FROM authtoken;")
    public abstract Single<AuthToken> getAuthToken();

    public Single<Boolean> isUserAuthenticated() {
        // return getAuthToken().map((token -> token != null));
        // TODO: 2020-07-13
        return Single.just(false);
    }
}
