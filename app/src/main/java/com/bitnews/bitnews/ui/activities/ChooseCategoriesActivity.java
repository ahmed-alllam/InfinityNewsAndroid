package com.bitnews.bitnews.ui.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bitnews.bitnews.R;
import com.bitnews.bitnews.adapters.CategoriesRecyclerAdapter;
import com.bitnews.bitnews.callbacks.PaginationScrollListener;
import com.bitnews.bitnews.data.models.Category;
import com.bitnews.bitnews.ui.viewmodels.CategoryViewModel;

import java.util.List;


public class ChooseCategoriesActivity extends AppCompatActivity {
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
            categoriesAdapter.setLoading(false);
            switch (categories.getStatus()) {
                case SUCCESFUL:
                    List<Category> items = categories.getitem().getItems();
                    int count = categories.getitem().getCount();
                    if (!items.isEmpty()) {
                        if (count > 0)
                            categoriesCount = count;
                        lastSort = items.get(items.size() - 1).getSort();
                        categoriesAdapter.addAll(categories.getitem().getItems());
                    }
                    break;
                case NETWORK_FAILED:
                    break;
            }
        });

        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);
        categoriesRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        categoriesAdapter = new CategoriesRecyclerAdapter(categoriesRecyclerView);
        categoriesRecyclerView.setAdapter(categoriesAdapter);
        categoriesRecyclerView.addOnScrollListener(new PaginationScrollListener(categoriesRecyclerView.getLayoutManager()) {
            @Override
            public boolean isLastPage() {
                return lastSort >= categoriesCount;
            }

            @Override
            public boolean isLoading() {
                return categoriesAdapter.isLoading();
            }

            @Override
            public void loadMoreItems() {
                loadCategories();
            }
        });

        loadCategories();
    }

    private void loadCategories() {
        categoriesAdapter.setLoading(true);
        categoryViewModel.getAllCategories(getApplicationContext(), lastSort);
    }
}
