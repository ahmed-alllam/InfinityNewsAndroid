package com.infinitynews.infinitynews.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.infinitynews.infinitynews.R;
import com.infinitynews.infinitynews.adapters.CommentsRecyclerAdapter;
import com.infinitynews.infinitynews.adapters.PostTagsAdapter;
import com.infinitynews.infinitynews.data.models.Comment;
import com.infinitynews.infinitynews.data.models.Post;
import com.infinitynews.infinitynews.data.models.Source;
import com.infinitynews.infinitynews.data.models.User;
import com.infinitynews.infinitynews.data.network.APIResponse;
import com.infinitynews.infinitynews.ui.viewmodels.CommentsViewModel;
import com.infinitynews.infinitynews.ui.viewmodels.PostViewModel;
import com.infinitynews.infinitynews.ui.viewmodels.UserViewModel;
import com.infinitynews.infinitynews.utils.TimeStampParser;

import java.util.Collections;
import java.util.List;


public class PostDetailActivity extends BaseActivity {
    private static final int BOTTOM_SHEET_EXPANDED_OFFSET = 100;
    private static final float BOTTOM_SHEET_COLLAPSED_PERCENT = 0.45f;
    private static final float POST_IMAGE_VIEW_HEIGHT_PERCENT = 0.5f;
    private String postSlug;
    private CommentsViewModel commentsViewModel;
    private NestedScrollView bottomSheet;
    private RecyclerView commentsRecyclerView;
    private WebView postBodyWebView;
    private CommentsRecyclerAdapter commentsRecyclerAdapter;
    private int totalCommentsCount;
    private int fetchedCommentsCount;
    private String lastCommentTimeStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        findViewById(R.id.backButton).setOnClickListener(v -> supportFinishAfterTransition());

        bindPostFromBundle(getIntent().getExtras());

        Button retryButton = findViewById(R.id.retryButton);

        PostViewModel postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        postViewModel.getDetailedPostLiveData().observe(this, response -> {
            switch (response.getStatus()) {
                case SUCCESFUL:
                    Post post = response.getitem();
                    bindPostFromResponse(post);
                    break;
                case NETWORK_FAILED:
                    retryButton.setVisibility(View.VISIBLE);
            }

            findViewById(R.id.progressBar4).setVisibility(View.GONE);
        });

        retryButton.setOnClickListener(v -> {
            retryButton.setVisibility(View.GONE);
            findViewById(R.id.progressBar4).setVisibility(View.VISIBLE);
            postViewModel.getDetailedPost(getApplicationContext(), postSlug);
        });

        commentsViewModel = new ViewModelProvider(this).get(CommentsViewModel.class);
        commentsViewModel.getCommentsLiveData().observe(this, response -> {
            commentsRecyclerView.suppressLayout(false);

            switch (response.getStatus()) {
                case SUCCESFUL:
                    onSuccseesfulCommentsResponse(response.getitem().getItems());
                    break;
                case NETWORK_FAILED:
                    onErrorCommentsResponse();
            }
        });

        postBodyWebView = findViewById(R.id.postBody);

        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        commentsRecyclerAdapter = new CommentsRecyclerAdapter(commentsRecyclerView,
                v -> loadComments());
        commentsRecyclerView.setAdapter(commentsRecyclerAdapter);

