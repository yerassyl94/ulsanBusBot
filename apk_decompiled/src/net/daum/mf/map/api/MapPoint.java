package net.daum.mf.map.api;

import net.daum.android.map.MapController;
import net.daum.android.map.coord.MapCoord;
import net.daum.android.map.coord.MapCoordLatLng;

public class MapPoint {
    private MapCoord _internalCoord;

    public static class GeoCoordinate {
        public double latitude;
        public double longitude;

        public GeoCoordinate(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    public static class PlainCoordinate {
        /* renamed from: x */
        public double f13x;
        /* renamed from: y */
        public double f14y;

        public PlainCoordinate(double x, double y) {
            this.f13x = x;
            this.f14y = y;
        }
    }

    private MapPoint(MapCoord mapCoordWCONG) {
        this._internalCoord = mapCoordWCONG;
    }

    public static MapPoint mapPointWithGeoCoord(double latitude, double longitude) {
        return new MapPoint(new MapCoordLatLng(latitude, longitude).toWcong());
    }

    public static MapPoint mapPointWithWCONGCoord(double xWCONG, double yWCONG) {
        return new MapPoint(new MapCoord(xWCONG, yWCONG, 2));
    }

    public static MapPoint mapPointWithCONGCoord(double xCONG, double yCONG) {
        return new MapPoint(new MapCoord(xCONG, yCONG, 1).toWcong());
    }

    public static MapPoint mapPointWithScreenLocation(double xPixel, double yPixel) {
        return new MapPoint(MapController.getInstance().convertGraphicPixelCoordToMapCoord(new MapCoord(xPixel, yPixel)));
    }

    public GeoCoordinate getMapPointGeoCoord() {
        MapCoordLatLng mapCoordLatLng = this._internalCoord.toWgs();
        return new GeoCoordinate(mapCoordLatLng.getLatitude(), mapCoordLatLng.getLongitude());
    }

    public PlainCoordinate getMapPointWCONGCoord() {
        return new PlainCoordinate(this._internalCoord.getX(), this._internalCoord.getY());
    }

    public PlainCoordinate getMapPointCONGCoord() {
        MapCoord mapCoordCONG = this._internalCoord.toCong();
        return new PlainCoordinate(mapCoordCONG.getX(), mapCoordCONG.getY());
    }

    public PlainCoordinate getMapPointScreenLocation() {
        MapCoord mapCoordScreenLocation = MapController.getInstance().convertMapCoordToGraphicPixelCoord(this._internalCoord);
        return new PlainCoordinate(mapCoordScreenLocation.getX(), mapCoordScreenLocation.getY());
    }

    MapCoord getInternalCoordObject() {
        return this._internalCoord;
    }
}
