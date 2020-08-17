package com.bitnews.bitnews.adapters;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bitnews.bitnews.R;

import java.util.ArrayList;
import java.util.List;

public abstract class PaginationRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_EMPTY_ITEM = 1;
    public static final int VIEW_TYPE_FOOTER = 2;

    static int ITEM_VIEW_HEIGHT = 0;

    private ArrayList<T> itemsList = new ArrayList<>();
    Context context;
    private RecyclerView recyclerView;
    private View.OnClickListener onFooterClickListener;
    private boolean isLoadingMore;
    private boolean isLoadingFailed;
    private boolean isLoadingInitially;
    int itemsPerScreenCount;

    PaginationRecyclerAdapter(RecyclerView recyclerView, View.OnClickListener onFooterClickListener) {
        this.recyclerView = recyclerView;
        context = recyclerView.getContext();
        this.onFooterClickListener = onFooterClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM)
            return createItemViewHolder(parent);
        if (viewType == VIEW_TYPE_FOOTER) {
            return new FooterItemViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.loading_footer, parent, false), onFooterClickListener);
        }

        return createEmptyItemViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_ITEM:
                bindItemViewHolder(holder, itemsList.get(position), position);
                break;
            case VIEW_TYPE_FOOTER:
                FooterItemViewHolder footerItemViewHolder = (FooterItemViewHolder) holder;
                if (isLoadingMore)
                    footerItemViewHolder.showProgressBar();
                if (isLoadingFailed)
                    footerItemViewHolder.showRetryButton();
        }
    }

    abstract void bindItemViewHolder(RecyclerView.ViewHolder holder, T item, int position);

    @Override
    public int getItemCount() {
        return itemsList.size() != 0 || !isLoadingInitially ? itemsList.size() : calculateEmptyItemsCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (itemsList.size() == 0)
            return VIEW_TYPE_EMPTY_ITEM;
        if (position == itemsList.size() - 1 && itemsList.get(position) == null) {
            return VIEW_TYPE_FOOTER;
        }
        return VIEW_TYPE_ITEM;
    }

    int calculateEmptyItemsCount() {
        if (itemsPerScreenCount == 0) {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            float recyclerViewHeightInDp = recyclerView.getHeight() / displayMetrics.density;

            itemsPerScreenCount = (int) Math.ceil(recyclerViewHeightInDp / ITEM_VIEW_HEIGHT);
        }
        return itemsPerScreenCount;
    }

    public void addAll(int index, List<T> objects) {
        recyclerView.post(() -> {
            int startIndex = index;
            if (index == -1)
                startIndex = itemsList.size();

            if (itemsList.size() != 0) {
                itemsList.addAll(startIndex, objects);
                notifyItemRangeInserted(startIndex, objects.size());
            } else {
                itemsList.addAll(objects);
                notifyDataSetChanged();
            }
        });
    }

    public void clear() {
        itemsList.clear();
        recyclerView.post(this::notifyDataSetChanged);
    }

    public void addFooterItem() {
        recyclerView.post(() -> {
            if (itemsList.contains(null))
                notifyItemChanged(itemsList.size() - 1);
            else {
                itemsList.add(null);
                notifyItemInserted(itemsList.size() - 1);
            }
        });
    }

    public void removeFooterItem() {
        recyclerView.post(() -> {
            if (itemsList.remove(null))
                notifyItemRemoved(itemsList.size());
        });
    }

    public boolean isLoading() {
        return isLoadingInitially || isLoadingMore;
    }

    public void finishLoading() {
        recyclerView.post(() -> {
            setLoadingInitially(false);
            setLoadingMore(false);
        });
    }

    public boolean isLoadingFailedAdded() {
        return isLoadingFailed;
    }

    public void setLoadingInitially(boolean loadingInitially) {
        isLoadingInitially = loadingInitially;
    }

    public void setLoadingMore(boolean loadingMore) {
        isLoadingMore = loadingMore;
    }

    public void setLoadingFailed(boolean loadingFailed) {
        isLoadingFailed = loadingFailed;
    }

    public boolean isEmpty() {
        return itemsList.isEmpty();
    }

    public int listSize() {
        return itemsList.size();
    }

    protected abstract RecyclerView.ViewHolder createItemViewHolder(ViewGroup parent);

    protected abstract RecyclerView.ViewHolder createEmptyItemViewHolder(ViewGroup parent);

    public static class FooterItemViewHolder extends RecyclerView.ViewHolder {
        protected ProgressBar progressBar;
        protected Button retryButton;

        FooterItemViewHolder(@NonNull View itemView, View.OnClickListener onFooterClickListener) {
            super(itemView);

            progressBar = itemView.findViewById(R.id.progressBar2);
            retryButton = itemView.findViewById(R.id.retryButton);

            retryButton.setOnClickListener(onFooterClickListener);
        }

        protected void showProgressBar() {
            progressBar.setVisibility(View.VISIBLE);
            retryButton.setVisibility(View.GONE);
        }

        protected void showRetryButton() {
            retryButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }
}
