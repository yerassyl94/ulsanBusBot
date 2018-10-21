package net.daum.mf.map.task;

import java.util.ArrayList;

public class WaitQueueManager {
    private static WaitQueueManager instance = new WaitQueueManager();
    private ArrayList<Runnable> eventQueue = new ArrayList();

    public static WaitQueueManager getInstance() {
        return instance;
    }

    public void queueToWaitQueue(Runnable task) {
        queueEvent(task);
    }

    public void queueEvent(Runnable r) {
        synchronized (this) {
            this.eventQueue.add(r);
        }
    }

    private Runnable getEvent() {
        synchronized (this) {
            if (this.eventQueue.size() > 0) {
                Runnable runnable = (Runnable) this.eventQueue.remove(0);
                return runnable;
            }
            return null;
        }
    }

    public void onLoop() {
        Runnable r = getEvent();
        if (r != null) {
            r.run();
        }
    }
}
