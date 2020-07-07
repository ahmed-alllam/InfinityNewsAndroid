package com.bitnews.bitnews.data.models;

import java.util.List;
import java.util.Objects;

public class Post {
    private String slug;
    private String title;
    private String description;
    private String timestamp;
    private Source source;
    private Category category;
    private List<String> tags;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(slug, post.slug) &&
                Objects.equals(title, post.title) &&
                Objects.equals(description, post.description) &&
                Objects.equals(timestamp, post.timestamp) &&
                Objects.equals(source, post.source) &&
                Objects.equals(category, post.category) &&
                Objects.equals(tags, post.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slug, title, description, timestamp, source, category, tags);
    }

    public String getSlug() {
        return slug;
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

    public Source getSource() {
        return source;
    }

    public Category getCategory() {
        return category;
    }

    public List<String> getTags() {
        return tags;
    }
}
