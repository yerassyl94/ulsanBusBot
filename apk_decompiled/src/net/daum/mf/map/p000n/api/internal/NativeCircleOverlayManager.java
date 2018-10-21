package net.daum.mf.map.p000n.api.internal;

import net.daum.mf.map.p000n.api.NativeMapCoord;

/* renamed from: net.daum.mf.map.n.api.internal.NativeCircleOverlayManager */
public class NativeCircleOverlayManager {
    public static native int addCircleToMap(NativeMapCoord nativeMapCoord, int i, int i2, int i3, int i4);

    public static native void removeAllCircles();

    public static native void removeCircle(int i);
}
