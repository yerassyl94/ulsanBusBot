package net.daum.mf.map.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapPointBounds {
    public MapPoint bottomLeft;
    public MapPoint topRight;

    public void add(MapPoint point) {
        if (this.bottomLeft == null) {
            this.bottomLeft = point;
        }
        if (this.topRight == null) {
            this.topRight = point;
        }
        calcBounds(new MapPoint[]{this.bottomLeft, this.topRight, point});
    }

    public MapPointBounds(MapPoint bottomLeft, MapPoint topRight) {
        this.bottomLeft = bottomLeft;
        this.topRight = topRight;
    }

    public MapPointBounds(MapPoint[] mapPointArray) {
        calcBounds(mapPointArray);
    }

    public MapPointBounds(MapPointBounds[] mapPointBoundsArray) {
        List<MapPoint> mapPointList = new ArrayList();
        for (MapPointBounds mapPointBounds : mapPointBoundsArray) {
            mapPointList.add(mapPointBounds.bottomLeft);
            mapPointList.add(mapPointBounds.topRight);
        }
        calcBounds((MapPoint[]) mapPointList.toArray(new MapPoint[0]));
    }

    private void calcBounds(MapPoint[] mapPoints) {
        if (mapPoints == null || mapPoints.length == 0) {
            this.bottomLeft = MapPoint.mapPointWithWCONGCoord(-1.0E100d, -1.0E100d);
            this.topRight = MapPoint.mapPointWithWCONGCoord(1.0E100d, 1.0E100d);
            return;
        }
        double left = 1.0E100d;
        double right = -1.0E100d;
        double bottom = 1.0E100d;
        double top = -1.0E100d;
        for (MapPoint point : mapPoints) {
            double x = point.getInternalCoordObject().getX();
            double y = point.getInternalCoordObject().getY();
            left = Math.min(left, x);
            right = Math.max(right, x);
            bottom = Math.min(bottom, y);
            top = Math.max(top, y);
        }
        this.bottomLeft = MapPoint.mapPointWithWCONGCoord(left, bottom);
        this.topRight = MapPoint.mapPointWithWCONGCoord(right, top);
    }

    public boolean contains(MapPoint mapPoint) {
        latBounds = new double[2];
        double[] lngBounds = new double[]{this.bottomLeft.getMapPointGeoCoord().latitude, this.topRight.getMapPointGeoCoord().latitude};
        Arrays.sort(latBounds);
        lngBounds[0] = this.bottomLeft.getMapPointGeoCoord().longitude;
        lngBounds[1] = this.topRight.getMapPointGeoCoord().longitude;
        Arrays.sort(lngBounds);
        double lat = mapPoint.getMapPointGeoCoord().latitude;
        double lng = mapPoint.getMapPointGeoCoord().longitude;
        if (latBounds[0] > lat || lat > latBounds[1] || lngBounds[0] > lng || lng > lngBounds[1]) {
            return false;
        }
        return true;
    }

    public MapPoint getCenter() {
        return MapPoint.mapPointWithGeoCoord((this.bottomLeft.getMapPointGeoCoord().latitude + this.topRight.getMapPointGeoCoord().latitude) / 2.0d, (this.bottomLeft.getMapPointGeoCoord().longitude + this.topRight.getMapPointGeoCoord().longitude) / 2.0d);
    }
}
