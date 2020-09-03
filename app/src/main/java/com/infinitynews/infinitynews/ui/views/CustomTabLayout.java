package com.infinitynews.infinitynews.ui.views;

import android.content.Context;
import android.util.AttributeSet;

import com.google.android.material.tabs.TabLayout;

public class CustomTabLayout extends TabLayout {
    public CustomTabLayout(Context context) {
        super(context);
    }

    public CustomTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getTabCount() == 0)
            return;

        for (int i = 0; i < getTabCount(); i++) {
            Tab tab = getTabAt(i);

            if (tab != null)
                tab.view.setMinimumWidth((int) (getMeasuredWidth() / (float) getTabCount()));
        }
    }


}
