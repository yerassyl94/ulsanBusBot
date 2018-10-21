package net.daum.mf.map.p000n.api.internal;

import net.daum.mf.map.p000n.api.NativeMapCoord;

/* renamed from: net.daum.mf.map.n.api.internal.NativePOIItemMarkerManager */
public class NativePOIItemMarkerManager {
    public static final int CALLOUT_BALLOON_BUTTON_TYPE_LEFT = 2;
    public static final int CALLOUT_BALLOON_BUTTON_TYPE_MAIN = 1;
    public static final int CALLOUT_BALLOON_BUTTON_TYPE_RIGHT = 3;
    public static final int MARKER_TYPE_BLUE_PIN = 1;
    public static final int MARKER_TYPE_CUSTOM_IMAGE = 4;
    public static final int MARKER_TYPE_RED_PIN = 2;
    public static final int MARKER_TYPE_YELLOW_PIN = 3;
    public static final int SHOW_ANINATION_TYPE_DROP_FROM_HEAVEN = 2;
    public static final int SHOW_ANINATION_TYPE_NO_ANIMATION = 1;
    public static final int SHOW_ANINATION_TYPE_SPRING_FROM_GROUND = 3;

    public static native int addPOIItemMarkerToMapView(String str, NativeMapCoord nativeMapCoord, int i, int i2, int i3, boolean z, boolean z2, boolean z3, float f, float f2, String str2, String str3, String str4, String str5, int i4, int i5, float f3, float f4, int i6, int i7, boolean z4, String str6, String str7, boolean z5, boolean z6);

    public static native void callbackAfterPrepareCalloutBalloonImage(int i, boolean z);

    public static native void deselectPOIItemMarker(int i);

    public static native void moveWithAnimation(int i, NativeMapCoord nativeMapCoord, Boolean bool);

    public static native void removeAllPOIItemMarkers();

    public static native void removePOIItemMarker(int i);

    public static native void selectPOIItemMarker(int i, boolean z);

    public static native void setAlpha(int i, float f);

    public static native void setCoord(int i, NativeMapCoord nativeMapCoord);

    public static native void setCustomCalloutBalloonImageFilePath(int i, String str);

    public static native void setCustomPressedCalloutBalloonImageFilePath(int i, String str);

    public static native void setName(int i, String str);

    public static native void setRotation(int i, float f);
}
