package com.infinitynews.infinitynews.ui.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.infinitynews.infinitynews.data.models.Category;
import com.infinitynews.infinitynews.data.models.ResponseList;
import com.infinitynews.infinitynews.data.network.APIResponse;
import com.infinitynews.infinitynews.data.repositories.CategoryRepository;

import java.util.List;

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

    public void getAllCategories(Context context, int offset, boolean isRefresh) {
        disposable.add(getCategoryRepository(context)
                .getAllCategories(offset, isRefresh)
                .subscribe(categories::setValue));
    }

    public void getFavouriteCategories(Context context) {
        disposable.add(getCategoryRepository(context)
                .getFavouriteCategories()
                .map(response -> {
                    ResponseList<Category> responseList = new ResponseList<>();
                    responseList.setItems(response.getitem());
                    return APIResponse.success(responseList);
                })
                .subscribe(categories::setValue));
    }

    public LiveData<APIResponse<Object>> updateFavouriteCategories(Context context, List<Category> categories) {
        MutableLiveData<APIResponse<Object>> responseLiveData = new MutableLiveData<>();
        disposable.add(getCategoryRepository(context)
                .updateFavouriteCategories(categories)
                .subscribe(responseLiveData::setValue));
        return responseLiveData;
    }

    public LiveData<Boolean> hasFavouriteCategories(Context context) {
        MutableLiveData<Boolean> responseLiveData = new MutableLiveData<>();
        disposable.add(getCategoryRepository(context)
                .hasFavouriteCategories().subscribe(responseLiveData::setValue));
        return responseLiveData;
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
