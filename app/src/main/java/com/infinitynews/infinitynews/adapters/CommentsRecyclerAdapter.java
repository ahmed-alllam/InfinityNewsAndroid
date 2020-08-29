package com.infinitynews.infinitynews.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.infinitynews.infinitynews.R;
import com.infinitynews.infinitynews.data.models.Comment;
import com.infinitynews.infinitynews.data.models.User;
import com.infinitynews.infinitynews.utils.TimeStampParser;

public class CommentsRecyclerAdapter extends PaginationRecyclerAdapter<Comment> {
    public CommentsRecyclerAdapter(RecyclerView recyclerView, View.OnClickListener onFooterClickListener) {
        super(recyclerView, onFooterClickListener);
    }

    @Override
    void bindItemViewHolder(RecyclerView.ViewHolder holder, Comment comment, int position) {
        CommentViewHolder commentViewHolder = (CommentViewHolder) holder;

        User commentsUser = comment.getUser();
        String usersName = String.format("%s %s", commentsUser.getFirstName(),
                commentsUser.getLastName());

        Glide.with(context)
                .load(commentsUser.getProfilePhoto())
                .placeholder(R.drawable.person_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(commentViewHolder.commentUserImage);

        commentViewHolder.commentUsername.setText(usersName);
        commentViewHolder.commentText.setText(comment.getText());
        commentViewHolder.commentTimestamp.setText(TimeStampParser.parseTimeStamp(comment.getTimestamp()));
    }

    @Override
    protected RecyclerView.ViewHolder createItemViewHolder(ViewGroup parent) {
        return new CommentViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_comment_item, parent, false));
    }

    @Override
    protected RecyclerView.ViewHolder createEmptyItemViewHolder(ViewGroup parent) {
        return new EmptyCommentViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.empty_comment_item, parent, false));
    }

    private static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView commentUserImage;
        TextView commentUsername, commentTimestamp, commentText;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            commentUserImage = itemView.findViewById(R.id.commentUserImage);
            commentUsername = itemView.findViewById(R.id.commentUsername);
            commentTimestamp = itemView.findViewById(R.id.commentTimestamp);
            commentText = itemView.findViewById(R.id.commentText);
        }
    }

    private static class EmptyCommentViewHolder extends RecyclerView.ViewHolder {
        public EmptyCommentViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void setItemsPerScreenCount(int count) {
        itemsPerScreenCount = count;
    }
}
