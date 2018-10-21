package com.neighbor.ulsanbus.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;
import com.neighbor.ulsanbus.AllBusArrival;
import com.neighbor.ulsanbus.AllBusArrival.ArrivedBusInfo;
import com.neighbor.ulsanbus.C0258R;
import com.neighbor.ulsanbus.data.DataConst;
import com.neighbor.ulsanbus.data.DataSAXParser;
import com.neighbor.ulsanbus.util.UtilNetworkConnection;
import java.util.ArrayList;
import java.util.Iterator;

public class FourByTwoCellWidgetProvider extends AppWidgetProvider {
    private static final int RESULT_DATA_EMPTY = 2;
    private static final int RESULT_DATA_OK = 0;
    private static final int RESULT_DATA_RESTORE = 10;
    private static final int RESULT_SERVER_ERROR = 1;
    private static final String SELECT_WIDGET3_ITEM = "com.neighbor.ulsanbus.widget3";
    private static String TAG = "[Ulsanbus] FourByTwoCellWidgetProvider ";
    private static int mWidgetCount = 0;
    private static int widgetId;
    private Handler handler = new C02661();
    private AllBusArrival mAllBusArrivalList;
    private ArrayList<ContentValues> mCVlist;
    private Context mContext;
    private DataInfo mData;
    private ArrayList<DataInfo> mDatalist;
    private DataSAXParser mSaxHelper;
    private AppWidgetManager manager;
    private RemoteViews remoteViews;

