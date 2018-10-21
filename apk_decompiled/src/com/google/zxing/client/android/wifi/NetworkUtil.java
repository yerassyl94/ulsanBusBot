package com.google.zxing.client.android.wifi;

import android.text.TextUtils;
import java.util.regex.Pattern;

final class NetworkUtil {
    private static final Pattern HEX_DIGITS = Pattern.compile("[0-9A-Fa-f]+");

    private NetworkUtil() {
    }

    static String convertToQuotedString(String string) {
        if (string == null) {
            return null;
        }
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        int lastPos = string.length() - 1;
        if (lastPos >= 0) {
            return (string.charAt(0) == '\"' && string.charAt(lastPos) == '\"') ? string : '\"' + string + '\"';
        } else {
            return string;
        }
    }

    static boolean isHexWepKey(CharSequence wepKey) {
        if (wepKey == null) {
            return false;
        }
        int length = wepKey.length();
        if ((length == 10 || length == 26 || length == 58) && HEX_DIGITS.matcher(wepKey).matches()) {
            return true;
        }
        return false;
    }
}
