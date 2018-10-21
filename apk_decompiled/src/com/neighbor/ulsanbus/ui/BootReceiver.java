package com.neighbor.ulsanbus.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.Build;
import android.os.Build.VERSION;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import com.neighbor.ulsanbus.data.DataConst;
import com.neighbor.ulsanbus.util.UtilNetworkConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class BootReceiver extends BroadcastReceiver {
    private final String BOOT_COMPLETED_ACTION = "android.intent.action.BOOT_COMPLETED";

    class RegBus {
        String Hour;
        String Min;
        String RoutId;
        String StopId;
        String id;

        RegBus() {
        }
    }

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            ArrayList<RegBus> index = new ArrayList();
            Cursor cursor = context.getContentResolver().query(DataConst.CONTENT_ALARM_URI, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    RegBus item = new RegBus();
                    item.StopId = cursor.getString(cursor.getColumnIndex("favor_stopid"));
                    item.RoutId = cursor.getString(cursor.getColumnIndex("favor_routeid"));
                    item.Hour = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ALARM_TIME_HOUR));
                    item.Min = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ALARM_TIME_MINU));
                    item.id = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ALARM_ID));
                    index.add(item);
                } while (cursor.moveToNext());
                cursor.close();
                Iterator it = index.iterator();
                while (it.hasNext()) {
                    RegBus bus = (RegBus) it.next();
                    Calendar calendar = Calendar.getInstance();
                    Intent pendIntent = new Intent(context, AlarmReceiver.class);
                    pendIntent.putExtra("stop_id", bus.StopId);
                    pendIntent.putExtra("route_id", bus.RoutId);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(bus.id), pendIntent, 134217728);
                    Date date = new Date();
                    date.setHours(Integer.parseInt(bus.Hour));
                    date.setMinutes(Integer.parseInt(bus.Min));
                    calendar.setTime(date);
                    ((AlarmManager) context.getSystemService("alarm")).setRepeating(0, calendar.getTimeInMillis(), 86400000, pendingIntent);
                }
                return;
            }
            return;
        }
        if (ContextCompat.checkSelfPermission(context, "android.permission.READ_PHONE_STATE") == 0) {
            final Context context2 = context;
            new Thread() {
                public void run() {
                    try {
                        String devId = BootReceiver.this.getDeviceID((TelephonyManager) context2.getSystemService("phone"));
                        String deviceName = Build.MODEL;
                        String sdkver = VERSION.RELEASE;
                        String infoURL = DataConst.API_PRIVATE_INFO + devId + "&modelid=" + deviceName + "&osinfo=" + sdkver + "&appver=" + context2.getPackageManager().getPackageInfo(context2.getPackageName(), 0).versionName;
                        if (new UtilNetworkConnection().IsConnected(context2)) {
                            HttpURLConnection UC = (HttpURLConnection) new URL(infoURL).openConnection();
                            if (UC != null) {
                                UC.setConnectTimeout(20000);
                                UC.setUseCaches(false);
                                if (UC.getResponseCode() != 200) {
                                    UC.disconnect();
                                }
                            }
                        }
                    } catch (NameNotFoundException e) {
                        e.printStackTrace();
                    } catch (MalformedURLException e2) {
                        e2.printStackTrace();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
            }.start();
        }
    }

    String getDeviceID(TelephonyManager phonyManager) {
        String id = phonyManager.getDeviceId();
        if (id == null) {
            id = "";
        }
        switch (phonyManager.getPhoneType()) {
        }
        return id;
    }
}
