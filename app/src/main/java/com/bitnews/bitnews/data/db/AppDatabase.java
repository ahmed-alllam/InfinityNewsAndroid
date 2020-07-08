package com.bitnews.bitnews.data.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.bitnews.bitnews.data.db.dao.AuthTokenDao;
import com.bitnews.bitnews.data.db.dao.UserDao;
import com.bitnews.bitnews.data.models.AuthToken;
import com.bitnews.bitnews.data.models.User;

@Database(entities = {User.class, AuthToken.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao getUserDao();

    public abstract AuthTokenDao getAuthTokenDao();
}
