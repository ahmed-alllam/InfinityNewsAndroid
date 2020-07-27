package com.bitnews.bitnews.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.bitnews.bitnews.data.db.dao.AuthTokenDao;
import com.bitnews.bitnews.data.db.dao.CategoryDao;
import com.bitnews.bitnews.data.db.dao.PostDao;
import com.bitnews.bitnews.data.db.dao.SourceDao;
import com.bitnews.bitnews.data.db.dao.UserDao;
import com.bitnews.bitnews.data.models.AuthToken;
import com.bitnews.bitnews.data.models.Category;
import com.bitnews.bitnews.data.models.Post;
import com.bitnews.bitnews.data.models.Source;
import com.bitnews.bitnews.data.models.Tag;
import com.bitnews.bitnews.data.models.User;

@Database(entities = {User.class, AuthToken.class, Category.class,
        Post.class, Source.class, Tag.class},
        version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "bitnews-db";
    private static volatile AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null)
                    instance = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
            }
        }
        return instance;
    }

    public abstract UserDao getUserDao();

    public abstract AuthTokenDao getAuthTokenDao();

    public abstract CategoryDao getCategoryDao();

    public abstract PostDao getPostsDao();

    public abstract SourceDao getSourceDao();
}
