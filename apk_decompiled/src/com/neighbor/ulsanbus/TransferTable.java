package com.neighbor.ulsanbus;

import java.util.ArrayList;

public class TransferTable {
    private String mLength;
    private String mTransCnt;
    private String mTravelTime;
    private ArrayList<TransitionBus> mlist = new ArrayList();

    public void setTravelTime(String arg0) {
        this.mTravelTime = arg0;
    }

    public void setTravelLength(String arg0) {
        this.mLength = arg0;
    }

    public void setTransCount(String arg0) {
        this.mTransCnt = arg0;
    }

    public void addTransitin(TransitionBus arg0) {
        this.mlist.add(arg0);
    }

    public String getTravelTime() {
        return this.mTravelTime;
    }

    public String getTravelLength() {
        return this.mLength;
    }

    public String getTransCount() {
        return this.mTransCnt;
    }

    public ArrayList<TransitionBus> getTransitionlist() {
        return this.mlist;
    }
}
