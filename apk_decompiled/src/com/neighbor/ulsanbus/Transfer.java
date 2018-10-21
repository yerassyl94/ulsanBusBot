package com.neighbor.ulsanbus;

import java.util.ArrayList;

public class Transfer {
    private String mDirectCnt;
    private ArrayList<DirectTable> mDirectlist = new ArrayList();
    private String mTransCnt;
    private ArrayList<TransferTable> mTransitionlist = new ArrayList();

    public void setDirectCnt(String arg0) {
        this.mDirectCnt = arg0;
    }

    public void setTransCnt(String arg0) {
        this.mTransCnt = arg0;
    }

    public void addDirectRoute(DirectTable arg0) {
        this.mDirectlist.add(arg0);
    }

    public void addTransRoute(TransferTable arg0) {
        this.mTransitionlist.add(arg0);
    }

    public String getDirectCnt() {
        return this.mDirectCnt;
    }

    public String getTransCnt() {
        return this.mTransCnt;
    }

    public ArrayList<DirectTable> getDirectList() {
        return this.mDirectlist;
    }

    public ArrayList<TransferTable> getTransList() {
        return this.mTransitionlist;
    }
}
