package com.neighbor.ulsanbus.ui;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import com.neighbor.ulsanbus.C0258R;
import com.neighbor.ulsanbus.RouteArrival;
import com.neighbor.ulsanbus.data.DataConst;

public class ConfigureWidget2 extends Activity implements OnItemClickListener {
    private FavoritesAdapter Favoradapter;
    private String[] FavoritesProjection = new String[]{DataConst.KEY_NOTICE_ID, DataConst.KEY_FAVORITE_CATEGORY, "favor_routeid", "favor_stopid", DataConst.KEY_FAVORITE_ALARM};
    private int GET_ARRIVAL_TIME = 0;
    private String TAG = ("[ConfigureWidget4_1]" + getClass().getSimpleName());
    private FavoriteRoute favorRoute = null;
    private FavoriteStop favorStop = null;
    private Cursor favoriteCursor;
    private Button mCancelBtn;
    private Context mContext;
    private LinearLayout mFavoriteLayout;
    private ListView mFavoriteList;
    private Handler mHandler;
    private TextView mHeaderTitle;
    private RouteArrival mRouteArrival;
    private RemoteViews views;
    private int widgetId;

    /* renamed from: com.neighbor.ulsanbus.ui.ConfigureWidget2$1 */
    class C02631 implements OnClickListener {
        C02631() {
        }

        public void onClick(View arg0) {
            Intent intent = new Intent(ConfigureWidget2.this, FourByOneCellWidgetProvider.class);
            intent.putExtra("appWidgetId", ConfigureWidget2.this.widgetId);
            ConfigureWidget2.this.setResult(0, intent);
            ConfigureWidget2.this.finish();
        }
    }

    class FavoriteRoute {
        String favorRouteBusTye;
        String favorRouteDir;
        String favorRouteFName;
        String favorRouteId;
        String favorRouteNo;
        String favorRouteRemark;
        String favorRouteTName;

        FavoriteRoute() {
        }
    }

    class FavoriteStop {
        String favoStopId;
        String favoStopName;
        String favoStopRemark;
        String favoStopX;
        String favoStopY;

        FavoriteStop() {
        }
    }

    class FavoritesAdapter extends CursorAdapter {
        private Cursor cursor;
        private Context mContext;
        private LayoutInflater mInflater;
        private int[] resIds;

        public FavoritesAdapter(Context context, Cursor c) {
            super(context, c);
            this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
            this.cursor = c;
            this.mContext = context;
            TypedArray busType = this.mContext.getResources().obtainTypedArray(C0258R.array.bus_type);
            this.resIds = new int[busType.length()];
            for (int i = 0; i < this.resIds.length; i++) {
                this.resIds[i] = busType.getResourceId(i, 0);
            }
            busType.recycle();
        }

