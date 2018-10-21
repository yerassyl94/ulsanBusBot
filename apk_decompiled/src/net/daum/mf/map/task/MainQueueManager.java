package net.daum.mf.map.task;

import net.daum.mf.map.common.MapThreadScheduling;

public final class MainQueueManager {
    private static final MainQueueManager instance = new MainQueueManager();
    protected MainQueueHandler mainQueueHandler;

    public static MainQueueManager getInstance() {
        return instance;
    }

    private MainQueueManager() {
    }

    public void setMainQueueHandler(MainQueueHandler handler) {
        this.mainQueueHandler = handler;
    }

    public void queueToMainQueue(Runnable task) {
        MapThreadScheduling.forceContinue();
        this.mainQueueHandler.queueToMainQueue(task);
    }
}
