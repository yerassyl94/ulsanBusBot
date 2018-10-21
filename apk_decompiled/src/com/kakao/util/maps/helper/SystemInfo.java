package com.kakao.util.maps.helper;

import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import java.util.Locale;

public class SystemInfo {
    private static final String COUNTRY_CODE = Locale.getDefault().getCountry().toUpperCase();
    private static final String DEVICE_MODEL = Build.MODEL.replaceAll("\\s", "-").toUpperCase();
    private static String KA_HEADER;
    private static final String LANGUAGE = Locale.getDefault().getLanguage().toLowerCase();
    private static final int OS_VERSION = VERSION.SDK_INT;

    public static void initialize(Context context) {
        if (KA_HEADER == null) {
            KA_HEADER = "sdk/1.3.0 mapSdk/1.3.1.0 os/android-" + OS_VERSION + " " + CommonProtocol.KA_LANG_KEY + LANGUAGE + "-" + COUNTRY_CODE + " " + CommonProtocol.KA_KEY_HASH + Utility.getKeyHash(context) + " " + CommonProtocol.KA_DEVICE_KEY + DEVICE_MODEL;
        }
    }

    public static String getKAHeader() {
        return KA_HEADER;
    }
}
