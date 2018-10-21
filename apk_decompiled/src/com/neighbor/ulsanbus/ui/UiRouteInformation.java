package com.neighbor.ulsanbus.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog.Builder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.neighbor.ulsanbus.C0258R;
import com.neighbor.ulsanbus.RouteInfoDetail;
import com.neighbor.ulsanbus.Timetable;
import com.neighbor.ulsanbus.data.CurRoute;
import com.neighbor.ulsanbus.data.DataConst;
import com.neighbor.ulsanbus.data.DataSAXParser;
import com.neighbor.ulsanbus.util.ExtendedHorizontalScrollView;
import com.neighbor.ulsanbus.util.ExtendedHorizontalScrollView.IScrollStateListener;
import com.neighbor.ulsanbus.util.Util;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TreeMap;

public class UiRouteInformation extends Activity implements OnClickListener {
    private static final int ROUTE_MAP_REQUEST_STOP = 0;
    private CurRoute CurRoute;
    private String[] RouteProjection = new String[]{DataConst.KEY_NOTICE_ID, "route_id", "route_no", DataConst.KEY_ROUTE_DIR, DataConst.KEY_ROUTE_TYPE, "route_bus_type", DataConst.KEY_ROUTE_BUS_COMPANY, DataConst.KEY_ROUTE_COMPANYTEL, DataConst.KEY_ROUTE_FSTOP_NAME, DataConst.KEY_ROUTE_TSTOP_NAME, DataConst.KEY_ROUTE_WD_START_TIME, DataConst.KEY_ROUTE_WD_END_TIME, DataConst.KEY_ROUTE_WD_MAX_INTERVAL, DataConst.KEY_ROUTE_WD_MIN_INTERVAL, DataConst.KEY_ROUTE_WE_START_TIME, DataConst.KEY_ROUTE_WE_END_TIME, DataConst.KEY_ROUTE_WE_MAX_INTERVAL, DataConst.KEY_ROUTE_WE_MIN_INTERVAL, DataConst.KEY_ROUTE_WS_START_TIME, DataConst.KEY_ROUTE_WS_END_TIME, DataConst.KEY_ROUTE_WS_MAX_INTERVAL, DataConst.KEY_ROUTE_WS_MIN_INTERVAL, "route_interval", DataConst.KEY_ROUTE_TRAVEL_TIME, DataConst.KEY_ROUTE_LENGTH, DataConst.KEY_ROUTE_OPERATION_CNT, DataConst.KEY_ROUTE_REMARK, DataConst.KEY_BRT_TIME_FLAG, DataConst.KEY_BRT_APP_REMARK, DataConst.KEY_BRT_APP_WAYPOINT_DESC, DataConst.KEY_BRT_CLASS_SEQNO};
    private boolean bEmergency;
    private TextView businessInterval;
    private TextView businessTime;
    private TextView companyName;
    private TextView companyTel;
    private Cursor dirRoutecursor;
    private LinearLayout dir_group;
    private ExtendedHorizontalScrollView directionHorizontalScrollView;
    private Button direction_button0;
    private Button direction_button1;
    private Button direction_button2;
    private Button direction_button3;
    private Button direction_button4;
    private Button direction_button5;
    private Button direction_button6;
    private Button direction_button7;
    private Button direction_button8;
    private Button direction_button9;
    private ImageView direction_next;
    private ImageView direction_prev;
    private InputMethodManager imm;
    private ImageView mBgImage;
    private String mRouteID;
    private RouteInfoDetail mRouteInfoDetail = new RouteInfoDetail();
    private DataSAXParser mSaxHelper;
    SharedPreferences prefs;
    private Button recycle_button0;
    private Button recycle_button1;
    private Button recycle_button2;
    private Button recycle_button3;
    private Button recycle_button4;
    private Button recycle_button5;
    private Button recycle_button6;
    private Button recycle_button7;
    private Button recycle_button8;
    private ExtendedHorizontalScrollView recyclerHorizontalScrollView;
    private LinearLayout recycler_group;
    private ImageView recycler_next;
    private ImageView recycler_prev;
    private RelativeLayout routelayout;
    private TimetableRequest scheduleReq;
    private TextView txBusinessTimewd;
    private TextView txBusinessTimewe;
    private TextView txBusinessTimews;
    private TextView txSection;
    private TextView txbusNo;
    private TextView txbusType;

    /* renamed from: com.neighbor.ulsanbus.ui.UiRouteInformation$1 */
    class C02951 implements OnClickListener {
        C02951() {
        }

