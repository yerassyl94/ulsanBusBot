package com.neighbor.ulsanbus.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;
import com.neighbor.ulsanbus.C0258R;
import com.neighbor.ulsanbus.data.DataConst;
import com.neighbor.ulsanbus.data.DataSAXParser;
import com.neighbor.ulsanbus.util.UtilTimeManager;
import java.util.ArrayList;
import java.util.Iterator;

public class UiWidgetListProvider implements RemoteViewsFactory {
    private static Context context = null;
    private int appWidgetId;
    private ArrayList<DispDataInfo> mArrivalList = new ArrayList();
    private DataSAXParser mSaxHelper;
    private ArrayList<ShowRouteInfo> mShowInfo = new ArrayList();
    private String mStopId;
    private String mStopName;
    private String mStopRemark;
    private WidgetData mWidgetInfo;
    private RemoteViews remoteView;

    private class ShowRouteInfo {
        String mArrivalTime;
        String mRouteBusType;
        String mRouteId;
        String mRouteInterval;
        String mRouteNo;
        String mStatus;
        String mTStopName;
        int mWidgetId;

        private ShowRouteInfo() {
        }
    }

    public UiWidgetListProvider(Context context, Intent intent) {
        context = context;
        this.appWidgetId = intent.getIntExtra("appWidgetId", 0);
        this.mStopId = intent.getExtras().getString("stop_id");
        this.mStopName = intent.getExtras().getString("stop_name");
        this.mStopRemark = intent.getExtras().getString("stap_remark");
    }

    public int getCount() {
        Log.d("UiWidgetListProvider", "getCount");
        return this.mShowInfo.size();
    }

    public long getItemId(int position) {
        Log.d("UiWidgetListProvider", "getItemId" + position);
        return (long) position;
    }

    public RemoteViews getViewAt(int position) {
        Log.d("UiWidgetListProvider", "getViewAt");
        Log.d("UiWidgetListProvider", "mShowInfo.size()" + this.mShowInfo.size());
        if (this.mShowInfo.size() <= 0) {
            return null;
        }
        RemoteViews rv = new RemoteViews(context.getPackageName(), C0258R.layout.widget_4x2_item);
        ShowRouteInfo listItem = (ShowRouteInfo) this.mShowInfo.get(position);
        Log.d("UiWidgetListProvider", "position" + position);
        String[] colorArray = context.getResources().getStringArray(C0258R.array.bus_type_color);
        String[] BusTypeArray = context.getResources().getStringArray(C0258R.array.stop_info_header_type);
        int BusType = Integer.parseInt(listItem.mRouteBusType);
        rv.setTextColor(C0258R.id.widget_type_number, Color.parseColor(colorArray[BusType]));
        StringBuffer sb = new StringBuffer();
        sb.append(BusTypeArray[BusType]);
        sb.append(listItem.mRouteNo);
        Log.d("UiWidgetListProvider", sb.toString());
        rv.setTextViewText(C0258R.id.widget_type_number, sb.toString());
        rv.setTextViewText(C0258R.id.widget_bus_destination, listItem.mTStopName + " 방면");
        if (listItem.mStatus.equals("1")) {
            StringBuffer strbuffer = new StringBuffer();
            strbuffer.append(listItem.mArrivalTime);
            strbuffer.insert(2, ":");
            strbuffer.append("출발 예정");
            rv.setTextViewText(C0258R.id.widget_bus_item_arravaltime, strbuffer.toString());
            return rv;
        }
        rv.setTextViewText(C0258R.id.widget_bus_item_arravaltime, UtilTimeManager.convertTimeArrival(Integer.parseInt(listItem.mArrivalTime), "arrival"));
        return rv;
    }

    public RemoteViews getLoadingView() {
        return null;
    }

    public int getViewTypeCount() {
        Log.d("UiWidgetListProvider", "getViewTypeCount");
        return 1;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onCreate() {
        Log.d("UiWidgetListProvider", "onCreate");
        if (this.mArrivalList.size() > 0) {
            this.mArrivalList.clear();
        }
        WidgetData wd = (WidgetData) context.getApplicationContext();
        wd.initWidgetData();
        Cursor itemCursor = context.getContentResolver().query(DataConst.CONTENT_WIDGET3_LIST_URI, null, "stop_id = " + this.mStopId + " AND " + "widget_id" + " = " + this.appWidgetId, null, null);
        Log.d("FourByTwoCellWidget", "itemCursor " + itemCursor.getCount());
        if (itemCursor.getCount() > 0 && itemCursor.moveToFirst()) {
            do {
                wd.setWidgetData(itemCursor.getString(itemCursor.getColumnIndex("route_id")), itemCursor.getString(itemCursor.getColumnIndex("status")), itemCursor.getString(itemCursor.getColumnIndex("remain_time")));
            } while (itemCursor.moveToNext());
            itemCursor.close();
        }
        this.mArrivalList = wd.getDisplaylist();
        Log.d("UiWidgetListProvider", "mArrivalList.size()" + this.mArrivalList.size());
        Iterator it = this.mArrivalList.iterator();
        while (it.hasNext()) {
            DispDataInfo item = (DispDataInfo) it.next();
            Cursor cursor = context.getContentResolver().query(DataConst.CONTENT_ROUTE_URI, new String[]{DataConst.KEY_NOTICE_ID, "route_id", "route_no", DataConst.KEY_ROUTE_DIR, DataConst.KEY_ROUTE_TYPE, "route_bus_type", DataConst.KEY_ROUTE_FSTOP_NAME, DataConst.KEY_ROUTE_TSTOP_NAME, DataConst.KEY_ROUTE_REMARK, "route_interval"}, "route_id = " + item.mRouteId, null, null);
            if (cursor.moveToFirst() && cursor.moveToFirst()) {
                ShowRouteInfo routeInfo = new ShowRouteInfo();
                routeInfo.mRouteId = cursor.getString(cursor.getColumnIndex("route_id"));
                routeInfo.mRouteNo = cursor.getString(cursor.getColumnIndex("route_no"));
                routeInfo.mRouteBusType = cursor.getString(cursor.getColumnIndex("route_bus_type"));
                routeInfo.mTStopName = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_TSTOP_NAME));
                routeInfo.mRouteInterval = cursor.getString(cursor.getColumnIndex("route_interval"));
                routeInfo.mStatus = item.mStatus;
                routeInfo.mArrivalTime = item.mArrivalTime;
                routeInfo.mWidgetId = this.appWidgetId;
                this.mShowInfo.add(routeInfo);
            }
            cursor.close();
        }
    }

    public void onDataSetChanged() {
        Log.d("UiWidgetListProvider", "onDataSetChanged");
        this.mWidgetInfo = (WidgetData) context.getApplicationContext();
        if (this.mWidgetInfo.getDisplaylist().size() > 0) {
            this.mArrivalList = this.mWidgetInfo.getDisplaylist();
            Log.d("UiWidgetListProvider", "mArrivalList.size():" + this.mArrivalList.size());
            for (int i = 0; i < this.mArrivalList.size(); i++) {
                ShowRouteInfo info = (ShowRouteInfo) this.mShowInfo.get(i);
                info.mArrivalTime = ((DispDataInfo) this.mArrivalList.get(i)).mArrivalTime;
                info.mStatus = ((DispDataInfo) this.mArrivalList.get(i)).mStatus;
                this.mShowInfo.set(i, info);
            }
        }
    }

    public void onDestroy() {
        Log.d("UiWidgetListProvider", "onDestroy");
    }
}
