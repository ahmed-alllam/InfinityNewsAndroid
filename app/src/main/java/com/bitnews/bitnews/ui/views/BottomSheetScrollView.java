package com.bitnews.bitnews.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

public class BottomSheetScrollView extends ScrollView {
    private BottomSheetBehavior<BottomSheetScrollView> bottomSheetBehavior;

    public BottomSheetScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case ACTION_UP:
            case ACTION_CANCEL:
            case ACTION_MOVE:
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    return false;
                }
        }
        return super.onTouchEvent(ev);
    }

    public void setBottomSheetBehavior(BottomSheetBehavior<BottomSheetScrollView> bottomSheetBehavior) {
        this.bottomSheetBehavior = bottomSheetBehavior;
    }
}
