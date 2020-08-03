package com.bitnews.bitnews.data.repositories;

import android.content.Context;
import android.text.format.DateUtils;

import com.bitnews.bitnews.data.db.AppDatabase;
import com.bitnews.bitnews.data.db.dao.CategoryDao;
import com.bitnews.bitnews.data.db.dao.PostDao;
import com.bitnews.bitnews.data.db.dao.SourceDao;
import com.bitnews.bitnews.data.models.Category;
import com.bitnews.bitnews.data.models.Post;
import com.bitnews.bitnews.data.models.ResponseList;
import com.bitnews.bitnews.data.models.Source;
import com.bitnews.bitnews.data.network.APIEndpoints;
import com.bitnews.bitnews.data.network.APIResponse;
import com.bitnews.bitnews.data.network.APIService;
import com.bitnews.bitnews.data.network.NetworkBoundResource;
import com.bitnews.bitnews.utils.PaginationCursorGenerator;
import com.bitnews.bitnews.utils.TimeStampParser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Single;

public class PostRepository {
    private Context context;
    private APIEndpoints apiEndpoints = APIService.getService();
    private PostDao postDao;
    private CategoryDao categoryDao;
    private SourceDao sourceDao;

    public PostRepository(Context context) {
        this.context = context;
        AppDatabase appDatabase = AppDatabase.getInstance(context);
        postDao = appDatabase.getPostsDao();
        categoryDao = appDatabase.getCategoryDao();
        sourceDao = appDatabase.getSourceDao();
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
                return postDao.getAllPostsByCategory(categorySlug, lastTimestamp, before)
                        .map(posts -> {
                            addNestedFieldsToPosts(posts);

                            ResponseList<Post> responseList = new ResponseList<>();
                            responseList.setItems(posts);
                            return responseList;
                        });
            }

            @Override
            protected boolean shouldFetchFromAPI(ResponseList<Post> data) {
                if (data.getItems().isEmpty() || before)
                    return true;

                Date postDate = TimeStampParser.getDateFromString(context, data.getItems().get(0).getTimestamp());

                long timeDifference = new Date().getTime() - postDate.getTime();

                return timeDifference > DateUtils.MINUTE_IN_MILLIS * 5;
            }

            @Override
            protected Single<ResponseList<Post>> getAPICall() {
                return apiEndpoints.getPostsByCategory(categorySlug,
                        PaginationCursorGenerator.getPositionReverseCursor(lastTimestamp, before));
            }

            @Override
            protected void saveToDB(ResponseList<Post> item, boolean isUpdate) {
                List<Post> posts = item.getItems();
                postDao.insertPosts(posts);

                List<Source> sources = new ArrayList<>();
                for (Post post : posts) {
                    sources.add(post.getSource());
                }

                sourceDao.addSources(sources);
            }

            @Override
            protected boolean shouldReturnDbResponseOnError(ResponseList<Post> dbResponse) {
                return !dbResponse.getItems().isEmpty();
            }
        }.asSingle();
    }

    private void addNestedFieldsToPosts(List<Post> posts) {
        List<String> categoriesSlugs = new ArrayList<>();
        List<String> sourcesSlugs = new ArrayList<>();
        for (Post post : posts) {
            categoriesSlugs.add(post.getCategorySlug());
            sourcesSlugs.add(post.getSourceSlug());
        }

        List<Category> categories = categoryDao.getCategories(categoriesSlugs);
        List<Source> sources = sourceDao.getSources(sourcesSlugs);
        for (Post post : posts) {
            for (Category category : categories) {
                if (category.getSlug().equals(post.getCategorySlug())) {
                    post.setCategory(category);
                    break;
                }
            }

            for (Source source : sources) {
                if (source.getSlug().equals(post.getSourceSlug())) {
                    post.setSource(source);
                    break;
                }
            }
        }
    }
}
