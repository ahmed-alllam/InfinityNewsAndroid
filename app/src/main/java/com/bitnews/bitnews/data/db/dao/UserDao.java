package com.bitnews.bitnews.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.bitnews.bitnews.data.models.User;

@Dao
public abstract class UserDao {

    @Query("SELECT * FROM user WHERE isCurrentUser=1;")
    public abstract User getCurrentUser();

    @Query("DELETE FROM user WHERE isCurrentUser=1;")
    public abstract void deleteCurrentUser();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void addUser(User user);

    // todo change to auth token dao
    public Boolean isUserAuthenticated() {
        return getCurrentUser() != null;
    }

    public Boolean isUserAuthenticatedAndNotGuest() {
        User user = getCurrentUser();
        return user != null && !user.isGuest();
    }
}
