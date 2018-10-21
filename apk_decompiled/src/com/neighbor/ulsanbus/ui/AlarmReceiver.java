package com.neighbor.ulsanbus.ui;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import com.neighbor.ulsanbus.C0258R;
import com.neighbor.ulsanbus.RouteArrival;
import com.neighbor.ulsanbus.data.DataConst;
import com.neighbor.ulsanbus.network.ParseArrivalBus;
import com.neighbor.ulsanbus.util.UtilTimeManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import org.xmlpull.v1.XmlPullParserException;

public class AlarmReceiver extends BroadcastReceiver {
    private static final int CONNECT_ERR = -2;
    private static final int NOTIFICATION = 1234;
    private static final int PARSE_ERR = -1;
    private static final int RESULT_0K = 0;
    private Handler handler = new C02601();
    private AlarmContent mAlarm;
    private ArrayList<RouteArrival> mArrivalList;
    private String[] mBusType;
    private Context mContext;
    ParseArrivalBus mParse;
    private RouteArrival mRouteArrival;
    private Vibrator m_vibrator = null;
    private NotificationManager nm = null;

    /* renamed from: com.neighbor.ulsanbus.ui.AlarmReceiver$1 */
    class C02601 extends Handler {
        C02601() {
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                StringBuffer sb;
                AlarmReceiver.this.m_vibrator = (Vibrator) AlarmReceiver.this.mContext.getSystemService("vibrator");
                AlarmReceiver.this.m_vibrator.vibrate(500);
                AlarmReceiver.this.nm = (NotificationManager) AlarmReceiver.this.mContext.getSystemService("notification");
                String remainTime = "";
                if (AlarmReceiver.this.mArrivalList.size() > 0) {
                    RouteArrival BusInfo = (RouteArrival) AlarmReceiver.this.mArrivalList.get(0);
                    if (BusInfo.getStatus().equals("1")) {
                        sb = new StringBuffer();
                        sb.append(BusInfo.getRemainTime());
                        sb.insert(2, ":");
                        sb.append(" 출발 ");
                        remainTime = sb.toString();
                    } else {
                        remainTime = UtilTimeManager.convertTimeArrival(Integer.parseInt(BusInfo.getRemainTime()), "arrival");
                    }
                } else {
                    remainTime = "운행 종료 ";
                }
                String tickerText = remainTime;
                Notification notification = new Notification(C0258R.drawable.android_icon, tickerText, System.currentTimeMillis());
                sb = new StringBuffer();
                sb.append(AlarmReceiver.this.mBusType[Integer.parseInt(AlarmReceiver.this.mAlarm.mRouteType)]);
                sb.append(AlarmReceiver.this.mAlarm.mRouteNo);
                sb.append("[");
                sb.append(AlarmReceiver.this.mAlarm.mStopNm);
                sb.append("]");
                CharSequence contentTitle = sb.toString();
                String contentText = remainTime;
                Intent toLaunch = new Intent();
                toLaunch.addFlags(872415232);
                toLaunch.setComponent(new ComponentName("com.neighbor.ulsanbus", "com.neighbor.ulsanbus.ui.UiLoading"));
                AlarmReceiver.this.nm.notify(AlarmReceiver.NOTIFICATION, new Builder(AlarmReceiver.this.mContext).setSmallIcon(C0258R.drawable.android_icon).setTicker(tickerText).setContentTitle(contentTitle).setContentText(contentText).setContentIntent(PendingIntent.getActivity(AlarmReceiver.this.mContext, 0, toLaunch, 0)).setAutoCancel(true).setSound(RingtoneManager.getDefaultUri(2)).build());
            }
        }
    }

    private class AlarmContent {
        ArrayList<String> mRepeatDay;
        String mRouteId;
        String mRouteNo;
        String mRouteType;
        String mSetHour;
        String mSetMin;
        String mStopId;
        String mStopNm;
        String mTimerId;

        private AlarmContent() {
        }
    }

    private class ReqBusArrival extends Thread {
        private ReqBusArrival() {
        }

        public void run() {
            AlarmReceiver.this.mParse = new ParseArrivalBus();
            try {
                AlarmReceiver.this.mParse.setRouteBus(AlarmReceiver.this.mAlarm.mRouteId, AlarmReceiver.this.mAlarm.mStopId);
                AlarmReceiver.this.mParse.parse();
                AlarmReceiver.this.mArrivalList = (ArrayList) AlarmReceiver.this.mParse.get();
                AlarmReceiver.this.handler.sendEmptyMessage(0);
            } catch (XmlPullParserException e) {
                AlarmReceiver.this.handler.sendEmptyMessage(-1);
            } catch (IOException e2) {
                AlarmReceiver.this.handler.sendEmptyMessage(-2);
            }
        }
    }

    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        this.mAlarm = new AlarmContent();
        this.mAlarm.mRepeatDay = new ArrayList();
        this.mBusType = this.mContext.getResources().getStringArray(C0258R.array.stop_info_header_type);
        this.mAlarm.mStopId = intent.getStringExtra("stop_id");
        this.mAlarm.mRouteId = intent.getStringExtra("route_id");
        try {
            Cursor cursor = context.getContentResolver().query(DataConst.CONTENT_ALARM_URI, null, "favor_stopid = " + this.mAlarm.mStopId + " AND " + "favor_routeid" + " = " + this.mAlarm.mRouteId, null, null);
            if (cursor.moveToFirst()) {
                this.mAlarm.mTimerId = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ALARM_ID));
                this.mAlarm.mStopId = cursor.getString(cursor.getColumnIndex("favor_stopid"));
                this.mAlarm.mRouteId = cursor.getString(cursor.getColumnIndex("favor_routeid"));
                this.mAlarm.mSetHour = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ALARM_TIME_HOUR));
                this.mAlarm.mSetMin = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ALARM_TIME_MINU));
                this.mAlarm.mRepeatDay.add(cursor.getString(cursor.getColumnIndex(DataConst.KEY_ALARM_DAY_0)));
                this.mAlarm.mRepeatDay.add(cursor.getString(cursor.getColumnIndex(DataConst.KEY_ALARM_DAY_1)));
                this.mAlarm.mRepeatDay.add(cursor.getString(cursor.getColumnIndex(DataConst.KEY_ALARM_DAY_2)));
                this.mAlarm.mRepeatDay.add(cursor.getString(cursor.getColumnIndex(DataConst.KEY_ALARM_DAY_3)));
                this.mAlarm.mRepeatDay.add(cursor.getString(cursor.getColumnIndex(DataConst.KEY_ALARM_DAY_4)));
                this.mAlarm.mRepeatDay.add(cursor.getString(cursor.getColumnIndex(DataConst.KEY_ALARM_DAY_5)));
                this.mAlarm.mRepeatDay.add(cursor.getString(cursor.getColumnIndex(DataConst.KEY_ALARM_DAY_6)));
            }
            cursor.close();
            Cursor rcv = context.getContentResolver().query(DataConst.CONTENT_ROUTE_URI, new String[]{"route_no", "route_bus_type"}, "route_id = " + this.mAlarm.mRouteId, null, null);
            if (rcv.moveToFirst()) {
                this.mAlarm.mRouteNo = rcv.getString(rcv.getColumnIndex("route_no"));
                this.mAlarm.mRouteType = rcv.getString(rcv.getColumnIndex("route_bus_type"));
            }
            rcv.close();
            Cursor scv = context.getContentResolver().query(DataConst.CONTENT_STOPS_URI, new String[]{"stop_name"}, "stop_id = " + this.mAlarm.mStopId, null, null);
            if (scv.moveToFirst()) {
                this.mAlarm.mStopNm = scv.getString(scv.getColumnIndex("stop_name"));
            }
            scv.close();
            if (((String) this.mAlarm.mRepeatDay.get(Calendar.getInstance().get(7) - 1)).equals("1")) {
                new ReqBusArrival().start();
            }
        } catch (Exception e) {
        }
    }
}
