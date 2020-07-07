package com.bitnews.bitnews.data.models;

import java.util.Objects;

public class Source {
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
                Objects.equals(name, source.name) &&
                Objects.equals(description, source.description) &&
                Objects.equals(photo, source.photo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, photo, isFavouritedByUser);
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
