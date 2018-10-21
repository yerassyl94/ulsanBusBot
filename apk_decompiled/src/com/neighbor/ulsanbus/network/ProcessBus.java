package com.neighbor.ulsanbus.network;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import com.neighbor.ulsanbus.Route;
import com.neighbor.ulsanbus.data.DataConst;
import java.util.List;

public class ProcessBus extends Action {
    List<Object> mBUSlist;
    Context mContext;

    public ProcessBus(Context ctx) {
        this.mContext = ctx;
    }

    public void parse() throws Exception {
        try {
            ParseBus mParse = new ParseBus();
            mParse.parse();
            this.mBUSlist = mParse.get();
        } catch (Exception e) {
            Log.e("parse: ", e.toString());
            throw new Exception(e.getMessage());
        }
    }

    public void save() throws Exception {
        if (this.mBUSlist.size() > 0) {
            this.mContext.getContentResolver().delete(DataConst.CONTENT_ROUTE_URI, null, null);
            this.mContext.getContentResolver().delete(DataConst.CONTENT_ROUTE_SEARCH_URI, null, null);
            ContentValues[] list = new ContentValues[this.mBUSlist.size()];
            int s = 0;
            while (s < this.mBUSlist.size()) {
                Route node = (Route) this.mBUSlist.get(s);
                node.toLogRoute();
                ContentValues cm = new ContentValues();
                try {
                    cm.put("route_id", node.getRouteID());
                    cm.put("route_no", node.getRouteNo());
                    cm.put(DataConst.KEY_ROUTE_DIR, node.getRouteDir());
                    cm.put(DataConst.KEY_ROUTE_TYPE, node.getRouteType());
                    cm.put("route_bus_type", node.getBusType());
                    if (node.getBusCompany() != null) {
                        cm.put(DataConst.KEY_ROUTE_BUS_COMPANY, node.getBusCompany());
                    } else {
                        cm.put(DataConst.KEY_ROUTE_BUS_COMPANY, "");
                    }
                    if (node.getRemark() != null) {
                        cm.put(DataConst.KEY_ROUTE_REMARK, node.getRemark());
                    } else {
                        cm.put(DataConst.KEY_ROUTE_REMARK, "");
                    }
                    if (node.getFstopName() != null) {
                        cm.put(DataConst.KEY_ROUTE_FSTOP_NAME, node.getFstopName());
                    } else {
                        cm.put(DataConst.KEY_ROUTE_FSTOP_NAME, "");
                    }
                    if (node.getTstopName() != null) {
                        cm.put(DataConst.KEY_ROUTE_TSTOP_NAME, node.getTstopName());
                    } else {
                        cm.put(DataConst.KEY_ROUTE_TSTOP_NAME, "");
                    }
                    if (node.getWD_Start_Time() != null) {
                        cm.put(DataConst.KEY_ROUTE_WD_START_TIME, node.getWD_Start_Time());
                    } else {
                        cm.put(DataConst.KEY_ROUTE_WD_START_TIME, "0000");
                    }
                    if (node.getWD_End_Time() != null) {
                        cm.put(DataConst.KEY_ROUTE_WD_END_TIME, node.getWD_End_Time());
                    } else {
                        cm.put(DataConst.KEY_ROUTE_WD_END_TIME, "0000");
                    }
                    if (node.getWD_Max_Interval() != null) {
                        cm.put(DataConst.KEY_ROUTE_WD_MAX_INTERVAL, node.getWD_Max_Interval());
                    } else {
                        cm.put(DataConst.KEY_ROUTE_WD_MAX_INTERVAL, "0");
                    }
                    if (node.getWD_Min_Interval() != null) {
                        cm.put(DataConst.KEY_ROUTE_WD_MIN_INTERVAL, node.getWD_Min_Interval());
                    } else {
                        cm.put(DataConst.KEY_ROUTE_WD_MIN_INTERVAL, "0");
                    }
                    if (node.getWE_Start_Time() != null) {
                        cm.put(DataConst.KEY_ROUTE_WE_START_TIME, node.getWE_Start_Time());
                    } else {
                        cm.put(DataConst.KEY_ROUTE_WE_START_TIME, "0000");
                    }
                    if (node.getWE_End_Time() != null) {
                        cm.put(DataConst.KEY_ROUTE_WE_END_TIME, node.getWE_End_Time());
                    } else {
                        cm.put(DataConst.KEY_ROUTE_WE_END_TIME, "0000");
                    }
                    if (node.getWE_Max_Interval() != null) {
                        cm.put(DataConst.KEY_ROUTE_WE_MAX_INTERVAL, node.getWE_Max_Interval());
                    } else {
                        cm.put(DataConst.KEY_ROUTE_WE_MAX_INTERVAL, "0");
                    }
                    if (node.getWE_Min_Interval() != null) {
                        cm.put(DataConst.KEY_ROUTE_WE_MIN_INTERVAL, node.getWE_Min_Interval());
                    } else {
                        cm.put(DataConst.KEY_ROUTE_WE_MIN_INTERVAL, "0");
                    }
                    if (node.getWS_Start_Time() != null) {
                        cm.put(DataConst.KEY_ROUTE_WS_START_TIME, node.getWS_Start_Time());
                    } else {
                        cm.put(DataConst.KEY_ROUTE_WS_START_TIME, "0000");
                    }
                    if (node.getWS_End_Time() != null) {
                        cm.put(DataConst.KEY_ROUTE_WS_END_TIME, node.getWS_End_Time());
                    } else {
                        cm.put(DataConst.KEY_ROUTE_WS_END_TIME, "0000");
                    }
                    if (node.getWS_Max_Interval() != null) {
                        cm.put(DataConst.KEY_ROUTE_WS_MAX_INTERVAL, node.getWS_Max_Interval());
                    } else {
                        cm.put(DataConst.KEY_ROUTE_WS_MAX_INTERVAL, "0");
                    }
                    if (node.getWS_Min_Interval() != null) {
                        cm.put(DataConst.KEY_ROUTE_WS_MIN_INTERVAL, node.getWS_Min_Interval());
                    } else {
                        cm.put(DataConst.KEY_ROUTE_WS_MIN_INTERVAL, "0");
                    }
                    if (node.getTravelTime() != null) {
                        cm.put(DataConst.KEY_ROUTE_TRAVEL_TIME, node.getTravelTime());
                    } else {
                        cm.put(DataConst.KEY_ROUTE_TRAVEL_TIME, "0");
                    }
                    if (node.getLength() != null) {
                        cm.put(DataConst.KEY_ROUTE_LENGTH, node.getLength());
                    } else {
                        cm.put(DataConst.KEY_ROUTE_LENGTH, "0");
                    }
                    if (node.getOperationCnt() != null) {
                        cm.put(DataConst.KEY_ROUTE_OPERATION_CNT, node.getOperationCnt());
                    } else {
                        cm.put(DataConst.KEY_ROUTE_OPERATION_CNT, "0");
                    }
                    if (node.getInterval() != null) {
                        cm.put("route_interval", node.getInterval());
                    } else {
                        cm.put("route_interval", "0");
                    }
                    if (node.getCompanyTel() != null) {
                        cm.put(DataConst.KEY_ROUTE_COMPANYTEL, node.getCompanyTel());
                    } else {
                        cm.put(DataConst.KEY_ROUTE_COMPANYTEL, "");
                    }
                    if (node.getBrtTimeFlag() != null) {
                        cm.put(DataConst.KEY_BRT_TIME_FLAG, node.getBrtTimeFlag());
                    } else {
                        cm.put(DataConst.KEY_BRT_TIME_FLAG, "0");
                    }
                    if (node.getBrt_app_remark() != null) {
                        cm.put(DataConst.KEY_BRT_APP_REMARK, node.getBrt_app_remark());
                    } else {
                        cm.put(DataConst.KEY_BRT_APP_REMARK, "");
                    }
                    if (node.getBrt_app_waypoint_desc() != null) {
                        cm.put(DataConst.KEY_BRT_APP_WAYPOINT_DESC, node.getBrt_app_waypoint_desc());
                    } else {
                        cm.put(DataConst.KEY_BRT_APP_WAYPOINT_DESC, "");
                    }
                    if (node.getBrt_class_seqno() != null) {
                        cm.put(DataConst.KEY_BRT_CLASS_SEQNO, node.getBrt_class_seqno());
                    } else {
                        cm.put(DataConst.KEY_BRT_CLASS_SEQNO, "");
                    }
                    list[s] = cm;
                    s++;
                } catch (Exception e) {
                    throw new Exception(e.getMessage());
                }
            }
            this.mContext.getContentResolver().bulkInsert(DataConst.CONTENT_ROUTE_URI, list);
        }
    }
}
