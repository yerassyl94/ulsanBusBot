package com.neighbor.ulsanbus.ui;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.neighbor.ulsanbus.AllBusArrival;
import com.neighbor.ulsanbus.AllBusArrival.ArrivedBusInfo;
import com.neighbor.ulsanbus.C0258R;
import com.neighbor.ulsanbus.data.DataConst;
import com.neighbor.ulsanbus.data.DataSAXParser;
import com.neighbor.ulsanbus.util.UtilNetworkConnection;
import com.neighbor.ulsanbus.util.UtilTimeManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class UiStopInformation extends Activity implements OnItemClickListener {
    private static final int RESULT_DATA_EMPTY = 2;
    private static final int RESULT_DATA_OK = 0;
    private static final int RESULT_SERVER_ERROR = 1;
    private String TAG = ("[Ulsanbus]" + getClass().getSimpleName());
    private SeparatedListAdapter adapter;
    private boolean bEmergency;
    private Handler handler;
    private InputMethodManager imm;
    private RelativeLayout input_background;
    private boolean isArrivalBus = true;
    private Button mAddFavorite;
    private AllBusArrival mAllBusArrivalList;
    private ImageView mBgImage;
    private Button mBusStationMap;
    private String mBusStopId;
    private String mBusStopName;
    private ProgressBar mProgressBar;
    private Button mRefresh;
    private ArrivalBusRequest mRequestTask;
    private DataSAXParser mSaxHelper;
    private TextView mStopIdTv;
    private ListView mStopListView;
    private TextView mStopNameTv;
    private String mStopRemark;
    private Button mSwitchArrivalMode;
    private Button mTransferEndSet;
    private Button mTransferStartSet;
    private SharedPreferences prefs;

    /* renamed from: com.neighbor.ulsanbus.ui.UiStopInformation$1 */
    class C03211 implements OnClickListener {
        C03211() {
        }

        public void onClick(View v) {
            UiStopInformation.this.showSelectDialog(UiStopInformation.this.mBusStopId, UiStopInformation.this.mBusStopName, false);
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiStopInformation$2 */
    class C03222 implements OnClickListener {
        C03222() {
        }

        public void onClick(View v) {
            UiStopInformation.this.showSelectDialog(UiStopInformation.this.mBusStopId, UiStopInformation.this.mBusStopName, true);
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiStopInformation$3 */
    class C03233 implements OnClickListener {
        C03233() {
        }

        public void onClick(View arg0) {
            if (UiStopInformation.this.isArrivalBus) {
                UiStopInformation.this.isArrivalBus = false;
                UiStopInformation.this.mSwitchArrivalMode.setBackgroundResource(C0258R.drawable.btn_icon_switch_time);
            } else {
                UiStopInformation.this.isArrivalBus = true;
                UiStopInformation.this.mSwitchArrivalMode.setBackgroundResource(C0258R.drawable.btn_icon_switch_bus);
            }
            if (UiStopInformation.this.mRequestTask.getStatus() == Status.FINISHED) {
                UiStopInformation.this.mRequestTask = new ArrivalBusRequest(UiStopInformation.this.mBusStopId, UiStopInformation.this.isArrivalBus);
                UiStopInformation.this.mRequestTask.execute(new Void[0]);
                UiStopInformation.this.mProgressBar.setVisibility(0);
            }
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiStopInformation$4 */
    class C03244 implements OnClickListener {
        C03244() {
        }

        public void onClick(View arg0) {
            Intent intent = new Intent(UiStopInformation.this, UiBusStopPosition.class);
            intent.putExtra("stop_name", UiStopInformation.this.mBusStopName);
            intent.putExtra("stop_id", UiStopInformation.this.mBusStopId);
            switch (UiMainTab.mTabHost.getCurrentTab()) {
                case 1:
                    TabFirst.TAB1.replaceActivity("stopInformation", intent, UiStopInformation.this);
                    return;
                case 2:
                    TabSecond.TAB2.replaceActivity("stopInformation", intent, UiStopInformation.this);
                    return;
                case 3:
                    TabThird.TAB3.replaceActivity("stopInformation", intent, UiStopInformation.this);
                    return;
                case 4:
                    TabFourth.TAB4.replaceActivity("stopInformation", intent, UiStopInformation.this);
                    return;
                case 5:
                    TabFiveth.TAB5.replaceActivity("stopInformation", intent, UiStopInformation.this);
                    return;
                default:
                    return;
            }
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiStopInformation$5 */
    class C03255 implements OnClickListener {
        C03255() {
        }

        public void onClick(View v) {
            if (UiStopInformation.this.getContentResolver().query(DataConst.CONTENT_FAVORITES_URI, new String[]{DataConst.KEY_NOTICE_ID, DataConst.KEY_FAVORITE_CATEGORY, "favor_stopid"}, "favor_stopid=? and category=?", new String[]{UiStopInformation.this.mBusStopId, "2"}, null).getCount() == 0) {
                ContentValues value = new ContentValues();
                value.put(DataConst.KEY_FAVORITE_CATEGORY, "2");
                value.put("favor_routeid", "");
                value.put("favor_stopid", UiStopInformation.this.mBusStopId);
                value.put(DataConst.KEY_FAVORITE_ALARM, "");
                UiStopInformation.this.getContentResolver().insert(DataConst.CONTENT_FAVORITES_URI, value);
                Toast.makeText(UiStopInformation.this.getApplicationContext(), "즐겨찾기에 추가되었습니다", 0).show();
                return;
            }
            Toast.makeText(UiStopInformation.this.getApplicationContext(), "이미 등록된 정류장 입니다. ", 0).show();
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiStopInformation$6 */
    class C03266 implements OnClickListener {
        C03266() {
        }

        public void onClick(View v) {
            if (UiStopInformation.this.mRequestTask.getStatus() == Status.FINISHED) {
                UiStopInformation.this.mRequestTask = new ArrivalBusRequest(UiStopInformation.this.mBusStopId, UiStopInformation.this.isArrivalBus);
                UiStopInformation.this.mRequestTask.execute(new Void[0]);
                UiStopInformation.this.mProgressBar.setVisibility(0);
            }
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiStopInformation$7 */
    class C03277 extends Handler {
        C03277() {
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            UiStopInformation.this.mProgressBar.setVisibility(8);
            Log.d(UiStopInformation.this.TAG, "Error :" + msg.what);
            if (msg.what == 0) {
                UiStopInformation.this.showInfo();
            } else {
                UiStopInformation.this.showError();
            }
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiStopInformation$8 */
    class C03288 implements DialogInterface.OnClickListener {
        C03288() {
        }

        public void onClick(DialogInterface dialog, int which) {
            UiStopInformation.this.finish();
        }
    }

    class ArrivalBusRequest extends AsyncTask<Void, Integer, Integer> {
        private AllBusArrival mBusList;
        private boolean mOrder;
        private String mStopId;
        private int responseCode = 0;

        public ArrivalBusRequest(String stopId, boolean order) {
            this.mStopId = stopId;
            this.mOrder = order;
            this.mBusList = new AllBusArrival();
        }

        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result.intValue() != 0) {
                UiStopInformation.this.handler.sendEmptyMessage(result.intValue());
                return;
            }
            try {
                this.mBusList = UiStopInformation.this.mSaxHelper.getAllBusArrivalList();
                if (!this.mBusList.getErrorMsg().equals("OK")) {
                    UiStopInformation.this.handler.sendEmptyMessage(1);
                } else if (this.mBusList.getAllBusArrivalInfoList().size() > 0) {
                    UiStopInformation.this.mAllBusArrivalList = this.mBusList;
                    UiStopInformation.this.handler.sendEmptyMessage(0);
                } else {
                    UiStopInformation.this.handler.sendEmptyMessage(2);
                }
            } catch (Exception e) {
                UiStopInformation.this.handler.sendEmptyMessage(1);
            }
        }

        protected Integer doInBackground(Void... arg0) {
            try {
                UiStopInformation.this.mSaxHelper.getName(DataConst.API_ALLBUSARRIVALINFO3 + this.mStopId + "&busOrder=" + (this.mOrder ? "1" : "2"), DataConst.FLAG_ALLBUSARRIVALINFO);
            } catch (Exception e) {
                e.printStackTrace();
                this.responseCode = 1;
            }
            return Integer.valueOf(this.responseCode);
        }
    }

    class SeparatedListAdapter extends BaseAdapter {
        static final int TYPE_SECTION_HEADER = 0;
        final ArrayAdapter<String> headers;
        final Map<String, Adapter> sections = new LinkedHashMap();

        public SeparatedListAdapter(Context context) {
            this.headers = new ArrayAdapter(context, C0258R.layout.list_common_header);
        }

        public void addSection(String section, Adapter adapter) {
            this.headers.add(section);
            this.sections.put(section, adapter);
        }

        public Object getItem(int position) {
            for (Object section : this.sections.keySet()) {
                Adapter adapter = (Adapter) this.sections.get(section);
                int size = adapter.getCount() + 1;
                if (position == 0) {
                    return section;
                }
                if (position < size) {
                    return adapter.getItem(position - 1);
                }
                position -= size;
            }
            return null;
        }

        public int getCount() {
            int total = 0;
            for (Adapter adapter : this.sections.values()) {
                total += adapter.getCount() + 1;
            }
            return total;
        }

        public int getViewTypeCount() {
            int total = 1;
            for (Adapter adapter : this.sections.values()) {
                total += adapter.getViewTypeCount();
            }
            return total;
        }

        public int getItemViewType(int position) {
            int type = 1;
            for (Object section : this.sections.keySet()) {
                Adapter adapter = (Adapter) this.sections.get(section);
                int size = adapter.getCount() + 1;
                if (position == 0) {
                    return 0;
                }
                if (position < size) {
                    return adapter.getItemViewType(position - 1) + type;
                }
                position -= size;
                type += adapter.getViewTypeCount();
            }
            return -1;
        }

        public boolean isEnabled(int position) {
            return getItemViewType(position) != 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            int sectionnum = 0;
            for (Object section : this.sections.keySet()) {
                Adapter adapter = (Adapter) this.sections.get(section);
                int size = adapter.getCount() + 1;
                if (position == 0) {
                    return this.headers.getView(sectionnum, convertView, parent);
                }
                if (position < size) {
                    return adapter.getView(position - 1, convertView, parent);
                }
                position -= size;
                sectionnum++;
            }
            return null;
        }

        public long getItemId(int position) {
            return (long) position;
        }
    }

    class StopItemAdapter extends ArrayAdapter<ArrivedBusInfo> {
        private ArrayList<ArrivedBusInfo> mBusArrival;
        private Context mContext;
        private int[] resIds;

        public StopItemAdapter(Context context, int textViewResourceId, ArrayList<ArrivedBusInfo> objects) {
            super(context, textViewResourceId, objects);
            this.mContext = context;
            this.mBusArrival = objects;
            TypedArray busType = this.mContext.getResources().obtainTypedArray(C0258R.array.bus_type);
            this.resIds = new int[busType.length()];
            for (int i = 0; i < this.resIds.length; i++) {
                this.resIds[i] = busType.getResourceId(i, 0);
            }
            busType.recycle();
        }

        public ArrivedBusInfo getItem(int position) {
            return (ArrivedBusInfo) this.mBusArrival.get(position);
        }

        public long getItemId(int position) {
            return super.getItemId(position);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (convertView == null) {
                v = ((LayoutInflater) this.mContext.getSystemService("layout_inflater")).inflate(C0258R.layout.stop_information_items, parent, false);
            }
            ArrivedBusInfo busArrival = (ArrivedBusInfo) this.mBusArrival.get(position);
            Cursor routeCursor = this.mContext.getContentResolver().query(DataConst.CONTENT_ROUTE_URI, new String[]{DataConst.KEY_NOTICE_ID, "route_id", "route_no", DataConst.KEY_ROUTE_DIR, DataConst.KEY_ROUTE_TYPE, "route_bus_type", DataConst.KEY_ROUTE_FSTOP_NAME, DataConst.KEY_ROUTE_TSTOP_NAME, DataConst.KEY_ROUTE_REMARK, DataConst.KEY_ROUTE_WD_START_TIME, DataConst.KEY_ROUTE_WD_END_TIME, DataConst.KEY_ROUTE_WE_START_TIME, DataConst.KEY_ROUTE_WE_END_TIME, DataConst.KEY_ROUTE_WS_START_TIME, DataConst.KEY_ROUTE_WS_END_TIME}, "route_id = " + busArrival.RouteID, null, null);
            if (routeCursor.getCount() > 0) {
                String strStartTime;
                String strEndTime;
                StringBuffer sb;
                routeCursor.moveToFirst();
                LinearLayout row = (LinearLayout) v.findViewById(C0258R.id.stop_information_row);
                if (UiStopInformation.this.bEmergency) {
                    row.setBackgroundResource(C0258R.drawable.list_emer_selector);
                } else {
                    row.setBackgroundResource(C0258R.drawable.list_selector);
                }
                ImageView mImageBusType = (ImageView) v.findViewById(C0258R.id.stop_information_item_image);
                int intBusType = Integer.parseInt(routeCursor.getString(routeCursor.getColumnIndex("route_bus_type")));
                mImageBusType.setImageResource(this.resIds[intBusType]);
                TextView mBusTypeTxt = (TextView) v.findViewById(C0258R.id.stop_information_bus_type);
                TextView textView = mBusTypeTxt;
                textView.setText(UiStopInformation.this.getResources().getStringArray(C0258R.array.stop_info_header_type)[intBusType]);
                String color = this.mContext.getResources().getStringArray(C0258R.array.bus_type_color)[intBusType];
                mBusTypeTxt.setTextColor(Color.parseColor(color));
                TextView number = (TextView) v.findViewById(C0258R.id.stop_information_item_number);
                number.setTextColor(Color.parseColor(color));
                TextView dest = (TextView) v.findViewById(C0258R.id.stop_information_item_desti);
                TextView arrivalTime = (TextView) v.findViewById(C0258R.id.stop_information_item_arravaltime);
                int date = Calendar.getInstance().get(7);
                if (date == 1) {
                    strStartTime = routeCursor.getString(routeCursor.getColumnIndex(DataConst.KEY_ROUTE_WS_START_TIME));
                    strEndTime = routeCursor.getString(routeCursor.getColumnIndex(DataConst.KEY_ROUTE_WS_END_TIME));
                } else if (date == 7) {
                    strStartTime = routeCursor.getString(routeCursor.getColumnIndex(DataConst.KEY_ROUTE_WE_START_TIME));
                    strEndTime = routeCursor.getString(routeCursor.getColumnIndex(DataConst.KEY_ROUTE_WE_END_TIME));
                } else {
                    strStartTime = routeCursor.getString(routeCursor.getColumnIndex(DataConst.KEY_ROUTE_WD_START_TIME));
                    strEndTime = routeCursor.getString(routeCursor.getColumnIndex(DataConst.KEY_ROUTE_WD_END_TIME));
                }
                int intStartTime = Integer.parseInt(strStartTime);
                int intEndTime = Integer.parseInt(strEndTime);
                String CurTime = UtilTimeManager.getCurHourMin();
                if (UiStopInformation.this.bEmergency) {
                    arrivalTime.setText("정보 없음");
                } else if (busArrival.Status.equals("1")) {
                    sb = new StringBuffer();
                    sb.append(busArrival.RemainTime);
                    sb.insert(2, ":");
                    sb.append("출발 예정");
                    arrivalTime.setText(sb.toString());
                    arrivalTime.setTextColor(Color.parseColor("#979797"));
                } else {
                    String strTime = UtilTimeManager.convertTimeArrival(Integer.parseInt(busArrival.RemainTime), "arrival");
                    arrivalTime.setText(strTime);
                    if (strTime.equals("운행 종료")) {
                        arrivalTime.setTextColor(Color.parseColor("#979797"));
                    } else {
                        arrivalTime.setTextColor(Color.parseColor("#DB2900"));
                    }
                }
                number.setText(routeCursor.getString(routeCursor.getColumnIndex("route_no")));
                String strRemark = routeCursor.getString(routeCursor.getColumnIndex(DataConst.KEY_ROUTE_REMARK));
                sb = new StringBuffer();
                sb.append(routeCursor.getString(routeCursor.getColumnIndex(DataConst.KEY_ROUTE_FSTOP_NAME)));
                sb.append(" > ");
                if (strRemark.length() > 0) {
                    sb.append(strRemark + " > ");
                }
                sb.append(routeCursor.getString(routeCursor.getColumnIndex(DataConst.KEY_ROUTE_TSTOP_NAME)));
                dest.setText(sb.toString());
            }
            return v;
        }
    }

    protected void onStart() {
        super.onStart();
        if (new UtilNetworkConnection().IsConnected(this)) {
            this.mRequestTask = new ArrivalBusRequest(this.mBusStopId, this.isArrivalBus);
            this.mRequestTask.execute(new Void[0]);
            return;
        }
        showError();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0258R.layout.stop_information);
        this.prefs = getSharedPreferences("PreStatus", 0);
        this.bEmergency = this.prefs.getBoolean(DataConst.KEY_PREF_EMER_MODE, false);
        this.imm = (InputMethodManager) getSystemService("input_method");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.mBusStopName = extras.getString("stop_name");
            this.mBusStopId = extras.getString("stop_id");
            this.mStopRemark = extras.getString("stop_remark");
        }
        if (!DataConst.SelectedStopMapStation.equals("")) {
            this.mBusStopId = DataConst.SelectedStopMapStation;
            DataConst.SelectedStopMapStation = "";
            Cursor cursor = getContentResolver().query(DataConst.CONTENT_STOPS_URI, new String[]{DataConst.KEY_NOTICE_ID, "stop_name", "stop_remark"}, "stop_id = " + this.mBusStopId, null, null);
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                this.mBusStopName = cursor.getString(cursor.getColumnIndex("stop_name"));
                this.mStopRemark = cursor.getString(cursor.getColumnIndex("stop_remark"));
            }
        }
        this.mBgImage = (ImageView) findViewById(C0258R.id.stop_bgimage);
        this.input_background = (RelativeLayout) findViewById(C0258R.id.stop_information_background_layout);
        this.mStopNameTv = (TextView) findViewById(C0258R.id.stop_info_stop_name);
        this.mStopIdTv = (TextView) findViewById(C0258R.id.stop_info_stop_id);
        this.mSwitchArrivalMode = (Button) findViewById(C0258R.id.icon_arriva_mode);
        this.mBusStationMap = (Button) findViewById(C0258R.id.stop_info_map);
        this.mAddFavorite = (Button) findViewById(C0258R.id.arrival_time_add_favorite);
        this.mRefresh = (Button) findViewById(C0258R.id.icon_refresh_stop_infomation);
        this.mTransferStartSet = (Button) findViewById(C0258R.id.transfer_start_add);
        this.mTransferStartSet.setOnClickListener(new C03211());
        this.mTransferEndSet = (Button) findViewById(C0258R.id.transfer_end_add);
        this.mTransferEndSet.setOnClickListener(new C03222());
        this.mSwitchArrivalMode.setOnClickListener(new C03233());
        this.mBusStationMap.setOnClickListener(new C03244());
        this.mAddFavorite.setOnClickListener(new C03255());
        this.mRefresh.setOnClickListener(new C03266());
        StringBuffer stopName = new StringBuffer();
        stopName.append(this.mBusStopName);
        if (this.mStopRemark.length() > 0) {
            stopName.append(" ( ");
            stopName.append(this.mStopRemark);
            stopName.append(" )");
        }
        this.mStopNameTv.setText(stopName.toString());
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(this.mBusStopId);
        sb.append(")");
        this.mStopIdTv.setText(sb.toString());
        this.mStopListView = (ListView) findViewById(C0258R.id.stop_information_list);
        this.mStopListView.setOnItemClickListener(this);
        this.mStopListView.setVisibility(8);
        this.mProgressBar = (ProgressBar) findViewById(C0258R.id.stop_information_progress);
        this.mAllBusArrivalList = new AllBusArrival();
        this.adapter = new SeparatedListAdapter(this);
        this.mSaxHelper = new DataSAXParser();
        this.handler = new C03277();
        if (this.imm.isActive()) {
            this.imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
        if (this.bEmergency) {
            this.mBgImage.setBackgroundResource(C0258R.drawable.background_emer);
            this.input_background.setBackgroundColor(Color.parseColor("#D7ADA7"));
            return;
        }
        this.mBgImage.setBackgroundResource(C0258R.drawable.background_nomal);
        this.input_background.setBackgroundColor(Color.parseColor("#77ADD1"));
    }

    protected void onStop() {
        super.onStop();
        DataConst.SelectedStopId = "";
    }

    private void showInfo() {
        String[] mBusType = getResources().getStringArray(C0258R.array.stop_info_header_type);
        LinkedHashMap<Integer, ArrayList<ArrivedBusInfo>> map = new LinkedHashMap();
        for (int i = 0; i < mBusType.length; i++) {
            ArrayList mBusList = new ArrayList();
            Iterator it = this.mAllBusArrivalList.getAllBusArrivalInfoList().iterator();
            while (it.hasNext()) {
                ArrivedBusInfo item = (ArrivedBusInfo) it.next();
                Cursor cursor = getContentResolver().query(DataConst.CONTENT_ROUTE_URI, new String[]{DataConst.KEY_NOTICE_ID, "route_bus_type"}, "route_id = " + item.RouteID, null, null);
                if (cursor.getCount() > 0) {
                    if (cursor.moveToFirst()) {
                        if (Integer.parseInt(cursor.getString(cursor.getColumnIndex("route_bus_type"))) == i) {
                            ArrivedBusInfo busArrival = this.mAllBusArrivalList.getNewInstanceBusInfo();
                            busArrival.EmergencyCD = item.EmergencyCD;
                            busArrival.LowType = item.LowType;
                            busArrival.RemainStopCnt = item.RemainStopCnt;
                            busArrival.RemainTime = item.RemainTime;
                            busArrival.RouteID = item.RouteID;
                            busArrival.mBusNo = item.mBusNo;
                            busArrival.Status = item.Status;
                            busArrival.StopID = item.StopID;
                            busArrival.BusType = item.BusType;
                            busArrival.mFStopName = item.mFStopName;
                            busArrival.mTstopName = item.mTstopName;
                            mBusList.add(busArrival);
                        }
                        map.put(new Integer(i), mBusList);
                    }
                    cursor.close();
                }
            }
        }
        for (Integer key : map.keySet()) {
            ArrayList<ArrivedBusInfo> value = (ArrayList) map.get(key);
            if (value.size() > 0) {
                this.adapter.addSection(mBusType[key.intValue()], new StopItemAdapter(this, 0, value));
            }
        }
        this.mStopListView.setAdapter(this.adapter);
        this.mStopListView.setVisibility(0);
    }

    private void showError() {
        new Builder(getParent().getParent()).setTitle(getResources().getString(C0258R.string.warnning)).setMessage(getResources().getString(C0258R.string.server_conn_no_response)).setPositiveButton(getString(C0258R.string.ok), new C03288()).show();
    }

    void showSelectDialog(String StopId, String StopName, boolean bDestination) {
        String mTitleId;
        boolean bdestination = bDestination;
        Editor editor = this.prefs.edit();
        if (bdestination) {
            editor.putString(DataConst.KEY_PREF_TO_STOP_ID, StopId);
            editor.putString(DataConst.KEY_PREF_TO_STOP_NAME, StopName);
            mTitleId = getResources().getString(C0258R.string.transfer_end_input_text);
        } else {
            editor.putString(DataConst.KEY_PREF_FROM_STOP_ID, StopId);
            editor.putString(DataConst.KEY_PREF_FROM_STOP_NAME, StopName);
            mTitleId = getResources().getString(C0258R.string.transfer_start_input_text);
        }
        editor.commit();
        Toast.makeText(getApplicationContext(), mTitleId + "로 등록 되었습니다", 0).show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("nameun", "onActivityResult");
    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        ArrivedBusInfo item = (ArrivedBusInfo) arg0.getAdapter().getItem(arg2);
        Intent intent = new Intent(this, UiStopArrivalTime.class);
        intent.putExtra("stop_id", this.mBusStopId);
        intent.putExtra("stop_name", this.mBusStopName);
        intent.putExtra("stop_remark", this.mStopRemark);
        intent.putExtra("route_id", item.RouteID);
        intent.putExtra("route_no", item.RouteNo);
        intent.putExtra("route_bus_type", item.BusType);
        intent.putExtra("low_type", item.LowType);
        switch (UiMainTab.mTabHost.getCurrentTab()) {
            case 1:
                TabFirst.TAB1.replaceActivity("bus_arrival", intent, this);
                return;
            case 2:
                TabSecond.TAB2.replaceActivity("bus_arrival", intent, this);
                return;
            case 3:
                TabThird.TAB3.replaceActivity("bus_arrival", intent, this);
                return;
            case 4:
                TabFourth.TAB4.replaceActivity("bus_arrival", intent, this);
                return;
            case 5:
                TabFiveth.TAB5.replaceActivity("bus_arrival", intent, this);
                return;
            default:
                return;
        }
    }
}
