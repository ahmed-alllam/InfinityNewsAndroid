package com.infinitynews.infinitynews.data.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Comment {
    @PrimaryKey
    @NonNull
    private String slug = "";
    @Ignore
    private User user;
    @ForeignKey(entity = User.class, parentColumns = "username", childColumns = "userUsername")
    private String userUsername;
    @ForeignKey(entity = Post.class, parentColumns = "slug", childColumns = "postSlug")
    private String postSlug;
    private String text;
    private String timestamp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return slug.equals(comment.slug) &&
                Objects.equals(user, comment.user) &&
                Objects.equals(userUsername, comment.userUsername) &&
                Objects.equals(postSlug, comment.postSlug) &&
                Objects.equals(text, comment.text) &&
                Objects.equals(timestamp, comment.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slug, user, userUsername, postSlug, text, timestamp);
    }

    @NonNull
    public String getSlug() {
        return slug;
    }

    public void setSlug(@NonNull String slug) {
        this.slug = slug;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserUsername() {
        return userUsername;
    }

    public void setUserUsername(String userUsername) {
        this.userUsername = userUsername;
    }

    public String getPostSlug() {
        return postSlug;
    }

    public void setPostSlug(String postSlug) {
        this.postSlug = postSlug;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
