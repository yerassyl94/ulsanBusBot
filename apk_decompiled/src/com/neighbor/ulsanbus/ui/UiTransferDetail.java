package com.neighbor.ulsanbus.ui;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.neighbor.ulsanbus.C0258R;
import com.neighbor.ulsanbus.ItemParcel;
import com.neighbor.ulsanbus.RouteArrival;
import com.neighbor.ulsanbus.RouteParcel;
import com.neighbor.ulsanbus.TransitionBus;
import com.neighbor.ulsanbus.data.DataConst;
import com.neighbor.ulsanbus.network.ParseArrivalBus;
import com.neighbor.ulsanbus.util.UtilTimeManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import org.xmlpull.v1.XmlPullParserException;

public class UiTransferDetail extends Activity implements OnClickListener {
    private static final int RESULT_DATA_OK = 0;
    private static final int RESULT_SERVER_ERROR = -1;
    private RelativeLayout FirstRow;
    private RelativeLayout LastRow;
    private boolean bEmergency;
    private String departureName;
    String departureid;
    private String destinationName;
    String destinationid;
    private Handler handler = new C03321();
    Queue<TRANSFER> mDoAction;
    ParseArrivalBus mParse;
    ArrayList<RouteArrival> mRouteArrivalist;
    private String mStopCount;
    String mTransCount;
    TRANSFER mTransfer;
    ArrayList<TransitionBus> mTransition;
    private TransAdapter mTranslist;
    private String mTravelTime;
    private String mTravleLength;
    private SharedPreferences prefs;
    private ProgressBar progress;
    private String routeType;
    private TextView updateTimeText;
    private RelativeLayout updatedTime;

    /* renamed from: com.neighbor.ulsanbus.ui.UiTransferDetail$1 */
    class C03321 extends Handler {
        C03321() {
        }

