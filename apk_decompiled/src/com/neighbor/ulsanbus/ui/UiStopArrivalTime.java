package com.neighbor.ulsanbus.ui;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.neighbor.ulsanbus.C0258R;
import com.neighbor.ulsanbus.RouteArrival;
import com.neighbor.ulsanbus.data.DataConst;
import com.neighbor.ulsanbus.network.ParseArrivalBus;
import com.neighbor.ulsanbus.util.UtilTimeManager;
import java.io.IOException;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParserException;

public class UiStopArrivalTime extends Activity implements OnClickListener {
    private static final int RESULT_DATA_OK = 0;
    private static final int RESULT_SERVER_ERROR = 1;
    private String Tag = getClass().getSimpleName();
    private Button addFavorite;
    private boolean bEmergency;
    private LinearLayout busLayout1;
    private LinearLayout busLayout2;
    private LinearLayout busLayout3;
    private LinearLayout busLayout4;
    private TextView busOrder1;
    private TextView busOrder2;
    private TextView busOrder3;
    private TextView busOrder4;
    private Handler handler = new C03191();
    private LinearLayout mArrivalbg;
    private FrameLayout mBusImageLayout1;
    private FrameLayout mBusImageLayout2;
    private FrameLayout mBusImageLayout3;
    private FrameLayout mBusImageLayout4;
    private ArrayList<RouteArrival> mComminglist;
    CurRoute mCurBus;
    private ProgressBar mProgessbar;
    private String mRouteId;
    private String mStopId;
    private String mStopName;
    private String mStopRemark;
    private SharedPreferences prefs;
    private Button refresh;

    /* renamed from: com.neighbor.ulsanbus.ui.UiStopArrivalTime$1 */
    class C03191 extends Handler {
        C03191() {
        }

