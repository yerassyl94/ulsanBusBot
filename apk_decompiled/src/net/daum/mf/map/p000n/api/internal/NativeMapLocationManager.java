package net.daum.mf.map.p000n.api.internal;

/* renamed from: net.daum.mf.map.n.api.internal.NativeMapLocationManager */
public class NativeMapLocationManager {
    public static final int TRACKING_MODE_OFF = 1;
    public static final int TRACKING_MODE_ON_WITHOUT_HEADING = 2;
    public static final int TRACKING_MODE_ON_WITHOUT_HEADING_WITHOUT_MAP_MOVING = 4;
    public static final int TRACKING_MODE_ON_WITH_HEADING = 3;
    public static final int TRACKING_MODE_ON_WITH_HEADING_WITHOUT_MAP_MOVING = 5;
    public static final int TRACKING_MODE_ON_WITH_MARKER_HEADING_WITHOUT_MAP_MOVING = 6;

    public static native int getCurrentLocationTrackingMode();

    public static native boolean isShowingCurrentLocationMarker();

    public static native void setCurrentLocationRadius(int i);

    public static native void setCurrentLocationRadiusFillColor(int i);

    public static native void setCurrentLocationRadiusStrokeColor(int i);

    public static native void setCurrentLocationTrackingMode(int i);

    public static native void setCustomCurrentLocationMarkerDirectionImage(String str, int i, int i2, int i3, int i4, boolean z);

    public static native void setCustomCurrentLocationMarkerImage(String str, int i, int i2, int i3, int i4, boolean z);

    public static native void setCustomCurrentLocationMarkerTrackingAnimationImages(String[] strArr, int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4, float f);

    public static native void setCustomCurrentLocationMarkerTrackingImage(String str, int i, int i2, int i3, int i4, boolean z);

    public static native void setDefaultCurrentLocationMarker();

    public static native void setShowCurrentLocationMarker(boolean z);
}
