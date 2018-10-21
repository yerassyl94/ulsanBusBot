package net.daum.mf.map.api;

import net.daum.mf.map.api.MapPoint.PlainCoordinate;

public class MapCircle {
    private MapPoint center;
    private int fillColor;
    private int id;
    private int radius;
    private int strokeColor;
    private int tag;

    public MapCircle(MapPoint center, int radius, int strokeColor, int fillColor) {
        this.radius = radius;
        this.center = center;
        this.strokeColor = strokeColor;
        this.fillColor = fillColor;
    }

    public int getStrokeColor() {
        return this.strokeColor;
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }

    public int getFillColor() {
        return this.fillColor;
    }

    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
    }

    public MapPoint getCenter() {
        return this.center;
    }

    public int getRadius() {
        return this.radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setCenter(MapPoint center) {
        this.center = center;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTag() {
        return this.tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public MapPointBounds getBound() {
        mapPoints = new MapPoint[2];
        PlainCoordinate point = this.center.getMapPointWCONGCoord();
        mapPoints[0] = MapPoint.mapPointWithWCONGCoord(point.f13x - (((double) this.radius) * 2.5d), point.f14y - (((double) this.radius) * 2.5d));
        mapPoints[1] = MapPoint.mapPointWithWCONGCoord(point.f13x + (((double) this.radius) * 2.5d), point.f14y + (((double) this.radius) * 2.5d));
        return new MapPointBounds(mapPoints);
    }
}
