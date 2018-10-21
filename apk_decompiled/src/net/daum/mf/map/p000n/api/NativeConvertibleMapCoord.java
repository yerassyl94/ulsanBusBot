package net.daum.mf.map.p000n.api;

import net.daum.android.map.coord.MapCoord;
import net.daum.android.map.coord.MapCoordLatLng;

/* renamed from: net.daum.mf.map.n.api.NativeConvertibleMapCoord */
public class NativeConvertibleMapCoord {
    protected int type = 1;
    /* renamed from: x */
    protected double f17x = 0.0d;
    /* renamed from: y */
    protected double f18y = 0.0d;
    /* renamed from: z */
    protected double f19z = 0.0d;

    public NativeConvertibleMapCoord(MapCoord coord) {
        this.f17x = coord.getX();
        this.f18y = coord.getY();
        this.type = coord.getType();
    }

    public NativeConvertibleMapCoord(MapCoordLatLng coord) {
        this.f17x = coord.getLongitude();
        this.f18y = coord.getLatitude();
        this.type = coord.getType();
    }

    public NativeConvertibleMapCoord(double x, double y, double z, int type) {
        this.f17x = x;
        this.f18y = y;
        this.f19z = z;
        this.type = type;
    }

    public MapCoord toMapCoord() {
        return new MapCoord((double) ((float) this.f17x), (double) ((float) this.f18y), this.type);
    }

    public MapCoordLatLng toMapCoordLatLng() {
        return new MapCoordLatLng(this.f17x, this.f18y, this.type);
    }

    public double getX() {
        return this.f17x;
    }

    public void setX(double x) {
        this.f17x = x;
    }

    public double getY() {
        return this.f18y;
    }

    public void setY(double y) {
        this.f18y = y;
    }

    public double getZ() {
        return this.f19z;
    }

    public void setZ(double z) {
        this.f19z = z;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static NativeConvertibleMapCoord newNativeConvertibleMapCoord(double x, double y, double z, int type) {
        return new NativeConvertibleMapCoord(x, y, z, type);
    }
}
