package com.neighbor.ulsanbus.network;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import com.neighbor.ulsanbus.TCardStore;
import com.neighbor.ulsanbus.data.DataConst;
import java.util.List;

public class ProcessTCard extends Action {
    Context mContext;
    List<Object> mTcardlist;

    public ProcessTCard(Context ctx) {
        this.mContext = ctx;
    }

    public void parse() throws Exception {
        try {
            ParseTCard mParse = new ParseTCard();
            mParse.parse();
            this.mTcardlist = mParse.get();
        } catch (Exception e) {
            Log.e("ProcessTCard", e.toString());
            throw new Exception(e.getMessage());
        }
    }

    public void save() throws Exception {
        if (this.mTcardlist.size() > 0) {
            this.mContext.getContentResolver().delete(DataConst.CONTENT_TSTORE_URI, null, null);
            ContentValues[] list = new ContentValues[this.mTcardlist.size()];
            int s = 0;
            while (s < this.mTcardlist.size()) {
                TCardStore node = (TCardStore) this.mTcardlist.get(s);
                ContentValues cm = new ContentValues();
                try {
                    cm.put(DataConst.KEY_TCARDSTORE_NAME, node.getTCardStore_Name());
                    if (node.getTCardStore_Address() != null) {
                        cm.put(DataConst.KEY_TCARDSTORE_ADDRESS, node.getTCardStore_Address());
                    } else {
                        cm.put(DataConst.KEY_TCARDSTORE_ADDRESS, "");
                    }
                    if (node.getTCardStore_CardX() != null) {
                        cm.put(DataConst.KEY_TCARDSTORE_CARDX, node.getTCardStore_CardX());
                    } else {
                        cm.put(DataConst.KEY_TCARDSTORE_CARDX, "0");
                    }
                    if (node.getTCardStore_CardY() != null) {
                        cm.put(DataConst.KEY_TCARDSTORE_CARDY, node.getTCardStore_CardY());
                    } else {
                        cm.put(DataConst.KEY_TCARDSTORE_CARDY, "0");
                    }
                    if (node.getTCardStore_StoreType() != null) {
                        cm.put(DataConst.KEY_TCARDSTORE_STORETYPE, node.getTCardStore_StoreType());
                    } else {
                        cm.put(DataConst.KEY_TCARDSTORE_STORETYPE, "");
                    }
                    list[s] = cm;
                    s++;
                } catch (Exception e) {
                    throw new Exception(e.getMessage());
                }
            }
            this.mContext.getContentResolver().bulkInsert(DataConst.CONTENT_TSTORE_URI, list);
        }
    }
}
