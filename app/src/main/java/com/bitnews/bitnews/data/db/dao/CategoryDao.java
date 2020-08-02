package com.bitnews.bitnews.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.bitnews.bitnews.data.models.Category;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;

@Dao
public abstract class CategoryDao {

    @Query("SELECT * FROM category WHERE sort > :offset order by sort")
    public abstract Single<List<Category>> getAllCategories(int offset);

    @Query("SELECT * FROM category WHERE slug IN(:slugs)")
    public abstract List<Category> getCategories(List<String> slugs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void addCategories(List<Category> categories);

    @Query("SELECT CASE WHEN " +
            "(SELECT COUNT(*) FROM category WHERE isFavouritedByUser = 1) >= 3 " +
            "THEN 1 " +
            "ELSE 0 " +
            "END")
    public abstract Single<Boolean> hasFavouriteCategories();

    @Query("SELECT * FROM category WHERE isFavouritedByUser = 1 order by sort")
    public abstract Single<List<Category>> getFavouriteCategories();

    @Query("UPDATE category " +
            "SET isFavouritedByUser = CASE " +
            "WHEN slug IN (:slugs) THEN 1 " +
            "ELSE 0 " +
            "END")
    public abstract void setFavouriteCategoriesBySlug(List<String> slugs);

    public void setFavouriteCategories(List<Category> categories) {
        ArrayList<String> categoriesSlugs = new ArrayList<>();

        for (Category category : categories) {
            categoriesSlugs.add(category.getSlug());
        }
        setFavouriteCategoriesBySlug(categoriesSlugs);
    }

    @Query("UPDATE category " +
            "SET isFavouritedByUser = 0 ")
    public abstract void removeFavouriteCategories();
}
