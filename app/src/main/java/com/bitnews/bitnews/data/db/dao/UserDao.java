package com.bitnews.bitnews.data.db.dao;

import androidx.room.Insert;
import androidx.room.Query;

import com.bitnews.bitnews.data.models.User;

public interface UserDao {

    @Query("SELECT * FROM user WHERE isCurrentUser=1")
    public User getCurrentUser();

    @Insert
    public void addUser(User user);
}
