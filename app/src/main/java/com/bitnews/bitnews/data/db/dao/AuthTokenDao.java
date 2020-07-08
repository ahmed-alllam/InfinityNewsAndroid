package com.bitnews.bitnews.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;

import com.bitnews.bitnews.data.models.AuthToken;

@Dao
public interface AuthTokenDao {

    @Insert
    public void addAuthToken(AuthToken authToken);

    @Delete
    public void deleteAuthToken(AuthToken authToken);
}
