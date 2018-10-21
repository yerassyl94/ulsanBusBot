package net.daum.android.map.coord;

import android.util.Log;
import net.daum.android.map.util.URLEncoder;
import net.daum.android.map.util.XorEncryptionUtils;
import net.daum.mf.map.p000n.api.NativeMapCoordConverter;

public class MapCoord {
    public static final MapCoord CONG_UNDEFINED = new MapCoord(-1.0E7d, -1.0E7d, 1);
    private static final String LOG_TAG = "MapCoord";
    public static final MapCoord UNDEFINED = new MapCoord(-1.0E7d, -1.0E7d);
    public static final MapCoord WCONG_UNDEFINED = new MapCoord(-1.0E7d, -1.0E7d, 2);
    public static final MapCoord ZERO = new MapCoord(0.0d, 0.0d);
    private static String _encKey = "yeKmuaDeliboM";
    protected int _type = MapCoordConstants.MAP_MAIN_COORD_TYPE;
    protected double _x = 0.0d;
    protected double _y = 0.0d;

    public MapCoord(int type) {
        this._type = type;
    }

    public MapCoord(double x, double y) {
        this._x = x;
        this._y = y;
    }

    public MapCoord(double x, double y, int type) {
        this._x = x;
        this._y = y;
        this._type = type;
    }

    public int getType() {
        return this._type;
    }

    public double getX() {
        return this._x;
    }

    public double getY() {
        return this._y;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        boolean result = false;
        if (other instanceof MapCoord) {
            MapCoord that = (MapCoord) other;
            if (getType() == that.getType() && getX() == that.getX() && getY() == that.getY()) {
                result = true;
            } else {
                result = false;
            }
        }
        return result;
    }

    public int hashCode() {
        return ((int) ((this._x * 141.431d) + (this._y * 11.41d))) + (this._type * 41);
    }

    public boolean isUndefined() {
        return this._x == -1.0E7d && this._y == -1.0E7d;
    }

    public MapCoord toMainCoord() {
        if (MapCoordConstants.MAP_MAIN_COORD_TYPE == 2) {
            return toWcong();
        }
        return toCong();
    }

    public MapCoordLatLng toMapCoordLatLng(MapCoord cCoord) {
        return new MapCoordLatLng(cCoord._y, cCoord._x, cCoord._type);
    }

    public MapCoord toCong() {
        if (this._type == 1) {
            return this;
        }
        if (isUndefined()) {
            return CONG_UNDEFINED;
        }
        NativeMapCoordConverter converter = new NativeMapCoordConverter();
        MapCoord newCoord = null;
        switch (this._type) {
            case 2:
            case 3:
            case 4:
                newCoord = converter.convertMapCoord(this, 1);
                break;
            default:
                error(1);
                break;
        }
        return newCoord;
    }

    public MapCoord toWcong() {
        if (this._type == 2) {
            return this;
        }
        if (isUndefined()) {
            return WCONG_UNDEFINED;
        }
        NativeMapCoordConverter converter = new NativeMapCoordConverter();
        MapCoord newCoord = null;
        switch (this._type) {
            case 1:
            case 3:
            case 4:
                newCoord = converter.convertMapCoord(this, 2);
                break;
            default:
                error(2);
                break;
        }
        return newCoord;
    }

    public MapCoordLatLng toWgs() {
        if (isUndefined()) {
            return MapCoordLatLng.WGS84_UNDEFINED;
        }
        NativeMapCoordConverter converter = new NativeMapCoordConverter();
        MapCoord newCoord = null;
        switch (this._type) {
            case 1:
            case 2:
                newCoord = converter.convertMapCoord(this, 4);
                break;
            default:
                error(4);
                break;
        }
        return toMapCoordLatLng(newCoord);
    }

    public MapCoord addCoord(MapCoord coord) {
        return new MapCoord(this._x + coord.getX(), this._y + coord.getY());
    }

    public static void setKey(String key) {
        _encKey = key;
    }

    protected String getEncryptedCoord(int coord) {
        return URLEncoder.encode(XorEncryptionUtils.encryptString(String.valueOf(coord), _encKey));
    }

    public String getEncryptedX() {
        return getEncryptedCoord((int) getX());
    }

    public String getEncryptedY() {
        return getEncryptedCoord((int) getY());
    }

    private void error(int toType) {
        Log.e(LOG_TAG, "cannot convert " + this._type + " => " + toType + "");
    }
}
