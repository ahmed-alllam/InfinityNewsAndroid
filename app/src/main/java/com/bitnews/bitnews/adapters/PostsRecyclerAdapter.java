package com.bitnews.bitnews.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bitnews.bitnews.R;
import com.bitnews.bitnews.data.models.Post;

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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_ITEM:
                PostViewHolder postViewHolder = (PostViewHolder) holder;
                postViewHolder.hi.setText(itemsList.get(position).getTitle());
                break;
            case VIEW_TYPE_FOOTER:
                bindFooterViewHolder(holder);
        }
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        TextView hi;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            hi = itemView.findViewById(R.id.hi);
        }
    }
}
