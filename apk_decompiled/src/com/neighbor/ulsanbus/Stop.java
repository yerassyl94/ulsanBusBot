package com.neighbor.ulsanbus;

import java.util.ArrayList;

public class Stop {
    private String ErrorMsg;
    private String IsDelete;
    private String Seq;
    private String StopID;
    private String StopLimousine;
    private String StopName;
    private String StopRemark;
    private String StopX;
    private String StopY;
    private VerTaxInfo mVertaxInfo;
    private int speed;
    private ArrayList<VerTaxInfo> vertaxInfo = new ArrayList();
    private String waypoint;

    public class VerTaxInfo {
        public String verTaxY;
        public String vertaxX;
    }

    public String getWaypoint() {
        return this.waypoint;
    }

    public void setWaypoint(String waypoint) {
        this.waypoint = waypoint;
    }

    public int getSpeed() {
        return this.speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public ArrayList<VerTaxInfo> setVertaxTable() {
        return this.vertaxInfo;
    }

    public VerTaxInfo getVertaxInfo() {
        return new VerTaxInfo();
    }

    public void AddVertaxInfo(VerTaxInfo vertax) {
        this.vertaxInfo.add(vertax);
    }

    public String getStopID() {
        return this.StopID;
    }

    public void setStopID(String stopid) {
        this.StopID = stopid;
    }

    public String getSeq() {
        return this.Seq;
    }

    public void setSeq(String seq) {
        this.Seq = seq;
    }

    public String getStopName() {
        return this.StopName;
    }

    public void setStopName(String stopname) {
        this.StopName = stopname;
    }

    public String getStopLimousine() {
        return this.StopLimousine;
    }

    public void setStopLimousine(String stoplimousine) {
        this.StopLimousine = stoplimousine;
    }

    public String getStopX() {
        return this.StopX;
    }

    public void setStopX(String stopx) {
        this.StopX = stopx;
    }

    public String getStopY() {
        return this.StopY;
    }

    public void setStopY(String stopy) {
        this.StopY = stopy;
    }

    public String getIsDelete() {
        return this.IsDelete;
    }

    public void setIsDelete(String isdelete) {
        this.IsDelete = isdelete;
    }

    public void setErrorMsg(String msg) {
        this.ErrorMsg = msg;
    }

    public String getErrorMsg() {
        return this.ErrorMsg;
    }

    public void setStopRemark(String remark) {
        this.StopRemark = remark;
    }

    public String getStopRemark() {
        return this.StopRemark;
    }

    public String toString() {
        return "Stop [StopID=" + this.StopID + ", Seq=" + this.Seq + ", StopName=" + this.StopName + ", StopLimousine=" + this.StopLimousine + ", StopX=" + this.StopX + ", StopY=" + this.StopY + ", IsDelete=" + this.IsDelete + ", ErrorMsg=" + this.ErrorMsg + ", StopRemark=" + this.StopRemark + ", speed=" + this.speed + ", waypoint=" + this.waypoint + ", mVertaxInfo=" + this.mVertaxInfo + ", vertaxInfo=" + this.vertaxInfo + "]";
    }
}
