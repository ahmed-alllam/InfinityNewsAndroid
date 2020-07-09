package com.bitnews.bitnews.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.bitnews.bitnews.data.models.AuthToken;

@Dao
public interface AuthTokenDao {

    @Insert
    void addAuthToken(AuthToken authToken);

    @Query("DELETE FROM authtoken;")
    void deleteAuthToken();
}
