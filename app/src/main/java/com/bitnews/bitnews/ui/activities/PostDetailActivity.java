package com.bitnews.bitnews.ui.activities;

import android.os.Bundle;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bitnews.bitnews.R;
import com.bitnews.bitnews.adapters.CommentsRecyclerAdapter;
import com.bitnews.bitnews.adapters.PostTagsAdapter;
import com.bitnews.bitnews.data.models.Comment;
import com.bitnews.bitnews.data.models.Post;
import com.bitnews.bitnews.data.models.Source;
import com.bitnews.bitnews.data.models.User;
import com.bitnews.bitnews.data.network.APIResponse;
import com.bitnews.bitnews.ui.viewmodels.CommentsViewModel;
import com.bitnews.bitnews.ui.viewmodels.PostViewModel;
import com.bitnews.bitnews.ui.viewmodels.UserViewModel;
import com.bitnews.bitnews.ui.views.BottomSheetScrollView;
import com.bitnews.bitnews.utils.TimeStampParser;
import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.Collections;
import java.util.List;


public class PostDetailActivity extends AppCompatActivity {
    private static final int BOTTOM_SHEET_EXPANDED_OFFSET = 150;
    private static final int BOTTOM_SHEET_COLLAPSED_OFFSET = 300;
    private String postSlug;
    private BottomSheetBehavior<BottomSheetScrollView> bottomSheetBehavior;
    private CommentsViewModel commentsViewModel;
    private RecyclerView commentsRecyclerView;
    private CommentsRecyclerAdapter commentsRecyclerAdapter;
    private TextView commentsErrorLabel;
    private int totalCommentsCount;
    private int fetchedCommentsCount;
    private String lastCommentTimeStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        findViewById(R.id.backButton).setOnClickListener(v -> supportFinishAfterTransition());

        bindPostFromBundle(getIntent().getExtras());

        PostViewModel postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        postViewModel.getPost(getApplicationContext(), postSlug).observe(this, response -> {
            if (response.getStatus() == APIResponse.Status.SUCCESFUL) {
                Post post = response.getitem();
                bindPostFromResponse(post);
            }

            findViewById(R.id.progressBar4).setVisibility(View.GONE);
        });

        commentsViewModel = new ViewModelProvider(this).get(CommentsViewModel.class);
        commentsViewModel.getCommentsLiveData().observe(this, response -> {
            if (response.getStatus() == APIResponse.Status.SUCCESFUL) {
                commentsRecyclerView.suppressLayout(false);
                onSuccseesfulCommentsResponse(response.getitem().getItems(), response.getitem().getCount());
            }
        });

        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        commentsRecyclerAdapter = new CommentsRecyclerAdapter(commentsRecyclerView,
                v -> loadComments());
        commentsRecyclerView.setAdapter(commentsRecyclerAdapter);

        commentsErrorLabel = findViewById(R.id.commentsErrorLabel);

