package net.daum.mf.map.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MapTaskManager {
    private static MapTaskManager instance = new MapTaskManager();
    private ExecutorService executor = new ThreadPoolExecutor(2, ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED, 60, TimeUnit.SECONDS, new SynchronousQueue());

    public static MapTaskManager getInstance() {
        return instance;
    }

    private MapTaskManager() {
    }

    public void execute(Runnable task) {
        this.executor.execute(task);
    }

    public void queueTask(Runnable task, boolean waitQueue) {
        if (waitQueue) {
            WaitQueueManager.getInstance().queueToWaitQueue(task);
        } else {
            MainQueueManager.getInstance().queueToMainQueue(task);
        }
    }
}
