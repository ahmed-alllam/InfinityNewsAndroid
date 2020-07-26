package com.bitnews.bitnews.ui.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bitnews.bitnews.R;
import com.bitnews.bitnews.adapters.PostsRecyclerAdapter;
import com.bitnews.bitnews.callbacks.PaginationScrollListener;
import com.bitnews.bitnews.data.models.Category;
import com.bitnews.bitnews.data.models.Post;
import com.bitnews.bitnews.ui.viewmodels.PostViewModel;

import java.util.List;

public class PostsFragment extends Fragment {
    private Category category;
    private PostViewModel postViewModel;
    private RecyclerView postsRecyclerView;
    private PostsRecyclerAdapter postsRecyclerAdapter;
    private SwipeRefreshLayout postsSwipeLayout;
    private TextView postsErrorLabel;
    private int totalPostsCount;
    private int fetchedPostsCount;
    private boolean isRefreshing;
    private String firstTimestamp;
    private String lastTimestamp;

    public PostsFragment(Category category) {
        this.category = category;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        postViewModel.getPostsLiveData().observe(getViewLifecycleOwner(), response -> {
            postsRecyclerView.suppressLayout(false);
            isRefreshing = false;
            postsSwipeLayout.setRefreshing(false);

            switch (response.getStatus()) {
                case SUCCESFUL:
                    onSuccessfulResponse(response.getitem().getItems(), response.getitem().getCount());
                    break;
                case NETWORK_FAILED:
                    onErrorResponse();
                    break;
            }
        });

        postsRecyclerView = view.findViewById(R.id.postsRecyclerView);
        postsRecyclerAdapter = new PostsRecyclerAdapter(postsRecyclerView, (v -> {
            if (!postsRecyclerAdapter.isLoading() && !isRefreshing)
                loadPosts(false, false);
        }));
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postsRecyclerView.setAdapter(postsRecyclerAdapter);
        postsRecyclerView.addOnScrollListener(getOnScrollListener());

        postsSwipeLayout = view.findViewById(R.id.postSwipeRefreshLayout);
        postsSwipeLayout.setColorSchemeColors(Color.YELLOW, Color.RED, Color.GREEN, Color.BLUE);
        postsSwipeLayout.setOnRefreshListener(this::onSwipeLayoutListener);

        postsErrorLabel = view.findViewById(R.id.postsErrorLabel);

        loadPosts(true, false);
    }

    private void onSuccessfulResponse(List<Post> posts, int count) {
        if (!posts.isEmpty()) {
            if (count > 0)
                totalPostsCount = count;
            else
                totalPostsCount = -1;
            fetchedPostsCount += posts.size();
            lastTimestamp = posts.get(posts.size() - 1).getTimestamp();
            if (isRefreshing || postsRecyclerAdapter.isEmpty())
                firstTimestamp = posts.get(0).getTimestamp();

            if (isRefreshing)
                postsRecyclerAdapter.addAll(0, posts);
            else
                postsRecyclerAdapter.addAll(posts);
        } else {
            if (postsRecyclerAdapter.isEmpty()) {
                postsRecyclerAdapter.setLoadingInitially(false);
                postsRecyclerView.setVisibility(View.INVISIBLE);
                postsErrorLabel.setVisibility(View.VISIBLE);
                postsErrorLabel.setText(R.string.no_feed);
            } else
                postsRecyclerAdapter.setLoadingFailed(true);
        }
    }

    private void onErrorResponse() {
        if (postsRecyclerAdapter.isEmpty()) {
            postsRecyclerAdapter.setLoadingInitially(false);
            postsRecyclerView.setVisibility(View.INVISIBLE);
            postsErrorLabel.setVisibility(View.VISIBLE);
            postsErrorLabel.setText(R.string.network_error);
        } else {
            postsRecyclerAdapter.setLoadingFailed(true);
        }
    }

    private PaginationScrollListener getOnScrollListener() {
        return new PaginationScrollListener(postsRecyclerView.getLayoutManager()) {
            @Override
            public boolean isLastPage() {
                return fetchedPostsCount >= totalPostsCount && totalPostsCount != -1;
            }

            @Override
            public boolean isLoading() {
                return postsRecyclerAdapter.isLoading() || postsRecyclerAdapter.isLoadingFailedAdded() || isRefreshing;
            }

            @Override
            public void loadMoreItems() {
                loadPosts(false, false);
            }
        };
    }

    private void onSwipeLayoutListener() {
        if (!postsRecyclerAdapter.isLoading()) {
            isRefreshing = true;
            boolean before = !postsRecyclerAdapter.isEmpty();
            loadPosts(true, before);
        } else {
            postsSwipeLayout.setRefreshing(false);
        }
    }

    private void loadPosts(boolean isLoadingInitally, boolean before) {
        postsRecyclerView.setVisibility(View.VISIBLE);
        postsErrorLabel.setVisibility(View.INVISIBLE);

        String timestamp = lastTimestamp;
        if (isLoadingInitally) {
            if (!before) {
                postsRecyclerAdapter.setLoadingInitially(true);
                postsRecyclerView.suppressLayout(true);
            } else
                timestamp = firstTimestamp;
        } else {
            postsRecyclerAdapter.setLoadingMore(true);
        }

        postViewModel.getPosts(getContext(), category.getSlug(), timestamp, before);
    }
}