        public void handleMessage(Message msg) {
            UiTransferDetail.this.progress.setVisibility(8);
            if (msg.what == 0) {
                UiTransferDetail.this.mTranslist.notifyDataSetChanged();
                UiTransferDetail.this.doAction();
                return;
            }
            UiTransferDetail.this.showError();
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiTransferDetail$2 */
    class C03332 implements DialogInterface.OnClickListener {
        C03332() {
        }

        public void onClick(DialogInterface dialog, int which) {
            UiTransferDetail.this.finish();
        }
    }

    class ArrivalTimeOfRoute extends Thread {
        ArrivalTimeOfRoute() {
        }

        public void run() {
            super.run();
            UiTransferDetail.this.mParse = new ParseArrivalBus();
            try {
                UiTransferDetail.this.mParse.setRouteBus(UiTransferDetail.this.mTransfer.routeid, UiTransferDetail.this.mTransfer.stopId);
                UiTransferDetail.this.mParse.parse();
                UiTransferDetail.this.mRouteArrivalist = (ArrayList) UiTransferDetail.this.mParse.get();
                UiTransferDetail.this.handler.sendEmptyMessage(0);
            } catch (XmlPullParserException e) {
                UiTransferDetail.this.handler.sendEmptyMessage(-1);
            } catch (IOException e2) {
                UiTransferDetail.this.handler.sendEmptyMessage(-1);
            }
        }
    }

    class TRANSFER {
        String routeid;
        String stopId;

        TRANSFER() {
        }
    }

    class TransAdapter extends BaseAdapter {
        String[] colorArray;
        Context mContext;
        int[] resIds;
        String[] txBusType;

        public TransAdapter(Context context) {
            this.mContext = context;
            TypedArray busType = this.mContext.getResources().obtainTypedArray(C0258R.array.bus_type);
            this.resIds = new int[busType.length()];
            for (int i = 0; i < this.resIds.length; i++) {
                this.resIds[i] = busType.getResourceId(i, 0);
            }
            busType.recycle();
            this.colorArray = this.mContext.getResources().getStringArray(C0258R.array.bus_type_color);
            this.txBusType = this.mContext.getResources().getStringArray(C0258R.array.stop_info_header_type);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            StringBuffer sb;
            View v = convertView;
            if (convertView == null) {
                v = ((LayoutInflater) this.mContext.getSystemService("layout_inflater")).inflate(C0258R.layout.transfer_detail_item, parent, false);
            }
            ImageView detailBusIcon = (ImageView) v.findViewById(C0258R.id.transdetail_bus_icon);
            TextView txtBusTypeNumber = (TextView) v.findViewById(C0258R.id.transdetail_bus_type_number);
            TextView txtDescription = (TextView) v.findViewById(C0258R.id.transdetail_route_description);
            TextView txtArrivaltime = (TextView) v.findViewById(C0258R.id.transdetail_arrival_time_txt);
            String arrivalTime = "";
            if (UiTransferDetail.this.bEmergency) {
                txtArrivaltime.setText("운행종료");
            } else {
                try {
                    String status = ((RouteArrival) UiTransferDetail.this.mRouteArrivalist.get(position)).getStatus();
                    sb = new StringBuffer();
                    if (status.equals("1")) {
                        sb.append(((RouteArrival) UiTransferDetail.this.mRouteArrivalist.get(position)).getRemainTime());
                        sb.insert(2, ":");
                        sb.append("출발 예정");
                        arrivalTime = sb.toString();
                    } else {
                        arrivalTime = UtilTimeManager.convertTimeArrival(Integer.parseInt(((RouteArrival) UiTransferDetail.this.mRouteArrivalist.get(position)).getRemainTime()), "arrival");
                    }
                    txtArrivaltime.setText(arrivalTime);
                } catch (Exception e) {
                    txtArrivaltime.setText("운행종료");
                }
            }
            TextView txtArrivalStopCnt = (TextView) v.findViewById(C0258R.id.transdetail_arrival_stopcnt_txt);
            TransitionBus item = (TransitionBus) UiTransferDetail.this.mTransition.get(position);
            Cursor cursor = this.mContext.getContentResolver().query(DataConst.CONTENT_ROUTE_URI, new String[]{"route_id", "route_no", "route_bus_type"}, "route_id=?", new String[]{item.getRouteId()}, null);
            if (cursor.moveToFirst()) {
                String comRouteId = cursor.getString(cursor.getColumnIndex("route_id"));
                String RouteNo = cursor.getString(cursor.getColumnIndex("route_no"));
                int intBusType = Integer.parseInt(cursor.getString(cursor.getColumnIndex("route_bus_type")));
                detailBusIcon.setImageResource(this.resIds[intBusType]);
                txtBusTypeNumber.setTextColor(Color.parseColor(this.colorArray[intBusType]));
                sb = new StringBuffer();
                sb.append(this.txBusType[intBusType]);
                sb.append(" ");
                sb.append(RouteNo);
                txtBusTypeNumber.setText(this.txBusType[intBusType] + " " + RouteNo);
            }
            cursor.close();
            StringBuffer descriptionbuffer = new StringBuffer();
            Cursor departurestopcursor = this.mContext.getContentResolver().query(DataConst.CONTENT_STOPS_URI, new String[]{"stop_name", "stop_id"}, "stop_id=?", new String[]{(String) item.getStoplist().get(0)}, null);
            if (departurestopcursor.moveToFirst()) {
                descriptionbuffer.append(departurestopcursor.getString(departurestopcursor.getColumnIndex("stop_name")));
            }
            departurestopcursor.close();
            if (Integer.parseInt(item.getWalkingLength()) > 0) {
                descriptionbuffer.append("( ");
                descriptionbuffer.append(item.getWalkingLength());
                descriptionbuffer.append(" 도보 ) ");
            }
            descriptionbuffer.append(" 승차 후 ");
            String stopId = (String) item.getStoplist().get(item.getStoplist().size() - 1);
            Cursor destinationstopcursor = this.mContext.getContentResolver().query(DataConst.CONTENT_STOPS_URI, new String[]{"stop_name", "stop_id"}, "stop_id=?", new String[]{stopId}, null);
            if (destinationstopcursor.moveToFirst()) {
                descriptionbuffer.append(destinationstopcursor.getString(destinationstopcursor.getColumnIndex("stop_name")));
            }
            destinationstopcursor.close();
            descriptionbuffer.append(" 하차 ( ");
            descriptionbuffer.append(item.getStoplist().size());
            descriptionbuffer.append("개 정류장 이동 )");
            txtDescription.setText(descriptionbuffer.toString());
            return v;
        }

        public int getCount() {
            return UiTransferDetail.this.mTransition.size();
        }

        public Object getItem(int position) {
            return UiTransferDetail.this.mTransition.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }
    }

    private void showError() {
        try {
            new Builder(getParent().getParent()).setTitle(getString(C0258R.string.error)).setMessage(getString(C0258R.string.server_conn_no_response)).setNeutralButton(getString(C0258R.string.ok), new C03332()).show();
        } catch (Exception e) {
        }
    }

    void init() {
        this.progress = (ProgressBar) findViewById(C0258R.id.transdetail_update_progress);
        this.progress.setVisibility(8);
        ((TextView) findViewById(C0258R.id.transdetail_departure_stop)).setText(this.departureName);
        ((TextView) findViewById(C0258R.id.transdetail_destination_stop)).setText(this.destinationName);
        this.FirstRow = (RelativeLayout) findViewById(C0258R.id.transdetail_first_row);
        this.FirstRow.setVisibility(8);
        this.LastRow = (RelativeLayout) findViewById(C0258R.id.transdetail_last_row);
        this.LastRow.setVisibility(8);
        TextView takenTime = (TextView) findViewById(C0258R.id.transdetail_take_time);
        String arrivalTime = UtilTimeManager.convertTimeArrival(Integer.parseInt(this.mTravelTime), " ");
        if (this.bEmergency) {
            takenTime.setText("정보 없음");
        } else {
            takenTime.setText(arrivalTime);
        }
        TextView transInfo = (TextView) findViewById(C0258R.id.transdetail_translation_info);
        StringBuffer sbStops = new StringBuffer();
        if (this.routeType.equals("1")) {
            sbStops.append(this.mStopCount);
            sbStops.append("개 정류장 ");
        } else {
            sbStops.append(this.mTransCount);
            sbStops.append("번 환승");
        }
        sbStops.append(UtilTimeManager.convertfromMeters(Double.parseDouble(this.mTravleLength)));
        sbStops.append(" km");
        transInfo.setText(sbStops.toString());
        ((Button) findViewById(C0258R.id.btn_transdetail_update)).setOnClickListener(this);
        ((TextView) findViewById(C0258R.id.transdetail_begin_text)).setText(this.departureName + " 출발");
        ((TextView) findViewById(C0258R.id.transdetail_end_text)).setText(this.destinationName + " 도착");
        this.updatedTime = (RelativeLayout) findViewById(C0258R.id.transdetail_update_layout);
        this.updateTimeText = (TextView) findViewById(C0258R.id.transdetail_update_text);
        ListView RouteList = (ListView) findViewById(C0258R.id.transdetail_route_list);
        this.mTranslist = new TransAdapter(this);
        RouteList.setAdapter(this.mTranslist);
        LinearLayout bg = (LinearLayout) findViewById(C0258R.id.transdetail_background);
        if (this.bEmergency) {
            bg.setBackgroundResource(C0258R.drawable.background_emer);
        } else {
            bg.setBackgroundResource(C0258R.drawable.background_nomal);
        }
        this.FirstRow.setVisibility(0);
        this.LastRow.setVisibility(0);
        this.updatedTime.setVisibility(0);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0258R.layout.transfer_detail);
        this.prefs = getSharedPreferences("PreStatus", 0);
        this.bEmergency = this.prefs.getBoolean(DataConst.KEY_PREF_EMER_MODE, false);
        this.departureid = this.prefs.getString(DataConst.KEY_PREF_FROM_STOP_ID, null);
        this.departureName = this.prefs.getString(DataConst.KEY_PREF_FROM_STOP_NAME, null);
        this.destinationid = this.prefs.getString(DataConst.KEY_PREF_TO_STOP_ID, null);
        this.destinationName = this.prefs.getString(DataConst.KEY_PREF_TO_STOP_NAME, null);
        this.mDoAction = new LinkedList();
        Intent intent = getIntent();
        this.routeType = intent.getStringExtra("RouteType");
        TRANSFER mTrans;
        TransitionBus bus;
        if (this.routeType.equals("1")) {
            ArrayList<String> mStopIdList = intent.getStringArrayListExtra("DirStopIDList");
            this.mTravelTime = intent.getStringExtra("DirTravelTime");
            this.mStopCount = intent.getStringExtra("DirStopCnt");
            this.mTravleLength = intent.getStringExtra("DirLength");
            mTrans = new TRANSFER();
            mTrans.routeid = intent.getStringExtra("DirRouteId");
            mTrans.stopId = (String) mStopIdList.get(0);
            this.mDoAction.offer(mTrans);
            this.mTransition = new ArrayList();
            bus = new TransitionBus();
            bus.setRouteId(mTrans.routeid);
            bus.setWalikingTime("0");
            bus.setWalkingLength("0");
            bus.mStoplist = mStopIdList;
            this.mTransition.add(bus);
        } else {
            RouteParcel routeParcel = (RouteParcel) getIntent().getExtras().getParcelable("routeInfo");
            this.mTransCount = routeParcel.getTransCnt();
            this.mTravleLength = routeParcel.getLength();
            this.mTravelTime = routeParcel.getTravelTime();
            ArrayList<ItemParcel> mTranParcel = (ArrayList) routeParcel.getTransferRouteList();
            this.mTransition = new ArrayList();
            Iterator it = mTranParcel.iterator();
            while (it.hasNext()) {
                ItemParcel item = (ItemParcel) it.next();
                bus = new TransitionBus();
                bus.setRouteId(item.getRouteId());
                bus.setWalikingTime(item.getWalkingTime());
                bus.setWalkingLength(item.getWalkingLength());
                bus.mStoplist = (ArrayList) item.getStoplist();
                mTrans = new TRANSFER();
                mTrans.routeid = bus.getRouteId();
                mTrans.stopId = (String) bus.getStoplist().get(0);
                this.mDoAction.offer(mTrans);
                bus.setRouteId(mTrans.routeid);
                this.mTransition.add(bus);
            }
        }
        init();
        doAction();
    }

    void doAction() {
        if (this.mDoAction.peek() != null) {
            this.mTransfer = (TRANSFER) this.mDoAction.poll();
            new ArrivalTimeOfRoute().start();
            return;
        }
        this.mTransfer.routeid = "";
        this.mTransfer.stopId = "";
    }

    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case C0258R.id.btn_transdetail_update:
                this.progress.setVisibility(0);
                Iterator it = this.mTransition.iterator();
                while (it.hasNext()) {
                    TransitionBus bus = (TransitionBus) it.next();
                    TRANSFER mTrans = new TRANSFER();
                    mTrans.routeid = bus.getRouteId();
                    mTrans.stopId = (String) bus.getStoplist().get(0);
                    this.mDoAction.offer(mTrans);
                }
                doAction();
                return;
            default:
                return;
        }
    }
}
