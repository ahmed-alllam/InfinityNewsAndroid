package com.bitnews.bitnews.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.bitnews.bitnews.data.models.Comment;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface CommentDao {

    @Insert
    void insertComments(List<Comment> comments);

    @Query("SELECT * from comment " +
            "WHERE postSlug = :postSlug " +
            "AND datetime(timestamp) < datetime(:lastTimeStamp) " +
            "ORDER BY timestamp DESC " +
            "LIMIT 15 ")
    Single<List<Comment>> getCommentsForPost(String postSlug, String lastTimeStamp);
}
