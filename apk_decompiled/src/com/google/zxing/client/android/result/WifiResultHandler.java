package com.google.zxing.client.android.result;

import android.app.Activity;
import com.google.zxing.client.android.C0224R;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.WifiParsedResult;

public final class WifiResultHandler extends ResultHandler {
    private final Activity parent;

    public WifiResultHandler(Activity activity, ParsedResult result) {
        super(activity, result);
        this.parent = activity;
    }

    public int getButtonCount() {
        return 1;
    }

    public int getButtonText(int index) {
        if (index == 0) {
            return C0224R.string.button_wifi;
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public void handleButtonPress(int index) {
        WifiParsedResult wifiResult = (WifiParsedResult) getResult();
        if (index == 0) {
            wifiConnect(wifiResult);
        }
    }

    public CharSequence getDisplayContents() {
        WifiParsedResult wifiResult = (WifiParsedResult) getResult();
        StringBuffer contents = new StringBuffer(50);
        ParsedResult.maybeAppend(this.parent.getString(C0224R.string.wifi_ssid_label) + '\n' + wifiResult.getSsid(), contents);
        ParsedResult.maybeAppend(this.parent.getString(C0224R.string.wifi_type_label) + '\n' + wifiResult.getNetworkEncryption(), contents);
        return contents.toString();
    }

    public int getDisplayTitle() {
        return C0224R.string.result_wifi;
    }
}
