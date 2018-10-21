package com.neighbor.ulsanbus;

import java.io.Serializable;
import java.util.ArrayList;

public class TransitionBus implements Serializable {
    public ArrayList<Position> mPosition = new ArrayList();
    private String mRouteId;
    private String mStopCnt;
    public ArrayList<String> mStoplist = new ArrayList();
    private String mVertaxCount;
    private String mWalkingLength;
    private String mWalkingTime;

    public void setRouteId(String arg0) {
        this.mRouteId = arg0;
    }

    public void setWalikingTime(String arg0) {
        this.mWalkingTime = arg0;
    }

    public void setWalkingLength(String arg0) {
        this.mWalkingLength = arg0;
    }

    public void setStopCnt(String arg0) {
        this.mStopCnt = arg0;
    }

    public void setVertaxCount(String arg0) {
        this.mVertaxCount = arg0;
    }

    public void addStopId(String arg0) {
        this.mStoplist.add(arg0);
    }

    public void addPosition(Position arg0) {
        this.mPosition.add(arg0);
    }

    public String getRouteId() {
        return this.mRouteId;
    }

    public String getWalikingTime() {
        return this.mWalkingTime;
    }

    public String getWalkingLength() {
        return this.mWalkingLength;
    }

    public String getStopCnt() {
        return this.mStopCnt;
    }

    public String getVertaxCount() {
        return this.mVertaxCount;
    }

    public ArrayList<String> getStoplist() {
        return this.mStoplist;
    }

    public ArrayList<Position> getPositonList() {
        return this.mPosition;
    }
}
