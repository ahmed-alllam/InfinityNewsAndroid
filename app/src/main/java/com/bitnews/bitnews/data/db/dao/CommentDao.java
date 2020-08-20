package com.bitnews.bitnews.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.bitnews.bitnews.data.models.Comment;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface CommentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertComments(List<Comment> comments);

    @Query("SELECT * from comment " +
            "WHERE postSlug = :postSlug " +
            "AND datetime(timestamp) < datetime(:lastTimeStamp) " +
            "ORDER BY datetime(timestamp) DESC " +
            "LIMIT 10 ")
    Single<List<Comment>> getCommentsForPost(String postSlug, String lastTimeStamp);
}
