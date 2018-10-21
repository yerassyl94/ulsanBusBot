package net.daum.mf.map.task;

public interface MainQueueHandler {
    void queueToMainQueue(Runnable runnable);
}
