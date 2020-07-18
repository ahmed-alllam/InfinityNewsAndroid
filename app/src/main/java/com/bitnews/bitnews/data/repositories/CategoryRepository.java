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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.reactivex.Single;
import okhttp3.MediaType;
import okhttp3.RequestBody;


public class CategoryRepository {
    private APIEndpoints apiEndpoints;
    private CategoryDao categoryDao;

    public CategoryRepository(Context context) {
        AppDatabase appDatabase = AppDatabase.getInstance(context);
        categoryDao = appDatabase.getCategoryDao();
        apiEndpoints = APIService.getService(context);
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

            @Override
            protected boolean shouldReturnDbResponseOnError(ResponseList<Category> dbResponse) {
                return !dbResponse.getItems().isEmpty();
            }
        }.asSingle();
    }

    public Single<APIResponse<Integer>> updateFavouriteCategories(List<Category> categories) {
        return new NetworkBoundResource<Integer>() {
            @Override
            protected boolean shouldFetchFromDB() {
                return false;
            }

            @Override
            protected Single<Integer> fetchFromDB() {
                return null;
            }

            @Override
            protected boolean shouldFetchFromAPI(Integer data) {
                return true;
            }

            @Override
            protected Single<Integer> getAPICall() {
                return apiEndpoints.updateFavouriteCategories(generateCategoriesRequestBody(categories))
                        .toSingleDefault(0);
            }

            @Override
            protected void saveToDB(Integer item, boolean isUpdate) {
                categoryDao.setFavouriteCategories(categories);
            }
        }.asSingle();
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

    private String generateCategoryCursor(int lastSort) {
        return PaginationCursorGenerator.getPositionCursor(lastSort);
    }
}
