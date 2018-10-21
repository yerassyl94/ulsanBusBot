package com.google.zxing.client.android;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPointCallback;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

final class DecodeThread extends Thread {
    public static final String BARCODE_BITMAP = "barcode_bitmap";
    private final CaptureActivity activity;
    private Handler handler;
    private final CountDownLatch handlerInitLatch = new CountDownLatch(1);
    private final Hashtable<DecodeHintType, Object> hints = new Hashtable(3);

    DecodeThread(CaptureActivity activity, Vector<BarcodeFormat> decodeFormats, String characterSet, ResultPointCallback resultPointCallback) {
        this.activity = activity;
        if (decodeFormats == null || decodeFormats.isEmpty()) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
            decodeFormats = new Vector();
            if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_1D, true)) {
                decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
            }
            if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_QR, true)) {
                decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
            }
            if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_DATA_MATRIX, true)) {
                decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
            }
        }
        this.hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        if (characterSet != null) {
            this.hints.put(DecodeHintType.CHARACTER_SET, characterSet);
        }
        this.hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);
    }

    Handler getHandler() {
        try {
            this.handlerInitLatch.await();
        } catch (InterruptedException e) {
        }
        return this.handler;
    }

    public void run() {
        Looper.prepare();
        this.handler = new DecodeHandler(this.activity, this.hints);
        this.handlerInitLatch.countDown();
        Looper.loop();
    }
}
