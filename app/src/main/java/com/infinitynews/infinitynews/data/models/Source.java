package com.infinitynews.infinitynews.data.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Source {
    @PrimaryKey()
    @NonNull
    private String slug = "";
    private String title;
    private String description;
    private String image;
    private String website;

    public Source() {
    }

    @Ignore
    public Source(String title, String image) {
        this.title = title;
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Source source = (Source) o;
        return slug.equals(source.slug) &&
                Objects.equals(title, source.title) &&
                Objects.equals(description, source.description) &&
                Objects.equals(image, source.image) &&
                Objects.equals(website, source.website);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slug, title, description, image, website);
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
