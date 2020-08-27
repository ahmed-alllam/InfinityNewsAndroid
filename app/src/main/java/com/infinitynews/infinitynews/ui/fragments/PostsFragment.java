package com.infinitynews.infinitynews.ui.fragments;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.infinitynews.infinitynews.R;
import com.infinitynews.infinitynews.adapters.PostsRecyclerAdapter;
import com.infinitynews.infinitynews.callbacks.PaginationScrollListener;
import com.infinitynews.infinitynews.data.models.Category;
import com.infinitynews.infinitynews.data.models.Post;
import com.infinitynews.infinitynews.ui.activities.PostDetailActivity;
import com.infinitynews.infinitynews.ui.viewmodels.PostViewModel;

import java.util.List;

public class PostsFragment extends Fragment {
    private String categorySlug;
    private PostViewModel postViewModel;
    private RecyclerView postsRecyclerView;
    private PostsRecyclerAdapter postsRecyclerAdapter;
    private SwipeRefreshLayout postsSwipeLayout;
    private TextView feedEmptyLabel;
    private Button retryButton;
    private int totalPostsCount;
    private int fetchedPostsCount;
    private boolean isRefreshing;
    private String firstPostTimestamp;

    public PostsFragment() {
    }

    private String lastPostTimestamp;

    public PostsFragment(Category category) {
        Bundle bundle = new Bundle();
        bundle.putString("categorySlug", category.getSlug());
        setArguments(bundle);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        categorySlug = getArguments().getString("categorySlug");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        postViewModel.getPostsLiveData().observe(getViewLifecycleOwner(), response -> {
            postsRecyclerView.suppressLayout(false);
            postsSwipeLayout.setRefreshing(false);

            switch (response.getStatus()) {
                case SUCCESFUL:
                    onSuccessfulResponse(response.getitem().getItems(), response.getitem().getCount());
                    break;
                case NETWORK_FAILED:
                    onErrorResponse();
                    break;
            }

            isRefreshing = false;
        });

        postsRecyclerView = view.findViewById(R.id.postsRecyclerView);
        postsRecyclerAdapter = new PostsRecyclerAdapter(postsRecyclerView, (v -> {
            if (!postsRecyclerAdapter.isLoading() && !isRefreshing)
                loadPosts(false, false);
        }), (post -> {
            Intent intent = new Intent(getActivity(), PostDetailActivity.class);
            savePostInIntent(intent, post);
            startActivity(intent);
        }));
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postsRecyclerView.setAdapter(postsRecyclerAdapter);

        postsRecyclerView.addOnScrollListener(getOnScrollListener());

        postsSwipeLayout = view.findViewById(R.id.postSwipeRefreshLayout);
        postsSwipeLayout.setColorSchemeColors(Color.YELLOW, Color.RED, Color.GREEN, Color.BLUE);
        postsSwipeLayout.setOnRefreshListener(this::onSwipeLayoutListener);

        feedEmptyLabel = view.findViewById(R.id.feedEmptyLabel);
        retryButton = view.findViewById(R.id.retryButton);
        retryButton.setOnClickListener(v -> {
            if (!postsRecyclerAdapter.isLoading())
                loadPosts(true, false);
        });

        loadPosts(true, false);
    }

    private void onSuccessfulResponse(List<Post> posts, int count) {
        postsRecyclerAdapter.removeFooterItem();

        if (count > 0) {
            totalPostsCount = count;
        } else {
            totalPostsCount = -1;
        }

        if (!posts.isEmpty()) {
            fetchedPostsCount += posts.size();

            if (!isRefreshing)
                lastPostTimestamp = posts.get(posts.size() - 1).getTimestamp();

            if (isRefreshing || postsRecyclerAdapter.isEmpty())
                firstPostTimestamp = posts.get(0).getTimestamp();

            if (isRefreshing) {
                postsRecyclerAdapter.addAll(0, posts);
                scrollToTop();
            } else
                postsRecyclerAdapter.addAll(-1, posts);
        } else {
            if (postsRecyclerAdapter.isEmpty()) {
                postsRecyclerView.setVisibility(View.INVISIBLE);
                feedEmptyLabel.setVisibility(View.VISIBLE);
                feedEmptyLabel.setText(R.string.no_feed);
            }
        }

        postsRecyclerAdapter.finishLoading();
    }

    private void onErrorResponse() {
        if (postsRecyclerAdapter.isEmpty()) {
            postsRecyclerAdapter.setLoadingInitially(false);
            postsRecyclerView.setVisibility(View.INVISIBLE);
            retryButton.setVisibility(View.VISIBLE);
        } else {
            if (!isRefreshing) {
                postsRecyclerAdapter.setLoadingMore(false);
                postsRecyclerAdapter.setLoadingFailed(true);
                postsRecyclerAdapter.addFooterItem();
            }
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
                return postsRecyclerAdapter.isLoading() || postsRecyclerAdapter.isLoadingFailedAdded();
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

    public void scrollToTop() {
        if (postsRecyclerView != null)
            postsRecyclerView.post(() -> postsRecyclerView.smoothScrollToPosition(0));
    }

    public boolean isAtTop() {
        if (postsRecyclerView != null)
            return postsRecyclerView.computeVerticalScrollOffset() == 0;
        return true;
    }

    private void loadPosts(boolean isLoadingInitally, boolean before) {
        postsRecyclerView.setVisibility(View.VISIBLE);
        feedEmptyLabel.setVisibility(View.INVISIBLE);
        retryButton.setVisibility(View.INVISIBLE);

        String timestamp = lastPostTimestamp;
        if (isLoadingInitally) {
            if (!before) {
                postsRecyclerAdapter.setLoadingInitially(true);
                postsRecyclerView.suppressLayout(true);
            } else
                timestamp = firstPostTimestamp;
        } else {
            postsRecyclerAdapter.setLoadingFailed(false);
            postsRecyclerAdapter.setLoadingMore(true);
            postsRecyclerAdapter.addFooterItem();
        }

        postViewModel.getPosts(getActivity().getApplicationContext(), categorySlug, timestamp, before);
    }

    private void savePostInIntent(Intent intent, Post post) {
        intent.putExtra("postSlug", post.getSlug());
        intent.putExtra("postFullImage", post.getFullImage());
        intent.putExtra("postTitle", post.getTitle());
        intent.putExtra("postDescription", post.getDescription());
        intent.putExtra("postTimestamp", post.getTimestamp());

        if (post.getSource() != null) {
            intent.putExtra("sourceTitle", post.getSource().getTitle());
            intent.putExtra("sourceImage", post.getSource().getImage());
        }
    }
}
