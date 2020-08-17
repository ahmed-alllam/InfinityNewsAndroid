package com.bitnews.bitnews.callbacks;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bitnews.bitnews.adapters.PaginationRecyclerAdapter;

public abstract class PaginationScrollListener extends RecyclerView.OnScrollListener {
    private RecyclerView.LayoutManager layoutManager;

    protected PaginationScrollListener(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (dy <= 0)
            return;

        int lastVisableItemPosition = 0;

        if (layoutManager instanceof LinearLayoutManager) {
            lastVisableItemPosition = ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
        }

        if (layoutManager instanceof GridLayoutManager) {
            lastVisableItemPosition = ((GridLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
        }


        PaginationRecyclerAdapter<?> recyclerAdapter = (PaginationRecyclerAdapter<?>) recyclerView.getAdapter();

        if (lastVisableItemPosition == recyclerAdapter.listSize() - 1
                && !isLastPage() && !isLoading()) {
            loadMoreItems();
        }
    }

    public abstract boolean isLastPage();

    public abstract boolean isLoading();

    public abstract void loadMoreItems();
}
