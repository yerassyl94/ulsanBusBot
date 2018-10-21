package com.neighbor.ulsanbus.ui;

import android.app.Application;
import java.util.ArrayList;

public class WidgetData extends Application {
    DispDataInfo dispInfo;
    ArrayList<DispDataInfo> displayInfo = new ArrayList();

    class DispDataInfo {
        String mArrivalTime;
        String mRouteId;
        String mStatus;

        DispDataInfo() {
        }
    }

    public ArrayList<DispDataInfo> getDisplaylist() {
        return this.displayInfo;
    }

    public void setDisplaylist(ArrayList<DispDataInfo> list) {
        this.displayInfo = list;
    }

    public DispDataInfo getInstance() {
        return new DispDataInfo();
    }

    public void initWidgetData() {
        if (this.displayInfo.size() > 0) {
            this.displayInfo.clear();
        }
    }

    public void setWidgetData(String routeId, String status, String remainTime) {
        DispDataInfo info = new DispDataInfo();
        info.mRouteId = routeId;
        info.mArrivalTime = remainTime;
        info.mStatus = status;
        this.displayInfo.add(info);
    }
}
