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
    private RecyclerView categoriesRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
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
        categoryViewModel.getCategoriesLiveData().observe(this, (categories) -> {
            categoriesRecyclerView.suppressLayout(false);
            swipeRefreshLayout.setRefreshing(false);

            switch (categories.getStatus()) {
                case SUCCESFUL:
                    List<Category> items = categories.getitem().getItems();
                    int count = categories.getitem().getCount();
                    if (!items.isEmpty()) {
                        if (count > 0)
                            categoriesCount = count;
                        offset = items.get(items.size() - 1).getSort();
                        categoriesAdapter.addAll(categories.getitem().getItems());
                        onNewCategoriesAdded(items);
                    }
                    break;
                case NETWORK_FAILED:
                    categoriesRecyclerView.setVisibility(View.INVISIBLE);
                    categoriesErrorLabel.setVisibility(View.VISIBLE);
                    categoriesErrorLabel.setText(R.string.network_error);
                    break;
            }
        });

        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);
        categoriesRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        categoriesAdapter = new CategoriesRecyclerAdapter(categoriesRecyclerView, this);
        categoriesRecyclerView.setAdapter(categoriesAdapter);
        categoriesRecyclerView.addOnScrollListener(new PaginationScrollListener(categoriesRecyclerView.getLayoutManager()) {
            @Override
            public boolean isLastPage() {
                return offset >= categoriesCount;
            }

            @Override
            public boolean isLoading() {
                return categoriesAdapter.isLoading();
            }

            @Override
            public void loadMoreItems() {
                loadCategories(false);
            }
        });

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(Color.YELLOW, Color.RED, Color.GREEN, Color.BLUE);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (!categoriesAdapter.isLoading()) {
                offset = 0;
                categoriesCount = 0;
                categoriesAdapter.clear();
                loadCategories(true);
            } else
                swipeRefreshLayout.setRefreshing(false);
        });

        nextButton = findViewById(R.id.nextButton);
        nextButton.setEnabled(false);
        nextButton.setOnClickListener((v) -> {
            LiveData<APIResponse> responseLiveData = categoryViewModel.updateFavouriteCategories(
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
                        finish();
                        break;
                    case NETWORK_FAILED:
                        nextButton.setEnabled(true);
                        nextErrorLabel.setVisibility(View.VISIBLE);
                        nextErrorLabel.setText(R.string.network_error);
                }
            });

        });

        loadCategories(true);
    }

    private void loadCategories(boolean isInitialLoad) {
        categoriesRecyclerView.setVisibility(View.VISIBLE);
        categoriesErrorLabel.setVisibility(View.INVISIBLE);

        if (!categoriesAdapter.isLoading()) {
            if (isInitialLoad) {
                categoriesAdapter.setLoadingInitially();
                categoriesRecyclerView.suppressLayout(true);
            } else {
                categoriesAdapter.setLoadingMore();
            }
            categoryViewModel.getAllCategories(getApplicationContext(), offset);
        }
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
