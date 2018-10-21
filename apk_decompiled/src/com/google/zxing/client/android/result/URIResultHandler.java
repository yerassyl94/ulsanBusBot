package com.google.zxing.client.android.result;

import android.app.Activity;
import com.google.zxing.client.android.C0224R;
import com.google.zxing.client.android.LocaleManager;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.URIParsedResult;

public final class URIResultHandler extends ResultHandler {
    private static final String[] SECURE_PROTOCOLS = new String[]{"otpauth:"};
    private static final int[] buttons = new int[]{C0224R.string.button_open_browser, C0224R.string.button_share_by_email, C0224R.string.button_share_by_sms, C0224R.string.button_search_book_contents};

    public URIResultHandler(Activity activity, ParsedResult result) {
        super(activity, result);
    }

    public int getButtonCount() {
        if (LocaleManager.isBookSearchUrl(((URIParsedResult) getResult()).getURI())) {
            return buttons.length;
        }
        return buttons.length - 1;
    }

    public int getButtonText(int index) {
        return buttons[index];
    }

    public void handleButtonPress(int index) {
        String uri = ((URIParsedResult) getResult()).getURI();
        switch (index) {
            case 0:
                openURL(uri);
                return;
            case 1:
                shareByEmail(uri);
                return;
            case 2:
                shareBySMS(uri);
                return;
            case 3:
                searchBookContents(uri);
                return;
            default:
                return;
        }
    }

    public int getDisplayTitle() {
        return C0224R.string.result_uri;
    }

    public boolean areContentsSecure() {
        String uri = ((URIParsedResult) getResult()).getURI().toLowerCase();
        for (String secure : SECURE_PROTOCOLS) {
            if (uri.startsWith(secure)) {
                return true;
            }
        }
        return false;
    }
}
