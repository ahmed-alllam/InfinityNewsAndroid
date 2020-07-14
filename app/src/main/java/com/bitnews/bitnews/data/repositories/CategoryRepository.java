package com.bitnews.bitnews.data.repositories;

import android.content.Context;

import com.bitnews.bitnews.data.db.AppDatabase;
import com.bitnews.bitnews.data.db.dao.CategoryDao;
import com.bitnews.bitnews.data.models.Category;
import com.bitnews.bitnews.data.models.ResponseList;
import com.bitnews.bitnews.data.network.APIEndpoints;
import com.bitnews.bitnews.data.network.APIResponse;
import com.bitnews.bitnews.data.network.APIService;
import com.bitnews.bitnews.data.network.NetworkBoundResource;

import io.reactivex.Single;


public class CategoryRepository {
    private static CategoryRepository instance;
    private APIEndpoints apiEndpoints = APIService.getService();
    private CategoryDao categoryDao;

    public static CategoryRepository getInstance(Context context) {
        if (instance == null) {
            instance = new CategoryRepository();
            instance.categoryDao = AppDatabase.getInstance(context).getCategoryDao();
        }
        return instance;
    }

    public Single<APIResponse<ResponseList<Category>>> getAllCategories() {
        return new NetworkBoundResource<ResponseList<Category>>() {
            @Override
            protected boolean shouldFetchFromDB() {
                return true;
            }

            @Override
            protected Single<ResponseList<Category>> fetchFromDB() {
                return categoryDao.getAllCategories().map(categories -> {
                    ResponseList<Category> response = new ResponseList<>();
                    response.setItems(categories);
                    response.setCount(categories.size());
                    return response;
                });
            }

            @Override
            protected boolean shouldFetchFromAPI(ResponseList<Category> data) {
                return true;
            }

            @Override
            protected Single<ResponseList<Category>> getAPICall() {
                return apiEndpoints.getAllCategories();
            }

            @Override
            protected void saveToDB(ResponseList<Category> list, boolean isUpdate) {
                categoryDao.addCategories(list.getItems());
            }

            @Override
            protected boolean shouldSaveToDB(ResponseList<Category> apiResponse, ResponseList<Category> dbResponse) {
                apiResponse.getItems().removeAll(dbResponse.getItems());
                return !apiResponse.getItems().isEmpty();
            }
        }.asSingle();
    }
}
