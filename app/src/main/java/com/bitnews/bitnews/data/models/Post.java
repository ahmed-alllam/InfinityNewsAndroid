package com.bitnews.bitnews.data.models;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Junction;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import java.util.List;
import java.util.Objects;

@Entity
public class Post {
    @PrimaryKey
    @NonNull
    private String postSlug = "";
    private String title;
    private String description;
    private String timestamp;
    @Embedded
    private Source source;
    @Embedded
    private Category category;
    @Relation(parentColumn = "postSlug", entityColumn = "tag",
            associateBy = @Junction(PostTags.class))
    private List<Tag> tags;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return postSlug.equals(post.postSlug) &&
                Objects.equals(title, post.title) &&
                Objects.equals(description, post.description) &&
                Objects.equals(timestamp, post.timestamp) &&
                Objects.equals(source, post.source) &&
                Objects.equals(category, post.category) &&
                Objects.equals(tags, post.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postSlug, title, description, timestamp, source, category, tags);
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getPostSlug() {
        return postSlug;
    }

    public void setPostSlug(@NonNull String postSlug) {
        this.postSlug = postSlug;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
