package com.infinitynews.infinitynews.data.repositories;

import android.content.Context;
import android.text.format.DateUtils;

import com.infinitynews.infinitynews.data.db.AppDatabase;
import com.infinitynews.infinitynews.data.db.dao.CommentDao;
import com.infinitynews.infinitynews.data.db.dao.UserDao;
import com.infinitynews.infinitynews.data.models.Comment;
import com.infinitynews.infinitynews.data.models.ResponseList;
import com.infinitynews.infinitynews.data.models.User;
import com.infinitynews.infinitynews.data.network.APIEndpoints;
import com.infinitynews.infinitynews.data.network.APIResponse;
import com.infinitynews.infinitynews.data.network.APIService;
import com.infinitynews.infinitynews.data.network.NetworkBoundResource;
import com.infinitynews.infinitynews.utils.PaginationCursorGenerator;
import com.infinitynews.infinitynews.utils.TimeStampParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.reactivex.Single;

public class CommentRepository {
    private APIEndpoints apiEndpoints = APIService.getService();
    private CommentDao commentDao;
    private UserDao userDao;

    public CommentRepository(Context context) {
        AppDatabase appDatabase = AppDatabase.getInstance(context);
        commentDao = appDatabase.getCommentDao();
        userDao = appDatabase.getUserDao();
    }

    public Single<APIResponse<ResponseList<Comment>>> getCommentsForPost(String postSlug,
                                                                         String timestamp) {
        String lastTimestamp;
        if (timestamp == null || timestamp.isEmpty())
            lastTimestamp = TimeStampParser.getCurrentTimeString();
        else
            lastTimestamp = timestamp;

        return new NetworkBoundResource<ResponseList<Comment>>() {
            @Override
            protected boolean shouldFetchFromDB() {
                return true;
            }

            @Override
            protected Single<ResponseList<Comment>> fetchFromDB() {
                return commentDao.getCommentsForPost(postSlug, lastTimestamp).map(comments -> {
                    addNestedFieldsToComments(comments);

                    ResponseList<Comment> responseList = new ResponseList<>();
                    responseList.setItems(comments);
                    return responseList;
                });
            }

            @Override
            protected boolean shouldFetchFromAPI(ResponseList<Comment> data) {
                if (data == null || data.getItems().isEmpty())
                    return true;

                Date commentDate = TimeStampParser.getDateFromString(data.getItems().get(0).getTimestamp());

                long timeDifference = new Date().getTime() - commentDate.getTime();

                return timeDifference > DateUtils.HOUR_IN_MILLIS / 2;
            }

            @Override
            protected Single<ResponseList<Comment>> getAPICall() {
                return apiEndpoints.getCommentsForPost(postSlug,
                        PaginationCursorGenerator.getPositionCursor(lastTimestamp));
            }

            @Override
            protected void saveToDB(ResponseList<Comment> item, boolean isUpdate) {
                List<Comment> comments = item.getItems();
                List<User> users = new ArrayList<>();

                for (Comment comment : comments) {
                    users.add(comment.getUser());
                    comment.setPostSlug(postSlug);
                    comment.setUserUsername(comment.getUser().getUsername());
                }

                commentDao.insertComments(comments);
                userDao.addUsers(users);
            }
        }.asSingle();
    }

    public Single<APIResponse<Comment>> sendCommentForPost(String postSlug,
                                                           String text) {
        return new NetworkBoundResource<Comment>() {
            @Override
            protected boolean shouldFetchFromDB() {
                return false;
            }

            @Override
            protected Single<Comment> fetchFromDB() {
                return null;
            }

            @Override
            protected boolean shouldFetchFromAPI(Comment data) {
                return true;
            }

            @Override
            protected Single<Comment> getAPICall() {
                return apiEndpoints.sendCommentForPost(postSlug, text);
            }

            @Override
            protected void saveToDB(Comment comment, boolean isUpdate) {
                comment.setTimestamp(TimeStampParser.getCurrentTimeString());
                comment.setPostSlug(postSlug);
                comment.setUserUsername(comment.getUser().getUsername());
                commentDao.insertComments(Collections.singletonList(comment));
            }
        }.asSingle();
    }

    private void addNestedFieldsToComments(List<Comment> comments) {
        List<String> usernames = new ArrayList<>();
        for (Comment comment : comments) {
            usernames.add(comment.getUserUsername());
        }

        List<User> users = userDao.getUsersByUsernames(usernames);
        for (Comment comment : comments) {
            for (User user : users) {
                if (user.getUsername().equals(comment.getUserUsername())) {
                    comment.setUser(user);
                    break;
                }
            }

            assert comment.getUser() != null;
        }
    }
}
