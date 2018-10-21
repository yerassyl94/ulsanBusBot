package net.daum.android.map;

import android.content.Context;
import java.util.concurrent.atomic.AtomicBoolean;
import net.daum.mf.map.p000n.api.NativeMapEngineContext;
import net.daum.mf.map.p000n.api.internal.NativeMapEngine;
import net.daum.mf.map.task.MainQueueManager;

public final class MapEngineManager {
    private static boolean MapEngineInitialized = false;
    private static MapEngineManager instance = new MapEngineManager();
    private MapView mapGraphicsView;
    private NativeMapEngine nativeMapEngine = new NativeMapEngine();
    private boolean running;
    private AtomicBoolean stopGlSwap = new AtomicBoolean();

    public static MapEngineManager getInstance() {
        return instance;
    }

    private MapEngineManager() {
    }

    private void initializeMapEngine(Context mapActivity, MapView mapView) {
        NativeMapEngineContext.getInstance().setApplicationContext(mapActivity.getApplicationContext());
        this.stopGlSwap.set(false);
        if (!MapEngineInitialized) {
            this.nativeMapEngine.onInitializeMapEngine();
            MapEngineInitialized = true;
        }
        if (this.mapGraphicsView != null) {
            this.mapGraphicsView.forceDestroyGraphicsView();
        }
        this.mapGraphicsView = mapView;
        MainQueueManager.getInstance().setMainQueueHandler(this.mapGraphicsView);
    }

    public void onCreateMapActivity(Context mapActivity, MapView mapView) {
        initializeMapEngine(mapActivity, mapView);
    }

    public void onStartMapActivity() {
        this.nativeMapEngine.onStartMapEngine();
        this.stopGlSwap.set(false);
    }

    public void onRestartMapActivity() {
        this.stopGlSwap.set(false);
    }

    public void onResumeMapActivity() {
        this.running = true;
        this.mapGraphicsView.onResumeActivity();
    }

    public void onPauseMapActivity() {
        this.mapGraphicsView.onPauseActivity();
        this.running = false;
        this.nativeMapEngine.onPauseMapEngine();
    }

    public void onStopMapActivity() {
        this.nativeMapEngine.onStopMapEngine();
        this.stopGlSwap.set(true);
    }

    public void onDestroyMapActivity() {
        this.mapGraphicsView.forceDestroyGraphicsView();
    }

    public MapView getMapView() {
        return this.mapGraphicsView;
    }

    public boolean isRunning() {
        return this.running;
    }

    void resumeMapEngine() {
        this.nativeMapEngine.onResumeMapEngine();
    }

    public boolean getStopGlSwap() {
        return this.stopGlSwap.get();
    }
}
