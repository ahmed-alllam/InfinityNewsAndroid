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
import com.bitnews.bitnews.utils.PaginationCursorGenerator;

import io.reactivex.Single;


public class CategoryRepository {
    private APIEndpoints apiEndpoints = APIService.getService();
    private CategoryDao categoryDao;

    public CategoryRepository(Context context) {
        AppDatabase appDatabase = AppDatabase.getInstance(context);
        categoryDao = appDatabase.getCategoryDao();
    }

    public Single<APIResponse<ResponseList<Category>>> getAllCategories(int lastSort) {
        return new NetworkBoundResource<ResponseList<Category>>() {
            @Override
            protected boolean shouldFetchFromDB() {
                return true;
            }

            @Override
            protected Single<ResponseList<Category>> fetchFromDB() {
                return categoryDao.getAllCategories(lastSort).map(categories -> {
                    ResponseList<Category> response = new ResponseList<>();
                    response.setItems(categories);
                    return response;
                });
            }

            @Override
            protected boolean shouldFetchFromAPI(ResponseList<Category> data) {
                return true;
            }

            @Override
            protected Single<ResponseList<Category>> getAPICall() {
                return apiEndpoints.getAllCategories(generateCategoryCursor(lastSort));
            }

            @Override
            protected void saveToDB(ResponseList<Category> list, boolean isUpdate) {
                categoryDao.addCategories(list.getItems());
            }
        }.asSingle();
    }

    private String generateCategoryCursor(int lastSort) {
        return PaginationCursorGenerator.getPositionCursor(lastSort);
    }
}
