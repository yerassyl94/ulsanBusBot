package net.daum.mf.map.common;

import java.util.concurrent.atomic.AtomicLong;
import net.daum.mf.map.p000n.api.NativeMapLoopScheduling;

public class MapThreadScheduling {
    private static boolean _mapViewMode = true;
    private static NativeMapLoopScheduling loopScheduling = new NativeMapLoopScheduling();
    private static AtomicLong waitStarted = new AtomicLong();
    private static AtomicLong waitStartedRoadView = new AtomicLong();

    public static boolean isMapViewMode() {
        return _mapViewMode;
    }

    public static void setMapViewMode(boolean mapViewMode) {
        _mapViewMode = mapViewMode;
    }

    public static void forceContinue() {
        if (_mapViewMode) {
            waitStarted.set(0);
        } else {
            waitStartedRoadView.set(0);
        }
    }

    private static void clearStarted() {
        if (_mapViewMode) {
            waitStarted.set(0);
        } else {
            waitStartedRoadView.set(0);
        }
    }

    public static void setBusyLoop(boolean busy) {
        if (_mapViewMode) {
            loopScheduling.setBusyLoop(busy);
        } else {
            loopScheduling.setRoadViewBusyLoop(busy);
        }
    }

    public static boolean needToWait() {
        boolean busy;
        if (_mapViewMode) {
            busy = loopScheduling.isBusyLoop();
            if (busy) {
                clearStarted();
            } else {
                waitStarted.set(System.currentTimeMillis());
            }
        } else {
            busy = loopScheduling.isRoadViewInBusyLoop();
            if (busy) {
                clearStarted();
            } else {
                waitStartedRoadView.set(System.currentTimeMillis());
            }
        }
        if (busy) {
            return false;
        }
        return true;
    }

    public static boolean isWaiting() {
        if (_mapViewMode) {
            if (waitStarted.get() <= 0) {
                return false;
            }
            if (loopScheduling.isBusyLoop()) {
                clearStarted();
                return false;
            } else if (System.currentTimeMillis() - waitStarted.get() < 500) {
                return true;
            } else {
                return false;
            }
        } else if (waitStartedRoadView.get() <= 0) {
            return false;
        } else {
            if (loopScheduling.isRoadViewInBusyLoop()) {
                clearStarted();
                return false;
            } else if (System.currentTimeMillis() - waitStartedRoadView.get() < 500) {
                return true;
            } else {
                return false;
            }
        }
    }
}
