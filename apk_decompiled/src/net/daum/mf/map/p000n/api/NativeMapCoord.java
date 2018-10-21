package net.daum.mf.map.p000n.api;

import net.daum.android.map.coord.MapCoord;
import net.daum.android.map.coord.MapCoordConstants;

/* renamed from: net.daum.mf.map.n.api.NativeMapCoord */
public class NativeMapCoord {
    protected int type = MapCoordConstants.MAP_MAIN_COORD_TYPE;
    /* renamed from: x */
    protected double f20x = 0.0d;
    /* renamed from: y */
    protected double f21y = 0.0d;

    public NativeMapCoord(MapCoord coord) {
        this.f20x = coord.getX();
        this.f21y = coord.getY();
        this.type = coord.getType();
    }

    public NativeMapCoord(int type) {
        this.type = type;
    }

    public NativeMapCoord(double x, double y) {
        this.f20x = x;
        this.f21y = y;
    }

    public NativeMapCoord(double x, double y, int type) {
        this.f20x = x;
        this.f21y = y;
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getX() {
        return this.f20x;
    }

    public void setX(double x) {
        this.f20x = x;
    }

    public double getY() {
        return this.f21y;
    }

    public void setY(double y) {
        this.f21y = y;
    }

    public MapCoord toMapCoord() {
        return new MapCoord(this.f20x, this.f21y, this.type);
    }
}
