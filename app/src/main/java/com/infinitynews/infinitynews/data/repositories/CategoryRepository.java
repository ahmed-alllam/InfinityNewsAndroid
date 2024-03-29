package com.infinitynews.infinitynews.data.repositories;

import android.content.Context;
import android.text.format.DateUtils;

import com.infinitynews.infinitynews.data.db.AppDatabase;
import com.infinitynews.infinitynews.data.db.dao.AuthTokenDao;
import com.infinitynews.infinitynews.data.db.dao.CategoryDao;
import com.infinitynews.infinitynews.data.models.Category;
import com.infinitynews.infinitynews.data.models.ResponseList;
import com.infinitynews.infinitynews.data.network.APIEndpoints;
import com.infinitynews.infinitynews.data.network.APIResponse;
import com.infinitynews.infinitynews.data.network.APIService;
import com.infinitynews.infinitynews.data.network.NetworkBoundResource;
import com.infinitynews.infinitynews.utils.PaginationCursorGenerator;

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

    public Single<APIResponse<ResponseList<Category>>> getAllCategories(int offset, boolean isRefresh) {
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
                if (isRefresh || data == null || data.getItems().isEmpty())
                    return true;

                long timeDifference = new Date().getTime() - data.getItems().get(0).getLastUpdated().getTime();

                return timeDifference > DateUtils.HOUR_IN_MILLIS;
            }

            @Override
            protected Single<ResponseList<Category>> getAPICall() {
                return apiEndpoints.getAllCategories(PaginationCursorGenerator
                        .getPositionCursor(String.valueOf(offset)))
                        .flatMap(categories -> {
                            if (AuthTokenDao.getToken().isEmpty()) {
                                return categoryDao.getFavouriteCategories().map(favourites -> {
                                    for (Category category : favourites) {
                                        int position = categories.getItems().indexOf(category);
                                        if (position >= 0)
                                            categories.getItems().get(position).setFavouritedByUser(true);
                                    }
                                    return categories;
                                });
                            } else
                                return Single.just(categories);
                        });
            }

            @Override
            protected void saveToDB(ResponseList<Category> list, boolean isUpdate) {
                for (Category category : list.getItems())
                    category.setLastUpdated(new Date());

                categoryDao.addCategories(list.getItems());
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
                if (AuthTokenDao.getToken().isEmpty())
                    return false;

                if (data == null || data.isEmpty())
                    return true;

                long timeDifference = new Date().getTime() - data.get(0).getLastUpdated().getTime();

                return timeDifference > DateUtils.HOUR_IN_MILLIS * 5;
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
                if (AuthTokenDao.getToken().isEmpty())
                    return Single.just(new Object());

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
