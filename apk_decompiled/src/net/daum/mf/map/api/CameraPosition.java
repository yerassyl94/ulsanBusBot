package net.daum.mf.map.api;

public class CameraPosition {
    public final float bearing = 0.0f;
    public final MapPoint target;
    public final float tilt = 0.0f;
    public final float zoomLevel;

    public CameraPosition(MapPoint target, float zoomLevel) {
        this.target = target;
        this.zoomLevel = zoomLevel;
    }
}
