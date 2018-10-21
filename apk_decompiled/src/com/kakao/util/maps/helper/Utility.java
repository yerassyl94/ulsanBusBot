package com.kakao.util.maps.helper;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Utility {
    private static final String TAG = Utility.class.getCanonicalName();

    public static PackageInfo getPackageInfo(Context context, int flag) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), flag);
        } catch (NameNotFoundException e) {
            Log.w(TAG, "Unable to get PackageInfo", e);
            return null;
        }
    }

    public static String getKeyHash(Context context) {
        String str = null;
        PackageInfo packageInfo = getPackageInfo(context, 64);
        if (packageInfo != null) {
            Signature[] arr$ = packageInfo.signatures;
            int len$ = arr$.length;
            int i$ = 0;
            while (i$ < len$) {
                Signature signature = arr$[i$];
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    str = Base64.encodeToString(md.digest(), 2);
                    break;
                } catch (NoSuchAlgorithmException e) {
                    Log.w(TAG, "Unable to get MessageDigest. signature=" + signature, e);
                    i$++;
                }
            }
        }
        return str;
    }

    public static String getMetadata(Context context, String key) {
        String str = null;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128);
            if (!(ai == null || ai.metaData == null)) {
                str = ai.metaData.getString(key);
            }
        } catch (NameNotFoundException e) {
        }
        return str;
    }
}
