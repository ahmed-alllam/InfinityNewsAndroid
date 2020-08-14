package com.bitnews.bitnews.data.repositories;

import android.content.Context;
import android.text.format.DateUtils;

import com.bitnews.bitnews.data.db.AppDatabase;
import com.bitnews.bitnews.data.db.dao.CommentDao;
import com.bitnews.bitnews.data.db.dao.UserDao;
import com.bitnews.bitnews.data.models.Comment;
import com.bitnews.bitnews.data.models.ResponseList;
import com.bitnews.bitnews.data.models.User;
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
            lastTimestamp = TimeStampParser.getCurrentTime();
        else
            lastTimestamp = timestamp;

        return new NetworkBoundResource<ResponseList<Comment>>() {
            @Override
            protected boolean shouldFetchFromDB() {
                return true;
            }

            @Override
            protected Single<ResponseList<Comment>> fetchFromDB() {
                return commentDao.getCommentsForPost(postSlug, timestamp).map(comments -> {
                    addUsersToComments(comments);

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

                return timeDifference > DateUtils.HOUR_IN_MILLIS * 5;
            }

            @Override
            protected Single<ResponseList<Comment>> getAPICall() {
                return apiEndpoints.getCommentsForPost(postSlug,
                        PaginationCursorGenerator.getPositionCursor(lastTimestamp));
            }

            @Override
            protected void saveToDB(ResponseList<Comment> item, boolean isUpdate) {
                List<Comment> comments = item.getItems();

                commentDao.insertComments(comments);

                List<User> users = new ArrayList<>();

                for (Comment comment : comments) {
                    users.add(comment.getUser());
                }

                userDao.addUsers(users);
            }

            @Override
            protected boolean shouldReturnDbResponseOnError(ResponseList<Comment> dbResponse) {
                return !dbResponse.getItems().isEmpty();
            }
        }.asSingle();
    }

    private void addUsersToComments(List<Comment> comments) {
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
