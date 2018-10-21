package com.neighbor.ulsanbus;

import android.util.Log;

public class Route {
    private String BusCompany;
    private String BusType;
    private String CompanyTel;
    private String ErrorMsg;
    private String FstopName;
    private String Interval;
    private String IsDelete;
    private String Length;
    private String OperationCnt;
    private String Remark = "";
    private String RevRouteId;
    private String RouteDir;
    private String RouteID;
    private String RouteNo;
    private String RouteType;
    private String Seq;
    private String TravelTime;
    private String TstopName;
    private String WD_End_Time;
    private String WD_Max_Interval;
    private String WD_Min_Interval;
    private String WD_Start_Time;
    private String WE_End_Time;
    private String WE_Max_Interval;
    private String WE_Min_Interval;
    private String WE_Start_Time;
    private String WS_End_Time;
    private String WS_Max_Interval;
    private String WS_Min_Interval;
    private String WS_Start_Time;
    private String brtTimeFlag;
    private String brt_app_remark;
    private String brt_app_waypoint_desc;
    private String brt_class_seqno;
    private String total;

    public String getRevRouteId() {
        return this.RevRouteId;
    }

    public void setRevRouteId(String revRouteId) {
        this.RevRouteId = revRouteId;
    }

    public String getSeq() {
        return this.Seq;
    }

    public void setSeq(String seq) {
        this.Seq = seq;
    }

    public String getRouteID() {
        return this.RouteID;
    }

    public void setRouteID(String routeid) {
        this.RouteID = routeid;
    }

    public String getRouteNo() {
        return this.RouteNo;
    }

    public void setRouteNo(String routeno) {
        this.RouteNo = routeno;
    }

    public String getRouteDir() {
        return this.RouteDir;
    }

    public void setRouteDir(String routedir) {
        this.RouteDir = routedir;
    }

    public String getRouteType() {
        return this.RouteType;
    }

    public void setRouteType(String routetype) {
        this.RouteType = routetype;
    }

    public String getBusType() {
        return this.BusType;
    }

    public void setBusType(String bustype) {
        this.BusType = bustype;
    }

    public String getBusCompany() {
        return this.BusCompany;
    }

    public void setBusCompany(String buscompany) {
        this.BusCompany = buscompany;
    }

    public String getFstopName() {
        return this.FstopName;
    }

    public void setFstopName(String fstopname) {
        this.FstopName = fstopname;
    }

    public String getTstopName() {
        return this.TstopName;
    }

    public void setTstopName(String tstopname) {
        this.TstopName = tstopname;
    }

    public String getRemark() {
        return this.Remark;
    }

    public void setRemark(String remark) {
        this.Remark = remark;
    }

    public String getWD_Start_Time() {
        return this.WD_Start_Time;
    }

    public void setWD_Start_Time(String wd_start_time) {
        this.WD_Start_Time = wd_start_time;
    }

    public String getWD_End_Time() {
        return this.WD_End_Time;
    }

    public void setWD_End_Time(String wd_end_time) {
        this.WD_End_Time = wd_end_time;
    }

    public String getWD_Max_Interval() {
        return this.WD_Max_Interval;
    }

    public void setWD_Max_Interval(String wd_max_interval) {
        this.WD_Max_Interval = wd_max_interval;
    }

    public String getWD_Min_Interval() {
        return this.WD_Min_Interval;
    }

    public void setWD_Min_Interval(String wd_min_interval) {
        this.WD_Min_Interval = wd_min_interval;
    }

    public String getWE_Start_Time() {
        return this.WE_Start_Time;
    }

    public void setWE_Start_Time(String we_start_time) {
        this.WE_Start_Time = we_start_time;
    }

    public String getWE_End_Time() {
        return this.WE_End_Time;
    }

    public void setWE_End_Time(String we_end_time) {
        this.WE_End_Time = we_end_time;
    }

    public String getWE_Max_Interval() {
        return this.WE_Max_Interval;
    }

    public void setWE_Max_Interval(String we_max_interval) {
        this.WE_Max_Interval = we_max_interval;
    }

    public String getWE_Min_Interval() {
        return this.WE_Min_Interval;
    }

    public void setWE_Min_Interval(String we_min_interval) {
        this.WE_Min_Interval = we_min_interval;
    }

    public String getWS_Start_Time() {
        return this.WS_Start_Time;
    }

    public void setWS_Start_Time(String ws_start_time) {
        this.WS_Start_Time = ws_start_time;
    }

