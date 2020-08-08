package com.bitnews.bitnews.ui.views;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.ScrollView;

public abstract class PopupScrollView extends ScrollView {
    float yOffset;
    float displayHeight;

    public PopupScrollView(Context context) {
        super(context);
    }

    protected float getDisplayHeight() {
        if (displayHeight == 0) {
            displayHeight = getContext().getResources().getDisplayMetrics().heightPixels;
        }
        return displayHeight;
    }

    protected abstract boolean isScrollable(boolean isScrollingUp);

    protected abstract void moveView(float yOffsetPercent);

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                yOffset = ev.getRawY();
                return true;

            case MotionEvent.ACTION_MOVE:
                float newY = ev.getRawY();

                float dy = yOffset - newY;
                yOffset = newY;

                if (isScrollable(dy > 0)) {
                    return super.onTouchEvent(ev);
                }

                moveView(dy / getDisplayHeight());

                return true;

            default:
                return false;
        }
    }
}
