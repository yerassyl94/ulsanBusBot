package net.daum.mf.map.api;

public class InternalMapViewAccessor {
    public static MapView getCurrentMapViewInstance() {
        return MapView.CurrentMapViewInstance;
    }

    public static void callMapView_onOpenAPIKeyAuthenticationResult(int resultCode, String resultMessage) {
        if (MapView.CurrentMapViewInstance != null) {
            MapView.CurrentMapViewInstance.onOpenAPIKeyAuthenticationResult(resultCode, resultMessage);
        }
    }

    public static void callMapView_onMapViewCenterPointMoved(double centerPointX, double centerPointY) {
        if (MapView.CurrentMapViewInstance != null) {
            MapView.CurrentMapViewInstance.onMapViewCenterPointMoved(centerPointX, centerPointY);
        }
    }

    public static void callMapView_onMapViewZoomLevelChanged(int zoomLevel) {
        if (MapView.CurrentMapViewInstance != null) {
            MapView.CurrentMapViewInstance.onMapViewZoomLevelChanged(zoomLevel);
        }
    }

    public static void callMapView_onMapViewSingleTapped(double mapPointX, double mapPointY) {
        if (MapView.CurrentMapViewInstance != null) {
            MapView.CurrentMapViewInstance.onMapViewSingleTapped(mapPointX, mapPointY);
        }
    }

    public static void callMapView_onMapViewDoubleTapped(double mapPointX, double mapPointY) {
        if (MapView.CurrentMapViewInstance != null) {
            MapView.CurrentMapViewInstance.onMapViewDoubleTapped(mapPointX, mapPointY);
        }
    }

    public static void callMapView_onMapViewLongPressed(double mapPointX, double mapPointY) {
        if (MapView.CurrentMapViewInstance != null) {
            MapView.CurrentMapViewInstance.onMapViewLongPressed(mapPointX, mapPointY);
        }
    }

    public static void callMapView_onMapViewDragStarted(double mapPointX, double mapPointY) {
        if (MapView.CurrentMapViewInstance != null) {
            MapView.CurrentMapViewInstance.onMapViewDragStarted(mapPointX, mapPointY);
        }
    }

    public static void callMapView_onMapViewDragEnded(double mapPointX, double mapPointY) {
        if (MapView.CurrentMapViewInstance != null) {
            MapView.CurrentMapViewInstance.onMapViewDragEnded(mapPointX, mapPointY);
        }
    }

    public static void callMapView_onMapViewMoveFinished(double mapPointX, double mapPointY) {
        if (MapView.CurrentMapViewInstance != null) {
            MapView.CurrentMapViewInstance.onMapViewMoveFinished(mapPointX, mapPointY);
        }
    }

    public static void callMapView_onCurrentLocationUpdate(double latitude, double longitude, float accuracy) {
        if (MapView.CurrentMapViewInstance != null) {
            MapView.CurrentMapViewInstance.onCurrentLocationUpdate(latitude, longitude, accuracy);
        }
    }

    public static void callMapView_onCurrentLocationDeviceHeadingUpdate(float headingAngle) {
        if (MapView.CurrentMapViewInstance != null) {
            MapView.CurrentMapViewInstance.onCurrentLocationDeviceHeadingUpdate(headingAngle);
        }
    }

    public static void callMapView_onCurrentLocationUpdateFailed() {
        if (MapView.CurrentMapViewInstance != null) {
            MapView.CurrentMapViewInstance.onCurrentLocationUpdateFailed();
        }
    }

    public static void callMapView_onCurrentLocationUpdateCancelled() {
        if (MapView.CurrentMapViewInstance != null) {
            MapView.CurrentMapViewInstance.onCurrentLocationUpdateCancelled();
        }
    }

    public static void callMapView_onPOIItemSelected(int poiMarkerId) {
        if (MapView.CurrentMapViewInstance != null) {
            MapView.CurrentMapViewInstance.onPOIItemSelected(poiMarkerId);
        }
    }

    public static void callMapView_onCalloutBalloonOfPOIItemTouched(int poiMarkerId, int buttonType) {
        if (MapView.CurrentMapViewInstance != null) {
            MapView.CurrentMapViewInstance.onCalloutBalloonOfPOIItemTouched(poiMarkerId, buttonType);
        }
    }

    public static void callMapView_onDraggablePOIItemMoved(int poiMarkerId, double newMapPointX, double newMapPointY) {
        if (MapView.CurrentMapViewInstance != null) {
            MapView.CurrentMapViewInstance.onDraggablePOIItemMoved(poiMarkerId, newMapPointX, newMapPointY);
        }
    }
}