    public String getWS_End_Time() {
        return this.WS_End_Time;
    }

    public void setWS_End_Time(String ws_end_time) {
        this.WS_End_Time = ws_end_time;
    }

    public String getWS_Max_Interval() {
        return this.WS_Max_Interval;
    }

    public void setWS_Max_Interval(String ws_max_interval) {
        this.WS_Max_Interval = ws_max_interval;
    }

    public String getWS_Min_Interval() {
        return this.WS_Min_Interval;
    }

    public void setWS_Min_Interval(String ws_min_interval) {
        this.WS_Min_Interval = ws_min_interval;
    }

    public String getTravelTime() {
        return this.TravelTime;
    }

    public void setTravelTime(String traveltime) {
        this.TravelTime = traveltime;
    }

    public String getInterval() {
        return this.Interval;
    }

    public void setInterval(String interval) {
        this.Interval = interval;
    }

    public String getLength() {
        return this.Length;
    }

    public void setLength(String length) {
        this.Length = length;
    }

    public String getOperationCnt() {
        return this.OperationCnt;
    }

    public void setOperationCnt(String operationcnt) {
        this.OperationCnt = operationcnt;
    }

    public String getCompanyTel() {
        return this.CompanyTel;
    }

    public void setCompanyTel(String companytel) {
        this.CompanyTel = companytel;
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

    public String getTotalCnt() {
        return this.total;
    }

    public void setTotalCnt(String cnt) {
        this.total = cnt;
    }

    public String getBrt_app_remark() {
        return this.brt_app_remark;
    }

    public void setBrt_app_remark(String brt_app_remark) {
        this.brt_app_remark = brt_app_remark;
    }

    public String getBrt_app_waypoint_desc() {
        return this.brt_app_waypoint_desc;
    }

    public void setBrt_app_waypoint_desc(String brt_app_waypoint_desc) {
        this.brt_app_waypoint_desc = brt_app_waypoint_desc;
    }

    public String getBrtTimeFlag() {
        return this.brtTimeFlag;
    }

    public void setBrtTimeFlag(String brtTimeFlag) {
        this.brtTimeFlag = brtTimeFlag;
    }

    public String getBrt_class_seqno() {
        return this.brt_class_seqno;
    }

    public void setBrt_class_seqno(String brt_class_seqno) {
        this.brt_class_seqno = brt_class_seqno;
    }

    public void toLogRoute() {
        Log.d("Route", "Seq : " + this.Seq);
        Log.d("Route", "RouteID : " + this.RouteID);
        Log.d("Route", "RouteNo : " + this.RouteNo);
        Log.d("Route", "RouteDir : " + this.RouteDir);
        Log.d("Route", "RouteType : " + this.RouteType);
        Log.d("Route", "BusType : " + this.BusType);
        Log.d("Route", "BusCompany : " + this.BusCompany);
        Log.d("Route", "FstopName : " + this.FstopName);
        Log.d("Route", "TstopName : " + this.TstopName);
        Log.d("Route", "Remark : " + this.Remark);
        Log.d("Route", "WD_Start_Time : " + this.WD_Start_Time);
        Log.d("Route", "WD_End_Time : " + this.WD_End_Time);
        Log.d("Route", "WD_Max_Interval : " + this.WD_Max_Interval);
        Log.d("Route", "WD_Min_Interval : " + this.WD_Min_Interval);
        Log.d("Route", "WE_Start_Time : " + this.WE_Start_Time);
        Log.d("Route", "WE_End_Time : " + this.WE_End_Time);
        Log.d("Route", "WE_Max_Interval : " + this.WE_Max_Interval);
        Log.d("Route", "WE_Min_Interval : " + this.WE_Min_Interval);
        Log.d("Route", "WS_Start_Time : " + this.WS_Start_Time);
        Log.d("Route", "WS_End_Time : " + this.WS_End_Time);
        Log.d("Route", "WS_Max_Interval : " + this.WS_Max_Interval);
        Log.d("Route", "WS_Min_Interval : " + this.WS_Min_Interval);
        Log.d("Route", "Interval : " + this.Interval);
        Log.d("Route", "Length : " + this.Length);
        Log.d("Route", "OperationCnt : " + this.OperationCnt);
        Log.d("Route", "CompanyTel : " + this.CompanyTel);
        Log.d("Route", "ErrorMsg : " + this.ErrorMsg);
    }
}
