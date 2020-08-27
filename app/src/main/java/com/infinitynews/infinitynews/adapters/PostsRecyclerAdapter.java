package com.infinitynews.infinitynews.adapters;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.infinitynews.infinitynews.R;
import com.infinitynews.infinitynews.callbacks.OnPostItemClickListener;
import com.infinitynews.infinitynews.data.models.Post;
import com.infinitynews.infinitynews.data.models.Source;
import com.infinitynews.infinitynews.utils.TimeStampParser;

public class PostsRecyclerAdapter extends PaginationRecyclerAdapter<Post> {
    private int lastAnimatedItemPosition = -1;
    private Interpolator interpolator = new DecelerateInterpolator();
    private OnPostItemClickListener onPostItemClickListener;

    public PostsRecyclerAdapter(RecyclerView recyclerView, View.OnClickListener retryOnClickListener,
                                OnPostItemClickListener onPostItemClickListener) {
        super(recyclerView, retryOnClickListener);
        ITEM_VIEW_HEIGHT = 150;
        this.onPostItemClickListener = onPostItemClickListener;
    }

    @Override
    protected RecyclerView.ViewHolder createItemViewHolder(ViewGroup parent) {
        return new PostViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item, parent, false), onPostItemClickListener);
    }

    @Override
    protected RecyclerView.ViewHolder createEmptyItemViewHolder(ViewGroup parent) {
        return new EmptyPostViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_empty_item, parent, false));
    }

    @Override
    void bindItemViewHolder(RecyclerView.ViewHolder holder, Post post, int position) {
        PostViewHolder postViewHolder = (PostViewHolder) holder;

        View itemView = postViewHolder.itemView;
        int adapterPosition = holder.getAdapterPosition();

        startAnimation(itemView, adapterPosition);

        postViewHolder.post = post;

        Glide.with(context)
                .load(post.getThumbnail())
                .placeholder(R.drawable.ic_launcher_background)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(postViewHolder.postImage);

        postViewHolder.postTitle.setText(post.getTitle());

        if (post.getDescription() != null && !post.getDescription().isEmpty()) {
            postViewHolder.postDescription.setText(post.getDescription());
            postViewHolder.postDescription.setVisibility(View.VISIBLE);
        } else {
            postViewHolder.postDescription.setVisibility(View.GONE);
        }

        Source postSource = post.getSource();

        if (postSource != null) {
            Glide.with(context)
                    .load(postSource.getImage())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(postViewHolder.sourceImage);
            postViewHolder.sourceImage.setVisibility(View.VISIBLE);
        } else {
            postViewHolder.sourceImage.setVisibility(View.GONE);
        }

        if (postSource != null && postSource.getTitle() != null && !postSource.getTitle().isEmpty()) {
            postViewHolder.sourceTitle.setText(postSource.getTitle());
            postViewHolder.sourceTitle.setVisibility(View.VISIBLE);
        } else {
            postViewHolder.sourceTitle.setVisibility(View.GONE);
        }

        if (post.getTimestamp() != null) {
            postViewHolder.timestamp.setText(TimeStampParser.parseTimeStamp(post.getTimestamp()));
            postViewHolder.timestamp.setVisibility(View.VISIBLE);
        } else {
            postViewHolder.timestamp.setVisibility(View.GONE);
        }
    }

    private void startAnimation(View view, int position) {
        if (position <= lastAnimatedItemPosition) {
            view.setTranslationX(0);
            view.setAlpha(1);
        } else {
            Animator translateAnimator = ObjectAnimator.ofFloat(view, "translationX",
                    view.getRootView().getWidth(), 0).setDuration(200);

            translateAnimator.start();
            translateAnimator.setInterpolator(interpolator);

            Animator alphaAnimator = ObjectAnimator.ofFloat(view, "alpha",
                    0, 1f).setDuration(300);
            alphaAnimator.start();
            alphaAnimator.setInterpolator(interpolator);

            lastAnimatedItemPosition = position;
        }
    }

    private static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView postImage, sourceImage;
        TextView postTitle, postDescription, sourceTitle, timestamp;
        Post post;

        PostViewHolder(@NonNull View itemView, OnPostItemClickListener onPostItemClickListener) {
            super(itemView);

            postTitle = itemView.findViewById(R.id.postTitle);
            postDescription = itemView.findViewById(R.id.postDescription);
            postImage = itemView.findViewById(R.id.postImage);
            sourceImage = itemView.findViewById(R.id.sourceImage);
            sourceTitle = itemView.findViewById(R.id.sourceTitle);
            timestamp = itemView.findViewById(R.id.timestamp);

            itemView.setOnClickListener(v -> onPostItemClickListener.onPostClicked(post));
        }
    }

    private static class EmptyPostViewHolder extends RecyclerView.ViewHolder {
        public EmptyPostViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}