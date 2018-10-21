package com.neighbor.ulsanbus;

import android.util.Log;

public class BusLocation {
    private String BusAngle;
    private String BusCnt;
    private String BusNo;
    private String BusStopSeq;
    private String BusType;
    private String BusX;
    private String BusY;
    private String EmergencyCD;
    private String EmergencyMode;
    private String Emergency_ver;
    private String ErrorMsg;
    private String LowType;
    private String RouteID;
    private String Seq;
    private String StopID;
    private String StopName;
    private int inOutFlag;
    private int speed;

    public int getSpeed() {
        return this.speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getInOutFlag() {
        return this.inOutFlag;
    }

    public void setInOutFlag(int inOutFlag) {
        this.inOutFlag = inOutFlag;
    }

    public String getRouteID() {
        return this.RouteID;
    }

    public void setRouteID(String routeid) {
        this.RouteID = routeid;
    }

    public String getSeq() {
        return this.Seq;
    }

    public void setSeq(String seq) {
        this.Seq = seq;
    }

    public String getBusCnt() {
        return this.BusCnt;
    }

    public void setBusCnt(String buscnt) {
        this.BusCnt = buscnt;
    }

    public String getBusNo() {
        return this.BusNo;
    }

    public void setBusNo(String busno) {
        this.BusNo = busno;
    }

    public String getBusType() {
        return this.BusType;
    }

    public void setBusType(String bustype) {
        this.BusType = bustype;
    }

    public String getLowType() {
        return this.LowType;
    }

    public void setLowType(String lowtype) {
        this.LowType = lowtype;
    }

    public String getStopID() {
        return this.StopID;
    }

    public void setStopID(String stopid) {
        this.StopID = stopid;
    }

    public String getBusAngle() {
        return this.BusAngle;
    }

    public void setBusAngle(String busangle) {
        this.BusAngle = busangle;
    }

    public String getStopName() {
        return this.StopName;
    }

    public void setStopName(String stopname) {
        this.StopName = stopname;
    }

    public String getBusX() {
        return this.BusX;
    }

    public void setBusX(String busx) {
        this.BusX = busx;
    }

    public String getBusY() {
        return this.BusY;
    }

    public void setBusY(String busy) {
        this.BusY = busy;
    }

    public String getEmergency_ver() {
        return this.Emergency_ver;
    }

    public void setEmergency_ver(String Emergency_ver) {
        this.Emergency_ver = Emergency_ver;
    }

    public String getEmergencyMode() {
        return this.EmergencyMode;
    }

    public void setEmergencyMode(String EmergencyMode) {
        this.EmergencyMode = EmergencyMode;
    }

    public String getEmergencyCD() {
        return this.EmergencyCD;
    }

    public void setEmergencyCD(String Emergencycd) {
        this.EmergencyCD = Emergencycd;
    }

    public String getBusStopSeq() {
        return this.BusStopSeq;
    }

    public void setBusStopSeq(String busStopSeq) {
        this.BusStopSeq = busStopSeq;
    }

    public void setErrorMsg(String msg) {
        this.ErrorMsg = msg;
    }

    public String getErrorMsg() {
        return this.ErrorMsg;
    }

    public void toLogBusLocation() {
        Log.d("BusLocation", "RouteID : " + this.RouteID);
        Log.d("BusLocation", "BustCnt : " + this.BusCnt);
        Log.d("BusLocation", "BusNo : " + this.BusNo);
        Log.d("BusLocation", "BusType : " + this.BusType);
        Log.d("BusLocation", "LowType : " + this.LowType);
        Log.d("BusLocation", "StopID : " + this.StopID);
        Log.d("BusLocation", "BusAngle : " + this.BusAngle);
        Log.d("BusLocation", "StopName : " + this.StopName);
        Log.d("BusLocation", "BusX : " + this.BusX);
        Log.d("BusLocation", "BusY : " + this.BusY);
        Log.d("BusLocation", "EmergencyCD : " + this.EmergencyCD);
        Log.d("BusLocation", "ErrorMsg : " + this.ErrorMsg);
    }
}
