package com.neighbor.ulsanbus;

import java.util.ArrayList;

public class DirectTable {
    private String mLength;
    private ArrayList<Position> mPositionlist = new ArrayList();
    private String mRouteId;
    private String mStopCnt;
    private ArrayList<String> mStoplist = new ArrayList();
    private String mTravelTime;
    private String mVertaxCnt;

    public void setRouteId(String arg0) {
        this.mRouteId = arg0;
    }

    public void setTravelTime(String arg0) {
        this.mTravelTime = arg0;
    }

    public void setLength(String arg0) {
        this.mLength = arg0;
    }

    public void setStopCNT(String arg0) {
        this.mStopCnt = arg0;
    }

    public void setVertaxCNT(String arg0) {
        this.mVertaxCnt = arg0;
    }

    public void addStop(String arg0) {
        this.mStoplist.add(arg0);
    }

    public void addPosition(Position arg0) {
        this.mPositionlist.add(arg0);
    }

    public String getRouteId() {
        return this.mRouteId;
    }

    public String getTravelTime() {
        return this.mTravelTime;
    }

    public String getLength() {
        return this.mLength;
    }

    public String getStopCNT() {
        return this.mStopCnt;
    }

    public String getVertaxCNT() {
        return this.mVertaxCnt;
    }

    public ArrayList<String> getStoplist() {
        return this.mStoplist;
    }

    public ArrayList<Position> getPositionlist() {
        return this.mPositionlist;
    }
}
