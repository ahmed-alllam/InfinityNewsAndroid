package com.bitnews.bitnews.adapters;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

    int calculateEmptyItemsCount() {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float parentHeight = recyclerView.getHeight() / displayMetrics.density;

        return (int) (parentHeight / ITEM_VIEW_HEIGHT);
    }

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

    private void addLoadingBar() {
        recyclerView.post(() -> {
            itemsList.add(null);
            notifyItemInserted(itemsList.size() - 1);
        });
    }

    private void removeLoadingBar() {
        int position = itemsList.size() - 1;
        recyclerView.post(() -> {
            itemsList.remove(position);
            notifyItemRemoved(position);
        });
    }

    public boolean isLoading() {
        return isLoadingInitially || isLoadingMore;
    }

    public void setLoading(boolean loading) {
        if (loading) {
            if (itemsList.isEmpty()) {
                isLoadingInitially = true;
                notifyDataSetChanged();
            } else {
                isLoadingMore = true;
                addLoadingBar();
            }
        } else {
            isLoadingInitially = false;

            if (isLoadingMore) {
                removeLoadingBar();
                isLoadingMore = false;
            }
        }
    }

    protected abstract RecyclerView.ViewHolder createItemViewHolder(ViewGroup parent);

    protected abstract RecyclerView.ViewHolder createEmptyItemViewHolder(ViewGroup parent);
}
