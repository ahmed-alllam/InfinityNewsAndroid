package com.bitnews.bitnews.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bitnews.bitnews.R;
import com.bitnews.bitnews.adapters.CategoriesRecyclerAdapter;
import com.bitnews.bitnews.adapters.PaginationRecyclerAdapter;
import com.bitnews.bitnews.callbacks.CategoryItemChooseListener;
import com.bitnews.bitnews.callbacks.PaginationScrollListener;
import com.bitnews.bitnews.data.models.Category;
import com.bitnews.bitnews.data.network.APIResponse;
import com.bitnews.bitnews.ui.viewmodels.CategoryViewModel;

import java.util.ArrayList;
import java.util.List;


public class ChooseCategoriesActivity extends AppCompatActivity implements CategoryItemChooseListener {
    public static final int MIN_CHOSEN_CATEGORIES = 3;
    private ArrayList<Category> chosenCategories = new ArrayList<>();
    private ArrayList<Category> initallyChosenCategories = new ArrayList<>();
    private RecyclerView categoriesRecyclerView;
    private SwipeRefreshLayout categoriesSwipeLayout;
    private CategoryViewModel categoryViewModel;
    private CategoriesRecyclerAdapter categoriesAdapter;
    private ProgressBar progressBar;
    private TextView categoriesErrorLabel;
    private Button nextButton;
    private TextView nextErrorLabel;
    private int categoriesCount;
    private int offset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_categories);

        progressBar = findViewById(R.id.nextProgressBar);
        categoriesErrorLabel = findViewById(R.id.categoriesErrorLabel);
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
        categoriesAdapter = new CategoriesRecyclerAdapter(categoriesRecyclerView, this, (v) -> {
            if (!categoriesAdapter.isLoading())
                loadCategories(false);
        });
        categoriesRecyclerView.setAdapter(categoriesAdapter);
        categoriesRecyclerView.setLayoutManager(getListLayoutManager());
        categoriesRecyclerView.addOnScrollListener(getOnScrollListener());

        categoriesSwipeLayout = findViewById(R.id.swipeRefreshLayout);
        categoriesSwipeLayout.setColorSchemeColors(Color.YELLOW, Color.RED, Color.GREEN, Color.BLUE);
        categoriesSwipeLayout.setOnRefreshListener(this::onSwipeLayoutListener);

        nextButton = findViewById(R.id.nextButton);

        loadCategories(true);
    }

    private void onSuccessfulResponse(List<Category> categories, int count) {
        if (!categories.isEmpty()) {
            if (count > 0)
                categoriesCount = count;
            else
                categoriesCount = -1;
            offset = categories.get(categories.size() - 1).getSort();
            categoriesAdapter.addAll(categories);
            onNewCategoriesAdded(categories);
        } else {
            if (categoriesAdapter.isEmpty()) {
                categoriesRecyclerView.setVisibility(View.INVISIBLE);
                categoriesErrorLabel.setVisibility(View.VISIBLE);
                categoriesErrorLabel.setText(R.string.no_feed);
            } else {
                categoriesAdapter.setLoadingMore(false);
            }
        }
    }

    private void onErrorResponse() {
        if (categoriesAdapter.isEmpty()) {
            categoriesAdapter.setLoadingInitially(false);
            categoriesRecyclerView.setVisibility(View.INVISIBLE);
            categoriesErrorLabel.setVisibility(View.VISIBLE);
            categoriesErrorLabel.setText(R.string.network_error);
        } else
            categoriesAdapter.setLoadingFailed(true);
    }

    private RecyclerView.LayoutManager getListLayoutManager() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (categoriesAdapter.getItemViewType(position) == PaginationRecyclerAdapter.VIEW_TYPE_FOOTER) {
                    return 3;
                }
                return 1;
            }
        });

        return layoutManager;
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
                loadCategories(false);
            }
        };
    }

    private void onSwipeLayoutListener() {
        if (!categoriesAdapter.isLoading()) {
            loadCategories(true);
        } else
            categoriesSwipeLayout.setRefreshing(false);
    }

    private void loadCategories(boolean isInitialLoad) {
        categoriesRecyclerView.setVisibility(View.VISIBLE);
        categoriesErrorLabel.setVisibility(View.INVISIBLE);

        if (isInitialLoad) {
            offset = 0;
            categoriesCount = 0;
            chosenCategories.clear();
            categoriesAdapter.clear();
            categoriesAdapter.setLoadingInitially(true);
            categoriesRecyclerView.suppressLayout(true);
        } else {
            categoriesAdapter.setLoadingMore(true);
        }
        categoryViewModel.getAllCategories(getApplicationContext(), offset);
    }

    public void onNextButtonClicked(View view) {
        if (chosenCategories.size() == initallyChosenCategories.size()) {
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
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finishAffinity();
                    break;
                case NETWORK_FAILED:
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
