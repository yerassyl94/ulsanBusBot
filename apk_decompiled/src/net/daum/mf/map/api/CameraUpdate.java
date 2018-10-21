package net.daum.mf.map.api;

public class CameraUpdate {
    float mDiameter;
    MapPoint mMapPoint;
    MapPointBounds mMapPointBounds;
    float mMaxZoomLevel;
    float mMinZoomLevel;
    int mPadding;
    UPDATE_TYPE mUpdateType;
    float mZoomLevel;

    enum UPDATE_TYPE {
        UPDATE_WITH_MAP_POINT,
        UPDATE_WITH_MAP_POINT_AND_ZOOM_LEVEL,
        UPDATE_WITH_MAP_POINT_AND_DIAMETER,
        UPDATE_WITH_MAP_POINT_AND_DIAMETER_AND_PADDING,
        UPDATE_WITH_MAP_POINT_BOUNDS,
        UPDATE_WITH_MAP_POINT_BOUNDS_AND_PADDING,
        UPDATE_WITH_MAP_POINT_BOUNDS_AND_PADDING_AND_MIN_ZOOM_LEVEL_AND_MAX_ZOOM_LEVEL
    }

    CameraUpdate() {
        this.mZoomLevel = -999999.0f;
    }

    CameraUpdate(MapPoint mapPoint) {
        this.mZoomLevel = -999999.0f;
        this.mUpdateType = UPDATE_TYPE.UPDATE_WITH_MAP_POINT;
        this.mMapPoint = mapPoint;
    }

    CameraUpdate(MapPoint mapPoint, float zoomLevel) {
        this.mZoomLevel = -999999.0f;
        this.mUpdateType = UPDATE_TYPE.UPDATE_WITH_MAP_POINT_AND_ZOOM_LEVEL;
        this.mMapPoint = mapPoint;
        this.mZoomLevel = zoomLevel;
    }

    CameraUpdate(MapPointBounds mapPointBounds) {
        this.mZoomLevel = -999999.0f;
        this.mUpdateType = UPDATE_TYPE.UPDATE_WITH_MAP_POINT_BOUNDS;
        this.mMapPointBounds = mapPointBounds;
    }

    CameraUpdate(MapPointBounds mapPointBounds, int padding) {
        this.mZoomLevel = -999999.0f;
        this.mUpdateType = UPDATE_TYPE.UPDATE_WITH_MAP_POINT_BOUNDS_AND_PADDING;
        this.mMapPointBounds = mapPointBounds;
        this.mPadding = padding;
    }

    CameraUpdate(MapPointBounds mapPointBounds, int padding, float minZoomLevel, float maxZoomLevel) {
        this.mZoomLevel = -999999.0f;
        this.mUpdateType = UPDATE_TYPE.UPDATE_WITH_MAP_POINT_BOUNDS_AND_PADDING_AND_MIN_ZOOM_LEVEL_AND_MAX_ZOOM_LEVEL;
        this.mMapPointBounds = mapPointBounds;
        this.mPadding = padding;
        this.mMinZoomLevel = minZoomLevel;
        this.mMaxZoomLevel = maxZoomLevel;
    }
}
