package com.neighbor.ulsanbus;

public class RouteArrival {
    private String BusNo;
    private String BusType;
    private String EmergencyCD;
    private String LowType;
    private String RemainStopCnt;
    private String RemainTime;
    private String Status;
    private String StopID;
    private String StopName;

    public void setBusNo(String arg0) {
        this.BusNo = arg0;
    }

    public void setBusType(String arg0) {
        this.BusType = arg0;
    }

    public void setLowType(String arg0) {
        this.LowType = arg0;
    }

    public void setStatus(String arg0) {
        this.Status = arg0;
    }

    public void setStopId(String arg0) {
        this.StopID = arg0;
    }

    public void setStopNM(String arg0) {
        this.StopName = arg0;
    }

    public void setRemainStop(String arg0) {
        this.RemainStopCnt = arg0;
    }

    public void setRemainTime(String arg0) {
        this.RemainTime = arg0;
    }

    public void setEmerCode(String arg0) {
        this.EmergencyCD = arg0;
    }

    public String getBusNo() {
        return this.BusNo;
    }

    public String getBusType() {
        return this.BusType;
    }

    public String getLowType() {
        return this.LowType;
    }

    public String getStatus() {
        return this.Status;
    }

    public String getStopId() {
        return this.StopID;
    }

    public String getStopNM() {
        return this.StopName;
    }

    public String getRemainStop() {
        return this.RemainStopCnt;
    }

    public String getRemainTime() {
        return this.RemainTime;
    }

    public String getEmerCode() {
        return this.EmergencyCD;
    }
}
