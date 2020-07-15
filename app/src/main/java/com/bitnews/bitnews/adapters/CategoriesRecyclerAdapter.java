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

public class CategoriesRecyclerAdapter extends PaginationRecyclerAdapter<Category> {

    public CategoriesRecyclerAdapter(RecyclerView recyclerView) {
        super(recyclerView);
    }

    @Override
    protected int calculateEmptyItemsCount() {
        return 0;
    }

    @Override
    protected RecyclerView.ViewHolder createItemViewHolder(ViewGroup parent) {
        return new CategoryViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_item, parent, false), false);
    }

    @Override
    protected RecyclerView.ViewHolder createEmptyItemViewHolder(ViewGroup parent) {
        CategoryViewHolder categoryViewHolder = new CategoryViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false), true);
        categoryViewHolder.title.setText("Loading...");
        return categoryViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    private class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        private ImageView image;
        private boolean isEmptyItem;

        public CategoryViewHolder(@NonNull View itemView, boolean isEmptyItem) {
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
