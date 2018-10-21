package net.daum.mf.map.p000n.api.internal;

import net.daum.mf.map.p000n.api.NativeMapCoord;

/* renamed from: net.daum.mf.map.n.api.internal.NativeMapViewController */
public class NativeMapViewController {
    public native void disuseInfoWindows();

    public native void setAccuracy(float f);

    public native void setLocationMarkerRotation(float f);

    public native void showInfoPanelTimedMessage(String str, float f);

    public native void showLocationMarkerWithAnimation(NativeMapCoord nativeMapCoord, boolean z, boolean z2);

    public native void showZoomControls(boolean z);

    public native void switchHeadingMarker(boolean z);

    public native void switchTrackingMarker(boolean z);
}
