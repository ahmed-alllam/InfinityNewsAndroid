package com.bitnews.bitnews.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.bitnews.bitnews.data.models.Post;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface PostDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertPosts(List<Post> posts);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPost(Post post);

    @Query("SELECT * FROM post" +
            " WHERE ((datetime(timestamp) < datetime(:timestamp) and :before = 0)" +
            " or (datetime(timestamp) > datetime(:timestamp) and :before = 1))" +
            " AND categorySlug = :categorySlug" +
            " ORDER BY timestamp DESC" +
            " LIMIT 40")
    Single<List<Post>> getAllPostsByCategory(String categorySlug, String timestamp, boolean before);

    @Query("SELECT * FROM post WHERE slug = :postSlug AND LENGTH(body) > 0")
    Single<Post> getPost(String postSlug);
}