    /* renamed from: com.neighbor.ulsanbus.ui.FourByTwoCellWidgetProvider$1 */
    class C02661 extends Handler {
        C02661() {
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(FourByTwoCellWidgetProvider.TAG, "msg.what" + msg.what);
            if (msg.what == 0 && FourByTwoCellWidgetProvider.this.mDatalist == null) {
                Log.d(FourByTwoCellWidgetProvider.TAG, "msg.what == RESULT_DATA_OK");
                WidgetData wd = (WidgetData) FourByTwoCellWidgetProvider.this.mContext.getApplicationContext();
                wd.initWidgetData();
                FourByTwoCellWidgetProvider.this.mCVlist = new ArrayList();
                FourByTwoCellWidgetProvider.this.mContext.getContentResolver().delete(DataConst.CONTENT_WIDGET3_LIST_URI, "widget_id = " + FourByTwoCellWidgetProvider.this.mData.mWidgetId, null);
                Iterator it = FourByTwoCellWidgetProvider.this.mAllBusArrivalList.getAllBusArrivalInfoList().iterator();
                while (it.hasNext()) {
                    ArrivedBusInfo info = (ArrivedBusInfo) it.next();
                    wd.setWidgetData(info.RouteID, info.Status, info.RemainTime);
                    ContentValues value = new ContentValues();
                    value.put("widget_id", Integer.valueOf(FourByTwoCellWidgetProvider.this.mData.mWidgetId));
                    value.put("stop_id", FourByTwoCellWidgetProvider.this.mData.mStopId);
                    value.put("stop_name", FourByTwoCellWidgetProvider.this.mData.mSTopName);
                    value.put("stop_remark", FourByTwoCellWidgetProvider.this.mData.mStopRemark);
                    value.put("remain_time", info.RemainTime);
                    value.put("route_id", info.RouteID);
                    value.put("status", info.Status);
                    FourByTwoCellWidgetProvider.this.mCVlist.add(value);
                }
                FourByTwoCellWidgetProvider.this.mContext.getContentResolver().bulkInsert(DataConst.CONTENT_WIDGET3_LIST_URI, (ContentValues[]) FourByTwoCellWidgetProvider.this.mCVlist.toArray(new ContentValues[0]));
                FourByTwoCellWidgetProvider.this.remoteViews = new RemoteViews(FourByTwoCellWidgetProvider.this.mContext.getPackageName(), C0258R.layout.widget_4x2_layout);
                if (FourByTwoCellWidgetProvider.this.remoteViews != null) {
                    FourByTwoCellWidgetProvider.this.remoteViews.setViewVisibility(C0258R.id.widget3_progress, 8);
                    FourByTwoCellWidgetProvider.this.remoteViews.setViewVisibility(C0258R.id.widget_text_stopname, 0);
                    FourByTwoCellWidgetProvider.this.remoteViews.setViewVisibility(C0258R.id.widget_arrivallist, 0);
                    StringBuffer StopName = new StringBuffer();
                    StopName.append(FourByTwoCellWidgetProvider.this.mData.mSTopName);
                    if (FourByTwoCellWidgetProvider.this.mData.mStopRemark != null && FourByTwoCellWidgetProvider.this.mData.mStopRemark.length() > 0) {
                        StopName.append(" ( ");
                        StopName.append(FourByTwoCellWidgetProvider.this.mData.mStopRemark);
                        StopName.append(" )");
                    }
                    FourByTwoCellWidgetProvider.this.remoteViews.setTextViewText(C0258R.id.widget_text_stopname, StopName.toString());
                    Intent svcIntent = new Intent(FourByTwoCellWidgetProvider.this.mContext, UiWidgetService.class);
                    svcIntent.putExtra("appWidgetId", FourByTwoCellWidgetProvider.this.mData.mWidgetId);
                    svcIntent.putExtra("stop_id", FourByTwoCellWidgetProvider.this.mData.mStopId);
                    svcIntent.putExtra("stop_name", FourByTwoCellWidgetProvider.this.mData.mSTopName);
                    svcIntent.putExtra("widget_id", FourByTwoCellWidgetProvider.this.mData.mWidgetId);
                    svcIntent.setData(Uri.parse(svcIntent.toUri(1)));
                    FourByTwoCellWidgetProvider.this.remoteViews.setRemoteAdapter(FourByTwoCellWidgetProvider.this.mData.mWidgetId, C0258R.id.widget_arrivallist, svcIntent);
                    Intent templateIntent = new Intent(FourByTwoCellWidgetProvider.this.mContext, FourByTwoCellWidgetProvider.class);
                    templateIntent.setAction(FourByTwoCellWidgetProvider.SELECT_WIDGET3_ITEM);
                    templateIntent.putExtra("appWidgetId", FourByTwoCellWidgetProvider.this.mData.mWidgetId);
                    FourByTwoCellWidgetProvider.this.remoteViews.setOnClickPendingIntent(C0258R.id.widget_refresh_info, PendingIntent.getBroadcast(FourByTwoCellWidgetProvider.this.mContext, FourByTwoCellWidgetProvider.this.mData.mWidgetId, templateIntent, 134217728));
                    FourByTwoCellWidgetProvider.this.manager = AppWidgetManager.getInstance(FourByTwoCellWidgetProvider.this.mContext);
                    FourByTwoCellWidgetProvider.this.manager.notifyAppWidgetViewDataChanged(FourByTwoCellWidgetProvider.this.mData.mWidgetId, C0258R.id.widget_arrivallist);
                    FourByTwoCellWidgetProvider.this.manager.updateAppWidget(FourByTwoCellWidgetProvider.this.mData.mWidgetId, FourByTwoCellWidgetProvider.this.remoteViews);
                    return;
                }
                return;
            }
            Log.d(FourByTwoCellWidgetProvider.TAG, "msg.what == RESULT_DATA_OTHER");
            FourByTwoCellWidgetProvider.this.restoreDisplay(FourByTwoCellWidgetProvider.this.mData);
            if (FourByTwoCellWidgetProvider.this.mDatalist != null) {
                FourByTwoCellWidgetProvider.access$908();
                Log.d(FourByTwoCellWidgetProvider.TAG, "mDatalist.size()" + FourByTwoCellWidgetProvider.this.mDatalist.size());
                Log.d(FourByTwoCellWidgetProvider.TAG, "mWidgetCount" + FourByTwoCellWidgetProvider.mWidgetCount);
                if (FourByTwoCellWidgetProvider.mWidgetCount < FourByTwoCellWidgetProvider.this.mDatalist.size()) {
                    FourByTwoCellWidgetProvider.this.mData = (DataInfo) FourByTwoCellWidgetProvider.this.mDatalist.get(FourByTwoCellWidgetProvider.mWidgetCount);
                    FourByTwoCellWidgetProvider.this.handler.sendEmptyMessageDelayed(10, 1000);
                }
            }
        }
    }

