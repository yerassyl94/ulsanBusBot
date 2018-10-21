package net.daum.android.map.coord;

import android.util.Log;
import net.daum.mf.map.p000n.api.NativeMapCoordConverter;

public class MapCoordLatLng {
    private static final String LOG_TAG = "MapCoordLatLng";
    public static final MapCoordLatLng WGS84_UNDEFINED = new MapCoordLatLng(-1.0E7d, -1.0E7d, 4);
    protected double _latitude;
    protected double _longitude;
    protected int _type;

    public MapCoordLatLng() {
        this._latitude = -1.0E7d;
        this._longitude = -1.0E7d;
        this._type = 4;
    }

    public MapCoordLatLng(double lat, double lng) {
        this._latitude = lat;
        this._longitude = lng;
        this._type = 4;
    }

    public MapCoordLatLng(double lat, double lng, int type) {
        this._latitude = lat;
        this._longitude = lng;
        this._type = type;
    }

    public int getType() {
        return this._type;
    }

    public double getLatitude() {
        return this._latitude;
    }

    public double getLongitude() {
        return this._longitude;
    }

    public boolean isUndefined() {
        return this._latitude == -1.0E7d && this._longitude == -1.0E7d;
    }

    public MapCoord toMainCoord() {
        if (MapCoordConstants.MAP_MAIN_COORD_TYPE == 2) {
            return toWcong();
        }
        return toCong();
    }

    private void error(int toType) {
        Log.e(LOG_TAG, "cannot convert " + this._type + " => " + toType + "");
    }

    private MapCoord toMapCoord(MapCoordLatLng cCoord) {
        return new MapCoord(cCoord.getLongitude(), cCoord.getLatitude(), cCoord.getType());
    }

    public MapCoord toCong() {
        if (isUndefined()) {
            return MapCoord.CONG_UNDEFINED;
        }
        NativeMapCoordConverter converter = new NativeMapCoordConverter();
        switch (this._type) {
            case 4:
                return converter.convertMapCoord(toMapCoord(this), 1);
            default:
                error(1);
                return null;
        }
    }

    public MapCoord toWcong() {
        if (isUndefined()) {
            return MapCoord.WCONG_UNDEFINED;
        }
        NativeMapCoordConverter converter = new NativeMapCoordConverter();
        switch (this._type) {
            case 4:
                return converter.convertMapCoord(toMapCoord(this), 2);
            default:
                error(2);
                return null;
        }
    }
}
