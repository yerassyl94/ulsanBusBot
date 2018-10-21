package com.neighbor.ulsanbus.ui;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.neighbor.ulsanbus.BusLocation;
import com.neighbor.ulsanbus.C0258R;
import com.neighbor.ulsanbus.Stop;
import com.neighbor.ulsanbus.data.DataConst;
import com.neighbor.ulsanbus.data.DataSAXParser;
import com.neighbor.ulsanbus.util.Util;
import com.neighbor.ulsanbus.util.UtilNetworkConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class UiRouteInfoStopNames extends Activity implements OnItemClickListener {
    private static final int RESULT_DATA_EMPTY = 2;
    private static final int RESULT_DATA_OK = 0;
    private static final int RESULT_END = 255;
    private static final int RESULT_SERVER_ERROR = 1;
    private static String mRouteBusType;
    private static String mRouteDir;
    private static String mRouteID;
    private static String mRouteNumber;
    private static String mRouteRemark;
    private static String mRouteTstopName;
    private static String mRouteType;
    private String TAG = "UiRouteInfoStopNames";
    private boolean bEmergency;
    private Button btnRefresh;
    private Button btnSwitch;
    private Handler handler = new C02911();
    private ArrayList<BusLocation> mBusLocationList;
    private BusPositonRequest mBusPositionRequest;
    private Activity mContext;
    private FrameLayout mLayoutBackground;
    private ProgressBar mProgressbar;
    private RouteInfoRequest mRequestTask;
    private RouteAdapter mRouteAdapter;
    private ListView mRouteListView;
    private DataSAXParser mSaxHelper;
    private SharedPreferences prefs;
    private RelativeLayout routelayout;
    private ArrayList<Stop> routelist;
    private TextView txbusDir;
    private TextView txbusNo;
    private TextView txbusType;

    /* renamed from: com.neighbor.ulsanbus.ui.UiRouteInfoStopNames$1 */
    class C02911 extends Handler {
        C02911() {
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            UiRouteInfoStopNames.this.mProgressbar.setVisibility(8);
            switch (msg.what) {
                case 0:
                    if (msg.arg1 == 0) {
                        try {
                            if (((Stop) UiRouteInfoStopNames.this.routelist.get(0)).getErrorMsg().equals("OK")) {
                                StringBuffer direction = new StringBuffer();
                                if (CurRouteStop.mCurRouteRemark.compareTo("") != 0) {
                                    direction.append("(");
                                    direction.append(CurRouteStop.mCurRouteRemark);
                                    direction.append(")");
                                } else {
                                    direction.append("(");
                                    direction.append(CurRouteStop.mCurRouteTSTopName);
                                    direction.append("방면");
                                    direction.append(" )");
                                }
                                UiRouteInfoStopNames.this.txbusDir.setText(direction.toString());
                                if (UiRouteInfoStopNames.this.mRouteAdapter != null) {
                                    UiRouteInfoStopNames.this.mRouteAdapter.clear();
                                }
                                UiRouteInfoStopNames.this.mRouteAdapter = new RouteAdapter(UiRouteInfoStopNames.this, 0, UiRouteInfoStopNames.this.routelist);
                                UiRouteInfoStopNames.this.mRouteListView.setAdapter(UiRouteInfoStopNames.this.mRouteAdapter);
                                UiRouteInfoStopNames.this.mBusPositionRequest = new BusPositonRequest(UiRouteInfoStopNames.this.mContext, CurRouteStop.mCurRouteId);
                                UiRouteInfoStopNames.this.mBusPositionRequest.execute(new Void[0]);
                                return;
                            } else if (msg.arg1 == 0) {
                                UiRouteInfoStopNames.this.mRouteAdapter.notifyDataSetChanged();
                                return;
                            } else {
                                return;
                            }
                        } catch (Exception e) {
                            UiRouteInfoStopNames.this.showError();
                            return;
                        }
                    } else if (msg.arg1 == 1) {
                        UiRouteInfoStopNames.this.mRouteAdapter.notifyDataSetChanged();
                        return;
                    } else {
                        return;
                    }
                case 1:
                    Log.d("debug", "RESULT_SERVER_ERROR");
                    UiRouteInfoStopNames.this.showError();
                    return;
                case 2:
                    if (msg.arg1 == 0) {
                        UiRouteInfoStopNames.this.showError();
                        return;
                    } else if (msg.arg1 == 1) {
                        Log.d("debug", "msg.arg1 == 1");
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiRouteInfoStopNames$2 */
    class C02922 implements OnClickListener {
        C02922() {
        }

        public void onClick(View arg0) {
            String mCurDir = null;
            if (CurRouteStop.mCurRouteDir != null) {
                mCurDir = CurRouteStop.mCurRouteDir.equals("3") ? "3" : CurRouteStop.mCurRouteDir.equals("1") ? "2" : "1";
            }
            String Selection = "route_no = " + CurRouteStop.mCurRouteNumber + " AND " + DataConst.KEY_ROUTE_DIR + " = " + mCurDir + " AND " + "route_bus_type" + " = " + CurRouteStop.mCurRouteBustype + " AND " + DataConst.KEY_ROUTE_TYPE + " = " + CurRouteStop.mCurRouteType;
            Log.e("onClick: ", Selection);
            StringBuffer optionalCondition = new StringBuffer();
            optionalCondition.append(Selection);
            Cursor cursor = UiRouteInfoStopNames.this.getContentResolver().query(DataConst.CONTENT_ROUTE_URI, new String[]{DataConst.KEY_NOTICE_ID, "route_id", DataConst.KEY_ROUTE_REMARK, "route_bus_type", DataConst.KEY_ROUTE_TSTOP_NAME, DataConst.KEY_ROUTE_DIR}, optionalCondition.toString(), null, null);
            if (cursor.moveToFirst()) {
                UiRouteInfoStopNames.this.mProgressbar.setVisibility(0);
                CurRouteStop.mCurRouteId = cursor.getString(cursor.getColumnIndex("route_id"));
                CurRouteStop.mCurRouteRemark = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_REMARK));
                CurRouteStop.mCurRouteTSTopName = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_TSTOP_NAME));
                CurRouteStop.mCurRouteDir = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_DIR));
                DataConst.SelectedRouteId = CurRouteStop.mCurRouteId;
                UiRouteInfoStopNames.this.mRequestTask = new RouteInfoRequest(UiRouteInfoStopNames.this, CurRouteStop.mCurRouteId);
                UiRouteInfoStopNames.this.mRequestTask.execute(new Void[0]);
                Editor editor = UiRouteInfoStopNames.this.prefs.edit();
                if (Util.getCurrentTabNum() == 1) {
                    editor.putString(DataConst.BUS_ID_TAB1, CurRouteStop.mCurRouteId);
                } else if (Util.getCurrentTabNum() == 3) {
                    editor.putString(DataConst.BUS_ID_TAB3, CurRouteStop.mCurRouteId);
                }
                editor.commit();
                if (CurRouteStop.mCurRouteDir.equals("3")) {
                    UiRouteInfoStopNames.this.btnSwitch.setVisibility(4);
                }
            }
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiRouteInfoStopNames$3 */
    class C02933 implements OnClickListener {
        C02933() {
        }

        public void onClick(View v) {
            UiRouteInfoStopNames.this.mProgressbar.setVisibility(0);
            UiRouteInfoStopNames.this.mBusPositionRequest = new BusPositonRequest(UiRouteInfoStopNames.this, CurRouteStop.mCurRouteId);
            UiRouteInfoStopNames.this.mBusPositionRequest.execute(new Void[0]);
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiRouteInfoStopNames$4 */
    class C02944 implements DialogInterface.OnClickListener {
        C02944() {
        }

        public void onClick(DialogInterface dialog, int which) {
            UiRouteInfoStopNames.this.finish();
        }
    }

    private class BusPositonRequest extends AsyncTask<Void, Integer, Integer> {
        private boolean isCancel = false;
        private Context mContext;
        private ArrayList<BusLocation> mLocation;
        private String mRouteId;
        private int responseCode = 0;

        public BusPositonRequest(Context context, String routeid) {
            this.mRouteId = routeid;
            this.mContext = context;
            this.mLocation = new ArrayList();
        }

        protected void onCancelled() {
            super.onCancelled();
            this.isCancel = true;
        }

        protected Integer doInBackground(Void... params) {
            try {
                UiRouteInfoStopNames.this.mSaxHelper.getName(DataConst.API_BUSLOCATIONINFO3 + this.mRouteId, DataConst.FLAG_BUSLOCATIONINFO);
            } catch (Exception e) {
                this.responseCode = 1;
            }
            return Integer.valueOf(this.responseCode);
        }

        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (this.isCancel) {
                UiRouteInfoStopNames.this.handler.sendEmptyMessage(255);
            } else if (result.intValue() != 0) {
                UiRouteInfoStopNames.this.handler.sendEmptyMessage(1);
            } else {
                try {
                    this.mLocation = UiRouteInfoStopNames.this.mSaxHelper.getBusLocationList();
                    if (this.mLocation == null) {
                        UiRouteInfoStopNames.this.handler.sendEmptyMessage(1);
                    } else if (this.mLocation.size() > 0) {
                        if (UiRouteInfoStopNames.this.mBusLocationList != null) {
                            UiRouteInfoStopNames.this.mBusLocationList.clear();
                        } else {
                            UiRouteInfoStopNames.this.mBusLocationList = new ArrayList();
                        }
                        UiRouteInfoStopNames.this.mBusLocationList = this.mLocation;
                        msg = UiRouteInfoStopNames.this.handler.obtainMessage();
                        msg.arg1 = 1;
                        msg.what = 0;
                        UiRouteInfoStopNames.this.handler.sendMessage(msg);
                    } else {
                        msg = UiRouteInfoStopNames.this.handler.obtainMessage();
                        msg.arg1 = 1;
                        msg.what = 2;
                        UiRouteInfoStopNames.this.handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    UiRouteInfoStopNames.this.handler.sendEmptyMessage(Integer.parseInt(e.getMessage()));
                }
            }
        }
    }

    private static class CurRouteStop {
        static String mCurRouteBustype = "";
        static String mCurRouteDir = "";
        static String mCurRouteId = "";
        static String mCurRouteNumber = "";
        static String mCurRouteRemark = "";
        static String mCurRouteTSTopName = "";
        static String mCurRouteType = "";

        private CurRouteStop() {
        }
    }

    class RouteAdapter extends ArrayAdapter<Stop> {
        List<Stop> Stoplist;
        private ImageView iconStops1;
        private ImageView iconStops2;
        private ImageView iconStops3;
        private Context mContext;

        public RouteAdapter(Context context, int textViewResourceId, List<Stop> objects) {
            super(context, textViewResourceId, objects);
            this.mContext = context;
            this.Stoplist = objects;
        }

        public void setSpeedImag(int position, ImageView line) {
            if (this.Stoplist != null) {
                Stop busLocation = (Stop) this.Stoplist.get(position);
                if (line != null && position == 0) {
                    this.iconStops1.setVisibility(0);
                    if (busLocation.getSpeed() == 0) {
                        this.iconStops1.setImageResource(C0258R.drawable.bus_line_bg_start);
                    } else if (busLocation.getSpeed() >= 25) {
                        this.iconStops1.setImageResource(C0258R.drawable.bus_line_grbg_start);
                    } else if (15 > busLocation.getSpeed() || busLocation.getSpeed() >= 25) {
                        this.iconStops1.setImageResource(C0258R.drawable.bus_line_rdbg_start);
                    } else {
                        this.iconStops1.setImageResource(C0258R.drawable.bus_line_ylbg_start);
                    }
                } else if (this.iconStops3 != null && position == getCount() - 1) {
                    this.iconStops3.setVisibility(0);
                    if (busLocation.getSpeed() == 0) {
                        this.iconStops3.setImageResource(C0258R.drawable.bus_line_bg_end);
                    } else if (busLocation.getSpeed() >= 25) {
                        this.iconStops3.setImageResource(C0258R.drawable.bus_line_grbg_end);
                    } else if (15 > busLocation.getSpeed() || busLocation.getSpeed() >= 25) {
                        this.iconStops3.setImageResource(C0258R.drawable.bus_line_rdbg_end);
                    } else {
                        this.iconStops3.setImageResource(C0258R.drawable.bus_line_ylbg_end);
                    }
                } else if (this.iconStops2 != null) {
                    this.iconStops2.setVisibility(0);
                    if (busLocation.getSpeed() == 0) {
                        this.iconStops2.setImageResource(C0258R.drawable.bus_line_bg_mid);
                    } else if (busLocation.getSpeed() >= 25) {
                        this.iconStops2.setImageResource(C0258R.drawable.bus_line_grbg_mid);
                    } else if (15 > busLocation.getSpeed() || busLocation.getSpeed() >= 25) {
                        this.iconStops2.setImageResource(C0258R.drawable.bus_line_rdbg_mid);
                    } else {
                        this.iconStops2.setImageResource(C0258R.drawable.bus_line_ylbg_mid);
                    }
                }
            }
        }

        public void addAll(Collection<? extends Stop> collection) {
            if (VERSION.SDK_INT >= 11) {
                this.Stoplist.addAll(collection);
                return;
            }
            for (Stop element : collection) {
                this.Stoplist.add(element);
            }
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = ((LayoutInflater) this.mContext.getSystemService("layout_inflater")).inflate(C0258R.layout.route_info_stop_names_item, parent, false);
            }
            FrameLayout row = (FrameLayout) v.findViewById(C0258R.id.routeshow_bus_stop_row);
            if (UiRouteInfoStopNames.this.bEmergency) {
                row.setBackgroundResource(C0258R.drawable.list_emer_selector);
            } else {
                row.setBackgroundResource(C0258R.drawable.list_selector);
            }
            Stop value = getItem(position);
            ImageView iconLimusine = (ImageView) v.findViewById(C0258R.id.routeshow_bus_stop_limusine);
            Cursor cursor = UiRouteInfoStopNames.this.getContentResolver().query(DataConst.CONTENT_STOPS_URI, new String[]{DataConst.KEY_NOTICE_ID, "stop_id", DataConst.KEY_STOP_LIMOUSINE, "stop_remark"}, "stop_id=" + value.getStopID(), null, null);
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                if (cursor.getString(cursor.getColumnIndex(DataConst.KEY_STOP_LIMOUSINE)).equals("1")) {
                    iconLimusine.setImageResource(C0258R.drawable.icon_bus_stop_limousine);
                    iconLimusine.setVisibility(0);
                } else {
                    iconLimusine.setVisibility(4);
                }
            }
            cursor.close();
            ((TextView) v.findViewById(C0258R.id.routeshow_bus_stop_item_name)).setText(value.getStopName());
            ((TextView) v.findViewById(C0258R.id.routeshow_bus_stop_item_id)).setText(value.getStopID());
            this.iconStops1 = (ImageView) v.findViewById(C0258R.id.routeshow_stop_line_start);
            this.iconStops2 = (ImageView) v.findViewById(C0258R.id.routeshow_stop_line_mid);
            this.iconStops3 = (ImageView) v.findViewById(C0258R.id.routeshow_stop_line_end);
            if (this.iconStops1.getVisibility() != 8) {
                this.iconStops1.setVisibility(8);
            }
            if (this.iconStops2.getVisibility() != 8) {
                this.iconStops2.setVisibility(8);
            }
            if (this.iconStops3.getVisibility() != 8) {
                this.iconStops3.setVisibility(8);
            }
            if (position == 0) {
                this.iconStops1.setVisibility(0);
                setSpeedImag(position, this.iconStops1);
            } else if (position == getCount() - 1) {
                this.iconStops3.setVisibility(0);
                setSpeedImag(position, this.iconStops3);
            } else {
                this.iconStops2.setVisibility(0);
                setSpeedImag(position, this.iconStops2);
            }
            LinearLayout buspos = (LinearLayout) v.findViewById(C0258R.id.routeshow_bus_info);
            if (buspos.getVisibility() == 0) {
                buspos.setVisibility(8);
            }
            ImageView busLocation = (ImageView) v.findViewById(C0258R.id.routeshow_bus_location);
            TextView busLocNo = (TextView) v.findViewById(C0258R.id.routeshow_bus_no);
            ImageView busWaypoint = (ImageView) v.findViewById(C0258R.id.routeshow_waypoint);
            Log.d(UiRouteInfoStopNames.this.TAG, "value.getStopName().equals(value.getWaypoint()) : " + value.getStopName().equals(value.getWaypoint()));
            Log.d(UiRouteInfoStopNames.this.TAG, "CurRouteStop.mCurRouteDir.equals 3 : " + CurRouteStop.mCurRouteDir);
            if (CurRouteStop.mCurRouteDir.equals("3") && value.getStopName().equals(value.getWaypoint())) {
                busWaypoint.setVisibility(0);
            } else {
                busWaypoint.setVisibility(8);
            }
            try {
                if (UiRouteInfoStopNames.this.mBusLocationList != null) {
                    Iterator it = UiRouteInfoStopNames.this.mBusLocationList.iterator();
                    while (it.hasNext()) {
                        BusLocation location = (BusLocation) it.next();
                        String stopId = location.getStopID();
                        String listStopId = location.getStopID();
                        String lowTypeRoute = location.getLowType();
                        Log.e(UiRouteInfoStopNames.this.TAG, "getView: " + lowTypeRoute);
                        if (lowTypeRoute != null) {
                            if (lowTypeRoute.equals("1")) {
                                busLocation.setImageResource(C0258R.drawable.icon_current_location_low);
                            } else {
                                busLocation.setImageResource(C0258R.drawable.icon_current_location);
                            }
                        } else {
                            busLocation.setImageResource(C0258R.drawable.icon_current_location);
                        }
                        if (value.getStopID().equals(location.getStopID())) {
                            busLocNo.setText(location.getBusNo());
                            buspos.setVisibility(0);
                            Log.d(UiRouteInfoStopNames.this.TAG, "location.getInOutFlag() : " + location.getInOutFlag());
                            DisplayMetrics dm;
                            LayoutParams layoutParams;
                            if (location.getInOutFlag() == 1) {
                                dm = UiRouteInfoStopNames.this.getResources().getDisplayMetrics();
                                layoutParams = new FrameLayout.LayoutParams(-2, -2);
                                layoutParams.rightMargin = Math.round(8.0f * dm.density);
                                layoutParams.gravity = 5;
                                buspos.setLayoutParams(layoutParams);
                            } else if (!(location.getInOutFlag() == 1 || location.getInOutFlag() == 0)) {
                                dm = UiRouteInfoStopNames.this.getResources().getDisplayMetrics();
                                layoutParams = new FrameLayout.LayoutParams(-2, -2);
                                layoutParams.topMargin = Math.round(30.0f * dm.density);
                                layoutParams.rightMargin = Math.round(8.0f * dm.density);
                                layoutParams.gravity = 5;
                                buspos.setLayoutParams(layoutParams);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                UiRouteInfoStopNames.this.showError();
            }
            return v;
        }

        public Stop getItem(int position) {
            return (Stop) this.Stoplist.get(position);
        }
    }

    private class RouteInfoRequest extends AsyncTask<Void, Integer, Integer> {
        private Context mContext;
        private String mRouteId;
        private ArrayList<Stop> mStoplist;
        private int responseCode = 0;

        public RouteInfoRequest(Context context, String routeid) {
            this.mRouteId = routeid;
            this.mContext = context;
            this.mStoplist = new ArrayList();
        }

        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result.intValue() != 0) {
                UiRouteInfoStopNames.this.handler.sendEmptyMessage(1);
                return;
            }
            try {
                this.mStoplist = UiRouteInfoStopNames.this.mSaxHelper.getRouteStopsList();
                if (this.mStoplist == null) {
                    UiRouteInfoStopNames.this.handler.sendEmptyMessage(1);
                } else if (this.mStoplist.size() > 0) {
                    if (UiRouteInfoStopNames.this.routelist != null) {
                        UiRouteInfoStopNames.this.routelist.clear();
                    } else {
                        UiRouteInfoStopNames.this.routelist = new ArrayList();
                    }
                    UiRouteInfoStopNames.this.routelist = this.mStoplist;
                    msg = UiRouteInfoStopNames.this.handler.obtainMessage();
                    msg.arg1 = 0;
                    msg.what = 0;
                    UiRouteInfoStopNames.this.handler.sendMessage(msg);
                } else {
                    msg = UiRouteInfoStopNames.this.handler.obtainMessage();
                    msg.arg1 = 0;
                    msg.what = 2;
                    UiRouteInfoStopNames.this.handler.sendMessage(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        protected Integer doInBackground(Void... params) {
            try {
                UiRouteInfoStopNames.this.mSaxHelper.getName(DataConst.API_ROUTE_STOPS_INFO + this.mRouteId, DataConst.FLAG_ROUTESTOPS);
            } catch (Exception e) {
                this.responseCode = 1;
                e.printStackTrace();
            }
            return Integer.valueOf(this.responseCode);
        }

        protected void onCancelled() {
            super.onCancelled();
        }
    }

    private void init() {
        String[] colorArray = getResources().getStringArray(C0258R.array.bus_type_color);
        String[] busTypeArray = getResources().getStringArray(C0258R.array.stop_info_header_type);
        int valueOfBusType = Integer.parseInt(CurRouteStop.mCurRouteBustype);
        this.mLayoutBackground = (FrameLayout) findViewById(C0258R.id.route_stop_background);
        this.routelayout = (RelativeLayout) findViewById(C0258R.id.route_stop_layout);
        this.routelayout.setBackgroundColor(Color.parseColor(colorArray[valueOfBusType]));
        this.txbusType = (TextView) findViewById(C0258R.id.route_stop_route_type);
        this.txbusType.setText(busTypeArray[valueOfBusType]);
        this.txbusNo = (TextView) findViewById(C0258R.id.route_info_stop_number);
        this.txbusNo.setText(CurRouteStop.mCurRouteNumber);
        this.txbusDir = (TextView) findViewById(C0258R.id.route_info_stop_dir);
        StringBuffer direction = new StringBuffer();
        if (CurRouteStop.mCurRouteRemark.compareTo("") != 0) {
            direction.append("(");
            direction.append(CurRouteStop.mCurRouteRemark);
            direction.append(" )");
        } else {
            direction.append("(");
            direction.append(CurRouteStop.mCurRouteTSTopName);
            direction.append("방면");
            direction.append(" )");
        }
        this.txbusDir.setText(direction.toString());
        this.mProgressbar = (ProgressBar) findViewById(C0258R.id.route_info_stop_progress);
        this.mRouteListView = (ListView) findViewById(C0258R.id.route_info_stop_list);
        this.mRouteListView.setOnItemClickListener(this);
        this.btnSwitch = (Button) findViewById(C0258R.id.btn_route_stop_switch);
        if (CurRouteStop.mCurRouteDir.equals("3")) {
            this.btnSwitch.setVisibility(4);
        }
        this.btnSwitch.setOnClickListener(new C02922());
        this.btnRefresh = (Button) findViewById(C0258R.id.btn_route_stop_refesh);
        this.btnRefresh.setOnClickListener(new C02933());
        if (this.bEmergency) {
            this.mLayoutBackground.setBackgroundResource(C0258R.drawable.background_emer);
        } else {
            this.mLayoutBackground.setBackgroundResource(C0258R.drawable.background_nomal);
        }
    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Stop stop = (Stop) arg0.getAdapter().getItem(arg2);
        Intent intent = new Intent(this, UiStopArrivalTime.class);
        intent.putExtra("stop_id", stop.getStopID());
        intent.putExtra("stop_name", stop.getStopName());
        intent.putExtra("stop_remark", CurRouteStop.mCurRouteTSTopName + "방면");
        intent.putExtra("route_id", CurRouteStop.mCurRouteId);
        intent.putExtra("route_no", CurRouteStop.mCurRouteNumber);
        intent.putExtra("route_bus_type", CurRouteStop.mCurRouteBustype);
        if (this.mBusLocationList != null) {
            Iterator it = this.mBusLocationList.iterator();
            while (it.hasNext()) {
                BusLocation location = (BusLocation) it.next();
                if (stop.getStopName() != null && location.getStopName() != null && location.getStopName().equals(stop.getStopName())) {
                    intent.putExtra("low_type", location.getLowType());
                    break;
                }
            }
        }
        switch (UiMainTab.mTabHost.getCurrentTab()) {
            case 1:
                TabFirst.TAB1.replaceActivity("busposition", intent, this);
                return;
            case 3:
                TabThird.TAB3.replaceActivity("busposition", intent, this);
                return;
            default:
                return;
        }
    }

    protected void onStop() {
        if (!(this.mRequestTask == null || this.mRequestTask.getStatus() == Status.FINISHED)) {
            this.mRequestTask.cancel(true);
        }
        if (!(this.mBusPositionRequest == null || this.mBusPositionRequest.getStatus() == Status.FINISHED)) {
            this.mBusPositionRequest.cancel(true);
        }
        super.onStop();
    }

    protected void onStart() {
        super.onStart();
        if (!new UtilNetworkConnection().IsConnected(getParent().getParent())) {
            showError();
        }
        this.mRequestTask = new RouteInfoRequest(this, CurRouteStop.mCurRouteId);
        this.mRequestTask.execute(new Void[0]);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0258R.layout.route_info_stop_names);
        this.prefs = getSharedPreferences("PreStatus", 0);
        this.bEmergency = this.prefs.getBoolean(DataConst.KEY_PREF_EMER_MODE, false);
        if (Util.getCurrentTabNum() == 1) {
            mRouteID = this.prefs.getString(DataConst.BUS_ID_TAB1, "");
        } else if (Util.getCurrentTabNum() == 3) {
            mRouteID = this.prefs.getString(DataConst.BUS_ID_TAB3, "");
        }
        initialize();
        this.mContext = this;
        CurRouteStop.mCurRouteId = mRouteID;
        CurRouteStop.mCurRouteNumber = mRouteNumber;
        CurRouteStop.mCurRouteDir = mRouteDir;
        CurRouteStop.mCurRouteBustype = mRouteBusType;
        CurRouteStop.mCurRouteRemark = mRouteRemark;
        CurRouteStop.mCurRouteTSTopName = mRouteTstopName;
        CurRouteStop.mCurRouteType = mRouteType;
        init();
        this.mSaxHelper = new DataSAXParser();
    }

    void initialize() {
        Cursor cs = getContentResolver().query(DataConst.CONTENT_ROUTE_URI, new String[]{"route_no", DataConst.KEY_ROUTE_DIR, "route_bus_type", DataConst.KEY_ROUTE_REMARK, DataConst.KEY_ROUTE_TSTOP_NAME, DataConst.KEY_ROUTE_TYPE}, "route_id=" + mRouteID, null, null);
        if (cs.moveToFirst()) {
            do {
                mRouteNumber = cs.getString(cs.getColumnIndex("route_no"));
                mRouteDir = cs.getString(cs.getColumnIndex(DataConst.KEY_ROUTE_DIR));
                mRouteBusType = cs.getString(cs.getColumnIndex("route_bus_type"));
                mRouteRemark = cs.getString(cs.getColumnIndex(DataConst.KEY_ROUTE_REMARK));
                mRouteTstopName = cs.getString(cs.getColumnIndex(DataConst.KEY_ROUTE_TSTOP_NAME));
                mRouteType = cs.getString(cs.getColumnIndex(DataConst.KEY_ROUTE_TYPE));
            } while (cs.moveToNext());
            cs.close();
        }
    }

    private void showError() {
        new Builder(getParent().getParent()).setTitle(getString(C0258R.string.error)).setMessage(getString(C0258R.string.server_conn_no_response)).setNeutralButton(getString(C0258R.string.ok), new C02944()).show();
    }
}
