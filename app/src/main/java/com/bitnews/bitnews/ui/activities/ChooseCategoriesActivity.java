package com.bitnews.bitnews.ui.activities;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bitnews.bitnews.R;
import com.bitnews.bitnews.adapters.CategoriesRecyclerAdapter;
import com.bitnews.bitnews.callbacks.CategoryItemChooseListener;
import com.bitnews.bitnews.callbacks.PaginationScrollListener;
import com.bitnews.bitnews.data.models.Category;
import com.bitnews.bitnews.ui.viewmodels.CategoryViewModel;

import java.util.ArrayList;
import java.util.List;


public class ChooseCategoriesActivity extends AppCompatActivity implements CategoryItemChooseListener {
    public static final int MIN_CHOSEN_CATEGORIES = 3;
    private ArrayList<Category> chosenCategories = new ArrayList<>();
    private RecyclerView categoriesRecyclerView;
    private CategoryViewModel categoryViewModel;
    private CategoriesRecyclerAdapter categoriesAdapter;
    private int categoriesCount;
    private int lastSort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_categories);

        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        categoryViewModel.getCategoriesLiveData().observe(this, (categories) -> {
            switch (categories.getStatus()) {
                case SUCCESFUL:
                    List<Category> items = categories.getitem().getItems();
                    int count = categories.getitem().getCount();
                    if (!items.isEmpty()) {
                        if (count > 0)
                            categoriesCount = count;
                        else
                            categoriesCount += items.size();
                        lastSort = items.get(items.size() - 1).getSort();
                        categoriesAdapter.addAll(categories.getitem().getItems());
                        onNewCategoriesAdded(items);
                    }
                    break;
                case NETWORK_FAILED:
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
                return lastSort >= 16;
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

        loadCategories(true);
    }

    private void loadCategories(boolean isInitialLoad) {
        if (!categoriesAdapter.isLoading()) {
            if (isInitialLoad) {
                categoriesAdapter.setLoadingInitially();
            } else {
                categoriesAdapter.setLoadingMore();
            }
            categoryViewModel.getAllCategories(getApplicationContext(), lastSort);
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
        Button nextButton = findViewById(R.id.nextButton);

        if (chosenCategories.size() >= MIN_CHOSEN_CATEGORIES) {
            nextButton.setEnabled(true);
            nextButton.setAlpha(1);
        } else {
            nextButton.setEnabled(false);
            nextButton.setAlpha(0.5f);
        }
    }
}
