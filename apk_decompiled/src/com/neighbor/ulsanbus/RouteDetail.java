package com.neighbor.ulsanbus;

import android.util.Log;

public class RouteDetail {
    private String CompanyName;
    private String CompanyPhone;
    private String EndTime;
    private String ErrorMsg;
    private String MaxInterval;
    private String MinInterval;
    private String RouteID;
    private String StartTime;

    public String getRouteID() {
        return this.RouteID;
    }

    public void setRouteID(String routeid) {
        this.RouteID = routeid;
    }

    public String getStartTime() {
        return this.StartTime;
    }

    public void setStartTime(String start_time) {
        this.StartTime = start_time;
    }

    public String getEndTime() {
        return this.EndTime;
    }

    public void setEndTime(String end_time) {
        this.EndTime = end_time;
    }

    public String getMinInterval() {
        return this.MinInterval;
    }

    public void setMinInterval(String min_interval) {
        this.MinInterval = min_interval;
    }

    public String getMaxInterval() {
        return this.MaxInterval;
    }

    public void setMaxInterval(String max_interval) {
        this.MaxInterval = max_interval;
    }

    public String getCompanyName() {
        return this.CompanyName;
    }

    public void setCompanyName(String company_name) {
        this.CompanyName = company_name;
    }

    public String getCompanyPhone() {
        return this.CompanyPhone;
    }

    public void setCompanyPhone(String company_phone) {
        this.CompanyPhone = company_phone;
    }

    public void setErrorMsg(String msg) {
        this.ErrorMsg = msg;
    }

    public String getErrorMsg() {
        return this.ErrorMsg;
    }

    public void toLogRouteDetail() {
        Log.d("Choi", "RouteID : " + this.RouteID);
        Log.d("Choi", "StartTime : " + this.StartTime);
        Log.d("Choi", "EndTime : " + this.EndTime);
        Log.d("Choi", "MinInterval : " + this.MinInterval);
        Log.d("Choi", "MaxInterval : " + this.MaxInterval);
        Log.d("Choi", "CompanyName : " + this.CompanyName);
        Log.d("Choi", "CompanyPhone : " + this.CompanyPhone);
        Log.d("Choi", "ErrorMsg : " + this.ErrorMsg);
    }
}
