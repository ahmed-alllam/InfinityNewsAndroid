package com.bitnews.bitnews.ui.viewmodels;

import android.content.Context;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.bitnews.bitnews.data.models.Category;
import com.bitnews.bitnews.data.models.ResponseList;
import com.bitnews.bitnews.data.network.APIResponse;
import com.bitnews.bitnews.data.repositories.CategoryRepository;

import io.reactivex.disposables.CompositeDisposable;

public class CategoryViewModel extends ViewModel {
    private CategoryRepository categoryRepository;
    private MediatorLiveData<APIResponse<ResponseList<Category>>> categories = new MediatorLiveData<>();
    private CompositeDisposable disposable = new CompositeDisposable();

    private CategoryRepository getCategoryRepository(Context context) {
        if (categoryRepository == null)
            categoryRepository = new CategoryRepository(context);
        return categoryRepository;
    }

    public void getAllCategories(Context context, int lastSort) {
        disposable.add(getCategoryRepository(context)
                .getAllCategories(lastSort)
                .subscribe(categories::setValue));
    }

    public MediatorLiveData<APIResponse<ResponseList<Category>>> getCategoriesLiveData() {
        return categories;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
