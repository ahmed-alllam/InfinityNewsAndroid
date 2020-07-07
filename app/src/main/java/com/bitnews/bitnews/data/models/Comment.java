package com.bitnews.bitnews.data.models;

import java.util.Objects;

public class Comment {
    private User user;
    private Post post;
    private String slug;
    private String text;
    private String timestamp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(user, comment.user) &&
                Objects.equals(post, comment.post) &&
                Objects.equals(slug, comment.slug) &&
                Objects.equals(text, comment.text) &&
                Objects.equals(timestamp, comment.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, post, slug, text, timestamp);
    }

    public User getUser() {
        return user;
    }

    public Post getPost() {
        return post;
    }

    public String getSlug() {
        return slug;
    }

    public String getText() {
        return text;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
