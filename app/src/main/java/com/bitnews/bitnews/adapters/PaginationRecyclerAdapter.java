package com.bitnews.bitnews.adapters;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bitnews.bitnews.R;

import java.util.ArrayList;
import java.util.List;

public abstract class PaginationRecyclerAdapter<T> extends RecyclerView.Adapter {

    static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_EMPTY_ITEM = 1;
    public static final int VIEW_TYPE_LOADING_BAR = 2;
    public static final int VIEW_TYPE_LOADING_FAILED = 3;

    static int ITEM_VIEW_HEIGHT = 0;

    ArrayList<T> itemsList = new ArrayList<>();
    Context context;
    private RecyclerView recyclerView;
    View.OnClickListener retryOnClickListener;
    private boolean isLoadingMore;
    private boolean isLoadingFailed;
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
        if (viewType == VIEW_TYPE_LOADING_BAR)
            return new RecyclerView.ViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.loading_item, parent, false)) {
            };
        if (viewType == VIEW_TYPE_LOADING_FAILED) {
            View loadingFailedView = LayoutInflater.from(context)
                    .inflate(R.layout.loading_failed_item, parent, false);
            loadingFailedView.findViewById(R.id.retryButton).setOnClickListener(retryOnClickListener);
            return new RecyclerView.ViewHolder(loadingFailedView) {
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
        if (position == itemsList.size() - 1 && itemsList.get(position) == null) {
            if (isLoadingMore)
                return VIEW_TYPE_LOADING_BAR;
            if (isLoadingFailed)
                return VIEW_TYPE_LOADING_FAILED;
        }
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
                removeFooterItem();
                itemsList.addAll(objects);
                notifyItemRangeInserted(itemsList.size() - objects.size(),
                        objects.size());
            } else {
                itemsList.addAll(objects);
                notifyDataSetChanged();
            }

            recyclerView.post(() -> {
                isLoadingFailed = false;
                isLoadingInitially = false;
                isLoadingMore = false;
            });
        });
    }

    public void clear() {
        itemsList.clear();
        notifyDataSetChanged();
    }

    private void addFooterItem() {
        itemsList.add(null);
        notifyItemInserted(itemsList.size() - 1);
    }

    private void removeFooterItem() {
        itemsList.remove(null);
        notifyItemRemoved(itemsList.size());
    }

    public boolean isLoading() {
        return isLoadingInitially || isLoadingMore;
    }

    public void setLoadingInitially(boolean loadingInitially) {
        isLoadingInitially = loadingInitially;
        recyclerView.post(this::notifyDataSetChanged);
    }

    public void setLoadingMore(boolean loadingMore) {
        isLoadingMore = loadingMore;

        if (loadingMore) {
            if (!isLoadingFailed)
                recyclerView.post(this::addFooterItem);
            else {
                isLoadingFailed = false;
                notifyItemChanged(itemsList.size() - 1);
            }
        } else
            recyclerView.post(this::removeFooterItem);
    }

    public void setLoadingFailed(boolean loadingFailed) {
        isLoadingFailed = loadingFailed;

        if (isLoadingFailed) {
            if (isLoadingMore) {
                isLoadingMore = false;
                recyclerView.post(() -> notifyItemChanged(itemsList.size()));
            }
        }
    }

    public boolean isEmpty() {
        return itemsList.isEmpty();
    }

    protected abstract RecyclerView.ViewHolder createItemViewHolder(ViewGroup parent);

    protected abstract RecyclerView.ViewHolder createEmptyItemViewHolder(ViewGroup parent);
}