        public void handleMessage(Message msg) {
            UiStopArrivalTime.this.mProgessbar.setVisibility(8);
            if (msg.what == 0) {
                UiStopArrivalTime.this.setInformation();
            } else {
                UiStopArrivalTime.this.showError();
            }
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiStopArrivalTime$2 */
    class C03202 implements DialogInterface.OnClickListener {
        C03202() {
        }

        public void onClick(DialogInterface dialog, int which) {
            UiStopArrivalTime.this.finish();
        }
    }

    class ArrivalTimeRequest extends Thread {
        ParseArrivalBus mParse;

        ArrivalTimeRequest() {
        }

        public void run() {
            this.mParse = new ParseArrivalBus();
            try {
                this.mParse.setRouteBus(UiStopArrivalTime.this.mRouteId, UiStopArrivalTime.this.mStopId);
                this.mParse.parse();
                UiStopArrivalTime.this.mComminglist = (ArrayList) this.mParse.get();
                UiStopArrivalTime.this.handler.sendEmptyMessage(0);
            } catch (XmlPullParserException e) {
                UiStopArrivalTime.this.handler.sendEmptyMessage(1);
            } catch (IOException e2) {
                UiStopArrivalTime.this.handler.sendEmptyMessage(1);
            }
        }
    }

    private static class CurRoute {
        String routeBusType;
        String routeDir;
        String routeFSTOPNM;
        String routeId;
        String routeNo;
        String routeRemark;
        String routeTStopNM;
        String routeType;

        private CurRoute() {
        }
    }

    private void getRouteInfo() {
        Cursor cs = getContentResolver().query(DataConst.CONTENT_ROUTE_URI, new String[]{DataConst.KEY_NOTICE_ID, "route_id", "route_no", DataConst.KEY_ROUTE_DIR, DataConst.KEY_ROUTE_TYPE, "route_bus_type", DataConst.KEY_ROUTE_FSTOP_NAME, DataConst.KEY_ROUTE_TSTOP_NAME, DataConst.KEY_ROUTE_REMARK}, "route_id=" + this.mRouteId, null, null);
        try {
            if (cs.moveToFirst()) {
                this.mCurBus = new CurRoute();
                this.mCurBus.routeId = cs.getString(cs.getColumnIndex("route_id"));
                this.mCurBus.routeNo = cs.getString(cs.getColumnIndex("route_no"));
                this.mCurBus.routeDir = cs.getString(cs.getColumnIndex(DataConst.KEY_ROUTE_DIR));
                this.mCurBus.routeType = cs.getString(cs.getColumnIndex(DataConst.KEY_ROUTE_TYPE));
                this.mCurBus.routeBusType = cs.getString(cs.getColumnIndex("route_bus_type"));
                this.mCurBus.routeFSTOPNM = cs.getString(cs.getColumnIndex(DataConst.KEY_ROUTE_FSTOP_NAME));
                this.mCurBus.routeTStopNM = cs.getString(cs.getColumnIndex(DataConst.KEY_ROUTE_TSTOP_NAME));
                this.mCurBus.routeRemark = cs.getString(cs.getColumnIndex(DataConst.KEY_ROUTE_REMARK));
            }
            cs.close();
        } catch (Exception e) {
        }
    }

    void init() {
        String[] orderArray = getResources().getStringArray(C0258R.array.bus_order_text);
        String[] busTypeText = getResources().getStringArray(C0258R.array.stop_info_header_type);
        String[] resColorIds = getResources().getStringArray(C0258R.array.bus_type_color);
        this.busOrder1 = (TextView) this.busLayout1.findViewById(C0258R.id.arrival_bus_order);
        this.busOrder2 = (TextView) this.busLayout2.findViewById(C0258R.id.arrival_bus_order);
        this.busOrder3 = (TextView) this.busLayout3.findViewById(C0258R.id.arrival_bus_order);
        this.busOrder4 = (TextView) this.busLayout4.findViewById(C0258R.id.arrival_bus_order);
        TextView busOrderTxt2 = (TextView) this.busLayout2.findViewById(C0258R.id.arrival_bus_order_text);
        TextView busOrderTxt3 = (TextView) this.busLayout3.findViewById(C0258R.id.arrival_bus_order_text);
        TextView busOrderTxt4 = (TextView) this.busLayout4.findViewById(C0258R.id.arrival_bus_order_text);
        ((TextView) this.busLayout1.findViewById(C0258R.id.arrival_bus_order_text)).setText(orderArray[0]);
        busOrderTxt2.setText(orderArray[1]);
        busOrderTxt3.setText(orderArray[2]);
        busOrderTxt4.setText(orderArray[3]);
        getRouteInfo();
        int mBusType = Integer.parseInt(this.mCurBus.routeBusType);
        ((RelativeLayout) findViewById(C0258R.id.arrival_bus_info)).setBackgroundColor(Color.parseColor(resColorIds[mBusType]));
        ((TextView) findViewById(C0258R.id.bus_type_text)).setText(busTypeText[mBusType]);
        TextView icBusDestInfo = (TextView) findViewById(C0258R.id.bus_destination_info);
        ((TextView) findViewById(C0258R.id.comming_bus_number)).setText(this.mCurBus.routeNo);
        StringBuffer sb = new StringBuffer();
        sb.append(this.mCurBus.routeFSTOPNM);
        sb.append(" > ");
        if (this.mCurBus.routeRemark.length() > 0) {
            sb.append(this.mCurBus.routeRemark);
            sb.append(" > ");
        }
        sb.append(this.mCurBus.routeTStopNM);
        icBusDestInfo.setText(sb.toString());
        TextView stopName = (TextView) findViewById(C0258R.id.arrival_stop_name);
        StringBuffer stopInfo = new StringBuffer();
        stopInfo.append(this.mStopName);
        if (this.mStopRemark != null && this.mStopRemark.length() > 0) {
            stopInfo.append(" ( ");
            stopInfo.append(this.mStopRemark);
            stopInfo.append(" )");
        }
        stopName.setText(stopInfo.toString());
        ((TextView) findViewById(C0258R.id.arrival_stop_id)).setText(this.mStopId);
    }

    void setInformation() {
        int i;
        String[] orderdigitArray = getResources().getStringArray(C0258R.array.bus_order_digit);
        TypedArray ArrivalbusType = getResources().obtainTypedArray(C0258R.array.arrival_bus_type);
        int[] resIds = new int[ArrivalbusType.length()];
        for (i = 0; i < resIds.length; i++) {
            resIds[i] = ArrivalbusType.getResourceId(i, 0);
        }
        ArrivalbusType.recycle();
        this.busOrder1.setText("1");
        this.busOrder2.setText("2");
        this.busOrder3.setText("3");
        this.busOrder4.setText("4");
        int order = 0;
        if (this.mComminglist != null) {
            if (this.mComminglist.size() >= 4) {
                order = 4;
            } else {
                order = this.mComminglist.size();
            }
        }
        TextView CommingBusInfo4 = (TextView) this.busLayout4.findViewById(C0258R.id.arrival_bus_info);
        TextView RemainTime4 = (TextView) this.busLayout4.findViewById(C0258R.id.arrival_bus_time);
        TextView RemainStopCnt4 = (TextView) this.busLayout4.findViewById(C0258R.id.arrival_bus_remain_cnt);
        TextView CommingBusInfo3 = (TextView) this.busLayout3.findViewById(C0258R.id.arrival_bus_info);
        TextView RemainTime3 = (TextView) this.busLayout3.findViewById(C0258R.id.arrival_bus_time);
        TextView RemainStopCnt3 = (TextView) this.busLayout3.findViewById(C0258R.id.arrival_bus_remain_cnt);
        TextView CommingBusInfo2 = (TextView) this.busLayout2.findViewById(C0258R.id.arrival_bus_info);
        TextView RemainTime2 = (TextView) this.busLayout2.findViewById(C0258R.id.arrival_bus_time);
        TextView RemainStopCnt2 = (TextView) this.busLayout2.findViewById(C0258R.id.arrival_bus_remain_cnt);
        TextView CommingBusInfo1 = (TextView) this.busLayout1.findViewById(C0258R.id.arrival_bus_info);
        TextView RemainTime1 = (TextView) this.busLayout1.findViewById(C0258R.id.arrival_bus_time);
        TextView RemainStopCnt1 = (TextView) this.busLayout1.findViewById(C0258R.id.arrival_bus_remain_cnt);
        int busType = Integer.parseInt(this.mCurBus.routeBusType);
        for (i = 0; i < order; i++) {
            RouteArrival mbus = (RouteArrival) this.mComminglist.get(i);
            mbus.getBusNo();
            StringBuffer sb = new StringBuffer();
            if (mbus.getStatus().equals("1")) {
                sb.append(mbus.getRemainTime());
                sb.insert(2, ":");
                sb.append("출발 예정");
                if (i == 0) {
                    RemainTime1.setText(sb.toString());
                } else if (i == 1) {
                    RemainTime2.setText(sb.toString());
                } else if (i == 2) {
                    RemainTime3.setText(sb.toString());
                } else if (i == 3) {
                    RemainTime4.setText(sb.toString());
                }
            } else if (!this.bEmergency) {
                sb.append(UtilTimeManager.convertTimeArrival(Integer.parseInt(mbus.getRemainTime()), "arrival"));
                if (mbus.getRemainStop().equals("0")) {
                    sb.append(" (이전 정류소) ");
                } else {
                    sb.append("( " + mbus.getRemainStop() + " 전 ) ");
                }
                if (i == 0) {
                    RemainTime1.setText(sb.toString());
                    if (mbus.getStopNM() != null) {
                        RemainStopCnt1.setText(mbus.getStopNM() + " 출발");
                    }
                } else if (i == 1) {
                    RemainTime2.setText(sb.toString());
                    if (mbus.getStopNM() != null) {
                        RemainStopCnt2.setText(mbus.getStopNM() + " 출발");
                    } else {
                        RemainStopCnt2.setText(mbus.getStopNM() + " 출발");
                    }
                } else if (i == 2) {
                    RemainTime3.setText(sb.toString());
                    if (mbus.getStopNM() != null) {
                        RemainStopCnt3.setText(mbus.getStopNM() + " 출발");
                    }
                } else if (i == 3) {
                    RemainTime4.setText(sb.toString());
                    if (mbus.getStopNM() != null) {
                        RemainStopCnt4.setText(mbus.getStopNM() + " 출발");
                    }
                }
            }
            if (i == 0) {
                CommingBusInfo1.setText(mbus.getBusNo());
            } else if (i == 1) {
                CommingBusInfo2.setText(mbus.getBusNo());
            } else if (i == 2) {
                CommingBusInfo3.setText(mbus.getBusNo());
            } else if (i == 3) {
                CommingBusInfo4.setText(mbus.getBusNo());
            }
            if (mbus.getLowType() == null) {
                switch (i) {
                    case 0:
                        this.mBusImageLayout1.setBackgroundResource(resIds[busType]);
                        break;
                    case 1:
                        this.mBusImageLayout2.setBackgroundResource(resIds[busType]);
                        break;
                    case 2:
                        this.mBusImageLayout3.setBackgroundResource(resIds[busType]);
                        break;
                    case 3:
                        this.mBusImageLayout4.setBackgroundResource(resIds[busType]);
                        break;
                    default:
                        break;
                }
            }
            switch (i) {
                case 0:
                    if (!mbus.getLowType().equals("1")) {
                        this.mBusImageLayout1.setBackgroundResource(resIds[busType]);
                        break;
                    } else {
                        this.mBusImageLayout1.setBackgroundResource(C0258R.drawable.icon_bus_arrival_low);
                        break;
                    }
                case 1:
                    if (!mbus.getLowType().equals("1")) {
                        this.mBusImageLayout2.setBackgroundResource(resIds[busType]);
                        break;
                    } else {
                        this.mBusImageLayout2.setBackgroundResource(C0258R.drawable.icon_bus_arrival_low);
                        break;
                    }
                case 2:
                    if (!mbus.getLowType().equals("1")) {
                        this.mBusImageLayout3.setBackgroundResource(resIds[busType]);
                        break;
                    } else {
                        this.mBusImageLayout3.setBackgroundResource(C0258R.drawable.icon_bus_arrival_low);
                        break;
                    }
                case 3:
                    if (!mbus.getLowType().equals("1")) {
                        this.mBusImageLayout4.setBackgroundResource(resIds[busType]);
                        break;
                    } else {
                        this.mBusImageLayout4.setBackgroundResource(C0258R.drawable.icon_bus_arrival_low);
                        break;
                    }
                default:
                    break;
            }
        }
        for (i = order; i < 4; i++) {
            switch (i) {
                case 0:
                    RemainTime1.setText("운행종료");
                    this.mBusImageLayout1.setBackgroundResource(resIds[busType]);
                    break;
                case 1:
                    RemainTime2.setText("운행종료");
                    this.mBusImageLayout2.setBackgroundResource(resIds[busType]);
                    break;
                case 2:
                    RemainTime3.setText("운행종료");
                    this.mBusImageLayout3.setBackgroundResource(resIds[busType]);
                    break;
                case 3:
                    RemainTime4.setText("운행종료");
                    this.mBusImageLayout4.setBackgroundResource(resIds[busType]);
                    break;
                default:
                    break;
            }
        }
    }

    void showError() {
        try {
            new Builder(getParent().getParent()).setTitle(getString(C0258R.string.error)).setMessage(getString(C0258R.string.server_conn_no_response)).setNeutralButton(getString(C0258R.string.ok), new C03202()).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0258R.layout.arrival_time);
        this.prefs = getSharedPreferences("PreStatus", 0);
        this.bEmergency = this.prefs.getBoolean(DataConst.KEY_PREF_EMER_MODE, false);
        Intent intent = getIntent();
        if (intent != null) {
            this.mStopId = intent.getStringExtra("stop_id");
            this.mStopName = intent.getStringExtra("stop_name");
            this.mRouteId = intent.getStringExtra("route_id");
            this.mStopRemark = intent.getStringExtra("stop_remark");
        }
        LinearLayout mArrivalbg = (LinearLayout) findViewById(C0258R.id.arrival_background);
        LinearLayout stopInfoBg = (LinearLayout) findViewById(C0258R.id.arrival_stop_background);
        this.busLayout1 = (LinearLayout) findViewById(C0258R.id.arrival_bus_no1);
        this.mBusImageLayout1 = (FrameLayout) this.busLayout1.findViewById(C0258R.id.arrival_bus_image1);
        this.busLayout2 = (LinearLayout) findViewById(C0258R.id.arrival_bus_no2);
        this.mBusImageLayout2 = (FrameLayout) this.busLayout2.findViewById(C0258R.id.arrival_bus_image1);
        this.busLayout3 = (LinearLayout) findViewById(C0258R.id.arrival_bus_no3);
        this.mBusImageLayout3 = (FrameLayout) this.busLayout3.findViewById(C0258R.id.arrival_bus_image1);
        this.busLayout4 = (LinearLayout) findViewById(C0258R.id.arrival_bus_no4);
        this.mBusImageLayout4 = (FrameLayout) this.busLayout4.findViewById(C0258R.id.arrival_bus_image1);
        this.refresh = (Button) findViewById(C0258R.id.icon_refresh_stop_infomation);
        this.refresh.setOnClickListener(this);
        this.addFavorite = (Button) findViewById(C0258R.id.arrival_time_add_favorite);
        this.addFavorite.setOnClickListener(this);
        this.mProgessbar = (ProgressBar) findViewById(C0258R.id.arrival_bus_progress);
        if (this.bEmergency) {
            mArrivalbg.setBackgroundResource(C0258R.drawable.background_emer);
            stopInfoBg.setBackgroundColor(Color.parseColor("#D7ADA7"));
        } else {
            mArrivalbg.setBackgroundResource(C0258R.drawable.background_nomal);
            stopInfoBg.setBackgroundColor(Color.parseColor("#77ADD1"));
        }
        init();
        new ArrivalTimeRequest().start();
        this.mProgessbar.setVisibility(0);
    }

    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case C0258R.id.arrival_time_add_favorite:
                String selection = "favor_stopid=" + this.mStopId + " and " + "favor_routeid" + "=" + this.mRouteId;
                Cursor favoriteCursor = getContentResolver().query(DataConst.CONTENT_FAVORITES_URI, new String[]{"favor_routeid", "favor_stopid"}, selection, null, null);
                try {
                    if (favoriteCursor.getCount() > 0) {
                        Toast.makeText(getApplicationContext(), "이미 등록된 정류장 입니다. ", 0).show();
                    } else {
                        ContentValues value = new ContentValues();
                        value.put(DataConst.KEY_FAVORITE_CATEGORY, "3");
                        value.put("favor_routeid", this.mRouteId);
                        value.put("favor_stopid", this.mStopId);
                        value.put(DataConst.KEY_FAVORITE_ALARM, "0");
                        getContentResolver().insert(DataConst.CONTENT_FAVORITES_URI, value);
                        Toast.makeText(getApplicationContext(), "즐겨찾기에 추가되었습니다", 0).show();
                    }
                    favoriteCursor.close();
                    return;
                } catch (Exception e) {
                    return;
                }
            case C0258R.id.icon_refresh_stop_infomation:
                new ArrivalTimeRequest().start();
                this.mProgessbar.setVisibility(0);
                return;
            default:
                return;
        }
    }
}
