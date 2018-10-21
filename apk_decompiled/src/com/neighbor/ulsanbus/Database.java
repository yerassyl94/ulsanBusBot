package com.neighbor.ulsanbus;

import android.util.Log;

public class Database {
    private String Emergency_Mode;
    private String Emergency_Ver;
    private String ErrorMsg;
    private String Notice_Ver;
    private int Notice_Ver_Internal = 1;
    private String Route_Ver;
    private String Stop_Ver;
    private String TCardStore;
    private int TCardStore_Internal = 1;
    private String Time_Ver;

    public void setInternal_Notice(int notice) {
        this.Notice_Ver_Internal = notice;
    }

    public int getInternal_Notice() {
        return this.Notice_Ver_Internal;
    }

    public void setInternal_TcardStore(int tcard) {
        this.TCardStore_Internal = tcard;
    }

    public int getInternal_TcardStore() {
        return this.TCardStore_Internal;
    }

    public String getRoute_Ver() {
        return this.Route_Ver;
    }

    public void setRoute_Ver(String route_ver) {
        this.Route_Ver = route_ver;
    }

    public String getStop_Ver() {
        return this.Stop_Ver;
    }

    public void setStop_Ver(String stop_ver) {
        this.Stop_Ver = stop_ver;
    }

    public String getTime_Ver() {
        return this.Time_Ver;
    }

    public void setTime_Ver(String time_ver) {
        this.Time_Ver = time_ver;
    }

    public String getNotice_Ver() {
        return this.Notice_Ver;
    }

    public void setNotice_Ver(String notice_ver) {
        this.Notice_Ver = notice_ver;
    }

    public String getEmergency_Ver() {
        return this.Emergency_Ver;
    }

    public void setEmergency_Ver(String emergency_ver) {
        this.Emergency_Ver = emergency_ver;
    }

    public String getEmergency_Mode() {
        return this.Emergency_Mode;
    }

    public void setEmergency_Mode(String emergencymode) {
        this.Emergency_Mode = emergencymode;
    }

    public String getTCardStore() {
        return this.TCardStore;
    }

    public void setTCardStore(String tcardstore) {
        this.TCardStore = tcardstore;
    }

    public void setErrorMsg(String msg) {
        this.ErrorMsg = msg;
    }

    public String getErrorMsg() {
        return this.ErrorMsg;
    }

    public void toLogDBVersion() {
        Log.d("DBVersion", "Route_Ver : " + this.Route_Ver);
        Log.d("DBVersion", "Stop_Ver : " + this.Stop_Ver);
        Log.d("DBVersion", "Time_Ver : " + this.Time_Ver);
        Log.d("DBVersion", "Notice_Ver : " + this.Notice_Ver);
        Log.d("DBVersion", "Emergency_Ver : " + this.Emergency_Ver);
        Log.d("DBVersion", "Emergency_Mode : " + this.Emergency_Mode);
        Log.d("DBVersion", "ErrorMsg : " + this.ErrorMsg);
    }
}
