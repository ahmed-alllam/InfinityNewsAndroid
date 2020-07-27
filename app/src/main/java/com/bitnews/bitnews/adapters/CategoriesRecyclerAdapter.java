package com.bitnews.bitnews.adapters;

import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bitnews.bitnews.R;
import com.bitnews.bitnews.callbacks.CategoryItemChooseListener;
import com.bitnews.bitnews.data.models.Category;
import com.bumptech.glide.Glide;

public class CategoriesRecyclerAdapter extends PaginationRecyclerAdapter<Category> {
    private static final int ITEMS_PER_ROW = 3;

    private CategoryItemChooseListener categoryItemChooseListener;
    private int selectedColor;

    public CategoriesRecyclerAdapter(RecyclerView recyclerView, CategoryItemChooseListener categoryItemChooseListener,
                                     View.OnClickListener retryOnClickListener) {
        super(recyclerView, retryOnClickListener);
        this.categoryItemChooseListener = categoryItemChooseListener;
        ITEM_VIEW_HEIGHT = 100;

        selectedColor = ContextCompat.getColor(context, R.color.colorAccent);
    }

    @Override
    protected RecyclerView.ViewHolder createItemViewHolder(ViewGroup parent) {
        return new CategoryViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.category_item, parent, false));
    }

    @Override
    protected RecyclerView.ViewHolder createEmptyItemViewHolder(ViewGroup parent) {
        return new CategoryViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.category_item, parent, false));
    }

    @Override
    int calculateEmptyItemsCount() {
        return super.calculateEmptyItemsCount() * ITEMS_PER_ROW;
    }

    @Override
    void bindItemViewHolder(RecyclerView.ViewHolder holder, Category category) {
        CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;

        Glide.with(context)
                .load(category.getImage())
                .placeholder(R.drawable.ic_launcher_background)
                .into(categoryViewHolder.image);
        categoryViewHolder.title.setText(category.getTitle());
        categoryViewHolder.itemView.setOnClickListener((v -> {
            if (!category.isFavouritedByUser()) {
                category.setFavouritedByUser(true);
                categoryViewHolder.highlightView();
                categoryItemChooseListener.onCategoryChosen(category);
            } else {
                category.setFavouritedByUser(false);
                categoryViewHolder.unHighlightView();
                categoryItemChooseListener.onCategoryUnchosen(category);
            }
        }));

        if (category.isFavouritedByUser()) {
            categoryViewHolder.highlightView();
        } else {
            categoryViewHolder.unHighlightView();
        }
    }

    private class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView image;
        private ImageView selectedIcon;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
            selectedIcon = itemView.findViewById(R.id.selectedIcon);
        }

        private void highlightView() {
            image.setColorFilter(selectedColor, PorterDuff.Mode.OVERLAY);
            selectedIcon.setVisibility(View.VISIBLE);
        }

        private void unHighlightView() {
            image.setColorFilter(null);
            selectedIcon.setVisibility(View.INVISIBLE);
        }
    }
}
