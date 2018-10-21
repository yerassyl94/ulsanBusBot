package net.daum.mf.map.p000n.api.internal;

import net.daum.android.map.MapEngineManager;
import net.daum.mf.map.p000n.api.NativeBaseNetConnection;
import net.daum.mf.map.p000n.api.NativeWebClientLoopEntry;

/* renamed from: net.daum.mf.map.n.api.internal.NativeNetConnection4 */
public class NativeNetConnection4 extends NativeBaseNetConnection {
    protected void queueTask(Runnable task) {
        if (MapEngineManager.getInstance().isRunning()) {
            NativeWebClientLoopEntry.queueLoopEntry(task);
        }
    }
}
