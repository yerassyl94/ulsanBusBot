package net.daum.mf.map.p000n.api;

import android.os.Process;
import android.util.Log;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import net.daum.android.map.MapBuildSettings;

/* renamed from: net.daum.mf.map.n.api.NativePlatformThread */
public class NativePlatformThread {
    private static HashMap<Integer, PlatformThread> ThreadMap = new HashMap();

    /* renamed from: net.daum.mf.map.n.api.NativePlatformThread$PlatformThread */
    private static class PlatformThread extends NativeThread {
        private AtomicBoolean isCanceled = new AtomicBoolean(false);
        private int sleepTimePerLoop;
        private int threadId;

        public PlatformThread(int threadId, int sleepTimePerLoop) {
            super(String.format("[MapEngine]PlatformThread%d", new Object[]{Integer.valueOf(threadId)}));
            this.threadId = threadId;
            this.sleepTimePerLoop = sleepTimePerLoop;
        }

        protected void nativeRun() {
            if (!MapBuildSettings.getInstance().isDistribution()) {
                Log.d(NativePlatformThread.class.getName(), super.getName() + " started");
            }
            Process.setThreadPriority(-2);
            while (!this.isCanceled.get()) {
                try {
                    try {
                        NativePlatformThread.onPlatformThreadLoopNative(this.threadId);
                        if (this.sleepTimePerLoop > 0) {
                            Thread.sleep((long) this.sleepTimePerLoop);
                        }
                    } catch (InterruptedException e) {
                    }
                } catch (RuntimeException e2) {
                    Log.e(NativePlatformThread.class.getName(), "" + e2.getMessage());
                    throw e2;
                } catch (Throwable th) {
                    synchronized (NativePlatformThread.ThreadMap) {
                        NativePlatformThread.ThreadMap.remove(new Integer(this.threadId));
                        if (!MapBuildSettings.getInstance().isDistribution()) {
                            Log.d(NativePlatformThread.class.getName(), super.getName() + " finished");
                        }
                    }
                }
            }
            synchronized (NativePlatformThread.ThreadMap) {
                NativePlatformThread.ThreadMap.remove(new Integer(this.threadId));
            }
            if (!MapBuildSettings.getInstance().isDistribution()) {
                Log.d(NativePlatformThread.class.getName(), super.getName() + " finished");
            }
        }

        public void setCancelled() {
            this.isCanceled.set(true);
        }
    }

    public static native void onPlatformThreadLoopNative(int i);

    static {
        NativeMapLibraryLoader.loadLibrary();
    }

    public static void startThread(int threadId, int sleepTimePerLoop) {
        synchronized (ThreadMap) {
            if (((PlatformThread) ThreadMap.get(new Integer(threadId))) == null) {
                PlatformThread thread = new PlatformThread(threadId, sleepTimePerLoop);
                ThreadMap.put(new Integer(threadId), thread);
                thread.start();
            }
        }
    }

    public static void cancelThread(int threadId) {
        synchronized (ThreadMap) {
            PlatformThread thread = (PlatformThread) ThreadMap.get(new Integer(threadId));
            if (thread != null) {
                if (!MapBuildSettings.getInstance().isDistribution()) {
                    Log.d(NativePlatformThread.class.getName(), "Cancelling " + thread.getName());
                }
                thread.setCancelled();
            }
        }
    }
}
