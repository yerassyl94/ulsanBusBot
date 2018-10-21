package com.neighbor.ulsanbus.ui;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.neighbor.ulsanbus.C0258R;
import com.neighbor.ulsanbus.data.DataConst;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class UiAlarm extends Activity implements OnClickListener {
    private boolean bEmergency;
    boolean bUpdate = false;
    private DateAdapter mAdapter;
    AlarmContent mContent;
    private Context mContext;
    AlarmElement mElement;
    int mSavedAlramId = 0;
    private SharedPreferences prefs;
    private TimePicker timepicker;

    /* renamed from: com.neighbor.ulsanbus.ui.UiAlarm$1 */
    class C02691 implements OnItemClickListener {
        C02691() {
        }

        public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
            if (((String) UiAlarm.this.mAdapter.getItem(arg2)).equals("0")) {
                UiAlarm.this.mAdapter.setItem(arg2, "1");
            } else {
                UiAlarm.this.mAdapter.setItem(arg2, "0");
            }
            UiAlarm.this.mAdapter.notifyDataSetChanged();
        }
    }

    private class AlarmContent {
        ArrayList<String> mRepeatDay;
        String mRouteId;
        String mSetHour;
        String mSetMin;
        String mStopId;
        String mTimerId;

        private AlarmContent() {
            this.mTimerId = "1";
        }
    }

    private class AlarmElement {
        String RouteId;
        String StopId;

        private AlarmElement() {
        }
    }

    class DateAdapter extends BaseAdapter {
        private Context mContext;
        private String[] mWeek;

        public DateAdapter(Context mContext) {
            this.mContext = mContext;
            this.mWeek = mContext.getResources().getStringArray(C0258R.array.alarm_date_week);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = ((LayoutInflater) this.mContext.getSystemService("layout_inflater")).inflate(C0258R.layout.alarm_item, parent, false);
            }
            String item = (String) getItem(position);
            ((TextView) v.findViewById(C0258R.id.alarm_name)).setText(this.mWeek[position]);
            ImageView bRepeat = (ImageView) v.findViewById(C0258R.id.alarm_check);
            if (item.equals("1")) {
                bRepeat.setVisibility(0);
            } else {
                bRepeat.setVisibility(4);
            }
            return v;
        }

        public void setItem(int position, String value) {
            UiAlarm.this.mContent.mRepeatDay.set(position, value);
        }

        public Object getItem(int position) {
            return UiAlarm.this.mContent.mRepeatDay.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        public int getCount() {
            return UiAlarm.this.mContent.mRepeatDay.size();
        }
    }

    void init() {
        this.timepicker = (TimePicker) findViewById(C0258R.id.timepicker);
        this.timepicker.setDescendantFocusability(393216);
        this.timepicker.setCurrentHour(Integer.valueOf(Integer.parseInt(this.mContent.mSetHour)));
        this.timepicker.setCurrentMinute(Integer.valueOf(Integer.parseInt(this.mContent.mSetMin)));
        LinearLayout bg = (LinearLayout) findViewById(C0258R.id.alarm_background);
        if (this.bEmergency) {
            bg.setBackgroundResource(C0258R.drawable.background_emer);
        } else {
            bg.setBackgroundResource(C0258R.drawable.background_nomal);
        }
        ListView list = (ListView) findViewById(C0258R.id.alarm_repeat_list);
        this.mAdapter = new DateAdapter(this.mContext);
        list.setAdapter(this.mAdapter);
        list.setOnItemClickListener(new C02691());
        ((Button) findViewById(C0258R.id.btn_ok)).setOnClickListener(this);
        ((Button) findViewById(C0258R.id.btn_delete)).setOnClickListener(this);
    }

    AlarmContent getAlarmInfo(AlarmElement element) {
        try {
            Cursor cursor = getContentResolver().query(DataConst.CONTENT_ALARM_URI, null, "favor_stopid = " + element.StopId + " and " + "favor_routeid" + " = " + element.RouteId, null, null);
            if (cursor.moveToFirst()) {
                this.mContent = new AlarmContent();
                this.mContent.mRepeatDay = new ArrayList();
                this.mContent.mTimerId = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ALARM_ID));
                this.mContent.mStopId = cursor.getString(cursor.getColumnIndex("favor_stopid"));
                this.mContent.mRouteId = cursor.getString(cursor.getColumnIndex("favor_routeid"));
                this.mContent.mSetHour = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ALARM_TIME_HOUR));
                this.mContent.mSetMin = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ALARM_TIME_MINU));
                this.mContent.mRepeatDay.add(cursor.getString(cursor.getColumnIndex(DataConst.KEY_ALARM_DAY_0)));
                this.mContent.mRepeatDay.add(cursor.getString(cursor.getColumnIndex(DataConst.KEY_ALARM_DAY_1)));
                this.mContent.mRepeatDay.add(cursor.getString(cursor.getColumnIndex(DataConst.KEY_ALARM_DAY_2)));
                this.mContent.mRepeatDay.add(cursor.getString(cursor.getColumnIndex(DataConst.KEY_ALARM_DAY_3)));
                this.mContent.mRepeatDay.add(cursor.getString(cursor.getColumnIndex(DataConst.KEY_ALARM_DAY_4)));
                this.mContent.mRepeatDay.add(cursor.getString(cursor.getColumnIndex(DataConst.KEY_ALARM_DAY_5)));
                this.mContent.mRepeatDay.add(cursor.getString(cursor.getColumnIndex(DataConst.KEY_ALARM_DAY_6)));
                cursor.close();
                this.bUpdate = true;
                return this.mContent;
            }
            cursor.close();
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0258R.layout.alarm);
        this.prefs = getSharedPreferences("PreStatus", 0);
        this.bEmergency = this.prefs.getBoolean(DataConst.KEY_PREF_EMER_MODE, false);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.mElement = new AlarmElement();
            this.mElement.StopId = extras.getString("stop_id");
            this.mElement.RouteId = extras.getString("route_id");
        }
        this.mContext = this;
        if (getAlarmInfo(this.mElement) == null) {
            this.mContent = new AlarmContent();
            this.mContent.mStopId = this.mElement.StopId;
            this.mContent.mRouteId = this.mElement.RouteId;
            Calendar calendar = Calendar.getInstance();
            this.mContent.mSetHour = String.valueOf(calendar.get(11));
            this.mContent.mSetMin = String.valueOf(calendar.get(12));
            this.mContent.mRepeatDay = new ArrayList();
            for (int i = 0; i < 7; i++) {
                this.mContent.mRepeatDay.add("0");
            }
        }
        init();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0258R.id.btn_delete:
                deleteAlarm();
                this.mAdapter.notifyDataSetChanged();
                Toast.makeText(this.mContext, "알람이 해제 되었습니다.", 0).show();
                return;
            case C0258R.id.btn_ok:
                setAlarm(this.timepicker.getCurrentHour().intValue(), this.timepicker.getCurrentMinute().intValue());
                return;
            default:
                return;
        }
    }

    void deleteAlarm() {
        int i;
        if (this.bUpdate) {
            getContentResolver().delete(DataConst.CONTENT_ALARM_URI, "favor_routeid = " + this.mContent.mRouteId + " AND " + "favor_stopid" + " = " + this.mContent.mStopId, null);
            Intent pendIntent = new Intent(getBaseContext(), AlarmReceiver.class);
            pendIntent.putExtra("stop_id", this.mContent.mStopId);
            pendIntent.putExtra("route_id", this.mContent.mRouteId);
            ((AlarmManager) getSystemService("alarm")).cancel(PendingIntent.getBroadcast(getBaseContext(), Integer.parseInt(this.mContent.mTimerId), pendIntent, 134217728));
            this.mContent.mRepeatDay.clear();
            for (i = 0; i < 7; i++) {
                this.mContent.mRepeatDay.add("0");
            }
            Calendar calendar = Calendar.getInstance();
            this.mContent.mSetHour = String.valueOf(calendar.get(10));
            this.mContent.mSetMin = String.valueOf(calendar.get(12));
        } else {
            this.mContent.mRepeatDay.clear();
            for (i = 0; i < 7; i++) {
                this.mContent.mRepeatDay.add("0");
            }
        }
        if (this.bUpdate) {
            this.bUpdate = false;
        }
        this.timepicker.setCurrentHour(Integer.valueOf(Integer.parseInt(this.mContent.mSetHour)));
        this.timepicker.setCurrentMinute(Integer.valueOf(Integer.parseInt(this.mContent.mSetMin)));
    }

    void saveAlarm(boolean update, int hour, int minu, String id) {
        ContentValues addItem = new ContentValues();
        if (!update) {
            this.mContent.mTimerId = id;
        }
        addItem.put(DataConst.KEY_ALARM_ID, this.mContent.mTimerId);
        addItem.put("favor_routeid", this.mContent.mRouteId);
        addItem.put("favor_stopid", this.mContent.mStopId);
        addItem.put(DataConst.KEY_ALARM_TIME_HOUR, String.valueOf(hour));
        addItem.put(DataConst.KEY_ALARM_TIME_MINU, String.valueOf(minu));
        addItem.put(DataConst.KEY_ALARM_DAY_0, (String) this.mContent.mRepeatDay.get(0));
        addItem.put(DataConst.KEY_ALARM_DAY_1, (String) this.mContent.mRepeatDay.get(1));
        addItem.put(DataConst.KEY_ALARM_DAY_2, (String) this.mContent.mRepeatDay.get(2));
        addItem.put(DataConst.KEY_ALARM_DAY_3, (String) this.mContent.mRepeatDay.get(3));
        addItem.put(DataConst.KEY_ALARM_DAY_4, (String) this.mContent.mRepeatDay.get(4));
        addItem.put(DataConst.KEY_ALARM_DAY_5, (String) this.mContent.mRepeatDay.get(5));
        addItem.put(DataConst.KEY_ALARM_DAY_6, (String) this.mContent.mRepeatDay.get(6));
        if (update) {
            getContentResolver().update(DataConst.CONTENT_ALARM_URI, addItem, "favor_routeid = " + this.mContent.mRouteId + " AND " + "favor_stopid" + " = " + this.mContent.mStopId, null);
        } else {
            getContentResolver().insert(DataConst.CONTENT_ALARM_URI, addItem);
        }
    }

    void setAlarm(int hour, int minu) {
        int checkRepeat = 0;
        ArrayList<String> timerid = new ArrayList();
        String id = "1";
        for (int i = 0; i < 7; i++) {
            if (((String) this.mContent.mRepeatDay.get(i)).equals("1")) {
                checkRepeat++;
            }
        }
        if (checkRepeat == 0) {
            Toast.makeText(this.mContext, "반복 요일을 선택 하여 주세요.", 0).show();
            return;
        }
        if (this.bUpdate) {
            saveAlarm(true, hour, minu, this.mContent.mTimerId);
        } else {
            Cursor totalAlarm = this.mContext.getContentResolver().query(DataConst.CONTENT_ALARM_URI, new String[]{DataConst.KEY_ALARM_ID}, null, null, null);
            try {
                if (totalAlarm.moveToFirst()) {
                    do {
                        timerid.add(totalAlarm.getString(totalAlarm.getColumnIndex(DataConst.KEY_ALARM_ID)));
                    } while (totalAlarm.moveToNext());
                    totalAlarm.close();
                }
            } catch (Exception e) {
                Log.d("UiAlarm", "" + e.getMessage());
            }
            if (timerid.size() >= 2) {
                Toast.makeText(this.mContext, "알람은 2개 이상 등록 할 수 없습니다.\n 등록된 알람을 삭제 하시고 등록 하시길 바랍니다.", 0).show();
                return;
            }
            if (timerid.size() > 0) {
                id = ((String) timerid.get(0)).equals("1") ? "2" : "1";
            }
            saveAlarm(false, hour, minu, id);
        }
        Calendar calendar = Calendar.getInstance();
        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        intent.putExtra("stop_id", this.mContent.mStopId);
        intent.putExtra("route_id", this.mContent.mRouteId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), Integer.parseInt(this.mContent.mTimerId), intent, 134217728);
        Date date = new Date();
        date.setHours(hour);
        date.setMinutes(minu);
        calendar.setTime(date);
        ((AlarmManager) getSystemService("alarm")).setRepeating(0, calendar.getTimeInMillis(), 86400000, pendingIntent);
        Toast.makeText(this.mContext, "알람이 설정 되었습니다.", 0).show();
        finish();
    }
}
