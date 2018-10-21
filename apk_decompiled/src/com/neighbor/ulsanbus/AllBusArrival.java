package com.neighbor.ulsanbus;

import java.util.ArrayList;

public class AllBusArrival {
    private String BusCount;
    private String EmergencyMode;
    private String Emergency_ver;
    private String ErrorMsg;
    private ArrayList<ArrivedBusInfo> mApprochingBusList = new ArrayList();

    public class ArrivedBusInfo {
        public String BusType;
        public String EmergencyCD;
        public String LowType;
        public String RemainStopCnt;
        public String RemainTime;
        public String RouteID;
        public String RouteNo;
        public String Status;
        public String StopID;
        public String mBusNo;
        public String mFStopName;
        public String mReMark;
        public String mTstopName;
    }

    public ArrivedBusInfo getNewInstanceBusInfo() {
        return new ArrivedBusInfo();
    }

    public ArrivedBusInfo getArrivalBus(int position) {
        return (ArrivedBusInfo) this.mApprochingBusList.get(position);
    }

    public void addArrivalBusInfo(ArrivedBusInfo object) {
        this.mApprochingBusList.add(object);
    }

    public ArrayList<ArrivedBusInfo> getAllBusArrivalInfoList() {
        return this.mApprochingBusList;
    }

    public void initAllBusArrivalInfoList() {
        if (this.mApprochingBusList.size() > 0) {
            this.mApprochingBusList.clear();
        }
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

    public void setBusCnt(String busCount) {
        this.BusCount = busCount;
    }

    public void setErrorMsg(String msg) {
        this.ErrorMsg = msg;
    }

    public String getErrorMsg() {
        return this.ErrorMsg;
    }
}
