package com.google.zxing.client.android.wifi;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.google.zxing.client.android.C0224R;
import com.google.zxing.client.android.Intents.WifiConnect;
import java.util.regex.Pattern;

public final class WifiActivity extends Activity {
    private static final int FAILURE_NO_NETWORK_ID = -1;
    private static final Pattern HEX_DIGITS_64 = Pattern.compile("[0-9A-Fa-f]{64}");
    private static final int MAX_ERROR_COUNT = 3;
    private static final String TAG = WifiActivity.class.getSimpleName();
    private int errorCount;
    private IntentFilter mWifiStateFilter;
    private int networkId;
    private boolean receiverRegistered;
    private TextView statusView;
    private WifiManager wifiManager;
    private WifiReceiver wifiReceiver;

    public enum NetworkType {
        NETWORK_WEP,
        NETWORK_WPA,
        NETWORK_NOPASS,
        NETWORK_INVALID
    }

    void gotError() {
        this.errorCount++;
        Log.d(TAG, "Encountered another error.  Errorcount = " + this.errorCount);
        if (this.errorCount > 3) {
            this.errorCount = 0;
            doError(C0224R.string.wifi_connect_failed);
        }
    }

    private int changeNetwork(NetworkSetting setting) {
        if (setting.getSsid() == null || setting.getSsid().length() == 0) {
            return doError(C0224R.string.wifi_ssid_missing);
        }
        if (setting.getNetworkType() == NetworkType.NETWORK_INVALID) {
            return doError(C0224R.string.wifi_type_incorrect);
        }
        if (setting.getPassword() == null || setting.getPassword().length() == 0 || setting.getNetworkType() == null || setting.getNetworkType() == NetworkType.NETWORK_NOPASS) {
            return changeNetworkUnEncrypted(setting);
        }
        if (setting.getNetworkType() == NetworkType.NETWORK_WPA) {
            return changeNetworkWPA(setting);
        }
        return changeNetworkWEP(setting);
    }

    private int doError(int resource_string) {
        this.statusView.setText(resource_string);
        this.wifiManager.disconnect();
        if (this.networkId > 0) {
            this.wifiManager.removeNetwork(this.networkId);
            this.networkId = -1;
        }
        if (this.receiverRegistered) {
            unregisterReceiver(this.wifiReceiver);
            this.receiverRegistered = false;
        }
        return -1;
    }

    private WifiConfiguration changeNetworkCommon(NetworkSetting input) {
        this.statusView.setText(C0224R.string.wifi_creating_network);
        Log.d(TAG, "Adding new configuration: \nSSID: " + input.getSsid() + "\nType: " + input.getNetworkType());
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = NetworkUtil.convertToQuotedString(input.getSsid());
        config.hiddenSSID = true;
        return config;
    }

    private int requestNetworkChange(WifiConfiguration config) {
        this.statusView.setText(C0224R.string.wifi_changing_network);
        return updateNetwork(config, false);
    }

    private int changeNetworkWEP(NetworkSetting input) {
        WifiConfiguration config = changeNetworkCommon(input);
        String pass = input.getPassword();
        if (NetworkUtil.isHexWepKey(pass)) {
            config.wepKeys[0] = pass;
        } else {
            config.wepKeys[0] = NetworkUtil.convertToQuotedString(pass);
        }
        config.allowedAuthAlgorithms.set(1);
        config.allowedGroupCiphers.set(3);
        config.allowedGroupCiphers.set(2);
        config.allowedGroupCiphers.set(0);
        config.allowedGroupCiphers.set(1);
        config.allowedKeyManagement.set(0);
        config.wepTxKeyIndex = 0;
        return requestNetworkChange(config);
    }

    private int changeNetworkWPA(NetworkSetting input) {
        WifiConfiguration config = changeNetworkCommon(input);
        String pass = input.getPassword();
        if (HEX_DIGITS_64.matcher(pass).matches()) {
            Log.d(TAG, "A 64 bit hex password entered.");
            config.preSharedKey = pass;
        } else {
            Log.d(TAG, "A normal password entered: I am quoting it.");
            config.preSharedKey = NetworkUtil.convertToQuotedString(pass);
        }
        config.allowedAuthAlgorithms.set(0);
        config.allowedProtocols.set(0);
        config.allowedKeyManagement.set(1);
        config.allowedGroupCiphers.set(2);
        config.allowedGroupCiphers.set(3);
        config.allowedProtocols.set(1);
        return requestNetworkChange(config);
    }

