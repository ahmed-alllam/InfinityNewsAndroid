package com.infinitynews.infinitynews.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.infinitynews.infinitynews.R;

import java.util.List;

public class PostTagsAdapter extends RecyclerView.Adapter<PostTagsAdapter.PostTagViewHolder> {
    List<String> postTags;

    public PostTagsAdapter(List<String> postTags) {
        this.postTags = postTags;
    }

    @NonNull
    @Override
    public PostTagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostTagViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_tag_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostTagViewHolder holder, int position) {
        holder.postTag.setText(postTags.get(position));
    }

    @Override
    public int getItemCount() {
        return postTags.size();
    }

    static class PostTagViewHolder extends RecyclerView.ViewHolder {
        TextView postTag;

        public PostTagViewHolder(@NonNull View itemView) {
            super(itemView);

            postTag = itemView.findViewById(R.id.postTag);
        }
    }
}
