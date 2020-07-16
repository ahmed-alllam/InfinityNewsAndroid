package com.bitnews.bitnews.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bitnews.bitnews.R;
import com.bitnews.bitnews.data.models.Category;
import com.bumptech.glide.Glide;

public class CategoriesRecyclerAdapter extends PaginationRecyclerAdapter<Category> {
    public CategoriesRecyclerAdapter(RecyclerView recyclerView) {
        super(recyclerView);
        ITEM_VIEW_HEIGHT = 100;
    }

    @Override
    protected RecyclerView.ViewHolder createItemViewHolder(ViewGroup parent) {
        return new CategoryViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_item, parent, false), false);
    }

    @Override
    protected RecyclerView.ViewHolder createEmptyItemViewHolder(ViewGroup parent) {
        return new CategoryViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_item, parent, false), true);
    }

    @Override
    int calculateEmptyItemsCount() {
        return super.calculateEmptyItemsCount() * 3;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;
            Category categoryItem = itemsList.get(position);

            Glide.with(context)
                    .load(categoryItem.getImage())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(categoryViewHolder.image);
            categoryViewHolder.title.setText(categoryItem.getTitle());
        }
    }

    private class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        private ImageView image;
        private boolean isEmptyItem;

        CategoryViewHolder(@NonNull View itemView, boolean isEmptyItem) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
            this.isEmptyItem = isEmptyItem;
        }

        @Override
        public void onClick(View v) {
            if (!isEmptyItem) {

            }
        }
    }
}
