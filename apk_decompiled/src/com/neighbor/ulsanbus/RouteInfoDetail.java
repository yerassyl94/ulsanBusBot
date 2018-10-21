package com.neighbor.ulsanbus;

public class RouteInfoDetail {
    private String ErrorMsg;
    private String FStopName;
    private String Intreval;
    private String TStopName;
    private String TravelTime;
    private String busCompany;
    private String busType;
    private String routeId;
    private String routeNo;
    private String routeType;

    public void setRouteId(String routeid) {
        this.routeId = routeid;
    }

    public void setRouteNo(String routeNo) {
        this.routeNo = routeNo;
    }

    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }

    public void setBusType(String busType) {
        this.busType = busType;
    }

    public void setCompany(String companyName) {
        this.busCompany = companyName;
    }

    public void setFStopName(String fstopName) {
        this.FStopName = fstopName;
    }

    public void setTStopName(String tstopName) {
        this.TStopName = tstopName;
    }

    public void setInterval(String interval) {
        this.Intreval = interval;
    }

    public void setTravelTime(String travelTime) {
        this.TravelTime = travelTime;
    }

    public void setErrorMsg(String err) {
        this.ErrorMsg = err;
    }

    public String getRouteId() {
        return this.routeId;
    }

    public String getRouteNo() {
        return this.routeNo;
    }

    public String getRouteType() {
        return this.routeType;
    }

    public String getBusType() {
        return this.busType;
    }

    public String getBusCompany() {
        return this.busCompany;
    }

    public String getFStopName() {
        return this.FStopName;
    }

    public String getTStopName() {
        return this.TStopName;
    }

    public String getInterval() {
        return this.Intreval;
    }

    public String getTravelTime() {
        return this.TravelTime;
    }

    public String getErrorMsg() {
        return this.ErrorMsg;
    }
}
