package com.bitnews.bitnews.adapters;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bitnews.bitnews.R;

import java.util.ArrayList;
import java.util.List;

public abstract class PaginationRecyclerAdapter<T> extends RecyclerView.Adapter {

    static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_EMPTY_ITEM = 1;
    private static final int VIEW_TYPE_LOADING_BAR = 2;
    static int ITEM_VIEW_HEIGHT = 0;

    ArrayList<T> itemsList = new ArrayList<>();
    Context context;
    private RecyclerView recyclerView;
    private boolean isLoadingMore;
    private boolean isLoadingInitially;

    PaginationRecyclerAdapter(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        context = recyclerView.getContext();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM)
            return createItemViewHolder(parent);
        if (viewType == VIEW_TYPE_LOADING_BAR) {
            return new RecyclerView.ViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.loading_item, parent, false)) {
            };
        }
        return createEmptyItemViewHolder(parent);
    }

    @Override
    public int getItemCount() {
        return itemsList.size() != 0 || !isLoadingInitially ? itemsList.size() : calculateEmptyItemsCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (itemsList.size() == 0)
            return VIEW_TYPE_EMPTY_ITEM;
        if (position == itemsList.size() - 1 && isLoadingMore && itemsList.get(position) == null)
            return VIEW_TYPE_LOADING_BAR;
        return VIEW_TYPE_ITEM;
    }

    int calculateEmptyItemsCount() {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float recyclerViewHeightInDp = recyclerView.getHeight() / displayMetrics.density;

        return (int) Math.ceil(recyclerViewHeightInDp / ITEM_VIEW_HEIGHT);
    }

    public void addAll(List<T> objects) {
        recyclerView.post(() -> {
            if (itemsList.size() != 0) {
                removeLoadingBar();
                itemsList.addAll(objects);
                notifyItemRangeInserted(itemsList.size() - objects.size(),
                        objects.size());
            } else {
                itemsList.addAll(objects);
                notifyDataSetChanged();
            }

            recyclerView.post(() -> {
                isLoadingInitially = false;
                isLoadingMore = false;
            });
        });
    }

    public void clear() {
        itemsList.clear();
        notifyDataSetChanged();
    }

    private void addLoadingBar() {
        itemsList.add(null);
        notifyItemInserted(itemsList.size() - 1);
    }

    private void removeLoadingBar() {
        itemsList.remove(null);
        notifyItemRemoved(itemsList.size());
    }

    public boolean isLoading() {
        return isLoadingInitially || isLoadingMore;
    }

    public void setLoadingInitially(boolean loadingInitially) {
        if (loadingInitially) {
            isLoadingInitially = true;
            recyclerView.post(this::notifyDataSetChanged);
        } else
            isLoadingInitially = false;
    }

    public void setLoadingMore(boolean loadingMore) {
        if (loadingMore) {
            isLoadingMore = true;
            recyclerView.post(this::addLoadingBar);
        } else {
            removeLoadingBar();
            recyclerView.post(() -> isLoadingMore = false);
        }
    }

    protected abstract RecyclerView.ViewHolder createItemViewHolder(ViewGroup parent);

    protected abstract RecyclerView.ViewHolder createEmptyItemViewHolder(ViewGroup parent);
}
