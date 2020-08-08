package com.bitnews.bitnews.ui.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.lifecycle.ViewModelProvider;

import com.bitnews.bitnews.R;
import com.bitnews.bitnews.data.models.Post;
import com.bitnews.bitnews.ui.viewmodels.PostViewModel;
import com.bitnews.bitnews.ui.views.PopupScrollView;

public class PostDetailActivity extends AppCompatActivity {
    private String postSlug;
    public static final float MIN_GUIDLINE_PERCENT = 0.2f;
    public static final float MAX_GUIDLINE_PERCENT = 0.35f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        postSlug = getIntent().getStringExtra("postSlug");

        CardView postCardView = findViewById(R.id.postCardView);
        ConstraintLayout postContainerLayout = findViewById(R.id.postContainerLayout);
        Guideline scrollViewGuideline = findViewById(R.id.scrollViewGuideline);

        PopupScrollView popupScrollView = new PopupScrollView(this) {
            @Override
            protected boolean isScrollable(boolean isScrollingUp) {
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)
                        scrollViewGuideline.getLayoutParams();
                float currentPercent = layoutParams.guidePercent;

                if (isScrollingUp) {
                    return currentPercent <= MIN_GUIDLINE_PERCENT;
                }

                return currentPercent >= MAX_GUIDLINE_PERCENT ||
                        (currentPercent <= MIN_GUIDLINE_PERCENT && computeVerticalScrollOffset() != 0);
            }

            @Override
            protected void moveView(float yOffsetPercent) {
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)
                        scrollViewGuideline.getLayoutParams();
                layoutParams.guidePercent = calculateBoundedPercent(layoutParams.guidePercent - yOffsetPercent);
                scrollViewGuideline.setLayoutParams(layoutParams);
            }
        };

        postCardView.removeView(postContainerLayout);
        postCardView.addView(popupScrollView);
        popupScrollView.addView(postContainerLayout);

        PostViewModel postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        postViewModel.getPost(getApplicationContext(), postSlug).observe(this, response -> {
            Post post = response.getitem();
        });
    }

    private float calculateBoundedPercent(float percent) {
        if (percent < MIN_GUIDLINE_PERCENT)
            return MIN_GUIDLINE_PERCENT;

        return Math.min(percent, MAX_GUIDLINE_PERCENT);
    }
}
