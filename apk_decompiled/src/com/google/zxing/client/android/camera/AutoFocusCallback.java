package com.google.zxing.client.android.camera;

import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;

final class AutoFocusCallback implements android.hardware.Camera.AutoFocusCallback {
    private static final long AUTOFOCUS_INTERVAL_MS = 1500;
    private static final String TAG = AutoFocusCallback.class.getSimpleName();
    private Handler autoFocusHandler;
    private int autoFocusMessage;

    AutoFocusCallback() {
    }

    void setHandler(Handler autoFocusHandler, int autoFocusMessage) {
        this.autoFocusHandler = autoFocusHandler;
        this.autoFocusMessage = autoFocusMessage;
    }

    public void onAutoFocus(boolean success, Camera camera) {
        if (this.autoFocusHandler != null) {
            this.autoFocusHandler.sendMessageDelayed(this.autoFocusHandler.obtainMessage(this.autoFocusMessage, Boolean.valueOf(success)), AUTOFOCUS_INTERVAL_MS);
            this.autoFocusHandler = null;
            return;
        }
        Log.d(TAG, "Got auto-focus callback, but no handler for it");
    }
}
