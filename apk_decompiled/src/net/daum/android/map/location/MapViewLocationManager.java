package net.daum.android.map.location;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.BadTokenException;
import android.view.WindowManager.LayoutParams;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import net.daum.android.map.MapController;
import net.daum.android.map.MapViewController;
import net.daum.android.map.coord.MapCoord;
import net.daum.android.map.coord.MapCoordLatLng;
import net.daum.mf.map.api.InternalMapViewAccessor;
import net.daum.mf.map.api.MapView.CurrentLocationTrackingMode;
import net.daum.mf.map.p000n.api.NativeMapCoord;
import net.daum.mf.map.p000n.api.internal.NativeMapController;
import net.daum.mf.map.task.MainQueueManager;

public class MapViewLocationManager implements OnCancelListener, OnClickListener {
    private static final int MAX_DURATION_TIME = 40000;
    static int MAX_LEVEL_FOR_ACCURACY = 8;
    static int MIN_LEVEL_FOR_ACCURACY = 0;
    private static final MapViewLocationManager instance = new MapViewLocationManager();
    private BestInactiveLocationProviderListener bestInactiveLocationProviderListener = new BestInactiveLocationProviderListener();
    private Criteria coarseCriteria = new Criteria();
    private LocationListener coarseLocationListener;
    private Activity dmapActivity;
    private Criteria fineCriteria;
    private LocationListener fineLocationListener;
    Date firstLocationTimestamp = null;
    private final Listener gpsStatusListener = new C03661();
    private final HeadingEventListener headingListener = new HeadingEventListener();
    private boolean isTrackingHeading = false;
    private boolean isTrackingMarkerHeading = false;
    private boolean isTrackingMode = false;
    private boolean isUsingMapMove = true;
    private ProgressDialog loadingDialog = null;
    private LocationManager locationManager;
    private long startTimerTime;
    private GpsSeekingTimerTask task = null;
    private Timer timer = null;

    /* renamed from: net.daum.android.map.location.MapViewLocationManager$1 */
    class C03661 implements Listener {
        C03661() {
        }

