package net.daum.mf.map.p000n.api.internal;

import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.p000n.api.NativeMapCoord;

/* renamed from: net.daum.mf.map.n.api.internal.NativePolylineOverlayManager */
public class NativePolylineOverlayManager {
    public static native int addPolylineToMap(MapPolyline mapPolyline);

    public static native int addPolylineToMapView(NativeMapCoord[] nativeMapCoordArr, int i, float f, float f2, float f3, float f4);

    public static native void removeAllPolylines();

    public static native void removePolyline(int i);
}
