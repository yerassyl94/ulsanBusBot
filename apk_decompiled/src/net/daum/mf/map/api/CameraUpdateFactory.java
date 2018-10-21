package net.daum.mf.map.api;

public class CameraUpdateFactory {
    public static CameraUpdate newMapPoint(MapPoint mapPoint) {
        return new CameraUpdate(mapPoint);
    }

    public static CameraUpdate newMapPoint(MapPoint mapPoint, float zoomLevel) {
        return new CameraUpdate(mapPoint, zoomLevel);
    }

    public static CameraUpdate newMapPointAndDiameter(MapPoint mapPoint, float diameter) {
        CameraUpdate cameraUpdate = new CameraUpdate();
        cameraUpdate.mUpdateType = UPDATE_TYPE.UPDATE_WITH_MAP_POINT_AND_DIAMETER;
        cameraUpdate.mMapPoint = mapPoint;
        cameraUpdate.mDiameter = diameter;
        return cameraUpdate;
    }

    public static CameraUpdate newMapPointAndDiameter(MapPoint mapPoint, float diameter, int padding) {
        CameraUpdate cameraUpdate = new CameraUpdate();
        cameraUpdate.mUpdateType = UPDATE_TYPE.UPDATE_WITH_MAP_POINT_AND_DIAMETER_AND_PADDING;
        cameraUpdate.mMapPoint = mapPoint;
        cameraUpdate.mDiameter = diameter;
        cameraUpdate.mPadding = padding;
        return cameraUpdate;
    }

    public static CameraUpdate newMapPointBounds(MapPointBounds mapPointBounds) {
        return new CameraUpdate(mapPointBounds);
    }

    public static CameraUpdate newMapPointBounds(MapPointBounds mapPointBounds, int padding) {
        return new CameraUpdate(mapPointBounds, padding);
    }

    public static CameraUpdate newMapPointBounds(MapPointBounds mapPointBounds, int padding, float minZoomLevel, float maxZoomLevel) {
        return new CameraUpdate(mapPointBounds, padding, minZoomLevel, maxZoomLevel);
    }

    public static CameraUpdate newCameraPosition(CameraPosition cameraPosition) {
        return newMapPoint(cameraPosition.target, cameraPosition.zoomLevel);
    }
}
