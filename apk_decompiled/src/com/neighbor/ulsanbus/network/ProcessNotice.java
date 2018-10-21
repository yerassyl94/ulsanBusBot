package com.neighbor.ulsanbus.network;

import android.content.ContentValues;
import android.content.Context;
import com.neighbor.ulsanbus.Notice;
import com.neighbor.ulsanbus.data.DataConst;
import java.util.List;

public class ProcessNotice extends Action {
    Context mContext;
    List<Object> mNoticelist;

    public ProcessNotice(Context ctx) {
        this.mContext = ctx;
    }

    public void parse() throws Exception {
        try {
            ParseNotice mParse = new ParseNotice();
            mParse.parse();
            this.mNoticelist = mParse.get();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void save() {
        if (this.mNoticelist.size() > 0) {
            this.mContext.getContentResolver().delete(DataConst.CONTENT_NOTICE_URI, null, null);
            ContentValues[] list = new ContentValues[this.mNoticelist.size()];
            for (int s = 0; s < this.mNoticelist.size(); s++) {
                Notice node = (Notice) this.mNoticelist.get(s);
                ContentValues cm = new ContentValues();
                if (node.getNoticeDate() != null) {
                    cm.put(DataConst.KEY_NOTICE_DATE, node.getNoticeDate());
                } else {
                    cm.put(DataConst.KEY_NOTICE_DATE, "20150000000000");
                }
                if (node.getNoticeTitle() != null) {
                    cm.put(DataConst.KEY_NOTICE_TITLE, node.getNoticeTitle());
                } else {
                    cm.put(DataConst.KEY_NOTICE_TITLE, "");
                }
                if (node.getNoticeContent() != null) {
                    cm.put(DataConst.KEY_NOTICE_CONTENT, node.getNoticeContent());
                } else {
                    cm.put(DataConst.KEY_NOTICE_CONTENT, "");
                }
                if (node.getNoticeFile() != null) {
                    cm.put(DataConst.KEY_NOTICE_FILE, node.getNoticeFile());
                } else {
                    cm.put(DataConst.KEY_NOTICE_FILE, "");
                }
                cm.put(DataConst.KEY_NOTICE_CNT, new Integer(this.mNoticelist.size()).toString());
                list[s] = cm;
            }
            this.mContext.getContentResolver().bulkInsert(DataConst.CONTENT_NOTICE_URI, list);
        }
    }
}
