package com.bitnews.bitnews.ui.viewmodels;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bitnews.bitnews.data.models.Post;
import com.bitnews.bitnews.data.models.ResponseList;
import com.bitnews.bitnews.data.network.APIResponse;
import com.bitnews.bitnews.data.repositories.PostRepository;

import io.reactivex.disposables.CompositeDisposable;

public class PostViewModel extends ViewModel {
    private MutableLiveData<APIResponse<ResponseList<Post>>> postsLiveData = new MutableLiveData<>();
    private MutableLiveData<APIResponse<Post>> detailedPostLiveData = new MutableLiveData<>();
    private PostRepository postRepository;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private PostRepository getPostRepository(Context context) {
        if (postRepository == null)
            postRepository = new PostRepository(context);
        return postRepository;
    }

    public void getPosts(Context context, String categorySlug, String timestamp, boolean before) {
        compositeDisposable.add(getPostRepository(context)
                .getPosts(categorySlug, timestamp, before)
                .subscribe(postsLiveData::setValue));
    }

    public void getDetailedPost(Context context, String postSlug) {
        compositeDisposable.add(getPostRepository(context)
                .getPost(postSlug).subscribe(detailedPostLiveData::setValue));
    }

    public MutableLiveData<APIResponse<ResponseList<Post>>> getPostsLiveData() {
        return postsLiveData;
    }

    public MutableLiveData<APIResponse<Post>> getDetailedPostLiveData() {
        return detailedPostLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
