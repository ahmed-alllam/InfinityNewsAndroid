package com.bitnews.bitnews.ui.viewmodels;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.bitnews.bitnews.data.models.Category;
import com.bitnews.bitnews.data.models.ResponseList;
import com.bitnews.bitnews.data.network.APIResponse;
import com.bitnews.bitnews.data.repositories.CategoryRepository;

public class CategoryViewModel extends ViewModel {
    private CategoryRepository categoryRepository;
    private MediatorLiveData<APIResponse<ResponseList<Category>>> categories = new MediatorLiveData<>();

    private CategoryRepository getCategoryRepository(Context context) {
        if (categoryRepository == null)
            categoryRepository = CategoryRepository.getInstance(context);
        return categoryRepository;
    }

    @SuppressLint("CheckResult")
    public void getAllCategories(Context context) {
        getCategoryRepository(context).getAllCategories().subscribe(categories::setValue);
    }
}
