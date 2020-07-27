package com.bitnews.bitnews.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bitnews.bitnews.R;
import com.bitnews.bitnews.data.models.Post;
import com.bumptech.glide.Glide;

public class PostsRecyclerAdapter extends PaginationRecyclerAdapter<Post> {
    public PostsRecyclerAdapter(RecyclerView recyclerView, View.OnClickListener retryOnClickListener) {
        super(recyclerView, retryOnClickListener);
    }

    @Override
    protected RecyclerView.ViewHolder createItemViewHolder(ViewGroup parent) {
        return new PostViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item, parent, false));
    }

    @Override
    protected RecyclerView.ViewHolder createEmptyItemViewHolder(ViewGroup parent) {
        return new PostViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item, parent, false));
    }

    @Override
    void bindItemViewHolder(RecyclerView.ViewHolder holder, Post post) {
        PostViewHolder postViewHolder = (PostViewHolder) holder;

        if (post.getImage() != null && false)
            Glide.with(context)
                    .load(post.getImage())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(postViewHolder.postImage);
        postViewHolder.postTitle.setText(post.getTitle());
        postViewHolder.postDescription.setText(post.getDescription());
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView postImage;
        TextView postTitle, postDescription;

        PostViewHolder(@NonNull View itemView) {
            super(itemView);

            postTitle = itemView.findViewById(R.id.postTitle);
            postDescription = itemView.findViewById(R.id.postDescription);
            postImage = itemView.findViewById(R.id.postImage);
        }
    }
}
