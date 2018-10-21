package com.neighbor.ulsanbus.ui;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.neighbor.ulsanbus.C0258R;
import com.neighbor.ulsanbus.data.DataConst;

public class UiFavorite extends Activity implements OnClickListener, OnItemClickListener, OnItemLongClickListener {
    private static final int REQUEST_ROUTE_DELELE_ALL = 10;
    private FavoritesAdapter Favoradapter;
    private String[] FavoritesProjection = new String[]{DataConst.KEY_NOTICE_ID, DataConst.KEY_FAVORITE_CATEGORY, "favor_routeid", "favor_stopid", DataConst.KEY_FAVORITE_ALARM};
    private String SearchMode = null;
    private AlarmManager alarmManager;
    private boolean bEmergency;
    private Cursor favoriteCursor;
    private ImageView mBgImage;
    private Context mContext;
    private LinearLayout mFavoriteLayout;
    private ListView mFavoriteList;
    private TextView mHeaderTitle;
    private Button mSelectAll;
    private Button mSelectArrival;
    private Button mSelectBus;
    private Button mSelectStation;
    private PendingIntent pendingIntent;
    private SharedPreferences prefs;

    /* renamed from: com.neighbor.ulsanbus.ui.UiFavorite$1 */
    class C02751 implements OnMenuItemClickListener {
        C02751() {
        }

