package com.neighbor.ulsanbus.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FilterQueryProvider;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.CursorToStringConverter;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import com.google.zxing.client.android.share.Browser.BookmarkColumns;
import com.neighbor.ulsanbus.C0258R;
import com.neighbor.ulsanbus.DirectTable;
import com.neighbor.ulsanbus.ItemParcel;
import com.neighbor.ulsanbus.RouteParcel;
import com.neighbor.ulsanbus.Transfer;
import com.neighbor.ulsanbus.TransferTable;
import com.neighbor.ulsanbus.TransitionBus;
import com.neighbor.ulsanbus.data.DataConst;
import com.neighbor.ulsanbus.network.ParseTransition;
import com.neighbor.ulsanbus.util.UtilTimeManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

public class UiTransferInput extends NavigationActivity implements OnClickListener {
    private static final int CONNECT_ERR = -2;
    private static final int PARSE_ERR = -1;
    public static UiTransferInput ROOT_TRANS;
    static final int[] to = new int[]{C0258R.id.transfer_input_complete_stop_name};
    private AutoCompleteTextView EditDepart;
    private AutoCompleteTextView EditDest;
    private final int FINISH_GET_TRANSFER_INFO = 1;
    private boolean bEmergency;
    private InputTextAdapter departAdapter;
    private InputTextAdapter destAdapter;
    private ListView directRouteList;
    Handler handler = new C03341();
    private InputMethodManager imm;
    private Cursor mDepatureCursor;
    private Cursor mDestCursor;
    private DirectRouteAdapter mDirectRouteAdapter;
    private Transfer mTransferInfo;
    private TransferRouteAdapter mTransferRouteAdapter;
    private SharedPreferences prefs;
    private ProgressBar progTransfer;
    private ListView transRouteList;
    private LinearLayout transferDirectLayout;
    private LinearLayout transferInfoLayout;
    private LinearLayout transferRouteLayout;

    /* renamed from: com.neighbor.ulsanbus.ui.UiTransferInput$1 */
    class C03341 extends Handler {
        C03341() {
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case -2:
                case -1:
                    UiTransferInput.this.showError();
                    return;
                case 1:
                    UiTransferInput.this.progTransfer.setVisibility(8);
                    UiTransferInput.this.transferInfoLayout.setVisibility(0);
                    try {
                        UiTransferInput.this.ShowTransitionResult();
                        return;
                    } catch (Exception e) {
                        UiTransferInput.this.showError();
                        return;
                    }
                default:
                    return;
            }
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiTransferInput$2 */
    class C03352 implements OnItemClickListener {
        C03352() {
        }

        public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
            DirectTable route = (DirectTable) UiTransferInput.this.mTransferInfo.getDirectList().get((int) arg3);
            Intent intent = new Intent(UiTransferInput.this, UiTransferDetail.class);
            intent.putExtra("DirStopIDList", route.getStoplist());
            intent.putExtra("DirTravelTime", route.getTravelTime());
            intent.putExtra("DirStopCnt", route.getStopCNT());
            intent.putExtra("DirLength", route.getLength());
            intent.putExtra("RouteType", "1");
            intent.putExtra("DirRouteId", route.getRouteId());
            TabFourth.TAB4.replaceActivity("BusStop", intent, UiTransferInput.this);
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiTransferInput$3 */
    class C03363 implements OnItemClickListener {
        C03363() {
        }

        public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
            TransferTable route = (TransferTable) UiTransferInput.this.mTransferInfo.getTransList().get((int) arg3);
            RouteParcel routeParcel = new RouteParcel();
            routeParcel.setTravelTime(route.getTravelTime());
            routeParcel.setLength(route.getTravelLength());
            routeParcel.setTransCnt(route.getTransCount());
            Iterator it = route.getTransitionlist().iterator();
            while (it.hasNext()) {
                TransitionBus infoItem = (TransitionBus) it.next();
                ItemParcel parcelItem = new ItemParcel();
                parcelItem.setRouteId(infoItem.getRouteId());
                parcelItem.setWalkingTime(infoItem.getWalikingTime());
                parcelItem.setWalkingLength(infoItem.getWalkingLength());
                parcelItem.setVertaxCount(infoItem.getVertaxCount());
                Iterator it2 = infoItem.getStoplist().iterator();
                while (it2.hasNext()) {
                    parcelItem.addStop((String) it2.next());
                }
                routeParcel.addTransferRoute(parcelItem);
            }
            Intent intent = new Intent(UiTransferInput.this, UiTransferDetail.class);
            intent.putExtra("routeInfo", routeParcel);
            intent.putExtra("RouteType", "2");
            TabFourth.TAB4.replaceActivity("BusStop", intent, UiTransferInput.this);
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiTransferInput$4 */
    class C03374 implements DialogInterface.OnClickListener {
        C03374() {
        }

