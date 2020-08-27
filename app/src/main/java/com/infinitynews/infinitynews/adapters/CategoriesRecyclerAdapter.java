package com.infinitynews.infinitynews.adapters;

import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.infinitynews.infinitynews.R;
import com.infinitynews.infinitynews.callbacks.CategoryItemChooseListener;
import com.infinitynews.infinitynews.data.models.Category;
import com.infinitynews.infinitynews.utils.CategoriesLocalizer;

public class CategoriesRecyclerAdapter extends PaginationRecyclerAdapter<Category> {
    private CategoryItemChooseListener categoryItemChooseListener;
    private static int selectedColor;
    private int itemsPerRow;

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
        if (itemsPerScreenCount == 0)
            itemsPerScreenCount = super.calculateEmptyItemsCount() * itemsPerRow;
        return itemsPerScreenCount;
    }

    public void setItemsPerRow(int itemsPerRow) {
        this.itemsPerRow = itemsPerRow;
    }

    @Override
    void bindItemViewHolder(RecyclerView.ViewHolder holder, Category category, int position) {
        CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;

        Glide.with(context)
                .load(category.getImage())
                .placeholder(R.drawable.ic_launcher_background)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(categoryViewHolder.image);

        categoryViewHolder.title.setText(CategoriesLocalizer.getLocalizedCategoryTitle(context, category.getTitle()));
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

    private static class CategoryViewHolder extends RecyclerView.ViewHolder {
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
