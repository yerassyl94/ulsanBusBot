package com.neighbor.ulsanbus;

import java.util.ArrayList;

public class BusRoute {
    private String mRouteId;
    private ArrayList<String> mStops = new ArrayList();

    public void setRouteId(String routeId) {
        this.mRouteId = routeId;
    }

    public void addStop(String stop) {
        this.mStops.add(stop);
    }

    public ArrayList<String> getStops() {
        return this.mStops;
    }

    public String getRouteId() {
        return this.mRouteId;
    }
}
