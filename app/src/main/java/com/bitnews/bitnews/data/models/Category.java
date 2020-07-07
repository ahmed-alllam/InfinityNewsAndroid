package com.bitnews.bitnews.data.models;

import java.util.Objects;

public class Category {
    private String slug;

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

    public String getSlug() {
        return slug;
    }
}
