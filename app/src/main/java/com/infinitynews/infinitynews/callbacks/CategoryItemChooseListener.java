package com.infinitynews.infinitynews.callbacks;

import com.infinitynews.infinitynews.data.models.Category;

public interface CategoryItemChooseListener {

    void onCategoryChosen(Category category);

    void onCategoryUnchosen(Category category);
}