        public boolean onMenuItemClick(MenuItem item) {
            Intent intent = new Intent(UiFavorite.this, UiDeletePopUp.class);
            intent.putExtra("delete_mode", DataConst.TABLE_FAVORITE_INFO);
            UiFavorite.this.startActivityForResult(intent, 10);
            return true;
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiFavorite$2 */
    class C02762 implements DialogInterface.OnClickListener {
        C02762() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }

    class FavoriteRoute {
        String favorAppRemark;
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

        FavoriteStop() {
        }
    }

    class FavoritesAdapter extends CursorAdapter {
        private Cursor cursor;
        private Context mContext;
        private LayoutInflater mInflater;
        private int[] resIds;

        /* renamed from: com.neighbor.ulsanbus.ui.UiFavorite$FavoritesAdapter$1 */
        class C02781 implements OnClickListener {
            C02781() {
            }

            public void onClick(View arg0) {
                Intent intent = new Intent(UiFavorite.this, UiAlarm.class);
                Bundle dataBundle = (Bundle) arg0.getTag();
                if (dataBundle != null) {
                    intent.putExtra("stop_id", dataBundle.getString("stop_id"));
                    intent.putExtra("route_id", dataBundle.getString("route_id"));
                    intent.putExtra("stop_name", dataBundle.getString("stop_name"));
                    intent.putExtra("route_bus_type", dataBundle.getString("route_bus_type"));
                }
                TabThird.TAB3.replaceActivity("Favorite", intent, UiFavorite.this);
            }
        }

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
            try {
                RelativeLayout row = (RelativeLayout) view.findViewById(C0258R.id.favorite_item_row);
                if (UiFavorite.this.bEmergency) {
                    row.setBackgroundResource(C0258R.drawable.list_emer_selector);
                } else {
                    row.setBackgroundResource(C0258R.drawable.list_selector);
                }
                ImageView categoryIcon = (ImageView) view.findViewById(C0258R.id.favorite_category_image);
                TextView Text1 = (TextView) view.findViewById(C0258R.id.favorite_text_item1);
                TextView SubText = (TextView) view.findViewById(C0258R.id.favorite_text_sub1);
                SubText.setVisibility(8);
                TextView Text2 = (TextView) view.findViewById(C0258R.id.favorite_text_item2);
                Button btnAlarm = (Button) view.findViewById(C0258R.id.favorite_btn_alarm);
                btnAlarm.setVisibility(8);
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
                        routeCursor = UiFavorite.this.getContentResolver().query(DataConst.CONTENT_ROUTE_URI, new String[]{DataConst.KEY_NOTICE_ID, "route_id", "route_no", DataConst.KEY_ROUTE_DIR, DataConst.KEY_ROUTE_TYPE, "route_bus_type", DataConst.KEY_ROUTE_FSTOP_NAME, DataConst.KEY_ROUTE_TSTOP_NAME, DataConst.KEY_ROUTE_REMARK, DataConst.KEY_BRT_APP_REMARK}, "route_id=" + routeId, null, null);
                    }
                    if (!category.equals("1")) {
                        stopcursor = UiFavorite.this.getContentResolver().query(DataConst.CONTENT_STOPS_URI, new String[]{DataConst.KEY_NOTICE_ID, "stop_name", "stop_id", "stop_remark"}, "stop_id = " + stopId, null, null);
                    }
                    StringBuffer sb;
                    if (category.equals("1")) {
                        if (routeCursor.getCount() > 0 && routeCursor.moveToFirst()) {
                            int busType = Integer.parseInt(routeCursor.getString(routeCursor.getColumnIndex("route_bus_type")));
                            categoryIcon.setImageResource(this.resIds[busType]);
                            Text1.setTextColor(Color.parseColor(colorArray[busType]));
                            Text1.setTextSize(16.66f);
                            sb = new StringBuffer();
                            sb.append(txBusType[busType]);
                            sb.append(routeCursor.getString(routeCursor.getColumnIndex("route_no")));
                            Text1.setText(sb.toString());
                            StringBuffer terminal = new StringBuffer();
                            terminal.append(routeCursor.getString(routeCursor.getColumnIndex(DataConst.KEY_ROUTE_FSTOP_NAME)));
                            if (routeCursor.getString(routeCursor.getColumnIndex(DataConst.KEY_ROUTE_REMARK)).length() > 0) {
                                terminal.append(" > ");
                                terminal.append(routeCursor.getString(routeCursor.getColumnIndex(DataConst.KEY_ROUTE_REMARK)));
                                terminal.append(" > ");
                            } else {
                                terminal.append(" ◀▶  ");
                            }
                            terminal.append(routeCursor.getString(routeCursor.getColumnIndex(DataConst.KEY_ROUTE_TSTOP_NAME)));
                            Text2.setText(terminal.toString());
                            Text2.setTextSize(16.66f);
                            Text2.setTextColor(ViewCompat.MEASURED_STATE_MASK);
                            if (Text2.getVisibility() == 8) {
                                Text2.setVisibility(0);
                            }
                        }
                    } else if (category.equals("2") && stopcursor.getCount() > 0 && stopcursor.moveToFirst()) {
                        regStopId = stopcursor.getString(stopcursor.getColumnIndex("stop_id"));
                        String regStopRemark = stopcursor.getString(stopcursor.getColumnIndex("stop_remark"));
                        categoryIcon.setImageResource(C0258R.drawable.icon_favorite_arrival);
                        Text1.setTextColor(Color.parseColor("#9C9C9C"));
                        Text1.setText(" [ " + regStopId + " ] ");
                        Text1.setTextSize(16.66f);
                        Text2.setTextColor(ViewCompat.MEASURED_STATE_MASK);
                        Text2.setTextSize(16.66f);
                        StringBuffer favorStop = new StringBuffer();
                        favorStop.append(stopcursor.getString(stopcursor.getColumnIndex("stop_name")));
                        if (regStopRemark.length() > 0) {
                            favorStop.append(" ( " + regStopRemark + "  ) ");
                        }
                        Text2.setText(favorStop.toString());
                        if (Text2.getVisibility() == 8) {
                            Text2.setVisibility(0);
                        }
                    } else if (stopcursor.getCount() > 0 && stopcursor.moveToFirst() && routeCursor.getCount() > 0 && routeCursor.moveToFirst()) {
                        categoryIcon.setImageResource(C0258R.drawable.icon_drive_time_interval);
                        sb = new StringBuffer();
                        sb.append(stopcursor.getString(stopcursor.getColumnIndex("stop_name")));
                        String remark = stopcursor.getString(stopcursor.getColumnIndex("stop_remark"));
                        if (!remark.equals("")) {
                            sb.append(" ( " + remark + " )");
                        }
                        Text1.setText(sb.toString());
                        Text1.setTextColor(ViewCompat.MEASURED_STATE_MASK);
                        Text1.setTextSize(16.66f);
                        SubText.setVisibility(0);
                        SubText.setText(routeCursor.getString(routeCursor.getColumnIndex("route_no")));
                        SubText.setTextColor(Color.parseColor("#9C9C9C"));
                        SubText.setTextSize(14.0f);
                        regStopId = stopcursor.getString(stopcursor.getColumnIndex("stop_id"));
                        String regRouteId = routeCursor.getString(routeCursor.getColumnIndex("route_id"));
                        String regStopName = stopcursor.getString(stopcursor.getColumnIndex("stop_name"));
                        String regRouteBusType = routeCursor.getString(routeCursor.getColumnIndex("route_bus_type"));
                        String[] txBusTypeArray = this.mContext.getResources().getStringArray(C0258R.array.stop_info_header_type);
                        if (UiFavorite.this.bEmergency) {
                            btnAlarm.setVisibility(4);
                        } else {
                            btnAlarm.setVisibility(0);
                        }
                        Bundle bundle = new Bundle();
                        bundle.putString("stop_id", regStopId);
                        bundle.putString("stop_name", regStopName);
                        bundle.putString("route_id", regRouteId);
                        bundle.putString("route_bus_type", txBusTypeArray[Integer.parseInt(regRouteBusType)]);
                        btnAlarm.setTag(bundle);
                        Cursor alarmCursor = this.mContext.getContentResolver().query(DataConst.CONTENT_ALARM_URI, new String[]{DataConst.KEY_ALARM_ID}, "favor_stopid = " + regStopId + " AND " + "favor_routeid" + " = " + regRouteId, null, null);
                        if (alarmCursor.moveToFirst()) {
                            btnAlarm.setSelected(true);
                        } else {
                            btnAlarm.setSelected(false);
                        }
                        alarmCursor.close();
                        btnAlarm.setOnClickListener(new C02781());
                        Text2.setVisibility(8);
                    }
                }
            } catch (Exception e) {
            }
        }

        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return this.mInflater.inflate(C0258R.layout.favorite_item, parent, false);
        }
    }

    private void init() {
        this.mBgImage = (ImageView) findViewById(C0258R.id.favor_background);
        this.mSelectAll = (Button) findViewById(C0258R.id.favorites_all_btn);
        this.mSelectBus = (Button) findViewById(C0258R.id.favorites_bus_btn);
        this.mSelectStation = (Button) findViewById(C0258R.id.favorites_station_btn);
        this.mSelectArrival = (Button) findViewById(C0258R.id.favorites_arrive_btn);
        this.mFavoriteLayout = (LinearLayout) findViewById(C0258R.id.favorite_item_layout);
        this.mFavoriteLayout.setVisibility(8);
        this.mHeaderTitle = (TextView) findViewById(C0258R.id.favorite_header_textview);
        this.mHeaderTitle.setText("전체");
        this.mFavoriteList = (ListView) findViewById(C0258R.id.favorite_list);
        this.mFavoriteList.setOnItemClickListener(this);
        this.mFavoriteList.setOnItemLongClickListener(this);
        this.mFavoriteList.setVisibility(8);
        this.mSelectAll.setOnClickListener(this);
        this.mSelectAll.setSelected(true);
        this.mSelectBus.setOnClickListener(this);
        this.mSelectStation.setOnClickListener(this);
        this.mSelectArrival.setOnClickListener(this);
        if (this.bEmergency) {
            this.mBgImage.setBackgroundResource(C0258R.drawable.background_emer);
            this.mSelectAll.setBackgroundResource(C0258R.drawable.tab_emer_favorite_all);
            this.mSelectBus.setBackgroundResource(C0258R.drawable.tab_emer_favorite_bus);
            this.mSelectStation.setBackgroundResource(C0258R.drawable.tab_emer_favorite_station);
            this.mSelectArrival.setBackgroundResource(C0258R.drawable.tab_emer_favorite_arrive);
            return;
        }
        this.mBgImage.setBackgroundResource(C0258R.drawable.background_nomal);
        this.mSelectAll.setBackgroundResource(C0258R.drawable.tab_favorite_all);
        this.mSelectBus.setBackgroundResource(C0258R.drawable.tab_favorite_bus);
        this.mSelectStation.setBackgroundResource(C0258R.drawable.tab_favorite_station);
        this.mSelectArrival.setBackgroundResource(C0258R.drawable.tab_favorite_arrive);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0258R.layout.favorite);
        this.prefs = getSharedPreferences("PreStatus", 0);
        this.bEmergency = this.prefs.getBoolean(DataConst.KEY_PREF_EMER_MODE, false);
        this.mContext = this;
        init();
    }

    public void onBackPressed() {
        super.onBackPressed();
        TabThird.TAB3.onBackPressed();
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onResume() {
        super.onResume();
        this.favoriteCursor = getContentResolver().query(DataConst.CONTENT_FAVORITES_URI, new String[]{DataConst.KEY_NOTICE_ID, DataConst.KEY_FAVORITE_CATEGORY, "favor_routeid", "favor_stopid", DataConst.KEY_FAVORITE_ALARM}, null, null, null);
        this.favoriteCursor = makeSorting(this.favoriteCursor);
        try {
            if (this.favoriteCursor.moveToFirst()) {
                this.Favoradapter = new FavoritesAdapter(this, this.favoriteCursor);
                this.mFavoriteList.setAdapter(this.Favoradapter);
                this.Favoradapter.changeCursor(this.favoriteCursor);
                this.mFavoriteLayout.setVisibility(0);
                this.mFavoriteList.setVisibility(0);
                return;
            }
            this.mFavoriteLayout.setVisibility(8);
        } catch (Exception e) {
        }
    }

    protected void onPause() {
        super.onPause();
        this.mFavoriteList.setAdapter(null);
        this.Favoradapter = null;
    }

    protected void onDestroy() {
        super.onDestroy();
        stopManagingCursor(this.favoriteCursor);
        this.mFavoriteList.setAdapter(null);
        this.Favoradapter = null;
    }

    public void onClick(View v) {
        String selection = null;
        this.mSelectAll.setSelected(false);
        this.mSelectBus.setSelected(false);
        this.mSelectStation.setSelected(false);
        this.mSelectArrival.setSelected(false);
        switch (v.getId()) {
            case C0258R.id.favorites_all_btn:
                this.mHeaderTitle.setText("전체");
                this.mSelectAll.setSelected(true);
                this.SearchMode = null;
                break;
            case C0258R.id.favorites_arrive_btn:
                this.mHeaderTitle.setText("도착정보");
                this.mSelectArrival.setSelected(true);
                this.SearchMode = "3";
                break;
            case C0258R.id.favorites_bus_btn:
                this.mHeaderTitle.setText("버스");
                this.mSelectBus.setSelected(true);
                this.SearchMode = "1";
                break;
            case C0258R.id.favorites_station_btn:
                this.mHeaderTitle.setText("정류장");
                this.mSelectStation.setSelected(true);
                this.SearchMode = "2";
                break;
        }
        try {
            if (this.SearchMode != null) {
                selection = "category= " + this.SearchMode;
            }
            this.favoriteCursor = getContentResolver().query(DataConst.CONTENT_FAVORITES_URI, new String[]{DataConst.KEY_NOTICE_ID, DataConst.KEY_FAVORITE_CATEGORY, "favor_routeid", "favor_stopid", DataConst.KEY_FAVORITE_ALARM}, selection, null, null);
            Cursor cursor = makeSorting(this.favoriteCursor);
            if (this.Favoradapter != null) {
                this.Favoradapter.changeCursor(cursor);
            }
        } catch (Exception e) {
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.database.Cursor makeSorting(android.database.Cursor r31) {
        /*
        r30 = this;
        r24 = new java.util.ArrayList;
        r24.<init>();
        r27 = new java.util.ArrayList;
        r27.<init>();
        if (r31 == 0) goto L_0x0049;
    L_0x000c:
        r2 = r31.getCount();
        if (r2 == 0) goto L_0x0049;
    L_0x0012:
        r2 = r31.moveToFirst();
        if (r2 == 0) goto L_0x0049;
    L_0x0018:
        r2 = "category";
        r0 = r31;
        r2 = r0.getColumnIndex(r2);
        r0 = r31;
        r17 = r0.getString(r2);
        r2 = "1";
        r0 = r17;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x00b5;
    L_0x0030:
        r2 = "favor_routeid";
        r0 = r31;
        r2 = r0.getColumnIndex(r2);
        r0 = r31;
        r2 = r0.getString(r2);
        r0 = r24;
        r0.add(r2);
    L_0x0043:
        r2 = r31.moveToNext();
        if (r2 != 0) goto L_0x0018;
    L_0x0049:
        r5 = 0;
        r2 = r24.isEmpty();
        if (r2 != 0) goto L_0x00de;
    L_0x0050:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "route_id=";
        r3 = r2.append(r3);
        r2 = 0;
        r0 = r24;
        r2 = r0.get(r2);
        r2 = (java.lang.String) r2;
        r2 = r2.toString();
        r2 = r3.append(r2);
        r5 = r2.toString();
        r2 = r24.size();
        r3 = 1;
        if (r2 <= r3) goto L_0x00de;
    L_0x0077:
        r20 = 1;
    L_0x0079:
        r2 = r24.size();
        r0 = r20;
        if (r0 >= r2) goto L_0x00de;
    L_0x0081:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2 = r2.append(r5);
        r3 = " OR ";
        r2 = r2.append(r3);
        r3 = "route_id";
        r2 = r2.append(r3);
        r3 = "=";
        r3 = r2.append(r3);
        r0 = r24;
        r1 = r20;
        r2 = r0.get(r1);
        r2 = (java.lang.String) r2;
        r2 = r2.toString();
        r2 = r3.append(r2);
        r5 = r2.toString();
        r20 = r20 + 1;
        goto L_0x0079;
    L_0x00b5:
        r2 = "2";
        r0 = r17;
        r2 = r0.equals(r2);
        if (r2 != 0) goto L_0x00c9;
    L_0x00bf:
        r2 = "3";
        r0 = r17;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x0043;
    L_0x00c9:
        r2 = "favor_stopid";
        r0 = r31;
        r2 = r0.getColumnIndex(r2);
        r0 = r31;
        r2 = r0.getString(r2);
        r0 = r27;
        r0.add(r2);
        goto L_0x0043;
    L_0x00de:
        r9 = 0;
        r2 = r27.isEmpty();
        if (r2 != 0) goto L_0x014a;
    L_0x00e5:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "stop_id=";
        r3 = r2.append(r3);
        r2 = 0;
        r0 = r27;
        r2 = r0.get(r2);
        r2 = (java.lang.String) r2;
        r2 = r2.toString();
        r2 = r3.append(r2);
        r9 = r2.toString();
        r2 = r27.size();
        r3 = 1;
        if (r2 <= r3) goto L_0x014a;
    L_0x010c:
        r20 = 1;
    L_0x010e:
        r2 = r27.size();
        r0 = r20;
        if (r0 >= r2) goto L_0x014a;
    L_0x0116:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2 = r2.append(r9);
        r3 = " OR ";
        r2 = r2.append(r3);
        r3 = "stop_id";
        r2 = r2.append(r3);
        r3 = "=";
        r3 = r2.append(r3);
        r0 = r27;
        r1 = r20;
        r2 = r0.get(r1);
        r2 = (java.lang.String) r2;
        r2 = r2.toString();
        r2 = r3.append(r2);
        r9 = r2.toString();
        r20 = r20 + 1;
        goto L_0x010e;
    L_0x014a:
        r23 = 0;
        r29 = 0;
        r22 = 0;
        r2 = r24.isEmpty();
        if (r2 != 0) goto L_0x019b;
    L_0x0156:
        r2 = r30.getContentResolver();
        r3 = com.neighbor.ulsanbus.data.DataConst.CONTENT_ROUTE_URI;
        r4 = 10;
        r4 = new java.lang.String[r4];
        r6 = 0;
        r7 = "_id";
        r4[r6] = r7;
        r6 = 1;
        r7 = "route_id";
        r4[r6] = r7;
        r6 = 2;
        r7 = "route_no";
        r4[r6] = r7;
        r6 = 3;
        r7 = "route_dir";
        r4[r6] = r7;
        r6 = 4;
        r7 = "route_type";
        r4[r6] = r7;
        r6 = 5;
        r7 = "route_bus_type";
        r4[r6] = r7;
        r6 = 6;
        r7 = "route_fstop_name";
        r4[r6] = r7;
        r6 = 7;
        r7 = "route_tstop_name";
        r4[r6] = r7;
        r6 = 8;
        r7 = "route_remark";
        r4[r6] = r7;
        r6 = 9;
        r7 = "brt_app_remark";
        r4[r6] = r7;
        r6 = 0;
        r7 = "route_no ASC";
        r23 = r2.query(r3, r4, r5, r6, r7);
    L_0x019b:
        r2 = r27.isEmpty();
        if (r2 != 0) goto L_0x01c5;
    L_0x01a1:
        r6 = r30.getContentResolver();
        r7 = com.neighbor.ulsanbus.data.DataConst.CONTENT_STOPS_URI;
        r2 = 4;
        r8 = new java.lang.String[r2];
        r2 = 0;
        r3 = "_id";
        r8[r2] = r3;
        r2 = 1;
        r3 = "stop_name";
        r8[r2] = r3;
        r2 = 2;
        r3 = "stop_id";
        r8[r2] = r3;
        r2 = 3;
        r3 = "stop_remark";
        r8[r2] = r3;
        r10 = 0;
        r11 = "stop_name ASC";
        r29 = r6.query(r7, r8, r9, r10, r11);
    L_0x01c5:
        r25 = new java.util.ArrayList;
        r25.<init>();
        r28 = new java.util.ArrayList;
        r28.<init>();
        if (r23 == 0) goto L_0x01f6;
    L_0x01d1:
        r2 = r23.getCount();
        if (r2 == 0) goto L_0x01f6;
    L_0x01d7:
        r2 = r23.moveToFirst();
        if (r2 == 0) goto L_0x01f6;
    L_0x01dd:
        r2 = "route_id";
        r0 = r23;
        r2 = r0.getColumnIndex(r2);
        r0 = r23;
        r2 = r0.getString(r2);
        r0 = r25;
        r0.add(r2);
        r2 = r23.moveToNext();
        if (r2 != 0) goto L_0x01dd;
    L_0x01f6:
        if (r29 == 0) goto L_0x021d;
    L_0x01f8:
        r2 = r29.getCount();
        if (r2 == 0) goto L_0x021d;
    L_0x01fe:
        r2 = r29.moveToFirst();
        if (r2 == 0) goto L_0x021d;
    L_0x0204:
        r2 = "stop_id";
        r0 = r29;
        r2 = r0.getColumnIndex(r2);
        r0 = r29;
        r2 = r0.getString(r2);
        r0 = r28;
        r0.add(r2);
        r2 = r29.moveToNext();
        if (r2 != 0) goto L_0x0204;
    L_0x021d:
        r18 = 0;
        r19 = 0;
        r16 = new java.util.ArrayList;
        r16.<init>();
        r2 = r25.isEmpty();
        if (r2 != 0) goto L_0x02bb;
    L_0x022c:
        r20 = 0;
    L_0x022e:
        r2 = r25.size();
        r0 = r20;
        if (r0 >= r2) goto L_0x02bb;
    L_0x0236:
        r0 = r30;
        r2 = r0.SearchMode;
        if (r2 != 0) goto L_0x029d;
    L_0x023c:
        r26 = "";
    L_0x023e:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r0 = r26;
        r2 = r2.append(r0);
        r3 = "favor_routeid";
        r2 = r2.append(r3);
        r3 = "=";
        r3 = r2.append(r3);
        r0 = r25;
        r1 = r20;
        r2 = r0.get(r1);
        r2 = (java.lang.String) r2;
        r2 = r2.toString();
        r2 = r3.append(r2);
        r13 = r2.toString();
        r10 = r30.getContentResolver();
        r11 = com.neighbor.ulsanbus.data.DataConst.CONTENT_FAVORITES_URI;
        r2 = 5;
        r12 = new java.lang.String[r2];
        r2 = 0;
        r3 = "_id";
        r12[r2] = r3;
        r2 = 1;
        r3 = "category";
        r12[r2] = r3;
        r2 = 2;
        r3 = "favor_routeid";
        r12[r2] = r3;
        r2 = 3;
        r3 = "favor_stopid";
        r12[r2] = r3;
        r2 = 4;
        r3 = "favor_alarm_status";
        r12[r2] = r3;
        r14 = 0;
        r15 = 0;
        r18 = r10.query(r11, r12, r13, r14, r15);
        r0 = r16;
        r1 = r18;
        r0.add(r1);
        r20 = r20 + 1;
        goto L_0x022e;
    L_0x029d:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "category= ";
        r2 = r2.append(r3);
        r0 = r30;
        r3 = r0.SearchMode;
        r2 = r2.append(r3);
        r3 = " AND ";
        r2 = r2.append(r3);
        r26 = r2.toString();
        goto L_0x023e;
    L_0x02bb:
        r2 = r28.isEmpty();
        if (r2 != 0) goto L_0x0426;
    L_0x02c1:
        r0 = r30;
        r2 = r0.SearchMode;
        if (r2 != 0) goto L_0x039d;
    L_0x02c7:
        r20 = 0;
    L_0x02c9:
        r2 = r28.size();
        r0 = r20;
        if (r0 >= r2) goto L_0x0332;
    L_0x02d1:
        r26 = "category= 2 AND ";
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r0 = r26;
        r2 = r2.append(r0);
        r3 = "favor_stopid";
        r2 = r2.append(r3);
        r3 = "=";
        r3 = r2.append(r3);
        r0 = r28;
        r1 = r20;
        r2 = r0.get(r1);
        r2 = (java.lang.String) r2;
        r2 = r2.toString();
        r2 = r3.append(r2);
        r13 = r2.toString();
        r10 = r30.getContentResolver();
        r11 = com.neighbor.ulsanbus.data.DataConst.CONTENT_FAVORITES_URI;
        r2 = 5;
        r12 = new java.lang.String[r2];
        r2 = 0;
        r3 = "_id";
        r12[r2] = r3;
        r2 = 1;
        r3 = "category";
        r12[r2] = r3;
        r2 = 2;
        r3 = "favor_routeid";
        r12[r2] = r3;
        r2 = 3;
        r3 = "favor_stopid";
        r12[r2] = r3;
        r2 = 4;
        r3 = "favor_alarm_status";
        r12[r2] = r3;
        r14 = 0;
        r15 = 0;
        r19 = r10.query(r11, r12, r13, r14, r15);
        r0 = r16;
        r1 = r19;
        r0.add(r1);
        r20 = r20 + 1;
        goto L_0x02c9;
    L_0x0332:
        r20 = 0;
    L_0x0334:
        r2 = r28.size();
        r0 = r20;
        if (r0 >= r2) goto L_0x0426;
    L_0x033c:
        r26 = "category= 3 AND ";
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r0 = r26;
        r2 = r2.append(r0);
        r3 = "favor_stopid";
        r2 = r2.append(r3);
        r3 = "=";
        r3 = r2.append(r3);
        r0 = r28;
        r1 = r20;
        r2 = r0.get(r1);
        r2 = (java.lang.String) r2;
        r2 = r2.toString();
        r2 = r3.append(r2);
        r13 = r2.toString();
        r10 = r30.getContentResolver();
        r11 = com.neighbor.ulsanbus.data.DataConst.CONTENT_FAVORITES_URI;
        r2 = 5;
        r12 = new java.lang.String[r2];
        r2 = 0;
        r3 = "_id";
        r12[r2] = r3;
        r2 = 1;
        r3 = "category";
        r12[r2] = r3;
        r2 = 2;
        r3 = "favor_routeid";
        r12[r2] = r3;
        r2 = 3;
        r3 = "favor_stopid";
        r12[r2] = r3;
        r2 = 4;
        r3 = "favor_alarm_status";
        r12[r2] = r3;
        r14 = 0;
        r15 = 0;
        r19 = r10.query(r11, r12, r13, r14, r15);
        r0 = r16;
        r1 = r19;
        r0.add(r1);
        r20 = r20 + 1;
        goto L_0x0334;
    L_0x039d:
        r20 = 0;
    L_0x039f:
        r2 = r28.size();
        r0 = r20;
        if (r0 >= r2) goto L_0x0426;
    L_0x03a7:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "category= ";
        r2 = r2.append(r3);
        r0 = r30;
        r3 = r0.SearchMode;
        r2 = r2.append(r3);
        r3 = " AND ";
        r2 = r2.append(r3);
        r26 = r2.toString();
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r0 = r26;
        r2 = r2.append(r0);
        r3 = "favor_stopid";
        r2 = r2.append(r3);
        r3 = "=";
        r3 = r2.append(r3);
        r0 = r28;
        r1 = r20;
        r2 = r0.get(r1);
        r2 = (java.lang.String) r2;
        r2 = r2.toString();
        r2 = r3.append(r2);
        r13 = r2.toString();
        r10 = r30.getContentResolver();
        r11 = com.neighbor.ulsanbus.data.DataConst.CONTENT_FAVORITES_URI;
        r2 = 5;
        r12 = new java.lang.String[r2];
        r2 = 0;
        r3 = "_id";
        r12[r2] = r3;
        r2 = 1;
        r3 = "category";
        r12[r2] = r3;
        r2 = 2;
        r3 = "favor_routeid";
        r12[r2] = r3;
        r2 = 3;
        r3 = "favor_stopid";
        r12[r2] = r3;
        r2 = 4;
        r3 = "favor_alarm_status";
        r12[r2] = r3;
        r14 = 0;
        r15 = 0;
        r19 = r10.query(r11, r12, r13, r14, r15);
        r0 = r16;
        r1 = r19;
        r0.add(r1);
        r19 = 0;
        r20 = r20 + 1;
        goto L_0x039f;
    L_0x0426:
        r21 = 0;
        r2 = r16.isEmpty();
        if (r2 != 0) goto L_0x0443;
    L_0x042e:
        r21 = new android.database.MergeCursor;
        r2 = r16.size();
        r2 = new android.database.Cursor[r2];
        r0 = r16;
        r2 = r0.toArray(r2);
        r2 = (android.database.Cursor[]) r2;
        r0 = r21;
        r0.<init>(r2);
    L_0x0443:
        return r21;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.neighbor.ulsanbus.ui.UiFavorite.makeSorting(android.database.Cursor):android.database.Cursor");
    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        FavoriteRoute favorRoute = null;
        FavoriteStop favorStop = null;
        Cursor itemCursor = (Cursor) arg0.getAdapter().getItem(arg2);
        String category = itemCursor.getString(itemCursor.getColumnIndex(DataConst.KEY_FAVORITE_CATEGORY));
        String routeId = itemCursor.getString(itemCursor.getColumnIndex("favor_routeid"));
        String stopId = itemCursor.getString(itemCursor.getColumnIndex("favor_stopid"));
        String alarmStatus = itemCursor.getString(itemCursor.getColumnIndex(DataConst.KEY_FAVORITE_ALARM));
        if (!category.equals("2")) {
            Cursor cursor = getContentResolver().query(DataConst.CONTENT_ROUTE_URI, new String[]{DataConst.KEY_NOTICE_ID, "route_id", "route_no", DataConst.KEY_ROUTE_DIR, "route_bus_type", DataConst.KEY_ROUTE_FSTOP_NAME, DataConst.KEY_ROUTE_TSTOP_NAME, DataConst.KEY_ROUTE_REMARK}, "route_id=" + routeId, null, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                favorRoute = new FavoriteRoute();
                favorRoute.favorRouteId = cursor.getString(cursor.getColumnIndex("route_id"));
                favorRoute.favorRouteNo = cursor.getString(cursor.getColumnIndex("route_no"));
                favorRoute.favorRouteDir = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_DIR));
                favorRoute.favorRouteBusTye = cursor.getString(cursor.getColumnIndex("route_bus_type"));
                favorRoute.favorRouteFName = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_FSTOP_NAME));
                favorRoute.favorRouteTName = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_TSTOP_NAME));
                favorRoute.favorRouteRemark = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_REMARK));
            } else {
                return;
            }
        }
        if (!category.equals("1")) {
            Cursor stopcursor = getContentResolver().query(DataConst.CONTENT_STOPS_URI, new String[]{DataConst.KEY_NOTICE_ID, "stop_name", "stop_id", "stop_remark"}, "stop_id = " + stopId, null, null);
            if (stopcursor.getCount() > 0) {
                stopcursor.moveToFirst();
                favorStop = new FavoriteStop();
                favorStop.favoStopId = stopcursor.getString(stopcursor.getColumnIndex("stop_id"));
                favorStop.favoStopName = stopcursor.getString(stopcursor.getColumnIndex("stop_name"));
                favorStop.favoStopRemark = stopcursor.getString(stopcursor.getColumnIndex("stop_remark"));
            } else {
                return;
            }
        }
        if (category.equals("1")) {
            Editor editor = this.prefs.edit();
            editor.putString(DataConst.BUS_ID_TAB3, favorRoute.favorRouteId);
            editor.commit();
            TabThird.TAB3.replaceActivity("favorites", new Intent(this, UiRouteInformation.class), this);
        } else if (category.equals("2")) {
            intent = new Intent(this, UiStopInformation.class);
            intent.putExtra("stop_name", favorStop.favoStopName);
            intent.putExtra("stop_id", favorStop.favoStopId);
            if (favorStop.favoStopRemark == null) {
                intent.putExtra("stop_remark", "");
            } else {
                intent.putExtra("stop_remark", favorStop.favoStopRemark);
            }
            TabThird.TAB3.replaceActivity("favorites", intent, this);
        } else {
            intent = new Intent(this, UiStopArrivalTime.class);
            intent.putExtra("stop_name", favorStop.favoStopName);
            intent.putExtra("stop_id", favorStop.favoStopId);
            if (favorStop.favoStopRemark == null) {
                intent.putExtra("stop_remark", "");
            } else {
                intent.putExtra("stop_remark", favorStop.favoStopRemark);
            }
            intent.putExtra("route_id", favorRoute.favorRouteId);
            intent.putExtra("route_no", favorRoute.favorRouteNo);
            intent.putExtra("route_bus_type", favorRoute.favorRouteBusTye);
            TabThird.TAB3.replaceActivity("favorites", intent, this);
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        Menu mMenu = menu;
        Cursor favorCursor = getContentResolver().query(DataConst.CONTENT_FAVORITES_URI, null, null, null, null);
        Log.d("UiFavorite", "[[[[[[ child count ]]]]]] " + favorCursor.getCount());
        if (favorCursor.getCount() <= 0) {
            return false;
        }
        if (mMenu.size() > 0) {
            mMenu.clear();
        }
        stopManagingCursor(favorCursor);
        mMenu.add(0, 0, 0, "전체삭제").setIcon(C0258R.drawable.icon_delete_recent_list).setOnMenuItemClickListener(new C02751());
        return super.onPrepareOptionsMenu(menu);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == -1) {
            Cursor alarmCursor = getContentResolver().query(DataConst.CONTENT_ALARM_URI, new String[]{DataConst.KEY_ALARM_ID}, null, null, null);
            if (alarmCursor.getCount() <= 0 || !alarmCursor.moveToFirst()) {
                alarmCursor.close();
                getContentResolver().delete(DataConst.CONTENT_ALARM_URI, null, null);
                getContentResolver().query(DataConst.CONTENT_ALARM_URI, null, null, null, null).setNotificationUri(getContentResolver(), DataConst.CONTENT_ALARM_URI);
            }
            do {
                AlarmManager alarmManager = (AlarmManager) getSystemService("alarm");
                alarmManager.cancel(PendingIntent.getBroadcast(getBaseContext(), alarmCursor.getInt(alarmCursor.getColumnIndex(DataConst.KEY_ALARM_ID)), new Intent(getBaseContext(), AlarmReceiver.class), 134217728));
            } while (alarmCursor.moveToNext());
            alarmCursor.close();
            getContentResolver().delete(DataConst.CONTENT_ALARM_URI, null, null);
            getContentResolver().query(DataConst.CONTENT_ALARM_URI, null, null, null, null).setNotificationUri(getContentResolver(), DataConst.CONTENT_ALARM_URI);
        }
    }

    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        final Cursor itemCursor = (Cursor) arg0.getAdapter().getItem(arg2);
        if (itemCursor.getCount() > 0) {
            new Builder(getParent()).setMessage(getResources().getString(C0258R.string.favorite_info_confirm_delete)).setPositiveButton(getResources().getString(C0258R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String condition;
                    String btncondition;
                    String category = itemCursor.getString(itemCursor.getColumnIndex(DataConst.KEY_FAVORITE_CATEGORY));
                    String routeId = itemCursor.getString(itemCursor.getColumnIndex("favor_routeid"));
                    String stopId = itemCursor.getString(itemCursor.getColumnIndex("favor_stopid"));
                    String alarmStatus = itemCursor.getString(itemCursor.getColumnIndex(DataConst.KEY_FAVORITE_ALARM));
                    if (category.equals("1")) {
                        condition = "category = " + category + " AND " + "favor_routeid" + " = " + routeId;
                    } else if (category.equals("2")) {
                        condition = "category = " + category + " AND " + "favor_stopid" + " = " + stopId;
                    } else {
                        condition = "category = " + category + " AND " + "favor_stopid" + " = " + stopId + " AND " + "favor_routeid" + " = " + routeId;
                    }
                    UiFavorite.this.getContentResolver().delete(DataConst.CONTENT_FAVORITES_URI, condition, null);
                    if (UiFavorite.this.SearchMode == null) {
                        btncondition = null;
                    } else {
                        btncondition = "category= " + UiFavorite.this.SearchMode;
                    }
                    Cursor cursor = UiFavorite.this.getContentResolver().query(DataConst.CONTENT_FAVORITES_URI, new String[]{DataConst.KEY_NOTICE_ID, DataConst.KEY_FAVORITE_CATEGORY, "favor_routeid", "favor_stopid", DataConst.KEY_FAVORITE_ALARM}, btncondition, null, null);
                    try {
                        if (category.equals("3")) {
                            Cursor alamcs = UiFavorite.this.getContentResolver().query(DataConst.CONTENT_ALARM_URI, null, "favor_routeid = " + routeId + " AND " + "favor_stopid" + " = " + stopId, null, null);
                            if (alamcs.getCount() > 0) {
                                UiFavorite.this.getContentResolver().delete(DataConst.CONTENT_ALARM_URI, "favor_routeid = " + routeId + " AND " + "favor_stopid" + " = " + stopId, null);
                                Intent intent = new Intent(UiFavorite.this.getBaseContext(), AlarmReceiver.class);
                                intent.putExtra("stop_id", stopId);
                                intent.putExtra("route_id", routeId);
                                UiFavorite.this.pendingIntent = PendingIntent.getBroadcast(UiFavorite.this.getBaseContext(), 0, intent, 134217728);
                                UiFavorite.this.alarmManager.cancel(UiFavorite.this.pendingIntent);
                            }
                            alamcs.close();
                            if (cursor.getCount() > 0) {
                                UiFavorite.this.Favoradapter.changeCursor(cursor);
                            } else {
                                UiFavorite.this.mFavoriteLayout.setVisibility(8);
                            }
                        }
                    } catch (Exception e) {
                    }
                    dialog.dismiss();
                }
            }).setNegativeButton(getResources().getString(C0258R.string.cancel), new C02762()).show();
        }
        return true;
    }
}
