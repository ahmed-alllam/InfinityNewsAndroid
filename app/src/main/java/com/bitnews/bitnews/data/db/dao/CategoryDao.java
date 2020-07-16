package com.bitnews.bitnews.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.bitnews.bitnews.data.models.Category;

import java.util.List;

import io.reactivex.Single;

@Dao
public abstract class CategoryDao {

    @Query("SELECT * FROM category WHERE sort > :lastSort;")
    public abstract Single<List<Category>> getAllCategories(int lastSort);

    @Insert
    public abstract void addCategories(List<Category> categories);

    @Delete
    public abstract void deleteCategory(Category category);
}
