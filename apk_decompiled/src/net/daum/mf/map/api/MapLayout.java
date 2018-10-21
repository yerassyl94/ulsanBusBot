package net.daum.mf.map.api;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class MapLayout extends FrameLayout {
    public static final String VIEW_TO_SHOW_BEFORE_MAP_VIEW_INITIALIZED = "VIEW_TO_SHOW_BEFORE_MAP_VIEW_INITIALIZED";
    private Activity activity;
    private MapView mapView;
    private View viewToShowBeforeMapViewInitialized;

    private static class GridPatternView extends View {
        private DrawFilter mDF;
        private final DrawFilter mFastDF = new PaintFlagsDrawFilter(6, 0);
        private final Paint mPaint;
        private final Shader mShader = new BitmapShader(MapLayout.makeBitmap(), TileMode.REPEAT, TileMode.REPEAT);
        private float mTouchCurrX;
        private float mTouchCurrY;
        private float mTouchStartX;
        private float mTouchStartY;

        public GridPatternView(Context context) {
            super(context);
            setFocusable(true);
            setFocusableInTouchMode(true);
            new Matrix().setRotate(30.0f);
            this.mPaint = new Paint(2);
        }

        protected void onDraw(Canvas canvas) {
            canvas.setDrawFilter(this.mDF);
            this.mPaint.setShader(this.mShader);
            canvas.drawPaint(this.mPaint);
            canvas.translate(this.mTouchCurrX - this.mTouchStartX, this.mTouchCurrY - this.mTouchStartY);
            canvas.drawPaint(this.mPaint);
        }

        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case 0:
                    this.mTouchCurrX = x;
                    this.mTouchStartX = x;
                    this.mTouchCurrY = y;
                    this.mTouchStartY = y;
                    this.mDF = this.mFastDF;
                    invalidate();
                    break;
                case 1:
                    this.mDF = null;
                    invalidate();
                    break;
                case 2:
                    this.mTouchCurrX = x;
                    this.mTouchCurrY = y;
                    invalidate();
                    break;
            }
            return true;
        }
    }

    public MapLayout(Activity activity) {
        super(activity);
        this.activity = activity;
        this.mapView = new MapView(activity);
        addView(this.mapView);
    }

    protected void onAttachedToWindow() {
        if (this.viewToShowBeforeMapViewInitialized == null) {
            this.viewToShowBeforeMapViewInitialized = new GridPatternView(this.activity);
        }
        this.viewToShowBeforeMapViewInitialized.setTag(VIEW_TO_SHOW_BEFORE_MAP_VIEW_INITIALIZED);
        addView(this.viewToShowBeforeMapViewInitialized);
        super.onAttachedToWindow();
    }

    private static Bitmap makeBitmap() {
        Bitmap bm = Bitmap.createBitmap(32, 32, Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        c.drawColor(Color.argb(255, 221, 222, 213));
        Paint p = new Paint();
        p.setColor(Color.argb(255, 237, 235, 237));
        c.drawRect(0.5f, 0.5f, 31.5f, 31.5f, p);
        return bm;
    }

    public MapView getMapView() {
        return this.mapView;
    }

    public void setViewToShowBeforeMapViewInitialized(View viewToShowBeforeMapViewInitialized) {
        this.viewToShowBeforeMapViewInitialized = viewToShowBeforeMapViewInitialized;
    }
}