        public void bindView(View view, Context context, Cursor cursor) {
            ImageView categoryIcon = (ImageView) view.findViewById(C0258R.id.widget_category_image);
            TextView Text1 = (TextView) view.findViewById(C0258R.id.widget_text_item1);
            TextView SubText = (TextView) view.findViewById(C0258R.id.widget_text_sub1);
            SubText.setVisibility(8);
            TextView Text2 = (TextView) view.findViewById(C0258R.id.widget_text_item2);
            if (cursor.getCount() > 0) {
                String category = cursor.getString(cursor.getColumnIndex(DataConst.KEY_FAVORITE_CATEGORY));
                String routeId = cursor.getString(cursor.getColumnIndex("favor_routeid"));
                String stopId = cursor.getString(cursor.getColumnIndex("favor_stopid"));
                String alarmStatus = cursor.getString(cursor.getColumnIndex(DataConst.KEY_FAVORITE_ALARM));
                String[] colorArray = this.mContext.getResources().getStringArray(C0258R.array.bus_type_color);
                String[] txBusType = this.mContext.getResources().getStringArray(C0258R.array.stop_info_header_type);
                Cursor routeCursor = null;
                Cursor stopcursor = null;
                if (!category.equals("2")) {
                    routeCursor = ConfigureWidget2.this.getContentResolver().query(DataConst.CONTENT_ROUTE_URI, new String[]{DataConst.KEY_NOTICE_ID, "route_id", "route_no", DataConst.KEY_ROUTE_DIR, DataConst.KEY_ROUTE_TYPE, "route_bus_type", DataConst.KEY_ROUTE_FSTOP_NAME, DataConst.KEY_ROUTE_TSTOP_NAME, DataConst.KEY_ROUTE_REMARK}, "route_id=" + routeId, null, null);
                }
                if (!category.equals("1")) {
                    stopcursor = ConfigureWidget2.this.getContentResolver().query(DataConst.CONTENT_STOPS_URI, new String[]{DataConst.KEY_NOTICE_ID, "stop_name", "stop_id", "stop_remark"}, "stop_id = " + stopId, null, null);
                }
                StringBuffer sb;
                if (category.equals("1")) {
                    if (routeCursor.getCount() > 0 && routeCursor.moveToFirst()) {
                        int busType = Integer.parseInt(routeCursor.getString(routeCursor.getColumnIndex("route_bus_type")));
                        categoryIcon.setImageResource(this.resIds[busType]);
                        Text1.setTextColor(Color.parseColor(colorArray[busType]));
                        Text1.setTextSize(12.66f);
                        sb = new StringBuffer();
                        sb.append(txBusType[busType]);
                        sb.append(routeCursor.getString(routeCursor.getColumnIndex("route_no")));
                        Text1.setText(sb.toString());
                        StringBuffer terminal = new StringBuffer();
                        terminal.append(routeCursor.getString(routeCursor.getColumnIndex(DataConst.KEY_ROUTE_FSTOP_NAME)));
                        terminal.append(" ∼  ");
                        terminal.append(routeCursor.getString(routeCursor.getColumnIndex(DataConst.KEY_ROUTE_TSTOP_NAME)));
                        Text2.setText(terminal.toString());
                        Text2.setTextSize(12.66f);
                        Text2.setTextColor(ViewCompat.MEASURED_STATE_MASK);
                        if (Text2.getVisibility() == 8) {
                            Text2.setVisibility(0);
                        }
                    }
                } else if (category.equals("2") && stopcursor.getCount() > 0) {
                    stopcursor.moveToFirst();
                    String regStopId = stopcursor.getString(stopcursor.getColumnIndex("stop_id"));
                    categoryIcon.setImageResource(C0258R.drawable.icon_favorite_arrival);
                    Text1.setTextColor(Color.parseColor("#9C9C9C"));
                    Text1.setText(" [ " + regStopId + " ] ");
                    Text1.setTextSize(12.66f);
                    Text2.setTextColor(ViewCompat.MEASURED_STATE_MASK);
                    Text2.setTextSize(12.66f);
                    Text2.setText(stopcursor.getString(stopcursor.getColumnIndex("stop_name")));
                    if (Text2.getVisibility() == 8) {
                        Text2.setVisibility(0);
                    }
                } else if (stopcursor.getCount() > 0 && routeCursor.getCount() > 0) {
                    stopcursor.moveToFirst();
                    routeCursor.moveToFirst();
                    categoryIcon.setImageResource(C0258R.drawable.icon_drive_time_interval);
                    sb = new StringBuffer();
                    sb.append(stopcursor.getString(stopcursor.getColumnIndex("stop_name")));
                    String remark = stopcursor.getString(stopcursor.getColumnIndex("stop_remark"));
                    if (!remark.equals("")) {
                        sb.append(" ( " + remark + " )");
                    }
                    Text1.setText(sb.toString());
                    Text1.setTextColor(ViewCompat.MEASURED_STATE_MASK);
                    Text1.setTextSize(12.66f);
                    SubText.setVisibility(0);
                    SubText.setText(routeCursor.getString(routeCursor.getColumnIndex("route_no")));
                    SubText.setTextColor(Color.parseColor("#9C9C9C"));
                    SubText.setTextSize(9.0f);
                    Text2.setVisibility(8);
                }
            }
        }

        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return this.mInflater.inflate(C0258R.layout.configure_item, parent, false);
        }
    }

    public class WidgetInfo {
        String RouteId;
        String RouteNo;
        String StopId;
        String StopName;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            this.widgetId = extras.getInt("appWidgetId", 0);
        }
        this.mContext = this;
        if (VERSION.SDK_INT < 11) {
            Toast.makeText(this.mContext, "os 버젼이 낮아 위젯을 지원하지 않습니다.", 0).show();
            setResult(0, intent);
            finish();
            return;
        }
        Cursor favoriteCursor = getContentResolver().query(DataConst.CONTENT_FAVORITES_URI, this.FavoritesProjection, "category = 3", null, null);
        if (favoriteCursor.getCount() > 0) {
            this.Favoradapter = new FavoritesAdapter(this, favoriteCursor);
            setResult(0, intent);
            setContentView(C0258R.layout.configure);
            this.mFavoriteList = (ListView) findViewById(C0258R.id.widget_item_list);
            this.mFavoriteList.setOnItemClickListener(this);
            this.mFavoriteList.setAdapter(this.Favoradapter);
            this.mCancelBtn = (Button) findViewById(C0258R.id.widget_pop_cancel);
            this.mCancelBtn.setOnClickListener(new C02631());
            return;
        }
        Toast.makeText(this.mContext, "등록된 즐겨 찾기가 없습니다.즐겨 찾기에 먼저 등록해 주세요", 0).show();
        setResult(0, intent);
        finish();
    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Cursor itemCursor = (Cursor) arg0.getAdapter().getItem(arg2);
        String category = itemCursor.getString(itemCursor.getColumnIndex(DataConst.KEY_FAVORITE_CATEGORY));
        String routeId = itemCursor.getString(itemCursor.getColumnIndex("favor_routeid"));
        String stopId = itemCursor.getString(itemCursor.getColumnIndex("favor_stopid"));
        if (!category.equals("2")) {
            Cursor cursor = getContentResolver().query(DataConst.CONTENT_ROUTE_URI, new String[]{DataConst.KEY_NOTICE_ID, "route_id", "route_no", DataConst.KEY_ROUTE_DIR, "route_bus_type", DataConst.KEY_ROUTE_FSTOP_NAME, DataConst.KEY_ROUTE_TSTOP_NAME, DataConst.KEY_ROUTE_REMARK}, "route_id=" + routeId, null, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                this.favorRoute = new FavoriteRoute();
                this.favorRoute.favorRouteId = cursor.getString(cursor.getColumnIndex("route_id"));
                this.favorRoute.favorRouteNo = cursor.getString(cursor.getColumnIndex("route_no"));
                this.favorRoute.favorRouteDir = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_DIR));
                this.favorRoute.favorRouteBusTye = cursor.getString(cursor.getColumnIndex("route_bus_type"));
                this.favorRoute.favorRouteFName = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_FSTOP_NAME));
                this.favorRoute.favorRouteTName = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_TSTOP_NAME));
                this.favorRoute.favorRouteRemark = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_REMARK));
            } else {
                return;
            }
        }
        if (!category.equals("1")) {
            Cursor stopcursor = getContentResolver().query(DataConst.CONTENT_STOPS_URI, new String[]{DataConst.KEY_NOTICE_ID, "stop_name", "stop_id", DataConst.KEY_STOP_X, DataConst.KEY_STOP_Y, "stop_remark"}, "stop_id = " + stopId, null, null);
            if (stopcursor.getCount() > 0 && stopcursor.moveToFirst()) {
                this.favorStop = new FavoriteStop();
                this.favorStop.favoStopId = stopcursor.getString(stopcursor.getColumnIndex("stop_id"));
                this.favorStop.favoStopName = stopcursor.getString(stopcursor.getColumnIndex("stop_name"));
                this.favorStop.favoStopX = stopcursor.getString(stopcursor.getColumnIndex(DataConst.KEY_STOP_X));
                this.favorStop.favoStopY = stopcursor.getString(stopcursor.getColumnIndex(DataConst.KEY_STOP_Y));
                this.favorStop.favoStopRemark = stopcursor.getString(stopcursor.getColumnIndex("stop_remark"));
                ContentValues value = new ContentValues();
                value.put("widget_id", Integer.valueOf(this.widgetId));
                value.put("route_id", this.favorRoute.favorRouteId);
                value.put("route_no", this.favorRoute.favorRouteNo);
                value.put("stop_id", this.favorStop.favoStopId);
                value.put("stop_name", this.favorStop.favoStopName);
                value.put("stop_remark", this.favorStop.favoStopRemark);
                value.put("route_bus_type", this.favorRoute.favorRouteBusTye);
                getContentResolver().insert(DataConst.CONTENT_WIDGET2_URI, value);
                FourByOneCellWidgetProvider.updateWidget(this.mContext, AppWidgetManager.getInstance(this.mContext), this.widgetId);
                Intent intent = new Intent(this, FourByOneCellWidgetProvider.class);
                intent.putExtra("appWidgetId", this.widgetId);
                setResult(-1, intent);
                finish();
            }
        }
    }
}
