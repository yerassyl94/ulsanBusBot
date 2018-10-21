package net.daum.mf.map.p000n.api.internal;

import android.graphics.Canvas;
import net.daum.mf.map.p000n.api.NativeMapLibraryLoader;
import net.daum.mf.map.p000n.api.NativeMapViewUiEvent;

/* renamed from: net.daum.mf.map.n.api.internal.NativeMapGraphicsViewGles */
public class NativeMapGraphicsViewGles {
    public native void onBeforeFinishedMapView();

    public native int onDrawMapView(Canvas canvas);

    public native void onInitMapView();

    public native void onReleaseMapView();

    public native void onSizeChangedMapView(int i, int i2, int i3, int i4);

    public native void onUiEventMapView(NativeMapViewUiEvent nativeMapViewUiEvent);

    static {
        NativeMapLibraryLoader.loadLibrary();
    }
}
