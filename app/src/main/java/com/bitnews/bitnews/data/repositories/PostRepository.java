package com.bitnews.bitnews.data.repositories;

import android.content.Context;

import com.bitnews.bitnews.data.db.AppDatabase;
import com.bitnews.bitnews.data.db.dao.PostDao;
import com.bitnews.bitnews.data.models.Post;
import com.bitnews.bitnews.data.models.ResponseList;
import com.bitnews.bitnews.data.network.APIEndpoints;
import com.bitnews.bitnews.data.network.APIResponse;
import com.bitnews.bitnews.data.network.APIService;
import com.bitnews.bitnews.data.network.NetworkBoundResource;
import com.bitnews.bitnews.utils.PaginationCursorGenerator;
import com.bitnews.bitnews.utils.TimeStampParser;

import io.reactivex.Single;

public class PostRepository {
    private APIEndpoints apiEndpoints = APIService.getService();
    private PostDao postDao;

    public PostRepository(Context context) {
        AppDatabase appDatabase = AppDatabase.getInstance(context);
        postDao = appDatabase.getPostsDao();
    }

    public Single<APIResponse<ResponseList<Post>>> getPosts(String categorySlug,
                                                            String timestamp, boolean before) {
        String lastTimestamp;
        if (timestamp == null || timestamp.isEmpty())
            lastTimestamp = TimeStampParser.getCurrentTime();
        else
            lastTimestamp = timestamp;

        return new NetworkBoundResource<ResponseList<Post>>() {
            @Override
            protected boolean shouldFetchFromDB() {
                return true;
            }

            @Override
            protected Single<ResponseList<Post>> fetchFromDB() {
                return postDao.getAllPostsByCategory(categorySlug, lastTimestamp, before).map(response -> {
                    ResponseList<Post> responseList = new ResponseList<>();
                    responseList.setItems(response);
                    return responseList;
                });
            }

            @Override
            protected boolean shouldFetchFromAPI(ResponseList<Post> data) {
                return true;
            }

            @Override
            protected Single<ResponseList<Post>> getAPICall() {
                return apiEndpoints.getPostsByCategory(categorySlug,
                        PaginationCursorGenerator.getPositionReverseCursor(lastTimestamp, before));
            }

            @Override
            protected void saveToDB(ResponseList<Post> item, boolean isUpdate) {
                postDao.insertPosts(item.getItems());
            }

            @Override
            protected boolean shouldReturnDbResponseOnError(ResponseList<Post> dbResponse) {
                return !dbResponse.getItems().isEmpty();
            }
        }.asSingle();
    }
}
