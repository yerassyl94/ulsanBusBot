package com.neighbor.ulsanbus;

public class Timetable {
    private String Remark;
    private String TodayTime;

    public String getTodayTime() {
        return this.TodayTime;
    }

    public void setTodayTime(String todayTime) {
        this.TodayTime = todayTime;
    }

    public String getRemark() {
        return this.Remark;
    }

    public void setRemark(String remark) {
        this.Remark = remark;
    }
}
