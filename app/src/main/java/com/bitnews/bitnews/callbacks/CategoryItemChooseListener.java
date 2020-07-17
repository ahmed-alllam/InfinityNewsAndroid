package com.bitnews.bitnews.callbacks;

import com.bitnews.bitnews.data.models.Category;

public interface CategoryItemChooseListener {

    void onCategoryChosen(Category category);

    void onCategoryUnchosen(Category category);
}
