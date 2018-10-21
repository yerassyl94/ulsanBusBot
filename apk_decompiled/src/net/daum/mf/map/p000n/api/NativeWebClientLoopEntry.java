package net.daum.mf.map.p000n.api;

import java.util.concurrent.ConcurrentLinkedQueue;

/* renamed from: net.daum.mf.map.n.api.NativeWebClientLoopEntry */
public class NativeWebClientLoopEntry {
    protected static ConcurrentLinkedQueue<Runnable> bufferQueue = new ConcurrentLinkedQueue();

    public void execute() {
        long start = System.nanoTime();
        do {
            Runnable r = (Runnable) bufferQueue.poll();
            if (r != null) {
                r.run();
            } else {
                return;
            }
        } while (System.nanoTime() - start <= 1000000);
    }

    public static void queueLoopEntry(Runnable r) {
        bufferQueue.add(r);
    }
}
