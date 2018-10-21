package net.daum.android.map;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.alt.GLSurfaceView;
import android.opengl.alt.GLSurfaceView.Renderer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import net.daum.android.map.coord.MapCoord;
import net.daum.mf.map.common.MapThreadScheduling;
import net.daum.mf.map.p000n.api.NativeMapViewUiEvent;
import net.daum.mf.map.p000n.api.internal.NativeMapGraphicsViewGles;
import net.daum.mf.map.task.MainQueueHandler;
import net.daum.mf.map.task.WaitQueueManager;

public class MapView extends GLSurfaceView implements Renderer, MainQueueHandler {
    private static boolean _needToRecreate = true;
    private PointF _lastDisplacement = new PointF(0.0f, 0.0f);
    private PointF _lastDisplacementSecond = new PointF(0.0f, 0.0f);
    private ConcurrentLinkedQueue<Object> eventQueue = new ConcurrentLinkedQueue();
    private boolean hasSurface = false;
    private boolean initialized = false;
    private AtomicBoolean isRunning = new AtomicBoolean();
    private boolean isValidSurface = false;
    private MotionEvent lastEvent = null;
    private MapViewEventListener mapViewEventListener;
    private AtomicBoolean mapViewUpdated = new AtomicBoolean();
    private NativeMapGraphicsViewGles nativeGraphicsViewGles;
    private MapViewTouchEventListener touchEventListener;

    private void init() {
        setRenderer(this);
        setFocusableInTouchMode(true);
        this.nativeGraphicsViewGles = new NativeMapGraphicsViewGles();
    }

    public MapView(Context context) {
        super(context);
        init();
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        init();
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!(event.getAction() == 2 && ignoreMotionEvent(event))) {
            queueMapEvent(new NativeMapViewUiEvent(event));
            if (event.getAction() == 1) {
                this.lastEvent = null;
                this._lastDisplacement.set(0.0f, 0.0f);
                this._lastDisplacementSecond.set(0.0f, 0.0f);
            }
        }
        return true;
    }

    protected float calculateDisplacement(float x, float y, float lastX, float lastY, PointF resultDisplacement) {
        float diffX = x - lastX;
        float diffY = y - lastY;
        if (Math.abs(diffX) < 1.0f && Math.abs(diffY) < 1.0f) {
            return 0.0f;
        }
        float vectorSize = (float) Math.sqrt((double) ((diffX * diffX) + (diffY * diffY)));
        float normalizedX = diffX / vectorSize;
        float normalizedY = diffY / vectorSize;
        float dotProduct = (resultDisplacement.x * normalizedX) + (resultDisplacement.y * normalizedY);
        resultDisplacement.set(normalizedX, normalizedY);
        return dotProduct;
    }

    protected boolean ignoreMotionEvent(MotionEvent event) {
        float dotProductFirstTouch = 1.0f;
        float dotProductSecondTouch = 1.0f;
        if (this.lastEvent != null) {
            dotProductFirstTouch = calculateDisplacement(event.getX(), event.getY(), this.lastEvent.getX(), this.lastEvent.getY(), this._lastDisplacement);
            if (event.getPointerCount() > 1 && this.lastEvent.getPointerCount() > 1) {
                dotProductSecondTouch = calculateDisplacement(event.getX(1), event.getY(1), this.lastEvent.getX(1), this.lastEvent.getY(1), this._lastDisplacementSecond);
            }
        }
        this.lastEvent = MotionEvent.obtain(event);
        if (dotProductFirstTouch < 0.9f || dotProductSecondTouch < 0.9f) {
            return true;
        }
        return false;
    }

    public boolean onTrackballEvent(final MotionEvent event) {
        queueToMainQueue(new Runnable() {
            public void run() {
                MapController mapController = MapController.getInstance();
                float zoomLevel = 1.0f / mapController.getZoom();
                MapCoord coord = mapController.getCurrentMapViewpoint();
                float directionX = 1.0f;
                float directionY = 1.0f;
                if (((double) event.getX()) == 0.0d) {
                    directionX = 0.0f;
                } else if (event.getX() < 0.0f) {
                    directionX = -1.0f;
                }
                if (((double) event.getY()) == 0.0d) {
                    directionY = 0.0f;
                } else if (event.getY() < 0.0f) {
                    directionY = -1.0f;
                }
                MapController.getInstance().move(new MapCoord(coord.getX() + ((double) ((directionX * 250.0f) * zoomLevel)), coord.getY() - ((double) ((directionY * 250.0f) * zoomLevel))));
            }
        });
        return true;
    }

    void forceDestroyGraphicsView() {
        super.onDetachedFromWindow();
    }

    void queueMapEvent(Object event) {
        MapThreadScheduling.forceContinue();
        this.eventQueue.add(event);
    }

    public void queueToMainQueue(Runnable task) {
        MapThreadScheduling.forceContinue();
        queueEvent(task);
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        this.nativeGraphicsViewGles.onInitMapView();
        this.hasSurface = true;
        MapEngineManager.getInstance().resumeMapEngine();
    }

    public void onSurfaceChanged(GL10 gl, int w, int h) {
        this.nativeGraphicsViewGles.onSizeChangedMapView(w, h, 0, 0);
        this.isValidSurface = true;
        if (this.mapViewEventListener != null && !this.initialized) {
            this.mapViewEventListener.onLoadMapView();
            this.initialized = true;
        }
    }

    public void onDrawFrame(GL10 gl) {
        if (this.isRunning.get() && this.isValidSurface && this.hasSurface) {
            while (!this.eventQueue.isEmpty()) {
                Object event = this.eventQueue.poll();
                if (event != null) {
                    onUiEvent((NativeMapViewUiEvent) event);
                }
            }
            if (this.nativeGraphicsViewGles.onDrawMapView(null) == 1) {
                this.mapViewUpdated.set(true);
            } else {
                this.mapViewUpdated.set(false);
            }
        }
    }

    public void onBeforeFinished(GL10 gl) {
        if (this.isRunning.get() && this.isValidSurface) {
            _needToRecreate = true;
            this.nativeGraphicsViewGles.onBeforeFinishedMapView();
        }
    }

    public void onAfterFinished(GL10 gl) {
        this.isRunning.set(false);
    }

    public boolean needsSwap(GL10 gl) {
        if (this.mapViewUpdated.get() && !MapEngineManager.getInstance().getStopGlSwap()) {
            return true;
        }
        return false;
    }

    public void didSwap(GL10 gl) {
        if (_needToRecreate) {
            _needToRecreate = false;
        }
    }

    void onPauseActivity() {
        this.mapViewUpdated.set(false);
        onPause();
    }

    void onResumeActivity() {
        onResume();
        this.isRunning.set(true);
    }

    void onUiEvent(NativeMapViewUiEvent event) {
        MapThreadScheduling.forceContinue();
        this.nativeGraphicsViewGles.onUiEventMapView(event);
    }

    public void onLoopWhenPaused(GL10 gl) {
        WaitQueueManager.getInstance().onLoop();
    }

    public MapViewTouchEventListener getTouchEventListener() {
        return this.touchEventListener;
    }

    public void setTouchEventListener(MapViewTouchEventListener touchEventListener) {
        this.touchEventListener = touchEventListener;
    }

    public MapViewEventListener getMapViewEventListener() {
        return this.mapViewEventListener;
    }

    public void setMapViewEventListener(MapViewEventListener mapViewEventListener) {
        this.mapViewEventListener = mapViewEventListener;
    }
}
