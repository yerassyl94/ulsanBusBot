package com.google.zxing.client.android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.client.android.camera.CameraManager;
import java.util.Vector;

public final class CaptureActivityHandler extends Handler {
    private static final String TAG = CaptureActivityHandler.class.getSimpleName();
    private final CaptureActivity activity;
    private final DecodeThread decodeThread;
    private State state = State.SUCCESS;

    private enum State {
        PREVIEW,
        SUCCESS,
        DONE
    }

    CaptureActivityHandler(CaptureActivity activity, Vector<BarcodeFormat> decodeFormats, String characterSet) {
        this.activity = activity;
        this.decodeThread = new DecodeThread(activity, decodeFormats, characterSet, new ViewfinderResultPointCallback(activity.getViewfinderView()));
        this.decodeThread.start();
        CameraManager.get().startPreview();
        restartPreviewAndDecode();
    }

    public void handleMessage(Message message) {
        if (message.what == C0224R.id.auto_focus) {
            if (this.state == State.PREVIEW) {
                CameraManager.get().requestAutoFocus(this, C0224R.id.auto_focus);
            }
        } else if (message.what == C0224R.id.restart_preview) {
            Log.d(TAG, "Got restart preview message");
            restartPreviewAndDecode();
        } else if (message.what == C0224R.id.decode_succeeded) {
            Bitmap barcode;
            Log.d(TAG, "Got decode succeeded message");
            this.state = State.SUCCESS;
            Bundle bundle = message.getData();
            if (bundle == null) {
                barcode = null;
            } else {
                barcode = (Bitmap) bundle.getParcelable(DecodeThread.BARCODE_BITMAP);
            }
            this.activity.handleDecode((Result) message.obj, barcode);
        } else if (message.what == C0224R.id.decode_failed) {
            this.state = State.PREVIEW;
            CameraManager.get().requestPreviewFrame(this.decodeThread.getHandler(), C0224R.id.decode);
        } else if (message.what == C0224R.id.return_scan_result) {
            Log.d(TAG, "Got return scan result message");
            this.activity.setResult(-1, (Intent) message.obj);
            this.activity.finish();
        } else if (message.what == C0224R.id.launch_product_query) {
            Log.d(TAG, "Got product query message");
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(message.obj));
            intent.addFlags(524288);
            this.activity.startActivity(intent);
        }
    }

    public void quitSynchronously() {
        this.state = State.DONE;
        CameraManager.get().stopPreview();
        Message.obtain(this.decodeThread.getHandler(), C0224R.id.quit).sendToTarget();
        try {
            this.decodeThread.join();
        } catch (InterruptedException e) {
        }
        removeMessages(C0224R.id.decode_succeeded);
        removeMessages(C0224R.id.decode_failed);
    }

    private void restartPreviewAndDecode() {
        if (this.state == State.SUCCESS) {
            this.state = State.PREVIEW;
            CameraManager.get().requestPreviewFrame(this.decodeThread.getHandler(), C0224R.id.decode);
            CameraManager.get().requestAutoFocus(this, C0224R.id.auto_focus);
            this.activity.drawViewfinder();
        }
    }
}
