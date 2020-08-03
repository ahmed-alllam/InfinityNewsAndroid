package com.bitnews.bitnews.data.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.bitnews.bitnews.utils.DateTimeConverter;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.Objects;

@Entity
public class Category {
    @PrimaryKey()
    @NonNull
    private String slug = "";
    private int sort;
    private String title;
    private String image;
    @SerializedName("is_favourited_by_user")
    private boolean isFavouritedByUser;
    @TypeConverters(DateTimeConverter.class)
    private Date lastUpdated;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(slug, category.slug);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slug);
    }

    @NonNull
    public String getSlug() {
        return slug;
    }

    public void setSlug(@NonNull String slug) {
        this.slug = slug;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isFavouritedByUser() {
        return isFavouritedByUser;
    }

    public void setFavouritedByUser(boolean favouritedByUser) {
        isFavouritedByUser = favouritedByUser;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
