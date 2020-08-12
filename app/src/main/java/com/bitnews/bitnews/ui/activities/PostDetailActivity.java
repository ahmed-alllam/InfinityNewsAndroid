package com.bitnews.bitnews.ui.activities;

import android.os.Bundle;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;

import com.bitnews.bitnews.R;
import com.bitnews.bitnews.data.models.Post;
import com.bitnews.bitnews.data.models.Source;
import com.bitnews.bitnews.data.network.APIResponse;
import com.bitnews.bitnews.ui.viewmodels.PostViewModel;
import com.bitnews.bitnews.ui.views.BottomSheetScrollView;
import com.bitnews.bitnews.utils.TimeStampParser;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;


public class PostDetailActivity extends AppCompatActivity {
    private String postSlug;
    public static final int BOTTOM_SHEET_EXPANDED_OFFSET = 100;
    public static final int BOTTOM_SHEET_COLLAPSED_OFFSET = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        findViewById(R.id.backButton).setOnClickListener(v -> supportFinishAfterTransition());

        bindPostFromBundle(getIntent().getExtras());

        PostViewModel postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        postViewModel.getPost(getApplicationContext(), postSlug).observe(this, response -> {
            findViewById(R.id.progressBar4).setVisibility(View.GONE);

            if (response.getStatus() == APIResponse.Status.SUCCESFUL) {
                Post post = response.getitem();
                bindPostFromResponse(post);
            }
        });

        BottomSheetScrollView bottomSheet = findViewById(R.id.postBottomSheet);
        BottomSheetBehavior<BottomSheetScrollView> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomSheet.getLayoutParams();
        layoutParams.height = getBootomSheetMaxHeight();
        bottomSheet.setLayoutParams(layoutParams);

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
            postDescription.setText(post.getDescription());
        }
    }

    private void bindPostFromResponse(Post post) {
        if (post.getBody() != null && !post.getBody().isEmpty()) {
            HtmlTextView postBody = findViewById(R.id.postBody);
            HtmlHttpImageGetter imageGetter = new HtmlHttpImageGetter(postBody);
            imageGetter.enableCompressImage(true);
            postBody.setHtml(post.getBody(), imageGetter);
            Linkify.addLinks(postBody, Linkify.WEB_URLS);
        }
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
