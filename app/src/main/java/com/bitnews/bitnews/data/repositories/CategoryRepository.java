package com.bitnews.bitnews.data.repositories;

import android.content.Context;
import android.text.format.DateUtils;

import com.bitnews.bitnews.data.db.AppDatabase;
import com.bitnews.bitnews.data.db.dao.CategoryDao;
import com.bitnews.bitnews.data.models.Category;
import com.bitnews.bitnews.data.models.ResponseList;
import com.bitnews.bitnews.data.network.APIEndpoints;
import com.bitnews.bitnews.data.network.APIResponse;
import com.bitnews.bitnews.data.network.APIService;
import com.bitnews.bitnews.data.network.NetworkBoundResource;
import com.bitnews.bitnews.utils.PaginationCursorGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;


public class CategoryRepository {
    private APIEndpoints apiEndpoints = APIService.getService();
    private CategoryDao categoryDao;

    public CategoryRepository(Context context) {
        AppDatabase appDatabase = AppDatabase.getInstance(context);
        categoryDao = appDatabase.getCategoryDao();
    }

    public Single<APIResponse<ResponseList<Category>>> getAllCategories(int offset) {
        return new NetworkBoundResource<ResponseList<Category>>() {
            @Override
            protected boolean shouldFetchFromDB() {
                return true;
            }

            @Override
            protected Single<ResponseList<Category>> fetchFromDB() {
                return categoryDao.getAllCategories(offset).map(categories -> {
                    ResponseList<Category> response = new ResponseList<>();
                    response.setItems(categories);
                    return response;
                });
            }

            @Override
            protected boolean shouldFetchFromAPI(ResponseList<Category> data) {
                if (data == null || data.getItems().isEmpty())
                    return true;

                long timeDifference = new Date().getTime() - data.getItems().get(0).getLastUpdated().getTime();

                return timeDifference > DateUtils.DAY_IN_MILLIS * 4;
            }

            @Override
            protected Single<ResponseList<Category>> getAPICall() {
                return apiEndpoints.getAllCategories(
                        PaginationCursorGenerator.getPositionCursor(String.valueOf(offset)));
            }

            @Override
            protected void saveToDB(ResponseList<Category> list, boolean isUpdate) {
                for (Category category : list.getItems())
                    category.setLastUpdated(new Date());

                categoryDao.addCategories(list.getItems());
            }

            @Override
            protected boolean shouldReturnDbResponseOnError(ResponseList<Category> dbResponse) {
                return !dbResponse.getItems().isEmpty();
            }
        }.asSingle();
    }

    public Single<APIResponse<List<Category>>> getFavouriteCategories() {
        return new NetworkBoundResource<List<Category>>() {
            @Override
            protected boolean shouldFetchFromDB() {
                return true;
            }

            @Override
            protected Single<List<Category>> fetchFromDB() {
                return categoryDao.getFavouriteCategories();
            }

            @Override
            protected boolean shouldFetchFromAPI(List<Category> data) {
                if (data == null || data.isEmpty())
                    return true;

                long timeDifference = new Date().getTime() - data.get(0).getLastUpdated().getTime();

                return timeDifference > DateUtils.DAY_IN_MILLIS * 7;
            }

            @Override
            protected Single<List<Category>> getAPICall() {
                return apiEndpoints.getFavouriteCategories();
            }

            @Override
            protected void saveToDB(List<Category> items, boolean isUpdate) {
                categoryDao.setFavouriteCategories(items);
            }
        }.asSingle();
    }

    public Single<APIResponse<Object>> updateFavouriteCategories(List<Category> categories) {
        return new NetworkBoundResource<Object>() {
            @Override
            protected boolean shouldFetchFromDB() {
                return false;
            }

            @Override
            protected Single<Object> fetchFromDB() {
                return null;
            }

            @Override
            protected boolean shouldFetchFromAPI(Object data) {
                return true;
            }

            @Override
            protected Single<Object> getAPICall() {
                return apiEndpoints.updateFavouriteCategories(generateCategoriesRequestBody(categories))
                        .toSingleDefault(new Object());
            }

            @Override
            protected void saveToDB(Object item, boolean isUpdate) {
                categoryDao.setFavouriteCategories(categories);
            }
        }.asSingle();
    }

    public Single<Boolean> hasFavouriteCategories() {
        return categoryDao.hasFavouriteCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private RequestBody generateCategoriesRequestBody(List<Category> categories) {
        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();

        for (Category category : categories) {
            array.put(category.getSlug());
        }

        try {
            jsonObject.put("categories", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
    }
}
