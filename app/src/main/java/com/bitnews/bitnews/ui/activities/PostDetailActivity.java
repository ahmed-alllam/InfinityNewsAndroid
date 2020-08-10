package com.bitnews.bitnews.ui.activities;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.lifecycle.ViewModelProvider;

import com.bitnews.bitnews.R;
import com.bitnews.bitnews.data.models.Post;
import com.bitnews.bitnews.data.models.Source;
import com.bitnews.bitnews.data.network.APIResponse;
import com.bitnews.bitnews.ui.viewmodels.PostViewModel;
import com.bitnews.bitnews.ui.views.PopupScrollView;
import com.bitnews.bitnews.utils.TimeStampParser;
import com.bumptech.glide.Glide;

public class PostDetailActivity extends AppCompatActivity {
    private String postSlug;
    public static final float MIN_GUIDLINE_PERCENT = 0.2f;
    public static final float MAX_GUIDLINE_PERCENT = 0.35f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        findViewById(R.id.backButton).setOnClickListener(v -> supportFinishAfterTransition());

        bindPostFromBundle(getIntent().getExtras());

        CardView postCardView = findViewById(R.id.postCardView);
        ConstraintLayout postContainerLayout = findViewById(R.id.postContainerLayout);
        Guideline scrollViewGuideline = findViewById(R.id.scrollViewGuideline);
        PopupScrollView popupScrollView = getScrollView(scrollViewGuideline);

        postCardView.removeView(postContainerLayout);
        postCardView.addView(popupScrollView);
        popupScrollView.addView(postContainerLayout);

        PostViewModel postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        postViewModel.getPost(getApplicationContext(), postSlug).observe(this, response -> {
            findViewById(R.id.progressBar4).setVisibility(View.GONE);

            if (response.getStatus() == APIResponse.Status.SUCCESFUL) {
                Post post = response.getitem();
                bindPostFromResponse(post);
            }
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
            TextView postBody = findViewById(R.id.postBody);
            postBody.setText(Html.fromHtml(post.getBody()));
        }
    }

    private PopupScrollView getScrollView(Guideline guideline) {
        return new PopupScrollView(this) {
            @Override
            protected boolean isScrollable(boolean isScrollingUp) {
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)
                        guideline.getLayoutParams();
                float currentPercent = layoutParams.guidePercent;

                if (isScrollingUp) {
                    return currentPercent <= MIN_GUIDLINE_PERCENT;
                }

                return currentPercent >= MAX_GUIDLINE_PERCENT ||
                        (currentPercent <= MIN_GUIDLINE_PERCENT && getScrollY() != 0);
            }

            @Override
            protected void moveView(float yOffsetPercent) {
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)
                        guideline.getLayoutParams();
                layoutParams.guidePercent = calculateBoundedPercent(layoutParams.guidePercent - yOffsetPercent);
                guideline.setLayoutParams(layoutParams);
            }
        };
    }

    private float calculateBoundedPercent(float percent) {
        if (percent < MIN_GUIDLINE_PERCENT)
            return MIN_GUIDLINE_PERCENT;

        return Math.min(percent, MAX_GUIDLINE_PERCENT);
    }
}
