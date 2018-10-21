package net.daum.android.map.util;

import android.util.Log;
import java.io.UnsupportedEncodingException;

public class XorEncryptionUtils {
    private static final String LOG_TAG = "XorEncryptionUtils";

    public static byte[] encryptString(String in, String key) {
        try {
            byte[] encryptedBytes = in.getBytes("UTF-8");
            int len = encryptedBytes.length;
            byte[] bArr = new byte[len];
            byte[] keyBytes = key.getBytes("UTF-8");
            int keyLength = key.length();
            for (int i = 0; i < len; i++) {
                bArr[i] = (byte) (encryptedBytes[i] ^ keyBytes[i % keyLength]);
            }
            return bArr;
        } catch (UnsupportedEncodingException e) {
            Log.e(LOG_TAG, "Failed to encrypt UnsupportedEncodingException");
            return null;
        }
    }

    public static byte[] decryptString(byte[] in, String key) {
        try {
            int len = in.length;
            byte[] bArr = new byte[len];
            byte[] keyBytes = key.getBytes("UTF-8");
            int keyLength = key.length();
            for (int i = 0; i < len; i++) {
                bArr[i] = (byte) (in[i] ^ keyBytes[i % keyLength]);
            }
            return bArr;
        } catch (UnsupportedEncodingException e) {
            Log.e(LOG_TAG, "Failed to encrypt UnsupportedEncodingException");
            return null;
        }
    }
}
