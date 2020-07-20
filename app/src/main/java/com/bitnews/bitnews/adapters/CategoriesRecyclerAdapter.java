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

public class CategoriesRecyclerAdapter extends PaginationRecyclerAdapter<Category> {
    private CategoryItemChooseListener categoryItemChooseListener;
    private int selectedColor;

    public CategoriesRecyclerAdapter(RecyclerView recyclerView, CategoryItemChooseListener categoryItemChooseListener) {
        super(recyclerView);
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
        return super.calculateEmptyItemsCount() * 3;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;
            Category categoryItem = itemsList.get(position);

            // Glide.with(context)
            //        .load(categoryItem.getImage())
            //       .placeholder(R.drawable.ic_launcher_background)
            //        .into(categoryViewHolder.image);
            categoryViewHolder.title.setText(categoryItem.getTitle());
            categoryViewHolder.itemView.setOnClickListener((v -> {
                if (!categoryItem.isFavouritedByUser()) {
                    categoryItem.setFavouritedByUser(true);
                    categoryViewHolder.highlightView();
                    categoryItemChooseListener.onCategoryChosen(categoryItem);
                } else {
                    categoryItem.setFavouritedByUser(false);
                    categoryViewHolder.unHighlightView();
                    categoryItemChooseListener.onCategoryUnchosen(categoryItem);
                }
            }));

            if (categoryItem.isFavouritedByUser()) {
                categoryViewHolder.highlightView();
            } else {
                categoryViewHolder.unHighlightView();
            }
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
