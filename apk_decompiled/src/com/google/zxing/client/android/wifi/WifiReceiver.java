package com.google.zxing.client.android.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.NetworkInfo.State;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.TextView;
import com.google.zxing.client.android.C0224R;

final class WifiReceiver extends BroadcastReceiver {
    private static final String TAG = WifiReceiver.class.getSimpleName();
    private final WifiManager mWifiManager;
    private final WifiActivity parent;
    private final TextView statusView;

    WifiReceiver(WifiManager wifiManager, WifiActivity wifiActivity, TextView statusView, String ssid) {
        this.parent = wifiActivity;
        this.statusView = statusView;
        this.mWifiManager = wifiManager;
    }

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.net.wifi.supplicant.STATE_CHANGE")) {
            handleChange((SupplicantState) intent.getParcelableExtra("newState"), intent.hasExtra("supplicantError"));
        } else if (intent.getAction().equals("android.net.wifi.STATE_CHANGE")) {
            handleNetworkStateChanged((NetworkInfo) intent.getParcelableExtra("networkInfo"));
        } else if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            for (NetworkInfo i : ((ConnectivityManager) this.parent.getSystemService("connectivity")).getAllNetworkInfo()) {
                if (i.getTypeName().contentEquals("WIFI")) {
                    State state = i.getState();
                    String ssid = this.mWifiManager.getConnectionInfo().getSSID();
                    if (state == State.CONNECTED && ssid != null) {
                        this.mWifiManager.saveConfiguration();
                        this.statusView.setText(this.parent.getString(C0224R.string.wifi_connected) + '\n' + ssid);
                        new Killer(this.parent).run();
                    }
                    if (state == State.DISCONNECTED) {
                        Log.d(TAG, "Got state Disconnected for ssid: " + ssid);
                        this.parent.gotError();
                    }
                }
            }
        }
    }

    private void handleNetworkStateChanged(NetworkInfo networkInfo) {
        if (networkInfo.getDetailedState() == DetailedState.FAILED) {
            Log.d(TAG, "Detailed Network state failed");
            this.parent.gotError();
        }
    }

    private void handleChange(SupplicantState state, boolean hasError) {
        if (hasError || state == SupplicantState.INACTIVE) {
            Log.d(TAG, "Found an error");
            this.parent.gotError();
        }
    }
}
