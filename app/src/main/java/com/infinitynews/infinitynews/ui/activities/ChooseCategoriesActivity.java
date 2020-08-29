package com.infinitynews.infinitynews.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.infinitynews.infinitynews.R;
import com.infinitynews.infinitynews.adapters.CategoriesRecyclerAdapter;
import com.infinitynews.infinitynews.adapters.PaginationRecyclerAdapter;
import com.infinitynews.infinitynews.callbacks.CategoryItemChooseListener;
import com.infinitynews.infinitynews.callbacks.PaginationScrollListener;
import com.infinitynews.infinitynews.data.models.Category;
import com.infinitynews.infinitynews.data.network.APIResponse;
import com.infinitynews.infinitynews.ui.viewmodels.CategoryViewModel;

import java.util.ArrayList;
import java.util.List;


public class ChooseCategoriesActivity extends BaseActivity implements CategoryItemChooseListener {
    public static final int MIN_CHOSEN_CATEGORIES = 3;
    public static final int CATEGORY_ITEM_WIDTH = 100;
    private ArrayList<Category> chosenCategories = new ArrayList<>();
    private ArrayList<Category> initallyChosenCategories = new ArrayList<>();
    private RecyclerView categoriesRecyclerView;
    private SwipeRefreshLayout categoriesSwipeLayout;
    private CategoryViewModel categoryViewModel;
    private CategoriesRecyclerAdapter categoriesAdapter;
    private ProgressBar progressBar;
    private TextView retryButton;
    private Button nextButton;
    private TextView nextErrorLabel;
    private int categoriesCount;
    private int offset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_categories);

        progressBar = findViewById(R.id.nextProgressBar);
        nextErrorLabel = findViewById(R.id.nextErrorLabel);

        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        categoryViewModel.getCategoriesLiveData().observe(this, (response) -> {
            categoriesRecyclerView.suppressLayout(false);
            categoriesSwipeLayout.setRefreshing(false);

            switch (response.getStatus()) {
                case SUCCESFUL:
                    onSuccessfulResponse(response.getitem().getItems(), response.getitem().getCount());
                    break;
                case NETWORK_FAILED:
                    onErrorResponse();
            }
        });

        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);
        categoriesRecyclerView.setHasFixedSize(true);
        categoriesAdapter = new CategoriesRecyclerAdapter(categoriesRecyclerView, this, (v) -> {
            if (!categoriesAdapter.isLoading())
                loadCategories();
        });
        categoriesRecyclerView.setAdapter(categoriesAdapter);
        int spanCount = calculateSpanCount();
        categoriesAdapter.setItemsPerRow(spanCount);
        categoriesRecyclerView.setLayoutManager(getListLayoutManager(spanCount));
        categoriesRecyclerView.addOnScrollListener(getOnScrollListener());

        categoriesSwipeLayout = findViewById(R.id.swipeRefreshLayout);
        categoriesSwipeLayout.setColorSchemeColors(Color.YELLOW, Color.RED, Color.GREEN, Color.BLUE);
        categoriesSwipeLayout.setOnRefreshListener(this::onSwipeLayoutListener);

        nextButton = findViewById(R.id.nextButton);
        retryButton = findViewById(R.id.retryButton);
        retryButton.setOnClickListener(v -> {
            if (!categoriesAdapter.isLoading())
                loadCategories();
        });

        loadCategories();
    }

    private void onSuccessfulResponse(List<Category> categories, int count) {
        categoriesAdapter.removeFooterItem();

        if (count > 0)
            categoriesCount = count;
        else
            categoriesCount = -1;

        if (!categories.isEmpty()) {
            offset = categories.get(categories.size() - 1).getSort();

            categoriesAdapter.addAll(-1, categories);
            onNewCategoriesAdded(categories);
        }

        categoriesAdapter.finishLoading();
    }

    private void onErrorResponse() {
        if (categoriesAdapter.isEmpty()) {
            categoriesAdapter.setLoadingInitially(false);
            categoriesRecyclerView.setVisibility(View.INVISIBLE);
            retryButton.setVisibility(View.VISIBLE);
        } else {
            categoriesAdapter.setLoadingMore(false);
            categoriesAdapter.setLoadingFailed(true);
            categoriesAdapter.addFooterItem();
        }
    }

    private RecyclerView.LayoutManager getListLayoutManager(int spanCount) {
        GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (categoriesAdapter.getItemViewType(position) == PaginationRecyclerAdapter.VIEW_TYPE_FOOTER) {
                    return spanCount;
                }
                return 1;
            }
        });

        return layoutManager;
    }

    private int calculateSpanCount() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        float displayWidth = displayMetrics.widthPixels / displayMetrics.density;
        float recyclerViewWidth = displayWidth - (displayWidth / 5); // - 20%

        return Math.max((int) (recyclerViewWidth / CATEGORY_ITEM_WIDTH), 3);
    }

    private PaginationScrollListener getOnScrollListener() {
        return new PaginationScrollListener(categoriesRecyclerView.getLayoutManager()) {
            @Override
            public boolean isLastPage() {
                return offset >= categoriesCount && categoriesCount != -1;
            }

            @Override
            public boolean isLoading() {
                return categoriesAdapter.isLoading() || categoriesAdapter.isLoadingFailedAdded();
            }

            @Override
            public void loadMoreItems() {
                loadCategories();
            }
        };
    }

    private void onSwipeLayoutListener() {
        if (!categoriesAdapter.isLoading()) {
            refreshCategoires();
        } else
            categoriesSwipeLayout.setRefreshing(false);
    }

    private void refreshCategoires() {
        offset = 0;
        categoriesCount = 0;
        chosenCategories.clear();
        categoriesAdapter.clear();
        initallyChosenCategories.clear();
        loadCategories();
    }

    private void loadCategories() {
        categoriesRecyclerView.setVisibility(View.VISIBLE);
        retryButton.setVisibility(View.INVISIBLE);

        if (categoriesAdapter.isEmpty()) {
            categoriesAdapter.setLoadingInitially(true);
            categoriesAdapter.notifyDataSetChanged();
            categoriesRecyclerView.suppressLayout(true);
        } else {
            categoriesAdapter.setLoadingFailed(false);
            categoriesAdapter.setLoadingMore(true);
            categoriesAdapter.addFooterItem();
        }
        categoryViewModel.getAllCategories(getApplicationContext(), offset);
    }

    public void onNextButtonClicked(View view) {
        if (chosenCategories.equals(initallyChosenCategories)) {
            if (!getIntent().getBooleanExtra("isFromMainActivity", false)) {
                startActivity(new Intent(this, MainActivity.class));
            }
            finish();
            return;
        }

        LiveData<APIResponse<Object>> responseLiveData = categoryViewModel.updateFavouriteCategories(
                getApplicationContext(), chosenCategories);
        nextButton.setEnabled(false);
        nextErrorLabel.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        responseLiveData.observe(this, (response) -> {
            progressBar.setVisibility(View.INVISIBLE);

            switch (response.getStatus()) {
                case SUCCESFUL:
                    startActivity(new Intent(this, MainActivity.class));
                    finishAffinity();
                    break;
                case NETWORK_FAILED:
                case BAD_REQUEST:
                    nextButton.setEnabled(true);
                    nextErrorLabel.setVisibility(View.VISIBLE);
                    nextErrorLabel.setText(R.string.network_error);
            }
        });
    }

    @Override
    public void onCategoryChosen(Category category) {
        chosenCategories.add(category);
        updateNextButton();
    }

    @Override
    public void onCategoryUnchosen(Category category) {
        chosenCategories.remove(category);
        updateNextButton();
    }

    private void onNewCategoriesAdded(List<Category> categories) {
        for (Category category : categories) {
            if (category.isFavouritedByUser()) {
                chosenCategories.add(category);
                initallyChosenCategories.add(category);
            }
        }
        updateNextButton();
    }

    private void updateNextButton() {
        if (chosenCategories.size() >= MIN_CHOSEN_CATEGORIES) {
            nextButton.setEnabled(true);
            nextButton.setAlpha(1);
        } else {
            nextButton.setEnabled(false);
            nextButton.setAlpha(0.5f);
        }
    }
}
