package com.neighbor.ulsanbus.ui;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.neighbor.ulsanbus.C0258R;
import com.neighbor.ulsanbus.Timetable;
import com.neighbor.ulsanbus.data.DataConst;
import com.neighbor.ulsanbus.data.DataSAXParser;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

public class UiTimetable extends Activity {
    private static final int RESULT_DATA_OK = 0;
    private static final int RESULT_SERVER_ERROR = 1;
    private static final int USER_CANCEL = 255;
    private static String mMatchFlag = "0";
    private timetableAdapter adapter;
    private ArrayList<Timetable> arrayListTimetable;
    private boolean bEmergency;
    private Handler handler = new C03291();
    private ProgressBar mProgressbar;
    private String mRouteBusType;
    private String mRouteID;
    private String mRouteNumber;
    private String mRouteTstopName;
    private DataSAXParser mSaxHelper;
    private String mSchduleMode;
    private ListView mTimeTableList;
    private TreeMap<Integer, ArrayList<String>> mapRemark;
    private FrameLayout mbackgroundImage;
    private SharedPreferences prefs;
    private TimetableRequest scheduleReq;
    private HashMap<String, TreeMap<Integer, ArrayList<String>>> timeTable;

    /* renamed from: com.neighbor.ulsanbus.ui.UiTimetable$1 */
    class C03291 extends Handler {
        C03291() {
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            UiTimetable.this.mProgressbar.setVisibility(8);
            switch (msg.what) {
                case 0:
                    UiTimetable.this.show_TimeTable();
                    return;
                default:
                    UiTimetable.this.showError();
                    return;
            }
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiTimetable$2 */
    class C03302 implements OnClickListener {
        C03302() {
        }

        public void onClick(DialogInterface dialog, int which) {
            UiTimetable.this.finish();
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiTimetable$3 */
    class C03313 implements OnClickListener {
        C03313() {
        }

        public void onClick(DialogInterface dialog, int which) {
            UiTimetable.this.finish();
            dialog.dismiss();
        }
    }

    class TimetableRequest extends AsyncTask<Void, Integer, Integer> {
        private ArrayList<Timetable> mArrayListTimetable;
        private String mRouteId;
        private String mScheduleMode;
        private HashMap<String, TreeMap<Integer, ArrayList<String>>> mTimeTable;
        private int responseCode = 0;

        public TimetableRequest(String routeid, String mode) {
            this.mRouteId = routeid;
            this.mTimeTable = new HashMap();
            this.mArrayListTimetable = new ArrayList();
            this.mScheduleMode = mode;
        }

        protected Integer doInBackground(Void... arg0) {
            Log.e("doInBackground: ", this.mRouteId);
            try {
                if (this.mScheduleMode.equals("0")) {
                    UiTimetable.this.mSaxHelper.getName(DataConst.API_ROUTEDETAILALLOCATIONINFO4 + this.mRouteId, DataConst.FLAG_ROUTETIMETABLE);
                } else {
                    UiTimetable.this.mSaxHelper.getName(DataConst.API_ROUTE_TIMETABLE + this.mRouteId + "&dayinfo=" + String.valueOf(Integer.parseInt(this.mScheduleMode) - 1), DataConst.FLAG_DAY_ROUTE_SCHEDULE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                this.responseCode = 1;
            }
            return Integer.valueOf(this.responseCode);
        }

        protected void onCancelled() {
            super.onCancelled();
        }

        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result.intValue() != 0) {
                UiTimetable.this.handler.sendEmptyMessage(result.intValue());
                return;
            }
            try {
                if (this.mScheduleMode.equals("0")) {
                    this.mTimeTable = UiTimetable.this.mSaxHelper.getRouteTimetable();
                    this.mArrayListTimetable = UiTimetable.this.mSaxHelper.getmArrayListTimetable();
                } else {
                    this.mTimeTable = UiTimetable.this.mSaxHelper.getDayRouteTimetable();
                    this.mArrayListTimetable = UiTimetable.this.mSaxHelper.getmArrayListTimetable();
                    UiTimetable.mMatchFlag = UiTimetable.this.mSaxHelper.getDayRouteMach();
                }
                if (this.mTimeTable != null) {
                    Log.d("UiTimetable", "mTimeTable!= null");
                    if (this.mTimeTable.size() <= 0) {
                        Log.d("UiTimetable", "mTimeTable.size() <= 0");
                        UiTimetable.this.handler.sendEmptyMessage(1);
                        return;
                    }
                    if (UiTimetable.this.timeTable == null) {
                        UiTimetable.this.timeTable = new HashMap();
                    }
                    UiTimetable.this.timeTable = this.mTimeTable;
                    UiTimetable.this.arrayListTimetable = this.mArrayListTimetable;
                    UiTimetable.this.handler.sendEmptyMessage(0);
                    return;
                }
                Log.d("UiTimetable", "mTimeTable == null");
                UiTimetable.this.handler.sendEmptyMessage(1);
            } catch (Exception e) {
                UiTimetable.this.handler.sendEmptyMessage(1);
            }
        }
    }

    class timetableAdapter extends ArrayAdapter {
        private ArrayList<String> HourList;
        private Context mContext;
        private TreeMap<Integer, ArrayList<String>> remarkTable;
        private TreeMap<Integer, ArrayList<String>> timeTable;

        public timetableAdapter(Context context, int resource, int textViewResourceId, List objects) {
            super(context, resource, textViewResourceId, objects);
            this.mContext = context;
            this.HourList = (ArrayList) objects;
        }

        public void registTimeTable(TreeMap<Integer, ArrayList<String>> table, TreeMap<Integer, ArrayList<String>> remark) {
            this.timeTable = table;
            this.remarkTable = remark;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public android.view.View getView(int r38, android.view.View r39, android.view.ViewGroup r40) {
            /*
            r37 = this;
            r32 = r39;
            if (r39 != 0) goto L_0x0023;
        L_0x0004:
            r0 = r37;
            r0 = r0.mContext;
            r33 = r0;
            r34 = "layout_inflater";
            r16 = r33.getSystemService(r34);
            r16 = (android.view.LayoutInflater) r16;
            r33 = 2131361884; // 0x7f0a005c float:1.8343533E38 double:1.0530326857E-314;
            r34 = 0;
            r0 = r16;
            r1 = r33;
            r2 = r40;
            r3 = r34;
            r32 = r0.inflate(r1, r2, r3);
        L_0x0023:
            r0 = r37;
            r0 = r0.HourList;
            r33 = r0;
            r0 = r33;
            r1 = r38;
            r14 = r0.get(r1);
            r14 = (java.lang.String) r14;
            r33 = "getView: ";
            r0 = r33;
            android.util.Log.e(r0, r14);
            r17 = java.lang.Integer.parseInt(r14);
            r33 = 2131230756; // 0x7f080024 float:1.8077574E38 double:1.0529679E-314;
            r29 = r32.findViewById(r33);
            r29 = (android.widget.TextView) r29;
            r33 = 8;
            r0 = r29;
            r1 = r33;
            r0.setVisibility(r1);
            r33 = 2131230947; // 0x7f0800e3 float:1.8077961E38 double:1.0529679943E-314;
            r31 = r32.findViewById(r33);
            r31 = (android.widget.TextView) r31;
            r33 = 8;
            r0 = r31;
            r1 = r33;
            r0.setVisibility(r1);
            r33 = 2131230946; // 0x7f0800e2 float:1.807796E38 double:1.052967994E-314;
            r30 = r32.findViewById(r33);
            r30 = (android.widget.TextView) r30;
            r33 = 8;
            r0 = r30;
            r1 = r33;
            r0.setVisibility(r1);
            r33 = 2131230755; // 0x7f080023 float:1.8077572E38 double:1.0529678994E-314;
            r28 = r32.findViewById(r33);
            r28 = (android.widget.TextView) r28;
            r33 = 8;
            r0 = r28;
            r1 = r33;
            r0.setVisibility(r1);
            r10 = com.neighbor.ulsanbus.util.UtilTimeManager.getCurHourMin();
            r33 = 0;
            r34 = 2;
            r0 = r33;
            r1 = r34;
            r33 = r10.substring(r0, r1);
            r11 = java.lang.Integer.parseInt(r33);
            r33 = "curhour";
            r34 = new java.lang.StringBuilder;
            r34.<init>();
            r0 = r34;
            r34 = r0.append(r11);
            r35 = "";
            r34 = r34.append(r35);
            r34 = r34.toString();
            android.util.Log.e(r33, r34);
            r33 = 12;
            r0 = r17;
            r1 = r33;
            if (r0 < r1) goto L_0x0272;
        L_0x00bc:
            r33 = 12;
            r0 = r17;
            r1 = r33;
            if (r0 <= r1) goto L_0x00c6;
        L_0x00c4:
            r17 = r17 + -12;
        L_0x00c6:
            r33 = 0;
            r0 = r31;
            r1 = r33;
            r0.setVisibility(r1);
            r33 = 0;
            r0 = r30;
            r1 = r33;
            r0.setVisibility(r1);
            r33 = "%02d";
            r34 = 1;
            r0 = r34;
            r0 = new java.lang.Object[r0];
            r34 = r0;
            r35 = 0;
            r36 = java.lang.Integer.valueOf(r17);
            r34[r35] = r36;
            r33 = java.lang.String.format(r33, r34);
            r0 = r30;
            r1 = r33;
            r0.setText(r1);
        L_0x00f5:
            r7 = java.lang.Integer.parseInt(r14);
            r33 = "#6F91B4";
            r33 = android.graphics.Color.parseColor(r33);
            r0 = r29;
            r1 = r33;
            r0.setTextColor(r1);
            r33 = "#6F91B4";
            r33 = android.graphics.Color.parseColor(r33);
            r0 = r28;
            r1 = r33;
            r0.setTextColor(r1);
            r33 = "#6F91B4";
            r33 = android.graphics.Color.parseColor(r33);
            r0 = r31;
            r1 = r33;
            r0.setTextColor(r1);
            r33 = "#6F91B4";
            r33 = android.graphics.Color.parseColor(r33);
            r0 = r30;
            r1 = r33;
            r0.setTextColor(r1);
            r33 = com.neighbor.ulsanbus.ui.UiTimetable.mMatchFlag;
            r34 = "1";
            r33 = r33.equals(r34);
            if (r33 == 0) goto L_0x014d;
        L_0x0139:
            r33 = 12;
            r0 = r33;
            if (r7 <= r0) goto L_0x02a3;
        L_0x013f:
            r33 = 12;
            r0 = r33;
            if (r11 <= r0) goto L_0x02a3;
        L_0x0145:
            r23 = r7 + -12;
            r8 = r11 + -12;
            r0 = r23;
            if (r0 != r8) goto L_0x014d;
        L_0x014d:
            r33 = 2131231115; // 0x7f08018b float:1.8078302E38 double:1.0529680773E-314;
            r13 = r32.findViewById(r33);
            r13 = (android.widget.LinearLayout) r13;
            r13.removeAllViews();
            r33 = 2;
            r0 = r33;
            r33 = r10.substring(r0);
            r9 = java.lang.Integer.parseInt(r33);
            r24 = new java.util.ArrayList;
            r24.<init>();
            r0 = r37;
            r0 = r0.timeTable;
            r33 = r0;
            r34 = java.lang.Integer.parseInt(r14);
            r34 = java.lang.Integer.valueOf(r34);
            r24 = r33.get(r34);
            r24 = (java.util.ArrayList) r24;
            r15 = 0;
        L_0x017f:
            r33 = r24.size();
            r33 = r33 / 5;
            r33 = r33 + 1;
            r0 = r33;
            if (r15 >= r0) goto L_0x02c9;
        L_0x018b:
            r5 = new android.widget.LinearLayout;
            r33 = r37.getContext();
            r0 = r33;
            r5.<init>(r0);
            r33 = 0;
            r0 = r33;
            r5.setOrientation(r0);
            r33 = new android.widget.LinearLayout$LayoutParams;
            r34 = -1;
            r35 = -2;
            r33.<init>(r34, r35);
            r0 = r33;
            r5.setLayoutParams(r0);
            r19 = 5;
            r33 = r24.size();
            r33 = r33 / 5;
            r0 = r33;
            if (r15 != r0) goto L_0x01bd;
        L_0x01b7:
            r33 = r24.size();
            r19 = r33 % 5;
        L_0x01bd:
            r18 = 0;
        L_0x01bf:
            r0 = r18;
            r1 = r19;
            if (r0 >= r1) goto L_0x02c2;
        L_0x01c5:
            r27 = new android.widget.TextView;
            r0 = r37;
            r0 = com.neighbor.ulsanbus.ui.UiTimetable.this;
            r33 = r0;
            r0 = r27;
            r1 = r33;
            r0.<init>(r1);
            r20 = new android.widget.LinearLayout$LayoutParams;
            r33 = -2;
            r34 = -2;
            r0 = r20;
            r1 = r33;
            r2 = r34;
            r0.<init>(r1, r2);
            r33 = 30;
            r34 = 0;
            r35 = 50;
            r36 = 0;
            r0 = r20;
            r1 = r33;
            r2 = r34;
            r3 = r35;
            r4 = r36;
            r0.setMargins(r1, r2, r3, r4);
            r0 = r27;
            r1 = r20;
            r0.setLayoutParams(r1);
            r33 = java.lang.Integer.parseInt(r14);
            r0 = r33;
            if (r11 < r0) goto L_0x02a7;
        L_0x0207:
            r33 = com.neighbor.ulsanbus.ui.UiTimetable.mMatchFlag;
            r34 = "1";
            r33 = r33.equals(r34);
            if (r33 == 0) goto L_0x02a7;
        L_0x0213:
            r33 = "#666666";
            r33 = android.graphics.Color.parseColor(r33);
            r0 = r27;
            r1 = r33;
            r0.setTextColor(r1);
            r33 = 0;
            r34 = 0;
            r0 = r27;
            r1 = r33;
            r2 = r34;
            r0.setTypeface(r1, r2);
            r33 = java.lang.Integer.parseInt(r14);
            r0 = r33;
            if (r11 != r0) goto L_0x024b;
        L_0x0235:
            r33 = r15 * 5;
            r33 = r33 + r18;
            r0 = r24;
            r1 = r33;
            r33 = r0.get(r1);
            r33 = (java.lang.String) r33;
            r33 = java.lang.Integer.parseInt(r33);
            r0 = r33;
            if (r9 >= r0) goto L_0x024b;
        L_0x024b:
            r33 = 1101004800; // 0x41a00000 float:20.0 double:5.439686476E-315;
            r0 = r27;
            r1 = r33;
            r0.setTextSize(r1);
            r33 = r15 * 5;
            r33 = r33 + r18;
            r0 = r24;
            r1 = r33;
            r33 = r0.get(r1);
            r33 = (java.lang.CharSequence) r33;
            r0 = r27;
            r1 = r33;
            r0.setText(r1);
            r0 = r27;
            r5.addView(r0);
            r18 = r18 + 1;
            goto L_0x01bf;
        L_0x0272:
            r33 = 0;
            r0 = r29;
            r1 = r33;
            r0.setVisibility(r1);
            r33 = 0;
            r0 = r28;
            r1 = r33;
            r0.setVisibility(r1);
            r33 = "%02d";
            r34 = 1;
            r0 = r34;
            r0 = new java.lang.Object[r0];
            r34 = r0;
            r35 = 0;
            r36 = java.lang.Integer.valueOf(r17);
            r34[r35] = r36;
            r33 = java.lang.String.format(r33, r34);
            r0 = r28;
            r1 = r33;
            r0.setText(r1);
            goto L_0x00f5;
        L_0x02a3:
            if (r7 != r11) goto L_0x014d;
        L_0x02a5:
            goto L_0x014d;
        L_0x02a7:
            r33 = "#666666";
            r33 = android.graphics.Color.parseColor(r33);
            r0 = r27;
            r1 = r33;
            r0.setTextColor(r1);
            r33 = 0;
            r34 = 0;
            r0 = r27;
            r1 = r33;
            r2 = r34;
            r0.setTypeface(r1, r2);
            goto L_0x024b;
        L_0x02c2:
            r13.addView(r5);
            r15 = r15 + 1;
            goto L_0x017f;
        L_0x02c9:
            r33 = 2131231116; // 0x7f08018c float:1.8078304E38 double:1.052968078E-314;
            r12 = r32.findViewById(r33);
            r12 = (android.widget.LinearLayout) r12;
            r12.removeAllViews();
            r22 = new java.util.ArrayList;
            r22.<init>();
            r0 = r37;
            r0 = r0.remarkTable;
            r33 = r0;
            r34 = java.lang.Integer.parseInt(r14);
            r34 = java.lang.Integer.valueOf(r34);
            r22 = r33.get(r34);
            r22 = (java.util.ArrayList) r22;
            if (r22 == 0) goto L_0x035b;
        L_0x02f0:
            r33 = r22.iterator();
        L_0x02f4:
            r34 = r33.hasNext();
            if (r34 == 0) goto L_0x035b;
        L_0x02fa:
            r21 = r33.next();
            r21 = (java.lang.String) r21;
            r25 = new android.widget.TextView;
            r0 = r37;
            r0 = com.neighbor.ulsanbus.ui.UiTimetable.this;
            r34 = r0;
            r0 = r25;
            r1 = r34;
            r0.<init>(r1);
            r34 = new android.widget.LinearLayout$LayoutParams;
            r35 = -2;
            r36 = -2;
            r34.<init>(r35, r36);
            r0 = r25;
            r1 = r34;
            r0.setLayoutParams(r1);
            r34 = "#666666";
            r34 = android.graphics.Color.parseColor(r34);
            r0 = r25;
            r1 = r34;
            r0.setTextColor(r1);
            r34 = 0;
            r35 = 0;
            r0 = r25;
            r1 = r34;
            r2 = r35;
            r0.setTypeface(r1, r2);
            r34 = 1097859072; // 0x41700000 float:15.0 double:5.424144515E-315;
            r0 = r25;
            r1 = r34;
            r0.setTextSize(r1);
            if (r21 == 0) goto L_0x0351;
        L_0x0344:
            r0 = r25;
            r1 = r21;
            r0.setText(r1);
        L_0x034b:
            r0 = r25;
            r12.addView(r0);
            goto L_0x02f4;
        L_0x0351:
            r34 = "";
            r0 = r25;
            r1 = r34;
            r0.setText(r1);
            goto L_0x034b;
        L_0x035b:
            r33 = com.neighbor.ulsanbus.ui.UiTimetable.mMatchFlag;
            r34 = "1";
            r33 = r33.equals(r34);
            if (r33 == 0) goto L_0x03bb;
        L_0x0367:
            r33 = 2131230878; // 0x7f08009e float:1.8077821E38 double:1.05296796E-314;
            r6 = r32.findViewById(r33);
            r6 = (android.widget.ImageView) r6;
            r33 = 2131231117; // 0x7f08018d float:1.8078306E38 double:1.0529680783E-314;
            r26 = r32.findViewById(r33);
            r26 = (android.widget.TextView) r26;
            r33 = "0";
            r0 = r33;
            r33 = r14.equals(r0);
            if (r33 == 0) goto L_0x0395;
        L_0x0383:
            r0 = r37;
            r0 = com.neighbor.ulsanbus.ui.UiTimetable.this;
            r33 = r0;
            r33 = r33.isLimousine();
            if (r33 == 0) goto L_0x0395;
        L_0x038f:
            if (r11 != 0) goto L_0x0393;
        L_0x0391:
            r11 = 24;
        L_0x0393:
            r14 = "24";
        L_0x0395:
            r33 = java.lang.Integer.parseInt(r14);
            r0 = r33;
            if (r11 <= r0) goto L_0x03bc;
        L_0x039d:
            r33 = 2131165497; // 0x7f070139 float:1.7945213E38 double:1.0529356577E-314;
            r0 = r33;
            r6.setImageResource(r0);
            r33 = "운행종료";
            r0 = r26;
            r1 = r33;
            r0.setText(r1);
            r33 = "#8FB4CD";
            r33 = android.graphics.Color.parseColor(r33);
            r0 = r26;
            r1 = r33;
            r0.setTextColor(r1);
        L_0x03bb:
            return r32;
        L_0x03bc:
            r33 = java.lang.Integer.parseInt(r14);
            r0 = r33;
            if (r11 != r0) goto L_0x03e3;
        L_0x03c4:
            r33 = 2131165496; // 0x7f070138 float:1.794521E38 double:1.052935657E-314;
            r0 = r33;
            r6.setImageResource(r0);
            r33 = "운행";
            r0 = r26;
            r1 = r33;
            r0.setText(r1);
            r33 = "#8FB4CD";
            r33 = android.graphics.Color.parseColor(r33);
            r0 = r26;
            r1 = r33;
            r0.setTextColor(r1);
            goto L_0x03bb;
        L_0x03e3:
            r33 = 2131165498; // 0x7f07013a float:1.7945215E38 double:1.052935658E-314;
            r0 = r33;
            r6.setImageResource(r0);
            r33 = "운행예정";
            r0 = r26;
            r1 = r33;
            r0.setText(r1);
            r33 = "#AAAAAA";
            r33 = android.graphics.Color.parseColor(r33);
            r0 = r26;
            r1 = r33;
            r0.setTextColor(r1);
            goto L_0x03bb;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.neighbor.ulsanbus.ui.UiTimetable.timetableAdapter.getView(int, android.view.View, android.view.ViewGroup):android.view.View");
        }
    }

    private void init() {
        String[] colorArray = getResources().getStringArray(C0258R.array.bus_type_color);
        String[] busTypeArray = getResources().getStringArray(C0258R.array.stop_info_header_type);
        int valueOfBusType = Integer.parseInt(this.mRouteBusType);
        this.mbackgroundImage = (FrameLayout) findViewById(C0258R.id.route_timetable_background);
        if (this.bEmergency) {
            this.mbackgroundImage.setBackgroundResource(C0258R.drawable.background_emer);
        } else {
            this.mbackgroundImage.setBackgroundResource(C0258R.drawable.background_nomal);
        }
        ((RelativeLayout) findViewById(C0258R.id.route_timetable_layout)).setBackgroundColor(Color.parseColor(colorArray[valueOfBusType]));
        ((TextView) findViewById(C0258R.id.route_timetable_route_type)).setText(busTypeArray[valueOfBusType]);
        TextView txbusNo = (TextView) findViewById(C0258R.id.route_timetable_stop_number);
        StringBuffer sb = new StringBuffer();
        sb.append(this.mRouteNumber);
        sb.append(" ( ");
        sb.append(this.mRouteTstopName);
        sb.append(" 방면");
        sb.append(" ) ");
        txbusNo.setText(sb.toString());
        TextView txdate = (TextView) findViewById(C0258R.id.route_timetable_stop_date);
        Calendar calendar = Calendar.getInstance();
        StringBuffer sbDate = new StringBuffer();
        sbDate.append("(");
        int date = calendar.get(7);
        if (date == 1) {
            sbDate.append(getResources().getString(C0258R.string.route_information_station_time_holidays));
        } else if (date == 7) {
            sbDate.append(getResources().getString(C0258R.string.route_information_station_time_semi_holiday));
        } else {
            sbDate.append(getResources().getString(C0258R.string.route_information_station_time_weekdays));
        }
        sbDate.append(")");
        this.timeTable = new HashMap();
        this.mapRemark = new TreeMap();
        this.arrayListTimetable = new ArrayList();
        this.mTimeTableList = (ListView) findViewById(C0258R.id.route_timetable_list);
        this.mProgressbar = (ProgressBar) findViewById(C0258R.id.route_timetable_progress);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.prefs = getSharedPreferences("PreStatus", 0);
        this.bEmergency = this.prefs.getBoolean(DataConst.KEY_PREF_EMER_MODE, false);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.mSchduleMode = extras.getString("schedule_mode");
            this.mRouteID = extras.getString("route_id");
            this.mRouteNumber = extras.getString("route_no");
            this.mRouteBusType = extras.getString("route_bus_type");
            this.mRouteTstopName = extras.getString("route_bus_tstop");
        }
        Log.d("UiTimetable", "[[ onCreate ]] ");
        setContentView(C0258R.layout.timetable);
    }

    protected void onStart() {
        super.onStart();
        Log.d("UiTimetable", "[[ onStart ]] ");
        init();
        this.mSaxHelper = new DataSAXParser();
        this.scheduleReq = new TimetableRequest(this.mRouteID, this.mSchduleMode);
        this.scheduleReq.execute(new Void[0]);
    }

    private void showError() {
        new Builder(getParent().getParent()).setTitle(getString(C0258R.string.error)).setMessage(getString(C0258R.string.request_dialog_fail)).setNeutralButton(getString(C0258R.string.ok), new C03302()).show();
    }

    void showEmptyData() {
        new Builder(getParent().getParent()).setTitle(" 경고 ").setMessage("배차 정보가 존재하지 않습니다.").setNeutralButton(getString(C0258R.string.ok), new C03313()).show();
    }

    private void show_TimeTable() {
        int date = Calendar.getInstance().get(7);
        TreeMap<Integer, ArrayList<String>> time = null;
        if (this.mapRemark != null) {
            this.mapRemark.clear();
        }
        if (!this.mSchduleMode.equals("0")) {
            try {
                time = (TreeMap) this.timeTable.get("schedule");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (date == 1) {
            try {
                time = (TreeMap) this.timeTable.get("ws_time");
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        } else if (date == 7 || date == 6) {
            try {
                time = (TreeMap) this.timeTable.get("we_time");
            } catch (Exception e22) {
                e22.printStackTrace();
            }
        } else {
            try {
                time = (TreeMap) this.timeTable.get("wd_time");
            } catch (Exception e222) {
                e222.printStackTrace();
            }
        }
        if (time != null) {
            String oldKey = null;
            ArrayList<String> mRemark = null;
            for (int i = 0; i < this.arrayListTimetable.size(); i++) {
                Timetable cTimetable = (Timetable) this.arrayListTimetable.get(i);
                String sTime = cTimetable.getTodayTime();
                String sRemark = cTimetable.getRemark();
                String strKey = sTime.substring(0, 2);
                if (oldKey == null) {
                    oldKey = strKey;
                }
                if (mRemark == null) {
                    mRemark = new ArrayList();
                }
                ArrayList item;
                Iterator it;
                if (oldKey.compareTo(strKey) == 0) {
                    mRemark.add(sRemark);
                    item = new ArrayList();
                    it = mRemark.iterator();
                    while (it.hasNext()) {
                        item.add((String) it.next());
                    }
                    this.mapRemark.put(Integer.valueOf(Integer.parseInt(oldKey)), item);
                } else {
                    item = new ArrayList();
                    it = mRemark.iterator();
                    while (it.hasNext()) {
                        item.add((String) it.next());
                    }
                    this.mapRemark.put(Integer.valueOf(Integer.parseInt(oldKey)), item);
                    mRemark.clear();
                    mRemark.add(sRemark);
                    oldKey = strKey;
                    if (this.arrayListTimetable.size() - 1 == i) {
                        ArrayList itemL = new ArrayList();
                        it = mRemark.iterator();
                        while (it.hasNext()) {
                            itemL.add((String) it.next());
                        }
                        this.mapRemark.put(Integer.valueOf(Integer.parseInt(oldKey)), itemL);
                    }
                }
            }
            ArrayList<String> tableHour = new ArrayList();
            if (!(this.adapter == null || this.adapter.isEmpty())) {
                this.adapter.clear();
            }
            for (Integer intValue : time.keySet()) {
                tableHour.add(new Integer(intValue.intValue()).toString());
            }
            if (tableHour != null && tableHour.size() != 0 && ((String) tableHour.get(0)).equals("0") && isLimousine()) {
                tableHour.remove(0);
                tableHour.add("0");
            }
            this.adapter = new timetableAdapter(this, 0, 0, tableHour);
            this.adapter.registTimeTable(time, this.mapRemark);
            this.mTimeTableList.setAdapter(this.adapter);
            return;
        }
        showError();
    }

    private boolean isLimousine() {
        if (this.mRouteNumber == null || (!this.mRouteNumber.equals("5001") && !this.mRouteNumber.equals("5002") && !this.mRouteNumber.equals("5003") && !this.mRouteNumber.equals("5004") && !this.mRouteNumber.equals("5005"))) {
            return false;
        }
        return true;
    }

    protected void onStop() {
        super.onStop();
        if (this.scheduleReq.getStatus() != Status.FINISHED) {
            this.scheduleReq.cancel(true);
        }
    }
}
