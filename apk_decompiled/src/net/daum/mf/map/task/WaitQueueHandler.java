package net.daum.mf.map.task;

public interface WaitQueueHandler {
    void queueToWaitQueue(Runnable runnable);
}
