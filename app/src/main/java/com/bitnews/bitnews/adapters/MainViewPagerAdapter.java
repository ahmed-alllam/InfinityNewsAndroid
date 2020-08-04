package com.bitnews.bitnews.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bitnews.bitnews.data.models.Category;
import com.bitnews.bitnews.ui.fragments.PostsFragment;

import java.util.ArrayList;
import java.util.List;

public class MainViewPagerAdapter extends FragmentStateAdapter {
    private List<Category> categoriesList;
    private List<PostsFragment> postsFragments = new ArrayList<>();

    public MainViewPagerAdapter(FragmentActivity fa, List<Category> categoriesList) {
        super(fa);
        this.categoriesList = categoriesList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        PostsFragment postsFragment = new PostsFragment(categoriesList.get(position));
        postsFragments.add(postsFragment);
        return postsFragment;
    }

    public PostsFragment getFragmentAt(int position) {
        if (position < postsFragments.size())
            return postsFragments.get(position);

        return null;
    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }
}