        public void onClick(DialogInterface dialog, int which) {
            UiTransferInput.this.finish();
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiTransferInput$5 */
    class C03385 implements CursorToStringConverter {
        C03385() {
        }

        public CharSequence convertToString(Cursor arg0) {
            StringBuffer dispStopName = new StringBuffer();
            dispStopName.append(arg0.getString(arg0.getColumnIndex("stop_name")));
            TransferInfo.departureRemark = arg0.getString(arg0.getColumnIndex("stop_remark"));
            TransferInfo.departureName = arg0.getString(arg0.getColumnIndex("stop_name"));
            TransferInfo.departureId = arg0.getString(arg0.getColumnIndex("stop_id"));
            return dispStopName.toString();
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiTransferInput$6 */
    class C03396 implements FilterQueryProvider {
        C03396() {
        }

        public Cursor runQuery(CharSequence constraint) {
            Cursor cur = UiTransferInput.this.getContentResolver().query(DataConst.CONTENT_STOPS_URI, new String[]{DataConst.KEY_NOTICE_ID, "stop_name", "stop_id", "stop_remark"}, "stop_name like ?", new String[]{((String) constraint) + "%"}, null);
            UiTransferInput.this.startManagingCursor(cur);
            return cur;
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiTransferInput$7 */
    class C03407 implements CursorToStringConverter {
        C03407() {
        }

        public CharSequence convertToString(Cursor arg0) {
            StringBuffer dispStopName = new StringBuffer();
            dispStopName.append(arg0.getString(arg0.getColumnIndex("stop_name")));
            TransferInfo.destinationRemark = arg0.getString(arg0.getColumnIndex("stop_remark"));
            TransferInfo.destinationName = arg0.getString(arg0.getColumnIndex("stop_name"));
            TransferInfo.destinationId = arg0.getString(arg0.getColumnIndex("stop_id"));
            return dispStopName.toString();
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiTransferInput$8 */
    class C03418 implements FilterQueryProvider {
        C03418() {
        }

        public Cursor runQuery(CharSequence constraint) {
            Cursor cur = UiTransferInput.this.getContentResolver().query(DataConst.CONTENT_STOPS_URI, new String[]{DataConst.KEY_NOTICE_ID, "stop_name", "stop_id", "stop_remark"}, "stop_name like ?", new String[]{((String) constraint) + "%"}, null);
            UiTransferInput.this.startManagingCursor(cur);
            return cur;
        }
    }

    class DirectRouteAdapter extends ArrayAdapter<DirectTable> {
        private Context mContext;

        /* renamed from: com.neighbor.ulsanbus.ui.UiTransferInput$DirectRouteAdapter$1 */
        class C03421 implements OnClickListener {
            C03421() {
            }

            public void onClick(View arg0) {
                DirectTable item = (DirectTable) arg0.getTag();
                Intent intent = new Intent(UiTransferInput.this, UiDirectRouteMap.class);
                intent.putExtra("DirRouteId", item.getRouteId());
                intent.putExtra("Dirvertax", item.getPositionlist());
                TabFourth.TAB4.replaceActivity("transferInput", intent, UiTransferInput.this);
            }
        }

        public DirectRouteAdapter(Context context, int resource, int textViewResourceId, List objects) {
            super(context, resource, textViewResourceId, objects);
            this.mContext = context;
        }

        public int getCount() {
            return UiTransferInput.this.mTransferInfo.getDirectList().size();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (convertView == null) {
                v = ((LayoutInflater) this.mContext.getSystemService("layout_inflater")).inflate(C0258R.layout.transfer_input_item, parent, false);
            }
            DirectTable routeInfo = (DirectTable) UiTransferInput.this.mTransferInfo.getDirectList().get(position);
            LinearLayout row = (LinearLayout) v.findViewById(C0258R.id.transfer_row);
            if (UiTransferInput.this.bEmergency) {
                row.setBackgroundResource(C0258R.drawable.list_emer_selector);
            } else {
                row.setBackgroundResource(C0258R.drawable.list_selector);
            }
            TextView txStart = (TextView) v.findViewById(C0258R.id.transfer_fist_route);
            TextView txTakedTime = (TextView) v.findViewById(C0258R.id.transfer_travel_time);
            TextView txTakedStops = (TextView) v.findViewById(C0258R.id.transfer_travel_stops);
            Button btnMap = (Button) v.findViewById(C0258R.id.btn_transfer_map);
            btnMap.setTag(routeInfo);
            btnMap.setOnClickListener(new C03421());
            try {
                Cursor cursor = this.mContext.getContentResolver().query(DataConst.CONTENT_ROUTE_URI, new String[]{DataConst.KEY_NOTICE_ID, "route_id", "route_no", DataConst.KEY_ROUTE_TYPE, "route_bus_type"}, "route_id=?", new String[]{routeInfo.getRouteId()}, null);
                if (cursor.moveToFirst()) {
                    String arrivalTime;
                    String[] colorArray = this.mContext.getResources().getStringArray(C0258R.array.bus_type_color);
                    String[] txBusType = this.mContext.getResources().getStringArray(C0258R.array.stop_info_header_type);
                    String routeNo = cursor.getString(cursor.getColumnIndex("route_no"));
                    int intBusType = Integer.parseInt(new String(cursor.getString(cursor.getColumnIndex("route_bus_type"))));
                    txStart.setTextColor(Color.parseColor(colorArray[intBusType]));
                    StringBuffer sb = new StringBuffer();
                    sb.append(txBusType[intBusType]);
                    sb.append(" ");
                    sb.append(routeNo);
                    txStart.setText(txBusType[intBusType] + " " + routeNo);
                    if (UiTransferInput.this.bEmergency) {
                        arrivalTime = "정보 없슴";
                    } else {
                        arrivalTime = UtilTimeManager.convertTimeArrival(Integer.parseInt(routeInfo.getTravelTime()), " ");
                    }
                    txTakedTime.setText(arrivalTime);
                    StringBuffer sbStops = new StringBuffer();
                    sbStops.append(routeInfo.getStopCNT());
                    sbStops.append("개 정류장 ");
                    sbStops.append(UtilTimeManager.convertfromMeters(Double.parseDouble(routeInfo.getLength())));
                    sbStops.append(" km");
                    txTakedStops.setText(sbStops.toString());
                }
            } catch (Exception e) {
            }
            return v;
        }
    }

    class InputTextAdapter extends SimpleCursorAdapter {
        private Cursor cursor;
        private LayoutInflater mInflater;

        public InputTextAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
            super(context, layout, c, from, to);
            this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
            this.cursor = c;
        }

        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return this.mInflater.inflate(C0258R.layout.transfer_input_completeitem, parent, false);
        }

        public void bindView(View view, Context context, Cursor cursor) {
            super.bindView(view, context, cursor);
            TextView stopname = (TextView) view.findViewById(C0258R.id.transfer_input_complete_stop_name);
            StringBuffer txNameId = new StringBuffer();
            txNameId.append(cursor.getString(cursor.getColumnIndex("stop_name")));
            String stopRemark = cursor.getString(cursor.getColumnIndex("stop_remark"));
            if (stopRemark.length() > 0) {
                txNameId.append(" - " + stopRemark);
            }
            txNameId.append(" [ ");
            txNameId.append(cursor.getString(cursor.getColumnIndex("stop_id")));
            txNameId.append(" ] ");
            stopname.setText(txNameId.toString());
        }
    }

    private static class TransferInfo {
        static String departureId = null;
        static String departureName = null;
        static String departureRemark = null;
        static String destinationId = null;
        static String destinationName = null;
        static String destinationRemark = null;

        private TransferInfo() {
        }
    }

    class TransferRequest extends Thread {
        private String departure;
        private String destination;
        private ParseTransition mTransition;

        public TransferRequest(String stardId, String EndStop) {
            this.departure = stardId;
            this.destination = EndStop;
        }

        public void run() {
            try {
                this.mTransition = new ParseTransition();
                this.mTransition.setEndStop(this.departure, this.destination);
                this.mTransition.parse();
                UiTransferInput.this.mTransferInfo = (Transfer) this.mTransition.getObject();
                UiTransferInput.this.handler.sendEmptyMessage(1);
            } catch (NullPointerException e) {
                UiTransferInput.this.handler.sendEmptyMessage(-1);
            } catch (XmlPullParserException e2) {
                UiTransferInput.this.handler.sendEmptyMessage(-1);
            } catch (IOException e3) {
                UiTransferInput.this.handler.sendEmptyMessage(-2);
            }
        }
    }

    class TransferRouteAdapter extends ArrayAdapter<TransferTable> {
        private static final int TRANS_COUNT_THIRD = 3;
        private static final int TRANS_COUNT_TWICE = 2;
        String[] colorArray = this.mContext.getResources().getStringArray(C0258R.array.bus_type_color);
        private ArrayList<ItemInfo> item = new ArrayList();
        private Context mContext;
        String[] txBusType = this.mContext.getResources().getStringArray(C0258R.array.stop_info_header_type);

        /* renamed from: com.neighbor.ulsanbus.ui.UiTransferInput$TransferRouteAdapter$1 */
        class C03431 implements OnClickListener {
            C03431() {
            }

            public void onClick(View arg0) {
                Log.e("test ", "here");
                TransferTable routeInfo = (TransferTable) arg0.getTag();
                Intent intent = new Intent(UiTransferInput.this, UiTransferRouteMap.class);
                intent.putExtra(DataConst.TABLE_ROUTE_LIST, routeInfo.getTransitionlist());
                TabFourth.TAB4.replaceActivity("BusStop", intent, UiTransferInput.this);
            }
        }

        class ItemInfo {
            String BusType;
            String RouteId;
            String RouteNo;
            String StopId;
            String StopName;

            ItemInfo() {
            }
        }

        public TransferRouteAdapter(Context context, int textViewResourceId, List objects) {
            super(context, textViewResourceId, objects);
            this.mContext = context;
        }

        void setBusInfo(int position, TextView txView) {
            String[] colorArray = this.mContext.getResources().getStringArray(C0258R.array.bus_type_color);
            String[] txBusType = this.mContext.getResources().getStringArray(C0258R.array.stop_info_header_type);
            ItemInfo info = (ItemInfo) this.item.get(position);
            int intBusType = Integer.parseInt(info.BusType);
            StringBuffer busInfo = new StringBuffer();
            busInfo.append(txBusType[intBusType]);
            busInfo.append("");
            busInfo.append(info.RouteNo);
            txView.setTextColor(Color.parseColor(colorArray[intBusType]));
            txView.setText(busInfo.toString());
        }

        public int getCount() {
            return Integer.parseInt(UiTransferInput.this.mTransferInfo.getTransCnt());
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            String arrivalTime;
            View v = convertView;
            if (convertView == null) {
                v = ((LayoutInflater) this.mContext.getSystemService("layout_inflater")).inflate(C0258R.layout.transfer_input_item, parent, false);
            }
            LinearLayout row = (LinearLayout) v.findViewById(C0258R.id.transfer_row);
            if (UiTransferInput.this.bEmergency) {
                row.setBackgroundResource(C0258R.drawable.list_emer_selector);
            } else {
                row.setBackgroundResource(C0258R.drawable.list_selector);
            }
            TextView txStart = (TextView) v.findViewById(C0258R.id.transfer_fist_route);
            LinearLayout sndLayout = (LinearLayout) v.findViewById(C0258R.id.transfer_second_route_layout);
            LinearLayout third = (LinearLayout) v.findViewById(C0258R.id.transfer_third_route_layout);
            TextView txTakedTime = (TextView) v.findViewById(C0258R.id.transfer_travel_time);
            TextView txTakedStops = (TextView) v.findViewById(C0258R.id.transfer_travel_stops);
            TextView scndText = (TextView) v.findViewById(C0258R.id.transfer_second_route);
            TextView txThird = (TextView) v.findViewById(C0258R.id.transfer_third_route);
            Button btnMap = (Button) v.findViewById(C0258R.id.btn_transfer_map);
            TransferTable routeInfo = (TransferTable) UiTransferInput.this.mTransferInfo.getTransList().get(position);
            ArrayList<TransitionBus> bus = routeInfo.getTransitionlist();
            this.item.clear();
            synchronized (bus) {
                Iterator<TransitionBus> iterator = bus.iterator();
                while (iterator.hasNext()) {
                    TransitionBus it = (TransitionBus) iterator.next();
                    ItemInfo info = new ItemInfo();
                    info.RouteId = it.getRouteId();
                    info.StopId = (String) it.getStoplist().get(0);
                    this.item.add(info);
                }
            }
            Iterator it2 = this.item.iterator();
            while (it2.hasNext()) {
                ItemInfo transinfo = (ItemInfo) it2.next();
                Cursor cursor = this.mContext.getContentResolver().query(DataConst.CONTENT_ROUTE_URI, new String[]{"route_id", "route_no", "route_bus_type"}, "route_id=" + transinfo.RouteId, null, null);
                if (cursor.moveToFirst()) {
                    transinfo.RouteNo = cursor.getString(cursor.getColumnIndex("route_no"));
                    transinfo.BusType = cursor.getString(cursor.getColumnIndex("route_bus_type"));
                }
                cursor.close();
                Cursor stopcursor = UiTransferInput.this.getContentResolver().query(DataConst.CONTENT_STOPS_URI, new String[]{"stop_name", "stop_id"}, "stop_id=?", new String[]{transinfo.StopId}, null);
                if (stopcursor.moveToFirst()) {
                    transinfo.StopName = stopcursor.getString(stopcursor.getColumnIndex("stop_name"));
                }
                stopcursor.close();
            }
            setBusInfo(0, txStart);
            switch (bus.size()) {
                case 2:
                    sndLayout.setVisibility(0);
                    setBusInfo(1, scndText);
                    break;
                case 3:
                    sndLayout.setVisibility(0);
                    third.setVisibility(0);
                    setBusInfo(1, scndText);
                    setBusInfo(2, txThird);
                    break;
            }
            if (UiTransferInput.this.bEmergency) {
                arrivalTime = "정보 없음";
            } else {
                arrivalTime = UtilTimeManager.convertTimeArrival(Integer.parseInt(routeInfo.getTravelTime()), " ");
            }
            txTakedTime.setText(arrivalTime);
            if (((ItemInfo) this.item.get(1)).StopName != null) {
                StringBuffer transferTxt = new StringBuffer();
                transferTxt.append(((ItemInfo) this.item.get(1)).StopName);
                transferTxt.append(" 정류장에서 ");
                transferTxt.append(routeInfo.getTransCount());
                transferTxt.append(" 번 환승");
                txTakedStops.setText(transferTxt.toString());
            }
            btnMap.setTag(routeInfo);
            btnMap.setOnClickListener(new C03431());
            return v;
        }
    }

    void ShowTransitionResult() {
        try {
            String dirCNT = this.mTransferInfo.getDirectCnt();
            String tranCNT = this.mTransferInfo.getTransCnt();
            Log.d("UiTransferInput", "dirCNT" + dirCNT);
            Log.d("UiTransferInput", "tranCNT" + tranCNT);
            this.transferInfoLayout.setVisibility(0);
            if (Integer.parseInt(dirCNT) > 0) {
                this.transferDirectLayout.setVisibility(0);
                this.mDirectRouteAdapter = new DirectRouteAdapter(this, 0, 0, null);
                this.directRouteList.setAdapter(this.mDirectRouteAdapter);
                this.directRouteList.setOnItemClickListener(new C03352());
            }
            if (Integer.parseInt(tranCNT) > 0) {
                this.transferRouteLayout.setVisibility(0);
                this.mTransferRouteAdapter = new TransferRouteAdapter(this, 0, null);
                this.transRouteList.setAdapter(this.mTransferRouteAdapter);
                this.transRouteList.setOnItemClickListener(new C03363());
            }
        } catch (Exception e) {
        }
    }

    private void showError() {
        try {
            new Builder(getParent().getParent()).setTitle(getString(C0258R.string.error)).setMessage(getString(C0258R.string.transfer_error)).setNeutralButton(getString(C0258R.string.ok), new C03374()).show();
        } catch (Exception e) {
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0258R.layout.transfer_input);
        ROOT_TRANS = this;
        this.imm = (InputMethodManager) getSystemService("input_method");
        this.prefs = getSharedPreferences("PreStatus", 0);
        this.bEmergency = this.prefs.getBoolean(DataConst.KEY_PREF_EMER_MODE, false);
        DataConst.SelectedStopMapStation = "";
        this.EditDepart = (AutoCompleteTextView) findViewById(C0258R.id.transfer_start_edittext);
        this.mDepatureCursor = getContentResolver().query(DataConst.CONTENT_STOPS_URI, new String[]{DataConst.KEY_NOTICE_ID, "stop_name", "stop_id", "stop_remark"}, null, null, null);
        this.departAdapter = new InputTextAdapter(this, C0258R.layout.transfer_input_completeitem, null, new String[]{"stop_name", "stop_id", "stop_remark"}, to);
        try {
            this.departAdapter.setCursorToStringConverter(new C03385());
            this.departAdapter.setFilterQueryProvider(new C03396());
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.EditDepart.setAdapter(this.departAdapter);
        this.EditDepart.setThreshold(1);
        ((Button) findViewById(C0258R.id.btn_transfer_start)).setOnClickListener(this);
        ((Button) findViewById(C0258R.id.btn_transfer_start_map)).setOnClickListener(this);
        this.EditDest = (AutoCompleteTextView) findViewById(C0258R.id.transfer_dest_edittext);
        this.mDestCursor = getContentResolver().query(DataConst.CONTENT_STOPS_URI, new String[]{DataConst.KEY_NOTICE_ID, "stop_name", "stop_id", "stop_remark"}, null, null, null);
        this.destAdapter = new InputTextAdapter(this, C0258R.layout.transfer_input_completeitem, null, new String[]{"stop_name", "stop_id", "stop_remark"}, to);
        try {
            this.destAdapter.setCursorToStringConverter(new C03407());
            this.destAdapter.setFilterQueryProvider(new C03418());
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        this.EditDest.setAdapter(this.destAdapter);
        this.EditDest.setThreshold(1);
        ((Button) findViewById(C0258R.id.btn_transfer_dest_search)).setOnClickListener(this);
        ((Button) findViewById(C0258R.id.btn_transfer_dest_map)).setOnClickListener(this);
        ((Button) findViewById(C0258R.id.btn_transfer_route_search)).setOnClickListener(this);
        this.progTransfer = (ProgressBar) findViewById(C0258R.id.transfer_route_progress);
        String departureid = this.prefs.getString(DataConst.KEY_PREF_FROM_STOP_ID, null);
        String departureName = this.prefs.getString(DataConst.KEY_PREF_FROM_STOP_NAME, null);
        String destinationid = this.prefs.getString(DataConst.KEY_PREF_TO_STOP_ID, null);
        String destinationName = this.prefs.getString(DataConst.KEY_PREF_TO_STOP_NAME, null);
        if (departureName != null) {
            TransferInfo.departureId = departureid;
            TransferInfo.departureName = departureName;
            this.EditDepart.setText(TransferInfo.departureName, BufferType.EDITABLE);
        }
        if (destinationName != null) {
            TransferInfo.destinationId = destinationid;
            TransferInfo.destinationName = destinationName;
            this.EditDest.setText(TransferInfo.destinationName, BufferType.EDITABLE);
        }
        this.transferInfoLayout = (LinearLayout) findViewById(C0258R.id.transfer_list_layout);
        this.transferDirectLayout = (LinearLayout) findViewById(C0258R.id.direct_route_layout);
        this.transferRouteLayout = (LinearLayout) findViewById(C0258R.id.transfer_route_layout);
        this.directRouteList = (ListView) findViewById(C0258R.id.direct_route_list);
        this.transRouteList = (ListView) findViewById(C0258R.id.transfer_route_list);
        FrameLayout bg = (FrameLayout) findViewById(C0258R.id.content_background);
        LinearLayout mDepartureLayout = (LinearLayout) findViewById(C0258R.id.departure_layout);
        LinearLayout mDestinationLayout = (LinearLayout) findViewById(C0258R.id.destination_layout);
        if (this.bEmergency) {
            bg.setBackgroundResource(C0258R.drawable.background_emer);
            mDepartureLayout.setBackgroundColor(Color.parseColor("#D7ADA7"));
            mDestinationLayout.setBackgroundColor(Color.parseColor("#D7ADA7"));
            return;
        }
        bg.setBackgroundResource(C0258R.drawable.background_nomal);
        mDepartureLayout.setBackgroundColor(Color.parseColor("#77ADD1"));
        mDestinationLayout.setBackgroundColor(Color.parseColor("#77ADD1"));
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != -1) {
            return;
        }
        if (requestCode == 5) {
            TransferInfo.departureName = data.getStringExtra("stopName");
            TransferInfo.departureId = data.getStringExtra("stopId");
            this.EditDepart.setText(TransferInfo.departureName);
            return;
        }
        TransferInfo.destinationName = data.getStringExtra("stopName");
        TransferInfo.destinationId = data.getStringExtra("stopId");
        this.EditDest.setText(TransferInfo.destinationName);
    }

    public void onClick(View arg0) {
        Intent intent;
        switch (arg0.getId()) {
            case C0258R.id.btn_transfer_dest_map:
                intent = new Intent(this, UiBusStopPosition.class);
                String destText = "";
                if (!this.EditDest.getText().toString().equals("도착지")) {
                    destText = this.EditDest.getText().toString();
                }
                intent.putExtra("stop_name", destText);
                intent.putExtra("stop_id", TransferInfo.destinationId);
                TabFourth.TAB4.replaceActivity("transferInput", intent, this);
                return;
            case C0258R.id.btn_transfer_dest_search:
                intent = new Intent(this, UiPopupList.class);
                intent.putExtra(BookmarkColumns.TITLE, "도착지검색");
                getParent().startActivityForResult(intent, 6);
                return;
            case C0258R.id.btn_transfer_route_search:
                if (TransferInfo.departureId != null && TransferInfo.destinationId != null) {
                    Editor editor = this.prefs.edit();
                    editor.putString(DataConst.KEY_PREF_FROM_STOP_ID, TransferInfo.departureId);
                    editor.putString(DataConst.KEY_PREF_FROM_STOP_NAME, TransferInfo.departureName);
                    editor.putString(DataConst.KEY_PREF_TO_STOP_ID, TransferInfo.destinationId);
                    editor.putString(DataConst.KEY_PREF_TO_STOP_NAME, TransferInfo.destinationName);
                    editor.commit();
                    new TransferRequest(TransferInfo.departureId, TransferInfo.destinationId).start();
                    if (this.transferInfoLayout.getVisibility() == 0) {
                        this.transferInfoLayout.setVisibility(8);
                    }
                    this.progTransfer.setVisibility(0);
                    if (this.imm.isActive()) {
                        this.imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                        return;
                    }
                    return;
                }
                return;
            case C0258R.id.btn_transfer_start:
                intent = new Intent(getParent(), UiPopupList.class);
                intent.putExtra(BookmarkColumns.TITLE, "출발지 검색");
                getParent().startActivityForResult(intent, 5);
                return;
            case C0258R.id.btn_transfer_start_map:
                intent = new Intent(this, UiBusStopPosition.class);
                String departureText = "";
                if (!this.EditDepart.getText().toString().equals("출발지")) {
                    departureText = this.EditDepart.getText().toString();
                }
                intent.putExtra("stop_name", departureText);
                intent.putExtra("stop_id", TransferInfo.departureId);
                TabFourth.TAB4.replaceActivity("transferInput", intent, this);
                return;
            default:
                return;
        }
    }
}
