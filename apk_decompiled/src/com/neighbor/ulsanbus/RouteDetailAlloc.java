package com.neighbor.ulsanbus;

import java.util.ArrayList;

public class RouteDetailAlloc {
    private String ErrorMsg;
    private String LowType;
    private String RouteID;
    private ArrayList<String> Today_TimeList = new ArrayList();
    private ArrayList<TodayTimeList> Today_TimeObjList = new ArrayList();
    private String WeekType;

    public class TodayTimeList {
        String LowType;
        String TodayTime;

        public TodayTimeList(String today_time, String low_type) {
            this.TodayTime = today_time;
            this.LowType = low_type;
        }

        public void setTodayTime(String todaytime) {
            this.TodayTime = todaytime;
        }

        public String getTodayTime() {
            return this.TodayTime;
        }

        public void setLowType(String lowtype) {
            this.LowType = lowtype;
        }

        public String getLowType() {
            return this.LowType;
        }
    }

    public String getRouteID() {
        return this.RouteID;
    }

    public void setRouteID(String routeid) {
        this.RouteID = routeid;
    }

    public String getWeekType() {
        return this.WeekType;
    }

    public void setWeekType(String week_type) {
        this.WeekType = week_type;
    }

    public void setErrorMsg(String msg) {
        this.ErrorMsg = msg;
    }

    public String getErrorMsg() {
        return this.ErrorMsg;
    }

    public ArrayList<String> getToday_TimeList() {
        return this.Today_TimeList;
    }

    public void setToday_TimeList(String today_list) {
        this.Today_TimeList.add(today_list);
    }

    public void toLogRouteArray() {
    }
}
