package com.bitnews.bitnews.ui.viewmodels;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bitnews.bitnews.data.models.Comment;
import com.bitnews.bitnews.data.models.ResponseList;
import com.bitnews.bitnews.data.network.APIResponse;
import com.bitnews.bitnews.data.repositories.CommentRepository;

import io.reactivex.disposables.CompositeDisposable;

class CommentsViewModel extends ViewModel {
    private MutableLiveData<APIResponse<ResponseList<Comment>>> commentsLiveData = new MutableLiveData<>();
    private CommentRepository commentRepository;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private CommentRepository getCommentRepository(Context context) {
        if (commentRepository == null)
            commentRepository = new CommentRepository(context);
        return commentRepository;
    }

    public void getComments(Context context, String categorySlug, String lastTimestamp) {
        compositeDisposable.add(getCommentRepository(context)
                .getCommentsForPost(categorySlug, lastTimestamp)
                .subscribe(commentsLiveData::setValue));
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
