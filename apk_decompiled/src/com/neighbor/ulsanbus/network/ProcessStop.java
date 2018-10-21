package com.neighbor.ulsanbus.network;

import android.content.ContentValues;
import android.content.Context;
import com.neighbor.ulsanbus.Stop;
import com.neighbor.ulsanbus.data.DataConst;
import java.util.List;

public class ProcessStop extends Action {
    Context mContext;
    List<Object> mStoplist;

    public ProcessStop(Context ctx) {
        this.mContext = ctx;
    }

    public void parse() throws Exception {
        try {
            ParseStop mParse = new ParseStop();
            mParse.parse();
            this.mStoplist = mParse.get();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void save() throws Exception {
        if (this.mStoplist.size() > 0) {
            this.mContext.getContentResolver().delete(DataConst.CONTENT_STOPS_URI, null, null);
            this.mContext.getContentResolver().delete(DataConst.CONTENT_STOP_SEARCH_URI, null, null);
            ContentValues[] list = new ContentValues[this.mStoplist.size()];
            for (int s = 0; s < this.mStoplist.size(); s++) {
                Stop node = (Stop) this.mStoplist.get(s);
                ContentValues cm = new ContentValues();
                cm.put("stop_id", node.getStopID());
                if (node.getStopName() != null) {
                    cm.put("stop_name", node.getStopName());
                } else {
                    cm.put("stop_name", "");
                }
                if (node.getStopLimousine() != null) {
                    cm.put(DataConst.KEY_STOP_LIMOUSINE, node.getStopLimousine());
                } else {
                    try {
                        cm.put(DataConst.KEY_STOP_LIMOUSINE, "0");
                    } catch (Exception e) {
                        throw new Exception(e.getMessage());
                    }
                }
                if (node.getStopX() != null) {
                    cm.put(DataConst.KEY_STOP_X, node.getStopX());
                } else {
                    cm.put(DataConst.KEY_STOP_X, "0");
                }
                if (node.getStopY() != null) {
                    cm.put(DataConst.KEY_STOP_Y, node.getStopY());
                } else {
                    cm.put(DataConst.KEY_STOP_Y, "0");
                }
                if (node.getStopRemark() != null) {
                    cm.put("stop_remark", node.getStopRemark());
                } else {
                    cm.put("stop_remark", "");
                }
                list[s] = cm;
            }
            this.mContext.getContentResolver().bulkInsert(DataConst.CONTENT_STOPS_URI, list);
        }
    }
}