        BottomSheetScrollView bottomSheet = findViewById(R.id.postBottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomSheet.getLayoutParams();
        layoutParams.height = getBootomSheetMaxHeight();
        bottomSheet.setLayoutParams(layoutParams);

        bottomSheet.setMinimumHeight(getBootomSheetMinHeight());
        bottomSheetBehavior.setPeekHeight(getBootomSheetMinHeight());
        bottomSheet.setBottomSheetBehavior(bottomSheetBehavior);
        bottomSheet.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (bottomSheet.getScrollY() == 0) {
                bottomSheetBehavior.setDraggable(true);
            } else
                bottomSheetBehavior.setDraggable(false);
        });
    }

    private void bindPostFromBundle(Bundle postBundle) {
        Post post = new Post(postBundle.getString("postImage"),
                postBundle.getString("postTitle"),
                postBundle.getString("postDescription"),
                postBundle.getString("postTimestamp"));

        postSlug = postBundle.getString("postSlug");

        if (post.getImage() != null && !post.getImage().isEmpty()) {
            ImageView postImage = findViewById(R.id.postImage);
            Glide.with(this)
                    .load(post.getImage())
                    .into(postImage);
        }

        TextView postTitle = findViewById(R.id.postTitle);
        postTitle.setText(post.getTitle());

        Source postSource = new Source(postBundle.getString("sourceTitle"),
                postBundle.getString("sourceImage"));

        if (postSource.getImage() != null && !postSource.getImage().isEmpty()) {
            ImageView sourceImage = findViewById(R.id.sourceImage);
            Glide.with(this)
                    .load(postSource.getImage())
                    .into(sourceImage);
        }

        if (postSource.getTitle() != null && !postSource.getTitle().isEmpty()) {
            TextView sourceTitle = findViewById(R.id.sourceTitle);
            sourceTitle.setText(postSource.getTitle());
        }

        TextView timestamp = findViewById(R.id.timestamp);
        timestamp.setText(TimeStampParser.parseTimeStamp(post.getTimestamp()));

        if (post.getDescription() != null && !post.getDescription().isEmpty()) {
            TextView postDescription = findViewById(R.id.postDescription);
            postDescription.setVisibility(View.VISIBLE);
            postDescription.setText(post.getDescription());
        }
    }

    private void bindPostFromResponse(Post post) {
        //todo check if blocks
        if (post.getBody() != null && !post.getBody().isEmpty()) {
            HtmlTextView postBody = findViewById(R.id.postBody);
            HtmlHttpImageGetter imageGetter = new HtmlHttpImageGetter(postBody);
            imageGetter.enableCompressImage(true);
            postBody.setHtml(post.getBody(), imageGetter);
            Linkify.addLinks(postBody, Linkify.ALL);
        }

        if (post.getTags() != null && !post.getTags().isEmpty()) {
            findViewById(R.id.tagsLabel).setVisibility(View.VISIBLE);
            RecyclerView tagsRecyclerView = findViewById(R.id.tagsRecyclerView);
            tagsRecyclerView.setLayoutManager(new FlexboxLayoutManager(this));
            tagsRecyclerView.setHasFixedSize(true);
            tagsRecyclerView.setVisibility(View.VISIBLE);
            tagsRecyclerView.setAdapter(new PostTagsAdapter(post.getTags()));
        }

        findViewById(R.id.commentsLabel).setVisibility(View.VISIBLE);

        TextView commentsCount = findViewById(R.id.commentsCount);
        commentsCount.setVisibility(View.VISIBLE);
        commentsCount.setText(String.valueOf(post.getCommentsCount()));

        loadCurrentUser();

        if (post.getCommentsCount() > 0) {
            loadComments();
        }
    }

    private void bindCurrentUserToPost(User user) {
        ImageView userImage = findViewById(R.id.userImage);
        userImage.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(user.getProfilePhoto())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(userImage);

        EditText commentEditText = findViewById(R.id.commentEditText);
        commentEditText.setVisibility(View.VISIBLE);

        ImageButton sendCommentButton = findViewById(R.id.sendCommentButton);
        sendCommentButton.setVisibility(View.VISIBLE);
        sendCommentButton.setOnClickListener(v -> {
            String text = commentEditText.getText().toString();
            if (!text.isEmpty())
                sendComment(text);

            sendCommentButton.setVisibility(View.GONE);
            findViewById(R.id.sendingCommentProgressBar).setVisibility(View.VISIBLE);
        });
    }

    private void onSuccseesfulCommentsResponse(List<Comment> comments, int count) {
        if (count > 0)
            totalCommentsCount = count;

        if (!comments.isEmpty()) {
            fetchedCommentsCount += comments.size();
            lastCommentTimeStamp = comments.get(comments.size() - 1).getTimestamp();
            commentsRecyclerAdapter.addAll(comments);
        } else {
            if (commentsRecyclerAdapter.isEmpty()) {
                commentsRecyclerAdapter.setLoadingInitially(false);
                commentsRecyclerView.setVisibility(View.INVISIBLE);
                commentsErrorLabel.setVisibility(View.VISIBLE);
                commentsErrorLabel.setText(R.string.no_feed);
            } else {
                commentsRecyclerAdapter.setLoadingMore(false);
            }
        }
    }

    private void sendComment(String text) {
        ImageButton sendCommentButton = findViewById(R.id.sendCommentButton);
        sendCommentButton.setVisibility(View.GONE);

        ProgressBar sendingCommentProgressBar = findViewById(R.id.sendingCommentProgressBar);
        sendingCommentProgressBar.setVisibility(View.VISIBLE);

        commentsViewModel.sendCommentForPost(getApplicationContext(), postSlug, text)
                .observe(this, response -> {
                    sendCommentButton.setVisibility(View.VISIBLE);
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
                if (!user.isGuest())
                    bindCurrentUserToPost(response.getitem());
            }
        });
    }

    private void loadComments() {
        commentsRecyclerView.setVisibility(View.VISIBLE);
        commentsErrorLabel.setVisibility(View.GONE);

        if (commentsRecyclerAdapter.isEmpty()) {
            commentsRecyclerAdapter.setLoadingInitially(true);
            commentsRecyclerView.suppressLayout(true);
        } else {
            commentsRecyclerAdapter.setLoadingMore(true);
        }

        commentsViewModel.getComments(getApplicationContext(), postSlug, lastCommentTimeStamp);
    }

    private int getBootomSheetMinHeight() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int) (displayMetrics.heightPixels - (BOTTOM_SHEET_COLLAPSED_OFFSET * displayMetrics.density));
    }

    private int getBootomSheetMaxHeight() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int) (displayMetrics.heightPixels - (BOTTOM_SHEET_EXPANDED_OFFSET * displayMetrics.density));
    }
}
