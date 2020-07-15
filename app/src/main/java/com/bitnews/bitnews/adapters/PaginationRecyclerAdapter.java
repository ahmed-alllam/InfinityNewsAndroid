package com.bitnews.bitnews.adapters;

import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class PaginationRecyclerAdapter<T> extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_EMPTY_ITEM = 1;
    private static final int VIEW_TYPE_LOADING_BAR = 2;
    private static final int ITEM_VIEW_HEIGHT = 0;

    private ArrayList<T> itemsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private boolean isLoadingMore;
    private boolean isLoadingInitially;

    public PaginationRecyclerAdapter(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM)
            return createItemViewHolder(parent);
        if (viewType == VIEW_TYPE_LOADING_BAR)
            return new RecyclerView.ViewHolder(new ProgressBar(parent.getContext())) {
            };
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
        if ((position == itemsList.size() - 1 && isLoadingMore) || itemsList.get(position) == null)
            return VIEW_TYPE_LOADING_BAR;
        return VIEW_TYPE_ITEM;
    }

    protected abstract int calculateEmptyItemsCount();

    public void addAll(List<T> objects) {
        if (itemsList.size() != 0) {
            recyclerView.post(() -> {
                itemsList.addAll(objects);
                notifyItemRangeInserted(itemsList.size() - objects.size(),
                        objects.size());
            });
        } else {
            recyclerView.post(() -> {
                itemsList.addAll(objects);
                notifyDataSetChanged();
            });
        }
    }

    public void clear() {
        recyclerView.post(() -> {
            itemsList.clear();
            notifyDataSetChanged();
        });
    }

    public void addLoadingBar() {
        isLoadingMore = true;
        recyclerView.post(() -> {
            itemsList.add(null);
            notifyItemInserted(itemsList.size() - 1);
        });
    }

    public void removeLoadingBar() {
        if (isLoadingMore) {
            isLoadingMore = false;
            int position = itemsList.size() - 1;
            recyclerView.post(() -> {
                itemsList.remove(position);
                notifyItemRemoved(position);
            });
        }
    }

    public boolean isLoading() {
        return isLoadingInitially || isLoadingMore;
    }

    protected abstract RecyclerView.ViewHolder createItemViewHolder(ViewGroup parent);

    protected abstract RecyclerView.ViewHolder createEmptyItemViewHolder(ViewGroup parent);
}
