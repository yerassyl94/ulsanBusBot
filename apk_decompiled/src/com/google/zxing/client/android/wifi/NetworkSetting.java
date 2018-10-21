package com.google.zxing.client.android.wifi;

import com.google.zxing.client.android.wifi.WifiActivity.NetworkType;

final class NetworkSetting {
    private final NetworkType networkType;
    private final String password;
    private final String ssid;

    NetworkSetting(String ssid, String password, NetworkType networkType) {
        this.ssid = ssid;
        this.password = password;
        this.networkType = networkType;
    }

    NetworkType getNetworkType() {
        return this.networkType;
    }

    String getPassword() {
        return this.password;
    }

    String getSsid() {
        return this.ssid;
    }
}
