package com.bitnews.bitnews.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.bitnews.bitnews.data.db.dao.AuthTokenDao;
import com.bitnews.bitnews.data.db.dao.UserDao;
import com.bitnews.bitnews.data.models.AuthToken;
import com.bitnews.bitnews.data.models.User;

@Database(entities = {User.class, AuthToken.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "bitnews-db";
    private static AppDatabase instance;

    public abstract UserDao getUserDao();

    public abstract AuthTokenDao getAuthTokenDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null)
            instance = Room.databaseBuilder(context,
                    AppDatabase.class, DATABASE_NAME).build();
        return instance;
    }
}
