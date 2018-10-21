package net.daum.mf.map.api;

public class MapCurrentLocationMarker {
    private float directionImageAnchorRatioX = 0.5f;
    private float directionImageAnchorRatioY = 0.5f;
    private int directionImageId;
    private int radius = 0;
    private int radiusFillColor;
    private int radiusStrokeColor;
    private float trackingAnimationDuration = 1.0f;
    private float[] trackingAnimationImageAnchorRatioX;
    private float[] trackingAnimationImageAnchorRatioY;
    private int[] trackingAnimationImageIds;
    private float trackingOffImageAnchorRatioX = 0.5f;
    private float trackingOffImageAnchorRatioY = 0.5f;
    private int trackingOffImageId;

    public int[] getTrackingAnimationImageIds() {
        return this.trackingAnimationImageIds;
    }

    public void setTrackingAnimationImageIds(int[] trackingAnimationImageIds) {
        this.trackingAnimationImageIds = trackingAnimationImageIds;
    }

    public float getTrackingAnimationDuration() {
        return this.trackingAnimationDuration;
    }

    public void setTrackingAnimationDuration(float trackingAnimationDuration) {
        this.trackingAnimationDuration = trackingAnimationDuration;
    }

    public void setTrackingAnimationImageAnchors(float[] x, float[] y) {
        this.trackingAnimationImageAnchorRatioX = x;
        this.trackingAnimationImageAnchorRatioY = y;
    }

    public int getTrackingOffImageId() {
        return this.trackingOffImageId;
    }

    public void setTrackingOffImageId(int trackingOffImageId) {
        this.trackingOffImageId = trackingOffImageId;
    }

    public void setTrackingOffImageAnchor(float x, float y) {
        this.trackingOffImageAnchorRatioX = x;
        this.trackingOffImageAnchorRatioY = y;
    }

    public int getDirectionImageId() {
        return this.directionImageId;
    }

    public void setDirectionImageId(int directionImageId) {
        this.directionImageId = directionImageId;
    }

    public void setDirectionImageAnchor(float x, float y) {
        this.directionImageAnchorRatioX = x;
        this.directionImageAnchorRatioY = y;
    }

    public int getRadius() {
        return this.radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getRadiusStrokeColor() {
        return this.radiusStrokeColor;
    }

    public void setRadiusStrokeColor(int radiusStrokeColor) {
        this.radiusStrokeColor = radiusStrokeColor;
    }

    public int getRadiusFillColor() {
        return this.radiusFillColor;
    }

    public void setRadiusFillColor(int radiusFillColor) {
        this.radiusFillColor = radiusFillColor;
    }

    public float getTrackingOffImageAnchorRatioX() {
        return this.trackingOffImageAnchorRatioX;
    }

    public float getTrackingOffImageAnchorRatioY() {
        return this.trackingOffImageAnchorRatioY;
    }

    public float getDirectionImageAnchorRatioX() {
        return this.directionImageAnchorRatioX;
    }

    public float getDirectionImageAnchorRatioY() {
        return this.directionImageAnchorRatioY;
    }

    public float[] getTrackingAnimationImageAnchorRatioX() {
        return this.trackingAnimationImageAnchorRatioX;
    }

    public float[] getTrackingAnimationImageAnchorRatioY() {
        return this.trackingAnimationImageAnchorRatioY;
    }
}
