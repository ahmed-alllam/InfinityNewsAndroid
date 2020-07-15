package com.bitnews.bitnews.ui.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bitnews.bitnews.R;
import com.bitnews.bitnews.adapters.CategoriesRecyclerAdapter;
import com.bitnews.bitnews.callbacks.PaginationScrollListener;
import com.bitnews.bitnews.ui.viewmodels.CategoryViewModel;

public class ChooseCategoriesActivity extends AppCompatActivity {
    private RecyclerView categoriesRecyclerView;
    private CategoryViewModel categoryViewModel;
    private int categoriesCount;
    private int offset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_categories);

        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        categoryViewModel.getCategories().observe(this, (categories) -> {

        });

        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);
        categoriesRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        CategoriesRecyclerAdapter categoriesAdapter = new CategoriesRecyclerAdapter(categoriesRecyclerView);
        categoriesRecyclerView.setAdapter(categoriesAdapter);
        categoriesRecyclerView.addOnScrollListener(new PaginationScrollListener(categoriesRecyclerView.getLayoutManager()) {
            @Override
            public boolean isLastPage() {
                return offset == categoriesCount;
            }

            @Override
            public boolean isLoading() {
                return categoriesAdapter.isLoading();
            }

            @Override
            public void loadMoreItems() {

            }
        });
    }
}
