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
import com.bitnews.bitnews.utils.TimeStampParser;
import com.bumptech.glide.Glide;

public class PostsRecyclerAdapter extends PaginationRecyclerAdapter<Post> {
    private int lastAnimatedItemPosition = -1;

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
    void bindItemViewHolder(RecyclerView.ViewHolder holder, Post post, int position) {
        PostViewHolder postViewHolder = (PostViewHolder) holder;

        if (post.getImage() != null)
            Glide.with(context)
                    .load(post.getImage())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(postViewHolder.postImage);
        postViewHolder.postTitle.setText(post.getTitle());

        if (post.getDescription() != null) {
            postViewHolder.postDescription.setText(post.getDescription());
            postViewHolder.postDescription.setVisibility(View.VISIBLE);
        } else {
            postViewHolder.postDescription.setVisibility(View.GONE);
        }

        if (post.getSource() != null && post.getSource().getImage() != null) {
            Glide.with(context)
                    .load(post.getSource().getImage())
                    .placeholder(R.drawable.ic_launcher_background)
                    .dontAnimate()
                    .into(postViewHolder.sourceImage);
            postViewHolder.sourceImage.setVisibility(View.VISIBLE);
        } else {
            postViewHolder.sourceImage.setVisibility(View.GONE);
        }

        if (post.getSource() != null && post.getSource().getTitle() != null) {
            postViewHolder.sourceTitle.setText(post.getSource().getTitle());
            postViewHolder.sourceTitle.setVisibility(View.VISIBLE);
        } else {
            postViewHolder.sourceTitle.setVisibility(View.GONE);
        }

        if (post.getTimestamp() != null) {
            postViewHolder.timestamp.setText(TimeStampParser.parseTimeStamp(context, post.getTimestamp()));
            postViewHolder.timestamp.setVisibility(View.VISIBLE);
        } else {
            postViewHolder.timestamp.setVisibility(View.GONE);
        }
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView postImage, sourceImage;
        TextView postTitle, postDescription, sourceTitle, timestamp;

        PostViewHolder(@NonNull View itemView) {
            super(itemView);

            postTitle = itemView.findViewById(R.id.postTitle);
            postDescription = itemView.findViewById(R.id.postDescription);
            postImage = itemView.findViewById(R.id.postImage);
            sourceImage = itemView.findViewById(R.id.sourceImage);
            sourceTitle = itemView.findViewById(R.id.sourceTitle);
            timestamp = itemView.findViewById(R.id.timestamp);
        }
    }
}
