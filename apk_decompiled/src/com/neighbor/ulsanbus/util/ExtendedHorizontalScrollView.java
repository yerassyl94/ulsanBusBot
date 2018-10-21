package com.neighbor.ulsanbus.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;

public class ExtendedHorizontalScrollView extends HorizontalScrollView {
    private IScrollStateListener scrollStateListener;

    public interface IScrollStateListener {
        void onScrollFromMostLeft();

        void onScrollFromMostRight();

        void onScrollMostLeft();

        void onScrollMostRight();
    }

    public ExtendedHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ExtendedHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtendedHorizontalScrollView(Context context) {
        super(context);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        prepare();
    }

    private void prepare() {
        if (this.scrollStateListener != null) {
            View content = getChildAt(0);
            if (content.getLeft() >= 0) {
                this.scrollStateListener.onScrollMostLeft();
            }
            if (content.getLeft() < 0) {
                this.scrollStateListener.onScrollFromMostLeft();
            }
            if (content.getRight() <= getWidth()) {
                this.scrollStateListener.onScrollMostRight();
            }
            if (content.getLeft() > getWidth()) {
                this.scrollStateListener.onScrollFromMostRight();
            }
        }
    }

    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (this.scrollStateListener != null) {
            if (l == 0) {
                this.scrollStateListener.onScrollMostLeft();
            } else if (oldl == 0) {
                this.scrollStateListener.onScrollFromMostLeft();
            }
            int mostRightL = getChildAt(0).getWidth() - getWidth();
            if (l >= mostRightL) {
                this.scrollStateListener.onScrollMostRight();
            }
            if (oldl >= mostRightL && l < mostRightL) {
                this.scrollStateListener.onScrollFromMostRight();
            }
        }
    }

    public void setScrollStateListener(IScrollStateListener listener) {
        this.scrollStateListener = listener;
    }
}
