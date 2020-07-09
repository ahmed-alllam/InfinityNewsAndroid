package com.bitnews.bitnews.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.bitnews.bitnews.data.models.User;

@Dao
public interface UserDao {

    @Query("SELECT * FROM user WHERE isCurrentUser=1;")
    User getCurrentUser();

    @Insert
    void addUser(User user);
}
