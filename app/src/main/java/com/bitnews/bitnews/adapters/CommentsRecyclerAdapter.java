package com.bitnews.bitnews.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bitnews.bitnews.R;
import com.bitnews.bitnews.data.models.Comment;
import com.bitnews.bitnews.data.models.User;
import com.bitnews.bitnews.utils.TimeStampParser;
import com.bumptech.glide.Glide;

public class CommentsRecyclerAdapter extends PaginationRecyclerAdapter<Comment> {
    public CommentsRecyclerAdapter(RecyclerView recyclerView, View.OnClickListener onFooterClickListener) {
        super(recyclerView, onFooterClickListener);
        itemsPerScreenCount = 5;
    }

    @Override
    void bindItemViewHolder(RecyclerView.ViewHolder holder, Comment comment, int position) {
        CommentViewHolder commentViewHolder = (CommentViewHolder) holder;

        User commentsUser = comment.getUser();
        String usersName = String.format("%s %s", commentsUser.getFirstName(),
                commentsUser.getLastName());

        Glide.with(context)
                .load(commentsUser.getProfilePhoto())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
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
}
