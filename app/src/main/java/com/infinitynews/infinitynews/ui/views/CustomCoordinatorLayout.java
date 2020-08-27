package com.infinitynews.infinitynews.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.infinitynews.infinitynews.R;

public class CustomCoordinatorLayout extends CoordinatorLayout {
    private NestedScrollView nestedScrollView;
    private BottomSheetBehavior<?> bottomSheetBehavior;
    private float lastY;

    public CustomCoordinatorLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private NestedScrollView getNestedScrollView() {
        if (nestedScrollView == null) {
            nestedScrollView = findViewById(R.id.postBottomSheet);
        }

        return nestedScrollView;
    }

    private BottomSheetBehavior<?> getBottomSheetBehavior() {
        if (bottomSheetBehavior == null) {
            LayoutParams layoutParams = (LayoutParams) getNestedScrollView().getLayoutParams();
            bottomSheetBehavior = (BottomSheetBehavior<?>) layoutParams.getBehavior();
        }

        return bottomSheetBehavior;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float newY = ev.getRawY();
                float dY = lastY - newY;
                lastY = newY;

                if (dY > 0) {
                    if (getNestedScrollView().canScrollVertically(-1))
                        return false;
                } else {
                    if (getBottomSheetBehavior().getState() != BottomSheetBehavior.STATE_EXPANDED)
                        return false;
                }
        }

        return super.onInterceptTouchEvent(ev);
    }
}