    private int changeNetworkUnEncrypted(NetworkSetting input) {
        Log.d(TAG, "Empty password prompting a simple account setting");
        WifiConfiguration config = changeNetworkCommon(input);
        config.wepKeys[0] = "";
        config.allowedKeyManagement.set(0);
        config.wepTxKeyIndex = 0;
        return requestNetworkChange(config);
    }

    private WifiConfiguration findNetworkInExistingConfig(String ssid) {
        for (WifiConfiguration existingConfig : this.wifiManager.getConfiguredNetworks()) {
            if (existingConfig.SSID.equals(ssid)) {
                return existingConfig;
            }
        }
        return null;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null || !intent.getAction().equals(WifiConnect.ACTION)) {
            finish();
            return;
        }
        NetworkType networkT;
        String ssid = intent.getStringExtra(WifiConnect.SSID);
        String password = intent.getStringExtra(WifiConnect.PASSWORD);
        String networkType = intent.getStringExtra(WifiConnect.TYPE);
        setContentView(C0224R.layout.network);
        this.statusView = (TextView) findViewById(C0224R.id.networkStatus);
        if ("WPA".equals(networkType)) {
            networkT = NetworkType.NETWORK_WPA;
        } else if ("WEP".equals(networkType)) {
            networkT = NetworkType.NETWORK_WEP;
        } else if ("nopass".equals(networkType)) {
            networkT = NetworkType.NETWORK_NOPASS;
        } else {
            networkT = NetworkType.NETWORK_INVALID;
        }
        this.wifiManager = (WifiManager) getApplicationContext().getSystemService("wifi");
        this.wifiManager.setWifiEnabled(true);
        this.wifiReceiver = new WifiReceiver(this.wifiManager, this, this.statusView, ssid);
        this.mWifiStateFilter = new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED");
        this.mWifiStateFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        this.mWifiStateFilter.addAction("android.net.wifi.supplicant.STATE_CHANGE");
        this.mWifiStateFilter.addAction("android.net.wifi.STATE_CHANGE");
        registerReceiver(this.wifiReceiver, this.mWifiStateFilter);
        this.receiverRegistered = true;
        if (password == null) {
            password = "";
        }
        Log.d(TAG, "Adding new configuration: \nSSID: " + ssid + "Type: " + networkT);
        changeNetwork(new NetworkSetting(ssid, password, networkT));
    }

    public void onPause() {
        super.onPause();
        if (this.receiverRegistered) {
            unregisterReceiver(this.wifiReceiver);
            this.receiverRegistered = false;
        }
    }

    public void onResume() {
        super.onResume();
        if (this.wifiReceiver != null && this.mWifiStateFilter != null && !this.receiverRegistered) {
            registerReceiver(this.wifiReceiver, this.mWifiStateFilter);
            this.receiverRegistered = true;
        }
    }

    protected void onDestroy() {
        if (this.wifiReceiver != null) {
            if (this.receiverRegistered) {
                unregisterReceiver(this.wifiReceiver);
                this.receiverRegistered = false;
            }
            this.wifiReceiver = null;
        }
        super.onDestroy();
    }

    private int updateNetwork(WifiConfiguration config, boolean disableOthers) {
        WifiConfiguration found = findNetworkInExistingConfig(config.SSID);
        this.wifiManager.disconnect();
        if (found == null) {
            this.statusView.setText(C0224R.string.wifi_creating_network);
        } else {
            this.statusView.setText(C0224R.string.wifi_modifying_network);
            Log.d(TAG, "Removing network " + found.networkId);
            this.wifiManager.removeNetwork(found.networkId);
            this.wifiManager.saveConfiguration();
        }
        this.networkId = this.wifiManager.addNetwork(config);
        Log.d(TAG, "Inserted/Modified network " + this.networkId);
        if (this.networkId < 0) {
            return -1;
        }
        if (this.wifiManager.enableNetwork(this.networkId, disableOthers)) {
            this.errorCount = 0;
            this.wifiManager.reassociate();
            return this.networkId;
        }
        this.networkId = -1;
        return -1;
    }
}
