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
    private View.OnClickListener retryOnClickListener;
    private boolean isLoadingMore;
    private boolean isLoadingFailed;
    private boolean isLoadingInitially;

    PaginationRecyclerAdapter(RecyclerView recyclerView, View.OnClickListener retryOnClickListener) {
        this.recyclerView = recyclerView;
        context = recyclerView.getContext();
        this.retryOnClickListener = retryOnClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM)
            return createItemViewHolder(parent);
        if (viewType == VIEW_TYPE_FOOTER) {
            return new FooterItemViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.loading_footer, parent, false), retryOnClickListener);
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
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float recyclerViewHeightInDp = recyclerView.getHeight() / displayMetrics.density;

        return (int) Math.ceil(recyclerViewHeightInDp / ITEM_VIEW_HEIGHT);
    }

    public void addAll(List<T> objects) {
        addAll(itemsList.size() - 1, objects);
    }

    public void addAll(int index, List<T> objects) {
        recyclerView.post(() -> {
            if (isLoadingMore || isLoadingFailed)
                removeFooterItem();

            if (itemsList.size() != 0) {
                itemsList.addAll(index, objects);
                notifyItemRangeInserted(index, objects.size());
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

    public boolean isLoadingFailedAdded() {
        return isLoadingFailed;
    }

    public void setLoadingInitially(boolean loadingInitially) {
        isLoadingInitially = loadingInitially;
        recyclerView.post(this::notifyDataSetChanged);
    }

    public void setLoadingMore(boolean loadingMore) {
        isLoadingMore = loadingMore;

        if (loadingMore) {
            if (!isLoadingFailed) {
                recyclerView.post(this::addFooterItem);
            }
            else {
                isLoadingFailed = false;
                notifyItemChanged(itemsList.size() - 1);
            }
        } else {
            recyclerView.post(this::removeFooterItem);
        }
    }

    public void setLoadingFailed(boolean loadingFailed) {
        isLoadingFailed = loadingFailed;

        if (isLoadingFailed) {
            if (isLoadingMore) {
                isLoadingMore = false;
                notifyItemChanged(itemsList.size() - 1);
            }
        }
    }

    public boolean isEmpty() {
        return itemsList.isEmpty();
    }

    protected abstract RecyclerView.ViewHolder createItemViewHolder(ViewGroup parent);

    protected abstract RecyclerView.ViewHolder createEmptyItemViewHolder(ViewGroup parent);

    public class FooterItemViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;
        private Button retryButton;

        FooterItemViewHolder(@NonNull View itemView, View.OnClickListener retryOnClickListener) {
            super(itemView);

            progressBar = itemView.findViewById(R.id.progressBar2);
            retryButton = itemView.findViewById(R.id.retryButton);

            retryButton.setOnClickListener(retryOnClickListener);
        }

        private void showProgressBar() {
            progressBar.setVisibility(View.VISIBLE);
            retryButton.setVisibility(View.GONE);
        }

        private void showRetryButton() {
            retryButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }
}