        bottomSheet = findViewById(R.id.postBottomSheet);
        BottomSheetBehavior.from(bottomSheet).setPeekHeight(getBootomSheetMinHeight());
        bottomSheet.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (!bottomSheet.canScrollVertically(1)) {
                if (fetchedCommentsCount < totalCommentsCount || totalCommentsCount == -1) {
                    if (!commentsRecyclerAdapter.isLoading() && !commentsRecyclerAdapter.isLoadingFailedAdded()) {
                        loadComments();
                    }
                }
            }
        });

        CoordinatorLayout.LayoutParams bottomSheetLayoutParams = (CoordinatorLayout.LayoutParams) bottomSheet.getLayoutParams();
        bottomSheetLayoutParams.height = getBootomSheetMaxHeight();
        bottomSheet.setLayoutParams(bottomSheetLayoutParams);
        bottomSheet.setMinimumHeight(getBootomSheetMinHeight());

        ImageView postImageView = findViewById(R.id.postImage);
        ViewGroup.LayoutParams postImageLayoutParams = postImageView.getLayoutParams();
        postImageLayoutParams.height = getPostImageHeight();
        postImageView.setLayoutParams(postImageLayoutParams);

        postViewModel.getDetailedPost(getApplicationContext(), postSlug);
    }

    private void bindPostFromBundle(Bundle postBundle) {
        Post post = new Post(postBundle.getString("postFullImage"),
                postBundle.getString("postTitle"),
                postBundle.getString("postDescription"),
                postBundle.getString("postTimestamp"));

        postSlug = postBundle.getString("postSlug");

        ImageView postImage = findViewById(R.id.postImage);
        Glide.with(this)
                .load(post.getFullImage())
                .placeholder(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(postImage);

        TextView postTitle = findViewById(R.id.postTitle);
        postTitle.setText(post.getTitle());

        Source postSource = new Source(postBundle.getString("sourceTitle"),
                postBundle.getString("sourceImage"));

        if (postSource.getImage() != null && !postSource.getImage().isEmpty()) {
            ImageView sourceImage = findViewById(R.id.sourceImage);
            Glide.with(this)
                    .load(postSource.getImage())
                    .placeholder(R.drawable.placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(sourceImage);
        }

        if (postSource.getTitle() != null && !postSource.getTitle().isEmpty()) {
            TextView sourceTitle = findViewById(R.id.sourceTitle);
            sourceTitle.setText(postSource.getTitle());
        }

        RelativeTimeTextView timestamp = findViewById(R.id.timestamp);
        timestamp.setReferenceTime(TimeStampParser.getDateFromString(post.getTimestamp()).getTime());

        if (post.getDescription() != null && !post.getDescription().isEmpty()) {
            TextView postDescription = findViewById(R.id.postDescription);
            postDescription.setVisibility(View.VISIBLE);
            postDescription.setText(post.getDescription());
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void bindPostFromResponse(Post post) {
        if (post.getBody() != null && !post.getBody().isEmpty()) {
            postBodyWebView.getSettings().setJavaScriptEnabled(true);
            postBodyWebView.loadDataWithBaseURL(post.getSource().getWebsite(), post.getBody(),
                    "text/html; charset=utf-8", "utf-8", null);
        }

        if (post.getTags() != null && !post.getTags().isEmpty()) {
            findViewById(R.id.tagsLabel).setVisibility(View.VISIBLE);
            RecyclerView tagsRecyclerView = findViewById(R.id.tagsRecyclerView);
            tagsRecyclerView.setLayoutManager(new FlexboxLayoutManager(this));
            tagsRecyclerView.setVisibility(View.VISIBLE);
            tagsRecyclerView.setAdapter(new PostTagsAdapter(post.getTags()));
        }

        Button viewOnSourceButton = findViewById(R.id.viewOnSourceButton);
        viewOnSourceButton.setVisibility(View.VISIBLE);
        viewOnSourceButton.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(post.getDetailUrl()));
            startActivity(i);
        });

        findViewById(R.id.commentsLabel).setVisibility(View.VISIBLE);

        TextView commentsCount = findViewById(R.id.commentsCount);
        commentsCount.setVisibility(View.VISIBLE);
        commentsCount.setText(String.valueOf(post.getCommentsCount()));

        loadCurrentUser();

        if (post.getCommentsCount() > 0) {
            commentsRecyclerAdapter.setItemsPerScreenCount(Math.min(post.getCommentsCount(), 5));
            totalCommentsCount = post.getCommentsCount();
            loadComments();
        }
    }

    private void bindCurrentUserToPost(User user) {
        ImageView userImage = findViewById(R.id.userImage);
        userImage.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(user.getProfilePhoto())
                .placeholder(R.drawable.person_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(userImage);

        EditText commentEditText = findViewById(R.id.commentEditText);
        commentEditText.setVisibility(View.VISIBLE);

        ImageButton sendCommentButton = findViewById(R.id.sendCommentButton);
        sendCommentButton.setVisibility(View.VISIBLE);
        sendCommentButton.setOnClickListener(v -> {
            String text = commentEditText.getText().toString();
            if (!text.isEmpty() && !commentsRecyclerAdapter.isLoading()) {
                sendComment(text);
                sendCommentButton.setVisibility(View.INVISIBLE);
                findViewById(R.id.sendingCommentProgressBar).setVisibility(View.VISIBLE);
            }
        });
    }

    private void onSuccseesfulCommentsResponse(List<Comment> comments) {
        commentsRecyclerAdapter.removeFooterItem();

        if (!comments.isEmpty()) {
            fetchedCommentsCount += comments.size();
            lastCommentTimeStamp = comments.get(comments.size() - 1).getTimestamp();

            commentsRecyclerAdapter.addAll(-1, comments);
        } else if (commentsRecyclerAdapter.isEmpty())
            commentsRecyclerView.setVisibility(View.GONE);

        commentsRecyclerAdapter.finishLoading();
    }

    private void onErrorCommentsResponse() {
        if (commentsRecyclerAdapter.isEmpty()) {
            commentsRecyclerAdapter.setLoadingInitially(false);
            commentsRecyclerAdapter.notifyDataSetChanged();
        } else
            commentsRecyclerAdapter.setLoadingMore(false);

        commentsRecyclerAdapter.setLoadingFailed(true);
        commentsRecyclerAdapter.addFooterItem();
    }

    private void sendComment(String text) {
        ProgressBar sendingCommentProgressBar = findViewById(R.id.sendingCommentProgressBar);
        sendingCommentProgressBar.setVisibility(View.VISIBLE);

        commentsViewModel.sendCommentForPost(getApplicationContext(), postSlug, text)
                .observe(this, response -> {
                    findViewById(R.id.sendCommentButton).setVisibility(View.VISIBLE);
                    sendingCommentProgressBar.setVisibility(View.GONE);

                    if (response.getStatus() == APIResponse.Status.SUCCESFUL) {
                        EditText commentEditText = findViewById(R.id.commentEditText);
                        commentEditText.getText().clear();

                        TextView commentsCount = findViewById(R.id.commentsCount);
                        int prevCount = Integer.parseInt(commentsCount.getText().toString());
                        commentsCount.setText(String.valueOf(prevCount + 1));

                        commentsRecyclerView.setVisibility(View.VISIBLE);
                        commentsRecyclerAdapter.addAll(0, Collections.singletonList(response.getitem()));
                    }
                });
    }

    private void loadCurrentUser() {
        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getCurrentUser(getApplicationContext());
        userViewModel.getUser().observe(this, response -> {
            if (response.getStatus() == APIResponse.Status.SUCCESFUL) {
                User user = response.getitem();
                bindCurrentUserToPost(user);
            }
        });
    }

    private void loadComments() {
        commentsRecyclerView.setVisibility(View.VISIBLE);

        if (commentsRecyclerAdapter.isEmpty()) {
            commentsRecyclerAdapter.setLoadingInitially(true);
            commentsRecyclerAdapter.notifyDataSetChanged();
            commentsRecyclerView.suppressLayout(true);
        } else {
            commentsRecyclerAdapter.setLoadingFailed(false);
            commentsRecyclerAdapter.setLoadingMore(true);
            commentsRecyclerAdapter.addFooterItem();
        }

        commentsViewModel.getComments(getApplicationContext(), postSlug, lastCommentTimeStamp);
    }

    private int getPostImageHeight() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int) (displayMetrics.heightPixels * POST_IMAGE_VIEW_HEIGHT_PERCENT);
    }

    private int getBootomSheetMinHeight() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int) (displayMetrics.heightPixels - (BOTTOM_SHEET_COLLAPSED_PERCENT * displayMetrics.heightPixels));
    }

    private int getBootomSheetMaxHeight() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int) (displayMetrics.heightPixels - (BOTTOM_SHEET_EXPANDED_OFFSET * displayMetrics.density));
    }

    @Override
    public AssetManager getAssets() {
        return getResources().getAssets();
    }

    @Override
    protected void onResume() {
        super.onResume();
        postBodyWebView.onResume();
    }

    @Override
    protected void onPause() {
        postBodyWebView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        postBodyWebView.destroy();
        super.onDestroy();
    }
}
