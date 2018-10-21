package net.daum.mf.map.p000n.api.internal;

import net.daum.mf.map.p000n.api.NativeMapCoord;
import net.daum.mf.map.p000n.api.NativeMapLibraryLoader;

/* renamed from: net.daum.mf.map.n.api.internal.NativeMapController */
public class NativeMapController {
    public static native boolean isValidMapCoordForSouthKorea(NativeMapCoord nativeMapCoord);

    public native void changeGroundScaleWithAnimation(float f, boolean z);

    public native void clearTiles();

    public native NativeMapCoord convertGraphicPixelCoordToMapCoord(NativeMapCoord nativeMapCoord);

    public native NativeMapCoord convertMapCoordToGraphicPixelCoord(NativeMapCoord nativeMapCoord);

    public native void fitMapViewAreaToShowAllMapPoints(NativeMapCoord[] nativeMapCoordArr);

    public native float getBestZoom(NativeMapCoord nativeMapCoord, NativeMapCoord nativeMapCoord2);

    public native NativeMapCoord getCurrentMapBoundsBeginPoint();

    public native NativeMapCoord getCurrentMapBoundsEndPoint();

    public native NativeMapCoord getCurrentMapViewpoint();

    public native NativeMapCoord getCurrentPointingCoord();

    public native NativeMapCoord getDestinationMapViewpoint();

    public native float getMapRotationAngle();

    public native int getMapTileMode();

    public native int getViewType();

    public native float getZoom();

    public native float getZoomLevel();

    public native boolean isFullHDScreen();

    public native boolean isHDMapTileEnabled();

    public native boolean isHDScreen();

    public native boolean isMapEnable();

    public native boolean isUseLayer(int i);

    public native void move(NativeMapCoord nativeMapCoord);

    public native void move(NativeMapCoord nativeMapCoord, int i);

    public native void moveToViewMarker(NativeMapCoord nativeMapCoord);

    public native void releaseUnusedMapTileImageResources();

    public native void resetMapTileCache();

    public native void setHDMapTileEnabled(boolean z);

    public native void setMapCenterPoint(NativeMapCoord nativeMapCoord, boolean z);

    public native void setMapCenterPointAndZoomLevel(NativeMapCoord nativeMapCoord, float f, boolean z);

    public native void setMapEnable(boolean z);

    public native void setMapGroundAngleWithAnimation(float f, boolean z);

    public native void setMapRotationAngle(float f, boolean z);

    public native void setMapTileMode(int i);

    public native void setNeedsRefreshTiles();

    public native void setUseHeading(boolean z);

    public native void setUseLayer(int i, boolean z);

    public native void setViewType(int i);

    public native void setZoom(float f);

    public native void setZoomLevel(float f, boolean z);

    public native void startReceivingTileCommand(NativeMapCoord nativeMapCoord, float f);

    public native void updateCameraWithMapPointAndDiameter(NativeMapCoord nativeMapCoord, float f);

    public native void updateCameraWithMapPointAndDiameterAndPadding(NativeMapCoord nativeMapCoord, float f, int i);

    public native void updateCameraWithMapPoints(NativeMapCoord[] nativeMapCoordArr);

    public native void updateCameraWithMapPointsAndPadding(NativeMapCoord[] nativeMapCoordArr, int i);

    /* renamed from: updateCameraWithMapPointsAndPaddingAndMinZoomLevelAndMaxZoomLevel */
    public native void m4x6d3014d3(NativeMapCoord[] nativeMapCoordArr, int i, float f, float f2);

    public native void zoomIn(boolean z);

    public native void zoomOut(boolean z);

    static {
        NativeMapLibraryLoader.loadLibrary();
    }
}
