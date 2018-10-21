package com.google.zxing.client.android;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

final class InactivityTimer {
    private static final int INACTIVITY_DELAY_SECONDS = 300;
    private final Activity activity;
    private ScheduledFuture<?> inactivityFuture = null;
    private final ScheduledExecutorService inactivityTimer = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory());
    private final PowerStatusReceiver powerStatusReceiver = new PowerStatusReceiver();

    private static final class DaemonThreadFactory implements ThreadFactory {
        private DaemonThreadFactory() {
        }

        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        }
    }

    private final class PowerStatusReceiver extends BroadcastReceiver {
        private PowerStatusReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (!"android.intent.action.BATTERY_CHANGED".equals(intent.getAction())) {
                return;
            }
            if (intent.getIntExtra("plugged", -1) == 0) {
                InactivityTimer.this.onActivity();
            } else {
                InactivityTimer.this.cancel();
            }
        }
    }

    InactivityTimer(Activity activity) {
        this.activity = activity;
        onActivity();
    }

    void onActivity() {
        cancel();
        if (!this.inactivityTimer.isShutdown()) {
            try {
                this.inactivityFuture = this.inactivityTimer.schedule(new FinishListener(this.activity), 300, TimeUnit.SECONDS);
            } catch (RejectedExecutionException e) {
            }
        }
    }

    public void onPause() {
        this.activity.unregisterReceiver(this.powerStatusReceiver);
    }

    public void onResume() {
        this.activity.registerReceiver(this.powerStatusReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
    }

    private void cancel() {
        if (this.inactivityFuture != null) {
            this.inactivityFuture.cancel(true);
            this.inactivityFuture = null;
        }
    }

    void shutdown() {
        cancel();
        this.inactivityTimer.shutdown();
    }
}
