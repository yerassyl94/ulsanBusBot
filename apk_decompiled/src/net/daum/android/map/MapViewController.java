package net.daum.android.map;

import android.os.Build;
import net.daum.android.map.coord.MapCoord;
import net.daum.mf.map.p000n.api.NativeMapCoord;
import net.daum.mf.map.p000n.api.internal.NativeMapViewController;
import net.daum.mf.map.task.MainQueueManager;
import net.daum.mf.map.task.MapTaskManager;

public final class MapViewController {
    private static final MapViewController _instance = new MapViewController();
    protected NativeMapViewController _nativeMapViewController = new NativeMapViewController();

    /* renamed from: net.daum.android.map.MapViewController$1 */
    class C03571 implements Runnable {
        C03571() {
        }

        public void run() {
            MapViewController.this._nativeMapViewController.disuseInfoWindows();
        }
    }

    public static MapViewController getInstance() {
        return _instance;
    }

    private MapViewController() {
    }

    public void disuseInfoWindows() {
        MapTaskManager.getInstance().queueTask(new C03571(), MapEngineManager.getInstance().getStopGlSwap());
    }

    public void showZoomControls(final boolean show) {
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                MapViewController.this._nativeMapViewController.showZoomControls(show);
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public void switchTrackingMarker(final boolean shouldTurnOn) {
        MainQueueManager.getInstance().queueToMainQueue(new Runnable() {
            public void run() {
                MapViewController.this._nativeMapViewController.switchTrackingMarker(shouldTurnOn);
            }
        });
    }

    public void switchHeadingMarker(final boolean shouldTurnOn) {
        MainQueueManager.getInstance().queueToMainQueue(new Runnable() {
            public void run() {
                MapViewController.this._nativeMapViewController.switchHeadingMarker(shouldTurnOn);
            }
        });
    }

    public void showLocationMarkerWithAnimation(final MapCoord coord, final boolean animate, boolean forceRunOnMapEngineLoop) {
        Runnable task = new Runnable() {
            public void run() {
                NativeMapCoord nativeMapCoord = new NativeMapCoord(coord);
                boolean drawRange = true;
                if (Build.MODEL.equals("LG-SU760")) {
                    drawRange = false;
                }
                MapViewController.this._nativeMapViewController.showLocationMarkerWithAnimation(nativeMapCoord, animate, drawRange);
            }
        };
        if (forceRunOnMapEngineLoop) {
            MainQueueManager.getInstance().queueToMainQueue(task);
        } else {
            task.run();
        }
    }

    public void setLocationMarkerRotation(final float rotation, boolean forceRunOnMapEngineLoop) {
        Runnable task = new Runnable() {
            public void run() {
                MapViewController.this._nativeMapViewController.setLocationMarkerRotation(rotation);
            }
        };
        if (forceRunOnMapEngineLoop) {
            MainQueueManager.getInstance().queueToMainQueue(task);
        } else {
            task.run();
        }
    }

    public void setAccuracy(final float accuracy, boolean forceRunOnMapEngineLoop) {
        Runnable task = new Runnable() {
            public void run() {
                MapViewController.this._nativeMapViewController.setAccuracy(accuracy);
            }
        };
        if (forceRunOnMapEngineLoop) {
            MainQueueManager.getInstance().queueToMainQueue(task);
        } else {
            task.run();
        }
    }

    public void showInfoPanelTimedMessage(final String message, final float timeoutTimeInterval, boolean forceRunOnMapEngineLoop) {
        Runnable task = new Runnable() {
            public void run() {
                MapViewController.this._nativeMapViewController.showInfoPanelTimedMessage(message, timeoutTimeInterval);
            }
        };
        if (forceRunOnMapEngineLoop) {
            MainQueueManager.getInstance().queueToMainQueue(task);
        } else {
            task.run();
        }
    }
}
