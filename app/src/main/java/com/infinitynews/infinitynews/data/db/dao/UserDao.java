package com.infinitynews.infinitynews.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.infinitynews.infinitynews.data.models.User;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface UserDao {

    @Query("SELECT * FROM user WHERE isCurrentUser=1")
    Single<User> getCurrentUser();

    @Query("SELECT * FROM user WHERE username IN(:usernames)")
    List<User> getUsersByUsernames(List<String> usernames);

    @Query("DELETE FROM user WHERE isCurrentUser=1")
    void deleteCurrentUser();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addUser(User user);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addUsers(List<User> user);

    @Update
    void updateUser(User user);
}
