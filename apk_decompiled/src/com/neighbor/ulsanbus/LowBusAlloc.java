package com.neighbor.ulsanbus;

import android.util.Log;

public class LowBusAlloc {
    private String ErrorMsg;
    private String RouteID;
    private String RouteNo;
    private String Seq;
    private String Time;

    public void setRouteID(String routeid) {
        this.RouteID = routeid;
    }

    public String getRouteID() {
        return this.RouteID;
    }

    public void setRouteNo(String routeno) {
        this.RouteNo = routeno;
    }

    public String getRouteNo() {
        return this.RouteNo;
    }

    public void setSeq(String seq) {
        this.Seq = seq;
    }

    public String getSeq() {
        return this.Seq;
    }

    public void setTime(String time) {
        this.Time = time;
    }

    public String getTime() {
        return this.Time;
    }

    public void setErrorMsg(String msg) {
        this.ErrorMsg = msg;
    }

    public String getErrorMsg() {
        return this.ErrorMsg;
    }

    public void toLogLowFloor() {
        Log.d("Low", "RouteID : " + this.RouteID);
        Log.d("Low", "RouteNo : " + this.RouteNo);
        Log.d("Low", "Seq : " + this.Seq);
        Log.d("Low", "Time : " + this.Time);
        Log.d("Low", "ErrorMsg : " + this.ErrorMsg);
    }
}
