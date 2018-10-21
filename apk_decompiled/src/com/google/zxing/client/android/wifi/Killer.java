package com.google.zxing.client.android.wifi;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import com.google.zxing.client.android.C0224R;
import java.util.Timer;
import java.util.TimerTask;

final class Killer implements Runnable {
    private static final long DELAY_MS = 3000;
    private final Activity parent;

    Killer(Activity parent) {
        this.parent = parent;
    }

    void launchIntent(Intent intent) {
        if (intent != null) {
            intent.addFlags(524288);
            try {
                this.parent.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Builder builder = new Builder(this.parent);
                builder.setTitle(C0224R.string.app_name);
                builder.setMessage(C0224R.string.msg_intent_failed);
                builder.setPositiveButton(C0224R.string.button_ok, null);
                builder.show();
            }
        }
    }

    public void run() {
        final Handler handler = new Handler();
        new Timer().schedule(new TimerTask() {

            /* renamed from: com.google.zxing.client.android.wifi.Killer$1$1 */
            class C02421 implements Runnable {
                C02421() {
                }

                public void run() {
                    Killer.this.launchIntent(new Intent("android.intent.action.VIEW", Uri.parse("http://www.google.com/")));
                }
            }

            public void run() {
                handler.post(new C02421());
            }
        }, DELAY_MS);
    }
}