    private class DataInfo {
        String mSTopName;
        String mStopId;
        String mStopRemark;
        int mWidgetId;

        private DataInfo() {
        }
    }

    class ReqArrivalRoute extends AsyncTask<Void, Integer, Integer> {
        private AllBusArrival mArrBus;
        private String mStopId;
        private int responseCode = 0;

        public ReqArrivalRoute(String stopid) {
            this.mStopId = stopid;
            this.mArrBus = new AllBusArrival();
        }

        protected Integer doInBackground(Void... params) {
            try {
                FourByTwoCellWidgetProvider.this.mSaxHelper.getName(DataConst.API_ALLBUSARRIVALINFO3 + this.mStopId, DataConst.FLAG_ALLBUSARRIVALINFO);
            } catch (Exception e) {
                this.responseCode = 3;
            }
            return Integer.valueOf(this.responseCode);
        }

        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result.intValue() != 0) {
                FourByTwoCellWidgetProvider.this.handler.sendEmptyMessage(result.intValue());
                return;
            }
            try {
                this.mArrBus = FourByTwoCellWidgetProvider.this.mSaxHelper.getAllBusArrivalList();
                if (this.mArrBus == null) {
                    FourByTwoCellWidgetProvider.this.handler.sendEmptyMessage(1);
                } else if (!this.mArrBus.getErrorMsg().equals("OK")) {
                    FourByTwoCellWidgetProvider.this.handler.sendEmptyMessage(1);
                } else if (this.mArrBus.getAllBusArrivalInfoList().size() > 0) {
                    if (FourByTwoCellWidgetProvider.this.mAllBusArrivalList == null) {
                        FourByTwoCellWidgetProvider.this.mAllBusArrivalList = new AllBusArrival();
                    }
                    FourByTwoCellWidgetProvider.this.mAllBusArrivalList.initAllBusArrivalInfoList();
                    FourByTwoCellWidgetProvider.this.mAllBusArrivalList = this.mArrBus;
                    FourByTwoCellWidgetProvider.this.handler.sendEmptyMessage(0);
                } else {
                    FourByTwoCellWidgetProvider.this.handler.sendEmptyMessage(2);
                }
            } catch (Exception e) {
                FourByTwoCellWidgetProvider.this.handler.sendEmptyMessage(1);
            }
        }
    }

    static /* synthetic */ int access$908() {
        int i = mWidgetCount;
        mWidgetCount = i + 1;
        return i;
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        this.mDatalist = new ArrayList();
        this.mContext = context;
        Cursor cursor = this.mContext.getContentResolver().query(DataConst.CONTENT_WIDGET3_URI, new String[]{DataConst.KEY_NOTICE_ID, "widget_id", "stop_id", "stop_name", "stop_remark"}, null, null, null);
        Log.d(TAG, "cursor.getCount()" + cursor.getCount());
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                DataInfo mData = new DataInfo();
                mData.mStopId = cursor.getString(cursor.getColumnIndex("stop_id"));
                mData.mSTopName = cursor.getString(cursor.getColumnIndex("stop_name"));
                mData.mWidgetId = cursor.getInt(cursor.getColumnIndex("widget_id"));
                mData.mStopRemark = cursor.getString(cursor.getColumnIndex("stop_remark"));
                this.mDatalist.add(mData);
            } while (cursor.moveToNext());
            cursor.close();
            mWidgetCount = 0;
            this.mData = (DataInfo) this.mDatalist.get(mWidgetCount);
            this.handler.sendEmptyMessageDelayed(10, 1000);
        }
    }

    static void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        widgetId = appWidgetId;
        Intent intent = new Intent();
        intent.setAction(SELECT_WIDGET3_ITEM);
        intent.putExtra("appWidgetId", widgetId);
        context.sendBroadcast(intent);
    }

    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            String Selection = "widget_id = " + widgetId;
            Log.d(TAG, "Selection : " + Selection);
            context.getContentResolver().delete(DataConst.CONTENT_WIDGET3_URI, Selection, null);
            context.getContentResolver().delete(DataConst.CONTENT_WIDGET3_LIST_URI, "widget_id = " + widgetId, null);
        }
        super.onDeleted(context, appWidgetIds);
    }

    public void onEnabled(Context context) {
        this.mDatalist = new ArrayList();
        this.mContext = context;
        Cursor cursor = this.mContext.getContentResolver().query(DataConst.CONTENT_WIDGET3_URI, new String[]{DataConst.KEY_NOTICE_ID, "widget_id", "stop_id", "stop_name", "stop_remark"}, null, null, null);
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                DataInfo mData = new DataInfo();
                mData.mStopId = cursor.getString(cursor.getColumnIndex("stop_id"));
                mData.mSTopName = cursor.getString(cursor.getColumnIndex("stop_name"));
                mData.mWidgetId = cursor.getInt(cursor.getColumnIndex("widget_id"));
                mData.mStopRemark = cursor.getString(cursor.getColumnIndex("stop_remark"));
                this.mDatalist.add(mData);
            } while (cursor.moveToNext());
            cursor.close();
            mWidgetCount = 0;
            this.mData = (DataInfo) this.mDatalist.get(mWidgetCount);
            this.handler.sendEmptyMessageDelayed(10, 1000);
        }
        super.onEnabled(context);
    }

    private void restoreDisplay(DataInfo info) {
        WidgetData wd = (WidgetData) this.mContext.getApplicationContext();
        wd.initWidgetData();
        this.remoteViews = new RemoteViews(this.mContext.getPackageName(), C0258R.layout.widget_4x2_layout);
        if (this.remoteViews != null) {
            this.remoteViews.setViewVisibility(C0258R.id.widget3_progress, 8);
            this.remoteViews.setViewVisibility(C0258R.id.widget_text_stopname, 0);
            this.remoteViews.setViewVisibility(C0258R.id.widget_arrivallist, 0);
            StringBuffer StopName = new StringBuffer();
            StopName.append(info.mSTopName);
            if (info.mStopRemark != null && info.mStopRemark.length() > 0) {
                StopName.append(" ( ");
                StopName.append(info.mStopRemark);
                StopName.append(" )");
            }
            this.remoteViews.setTextViewText(C0258R.id.widget_text_stopname, StopName.toString());
            Intent intent = new Intent(this.mContext, UiWidgetService.class);
            intent = intent;
            intent.putExtra("appWidgetId", info.mWidgetId);
            intent = intent;
            intent.putExtra("stop_id", info.mStopId);
            intent = intent;
            intent.putExtra("stop_name", info.mSTopName);
            intent = intent;
            intent.putExtra("widget_id", info.mWidgetId);
            Cursor itemCursor = this.mContext.getContentResolver().query(DataConst.CONTENT_WIDGET3_LIST_URI, null, "stop_id = " + info.mStopId + " AND " + "widget_id" + " = " + info.mWidgetId, null, null);
            Log.d("FourByTwoCellWidget", "itemCursor " + itemCursor.getCount());
            if (itemCursor.getCount() > 0 && itemCursor.moveToFirst()) {
                do {
                    String routeId = itemCursor.getString(itemCursor.getColumnIndex("route_id"));
                    String status = itemCursor.getString(itemCursor.getColumnIndex("status"));
                    String remainTime = itemCursor.getString(itemCursor.getColumnIndex("remain_time"));
                    String stopName = itemCursor.getString(itemCursor.getColumnIndex("stop_name"));
                    String stopRemark = itemCursor.getString(itemCursor.getColumnIndex("stop_remark"));
                    int widgetId = itemCursor.getInt(itemCursor.getColumnIndex("widget_id"));
                    String stopId = itemCursor.getString(itemCursor.getColumnIndex("stop_id"));
                    wd.setWidgetData(routeId, status, remainTime);
                    Log.d("FourByTwoCellWidget", "+++++++++++++++++++++++");
                    Log.d("FourByTwoCellWidget", "routeId" + routeId);
                    Log.d("FourByTwoCellWidget", "status" + status);
                    Log.d("FourByTwoCellWidget", "remainTime" + remainTime);
                    Log.d("FourByTwoCellWidget", "stopName" + stopName);
                    Log.d("FourByTwoCellWidget", "stopRemark" + stopRemark);
                    Log.d("FourByTwoCellWidget", "widgetId" + widgetId);
                    Log.d("FourByTwoCellWidget", "stopId" + stopId);
                    Log.d("FourByTwoCellWidget", "+++++++++++++++++++++++");
                } while (itemCursor.moveToNext());
                itemCursor.close();
                intent.setData(Uri.parse(intent.toUri(1)));
                this.remoteViews.setRemoteAdapter(info.mWidgetId, C0258R.id.widget_arrivallist, intent);
                Intent pendingintent = new Intent(this.mContext, FourByTwoCellWidgetProvider.class);
                pendingintent.setAction(SELECT_WIDGET3_ITEM);
                pendingintent.putExtra("appWidgetId", info.mWidgetId);
                this.remoteViews.setOnClickPendingIntent(C0258R.id.widget_refresh_info, PendingIntent.getBroadcast(this.mContext, info.mWidgetId, pendingintent, 134217728));
                this.manager = AppWidgetManager.getInstance(this.mContext);
                this.manager.notifyAppWidgetViewDataChanged(info.mWidgetId, C0258R.id.widget_arrivallist);
                this.manager.updateAppWidget(info.mWidgetId, this.remoteViews);
                return;
            }
            return;
        }
        Log.d("FourByTwoCellWidget", "<<<<<<<<< Error >>>>>>>>>>> ");
    }

    public void onReceive(Context context, Intent intent) {
        int widgetId = intent.getIntExtra("appWidgetId", 0);
        this.mContext = context;
        Log.d(TAG, "widgetId" + widgetId);
        Log.d(TAG, "action : " + intent.getAction());
        if (SELECT_WIDGET3_ITEM.equals(intent.getAction()) && widgetId != 0) {
            Cursor cursor = context.getContentResolver().query(DataConst.CONTENT_WIDGET3_URI, new String[]{DataConst.KEY_NOTICE_ID, "widget_id", "stop_id", "stop_name", "stop_remark"}, "widget_id = " + widgetId, null, null);
            Log.d(TAG, "cursor count :" + cursor.getCount());
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                this.mData = new DataInfo();
                this.mData.mStopId = cursor.getString(cursor.getColumnIndex("stop_id"));
                this.mData.mSTopName = cursor.getString(cursor.getColumnIndex("stop_name"));
                this.mData.mWidgetId = cursor.getInt(cursor.getColumnIndex("widget_id"));
                this.mData.mStopRemark = cursor.getString(cursor.getColumnIndex("stop_remark"));
                this.mSaxHelper = new DataSAXParser();
                this.mAllBusArrivalList = new AllBusArrival();
                try {
                    if (new UtilNetworkConnection().IsConnected(this.mContext)) {
                        new ReqArrivalRoute(this.mData.mStopId).execute(new Void[0]);
                        cursor.close();
                        this.remoteViews = new RemoteViews(this.mContext.getPackageName(), C0258R.layout.widget_4x2_layout);
                        this.remoteViews.setViewVisibility(C0258R.id.widget3_progress, 0);
                        this.remoteViews.setViewVisibility(C0258R.id.widget_text_stopname, 8);
                        this.remoteViews.setViewVisibility(C0258R.id.widget_arrivallist, 8);
                        this.manager = AppWidgetManager.getInstance(this.mContext);
                        this.manager.updateAppWidget(this.mData.mWidgetId, this.remoteViews);
                    }
                } catch (Exception e) {
                    if (!cursor.isClosed()) {
                        cursor.close();
                    }
                }
            }
        }
        super.onReceive(context, intent);
    }
}
