package com.infinitynews.infinitynews.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.infinitynews.infinitynews.data.db.dao.AuthTokenDao;
import com.infinitynews.infinitynews.data.db.dao.CategoryDao;
import com.infinitynews.infinitynews.data.db.dao.CommentDao;
import com.infinitynews.infinitynews.data.db.dao.PostDao;
import com.infinitynews.infinitynews.data.db.dao.SourceDao;
import com.infinitynews.infinitynews.data.db.dao.UserDao;
import com.infinitynews.infinitynews.data.models.AuthToken;
import com.infinitynews.infinitynews.data.models.Category;
import com.infinitynews.infinitynews.data.models.Comment;
import com.infinitynews.infinitynews.data.models.Post;
import com.infinitynews.infinitynews.data.models.Source;
import com.infinitynews.infinitynews.data.models.User;

@Database(entities = {User.class, AuthToken.class, Category.class,
        Post.class, Source.class, Comment.class},
        version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "infinitynews-db";
    private static volatile AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null)
                    instance = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
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

    public abstract CommentDao getCommentDao();
}
