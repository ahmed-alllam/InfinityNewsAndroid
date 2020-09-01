package com.infinitynews.infinitynews.data.repositories;

import android.content.Context;
import android.text.format.DateUtils;

import com.infinitynews.infinitynews.data.db.AppDatabase;
import com.infinitynews.infinitynews.data.db.dao.CategoryDao;
import com.infinitynews.infinitynews.data.db.dao.PostDao;
import com.infinitynews.infinitynews.data.db.dao.SourceDao;
import com.infinitynews.infinitynews.data.models.Category;
import com.infinitynews.infinitynews.data.models.Post;
import com.infinitynews.infinitynews.data.models.ResponseList;
import com.infinitynews.infinitynews.data.models.Source;
import com.infinitynews.infinitynews.data.network.APIEndpoints;
import com.infinitynews.infinitynews.data.network.APIResponse;
import com.infinitynews.infinitynews.data.network.APIService;
import com.infinitynews.infinitynews.data.network.NetworkBoundResource;
import com.infinitynews.infinitynews.utils.PaginationCursorGenerator;
import com.infinitynews.infinitynews.utils.TimeStampParser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Single;

public class PostRepository {
    private APIEndpoints apiEndpoints = APIService.getService();
    private PostDao postDao;
    private CategoryDao categoryDao;
    private SourceDao sourceDao;

    public PostRepository(Context context) {
        AppDatabase appDatabase = AppDatabase.getInstance(context);
        postDao = appDatabase.getPostsDao();
        categoryDao = appDatabase.getCategoryDao();
        sourceDao = appDatabase.getSourceDao();
    }

    public Single<APIResponse<ResponseList<Post>>> getPosts(String categorySlug,
                                                            String timestamp, boolean before) {
        String lastTimestamp;
        if (timestamp == null || timestamp.isEmpty())
            lastTimestamp = TimeStampParser.getCurrentTimeString();
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
                if (data == null || data.getItems().isEmpty() || before)
                    return true;

                Date postDate = TimeStampParser.getDateFromString(data.getItems().get(0).getTimestamp());

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
        }.asSingle();
    }

    public Single<APIResponse<Post>> getPost(String postSlug) {
        return new NetworkBoundResource<Post>() {
            @Override
            protected boolean shouldFetchFromDB() {
                return true;
            }

            @Override
            protected Single<Post> fetchFromDB() {
                return postDao.getPost(postSlug);
            }

            @Override
            protected boolean shouldFetchFromAPI(Post post) {
                if (post == null || post.getBody() == null || post.getBody().isEmpty())
                    return true;

                Date postDate = TimeStampParser.getDateFromString(post.getTimestamp());

                long timeDifference = new Date().getTime() - postDate.getTime();

                return timeDifference > DateUtils.HOUR_IN_MILLIS * 5;
            }

            @Override
            protected Single<Post> getAPICall() {
                return apiEndpoints.getPost(postSlug);
            }

            @Override
            protected void saveToDB(Post post, boolean isUpdate) {
                postDao.insertPost(post);
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

        List<Category> categories = categoryDao.getCategoriesBySlugs(categoriesSlugs);
        List<Source> sources = sourceDao.getSourcesBySlugs(sourcesSlugs);
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

            assert post.getCategory() != null;
            assert post.getSource() != null;
        }
    }
}
