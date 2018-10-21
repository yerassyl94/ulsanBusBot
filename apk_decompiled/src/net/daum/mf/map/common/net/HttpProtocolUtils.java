package net.daum.mf.map.common.net;

import android.os.Build.VERSION;
import java.util.Locale;

public class HttpProtocolUtils {
    private static String DaumMapLibraryVersion = "1.0";
    private static String userAgent = null;

    public static String getUserAgent() {
        if (userAgent != null) {
            return userAgent;
        }
        userAgent = String.format("DaumMobileApp (Linux; U; Android %s; %s-%s) DaumMapLibrary/%s", new Object[]{VERSION.RELEASE, Locale.getDefault().getLanguage(), Locale.getDefault().getCountry(), DaumMapLibraryVersion});
        return userAgent;
    }
}
