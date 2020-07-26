package com.bitnews.bitnews.data.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.bitnews.bitnews.utils.ListToStringConverter;

import java.util.List;
import java.util.Objects;

@Entity
public class Post {
    @PrimaryKey
    @NonNull
    private String slug = "";
    private String title;
    private String description;
    private String timestamp;
    @ForeignKey(entity = Source.class, parentColumns = "slug", childColumns = "sourceSlug")
    private String sourceSlug;
    @ForeignKey(entity = Category.class, parentColumns = "slug", childColumns = "categorySlug")
    private String categorySlug;
    @TypeConverters(ListToStringConverter.class)
    private List<String> tags;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return slug.equals(post.slug) &&
                Objects.equals(title, post.title) &&
                Objects.equals(description, post.description) &&
                Objects.equals(timestamp, post.timestamp) &&
                Objects.equals(sourceSlug, post.sourceSlug) &&
                Objects.equals(categorySlug, post.categorySlug) &&
                Objects.equals(tags, post.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slug, title, description, timestamp, sourceSlug, categorySlug, tags);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSourceSlug() {
        return sourceSlug;
    }

    public void setSourceSlug(String sourceSlug) {
        this.sourceSlug = sourceSlug;
    }

    public String getCategorySlug() {
        return categorySlug;
    }

    public void setCategorySlug(String categorySlug) {
        this.categorySlug = categorySlug;
    }

    @NonNull
    public String getSlug() {
        return slug;
    }

    public void setSlug(@NonNull String slug) {
        this.slug = slug;
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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
