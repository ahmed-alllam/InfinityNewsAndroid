package com.bitnews.bitnews.data.models;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

import java.util.Objects;

public class Source {
    @PrimaryKey()
    @NonNull
    private String slug = "";
    private String name;
    private String description;
    private String photo;
    private boolean isFavouritedByUser;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Source source = (Source) o;
        return isFavouritedByUser == source.isFavouritedByUser &&
                Objects.equals(slug, source.slug) &&
                Objects.equals(name, source.name) &&
                Objects.equals(description, source.description) &&
                Objects.equals(photo, source.photo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slug, name, description, photo, isFavouritedByUser);
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPhoto() {
        return photo;
    }

    public boolean isFavouritedByUser() {
        return isFavouritedByUser;
    }
}
