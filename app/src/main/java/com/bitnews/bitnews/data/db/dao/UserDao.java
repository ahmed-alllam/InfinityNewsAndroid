package com.bitnews.bitnews.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.bitnews.bitnews.data.models.User;

import io.reactivex.Single;

@Dao
public abstract class UserDao {

    @Query("SELECT * FROM user WHERE isCurrentUser=1")
    public abstract Single<User> getCurrentUser();

    @Query("DELETE FROM user WHERE isCurrentUser=1")
    public abstract void deleteCurrentUser();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void addUser(User user);

    @Update
    public abstract void updateUser(User user);

    @Query("SELECT EXISTS(SELECT * FROM user WHERE isCurrentUser=1)")
    public abstract Single<Boolean> hasUserProfile();
}
