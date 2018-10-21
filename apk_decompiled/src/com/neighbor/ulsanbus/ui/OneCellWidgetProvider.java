package com.neighbor.ulsanbus.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.widget.RemoteViews;
import com.neighbor.ulsanbus.C0258R;
import com.neighbor.ulsanbus.RouteArrival;
import com.neighbor.ulsanbus.data.DataConst;
import com.neighbor.ulsanbus.network.ParseArrivalBus;
import com.neighbor.ulsanbus.util.UtilTimeManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import org.xmlpull.v1.XmlPullParserException;

public class OneCellWidgetProvider extends AppWidgetProvider {
    private static final int CONNECT_ERR = -2;
    private static final int PARSE_ERR = -1;
    private static final int RESULT_0K = 0;
    private static final int RESULT_RESTORE = 2;
    private static final String SELECT_WIDGET_ITEM = "com.neighbor.ulsanbus.widget1";
    private static int widgetId;
    private Handler handler = new C02671();
    private ArrayList<RouteArrival> mArrivalList;
    private Context mContext;
    DataInfo mData;
    Queue<DataInfo> mItem = new LinkedList();
    private ParseArrivalBus mParse;
    private AppWidgetManager manager;
    private RemoteViews remoteViews;

    /* renamed from: com.neighbor.ulsanbus.ui.OneCellWidgetProvider$1 */
    class C02671 extends Handler {
        C02671() {
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                OneCellWidgetProvider.this.showWidget();
                OneCellWidgetProvider.this.setContents();
                OneCellWidgetProvider.this.saveInfo(OneCellWidgetProvider.this.mContext);
                return;
            }
            OneCellWidgetProvider.this.restoreScreen(OneCellWidgetProvider.this.mData);
            if (OneCellWidgetProvider.this.mItem.peek() != null) {
                OneCellWidgetProvider.this.mData = (DataInfo) OneCellWidgetProvider.this.mItem.poll();
                OneCellWidgetProvider.this.handler.sendEmptyMessage(2);
            }
        }
    }

    static class DataInfo {
        String mRemainTime;
        String mRouteBusType;
        String mRouteId;
        String mRouteNo;
        String mSTopName;
        String mStopId;
        int mWidgetId;

        DataInfo() {
        }
    }

    class reqWhenTheBusArrive extends Thread {
        reqWhenTheBusArrive() {
        }

        public void run() {
            OneCellWidgetProvider.this.mParse = new ParseArrivalBus();
            try {
                OneCellWidgetProvider.this.mParse.setRouteBus(OneCellWidgetProvider.this.mData.mRouteId, OneCellWidgetProvider.this.mData.mStopId);
                OneCellWidgetProvider.this.mParse.parse();
                OneCellWidgetProvider.this.mArrivalList = (ArrayList) OneCellWidgetProvider.this.mParse.get();
                OneCellWidgetProvider.this.handler.sendEmptyMessage(0);
            } catch (XmlPullParserException e) {
                OneCellWidgetProvider.this.handler.sendEmptyMessage(-1);
            } catch (IOException e2) {
                OneCellWidgetProvider.this.handler.sendEmptyMessage(-2);
            }
        }
    }

    void setContents() {
        String[] colorArray = this.mContext.getResources().getStringArray(C0258R.array.bus_type_color);
        int busType = Integer.parseInt(this.mData.mRouteBusType);
        this.remoteViews.setImageViewResource(C0258R.id.widget1_background, setBackGroundResource(busType));
        this.remoteViews.setTextColor(C0258R.id.widget1_route_number, Color.parseColor(colorArray[busType]));
        if (this.mArrivalList.size() > 0) {
            RouteArrival BusInfo = (RouteArrival) this.mArrivalList.get(0);
            StringBuffer sb = new StringBuffer();
            if (BusInfo.getStatus().equals("1")) {
                sb.append(BusInfo.getRemainTime());
                sb.insert(2, ":");
                sb.append(" 출발");
                this.mData.mRemainTime = sb.toString();
                this.remoteViews.setTextViewText(C0258R.id.widget1_remain_time, this.mData.mRemainTime);
            } else if (BusInfo.getStatus().equals("0")) {
                this.mData.mRemainTime = UtilTimeManager.convertTimeArrival(Integer.parseInt(BusInfo.getRemainTime()), "arrival");
                this.remoteViews.setTextViewText(C0258R.id.widget1_remain_time, UtilTimeManager.convertTimeArrival(Integer.parseInt(BusInfo.getRemainTime()), "arrival"));
            }
        } else {
            this.remoteViews.setTextViewText(C0258R.id.widget1_remain_time, "운행종료");
        }
        Intent pendingintent = new Intent(this.mContext, OneCellWidgetProvider.class);
        pendingintent.setAction(SELECT_WIDGET_ITEM);
        pendingintent.putExtra("appWidgetId", this.mData.mWidgetId);
        this.remoteViews.setOnClickPendingIntent(C0258R.id.widget1, PendingIntent.getBroadcast(this.mContext, this.mData.mWidgetId, pendingintent, 134217728));
        this.manager = AppWidgetManager.getInstance(this.mContext);
        this.manager.updateAppWidget(this.mData.mWidgetId, this.remoteViews);
    }

    void restoreScreen(DataInfo item) {
        showWidget();
        this.remoteViews.setTextViewText(C0258R.id.widget1_stop_name, this.mData.mSTopName);
        this.remoteViews.setTextViewText(C0258R.id.widget1_route_number, this.mData.mRouteNo);
        this.remoteViews.setTextViewText(C0258R.id.widget1_remain_time, this.mData.mRemainTime);
        int busType = Integer.parseInt(this.mData.mRouteBusType);
        this.remoteViews.setImageViewResource(C0258R.id.widget1_background, setBackGroundResource(busType));
        this.remoteViews.setTextColor(C0258R.id.widget1_route_number, Color.parseColor(this.mContext.getResources().getStringArray(C0258R.array.bus_type_color)[busType]));
        Intent pendingintent = new Intent(this.mContext, OneCellWidgetProvider.class);
        pendingintent.setAction(SELECT_WIDGET_ITEM);
        pendingintent.putExtra("appWidgetId", this.mData.mWidgetId);
        this.remoteViews.setOnClickPendingIntent(C0258R.id.widget1, PendingIntent.getBroadcast(this.mContext, this.mData.mWidgetId, pendingintent, 134217728));
        this.manager = AppWidgetManager.getInstance(this.mContext);
        this.manager.updateAppWidget(this.mData.mWidgetId, this.remoteViews);
    }

    private int setBackGroundResource(int busType) {
        switch (busType) {
            case 0:
                return C0258R.drawable.bus_background_1x1;
            case 1:
                return C0258R.drawable.bus_background_special;
            case 2:
                return C0258R.drawable.bus_background_limousine;
            case 3:
                return C0258R.drawable.bus_background_town;
            case 4:
                return C0258R.drawable.bus_background_jisun;
            default:
                return 0;
        }
    }

    void saveInfo(Context context) {
        ContentValues value = new ContentValues();
        value.put("widget_id", Integer.valueOf(this.mData.mWidgetId));
        value.put("route_id", this.mData.mRouteId);
        value.put("route_no", this.mData.mRouteNo);
        value.put("stop_id", this.mData.mStopId);
        value.put("stop_name", this.mData.mSTopName);
        value.put("route_bus_type", this.mData.mRouteBusType);
        value.put("remain_time", this.mData.mRemainTime);
        this.mContext.getContentResolver().update(DataConst.CONTENT_WIDGET1_URI, value, "widget_id = " + this.mData.mWidgetId, null);
    }

    public void onEnabled(Context context) {
        super.onEnabled(context);
        getInfo("");
        if (this.mItem.peek() != null) {
            this.mData = (DataInfo) this.mItem.poll();
            this.handler.sendEmptyMessage(2);
        }
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        getInfo("");
        if (this.mItem.peek() != null) {
            this.mData = (DataInfo) this.mItem.poll();
            this.handler.sendEmptyMessage(2);
        }
    }

    static void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        widgetId = appWidgetId;
        Intent intent = new Intent();
        intent.setAction(SELECT_WIDGET_ITEM);
        intent.putExtra("appWidgetId", widgetId);
        context.sendBroadcast(intent);
    }

    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        for (int widgetid : appWidgetIds) {
            context.getContentResolver().delete(DataConst.CONTENT_WIDGET1_URI, "widget_id = " + widgetid, null);
        }
    }

    void hideWidget() {
        this.remoteViews = new RemoteViews(this.mContext.getPackageName(), C0258R.layout.widget_1x1_layout);
        this.remoteViews.setViewVisibility(C0258R.id.widget1_progress, 0);
        this.remoteViews.setViewVisibility(C0258R.id.widget1_stop_name, 8);
        this.remoteViews.setViewVisibility(C0258R.id.widget1_route_number, 8);
        this.remoteViews.setViewVisibility(C0258R.id.widget1_remain_time, 8);
        this.manager = AppWidgetManager.getInstance(this.mContext);
        this.manager.updateAppWidget(this.mData.mWidgetId, this.remoteViews);
    }

    void showWidget() {
        this.remoteViews = new RemoteViews(this.mContext.getPackageName(), C0258R.layout.widget_1x1_layout);
        this.remoteViews.setViewVisibility(C0258R.id.widget1_progress, 8);
        this.remoteViews.setViewVisibility(C0258R.id.widget1_stop_name, 0);
        this.remoteViews.setViewVisibility(C0258R.id.widget1_route_number, 0);
        this.remoteViews.setViewVisibility(C0258R.id.widget1_remain_time, 0);
        this.remoteViews.setTextViewText(C0258R.id.widget1_stop_name, this.mData.mSTopName);
        this.remoteViews.setTextViewText(C0258R.id.widget1_route_number, this.mData.mRouteNo);
    }

    void getInfo(String arg0) {
        String condition = null;
        if (arg0.length() > 0) {
            condition = "widget_id = " + arg0;
        }
        try {
            Cursor cursor = this.mContext.getContentResolver().query(DataConst.CONTENT_WIDGET1_URI, null, condition, null, null);
            if (cursor.moveToFirst()) {
                do {
                    this.mData = new DataInfo();
                    this.mData.mRouteId = cursor.getString(cursor.getColumnIndex("route_id"));
                    this.mData.mRouteNo = cursor.getString(cursor.getColumnIndex("route_no"));
                    this.mData.mStopId = cursor.getString(cursor.getColumnIndex("stop_id"));
                    this.mData.mSTopName = cursor.getString(cursor.getColumnIndex("stop_name"));
                    this.mData.mWidgetId = cursor.getInt(cursor.getColumnIndex("widget_id"));
                    this.mData.mRouteBusType = cursor.getString(cursor.getColumnIndex("route_bus_type"));
                    this.mData.mRemainTime = cursor.getString(cursor.getColumnIndex("remain_time"));
                    this.mItem.offer(this.mData);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
        }
    }

    public void onReceive(Context context, Intent intent) {
        widgetId = intent.getIntExtra("appWidgetId", 0);
        this.mContext = context;
        if (SELECT_WIDGET_ITEM.equals(intent.getAction()) && widgetId != 0) {
            getInfo(String.valueOf(widgetId));
            if (this.mItem.peek() != null) {
                this.mData = (DataInfo) this.mItem.poll();
                new reqWhenTheBusArrive().start();
            }
            hideWidget();
        }
        super.onReceive(context, intent);
    }
}
