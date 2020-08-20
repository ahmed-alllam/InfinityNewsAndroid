package com.bitnews.bitnews.data.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.bitnews.bitnews.utils.ListToStringConverter;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

@Entity
public class Post {
    @PrimaryKey
    @NonNull
    private String slug = "";
    private String thumbnail;
    @SerializedName("full_image")
    private String fullImage;
    private String title;
    private String description;
    private String timestamp;
    @ForeignKey(entity = Source.class, parentColumns = "slug", childColumns = "sourceSlug")
    private String sourceSlug;
    @ForeignKey(entity = Category.class, parentColumns = "slug", childColumns = "categorySlug")
    private String categorySlug;
    @SerializedName("detailed_url")
    private String detailedUrl;
    private String body;
    @Ignore
    private Source source;
    @Ignore
    private Category category;
    @TypeConverters(ListToStringConverter.class)
    private List<String> tags;
    @SerializedName("comments_count")
    private int commentsCount;

    public Post() {
    }

    @Ignore
    public Post(String fullImage, String title,
                String description, String timestamp) {
        this.fullImage = fullImage;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return commentsCount == post.commentsCount &&
                slug.equals(post.slug) &&
                Objects.equals(thumbnail, post.thumbnail) &&
                Objects.equals(title, post.title) &&
                Objects.equals(description, post.description) &&
                Objects.equals(timestamp, post.timestamp) &&
                Objects.equals(sourceSlug, post.sourceSlug) &&
                Objects.equals(categorySlug, post.categorySlug) &&
                Objects.equals(detailedUrl, post.detailedUrl) &&
                Objects.equals(body, post.body) &&
                Objects.equals(source, post.source) &&
                Objects.equals(category, post.category) &&
                Objects.equals(tags, post.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slug, thumbnail, title, description, timestamp, sourceSlug, categorySlug, detailedUrl, body, source, category, tags, commentsCount);
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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getFullImage() {
        return fullImage;
    }

    public void setFullImage(String fullImage) {
        this.fullImage = fullImage;
    }

    public String getSourceSlug() {
        return source != null ? source.getSlug() : sourceSlug;
    }

    public void setSourceSlug(String sourceSlug) {
        this.sourceSlug = sourceSlug;
    }

    public String getCategorySlug() {
        return category != null ? category.getSlug() : categorySlug;
    }

    public void setCategorySlug(String categorySlug) {
        this.categorySlug = categorySlug;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDetailedUrl() {
        return detailedUrl;
    }

    public void setDetailedUrl(String detailedUrl) {
        this.detailedUrl = detailedUrl;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }
}
