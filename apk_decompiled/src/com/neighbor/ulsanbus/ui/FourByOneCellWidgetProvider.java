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

public class FourByOneCellWidgetProvider extends AppWidgetProvider {
    private static final int RESULT_DATA_OK = 0;
    private static final int RESULT_RESTORE = 2;
    private static final int RESULT_SERVER_ERROR = 1;
    private static final String SELECT_WIDGET2_ITEM = "com.neighbor.ulsanbus.widget2";
    private static int widgetId;
    private Handler handler = new C02651();
    private ArrayList<RouteArrival> mArrivalList;
    private Context mContext;
    private WidgetData mData;
    private Queue<WidgetData> mItem = new LinkedList();
    private AppWidgetManager manager;
    private RemoteViews remoteViews;

    /* renamed from: com.neighbor.ulsanbus.ui.FourByOneCellWidgetProvider$1 */
    class C02651 extends Handler {
        C02651() {
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                FourByOneCellWidgetProvider.this.showWidget();
                FourByOneCellWidgetProvider.this.setContent();
                FourByOneCellWidgetProvider.this.saveInfo(FourByOneCellWidgetProvider.this.mContext);
                return;
            }
            FourByOneCellWidgetProvider.this.restoreScreen(FourByOneCellWidgetProvider.this.mData);
            if (FourByOneCellWidgetProvider.this.mItem.peek() != null) {
                FourByOneCellWidgetProvider.this.mData = (WidgetData) FourByOneCellWidgetProvider.this.mItem.poll();
                FourByOneCellWidgetProvider.this.handler.sendEmptyMessage(2);
            }
        }
    }

    class RequsetArrival extends Thread {
        ParseArrivalBus mParse;

        RequsetArrival() {
        }

        public void run() {
            this.mParse = new ParseArrivalBus();
            try {
                this.mParse.setRouteBus(FourByOneCellWidgetProvider.this.mData.mRouteId, FourByOneCellWidgetProvider.this.mData.mStopId);
                this.mParse.parse();
                FourByOneCellWidgetProvider.this.mArrivalList = (ArrayList) this.mParse.get();
                FourByOneCellWidgetProvider.this.handler.sendEmptyMessage(0);
            } catch (XmlPullParserException e) {
                FourByOneCellWidgetProvider.this.handler.sendEmptyMessage(1);
            } catch (IOException e2) {
                FourByOneCellWidgetProvider.this.handler.sendEmptyMessage(1);
            }
        }
    }

    static class WidgetData {
        String mLowType;
        String mRemainTime1;
        String mRemainTime2;
        String mRouteBusType;
        String mRouteId;
        String mRouteNo;
        String mSTopName;
        String mStopId;
        String mStopRemark;
        int mWidgetId;

        WidgetData() {
        }
    }

    int getImageFromBusType(String busType) {
        if (busType.equals("0")) {
            return C0258R.drawable.icon_bus_normal_4x1;
        }
        if (busType.equals("1")) {
            return C0258R.drawable.icon_bus_special_4x1;
        }
        if (busType.equals("2")) {
            return C0258R.drawable.icon_bus_limousine_4x1;
        }
        if (busType.equals("3")) {
            return C0258R.drawable.icon_bus_town_4x1;
        }
        if (busType.equals("4")) {
            return C0258R.drawable.icon_bus_jisun_4x1;
        }
        return 0;
    }

    void setContent() {
        int intBusType = Integer.parseInt(this.mData.mRouteBusType);
        this.remoteViews.setTextColor(C0258R.id.widget2_RouteNo, Color.parseColor(this.mContext.getResources().getStringArray(C0258R.array.bus_type_color)[intBusType]));
        this.remoteViews.setTextViewText(C0258R.id.widget2_RouteNo, this.mData.mRouteNo);
        this.remoteViews.setTextViewText(C0258R.id.widget2_stop_name, this.mData.mSTopName);
        this.remoteViews.setImageViewResource(C0258R.id.widget_1st_route, getImageFromBusType(this.mData.mRouteBusType));
        this.remoteViews.setImageViewResource(C0258R.id.widget_2nd_route, getImageFromBusType(this.mData.mRouteBusType));
        if (this.mArrivalList.size() > 0) {
            int MaxLength;
            if (this.mArrivalList.size() >= 2) {
                MaxLength = 2;
            } else {
                MaxLength = this.mArrivalList.size();
            }
            for (int i = 0; i < MaxLength; i++) {
                StringBuffer sb = new StringBuffer();
                RouteArrival item = (RouteArrival) this.mArrivalList.get(i);
                try {
                    if (item.getStatus().equals("1")) {
                        sb.append(item.getRemainTime());
                        sb.insert(2, ":");
                        sb.append("출발 예정");
                    } else if (item.getStatus().equals("0")) {
                        sb.append(UtilTimeManager.convertTimeArrival(Integer.parseInt(item.getRemainTime()), "arrival"));
                    }
                    if (item.getLowType() != null && item.getLowType().equals("1")) {
                        sb.append(" (저상)");
                    }
                    if (i == 0) {
                        this.remoteViews.setTextViewText(C0258R.id.widget_1st_time, sb.toString());
                        this.mData.mRemainTime1 = sb.toString();
                    } else {
                        this.remoteViews.setTextViewText(C0258R.id.widget_2nd_time, sb.toString());
                        this.mData.mRemainTime2 = sb.toString();
                    }
                } catch (NullPointerException e) {
                }
            }
            if (MaxLength < 2) {
                this.remoteViews.setTextViewText(C0258R.id.widget_2nd_time, "운행 종료");
                this.mData.mRemainTime2 = "운행종료";
            }
        } else {
            this.remoteViews.setTextViewText(C0258R.id.widget_2nd_time, "운행 종료");
            this.remoteViews.setTextViewText(C0258R.id.widget_1st_time, "운행 종료");
            this.mData.mRemainTime2 = "운행종료";
            this.mData.mRemainTime1 = "운행종료";
        }
        Intent pendingintent = new Intent(this.mContext, FourByOneCellWidgetProvider.class);
        pendingintent.setAction(SELECT_WIDGET2_ITEM);
        pendingintent.putExtra("appWidgetId", this.mData.mWidgetId);
        this.remoteViews.setOnClickPendingIntent(C0258R.id.widget2_refresh, PendingIntent.getBroadcast(this.mContext, this.mData.mWidgetId, pendingintent, 134217728));
        this.manager = AppWidgetManager.getInstance(this.mContext);
        this.manager.updateAppWidget(this.mData.mWidgetId, this.remoteViews);
    }

    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    public void onEnabled(Context context) {
        super.onEnabled(context);
        getInfo("");
        if (this.mItem.peek() != null) {
            this.mData = (WidgetData) this.mItem.poll();
            this.handler.sendEmptyMessage(2);
        }
    }

    void restoreScreen(WidgetData item) {
        int intBusType = Integer.parseInt(item.mRouteBusType);
        String[] colorArray = this.mContext.getResources().getStringArray(C0258R.array.bus_type_color);
        showWidget();
        this.remoteViews.setImageViewResource(C0258R.id.widget_1st_route, getImageFromBusType(item.mRouteBusType));
        this.remoteViews.setImageViewResource(C0258R.id.widget_2nd_route, getImageFromBusType(item.mRouteBusType));
        this.remoteViews.setTextColor(C0258R.id.widget2_RouteNo, Color.parseColor(colorArray[intBusType]));
        this.remoteViews.setTextViewText(C0258R.id.widget2_RouteNo, item.mRouteNo);
        StringBuffer stopName = new StringBuffer();
        stopName.append(item.mSTopName);
        if (item.mStopRemark.length() > 0) {
            stopName.append(" ( ");
            stopName.append(item.mStopRemark);
            stopName.append(" )");
        }
        this.remoteViews.setTextViewText(C0258R.id.widget2_stop_name, stopName.toString());
        this.remoteViews.setTextViewText(C0258R.id.widget_2nd_time, item.mRemainTime2);
        this.remoteViews.setTextViewText(C0258R.id.widget_1st_time, this.mData.mRemainTime1);
        Intent pendingintent = new Intent(this.mContext, FourByOneCellWidgetProvider.class);
        pendingintent.setAction(SELECT_WIDGET2_ITEM);
        pendingintent.putExtra("appWidgetId", this.mData.mWidgetId);
        this.remoteViews.setOnClickPendingIntent(C0258R.id.widget2_refresh, PendingIntent.getBroadcast(this.mContext, this.mData.mWidgetId, pendingintent, 134217728));
        this.manager = AppWidgetManager.getInstance(this.mContext);
        this.manager.updateAppWidget(this.mData.mWidgetId, this.remoteViews);
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        getInfo("");
        if (this.mItem.peek() != null) {
            this.mData = (WidgetData) this.mItem.poll();
            this.handler.sendEmptyMessage(2);
        }
    }

    static void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        widgetId = appWidgetId;
        Intent intent = new Intent();
        intent.setAction(SELECT_WIDGET2_ITEM);
        intent.putExtra("appWidgetId", widgetId);
        context.sendBroadcast(intent);
    }

    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        for (int widgetid : appWidgetIds) {
            context.getContentResolver().delete(DataConst.CONTENT_WIDGET2_URI, "widget_id = " + widgetid, null);
        }
    }

    void saveInfo(Context context) {
        ContentValues cv = new ContentValues();
        cv.put("widget_id", Integer.valueOf(this.mData.mWidgetId));
        cv.put("route_id", this.mData.mRouteId);
        cv.put("route_no", this.mData.mRouteNo);
        cv.put("stop_id", this.mData.mStopId);
        cv.put("stop_name", this.mData.mSTopName);
        cv.put("stop_remark", this.mData.mStopRemark);
        cv.put(DataConst.KEY_WIDGET2_REMAINTIME1, this.mData.mRemainTime1);
        cv.put(DataConst.KEY_WIDGET2_REMAINTIME2, this.mData.mRemainTime2);
        context.getContentResolver().update(DataConst.CONTENT_WIDGET2_URI, cv, "widget_id = " + this.mData.mWidgetId, null);
    }

    void getInfo(String arg0) {
        String condition = null;
        if (arg0.length() > 0) {
            condition = "widget_id = " + arg0;
        }
        try {
            Cursor cursor = this.mContext.getContentResolver().query(DataConst.CONTENT_WIDGET2_URI, null, condition, null, null);
            if (cursor.moveToFirst()) {
                do {
                    this.mData = new WidgetData();
                    this.mData.mRouteId = cursor.getString(cursor.getColumnIndex("route_id"));
                    this.mData.mRouteNo = cursor.getString(cursor.getColumnIndex("route_no"));
                    this.mData.mStopId = cursor.getString(cursor.getColumnIndex("stop_id"));
                    this.mData.mSTopName = cursor.getString(cursor.getColumnIndex("stop_name"));
                    this.mData.mWidgetId = cursor.getInt(cursor.getColumnIndex("widget_id"));
                    this.mData.mStopRemark = cursor.getString(cursor.getColumnIndex("stop_remark"));
                    this.mData.mRouteBusType = cursor.getString(cursor.getColumnIndex("route_bus_type"));
                    this.mData.mRemainTime1 = cursor.getString(cursor.getColumnIndex(DataConst.KEY_WIDGET2_REMAINTIME1));
                    this.mData.mRemainTime2 = cursor.getString(cursor.getColumnIndex(DataConst.KEY_WIDGET2_REMAINTIME2));
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
        if (SELECT_WIDGET2_ITEM.equals(intent.getAction()) && widgetId != 0) {
            getInfo(String.valueOf(widgetId));
            if (this.mItem.peek() != null) {
                this.mData = (WidgetData) this.mItem.poll();
                new RequsetArrival().start();
            }
            hideWidget();
        }
        super.onReceive(context, intent);
    }

    void hideWidget() {
        this.remoteViews = new RemoteViews(this.mContext.getPackageName(), C0258R.layout.widget_4x1_layout);
        this.remoteViews.setViewVisibility(C0258R.id.widget2_progress, 0);
        this.remoteViews.setViewVisibility(C0258R.id.widget2_stop_name, 8);
        this.remoteViews.setViewVisibility(C0258R.id.widget_2nd_time, 8);
        this.remoteViews.setViewVisibility(C0258R.id.widget_2nd_route, 8);
        this.remoteViews.setViewVisibility(C0258R.id.widget2_RouteNo, 8);
        this.remoteViews.setViewVisibility(C0258R.id.widget_1st_time, 8);
        this.remoteViews.setViewVisibility(C0258R.id.widget_1st_route, 8);
        this.manager = AppWidgetManager.getInstance(this.mContext);
        this.manager.updateAppWidget(this.mData.mWidgetId, this.remoteViews);
    }

    void showWidget() {
        this.remoteViews = new RemoteViews(this.mContext.getPackageName(), C0258R.layout.widget_4x1_layout);
        this.remoteViews.setViewVisibility(C0258R.id.widget2_progress, 8);
        this.remoteViews.setViewVisibility(C0258R.id.widget2_stop_name, 0);
        this.remoteViews.setViewVisibility(C0258R.id.widget_2nd_time, 0);
        this.remoteViews.setViewVisibility(C0258R.id.widget_2nd_route, 0);
        this.remoteViews.setViewVisibility(C0258R.id.widget2_RouteNo, 0);
        this.remoteViews.setViewVisibility(C0258R.id.widget_1st_time, 0);
        this.remoteViews.setViewVisibility(C0258R.id.widget_1st_route, 0);
    }
}
