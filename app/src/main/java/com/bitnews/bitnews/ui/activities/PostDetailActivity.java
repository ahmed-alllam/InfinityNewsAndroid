package com.bitnews.bitnews.ui.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bitnews.bitnews.R;
import com.bitnews.bitnews.data.models.Post;
import com.bitnews.bitnews.ui.viewmodels.PostViewModel;

public class PostDetailActivity extends AppCompatActivity {
    private String postSlug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        postSlug = getIntent().getStringExtra("postSlug");

        PostViewModel postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        postViewModel.getPost(getApplicationContext(), postSlug).observe(this, response -> {
            Post post = response.getitem();
        });
    }
}