        public void onGpsStatusChanged(int event) {
            switch (event) {
                case 3:
                    if (MapViewLocationManager.this.coarseLocationListener != null) {
                        MapViewLocationManager.this.locationManager.removeUpdates(MapViewLocationManager.this.coarseLocationListener);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    /* renamed from: net.daum.android.map.location.MapViewLocationManager$2 */
    class C03672 implements Runnable {
        C03672() {
        }

        public void run() {
            if (MapViewLocationManager.this.isWpsLocationSet() || MapViewLocationManager.this.isGpsLocationSet()) {
                if (!MapViewLocationManager.this.isWpsLocationSet()) {
                    MapViewLocationManager.this.showLoadingIndicator();
                    MapViewLocationManager.this.cancelTimer();
                    MapViewLocationManager.this.timer = new Timer();
                    MapViewLocationManager.this.task = new GpsSeekingTimerTask();
                    MapViewLocationManager.this.timer.schedule(MapViewLocationManager.this.task, 0, 1000);
                    MapViewLocationManager.this.startTimerTime = SystemClock.elapsedRealtime();
                }
                MapViewLocationManager.this.requestLocationUpdate();
                return;
            }
            MapViewLocationManager.this.isTrackingMode = false;
        }
    }

    /* renamed from: net.daum.android.map.location.MapViewLocationManager$3 */
    class C03683 implements Runnable {
        C03683() {
        }

        public void run() {
            MapViewLocationManager.this.cancelTimer();
            MapViewLocationManager.this.hideLoadingIndicator();
            if (MapViewLocationManager.this.coarseLocationListener != null) {
                MapViewLocationManager.this.locationManager.removeUpdates(MapViewLocationManager.this.coarseLocationListener);
            }
            if (MapViewLocationManager.this.fineLocationListener != null) {
                MapViewLocationManager.this.locationManager.removeUpdates(MapViewLocationManager.this.fineLocationListener);
            }
            if (MapViewLocationManager.this.isTrackingHeading) {
                MapViewLocationManager.this.stopTrackingHeading();
            }
        }
    }

    /* renamed from: net.daum.android.map.location.MapViewLocationManager$4 */
    class C03694 implements Runnable {
        C03694() {
        }

        public void run() {
            if (MapViewLocationManager.this.loadingDialog == null || !MapViewLocationManager.this.loadingDialog.isShowing()) {
                MapViewLocationManager.this.loadingDialog = new ProgressDialog(MapViewLocationManager.this.dmapActivity);
                Window theWindow = MapViewLocationManager.this.loadingDialog.getWindow();
                theWindow.requestFeature(1);
                LayoutParams attribute = theWindow.getAttributes();
                attribute.softInputMode = 1;
                MapViewLocationManager.this.loadingDialog.setMessage("현재 위치를 찾고 있습니다.");
                MapViewLocationManager.this.loadingDialog.setCanceledOnTouchOutside(false);
                MapViewLocationManager.this.loadingDialog.setOnCancelListener(MapViewLocationManager.this);
                MapViewLocationManager.this.loadingDialog.setButton("취소", MapViewLocationManager.this);
                theWindow.setAttributes(attribute);
                try {
                    MapViewLocationManager.this.loadingDialog.show();
                } catch (BadTokenException e) {
                }
            }
        }
    }

    /* renamed from: net.daum.android.map.location.MapViewLocationManager$5 */
    class C03705 implements Runnable {
        C03705() {
        }

        public void run() {
            if (MapViewLocationManager.this.loadingDialog != null && MapViewLocationManager.this.loadingDialog.isShowing()) {
                MapViewLocationManager.this.loadingDialog.dismiss();
                MapViewLocationManager.this.loadingDialog = null;
            }
        }
    }

    class BestInactiveLocationProviderListener implements android.location.LocationListener {
        BestInactiveLocationProviderListener() {
        }

        public void onLocationChanged(Location l) {
        }

        public void onProviderDisabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
            MapViewLocationManager.this.locationManager.removeUpdates(MapViewLocationManager.this.bestInactiveLocationProviderListener);
            MapViewLocationManager.this.requestLocationUpdate();
        }
    }

    private class GpsSeekingTimerTask extends TimerTask {
        private GpsSeekingTimerTask() {
        }

        public void run() {
            if (SystemClock.elapsedRealtime() - MapViewLocationManager.this.startTimerTime > 40000) {
                MapViewLocationManager.this.cancelTimer();
                InternalMapViewAccessor.callMapView_onCurrentLocationUpdateFailed();
            }
        }
    }

    private class HeadingEventListener implements SensorEventListener {
        private HeadingEventListener() {
        }

        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        public void onSensorChanged(SensorEvent event) {
            float heading = event.values[0];
            Display display = ((WindowManager) MapViewLocationManager.this.dmapActivity.getSystemService("window")).getDefaultDisplay();
            if (display.getOrientation() == 1) {
                heading += 90.0f;
            } else if (display.getOrientation() == 3) {
                heading += 270.0f;
            }
            if (MapViewLocationManager.this.isTrackingMarkerHeading) {
                MapViewController.getInstance().setLocationMarkerRotation(heading, false);
                return;
            }
            MapController.getInstance().setMapGroundAngleWithAnimation(heading, true);
            InternalMapViewAccessor.callMapView_onCurrentLocationDeviceHeadingUpdate(heading);
        }
    }

    private class LocationListener implements android.location.LocationListener {
        public void onLocationChanged(final Location newLocation) {
            MapViewLocationManager.this.cancelTimer();
            MapViewLocationManager.this.makeTimeStampIfFirstLoading();
            MapViewLocationManager.this.hideLoadingIndicator();
            final MapCoord currentLocation = new MapCoordLatLng(newLocation.getLatitude(), newLocation.getLongitude()).toWcong();
            if (currentLocation != null && NativeMapController.isValidMapCoordForSouthKorea(new NativeMapCoord(currentLocation))) {
                MainQueueManager.getInstance().queueToMainQueue(new Runnable() {
                    public void run() {
                        MapViewController.getInstance().showLocationMarkerWithAnimation(currentLocation, true, false);
                        if (MapViewLocationManager.this.isUsingMapMove) {
                            MapController.getInstance().move2(currentLocation);
                        }
                        MapViewLocationManager.this.updateLocationMarkerWithAccuracy(newLocation.getAccuracy());
                    }
                });
                InternalMapViewAccessor.callMapView_onCurrentLocationUpdate(newLocation.getLatitude(), newLocation.getLongitude(), newLocation.getAccuracy());
            }
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
            MapViewLocationManager.this.requestLocationUpdate();
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (provider.equals("gps")) {
                switch (status) {
                    case 0:
                        MapViewLocationManager.this.requestLocationUpdate();
                        return;
                    default:
                        return;
                }
            }
        }
    }

    public static MapViewLocationManager getInstance() {
        return instance;
    }

    private MapViewLocationManager() {
        this.coarseCriteria.setAccuracy(2);
        this.fineCriteria = new Criteria();
        this.fineCriteria.setAccuracy(1);
    }

    public void init() {
        try {
            if (this.locationManager == null) {
                this.locationManager = (LocationManager) this.dmapActivity.getSystemService("location");
                this.locationManager.addGpsStatusListener(this.gpsStatusListener);
                this.fineLocationListener = new LocationListener();
                this.coarseLocationListener = new LocationListener();
            }
        } catch (Exception e) {
            Log.e("MapViewLocationManager", "init failed");
            this.locationManager = null;
        }
    }

    public void setMapActivity(Activity dmapActivity) {
        this.dmapActivity = dmapActivity;
        init();
    }

    public void onResumeMapActivity() {
        if (this.isTrackingHeading) {
            try {
                SensorManager sensorManager = (SensorManager) this.dmapActivity.getSystemService("sensor");
                sensorManager.registerListener(this.headingListener, sensorManager.getDefaultSensor(3), 0);
            } catch (Exception e) {
            }
        }
        if (this.isTrackingMode) {
            requestLocationUpdate();
        }
    }

    public void onPauseMapActivity() {
        if (this.isTrackingHeading) {
            ((SensorManager) this.dmapActivity.getSystemService("sensor")).unregisterListener(this.headingListener);
        }
        if (this.isTrackingMode) {
            if (this.coarseLocationListener != null) {
                this.locationManager.removeUpdates(this.coarseLocationListener);
            }
            if (this.fineLocationListener != null) {
                this.locationManager.removeUpdates(this.fineLocationListener);
            }
        }
    }

    public boolean isWpsLocationSet() {
        boolean isWpsEnabled = false;
        try {
            isWpsEnabled = this.locationManager.isProviderEnabled("network");
        } catch (IllegalArgumentException e) {
        } catch (Exception e2) {
        }
        return isWpsEnabled;
    }

    public boolean isGpsLocationSet() {
        boolean isGPSEnabled = false;
        try {
            isGPSEnabled = this.locationManager.isProviderEnabled("gps");
        } catch (IllegalArgumentException e) {
        } catch (Exception e2) {
        }
        return isGPSEnabled;
    }

    public void startTracking() {
        MapViewController.getInstance().switchTrackingMarker(true);
        startResolveCurrentLocation();
        this.isTrackingMode = true;
    }

    public void stopTracking() {
        MapViewController.getInstance().switchTrackingMarker(false);
        stopResolveCurrentLocation();
        this.isTrackingMode = false;
    }

    public boolean isTrackingMode() {
        return this.isTrackingMode;
    }

    public boolean isTrackingHeadingMode() {
        return this.isTrackingHeading;
    }

    public boolean isUsingMapMove() {
        return this.isUsingMapMove;
    }

    public void startTrackingHeading() {
        boolean z = true;
        SensorManager sensorManager = (SensorManager) this.dmapActivity.getSystemService("sensor");
        if (sensorManager.registerListener(this.headingListener, sensorManager.getDefaultSensor(3), 0)) {
            this.isTrackingHeading = true;
            MapController.getInstance().setUseHeading(true);
        }
        MapViewController instance = MapViewController.getInstance();
        if (this.isTrackingMarkerHeading) {
            z = false;
        }
        instance.switchHeadingMarker(z);
    }

    public void setUsingMapMove(boolean use) {
        this.isUsingMapMove = use;
    }

    public void setMarkerHeadingTracking(boolean trackMarkerHeading) {
        this.isTrackingMarkerHeading = trackMarkerHeading;
    }

    public boolean getMarkerHeadingTracking() {
        return this.isTrackingMarkerHeading;
    }

    public void stopTrackingHeading() {
        ((SensorManager) this.dmapActivity.getSystemService("sensor")).unregisterListener(this.headingListener);
        this.isTrackingHeading = false;
        MapController.getInstance().setUseHeading(false);
        MapViewController.getInstance().switchHeadingMarker(false);
    }

    public void requestLocationUpdate() {
        if (isWpsLocationSet() && this.coarseLocationListener != null) {
            this.locationManager.requestLocationUpdates("network", 0, 0.0f, this.coarseLocationListener);
        }
        if (isGpsLocationSet() && this.fineLocationListener != null) {
            this.locationManager.requestLocationUpdates("gps", 0, 0.0f, this.fineLocationListener);
        }
        String bestProvider = this.locationManager.getBestProvider(this.fineCriteria, false);
        String bestAvailableProvider = this.locationManager.getBestProvider(this.fineCriteria, true);
        if (bestProvider != null && !bestProvider.equals(bestAvailableProvider)) {
            this.locationManager.requestLocationUpdates(bestProvider, 0, 0.0f, this.bestInactiveLocationProviderListener, Looper.getMainLooper());
        }
    }

    public boolean startResolveCurrentLocation() {
        if (this.dmapActivity.isFinishing()) {
            return false;
        }
        this.dmapActivity.runOnUiThread(new C03672());
        return true;
    }

    public boolean stopResolveCurrentLocation() {
        this.dmapActivity.runOnUiThread(new C03683());
        return true;
    }

    public void cancelTimer() {
        if (this.timer != null) {
            this.timer.cancel();
            this.timer = null;
        }
    }

    void updateLocationMarkerWithAccuracy(float locationAccuracy) {
        MapViewController.getInstance().setAccuracy(locationAccuracy, false);
    }

    public float getZoomWithLevel(int level) {
        return (float) (1.0d / Math.pow(2.0d, (double) level));
    }

    public float getLevelWithZoom(float zoom) {
        return (float) (Math.log((double) (1.0f / zoom)) / Math.log(2.0d));
    }

    boolean isCurrentLevelOutOfRange() {
        boolean currentLevelIsTooLow;
        int currentLevel = MapController.getInstance().getZoomLevelInt();
        float zoom = MapController.getInstance().getZoom();
        if (currentLevel > MIN_LEVEL_FOR_ACCURACY || ((double) zoom) <= 1.0d) {
            currentLevelIsTooLow = false;
        } else {
            currentLevelIsTooLow = true;
        }
        boolean currentLevelIsTooHigh;
        if (currentLevel > MAX_LEVEL_FOR_ACCURACY) {
            currentLevelIsTooHigh = true;
        } else {
            currentLevelIsTooHigh = false;
        }
        if (currentLevelIsTooLow || currentLevelIsTooHigh) {
            return true;
        }
        return false;
    }

    void makeTimeStampIfFirstLoading() {
        if (this.firstLocationTimestamp == null) {
            this.firstLocationTimestamp = new Date();
        }
    }

    boolean isShortIntervalFromFirstLoading(Date date) {
        boolean z = false;
        if (!(this.firstLocationTimestamp == null || this.firstLocationTimestamp.equals(date))) {
            if (new Date().getTime() - this.firstLocationTimestamp.getTime() < 2000) {
                z = true;
            }
        }
        return z;
    }

    public boolean isLocationAvailable() {
        List<String> locationProviders = this.locationManager.getAllProviders();
        if (locationProviders == null || locationProviders.size() <= 0) {
            return false;
        }
        for (int i = 0; i < locationProviders.size(); i++) {
            String providerName = (String) locationProviders.get(i);
            if ((providerName.equals("network") || providerName.equals("gps")) && this.locationManager.getProvider(providerName) != null) {
                return true;
            }
        }
        return false;
    }

    public boolean isHeadingAvailable() {
        boolean sensorAvailable;
        if (((SensorManager) this.dmapActivity.getSystemService("sensor")).getDefaultSensor(3) != null) {
            sensorAvailable = true;
        } else {
            sensorAvailable = false;
        }
        return sensorAvailable;
    }

    private void showLoadingIndicator() {
        if (this.dmapActivity != null && !this.dmapActivity.isFinishing()) {
            this.dmapActivity.runOnUiThread(new C03694());
        }
    }

    private void hideLoadingIndicator() {
        if (this.loadingDialog != null && this.loadingDialog.isShowing() && this.dmapActivity != null && !this.dmapActivity.isFinishing()) {
            this.dmapActivity.runOnUiThread(new C03705());
        }
    }

    public void onCancel(DialogInterface dialog) {
        hideLoadingIndicator();
        InternalMapViewAccessor.callMapView_onCurrentLocationUpdateCancelled();
        InternalMapViewAccessor.getCurrentMapViewInstance().setCurrentLocationTrackingMode(CurrentLocationTrackingMode.TrackingModeOff);
    }

    public void onClick(DialogInterface dialog, int which) {
        hideLoadingIndicator();
        InternalMapViewAccessor.callMapView_onCurrentLocationUpdateCancelled();
        InternalMapViewAccessor.getCurrentMapViewInstance().setCurrentLocationTrackingMode(CurrentLocationTrackingMode.TrackingModeOff);
    }
}
