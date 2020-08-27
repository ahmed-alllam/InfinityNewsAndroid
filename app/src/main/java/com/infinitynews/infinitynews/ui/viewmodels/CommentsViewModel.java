package com.infinitynews.infinitynews.ui.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.infinitynews.infinitynews.data.models.Comment;
import com.infinitynews.infinitynews.data.models.ResponseList;
import com.infinitynews.infinitynews.data.network.APIResponse;
import com.infinitynews.infinitynews.data.repositories.CommentRepository;

import io.reactivex.disposables.CompositeDisposable;

public class CommentsViewModel extends ViewModel {
    private MutableLiveData<APIResponse<ResponseList<Comment>>> commentsLiveData = new MutableLiveData<>();
    private CommentRepository commentRepository;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private CommentRepository getCommentRepository(Context context) {
        if (commentRepository == null)
            commentRepository = new CommentRepository(context);
        return commentRepository;
    }

    public void getComments(Context context, String postSlug, String lastTimestamp) {
        compositeDisposable.add(getCommentRepository(context)
                .getCommentsForPost(postSlug, lastTimestamp)
                .subscribe(commentsLiveData::setValue));
    }

    public LiveData<APIResponse<Comment>> sendCommentForPost(Context context, String postSlug,
                                                             String text) {
        MutableLiveData<APIResponse<Comment>> commentLiveData = new MutableLiveData<>();
        compositeDisposable.add(getCommentRepository(context)
                .sendCommentForPost(postSlug, text).subscribe(commentLiveData::setValue));
        return commentLiveData;
    }

    public MutableLiveData<APIResponse<ResponseList<Comment>>> getCommentsLiveData() {
        return commentsLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
