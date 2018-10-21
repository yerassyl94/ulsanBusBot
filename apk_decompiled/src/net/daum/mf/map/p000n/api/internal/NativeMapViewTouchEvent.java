package net.daum.mf.map.p000n.api.internal;

import net.daum.android.map.MapEngineManager;
import net.daum.android.map.MapViewTouchEventListener;

/* renamed from: net.daum.mf.map.n.api.internal.NativeMapViewTouchEvent */
public class NativeMapViewTouchEvent {
    public static void onSingleTap() {
        MapViewTouchEventListener listener = MapEngineManager.getInstance().getMapView().getTouchEventListener();
        if (listener != null) {
            listener.onSingleTap();
        }
    }

    public static void onDoubleTap() {
        MapViewTouchEventListener listener = MapEngineManager.getInstance().getMapView().getTouchEventListener();
        if (listener != null) {
            listener.onDoubleTap();
        }
    }

    public static void onHoldMap() {
        MapViewTouchEventListener listener = MapEngineManager.getInstance().getMapView().getTouchEventListener();
        if (listener != null) {
            listener.onHoldMap();
        }
    }

    public static void onMoveMap() {
        MapViewTouchEventListener listener = MapEngineManager.getInstance().getMapView().getTouchEventListener();
        if (listener != null) {
            listener.onMoveMap();
        }
    }
}