        public void onClick(View v) {
            UiRouteInformation.this.recyclerHorizontalScrollView.smoothScrollBy(200, 0);
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiRouteInformation$2 */
    class C02962 implements OnClickListener {
        C02962() {
        }

        public void onClick(View v) {
            UiRouteInformation.this.recyclerHorizontalScrollView.smoothScrollBy(-200, 0);
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiRouteInformation$3 */
    class C02973 implements OnClickListener {
        C02973() {
        }

        public void onClick(View v) {
            UiRouteInformation.this.recyclerHorizontalScrollView.smoothScrollBy(200, 0);
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiRouteInformation$4 */
    class C02984 implements OnClickListener {
        C02984() {
        }

        public void onClick(View v) {
            UiRouteInformation.this.recyclerHorizontalScrollView.smoothScrollBy(-200, 0);
        }
    }

    class TimetableRequest extends AsyncTask<Void, Integer, Integer> {
        private ArrayList<Timetable> mArrayListTimetable;
        private String mRouteId;
        private String mScheduleMode;
        private HashMap<String, TreeMap<Integer, ArrayList<String>>> mTimeTable;
        private int responseCode = 0;

        /* renamed from: com.neighbor.ulsanbus.ui.UiRouteInformation$TimetableRequest$1 */
        class C02991 implements DialogInterface.OnClickListener {
            C02991() {
            }

            public void onClick(DialogInterface dialog, int which) {
            }
        }

        public TimetableRequest(String routeid, String mode) {
            this.mRouteId = routeid;
            this.mTimeTable = new HashMap();
            this.mArrayListTimetable = new ArrayList();
            this.mScheduleMode = mode;
        }

        protected Integer doInBackground(Void... arg0) {
            Log.e("doInBackground: ", this.mRouteId);
            try {
                UiRouteInformation.this.mSaxHelper.getName(DataConst.API_ROUTE_TIMETABLE + this.mRouteId + "&dayinfo=" + String.valueOf(Integer.parseInt(this.mScheduleMode) - 1), DataConst.FLAG_DAY_ROUTE_SCHEDULE);
            } catch (Exception e) {
                e.printStackTrace();
                this.responseCode = 3;
            }
            return Integer.valueOf(this.responseCode);
        }

        protected void onCancelled() {
            super.onCancelled();
        }

        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result.intValue() == 0) {
                try {
                    this.mArrayListTimetable = UiRouteInformation.this.mSaxHelper.getmArrayListTimetable();
                    StringBuffer message = new StringBuffer();
                    if (this.mArrayListTimetable == null || this.mArrayListTimetable.size() == 0) {
                        message.append("선택하신 노선은 \n현재 운행하는 노선이 없습니다.");
                    } else {
                        message.append("선택하신 노선은 \n");
                        for (int i = 0; i < this.mArrayListTimetable.size(); i++) {
                            message.append(" " + (((Timetable) this.mArrayListTimetable.get(i)).getTodayTime().substring(0, 2) + ":" + ((Timetable) this.mArrayListTimetable.get(i)).getTodayTime().substring(2, 4)));
                        }
                        message.append("\n 시간에만 운행합니다.");
                    }
                    new Builder(UiRouteInformation.this.getParent(), C0258R.style.MyAlertDialogTheme).setMessage(message.toString()).setPositiveButton((CharSequence) "확인", new C02991()).create().show();
                } catch (Exception e) {
                }
            }
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiRouteInformation$5 */
    class C04425 implements IScrollStateListener {
        C04425() {
        }

        public void onScrollMostLeft() {
            ((View) UiRouteInformation.this.recyclerHorizontalScrollView.getParent()).findViewById(C0258R.id.recycler_prev).setVisibility(4);
        }

        public void onScrollFromMostLeft() {
            ((View) UiRouteInformation.this.recyclerHorizontalScrollView.getParent()).findViewById(C0258R.id.recycler_prev).setVisibility(0);
        }

        public void onScrollMostRight() {
            ((View) UiRouteInformation.this.recyclerHorizontalScrollView.getParent()).findViewById(C0258R.id.recycler_next).setVisibility(4);
        }

        public void onScrollFromMostRight() {
            ((View) UiRouteInformation.this.recyclerHorizontalScrollView.getParent()).findViewById(C0258R.id.recycler_next).setVisibility(0);
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiRouteInformation$6 */
    class C04436 implements IScrollStateListener {
        C04436() {
        }

        public void onScrollMostLeft() {
            ((View) UiRouteInformation.this.directionHorizontalScrollView.getParent()).findViewById(C0258R.id.direction_prev).setVisibility(4);
        }

        public void onScrollFromMostLeft() {
            ((View) UiRouteInformation.this.directionHorizontalScrollView.getParent()).findViewById(C0258R.id.direction_prev).setVisibility(0);
        }

        public void onScrollMostRight() {
            ((View) UiRouteInformation.this.directionHorizontalScrollView.getParent()).findViewById(C0258R.id.direction_next).setVisibility(4);
        }

        public void onScrollFromMostRight() {
            ((View) UiRouteInformation.this.directionHorizontalScrollView.getParent()).findViewById(C0258R.id.direction_next).setVisibility(0);
        }
    }

    private void init() {
        this.routelayout = (RelativeLayout) findViewById(C0258R.id.routeinfomation_top_layout);
        this.mBgImage = (ImageView) findViewById(C0258R.id.route_info_background);
        if (this.bEmergency) {
            this.mBgImage.setImageResource(C0258R.drawable.background_emer);
        } else {
            this.mBgImage.setImageResource(C0258R.drawable.background_nomal);
        }
        this.txbusType = (TextView) findViewById(C0258R.id.route_info_route_type);
        this.txbusNo = (TextView) findViewById(C0258R.id.route_info_route_number);
        this.txSection = (TextView) findViewById(C0258R.id.route_info_section);
        this.txBusinessTimewd = (TextView) findViewById(C0258R.id.today_business_time_working);
        this.txBusinessTimewe = (TextView) findViewById(C0258R.id.today_business_time_semi);
        this.txBusinessTimews = (TextView) findViewById(C0258R.id.today_business_time_holiday);
        Button btnBusinessTimeWD = (Button) findViewById(C0258R.id.btn_business_time_working);
        btnBusinessTimeWD.setOnClickListener(this);
        Button btnBusinessTimeWS = (Button) findViewById(C0258R.id.btn_business_time_semi);
        btnBusinessTimeWS.setOnClickListener(this);
        Button btnBusinessTimeWE = (Button) findViewById(C0258R.id.btn_business_time_holiday);
        btnBusinessTimeWE.setOnClickListener(this);
        if (this.bEmergency) {
            btnBusinessTimeWD.setVisibility(4);
            btnBusinessTimeWS.setVisibility(4);
            btnBusinessTimeWE.setVisibility(4);
        } else {
            btnBusinessTimeWD.setVisibility(0);
            btnBusinessTimeWS.setVisibility(0);
            btnBusinessTimeWE.setVisibility(0);
        }
        ((Button) findViewById(C0258R.id.btn_route_info_buspos)).setOnClickListener(this);
        ((Button) findViewById(C0258R.id.btn_route_info_add_favorite)).setOnClickListener(this);
        ((Button) findViewById(C0258R.id.btn_route_info_map)).setOnClickListener(this);
        this.businessInterval = (TextView) findViewById(C0258R.id.today_business_inteval);
        this.companyName = (TextView) findViewById(C0258R.id.route_company_name);
        this.companyTel = (TextView) findViewById(C0258R.id.route_company_tel);
        ((TextView) findViewById(C0258R.id.today_business_time_notice)).setVisibility(8);
        this.recycler_group = (LinearLayout) findViewById(C0258R.id.recycler_group);
        this.recycle_button0 = (Button) findViewById(C0258R.id.route_type_recycle_button0);
        this.recycle_button1 = (Button) findViewById(C0258R.id.route_type_recycle_button1);
        this.recycle_button2 = (Button) findViewById(C0258R.id.route_type_recycle_button2);
        this.recycle_button3 = (Button) findViewById(C0258R.id.route_type_recycle_button3);
        this.recycle_button4 = (Button) findViewById(C0258R.id.route_type_recycle_button4);
        this.recycle_button5 = (Button) findViewById(C0258R.id.route_type_recycle_button5);
        this.recycle_button6 = (Button) findViewById(C0258R.id.route_type_recycle_button6);
        this.recycle_button7 = (Button) findViewById(C0258R.id.route_type_recycle_button7);
        this.recycle_button8 = (Button) findViewById(C0258R.id.route_type_recycle_button8);
        this.recycle_button0.setOnClickListener(this);
        this.recycle_button1.setOnClickListener(this);
        this.recycle_button2.setOnClickListener(this);
        this.recycle_button3.setOnClickListener(this);
        this.recycle_button4.setOnClickListener(this);
        this.recycle_button5.setOnClickListener(this);
        this.recycle_button6.setOnClickListener(this);
        this.recycle_button7.setOnClickListener(this);
        this.recycle_button8.setOnClickListener(this);
        this.dir_group = (LinearLayout) findViewById(C0258R.id.dir_group);
        this.direction_button0 = (Button) findViewById(C0258R.id.route_type_dir_button0);
        this.direction_button1 = (Button) findViewById(C0258R.id.route_type_dir_button1);
        this.direction_button2 = (Button) findViewById(C0258R.id.route_type_dir_button2);
        this.direction_button3 = (Button) findViewById(C0258R.id.route_type_dir_button3);
        this.direction_button4 = (Button) findViewById(C0258R.id.route_type_dir_button4);
        this.direction_button5 = (Button) findViewById(C0258R.id.route_type_dir_button5);
        this.direction_button6 = (Button) findViewById(C0258R.id.route_type_dir_button6);
        this.direction_button7 = (Button) findViewById(C0258R.id.route_type_dir_button7);
        this.direction_button8 = (Button) findViewById(C0258R.id.route_type_dir_button8);
        this.direction_button9 = (Button) findViewById(C0258R.id.route_type_dir_button9);
        this.direction_button0.setOnClickListener(this);
        this.direction_button1.setOnClickListener(this);
        this.direction_button2.setOnClickListener(this);
        this.direction_button3.setOnClickListener(this);
        this.direction_button4.setOnClickListener(this);
        this.direction_button5.setOnClickListener(this);
        this.direction_button6.setOnClickListener(this);
        this.direction_button7.setOnClickListener(this);
        this.direction_button8.setOnClickListener(this);
        this.direction_button9.setOnClickListener(this);
        this.recyclerHorizontalScrollView = (ExtendedHorizontalScrollView) findViewById(C0258R.id.recycler_scroller);
        this.directionHorizontalScrollView = (ExtendedHorizontalScrollView) findViewById(C0258R.id.direction_scroller);
        this.recycler_prev = (ImageView) findViewById(C0258R.id.recycler_prev);
        this.recycler_next = (ImageView) findViewById(C0258R.id.recycler_next);
        this.direction_prev = (ImageView) findViewById(C0258R.id.direction_prev);
        this.direction_next = (ImageView) findViewById(C0258R.id.direction_next);
        this.recycler_next.setOnClickListener(new C02951());
        this.recycler_prev.setOnClickListener(new C02962());
        this.direction_next.setOnClickListener(new C02973());
        this.direction_prev.setOnClickListener(new C02984());
        ((ExtendedHorizontalScrollView) findViewById(C0258R.id.recycler_scroller)).setScrollStateListener(new C04425());
        ((ExtendedHorizontalScrollView) findViewById(C0258R.id.direction_scroller)).setScrollStateListener(new C04436());
    }

    private void setCurRouteInfo(Cursor cursor) {
        String OpenTime;
        String CloseTime;
        String MaxInterval;
        String MinInterval;
        this.CurRoute = new CurRoute();
        this.CurRoute.setmCurRouteId(cursor.getString(cursor.getColumnIndex("route_id")));
        this.CurRoute.setmCurRouteNumber(cursor.getString(cursor.getColumnIndex("route_no")));
        this.CurRoute.setmCurRouteDir(cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_DIR)));
        this.CurRoute.setmCurRouteType(cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_TYPE)));
        this.CurRoute.setmCurRouteBustype(cursor.getString(cursor.getColumnIndex("route_bus_type")));
        this.CurRoute.setmCurRouteTSTopName(cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_TSTOP_NAME)));
        this.CurRoute.setmCurRouteFStopName(cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_FSTOP_NAME)));
        this.CurRoute.setmCurBusCompanyName(cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_BUS_COMPANY)));
        this.CurRoute.setmCurBusCompanyTel(cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_COMPANYTEL)));
        this.CurRoute.setmCurRouteRemark(cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_REMARK)));
        this.CurRoute.setmCurRouteOpenTimeWS(cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_WS_START_TIME)));
        this.CurRoute.setmCurRouteCloseTimeWS(cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_WS_END_TIME)));
        this.CurRoute.setmCurRouteOpenTimeWE(cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_WE_START_TIME)));
        this.CurRoute.setmCurRouteCloseTimeWE(cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_WE_END_TIME)));
        this.CurRoute.setmCurRouteOpenTimeWD(cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_WD_START_TIME)));
        this.CurRoute.setmCurRouteCloseTimeWD(cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_WD_END_TIME)));
        this.CurRoute.setmTimeFlag(cursor.getString(cursor.getColumnIndex(DataConst.KEY_BRT_TIME_FLAG)));
        this.CurRoute.setmCurBrtClassSeqNo(cursor.getString(cursor.getColumnIndex(DataConst.KEY_BRT_CLASS_SEQNO)));
        Editor editor = this.prefs.edit();
        if (Util.getCurrentTabNum() == 1) {
            editor.putString(DataConst.BUS_ID_TAB1, this.CurRoute.getmCurRouteId());
        } else {
            editor.putString(DataConst.BUS_ID_TAB3, this.CurRoute.getmCurRouteId());
        }
        editor.commit();
        int date = Calendar.getInstance().get(7);
        if (date == 1) {
            OpenTime = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_WS_START_TIME));
            CloseTime = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_WS_END_TIME));
            MaxInterval = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_WS_MAX_INTERVAL));
            MinInterval = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_WS_MIN_INTERVAL));
        } else if (date == 7 || ((this.CurRoute.getmTimeFlag() != null && this.CurRoute.getmTimeFlag().equals("1")) || 6 == date)) {
            OpenTime = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_WE_START_TIME));
            CloseTime = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_WE_END_TIME));
            MaxInterval = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_WE_MAX_INTERVAL));
            MinInterval = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_WE_MIN_INTERVAL));
        } else {
            OpenTime = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_WD_START_TIME));
            CloseTime = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_WD_END_TIME));
            MaxInterval = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_WD_MAX_INTERVAL));
            MinInterval = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_WD_MIN_INTERVAL));
        }
        this.CurRoute.setmCurRouteOpenTime(OpenTime);
        this.CurRoute.setmCurRouteCloseTime(CloseTime);
        this.CurRoute.setmCurRouteMaxInterval(MaxInterval);
        this.CurRoute.setmCurRouteMinInterval(MinInterval);
        String type = this.CurRoute.getmCurRouteType();
        String seqNo = this.CurRoute.getmCurBrtClassSeqNo();
        Log.e("setCurRouteInfo: ", this.CurRoute.getmCurBrtClassSeqNo());
        String day;
        int todayDate;
        if (this.CurRoute.getmCurRouteDir().equals("3")) {
            if (seqNo.equals("0")) {
                this.recycle_button0.setSelected(true);
            } else if (seqNo.equals("1")) {
                this.recycle_button1.setSelected(true);
            } else if (seqNo.equals("2")) {
                this.recycle_button2.setSelected(true);
            } else if (seqNo.equals("3")) {
                this.recycle_button3.setSelected(true);
            } else if (seqNo.equals("4")) {
                this.recycle_button4.setSelected(true);
            } else if (seqNo.equals("5")) {
                this.recycle_button5.setSelected(true);
            } else if (seqNo.equals("6")) {
                this.recycle_button6.setSelected(true);
            } else if (seqNo.equals("7")) {
                this.recycle_button7.setSelected(true);
            } else if (seqNo.equals("8")) {
                this.recycle_button8.setSelected(true);
            }
            if (!seqNo.equals("0")) {
                day = "1";
                todayDate = Calendar.getInstance().get(7);
                Log.d("hts", "date : " + (date == 6));
                if (todayDate == 1) {
                    day = "3";
                } else if (todayDate == 7 || (this.CurRoute.getmTimeFlag() != null && this.CurRoute.getmTimeFlag().equals("1") && 6 == todayDate)) {
                    day = "2";
                } else {
                    day = "1";
                }
                this.mSaxHelper = new DataSAXParser();
                this.scheduleReq = new TimetableRequest(this.CurRoute.getmCurRouteId(), day);
                this.scheduleReq.execute(new Void[0]);
            }
        } else if (this.CurRoute.getmCurRouteDir().equals("1")) {
            if (type.equals("0")) {
                this.direction_button0.setSelected(true);
            } else if (type.equals("1")) {
                this.direction_button1.setSelected(true);
            } else if (type.equals("2")) {
                this.direction_button2.setSelected(true);
            } else if (type.equals("3")) {
                this.direction_button3.setSelected(true);
            } else if (type.equals("4")) {
                this.direction_button4.setSelected(true);
            }
            if (!type.equals("0")) {
                day = "1";
                todayDate = Calendar.getInstance().get(7);
                Log.d("hts", "date : " + (date == 6));
                if (todayDate == 1) {
                    day = "3";
                } else if (todayDate == 7 || (this.CurRoute.getmTimeFlag() != null && this.CurRoute.getmTimeFlag().equals("1") && 6 == todayDate)) {
                    day = "2";
                } else {
                    day = "1";
                }
                this.mSaxHelper = new DataSAXParser();
                this.scheduleReq = new TimetableRequest(this.CurRoute.getmCurRouteId(), day);
                this.scheduleReq.execute(new Void[0]);
            }
        } else if (this.CurRoute.getmCurRouteDir().equals("2")) {
            if (type.equals("0")) {
                this.direction_button5.setSelected(true);
            } else if (type.equals("1")) {
                this.direction_button6.setSelected(true);
            } else if (type.equals("2")) {
                this.direction_button7.setSelected(true);
            } else if (type.equals("3")) {
                this.direction_button8.setSelected(true);
            } else if (type.equals("4")) {
                this.direction_button9.setSelected(true);
            }
            if (!type.equals("0")) {
                day = "1";
                todayDate = Calendar.getInstance().get(7);
                Log.d("hts", "date : " + (date == 6));
                if (todayDate == 1) {
                    day = "3";
                } else if (todayDate == 7 || (this.CurRoute.getmTimeFlag() != null && this.CurRoute.getmTimeFlag().equals("1") && 6 == todayDate)) {
                    day = "2";
                } else {
                    day = "1";
                }
                this.mSaxHelper = new DataSAXParser();
                this.scheduleReq = new TimetableRequest(this.CurRoute.getmCurRouteId(), day);
                this.scheduleReq.execute(new Void[0]);
            }
        }
    }

    protected void onPause() {
        super.onPause();
        DataConst.SelectedRouteId = "";
    }

    void setDefault() {
        this.imm = (InputMethodManager) getSystemService("input_method");
        if (this.imm.isActive()) {
            this.imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
        this.prefs = getSharedPreferences("PreStatus", 0);
    }

    void goToStopInfo() {
        String stopId = DataConst.SelectedStopId;
        if (stopId.length() > 0) {
            Cursor cursor = getContentResolver().query(DataConst.CONTENT_STOPS_URI, new String[]{DataConst.KEY_NOTICE_ID, "stop_name", "stop_id", DataConst.KEY_STOP_X, DataConst.KEY_STOP_Y}, "stop_id = " + stopId, null, null);
            if (cursor.moveToFirst()) {
                DataConst.SelectedStopId = "";
                Intent intent = new Intent(this, UiStopInformation.class);
                intent.putExtra("stop_id", cursor.getString(cursor.getColumnIndex("stop_id")));
                intent.putExtra("stop_name", cursor.getString(cursor.getColumnIndex("stop_name")));
                intent.putExtra("stop_coord_x", cursor.getString(cursor.getColumnIndex(DataConst.KEY_STOP_X)));
                intent.putExtra("stop_coord_y", cursor.getString(cursor.getColumnIndex(DataConst.KEY_STOP_Y)));
                cursor.close();
                TabFirst.TAB1.replaceActivity("BusStop", intent, this);
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0258R.layout.route_information);
        setDefault();
        goToStopInfo();
        String idfrompref = "";
        String idfromData = "";
        this.bEmergency = this.prefs.getBoolean(DataConst.KEY_PREF_EMER_MODE, false);
        if (Util.getCurrentTabNum() == 1) {
            idfrompref = this.prefs.getString(DataConst.BUS_ID_TAB1, "");
        } else if (Util.getCurrentTabNum() == 3) {
            idfrompref = this.prefs.getString(DataConst.BUS_ID_TAB3, "");
        }
        if (!DataConst.SelectedRouteId.equals("")) {
            idfromData = DataConst.SelectedRouteId;
        }
        if (idfrompref.equals("")) {
            idfrompref = idfromData;
        }
        this.mRouteID = idfrompref;
        init();
        this.dirRoutecursor = getContentResolver().query(DataConst.CONTENT_ROUTE_URI, this.RouteProjection, "route_id= " + this.mRouteID, null, null);
        if (this.dirRoutecursor.moveToFirst() && this.dirRoutecursor.getCount() > 0) {
            setCurRouteInfo(this.dirRoutecursor);
            dispInformation();
        }
    }

    void dispInformation() {
        String[] colorArray = getResources().getStringArray(C0258R.array.bus_type_color);
        String[] busTypeArray = getResources().getStringArray(C0258R.array.stop_info_header_type);
        if (this.CurRoute.getmCurRouteDir().equals("1") || this.CurRoute.getmCurRouteDir().equals("2")) {
            this.recycler_group.setVisibility(8);
            this.dir_group.setVisibility(0);
            setDirectionBtnUI();
        } else if (this.CurRoute.getmCurRouteDir().equals("3")) {
            this.recycler_group.setVisibility(0);
            this.dir_group.setVisibility(8);
            setRecycleBtnUI();
        }
        int valueOfBusType = Integer.parseInt(this.CurRoute.getmCurRouteBustype());
        this.routelayout.setBackgroundColor(Color.parseColor(colorArray[valueOfBusType]));
        this.txbusType.setText(busTypeArray[valueOfBusType]);
        this.txbusNo.setText(this.CurRoute.getmCurRouteNumber());
        StringBuffer sbSection = new StringBuffer();
        sbSection.append(this.CurRoute.getmCurRouteFStopName());
        sbSection.append(" > ");
        if (this.CurRoute.getmCurRouteRemark().length() > 0) {
            sbSection.append(this.CurRoute.getmCurRouteRemark());
            sbSection.append(" > ");
        }
        sbSection.append(this.CurRoute.getmCurRouteTSTopName());
        this.txSection.setText(sbSection.toString());
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.setLength(0);
        stringBuffer.append(this.CurRoute.getmCurRouteMinInterval() + "분");
        stringBuffer.append(" ~ ");
        stringBuffer.append(this.CurRoute.getmCurRouteMaxInterval() + "분");
        if (this.bEmergency) {
            this.businessInterval.setText("정보 없음");
        } else {
            this.businessInterval.setText(stringBuffer.toString());
        }
        this.companyName.setText(this.CurRoute.getmCurBusCompanyName());
        this.companyTel.setText(this.CurRoute.getmCurBusCompanyTel());
        StringBuffer bizTimeWD = new StringBuffer();
        int date = Calendar.getInstance().get(7);
        if (this.CurRoute.getmTimeFlag().equals("1") && 6 == date) {
            bizTimeWD.append(this.CurRoute.getmCurRouteOpenTimeWE());
            bizTimeWD.insert(2, ":");
            bizTimeWD.append(" ~ ");
            bizTimeWD.append(this.CurRoute.getmCurRouteCloseTimeWE());
            bizTimeWD.insert(10, ":");
        } else {
            bizTimeWD.append(this.CurRoute.getmCurRouteOpenTimeWD());
            Log.e("dispInformation2: ", this.CurRoute.getmCurRouteOpenTimeWD());
            bizTimeWD.insert(2, ":");
            bizTimeWD.append(" ~ ");
            bizTimeWD.append(this.CurRoute.getmCurRouteCloseTimeWD());
            bizTimeWD.insert(10, ":");
        }
        if (this.bEmergency) {
            this.txBusinessTimewd.setText("정보 없음");
        } else {
            this.txBusinessTimewd.setText(bizTimeWD.toString());
        }
        StringBuffer bizTimeWE = new StringBuffer();
        bizTimeWE.append(this.CurRoute.getmCurRouteOpenTimeWE());
        bizTimeWE.insert(2, ":");
        bizTimeWE.append(" ~ ");
        bizTimeWE.append(this.CurRoute.getmCurRouteCloseTimeWE());
        bizTimeWE.insert(10, ":");
        if (this.bEmergency) {
            this.txBusinessTimewe.setText("정보 없음");
        } else {
            this.txBusinessTimewe.setText(bizTimeWE.toString());
        }
        StringBuffer bizTimeWS = new StringBuffer();
        bizTimeWS.append(this.CurRoute.getmCurRouteOpenTimeWS());
        bizTimeWS.insert(2, ":");
        bizTimeWS.append(" ~ ");
        bizTimeWS.append(this.CurRoute.getmCurRouteCloseTimeWS());
        bizTimeWS.insert(10, ":");
        if (this.bEmergency) {
            this.txBusinessTimews.setText("정보 없음");
        } else {
            this.txBusinessTimews.setText(bizTimeWS.toString());
        }
    }

    public void onClick(View arg0) {
        Cursor cursor;
        Intent intent;
        switch (arg0.getId()) {
            case C0258R.id.btn_business_time_holiday:
                DataConst.SelectedRouteId = this.CurRoute.getmCurRouteId();
                intent = new Intent(this, UiTimetable.class);
                intent.putExtra("schedule_mode", "3");
                intent.putExtra("route_id", this.CurRoute.getmCurRouteId());
                intent.putExtra("route_no", this.CurRoute.getmCurRouteNumber());
                intent.putExtra(DataConst.KEY_ROUTE_DIR, this.CurRoute.getmCurRouteDir());
                intent.putExtra("route_bus_type", this.CurRoute.getmCurRouteBustype());
                intent.putExtra("route_bus_tstop", this.CurRoute.getmCurRouteTSTopName());
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
            case C0258R.id.btn_business_time_semi:
                DataConst.SelectedRouteId = this.CurRoute.getmCurRouteId();
                intent = new Intent(this, UiTimetable.class);
                intent.putExtra("schedule_mode", "2");
                intent.putExtra("route_id", this.CurRoute.getmCurRouteId());
                intent.putExtra("route_no", this.CurRoute.getmCurRouteNumber());
                intent.putExtra(DataConst.KEY_ROUTE_DIR, this.CurRoute.getmCurRouteDir());
                intent.putExtra("route_bus_type", this.CurRoute.getmCurRouteBustype());
                intent.putExtra("route_bus_tstop", this.CurRoute.getmCurRouteTSTopName());
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
            case C0258R.id.btn_business_time_working:
                boolean z;
                DataConst.SelectedRouteId = this.CurRoute.getmCurRouteId();
                intent = new Intent(this, UiTimetable.class);
                int date = Calendar.getInstance().get(7);
                String str = "hts";
                StringBuilder append = new StringBuilder().append("date : ");
                if (date == 6) {
                    z = true;
                } else {
                    z = false;
                }
                Log.d(str, append.append(z).toString());
                if (date == 6 && this.CurRoute.getmTimeFlag() != null && this.CurRoute.getmTimeFlag().equals("1")) {
                    intent.putExtra("schedule_mode", "2");
                } else {
                    intent.putExtra("schedule_mode", "1");
                }
                intent.putExtra("route_id", this.CurRoute.getmCurRouteId());
                intent.putExtra("route_no", this.CurRoute.getmCurRouteNumber());
                intent.putExtra(DataConst.KEY_ROUTE_DIR, this.CurRoute.getmCurRouteDir());
                intent.putExtra("route_bus_type", this.CurRoute.getmCurRouteBustype());
                intent.putExtra("route_bus_tstop", this.CurRoute.getmCurRouteTSTopName());
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
            case C0258R.id.btn_route_info_add_favorite:
                Cursor favoriteCursor = getContentResolver().query(DataConst.CONTENT_FAVORITES_URI, new String[]{DataConst.KEY_NOTICE_ID, DataConst.KEY_FAVORITE_CATEGORY, "favor_routeid", "favor_stopid", DataConst.KEY_FAVORITE_ALARM}, "favor_routeid=" + this.CurRoute.getmCurRouteId() + " AND " + DataConst.KEY_FAVORITE_CATEGORY + " = 1", null, null);
                if (favoriteCursor.getCount() == 0) {
                    ContentValues value = new ContentValues();
                    value.put(DataConst.KEY_FAVORITE_CATEGORY, "1");
                    value.put("favor_routeid", this.CurRoute.getmCurRouteId());
                    value.put("favor_stopid", "");
                    value.put(DataConst.KEY_FAVORITE_ALARM, "");
                    getContentResolver().insert(DataConst.CONTENT_FAVORITES_URI, value);
                    Toast.makeText(getApplicationContext(), "즐겨찾기에 추가되었습니다", 0).show();
                    return;
                }
                favoriteCursor.close();
                Toast.makeText(getApplicationContext(), "이미 등록된 노선 입니다. ", 0).show();
                return;
            case C0258R.id.btn_route_info_buspos:
                DataConst.SelectedRouteId = this.CurRoute.getmCurRouteId();
                intent = new Intent(this, UiRouteInfoStopNames.class);
                intent.putExtra("route_id", this.CurRoute.getmCurRouteId());
                intent.putExtra("route_no", this.CurRoute.getmCurRouteNumber());
                intent.putExtra(DataConst.KEY_ROUTE_DIR, this.CurRoute.getmCurRouteDir());
                intent.putExtra("route_bus_type", this.CurRoute.getmCurRouteBustype());
                intent.putExtra(DataConst.KEY_ROUTE_REMARK, this.CurRoute.getmCurRouteRemark());
                intent.putExtra("route_destName", this.CurRoute.getmCurRouteTSTopName());
                intent.putExtra(DataConst.KEY_ROUTE_TYPE, this.CurRoute.getmCurRouteType());
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
            case C0258R.id.btn_route_info_map:
                DataConst.SelectedRouteId = this.CurRoute.getmCurRouteId();
                intent = new Intent(this, UiRouteMap.class);
                intent.putExtra("route_id", this.CurRoute.getmCurRouteId());
                intent.putExtra("route_no", this.CurRoute.getmCurRouteNumber());
                intent.putExtra(DataConst.KEY_ROUTE_DIR, this.CurRoute.getmCurRouteDir());
                intent.putExtra("route_bus_type", this.CurRoute.getmCurRouteBustype());
                intent.putExtra(DataConst.KEY_ROUTE_REMARK, this.CurRoute.getmCurRouteRemark());
                intent.putExtra(DataConst.KEY_ROUTE_TYPE, this.CurRoute.getmCurRouteType());
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
            case C0258R.id.route_type_dir_button0:
                this.direction_button0.setSelected(true);
                this.direction_button1.setSelected(false);
                this.direction_button2.setSelected(false);
                this.direction_button3.setSelected(false);
                this.direction_button4.setSelected(false);
                this.direction_button5.setSelected(false);
                this.direction_button6.setSelected(false);
                this.direction_button7.setSelected(false);
                this.direction_button8.setSelected(false);
                this.direction_button9.setSelected(false);
                cursor = getTypeOfCursor("0", "1");
                if (cursor.moveToFirst()) {
                    setCurRouteInfo(cursor);
                    dispInformation();
                }
                cursor.close();
                return;
            case C0258R.id.route_type_dir_button1:
                this.direction_button0.setSelected(false);
                this.direction_button1.setSelected(true);
                this.direction_button2.setSelected(false);
                this.direction_button3.setSelected(false);
                this.direction_button4.setSelected(false);
                this.direction_button5.setSelected(false);
                this.direction_button6.setSelected(false);
                this.direction_button7.setSelected(false);
                this.direction_button8.setSelected(false);
                this.direction_button9.setSelected(false);
                cursor = getTypeOfCursor("1", "1");
                if (cursor.moveToFirst()) {
                    setCurRouteInfo(cursor);
                    dispInformation();
                }
                cursor.close();
                return;
            case C0258R.id.route_type_dir_button2:
                this.direction_button0.setSelected(false);
                this.direction_button1.setSelected(false);
                this.direction_button2.setSelected(true);
                this.direction_button3.setSelected(false);
                this.direction_button4.setSelected(false);
                this.direction_button5.setSelected(false);
                this.direction_button6.setSelected(false);
                this.direction_button7.setSelected(false);
                this.direction_button8.setSelected(false);
                this.direction_button9.setSelected(false);
                cursor = getTypeOfCursor("2", "1");
                if (cursor.moveToFirst()) {
                    setCurRouteInfo(cursor);
                    dispInformation();
                }
                cursor.close();
                return;
            case C0258R.id.route_type_dir_button3:
                this.direction_button0.setSelected(false);
                this.direction_button1.setSelected(false);
                this.direction_button2.setSelected(false);
                this.direction_button3.setSelected(true);
                this.direction_button4.setSelected(false);
                this.direction_button5.setSelected(false);
                this.direction_button6.setSelected(false);
                this.direction_button7.setSelected(false);
                this.direction_button8.setSelected(false);
                this.direction_button9.setSelected(false);
                cursor = getTypeOfCursor("3", "1");
                if (cursor.moveToFirst()) {
                    setCurRouteInfo(cursor);
                    dispInformation();
                }
                cursor.close();
                return;
            case C0258R.id.route_type_dir_button4:
                this.direction_button0.setSelected(false);
                this.direction_button1.setSelected(false);
                this.direction_button2.setSelected(false);
                this.direction_button3.setSelected(false);
                this.direction_button4.setSelected(true);
                this.direction_button5.setSelected(false);
                this.direction_button6.setSelected(false);
                this.direction_button7.setSelected(false);
                this.direction_button8.setSelected(false);
                this.direction_button9.setSelected(false);
                cursor = getTypeOfCursor("4", "1");
                if (cursor.moveToFirst()) {
                    setCurRouteInfo(cursor);
                    dispInformation();
                }
                cursor.close();
                return;
            case C0258R.id.route_type_dir_button5:
                this.direction_button0.setSelected(false);
                this.direction_button1.setSelected(false);
                this.direction_button2.setSelected(false);
                this.direction_button3.setSelected(false);
                this.direction_button4.setSelected(false);
                this.direction_button5.setSelected(true);
                this.direction_button6.setSelected(false);
                this.direction_button7.setSelected(false);
                this.direction_button8.setSelected(false);
                this.direction_button9.setSelected(false);
                cursor = getTypeOfCursor("0", "2");
                if (cursor.moveToFirst()) {
                    setCurRouteInfo(cursor);
                    dispInformation();
                }
                cursor.close();
                return;
            case C0258R.id.route_type_dir_button6:
                this.direction_button0.setSelected(false);
                this.direction_button1.setSelected(false);
                this.direction_button2.setSelected(false);
                this.direction_button3.setSelected(false);
                this.direction_button4.setSelected(false);
                this.direction_button5.setSelected(false);
                this.direction_button6.setSelected(true);
                this.direction_button7.setSelected(false);
                this.direction_button8.setSelected(false);
                this.direction_button9.setSelected(false);
                cursor = getTypeOfCursor("1", "2");
                if (cursor.moveToFirst()) {
                    setCurRouteInfo(cursor);
                    dispInformation();
                }
                cursor.close();
                return;
            case C0258R.id.route_type_dir_button7:
                this.direction_button0.setSelected(false);
                this.direction_button1.setSelected(false);
                this.direction_button2.setSelected(false);
                this.direction_button3.setSelected(false);
                this.direction_button4.setSelected(false);
                this.direction_button5.setSelected(false);
                this.direction_button6.setSelected(false);
                this.direction_button7.setSelected(true);
                this.direction_button8.setSelected(false);
                this.direction_button9.setSelected(false);
                cursor = getTypeOfCursor("2", "2");
                if (cursor.moveToFirst()) {
                    setCurRouteInfo(cursor);
                    dispInformation();
                }
                cursor.close();
                return;
            case C0258R.id.route_type_dir_button8:
                this.direction_button0.setSelected(false);
                this.direction_button1.setSelected(false);
                this.direction_button2.setSelected(false);
                this.direction_button3.setSelected(false);
                this.direction_button4.setSelected(false);
                this.direction_button5.setSelected(false);
                this.direction_button6.setSelected(false);
                this.direction_button7.setSelected(false);
                this.direction_button8.setSelected(true);
                this.direction_button9.setSelected(false);
                cursor = getTypeOfCursor("3", "2");
                if (cursor.moveToFirst()) {
                    setCurRouteInfo(cursor);
                    dispInformation();
                }
                cursor.close();
                return;
            case C0258R.id.route_type_dir_button9:
                this.direction_button0.setSelected(false);
                this.direction_button1.setSelected(false);
                this.direction_button2.setSelected(false);
                this.direction_button3.setSelected(false);
                this.direction_button4.setSelected(false);
                this.direction_button5.setSelected(false);
                this.direction_button6.setSelected(false);
                this.direction_button7.setSelected(false);
                this.direction_button8.setSelected(false);
                this.direction_button9.setSelected(true);
                cursor = getTypeOfCursor("4", "2");
                if (cursor.moveToFirst()) {
                    setCurRouteInfo(cursor);
                    dispInformation();
                }
                cursor.close();
                return;
            case C0258R.id.route_type_recycle_button0:
                if (this.CurRoute.getmCurRouteDir().equals("3")) {
                    this.recycle_button0.setSelected(true);
                    this.recycle_button1.setSelected(false);
                    this.recycle_button2.setSelected(false);
                    this.recycle_button3.setSelected(false);
                    this.recycle_button4.setSelected(false);
                    this.recycle_button5.setSelected(false);
                    this.recycle_button6.setSelected(false);
                    this.recycle_button7.setSelected(false);
                    this.recycle_button8.setSelected(false);
                    cursor = getSeqOfCursor("0", "3");
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            setCurRouteInfo(cursor);
                            dispInformation();
                        }
                        cursor.close();
                        return;
                    }
                    return;
                }
                return;
            case C0258R.id.route_type_recycle_button1:
                Log.e("onClick1: ", this.CurRoute.getmCurRouteNumber());
                if (this.CurRoute.getmCurRouteDir().equals("3")) {
                    this.recycle_button0.setSelected(false);
                    this.recycle_button1.setSelected(true);
                    this.recycle_button2.setSelected(false);
                    this.recycle_button3.setSelected(false);
                    this.recycle_button4.setSelected(false);
                    this.recycle_button5.setSelected(false);
                    this.recycle_button6.setSelected(false);
                    this.recycle_button7.setSelected(false);
                    this.recycle_button8.setSelected(false);
                    cursor = getSeqOfCursor("1", "3");
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            setCurRouteInfo(cursor);
                            dispInformation();
                        }
                        cursor.close();
                        return;
                    }
                    return;
                }
                break;
            case C0258R.id.route_type_recycle_button2:
                break;
            case C0258R.id.route_type_recycle_button3:
                if (this.CurRoute.getmCurRouteDir().equals("3")) {
                    this.recycle_button0.setSelected(false);
                    this.recycle_button1.setSelected(false);
                    this.recycle_button2.setSelected(false);
                    this.recycle_button3.setSelected(true);
                    this.recycle_button4.setSelected(false);
                    this.recycle_button5.setSelected(false);
                    this.recycle_button6.setSelected(false);
                    this.recycle_button7.setSelected(false);
                    this.recycle_button8.setSelected(false);
                    cursor = getSeqOfCursor("3", "3");
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            setCurRouteInfo(cursor);
                            dispInformation();
                        }
                        cursor.close();
                        return;
                    }
                    return;
                }
                return;
            case C0258R.id.route_type_recycle_button4:
                if (this.CurRoute.getmCurRouteDir().equals("3")) {
                    this.recycle_button0.setSelected(false);
                    this.recycle_button1.setSelected(false);
                    this.recycle_button2.setSelected(false);
                    this.recycle_button3.setSelected(false);
                    this.recycle_button4.setSelected(true);
                    this.recycle_button5.setSelected(false);
                    this.recycle_button6.setSelected(false);
                    this.recycle_button7.setSelected(false);
                    this.recycle_button8.setSelected(false);
                    cursor = getSeqOfCursor("4", "3");
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            setCurRouteInfo(cursor);
                            dispInformation();
                        }
                        cursor.close();
                        return;
                    }
                    return;
                }
                return;
            case C0258R.id.route_type_recycle_button5:
                if (this.CurRoute.getmCurRouteDir().equals("3")) {
                    this.recycle_button0.setSelected(false);
                    this.recycle_button1.setSelected(false);
                    this.recycle_button2.setSelected(false);
                    this.recycle_button3.setSelected(false);
                    this.recycle_button4.setSelected(false);
                    this.recycle_button5.setSelected(true);
                    this.recycle_button6.setSelected(false);
                    this.recycle_button7.setSelected(false);
                    this.recycle_button8.setSelected(false);
                    cursor = getSeqOfCursor("5", "3");
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            setCurRouteInfo(cursor);
                            dispInformation();
                        }
                        cursor.close();
                        return;
                    }
                    return;
                }
                return;
            case C0258R.id.route_type_recycle_button6:
                if (this.CurRoute.getmCurRouteDir().equals("3")) {
                    this.recycle_button0.setSelected(false);
                    this.recycle_button1.setSelected(false);
                    this.recycle_button2.setSelected(false);
                    this.recycle_button3.setSelected(false);
                    this.recycle_button4.setSelected(false);
                    this.recycle_button5.setSelected(false);
                    this.recycle_button6.setSelected(true);
                    this.recycle_button7.setSelected(false);
                    this.recycle_button8.setSelected(false);
                    cursor = getSeqOfCursor("6", "3");
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            setCurRouteInfo(cursor);
                            dispInformation();
                        }
                        cursor.close();
                        return;
                    }
                    return;
                }
                return;
            case C0258R.id.route_type_recycle_button7:
                if (this.CurRoute.getmCurRouteDir().equals("3")) {
                    this.recycle_button0.setSelected(false);
                    this.recycle_button1.setSelected(false);
                    this.recycle_button2.setSelected(false);
                    this.recycle_button3.setSelected(false);
                    this.recycle_button4.setSelected(false);
                    this.recycle_button5.setSelected(false);
                    this.recycle_button6.setSelected(false);
                    this.recycle_button7.setSelected(true);
                    this.recycle_button8.setSelected(false);
                    cursor = getSeqOfCursor("7", "3");
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            setCurRouteInfo(cursor);
                            dispInformation();
                        }
                        cursor.close();
                        return;
                    }
                    return;
                }
                return;
            case C0258R.id.route_type_recycle_button8:
                if (this.CurRoute.getmCurRouteDir().equals("3")) {
                    this.recycle_button0.setSelected(false);
                    this.recycle_button1.setSelected(false);
                    this.recycle_button2.setSelected(false);
                    this.recycle_button3.setSelected(false);
                    this.recycle_button4.setSelected(false);
                    this.recycle_button5.setSelected(false);
                    this.recycle_button6.setSelected(false);
                    this.recycle_button7.setSelected(false);
                    this.recycle_button8.setSelected(true);
                    cursor = getSeqOfCursor("8", "3");
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            setCurRouteInfo(cursor);
                            dispInformation();
                        }
                        cursor.close();
                        return;
                    }
                    return;
                }
                return;
            default:
                return;
        }
        if (this.CurRoute.getmCurRouteDir().equals("3")) {
            this.recycle_button0.setSelected(false);
            this.recycle_button1.setSelected(false);
            this.recycle_button2.setSelected(true);
            this.recycle_button3.setSelected(false);
            this.recycle_button4.setSelected(false);
            this.recycle_button5.setSelected(false);
            this.recycle_button6.setSelected(false);
            this.recycle_button7.setSelected(false);
            this.recycle_button8.setSelected(false);
            cursor = getSeqOfCursor("2", "3");
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    setCurRouteInfo(cursor);
                    dispInformation();
                }
                cursor.close();
            }
        }
    }

    private Cursor getTypeOfCursor(String route_type, String route_dir) {
        String Selection;
        if (DataConst.KEY_ROUTE_REMARK.length() > 0) {
            Selection = "route_no=" + this.CurRoute.getmCurRouteNumber() + " AND " + "route_bus_type" + " = " + this.CurRoute.getmCurRouteBustype() + " AND " + DataConst.KEY_ROUTE_TYPE + " = " + route_type + " AND " + DataConst.KEY_ROUTE_DIR + " = " + route_dir;
        } else {
            Selection = "route_no=" + this.CurRoute.getmCurRouteNumber() + " AND " + "route_bus_type" + " = " + this.CurRoute.getmCurRouteBustype() + " AND " + DataConst.KEY_ROUTE_TYPE + " = " + route_type + " AND " + DataConst.KEY_ROUTE_DIR + " = " + route_dir;
        }
        return getContentResolver().query(DataConst.CONTENT_ROUTE_URI, this.RouteProjection, Selection, null, null);
    }

    private Cursor getSeqOfCursor(String brt_class_seqno, String route_dir) {
        String Selection;
        if (DataConst.KEY_ROUTE_REMARK.length() > 0) {
            Selection = "route_no=" + this.CurRoute.getmCurRouteNumber() + " AND " + "route_bus_type" + " = " + this.CurRoute.getmCurRouteBustype() + " AND " + DataConst.KEY_BRT_CLASS_SEQNO + " = " + brt_class_seqno + " AND " + DataConst.KEY_ROUTE_DIR + " = " + route_dir;
        } else {
            Selection = "route_no=" + this.CurRoute.getmCurRouteNumber() + " AND " + "route_bus_type" + " = " + this.CurRoute.getmCurRouteBustype() + " AND " + DataConst.KEY_BRT_CLASS_SEQNO + " = " + brt_class_seqno + " AND " + DataConst.KEY_ROUTE_DIR + " = " + route_dir;
        }
        return getContentResolver().query(DataConst.CONTENT_ROUTE_URI, this.RouteProjection, Selection, null, null);
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    private Cursor getTypeCursor(String type) {
        return getContentResolver().query(DataConst.CONTENT_ROUTE_URI, this.RouteProjection, "route_no= " + this.CurRoute.getmCurRouteNumber() + " AND " + DataConst.KEY_ROUTE_DIR + "= " + type + " AND " + "route_bus_type" + "=" + this.CurRoute.getmCurRouteBustype(), null, "route_type ASC");
    }

    private Cursor getSeqCursor(String type) {
        return getContentResolver().query(DataConst.CONTENT_ROUTE_URI, this.RouteProjection, "route_no= " + this.CurRoute.getmCurRouteNumber() + " AND " + DataConst.KEY_ROUTE_DIR + "= " + type + " AND " + "route_bus_type" + "=" + this.CurRoute.getmCurRouteBustype(), null, "brt_class_seqno ASC");
    }

    private void setRecycleBtnUI() {
        Cursor dirTypeCursor = getSeqCursor("3");
        String maxRecycleRouteNo = "0";
        if (dirTypeCursor.moveToFirst() && dirTypeCursor.getCount() > 0) {
            maxRecycleRouteNo = String.valueOf(dirTypeCursor.getCount() - 1);
            Log.e("setRecycleBtnUIa: ", maxRecycleRouteNo);
            Cursor seq_Cursor = getContentResolver().query(DataConst.CONTENT_ROUTE_URI, this.RouteProjection, "route_no= " + this.CurRoute.getmCurRouteNumber() + " AND (" + DataConst.KEY_BRT_CLASS_SEQNO + "= 1  OR " + DataConst.KEY_BRT_CLASS_SEQNO + "= 2)", null, "brt_class_seqno ASC");
            if (seq_Cursor == null) {
                Log.e("setRecycleBtnUIb: ", "seq cursor null");
            }
            if (seq_Cursor != null) {
                Log.e("setRecycleBtnUIb: ", seq_Cursor.getCount() + "");
                if (seq_Cursor.getCount() != 0) {
                    seq_Cursor.moveToFirst();
                    if (seq_Cursor.getCount() == 1) {
                        String num = seq_Cursor.getString(seq_Cursor.getColumnIndex(DataConst.KEY_BRT_CLASS_SEQNO));
                        if (num.equals("1")) {
                            this.recycle_button2.setVisibility(4);
                        } else if (num.equals("2")) {
                            this.recycle_button1.setVisibility(4);
                        }
                        maxRecycleRouteNo = String.valueOf(Integer.parseInt(maxRecycleRouteNo) + 1);
                    }
                } else {
                    Log.e("setRecycleBtnUIc: ", seq_Cursor.getCount() + "");
                    this.recycle_button1.setVisibility(4);
                    this.recycle_button2.setVisibility(4);
                    maxRecycleRouteNo = String.valueOf(Integer.parseInt(maxRecycleRouteNo) + 2);
                }
            }
            Log.e("setRecycleBtnUId: ", maxRecycleRouteNo);
            do {
                switch (Integer.valueOf(dirTypeCursor.getString(dirTypeCursor.getColumnIndex(DataConst.KEY_BRT_CLASS_SEQNO))).intValue()) {
                    case 0:
                        this.recycle_button0.setText(dirTypeCursor.getString(dirTypeCursor.getColumnIndex(DataConst.KEY_BRT_APP_WAYPOINT_DESC)));
                        break;
                    case 1:
                        this.recycle_button1.setText(dirTypeCursor.getString(dirTypeCursor.getColumnIndex(DataConst.KEY_BRT_APP_WAYPOINT_DESC)));
                        break;
                    case 2:
                        this.recycle_button2.setText(dirTypeCursor.getString(dirTypeCursor.getColumnIndex(DataConst.KEY_BRT_APP_WAYPOINT_DESC)));
                        break;
                    case 3:
                        this.recycle_button3.setText(dirTypeCursor.getString(dirTypeCursor.getColumnIndex(DataConst.KEY_BRT_APP_WAYPOINT_DESC)));
                        this.recycle_button3.setVisibility(0);
                        break;
                    case 4:
                        this.recycle_button4.setText(dirTypeCursor.getString(dirTypeCursor.getColumnIndex(DataConst.KEY_BRT_APP_WAYPOINT_DESC)));
                        this.recycle_button4.setVisibility(0);
                        break;
                    case 5:
                        this.recycle_button5.setText(dirTypeCursor.getString(dirTypeCursor.getColumnIndex(DataConst.KEY_BRT_APP_WAYPOINT_DESC)));
                        this.recycle_button5.setVisibility(0);
                        break;
                    case 6:
                        this.recycle_button6.setText(dirTypeCursor.getString(dirTypeCursor.getColumnIndex(DataConst.KEY_BRT_APP_WAYPOINT_DESC)));
                        this.recycle_button6.setVisibility(0);
                        break;
                    case 7:
                        this.recycle_button7.setText(dirTypeCursor.getString(dirTypeCursor.getColumnIndex(DataConst.KEY_BRT_APP_WAYPOINT_DESC)));
                        this.recycle_button7.setVisibility(0);
                        break;
                    case 8:
                        this.recycle_button8.setText(dirTypeCursor.getString(dirTypeCursor.getColumnIndex(DataConst.KEY_BRT_APP_WAYPOINT_DESC)));
                        this.recycle_button8.setVisibility(0);
                        break;
                }
            } while (dirTypeCursor.moveToNext());
        }
    }

    private void setDirectionBtnUI() {
        Cursor dirTypeCursor1 = getTypeCursor("1");
        Cursor dirTypeCursor2;
        if (!dirTypeCursor1.moveToFirst() || dirTypeCursor1.getCount() <= 0) {
            dirTypeCursor2 = getTypeCursor("2");
            if (dirTypeCursor2.moveToFirst() || dirTypeCursor2.getCount() <= 0) {
                dirTypeCursor2.close();
                dirTypeCursor1.close();
            }
            do {
                switch (Integer.valueOf(dirTypeCursor2.getString(dirTypeCursor2.getColumnIndex(DataConst.KEY_ROUTE_TYPE))).intValue()) {
                    case 0:
                        this.direction_button5.setText(dirTypeCursor2.getString(dirTypeCursor2.getColumnIndex(DataConst.KEY_BRT_APP_WAYPOINT_DESC)));
                        this.direction_button5.setVisibility(0);
                        break;
                    case 1:
                        this.direction_button6.setText(dirTypeCursor2.getString(dirTypeCursor2.getColumnIndex(DataConst.KEY_BRT_APP_WAYPOINT_DESC)));
                        this.direction_button6.setVisibility(0);
                        break;
                    case 2:
                        this.direction_button7.setText(dirTypeCursor2.getString(dirTypeCursor2.getColumnIndex(DataConst.KEY_BRT_APP_WAYPOINT_DESC)));
                        this.direction_button7.setVisibility(0);
                        break;
                    case 3:
                        this.direction_button8.setText(dirTypeCursor2.getString(dirTypeCursor2.getColumnIndex(DataConst.KEY_BRT_APP_WAYPOINT_DESC)));
                        this.direction_button8.setVisibility(0);
                        break;
                    case 4:
                        this.direction_button9.setText(dirTypeCursor2.getString(dirTypeCursor2.getColumnIndex(DataConst.KEY_BRT_APP_WAYPOINT_DESC)));
                        this.direction_button9.setVisibility(0);
                        break;
                }
            } while (dirTypeCursor2.moveToNext());
            dirTypeCursor2.close();
            dirTypeCursor1.close();
        }
        do {
            switch (Integer.valueOf(dirTypeCursor1.getString(dirTypeCursor1.getColumnIndex(DataConst.KEY_ROUTE_TYPE))).intValue()) {
                case 0:
                    this.direction_button0.setText(dirTypeCursor1.getString(dirTypeCursor1.getColumnIndex(DataConst.KEY_BRT_APP_WAYPOINT_DESC)));
                    this.direction_button0.setVisibility(0);
                    break;
                case 1:
                    this.direction_button1.setText(dirTypeCursor1.getString(dirTypeCursor1.getColumnIndex(DataConst.KEY_BRT_APP_WAYPOINT_DESC)));
                    this.direction_button1.setVisibility(0);
                    break;
                case 2:
                    this.direction_button2.setText(dirTypeCursor1.getString(dirTypeCursor1.getColumnIndex(DataConst.KEY_BRT_APP_WAYPOINT_DESC)));
                    this.direction_button2.setVisibility(0);
                    break;
                case 3:
                    this.direction_button3.setText(dirTypeCursor1.getString(dirTypeCursor1.getColumnIndex(DataConst.KEY_BRT_APP_WAYPOINT_DESC)));
                    this.direction_button3.setVisibility(0);
                    break;
                case 4:
                    this.direction_button4.setText(dirTypeCursor1.getString(dirTypeCursor1.getColumnIndex(DataConst.KEY_BRT_APP_WAYPOINT_DESC)));
                    this.direction_button4.setVisibility(0);
                    break;
            }
        } while (dirTypeCursor1.moveToNext());
        dirTypeCursor2 = getTypeCursor("2");
        if (dirTypeCursor2.moveToFirst()) {
        }
        dirTypeCursor2.close();
        dirTypeCursor1.close();
    }
}
