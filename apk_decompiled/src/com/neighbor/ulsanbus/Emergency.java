package com.neighbor.ulsanbus;

import android.util.Log;

public class Emergency {
    private String EndDate;
    private String ErrorMsg;
    private String RouteCnt;
    private String RouteID;
    private String StartDate;

    public void setRouteCnt(String Routecnt) {
        this.RouteCnt = Routecnt;
    }

    public String getRouteCnt() {
        return this.RouteCnt;
    }

    public void setRouteID(String Routeid) {
        this.RouteID = Routeid;
    }

    public String getRouteID() {
        return this.RouteID;
    }

    public void setStartDate(String startdate) {
        this.StartDate = startdate;
    }

    public String getStartDate() {
        return this.StartDate;
    }

    public void setEndDate(String enddate) {
        this.EndDate = enddate;
    }

    public String getEndDate() {
        return this.EndDate;
    }

    public void setErrorMsg(String msg) {
        this.ErrorMsg = msg;
    }

    public String getErrorMsg() {
        return this.ErrorMsg;
    }

    public void toLogRouteArrival() {
        Log.d("Emergency", "RouteCnt : " + this.RouteCnt);
        Log.d("Emergency", "RouteID : " + this.RouteID);
        Log.d("Emergency", "StartDate : " + this.StartDate);
        Log.d("Emergency", "EndDate : " + this.EndDate);
        Log.d("Emergency", "ErrorMsg : " + this.ErrorMsg);
    }
}
