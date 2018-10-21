package net.daum.mf.map.api;

import android.support.v4.internal.view.SupportMenu;
import java.util.ArrayList;

public class MapPolyline {
    private int id;
    private int lineColor;
    private ArrayList<MapPoint> mapPointList;
    private int tag;

    public MapPolyline() {
        this.mapPointList = new ArrayList();
        this.lineColor = SupportMenu.CATEGORY_MASK;
        this.tag = 0;
        this.id = -1;
    }

    public MapPolyline(int pointListCapacity) {
        this.mapPointList = new ArrayList(pointListCapacity);
        this.lineColor = SupportMenu.CATEGORY_MASK;
        this.tag = 0;
    }

    public Object[] getObjects() {
        return this.mapPointList.toArray();
    }

    public MapPoint[] getMapPoints() {
        return (MapPoint[]) this.mapPointList.toArray(new MapPoint[0]);
    }

    public int getPointCount() {
        return this.mapPointList.size();
    }

    public MapPoint getPoint(int index) {
        return (MapPoint) this.mapPointList.get(index);
    }

    public void addPoint(MapPoint mapPoint) {
        this.mapPointList.add(mapPoint);
    }

    public void addPoints(MapPoint[] mapPoints) {
        for (Object add : mapPoints) {
            this.mapPointList.add(add);
        }
    }

    public int getLineColor() {
        return this.lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public int getTag() {
        return this.tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    int getId() {
        return this.id;
    }

    void setId(int id) {
        this.id = id;
    }
}
