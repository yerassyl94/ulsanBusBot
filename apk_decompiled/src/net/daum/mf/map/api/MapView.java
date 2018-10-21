package net.daum.mf.map.api;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.microedition.khronos.opengles.GL10;
import net.daum.android.map.MapController;
import net.daum.android.map.MapEngineManager;
import net.daum.android.map.MapTileVersionCheckWebService;
import net.daum.android.map.MapTileVersionCheckWebService.MapTileVersionCheckResultListener;
import net.daum.android.map.coord.MapCoord;
import net.daum.android.map.location.MapViewLocationManager;
import net.daum.android.map.openapi.auth.OpenAPIKeyAuthenticationWebService;
import net.daum.android.map.util.BitmapUtils;
import net.daum.android.map.util.PersistentKeyValueStore;
import net.daum.mf.map.api.MapPOIItem.CalloutBalloonButtonType;
import net.daum.mf.map.api.MapPOIItem.ImageOffset;
import net.daum.mf.map.api.MapPOIItem.MarkerType;
import net.daum.mf.map.api.MapPOIItem.ShowAnimationType;
import net.daum.mf.map.api.MapPoint.PlainCoordinate;
import net.daum.mf.map.common.ResourceUtils;
import net.daum.mf.map.p000n.api.NativeMapCoord;
import net.daum.mf.map.p000n.api.NativeTileUrlInfo;
import net.daum.mf.map.p000n.api.internal.NativeCircleOverlayManager;
import net.daum.mf.map.p000n.api.internal.NativeMapBuildSettings;
import net.daum.mf.map.p000n.api.internal.NativeMapController;
import net.daum.mf.map.p000n.api.internal.NativeMapLocationManager;
import net.daum.mf.map.p000n.api.internal.NativePOIItemMarkerManager;
import net.daum.mf.map.p000n.api.internal.NativePolylineOverlayManager;
import net.daum.mf.map.task.MainQueueManager;
import net.daum.mf.map.task.MapTaskManager;

public class MapView extends net.daum.android.map.MapView implements net.daum.android.map.openapi.auth.OpenAPIKeyAuthenticationWebService.OpenAPIKeyAuthenticationResultListener, MapTileVersionCheckResultListener {
    static MapView CurrentMapViewInstance = null;
    private static boolean IsMapTilePersistentCacheEnabled = false;
    public static float MAX_ZOOM_LEVEL = 12.0f;
    public static float MIN_ZOOM_LEVEL = -2.0f;
    private static boolean MapEngineIsActive = false;
    private static CurrentLocationTrackingMode currentLocationTrackingMode = CurrentLocationTrackingMode.TrackingModeOff;
    private final int CAMERA_ANIMATION_PHASE_COUNT = 2;
    private final long CAMERA_ANIMATION_TIMER_PERIOD = 33;
    private final float DEFAULT_CAMERA_ANIMATION_DURATION = 1000.0f;
    private String apiKey;
    private CalloutBalloonAdapter calloutBalloonAdapter;
    CancelableCallback cameraAnimationCancelableCallback;
    Timer cameraAnimationTimer;
    private ArrayList<MapCircle> circles;
    private WeakReference<CurrentLocationEventListener> currentLocationEventListener;
    private WeakReference<Activity> dmapActivity;
    boolean dragStarted = false;
    private MapPoint dragStartedMapPoint = null;
    boolean isFirstOnDrawFrame = true;
    private boolean mapEngineLoadedForThisMapView;
    private MapTileVersionCheckWebService mapTileVersionCheckWebService = null;
    private WeakReference<MapViewEventListener> mapViewEventListener;
    private boolean needSynchronousCalloutBalloonGeneration = false;
    private OpenAPIKeyAuthenticationWebService openAPIKeyAuthService = null;
    private WeakReference<OpenAPIKeyAuthenticationResultListener> openAPIKeyAuthenticationResultListener;
    private WeakReference<POIItemEventListener> poiItemEventListener;
    private Queue<MapPOIItem> poiItems;
    private ArrayList<MapPolyline> polylines;

    /* renamed from: net.daum.mf.map.api.MapView$1 */
    class C03811 implements Runnable {
        C03811() {
        }

        public void run() {
            NativeTileUrlInfo.resetToDefaultTileVersion();
            new NativeMapController().resetMapTileCache();
        }
    }

    /* renamed from: net.daum.mf.map.api.MapView$2 */
    class C03822 implements Runnable {
        C03822() {
        }

        public void run() {
            MapEngineManager.getInstance().onPauseMapActivity();
        }
    }

    /* renamed from: net.daum.mf.map.api.MapView$7 */
    class C03877 implements Runnable {
        C03877() {
        }

        public void run() {
            NativeMapLocationManager.setDefaultCurrentLocationMarker();
        }
    }

    public interface CurrentLocationEventListener {
        void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float f);

        void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float f);

        void onCurrentLocationUpdateCancelled(MapView mapView);

        void onCurrentLocationUpdateFailed(MapView mapView);
    }

    public enum CurrentLocationTrackingMode {
        TrackingModeOff,
        TrackingModeOnWithoutHeading,
        TrackingModeOnWithHeading,
        TrackingModeOnWithoutHeadingWithoutMapMoving,
        TrackingModeOnWithHeadingWithoutMapMoving,
        TrackingModeOnWithMarkerHeadingWithoutMapMoving
    }

    public enum MapTileMode {
        Standard,
        HD,
        HD2X
    }

    public enum MapType {
        Standard,
        Satellite,
        Hybrid
    }

    public interface MapViewEventListener {
        void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint);

        void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint);

        void onMapViewDragEnded(MapView mapView, MapPoint mapPoint);

        void onMapViewDragStarted(MapView mapView, MapPoint mapPoint);

        void onMapViewInitialized(MapView mapView);

        void onMapViewLongPressed(MapView mapView, MapPoint mapPoint);

        void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint);

        void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint);

        void onMapViewZoomLevelChanged(MapView mapView, int i);
    }

    public interface OpenAPIKeyAuthenticationResultListener {
        void onDaumMapOpenAPIKeyAuthenticationResult(MapView mapView, int i, String str);
    }

    public interface POIItemEventListener {
        @Deprecated
        void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem);

        void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, CalloutBalloonButtonType calloutBalloonButtonType);

        void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint);

        void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem);
    }

    public static boolean isMapTilePersistentCacheEnabled() {
        String GALAXYS = "SHW-M110S";
        String GALAXYK = "SHW-M130K";
        if ("SHW-M110S".equals(Build.MODEL) || "SHW-M130K".equals(Build.MODEL)) {
            return false;
        }
        return IsMapTilePersistentCacheEnabled;
    }

    public static void setMapTilePersistentCacheEnabled(boolean enableCache) {
        IsMapTilePersistentCacheEnabled = enableCache;
    }

    public static void clearMapTilePersistentCache() {
        if (CurrentMapViewInstance != null) {
            MapController.getInstance().resetMapTileCache();
        } else {
            MapController.getInstance().resetMapTileCacheRunOnCurrentThread();
        }
    }

    private void init(Activity activity) {
        boolean z = false;
        if (MapEngineIsActive) {
            throw new RuntimeException("DaumMap does not support that two or more net.daum.mf.map.api.MapView objects exists at the same time");
        }
        CurrentMapViewInstance = this;
        MapEngineManager.getInstance().onCreateMapActivity(activity, this);
        MapEngineManager.getInstance().onStartMapActivity();
        MapViewLocationManager.getInstance().setMapActivity(activity);
        MapEngineIsActive = true;
        this.dmapActivity = new WeakReference(activity);
        this.poiItems = new ConcurrentLinkedQueue();
        this.polylines = new ArrayList(8);
        this.circles = new ArrayList(16);
        this.openAPIKeyAuthenticationResultListener = null;
        this.mapViewEventListener = null;
        this.currentLocationEventListener = null;
        this.poiItemEventListener = null;
        this.mapEngineLoadedForThisMapView = false;
        setMapType(MapType.Standard);
        if (VERSION.SDK_INT <= 10 || ((Build.MODEL != null && Build.MODEL.startsWith("SHV-E160")) || (Build.MODEL != null && Build.MODEL.startsWith("IM-A830S")))) {
            z = true;
        }
        this.needSynchronousCalloutBalloonGeneration = z;
    }

    public MapView(Activity activity) {
        super(activity);
        init(activity);
    }

    public MapView(Context context) {
        super(context);
        if (context instanceof Activity) {
            init((Activity) context);
            return;
        }
        throw new RuntimeException("net.daum.mf.map.api.MapView.MapView constructors should get Activity instance as context input parameter");
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (context instanceof Activity) {
            init((Activity) context);
            return;
        }
        throw new RuntimeException("net.daum.mf.map.api.MapView.MapView constructors should get Activity instance as context input parameter");
    }

    public MapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (context instanceof Activity) {
            init((Activity) context);
            return;
        }
        throw new RuntimeException("net.daum.mf.map.api.MapView.MapView constructors should get Activity instance as context input parameter");
    }

    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
        MapEngineManager.getInstance().onResumeMapActivity();
        MapViewLocationManager.getInstance().onResumeMapActivity();
        if (NativeMapBuildSettings.isOpenAPIMapLibraryBuild()) {
            String apiKey = getDaumMapApiKey();
            String appId = null;
            if (this.dmapActivity.get() != null) {
                appId = ((Activity) this.dmapActivity.get()).getPackageName();
            }
            String appVersion = null;
            try {
                if (this.dmapActivity.get() != null) {
                    Activity activity = (Activity) this.dmapActivity.get();
                    appVersion = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
                }
            } catch (NameNotFoundException e) {
            }
            this.openAPIKeyAuthService = new OpenAPIKeyAuthenticationWebService((Context) this.dmapActivity.get(), apiKey, appId, appVersion, this);
            this.openAPIKeyAuthService.requestOpenAPIKeyAuthenticationService();
            return;
        }
        this.mapTileVersionCheckWebService = new MapTileVersionCheckWebService(this);
        this.mapTileVersionCheckWebService.requestMapTileVersionCheckService();
    }

    private void updateTileVersions(String imageTileVersion, String hybridTileVersion, String roadViewTileVersion) {
        boolean currentTileVersionExists = false;
        boolean newImageTileVersion = false;
        boolean newHybridTileVersion = false;
        boolean newRoadViewTileVersion = false;
        Activity activity = (Activity) this.dmapActivity.get();
        if (activity != null) {
            PersistentKeyValueStore keyValueStore = new PersistentKeyValueStore(activity);
            if (imageTileVersion != null) {
                imageTileVersion = imageTileVersion.trim();
                if (imageTileVersion.length() > 0) {
                    currentTileVersionExists = true;
                    if (!imageTileVersion.equals(keyValueStore.getLastImageTileVersion())) {
                        keyValueStore.setLastImageTileVersion(imageTileVersion);
                        newImageTileVersion = true;
                    }
                }
            }
            if (hybridTileVersion != null) {
                hybridTileVersion = hybridTileVersion.trim();
                if (hybridTileVersion.length() > 0) {
                    currentTileVersionExists = true;
                    if (!hybridTileVersion.equals(keyValueStore.getLastHybridTileVersion())) {
                        keyValueStore.setLastHybridTileVersion(hybridTileVersion);
                        newHybridTileVersion = true;
                    }
                }
            }
            if (roadViewTileVersion != null) {
                roadViewTileVersion = roadViewTileVersion.trim();
                if (roadViewTileVersion.length() > 0) {
                    currentTileVersionExists = true;
                    if (!roadViewTileVersion.equals(keyValueStore.getLastRoadViewTileVersion())) {
                        keyValueStore.setLastRoadViewTileVersion(roadViewTileVersion);
                        newRoadViewTileVersion = true;
                    }
                }
            }
            if (!currentTileVersionExists || newImageTileVersion || newHybridTileVersion || newRoadViewTileVersion) {
                MainQueueManager.getInstance().queueToMainQueue(new C03811());
            }
        }
    }

    public void onAuthenticationResultReceived(int resultCode, String message, String imageTileVersion, String hybridTileVersion, String roadViewTileVersion) {
        onOpenAPIKeyAuthenticationResult(resultCode, message);
        if (this.dmapActivity.get() != null) {
            updateTileVersions(imageTileVersion, hybridTileVersion, roadViewTileVersion);
        }
    }

    public void onAuthenticationErrorOccured() {
        Activity activity = (Activity) this.dmapActivity.get();
        if (activity != null) {
            activity.runOnUiThread(new C03822());
        }
    }

    public void onMapTileVersionCheckResultReceived(String imageTileVersion, String hybridTileVersion, String roadViewTileVersion) {
        updateTileVersions(imageTileVersion, hybridTileVersion, roadViewTileVersion);
    }

    public void onMapTileVersionCheckServiceErrorOccured() {
    }

    public void onSurfaceDestroyed() {
        MapEngineManager.getInstance().onPauseMapActivity();
        MapViewLocationManager.getInstance().onPauseMapActivity();
        MapEngineIsActive = false;
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        onSurfaceDestroyed();
        super.surfaceDestroyed(holder);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        super.surfaceChanged(holder, format, w, h);
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NativePOIItemMarkerManager.removeAllPOIItemMarkers();
        NativePolylineOverlayManager.removeAllPolylines();
        NativeCircleOverlayManager.removeAllCircles();
        this.poiItems.clear();
        this.polylines.clear();
        this.circles.clear();
        MapEngineManager.getInstance().onStopMapActivity();
        MapEngineIsActive = false;
        this.dmapActivity.clear();
    }

    @Deprecated
    public void setDaumMapApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    String getDaumMapApiKey() {
        return this.apiKey;
    }

    public void setOpenAPIKeyAuthenticationResultListener(OpenAPIKeyAuthenticationResultListener openAPIKeyAuthListener) {
        this.openAPIKeyAuthenticationResultListener = new WeakReference(openAPIKeyAuthListener);
    }

    public void setMapViewEventListener(MapViewEventListener mapViewEventListener) {
        this.mapViewEventListener = new WeakReference(mapViewEventListener);
    }

    public void setCurrentLocationEventListener(CurrentLocationEventListener currentLocationEventListener) {
        this.currentLocationEventListener = new WeakReference(currentLocationEventListener);
    }

    public void setPOIItemEventListener(POIItemEventListener poiItemEventListener) {
        this.poiItemEventListener = new WeakReference(poiItemEventListener);
    }

    public void setCalloutBalloonAdapter(CalloutBalloonAdapter calloutBalloonAdapter) {
        this.calloutBalloonAdapter = calloutBalloonAdapter;
    }

    public MapType getMapType() {
        int mapViewType = MapController.getInstance().getViewType();
        if (mapViewType == 1) {
            return MapType.Standard;
        }
        if (mapViewType == 2) {
            return MapType.Satellite;
        }
        if (mapViewType == 3) {
            return MapType.Hybrid;
        }
        return MapType.Standard;
    }

    public void setMapType(MapType mapType) {
        MapController mapController = MapController.getInstance();
        if (mapType == MapType.Standard) {
            mapController.setViewType(1);
        } else if (mapType == MapType.Satellite) {
            mapController.setViewType(2);
        } else if (mapType == MapType.Hybrid) {
            mapController.setViewType(3);
        }
        mapController.setNeedsRefreshTiles();
    }

    public boolean isHDMapTileEnabled() {
        return MapController.getInstance().isHDMapTileEnabled();
    }

    public void setHDMapTileEnabled(boolean enabled) {
        MapController.getInstance().setHDMapTileEnabled(enabled, true);
    }

    public MapTileMode getMapTileMode() {
        int tileMode = MapController.getInstance().getMapTileMode();
        if (tileMode == 200) {
            return MapTileMode.HD2X;
        }
        if (tileMode == 100) {
            return MapTileMode.HD;
        }
        return MapTileMode.Standard;
    }

    public void setMapTileMode(MapTileMode tileMode) {
        MapController mapController = MapController.getInstance();
        if (tileMode == MapTileMode.HD2X) {
            mapController.setMapTileMode(200, false);
        } else if (tileMode == MapTileMode.HD) {
            mapController.setMapTileMode(100, false);
        } else {
            mapController.setMapTileMode(0, false);
        }
    }

    public void setCurrentLocationTrackingMode(CurrentLocationTrackingMode trackingMode) {
        int trackingModeInt = 1;
        boolean isUsingMapMove = false;
        boolean trackMarkerHeading = false;
        if (trackingMode == CurrentLocationTrackingMode.TrackingModeOnWithoutHeading) {
            trackingModeInt = 2;
            isUsingMapMove = true;
        } else if (trackingMode == CurrentLocationTrackingMode.TrackingModeOnWithHeading) {
            trackingModeInt = 3;
            isUsingMapMove = true;
        } else if (trackingMode == CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving) {
            trackingModeInt = 4;
            isUsingMapMove = false;
        } else if (trackingMode == CurrentLocationTrackingMode.TrackingModeOnWithHeadingWithoutMapMoving) {
            trackingModeInt = 5;
            isUsingMapMove = false;
        } else if (trackingMode == CurrentLocationTrackingMode.TrackingModeOnWithMarkerHeadingWithoutMapMoving) {
            trackingModeInt = 5;
            isUsingMapMove = false;
            trackMarkerHeading = true;
        }
        MapViewLocationManager.getInstance().init();
        MapViewLocationManager.getInstance().setUsingMapMove(isUsingMapMove);
        MapViewLocationManager.getInstance().setMarkerHeadingTracking(trackMarkerHeading);
        NativeMapLocationManager.setCurrentLocationTrackingMode(trackingModeInt);
    }

    public CurrentLocationTrackingMode getCurrentLocationTrackingMode() {
        int trackingModeInt = NativeMapLocationManager.getCurrentLocationTrackingMode();
        if (trackingModeInt == 2) {
            return CurrentLocationTrackingMode.TrackingModeOnWithoutHeading;
        }
        if (trackingModeInt == 3) {
            return CurrentLocationTrackingMode.TrackingModeOnWithHeading;
        }
        if (trackingModeInt == 4) {
            return CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving;
        }
        if (trackingModeInt != 5) {
            return CurrentLocationTrackingMode.TrackingModeOff;
        }
        if (MapViewLocationManager.getInstance().getMarkerHeadingTracking()) {
            return CurrentLocationTrackingMode.TrackingModeOnWithMarkerHeadingWithoutMapMoving;
        }
        return CurrentLocationTrackingMode.TrackingModeOnWithHeadingWithoutMapMoving;
    }

    public void setShowCurrentLocationMarker(boolean show) {
        NativeMapLocationManager.setShowCurrentLocationMarker(show);
    }

    public boolean isShowingCurrentLocationMarker() {
        return NativeMapLocationManager.isShowingCurrentLocationMarker();
    }

    public void setCurrentLocationMarker(final MapCurrentLocationMarker marker) {
        NativeMapLocationManager.setCurrentLocationRadius(marker.getRadius());
        NativeMapLocationManager.setCurrentLocationRadiusStrokeColor(marker.getRadiusStrokeColor());
        NativeMapLocationManager.setCurrentLocationRadiusFillColor(marker.getRadiusFillColor());
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                Drawable drawable;
                int[] trackingAnimationImageIds = marker.getTrackingAnimationImageIds();
                float[] trackingAnimationImageAnchorRatioXs = marker.getTrackingAnimationImageAnchorRatioX();
                float[] trackingAnimationImageAnchorRatioYs = marker.getTrackingAnimationImageAnchorRatioY();
                if (trackingAnimationImageIds != null && trackingAnimationImageIds.length > 0) {
                    int count = trackingAnimationImageIds.length;
                    String[] paths = new String[count];
                    int[] widths = new int[count];
                    int[] heights = new int[count];
                    int[] anchorPointX = new int[count];
                    int[] anchorPointY = new int[count];
                    for (int i = 0; i < trackingAnimationImageIds.length; i++) {
                        int trackingAnimationImageId = trackingAnimationImageIds[i];
                        paths[i] = MapView.this.getCustomImageResourcePath(trackingAnimationImageId);
                        widths[i] = -1;
                        heights[i] = -1;
                        drawable = MapView.this.getContext().getResources().getDrawable(trackingAnimationImageId);
                        if (drawable != null) {
                            widths[i] = drawable.getIntrinsicWidth();
                            heights[i] = drawable.getIntrinsicHeight();
                        }
                        float trackingAnimationImageAnchorRatioX = 0.5f;
                        float trackingAnimationImageAnchorRatioY = 0.5f;
                        if (i < trackingAnimationImageAnchorRatioXs.length) {
                            trackingAnimationImageAnchorRatioX = Math.min(1.0f, trackingAnimationImageAnchorRatioXs[i]);
                        }
                        if (i < trackingAnimationImageAnchorRatioYs.length) {
                            trackingAnimationImageAnchorRatioY = Math.min(1.0f, trackingAnimationImageAnchorRatioYs[i]);
                        }
                        int trackingAnimationImageAnchorOffsetX = Math.round(((float) widths[i]) * trackingAnimationImageAnchorRatioX);
                        int trackingAnimationImageAnchorOffsetY = Math.max(0, heights[i] - Math.round(((float) heights[i]) * trackingAnimationImageAnchorRatioY));
                        anchorPointX[i] = trackingAnimationImageAnchorOffsetX;
                        anchorPointY[i] = trackingAnimationImageAnchorOffsetY;
                    }
                    NativeMapLocationManager.setCustomCurrentLocationMarkerTrackingAnimationImages(paths, widths, heights, anchorPointX, anchorPointY, marker.getTrackingAnimationDuration());
                }
                int trackingOffImageId = marker.getTrackingOffImageId();
                if (trackingOffImageId != 0) {
                    int trackingOffImageWidth = -1;
                    int trackingOffImageHeight = -1;
                    drawable = MapView.this.getContext().getResources().getDrawable(trackingOffImageId);
                    if (drawable != null) {
                        trackingOffImageWidth = drawable.getIntrinsicWidth();
                        trackingOffImageHeight = drawable.getIntrinsicHeight();
                    }
                    float trackingOffImageAnchorRatioX = Math.min(1.0f, marker.getTrackingOffImageAnchorRatioX());
                    float trackingOffImageAnchorRatioY = Math.min(1.0f, marker.getTrackingOffImageAnchorRatioY());
                    NativeMapLocationManager.setCustomCurrentLocationMarkerImage(MapView.this.getCustomImageResourcePath(trackingOffImageId), trackingOffImageWidth, trackingOffImageHeight, Math.round(((float) trackingOffImageWidth) * trackingOffImageAnchorRatioX), Math.max(0, trackingOffImageHeight - Math.round(((float) trackingOffImageHeight) * trackingOffImageAnchorRatioY)), false);
                }
                int directionImageId = marker.getDirectionImageId();
                if (directionImageId != 0) {
                    int directionImageWidth = -1;
                    int directionImageHeight = -1;
                    drawable = MapView.this.getContext().getResources().getDrawable(directionImageId);
                    if (drawable != null) {
                        directionImageWidth = drawable.getIntrinsicWidth();
                        directionImageHeight = drawable.getIntrinsicHeight();
                    }
                    float directionImageAnchorRatioX = Math.min(1.0f, marker.getDirectionImageAnchorRatioX());
                    float directionImageAnchorRatioY = Math.min(1.0f, marker.getDirectionImageAnchorRatioY());
                    NativeMapLocationManager.setCustomCurrentLocationMarkerDirectionImage(MapView.this.getCustomImageResourcePath(directionImageId), directionImageWidth, directionImageHeight, Math.round(((float) directionImageWidth) * directionImageAnchorRatioX), Math.max(0, directionImageHeight - Math.round(((float) directionImageHeight) * directionImageAnchorRatioY)), false);
                }
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public void setCurrentLocationRadius(int meter) {
        NativeMapLocationManager.setCurrentLocationRadius(meter);
    }

    public void setCurrentLocationRadiusStrokeColor(int color) {
        NativeMapLocationManager.setCurrentLocationRadiusStrokeColor(color);
    }

    public void setCurrentLocationRadiusFillColor(int color) {
        NativeMapLocationManager.setCurrentLocationRadiusFillColor(color);
    }

    public void setCustomCurrentLocationMarkerImage(int id, ImageOffset anchorPointOffset) {
        int anchorPointOffsetX = -1;
        int anchorPointOffsetY = -1;
        if (anchorPointOffset != null) {
            anchorPointOffsetX = anchorPointOffset.offsetX;
            anchorPointOffsetY = anchorPointOffset.offsetY;
        }
        int width = -1;
        int height = -1;
        if (id != 0) {
            Drawable customImageDrawable = getContext().getResources().getDrawable(id);
            if (customImageDrawable != null) {
                width = customImageDrawable.getIntrinsicWidth();
                height = customImageDrawable.getIntrinsicHeight();
            }
        }
        final int final_id = id;
        final int final_anchorPointOffsetX = anchorPointOffsetX;
        final int final_anchorPointOffsetY = anchorPointOffsetY;
        final int final_width = width;
        final int final_height = height;
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                NativeMapLocationManager.setCustomCurrentLocationMarkerImage(MapView.this.getCustomImageResourcePath(final_id), final_width, final_height, final_anchorPointOffsetX, final_anchorPointOffsetY, true);
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public void setCustomCurrentLocationMarkerTrackingImage(int id, ImageOffset anchorPointOffset) {
        int anchorPointOffsetX = -1;
        int anchorPointOffsetY = -1;
        if (anchorPointOffset != null) {
            anchorPointOffsetX = anchorPointOffset.offsetX;
            anchorPointOffsetY = anchorPointOffset.offsetY;
        }
        int width = -1;
        int height = -1;
        if (id != 0) {
            Drawable customImageDrawable = getContext().getResources().getDrawable(id);
            if (customImageDrawable != null) {
                width = customImageDrawable.getIntrinsicWidth();
                height = customImageDrawable.getIntrinsicHeight();
            }
        }
        final int final_id = id;
        final int final_anchorPointOffsetX = anchorPointOffsetX;
        final int final_anchorPointOffsetY = anchorPointOffsetY;
        final int final_width = width;
        final int final_height = height;
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                NativeMapLocationManager.setCustomCurrentLocationMarkerTrackingImage(MapView.this.getCustomImageResourcePath(final_id), final_width, final_height, final_anchorPointOffsetX, final_anchorPointOffsetY, true);
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public void setCustomCurrentLocationMarkerDirectionImage(int id, ImageOffset anchorPointOffset) {
        int anchorPointOffsetX = -1;
        int anchorPointOffsetY = -1;
        if (anchorPointOffset != null) {
            anchorPointOffsetX = anchorPointOffset.offsetX;
            anchorPointOffsetY = anchorPointOffset.offsetY;
        }
        int width = -1;
        int height = -1;
        if (id != 0) {
            Drawable customImageDrawable = getContext().getResources().getDrawable(id);
            if (customImageDrawable != null) {
                width = customImageDrawable.getIntrinsicWidth();
                height = customImageDrawable.getIntrinsicHeight();
            }
        }
        final int final_id = id;
        final int final_anchorPointOffsetX = anchorPointOffsetX;
        final int final_anchorPointOffsetY = anchorPointOffsetY;
        final int final_width = width;
        final int final_height = height;
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                NativeMapLocationManager.setCustomCurrentLocationMarkerDirectionImage(MapView.this.getCustomImageResourcePath(final_id), final_width, final_height, final_anchorPointOffsetX, final_anchorPointOffsetY, true);
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public void setDefaultCurrentLocationMarker() {
        MapTaskManager.getInstance().queueTask(new C03877(), MapEngineManager.getInstance().getStopGlSwap());
    }

    private String getCustomImageResourcePath(int id) {
        if (id == 0) {
            return null;
        }
        return String.format("res:%d", new Object[]{Integer.valueOf(id)});
    }

    public MapPoint getMapCenterPoint() {
        MapCoord coord = MapController.getInstance().getDestinationMapViewpoint();
        return MapPoint.mapPointWithWCONGCoord(coord.getX(), coord.getY());
    }

    public MapPointBounds getMapPointBounds() {
        return MapController.getInstance().getCurrentMapBounds();
    }

    public int getZoomLevel() {
        return MapController.getInstance().getZoomLevelInt();
    }

    public float getZoomLevelFloat() {
        return MapController.getInstance().getZoomLevelFloat();
    }

    public float getMapRotationAngle() {
        return MapController.getInstance().getMapRotationAngle();
    }

    public void setMapCenterPoint(MapPoint centerPoint, boolean animated) {
        MapController.getInstance().move(centerPoint.getInternalCoordObject(), animated);
    }

    public void setMapCenterPointAndZoomLevel(MapPoint centerPoint, int zoomLevel, boolean animated) {
        MapController.getInstance().move(centerPoint.getInternalCoordObject(), (float) zoomLevel, animated);
    }

    public void setZoomLevel(int zoomLevel, boolean animated) {
        MapController.getInstance().setZoomLevel((float) zoomLevel, animated);
    }

    public void setZoomLevelFloat(float zoomLevel, boolean animated) {
        MapController.getInstance().setZoomLevel(zoomLevel, animated);
    }

    public void zoomIn(boolean animated) {
        MapController.getInstance().zoomIn(animated);
    }

    public void zoomOut(boolean animated) {
        MapController.getInstance().zoomOut(animated);
    }

    public void setMapRotationAngle(float angle, boolean animated) {
        MapController.getInstance().setMapRotationAngle(angle, animated);
    }

    public void fitMapViewAreaToShowMapPoints(MapPoint[] mapPoints) {
        if (mapPoints != null && mapPoints.length > 0) {
            NativeMapCoord[] nativeMapCoords = new NativeMapCoord[mapPoints.length];
            for (int i = 0; i < mapPoints.length; i++) {
                PlainCoordinate mapPointWCONG = mapPoints[i].getMapPointWCONGCoord();
                nativeMapCoords[i] = new NativeMapCoord(mapPointWCONG.f13x, mapPointWCONG.f14y);
            }
            MapController.getInstance().fitMapViewAreaToShowAllMapPoints(nativeMapCoords);
        }
    }

    public void refreshMapTiles() {
        MapController.getInstance().setNeedsRefreshTiles();
    }

    public void releaseUnusedMapTileImageResources() {
        MapController.getInstance().releaseUnusedMapTileImageResources();
    }

    public MapPOIItem[] getPOIItems() {
        return (MapPOIItem[]) this.poiItems.toArray(new MapPOIItem[0]);
    }

    public void addPOIItem(MapPOIItem poiItem) {
        if (poiItem.getItemName() != null && poiItem.getMapPoint() != null) {
            if (poiItem.getMarkerType() != MarkerType.CustomImage || poiItem.getCustomImageResourceId() != 0 || poiItem.getCustomImageBitmap() != null) {
                int showAnimationTypeN;
                File file;
                if (this.needSynchronousCalloutBalloonGeneration && this.poiItems != null && this.poiItems.size() > 0) {
                    long mx = 0;
                    for (MapPOIItem item : this.poiItems) {
                        mx = Math.max(mx, item.createdTime);
                    }
                    if (mx >= poiItem.createdTime) {
                        poiItem.createdTime = 1 + mx;
                    }
                }
                this.poiItems.add(poiItem);
                final MapPOIItem poiItem_ = poiItem;
                String itemName = poiItem.getItemName();
                PlainCoordinate mapPointWCONG = poiItem.getMapPoint().getMapPointWCONGCoord();
                NativeMapCoord nativeMapPoint = new NativeMapCoord(mapPointWCONG.f13x, mapPointWCONG.f14y, 2);
                final int markerType = convertMarkerType(poiItem.getMarkerType());
                final int selectedMarkerType = convertMarkerType(poiItem.getSelectedMarkerType());
                ShowAnimationType showAnimationType_ = poiItem.getShowAnimationType();
                if (showAnimationType_ == ShowAnimationType.DropFromHeaven) {
                    showAnimationTypeN = 2;
                } else if (showAnimationType_ == ShowAnimationType.SpringFromGround) {
                    showAnimationTypeN = 3;
                } else {
                    showAnimationTypeN = 1;
                }
                final int showAnimationType = showAnimationTypeN;
                final boolean showCalloutBalloonOnTouch = poiItem.isShowCalloutBalloonOnTouch();
                final boolean showDisclosureButtonOnCalloutBalloon = poiItem.isShowDisclosureButtonOnCalloutBalloon();
                final boolean draggable = poiItem.isDraggable();
                final float alpha = poiItem.getAlpha();
                final float rotation = poiItem.getRotation();
                String customImageResourcePath = null;
                String customSelectedImageResourcePath = null;
                final int customImageAnchorPointOffsetX_F = -1;
                final int customImageAnchorPointOffsetY_F = -1;
                final float customImageAnchorRatioFromTopLeftOriginX_F = poiItem.getCustomImageAnchorRatioFromTopLeftOriginX();
                final float customImageAnchorRatioFromTopLeftOriginY_F = poiItem.getCustomImageAnchorRatioFromTopLeftOriginY();
                final boolean customImageAutoscale = poiItem.isCustomImageAutoscale();
                final String leftSideButtonResourceIdOnCalloutBalloon_F = ResourceUtils.getResourcePath(poiItem.getLeftSideButtonResourceIdOnCalloutBalloon());
                final String rightSideButtonResourceIdOnCalloutBalloon_F = ResourceUtils.getResourcePath(poiItem.getRightSideButtonResourceIdOnCalloutBalloon());
                final boolean useCalloutBalloonAdapter = this.calloutBalloonAdapter != null;
                final boolean moveToCenterOnSelect = poiItem.isMoveToCenterOnSelect();
                String customImageFileAbsolutePath = null;
                if (poiItem.getMarkerType() == MarkerType.CustomImage) {
                    if (poiItem.getCustomImageResourceId() != 0) {
                        customImageResourcePath = poiItem.getCustomImageResourcePath();
                    } else {
                        file = BitmapUtils.saveBitmapAsPngFile(getContext(), poiItem.getCustomImageBitmap(), "image/custom_info_window", poiItem.getCustomImageFileName());
                        if (file.exists()) {
                            customImageFileAbsolutePath = ResourceUtils.getResourceAbsolutePath(file.getAbsolutePath());
                        }
                        poiItem_.setCustomImageBitmap(null);
                    }
                    ImageOffset imageAnchorPointOffset = poiItem.getCustomImageAnchorPointOffset();
                    if (imageAnchorPointOffset != null) {
                        int customImageAnchorPointOffsetX = imageAnchorPointOffset.offsetX;
                        int customImageAnchorPointOffsetY = imageAnchorPointOffset.offsetY;
                    }
                }
                String customPressedImageFileAbsolutePath = null;
                if (poiItem.getSelectedMarkerType() == MarkerType.CustomImage) {
                    if (poiItem.getCustomSelectedImageResourceId() != 0) {
                        customSelectedImageResourcePath = poiItem.getCustomSelectedImageResourcePath();
                    } else {
                        file = BitmapUtils.saveBitmapAsPngFile(getContext(), poiItem.getCustomSelectedImageBitmap(), "image/custom_info_window", poiItem.getCustomSelectedImageFileName());
                        if (file.exists()) {
                            customPressedImageFileAbsolutePath = ResourceUtils.getResourceAbsolutePath(file.getAbsolutePath());
                        }
                        poiItem_.setCustomSelectedImageBitmap(null);
                    }
                }
                final String customImageResourcePath_F = customImageResourcePath;
                final String customSelectedImageResourcePath_F = customSelectedImageResourcePath;
                String customInfoWindowImageFileAbsolutePath = null;
                String customPressedInfoWindowImageFileAbsolutePath = null;
                if (this.needSynchronousCalloutBalloonGeneration) {
                    if (useCalloutBalloonAdapter) {
                        View ll = wrapViewWithLinearLayout(this.calloutBalloonAdapter.getCalloutBalloon(poiItem_));
                        poiItem_.setCustomCalloutBalloon(ll);
                        if (ll != null) {
                            ll.removeAllViews();
                        }
                        View llPressed = wrapViewWithLinearLayout(this.calloutBalloonAdapter.getPressedCalloutBalloon(poiItem_));
                        poiItem_.setCustomPressedCalloutBalloon(llPressed);
                        if (llPressed != null) {
                            llPressed.removeAllViews();
                        }
                    }
                    Bitmap customCalloutBalloonBitmap = poiItem_.getCustomCalloutBalloonBitmap();
                    if (customCalloutBalloonBitmap != null) {
                        file = BitmapUtils.saveBitmapAsPngFile(getContext(), customCalloutBalloonBitmap, "image/custom_info_window", poiItem.getCustomCalloutBalloonImageFileName());
                        if (file.exists()) {
                            customInfoWindowImageFileAbsolutePath = ResourceUtils.getResourceAbsolutePath(file.getAbsolutePath());
                        }
                        Bitmap customPressedCalloutBalloonBitmap = poiItem_.getCustomPressedCalloutBalloonBitmap();
                        if (customPressedCalloutBalloonBitmap == null) {
                            View customCalloutBalloonView = poiItem_.getCustomCalloutBalloon();
                            if (useCalloutBalloonAdapter) {
                                customCalloutBalloonView = this.calloutBalloonAdapter.getCalloutBalloon(poiItem_);
                                LinearLayout ll2 = wrapViewWithLinearLayout(customCalloutBalloonView);
                                if (ll2 != null) {
                                    ll2.removeAllViews();
                                }
                            }
                            if (customCalloutBalloonView != null) {
                                Drawable bgBackup = customCalloutBalloonView.getBackground().getConstantState().newDrawable();
                                customCalloutBalloonView.getBackground().setColorFilter(858993459, Mode.SRC_ATOP);
                                customPressedCalloutBalloonBitmap = BitmapUtils.createBitmapFromView(customCalloutBalloonView);
                                customCalloutBalloonView.setBackgroundDrawable(bgBackup);
                            }
                        }
                        if (customPressedCalloutBalloonBitmap != null) {
                            File filePressed = BitmapUtils.saveBitmapAsPngFile(getContext(), customPressedCalloutBalloonBitmap, "image/custom_pressed_info_window", poiItem.getCustomPressedCalloutBalloonImageFileName());
                            if (filePressed.exists()) {
                                customPressedInfoWindowImageFileAbsolutePath = ResourceUtils.getResourceAbsolutePath(filePressed.getAbsolutePath());
                            }
                        }
                    }
                    poiItem_.setCustomCalloutBalloonBitmap(null);
                    poiItem_.setCustomPressedCalloutBalloonBitmap(null);
                }
                final String customInfoWindowImageFileAbsolutePath_F = customInfoWindowImageFileAbsolutePath;
                final String customPressedInfoWindowImageFileAbsolutePath_F = customPressedInfoWindowImageFileAbsolutePath;
                final String customImageFileAbsolutePath_F = customImageFileAbsolutePath;
                final String customPressedImageFileAbsolutePath_F = customPressedImageFileAbsolutePath;
                final String str = itemName;
                final NativeMapCoord nativeMapCoord = nativeMapPoint;
                final MapPOIItem mapPOIItem = poiItem;
                MapTaskManager.getInstance().queueTask(new Runnable() {
                    public void run() {
                        int id = NativePOIItemMarkerManager.addPOIItemMarkerToMapView(str, nativeMapCoord, markerType, selectedMarkerType, showAnimationType, showCalloutBalloonOnTouch, showDisclosureButtonOnCalloutBalloon, draggable, alpha, rotation, customImageResourcePath_F, customSelectedImageResourcePath_F, customImageFileAbsolutePath_F, customPressedImageFileAbsolutePath_F, customImageAnchorPointOffsetX_F, customImageAnchorPointOffsetY_F, customImageAnchorRatioFromTopLeftOriginX_F, customImageAnchorRatioFromTopLeftOriginY_F, -1, -1, customImageAutoscale, leftSideButtonResourceIdOnCalloutBalloon_F, rightSideButtonResourceIdOnCalloutBalloon_F, useCalloutBalloonAdapter, moveToCenterOnSelect);
                        poiItem_.setId(id);
                        if (MapView.this.needSynchronousCalloutBalloonGeneration) {
                            if (customInfoWindowImageFileAbsolutePath_F != null) {
                                NativePOIItemMarkerManager.setCustomCalloutBalloonImageFilePath(id, customInfoWindowImageFileAbsolutePath_F);
                            }
                            if (customPressedInfoWindowImageFileAbsolutePath_F != null) {
                                NativePOIItemMarkerManager.setCustomPressedCalloutBalloonImageFilePath(id, customPressedInfoWindowImageFileAbsolutePath_F);
                                return;
                            }
                            return;
                        }
                        Bitmap customCalloutBalloonBitmap = poiItem_.getCustomCalloutBalloonBitmap();
                        if (customCalloutBalloonBitmap != null) {
                            File file = BitmapUtils.saveBitmapAsPngFile(MapView.this.getContext(), customCalloutBalloonBitmap, "image/custom_info_window", mapPOIItem.getCustomCalloutBalloonImageFileName());
                            if (file.exists()) {
                                NativePOIItemMarkerManager.setCustomCalloutBalloonImageFilePath(id, ResourceUtils.getResourceAbsolutePath(file.getAbsolutePath()));
                            }
                            Bitmap customPressedCalloutBalloonBitmap = poiItem_.getCustomPressedCalloutBalloonBitmap();
                            if (customPressedCalloutBalloonBitmap == null) {
                                View customCalloutBalloonView = poiItem_.getCustomCalloutBalloon();
                                if (customCalloutBalloonView != null) {
                                    Drawable bgBackup = customCalloutBalloonView.getBackground().getConstantState().newDrawable();
                                    customCalloutBalloonView.getBackground().setColorFilter(858993459, Mode.SRC_ATOP);
                                    customPressedCalloutBalloonBitmap = BitmapUtils.createBitmapFromView(customCalloutBalloonView);
                                    customCalloutBalloonView.setBackgroundDrawable(bgBackup);
                                }
                            }
                            if (customPressedCalloutBalloonBitmap != null) {
                                File filePressed = BitmapUtils.saveBitmapAsPngFile(MapView.this.getContext(), customPressedCalloutBalloonBitmap, "image/custom_pressed_info_window", mapPOIItem.getCustomPressedCalloutBalloonImageFileName());
                                if (filePressed.exists()) {
                                    NativePOIItemMarkerManager.setCustomPressedCalloutBalloonImageFilePath(id, ResourceUtils.getResourceAbsolutePath(filePressed.getAbsolutePath()));
                                }
                            }
                        }
                    }
                }, MapEngineManager.getInstance().getStopGlSwap());
            }
        }
    }

    private int convertMarkerType(MarkerType markerType) {
        if (markerType == MarkerType.BluePin) {
            return 1;
        }
        if (markerType == MarkerType.RedPin) {
            return 2;
        }
        if (markerType == MarkerType.YellowPin) {
            return 3;
        }
        if (markerType == MarkerType.CustomImage) {
            return 4;
        }
        return 0;
    }

    public void addPOIItems(MapPOIItem[] poiItems) {
        for (MapPOIItem poiItem : poiItems) {
            addPOIItem(poiItem);
        }
    }

    public void selectPOIItem(MapPOIItem poiItem, boolean animated) {
        final MapPOIItem poiItemF = poiItem;
        final boolean animatedF = animated;
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                NativePOIItemMarkerManager.selectPOIItemMarker(poiItemF.getId(), animatedF);
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public void deselectPOIItem(MapPOIItem poiItem) {
        final MapPOIItem poiItemF = poiItem;
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                NativePOIItemMarkerManager.deselectPOIItemMarker(poiItemF.getId());
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public MapPOIItem findPOIItemByTag(int tag) {
        for (MapPOIItem poiItem : this.poiItems) {
            if (poiItem.getTag() == tag) {
                return poiItem;
            }
        }
        return null;
    }

    public MapPOIItem[] findPOIItemByName(String itemName) {
        if (itemName == null) {
            return null;
        }
        ArrayList<MapPOIItem> foundPOIItems = null;
        for (MapPOIItem poiItem : this.poiItems) {
            String itemName_ = poiItem.getItemName();
            if (itemName_ != null && itemName_.equals(itemName)) {
                if (foundPOIItems == null) {
                    foundPOIItems = new ArrayList();
                }
                foundPOIItems.add(poiItem);
            }
        }
        if (foundPOIItems != null) {
            return (MapPOIItem[]) foundPOIItems.toArray(new MapPOIItem[0]);
        }
        return null;
    }

    public void removePOIItem(MapPOIItem poiItem) {
        final MapPOIItem poiItemF = poiItem;
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                NativePOIItemMarkerManager.removePOIItemMarker(poiItemF.getId());
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
        BitmapUtils.deleteFile(getContext(), "image/custom_info_window", poiItem.getCustomCalloutBalloonImageFileName());
        BitmapUtils.deleteFile(getContext(), "image/custom_pressed_info_window", poiItem.getCustomPressedCalloutBalloonImageFileName());
        this.poiItems.remove(poiItem);
    }

    public void removePOIItems(MapPOIItem[] poiItems) {
        for (MapPOIItem poiItem : poiItems) {
            removePOIItem(poiItem);
        }
    }

    public void removeAllPOIItems() {
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                NativePOIItemMarkerManager.removeAllPOIItemMarkers();
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
        BitmapUtils.deleteAllFilesInDirectory(getContext(), "image/custom_info_window");
        BitmapUtils.deleteAllFilesInDirectory(getContext(), "image/custom_pressed_info_window");
        this.poiItems.clear();
    }

    public void fitMapViewAreaToShowAllPOIItems() {
        int poiItemCount = this.poiItems.size();
        if (poiItemCount != 0) {
            MapPoint[] mapPoints = new MapPoint[poiItemCount];
            int i = 0;
            for (MapPOIItem poiItem : this.poiItems) {
                int i2 = i + 1;
                mapPoints[i] = poiItem.getMapPoint();
                i = i2;
            }
            fitMapViewAreaToShowMapPoints(mapPoints);
        }
    }

    public MapCircle[] getCircles() {
        return (MapCircle[]) this.circles.toArray(new MapCircle[0]);
    }

    public void addCircle(final MapCircle circle) {
        this.circles.add(circle);
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                MapPoint mapPoint = circle.getCenter();
                circle.setId(NativeCircleOverlayManager.addCircleToMap(new NativeMapCoord(mapPoint.getMapPointWCONGCoord().f13x, mapPoint.getMapPointWCONGCoord().f14y, 2), circle.getRadius(), circle.getStrokeColor(), 1, circle.getFillColor()));
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public MapCircle findCircleByTag(int tag) {
        Iterator i$ = this.circles.iterator();
        while (i$.hasNext()) {
            MapCircle circle = (MapCircle) i$.next();
            if (circle.getTag() == tag) {
                return circle;
            }
        }
        return null;
    }

    public void removeCircle(MapCircle circle) {
        final MapCircle circleF = circle;
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                NativeCircleOverlayManager.removeCircle(circleF.getId());
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
        this.circles.remove(circle);
    }

    public void removeAllCircles() {
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                NativeCircleOverlayManager.removeAllCircles();
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
        this.circles.clear();
    }

    public void fitMapViewAreaToShowCircle(MapCircle circle) {
        if (circle != null) {
            MapPoint[] mapPoints = new MapPoint[2];
            PlainCoordinate point = circle.getCenter().getMapPointWCONGCoord();
            int radius = circle.getRadius();
            mapPoints[0] = MapPoint.mapPointWithWCONGCoord(point.f13x - (((double) radius) * 2.5d), point.f14y - (((double) radius) * 2.5d));
            mapPoints[1] = MapPoint.mapPointWithWCONGCoord(point.f13x + (((double) radius) * 2.5d), point.f14y + (((double) radius) * 2.5d));
            fitMapViewAreaToShowMapPoints(mapPoints);
        }
    }

    public void fitMapViewAreaToShowAllCircle() {
        if (this.circles != null && this.circles.size() != 0) {
            MapPoint[] mapPoints = new MapPoint[2];
            double minx = Double.POSITIVE_INFINITY;
            double maxx = Double.NEGATIVE_INFINITY;
            double miny = Double.POSITIVE_INFINITY;
            double maxy = Double.NEGATIVE_INFINITY;
            Iterator i$ = this.circles.iterator();
            while (i$.hasNext()) {
                MapCircle circle = (MapCircle) i$.next();
                PlainCoordinate point = circle.getCenter().getMapPointWCONGCoord();
                int radius = circle.getRadius();
                minx = Math.min(minx, point.f13x - (((double) radius) * 2.5d));
                maxx = Math.max(maxx, point.f13x + (((double) radius) * 2.5d));
                miny = Math.min(miny, point.f14y - (((double) radius) * 2.5d));
                maxy = Math.max(maxy, point.f14y + (((double) radius) * 2.5d));
            }
            mapPoints[0] = MapPoint.mapPointWithWCONGCoord(minx, miny);
            mapPoints[1] = MapPoint.mapPointWithWCONGCoord(maxx, maxy);
            fitMapViewAreaToShowMapPoints(mapPoints);
        }
    }

    public MapPolyline[] getPolylines() {
        return (MapPolyline[]) this.polylines.toArray(new MapPolyline[0]);
    }

    public void addPolyline(MapPolyline polyline) {
        this.polylines.add(polyline);
        final MapPolyline _polyline = polyline;
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                _polyline.setId(NativePolylineOverlayManager.addPolylineToMap(_polyline));
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
    }

    public MapPolyline findPolylineByTag(int tag) {
        Iterator i$ = this.polylines.iterator();
        while (i$.hasNext()) {
            MapPolyline polyline = (MapPolyline) i$.next();
            if (polyline.getTag() == tag) {
                return polyline;
            }
        }
        return null;
    }

    public void removePolyline(MapPolyline polyline) {
        final MapPolyline polylineF = polyline;
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                NativePolylineOverlayManager.removePolyline(polylineF.getId());
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
        this.polylines.remove(polyline);
    }

    public void removeAllPolylines() {
        MapTaskManager.getInstance().queueTask(new Runnable() {
            public void run() {
                NativePolylineOverlayManager.removeAllPolylines();
            }
        }, MapEngineManager.getInstance().getStopGlSwap());
        this.polylines.clear();
    }

    public void fitMapViewAreaToShowPolyline(MapPolyline polyline) {
        if (polyline != null) {
            int pointCount = polyline.getPointCount();
            MapPoint[] mapPoints = new MapPoint[pointCount];
            for (int i = 0; i < pointCount; i++) {
                mapPoints[i] = polyline.getPoint(i);
            }
            fitMapViewAreaToShowMapPoints(mapPoints);
        }
    }

    public void fitMapViewAreaToShowAllPolylines() {
        int i;
        int polylineCount = this.polylines.size();
        int totalPointCount = 0;
        for (i = 0; i < polylineCount; i++) {
            totalPointCount += ((MapPolyline) this.polylines.get(i)).getPointCount();
        }
        if (totalPointCount > 0) {
            MapPoint[] mapPoints = new MapPoint[totalPointCount];
            int pointIndex = 0;
            i = 0;
            while (i < polylineCount) {
                MapPolyline polyline = (MapPolyline) this.polylines.get(i);
                int pointCountInPolyline = polyline.getPointCount();
                int j = 0;
                int pointIndex2 = pointIndex;
                while (j < pointCountInPolyline) {
                    pointIndex = pointIndex2 + 1;
                    mapPoints[pointIndex2] = polyline.getPoint(j);
                    j++;
                    pointIndex2 = pointIndex;
                }
                i++;
                pointIndex = pointIndex2;
            }
            fitMapViewAreaToShowMapPoints(mapPoints);
        }
    }

    public void moveCamera(CameraUpdate cameraUpdate) {
        NativeMapCoord[] nativeMapCoords;
        PlainCoordinate mapPointBottomLeftWCONG;
        PlainCoordinate mapPointTopRightWCONG;
        switch (cameraUpdate.mUpdateType) {
            case UPDATE_WITH_MAP_POINT:
                if (cameraUpdate.mMapPoint != null) {
                    MapController.getInstance().move(cameraUpdate.mMapPoint.getInternalCoordObject(), true);
                    return;
                }
                return;
            case UPDATE_WITH_MAP_POINT_AND_ZOOM_LEVEL:
                if (cameraUpdate.mMapPoint != null) {
                    MapController.getInstance().move(cameraUpdate.mMapPoint.getInternalCoordObject(), (float) Math.max(0, Math.round(cameraUpdate.mZoomLevel)), true);
                    return;
                }
                return;
            case UPDATE_WITH_MAP_POINT_AND_DIAMETER:
                if (cameraUpdate.mMapPoint != null) {
                    MapController.getInstance().updateCameraWithMapPointAndDiameter(cameraUpdate.mMapPoint.getInternalCoordObject(), cameraUpdate.mDiameter);
                    return;
                }
                return;
            case UPDATE_WITH_MAP_POINT_AND_DIAMETER_AND_PADDING:
                if (cameraUpdate.mMapPoint != null) {
                    MapController.getInstance().updateCameraWithMapPointAndDiameterAndPadding(cameraUpdate.mMapPoint.getInternalCoordObject(), cameraUpdate.mDiameter, cameraUpdate.mPadding);
                    return;
                }
                return;
            case UPDATE_WITH_MAP_POINT_BOUNDS:
                if (cameraUpdate.mMapPointBounds != null && cameraUpdate.mMapPointBounds.bottomLeft != null && cameraUpdate.mMapPointBounds.topRight != null) {
                    nativeMapCoords = new NativeMapCoord[2];
                    mapPointBottomLeftWCONG = cameraUpdate.mMapPointBounds.bottomLeft.getMapPointWCONGCoord();
                    nativeMapCoords[0] = new NativeMapCoord(mapPointBottomLeftWCONG.f13x, mapPointBottomLeftWCONG.f14y);
                    mapPointTopRightWCONG = cameraUpdate.mMapPointBounds.topRight.getMapPointWCONGCoord();
                    nativeMapCoords[1] = new NativeMapCoord(mapPointTopRightWCONG.f13x, mapPointTopRightWCONG.f14y);
                    MapController.getInstance().updateCameraWithMapPoints(nativeMapCoords);
                    return;
                }
                return;
            case UPDATE_WITH_MAP_POINT_BOUNDS_AND_PADDING:
                if (cameraUpdate.mMapPointBounds != null && cameraUpdate.mMapPointBounds.bottomLeft != null && cameraUpdate.mMapPointBounds.topRight != null) {
                    nativeMapCoords = new NativeMapCoord[2];
                    mapPointBottomLeftWCONG = cameraUpdate.mMapPointBounds.bottomLeft.getMapPointWCONGCoord();
                    nativeMapCoords[0] = new NativeMapCoord(mapPointBottomLeftWCONG.f13x, mapPointBottomLeftWCONG.f14y);
                    mapPointTopRightWCONG = cameraUpdate.mMapPointBounds.topRight.getMapPointWCONGCoord();
                    nativeMapCoords[1] = new NativeMapCoord(mapPointTopRightWCONG.f13x, mapPointTopRightWCONG.f14y);
                    MapController.getInstance().updateCameraWithMapPointsAndPadding(nativeMapCoords, cameraUpdate.mPadding);
                    return;
                }
                return;
            case UPDATE_WITH_MAP_POINT_BOUNDS_AND_PADDING_AND_MIN_ZOOM_LEVEL_AND_MAX_ZOOM_LEVEL:
                if (cameraUpdate.mMapPointBounds != null && cameraUpdate.mMapPointBounds.bottomLeft != null && cameraUpdate.mMapPointBounds.topRight != null) {
                    nativeMapCoords = new NativeMapCoord[2];
                    mapPointBottomLeftWCONG = cameraUpdate.mMapPointBounds.bottomLeft.getMapPointWCONGCoord();
                    nativeMapCoords[0] = new NativeMapCoord(mapPointBottomLeftWCONG.f13x, mapPointBottomLeftWCONG.f14y);
                    mapPointTopRightWCONG = cameraUpdate.mMapPointBounds.topRight.getMapPointWCONGCoord();
                    nativeMapCoords[1] = new NativeMapCoord(mapPointTopRightWCONG.f13x, mapPointTopRightWCONG.f14y);
                    float minZoomLevel = cameraUpdate.mMinZoomLevel;
                    float maxZoomLevel = cameraUpdate.mMaxZoomLevel;
                    if (minZoomLevel > maxZoomLevel) {
                        float tmp = minZoomLevel;
                        minZoomLevel = maxZoomLevel;
                        maxZoomLevel = tmp;
                    }
                    MapController.getInstance().m3x6d3014d3(nativeMapCoords, cameraUpdate.mPadding, minZoomLevel, maxZoomLevel);
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void animateCamera(CameraUpdate cameraUpdate) {
        animateCamera(cameraUpdate, 1000.0f, null);
    }

    public void animateCamera(CameraUpdate cameraUpdate, CancelableCallback callback) {
        animateCamera(cameraUpdate, 1000.0f, callback);
    }

    public void animateCamera(CameraUpdate cameraUpdate, float duration, CancelableCallback callback) {
        if (duration < 0.0f) {
            duration = 1000.0f;
        }
        final double currentZoomLevel = (double) getZoomLevelFloat();
        double targetZoomLevel = (double) cameraUpdate.mZoomLevel;
        if (targetZoomLevel < ((double) MIN_ZOOM_LEVEL)) {
            targetZoomLevel = currentZoomLevel;
        }
        MapPoint currentMapPoint = getMapCenterPoint();
        final MapPoint targetMapPoint = cameraUpdate.mMapPoint;
        final MapCoord currentCoord = currentMapPoint.getInternalCoordObject();
        final MapCoord targetCoord = targetMapPoint.getInternalCoordObject();
        double distanceWcong = Math.hypot(currentCoord.getX() - targetCoord.getX(), currentCoord.getY() - targetCoord.getY());
        final double dx = targetCoord.getX() - currentCoord.getX();
        final double dy = targetCoord.getY() - currentCoord.getY();
        Display display = ((WindowManager) getContext().getSystemService("window")).getDefaultDisplay();
        MapPoint leftScreenMapPoint = MapPoint.mapPointWithScreenLocation(0.0d, 0.0d);
        MapPoint rightScreenMapPoint = MapPoint.mapPointWithScreenLocation((double) display.getWidth(), 0.0d);
        final double targetZoomLevelFinal = targetZoomLevel;
        final double peakZoomLevelFinal = Math.min(Math.max(Math.max(currentZoomLevel + (Math.log((distanceWcong / 2.0d) / Math.abs(leftScreenMapPoint.getInternalCoordObject().getX() - rightScreenMapPoint.getInternalCoordObject().getX())) / Math.log(2.0d)), currentZoomLevel), targetZoomLevel), (double) MAX_ZOOM_LEVEL);
        final int timerCountLimit = (int) Math.floor((double) (duration / 33.0f));
        final int timerCountPerPhase = timerCountLimit / 2;
        if (this.cameraAnimationTimer != null) {
            this.cameraAnimationTimer.cancel();
            this.cameraAnimationTimer = null;
            if (this.cameraAnimationCancelableCallback != null) {
                this.cameraAnimationCancelableCallback.onCancel();
                this.cameraAnimationCancelableCallback = null;
            }
        }
        this.cameraAnimationCancelableCallback = callback;
        this.cameraAnimationTimer = new Timer();
        final CancelableCallback cancelableCallback = callback;
        this.cameraAnimationTimer.schedule(new TimerTask() {
            int timerCount = 0;
            /* renamed from: x */
            double f15x;
            /* renamed from: y */
            double f16y;
            double zoomLevel = currentZoomLevel;

            /* renamed from: net.daum.mf.map.api.MapView$19$1 */
            class C03801 implements Runnable {
                C03801() {
                }

                public void run() {
                    if (cancelableCallback != null) {
                        cancelableCallback.onFinish();
                    }
                }
            }

            public void run() {
                if (this.timerCount == timerCountLimit) {
                    update(targetZoomLevelFinal, targetMapPoint);
                    MapView.this.cameraAnimationTimer.cancel();
                    MapView.this.cameraAnimationTimer = null;
                    new Handler(Looper.getMainLooper()).post(new C03801());
                    return;
                }
                double distanceRate = ((-Math.cos((3.141592653589793d * ((double) this.timerCount)) / ((double) timerCountLimit))) + 1.0d) / ((-Math.cos(3.141592653589793d)) + 1.0d);
                boolean isUpPhase = this.timerCount < timerCountPerPhase;
                boolean isDownPhase = this.timerCount >= timerCountLimit - timerCountPerPhase;
                double peakPhaseDx = 0.0d;
                double peakPhaseDy = 0.0d;
                if (isUpPhase) {
                    this.zoomLevel = currentZoomLevel + ((peakZoomLevelFinal - currentZoomLevel) * Math.sin(((3.141592653589793d * ((double) this.timerCount)) / ((double) timerCountPerPhase)) / 2.0d));
                    this.zoomLevel = Math.min(this.zoomLevel, peakZoomLevelFinal);
                    this.f15x = currentCoord.getX() + (dx * distanceRate);
                    this.f16y = currentCoord.getY() + (dy * distanceRate);
                } else if (isDownPhase) {
                    this.zoomLevel = targetZoomLevelFinal + ((peakZoomLevelFinal - targetZoomLevelFinal) * Math.sin(3.141592653589793d * (((0.5d * ((double) (timerCountPerPhase - (timerCountLimit - this.timerCount)))) / ((double) timerCountPerPhase)) + 0.5d)));
                    this.zoomLevel = Math.max(this.zoomLevel, targetZoomLevelFinal);
                    this.f15x = currentCoord.getX() + (dx * distanceRate);
                    this.f16y = currentCoord.getY() + (dy * distanceRate);
                } else {
                    if (!false) {
                        peakPhaseDx = Math.abs(dx) - (Math.abs(currentCoord.getX() - this.f15x) * 2.0d);
                        if (targetCoord.getX() < currentCoord.getX()) {
                            peakPhaseDx = -peakPhaseDx;
                        }
                        peakPhaseDy = Math.abs(dy) - (Math.abs(currentCoord.getY() - this.f16y) * 2.0d);
                        if (targetCoord.getY() < currentCoord.getY()) {
                            peakPhaseDy = -peakPhaseDy;
                        }
                    }
                    this.f15x += peakPhaseDx;
                    this.f16y += peakPhaseDy;
                    this.zoomLevel = peakZoomLevelFinal;
                }
                update(this.zoomLevel, MapPoint.mapPointWithWCONGCoord(this.f15x, this.f16y));
                this.timerCount++;
            }

            void update(double zoomLevel, MapPoint point) {
                float zoomLevelFloat = (float) zoomLevel;
                MapController.getInstance().move(point.getInternalCoordObject(), zoomLevelFloat, true);
            }
        }, 0, 33);
    }

    public void stopAnimation() {
        if (this.cameraAnimationTimer != null) {
            this.cameraAnimationTimer.cancel();
            this.cameraAnimationTimer = null;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    if (MapView.this.cameraAnimationCancelableCallback != null) {
                        MapView.this.cameraAnimationCancelableCallback.onCancel();
                    }
                }
            });
        }
    }

    void onOpenAPIKeyAuthenticationResult(final int resultCode, final String resultMessage) {
        Activity activity = (Activity) this.dmapActivity.get();
        if (activity != null && this.openAPIKeyAuthenticationResultListener != null && this.openAPIKeyAuthenticationResultListener.get() != null) {
            final MapView mapView = this;
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    if (MapView.this.openAPIKeyAuthenticationResultListener != null) {
                        OpenAPIKeyAuthenticationResultListener listener = (OpenAPIKeyAuthenticationResultListener) MapView.this.openAPIKeyAuthenticationResultListener.get();
                        if (listener != null) {
                            listener.onDaumMapOpenAPIKeyAuthenticationResult(mapView, resultCode, resultMessage);
                        }
                    }
                }
            });
        }
    }

    void onMapViewLoaded() {
        if (!this.mapEngineLoadedForThisMapView) {
            this.mapEngineLoadedForThisMapView = true;
            if (this.mapViewEventListener != null && this.mapViewEventListener.get() == null) {
            }
        }
    }

    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        if (this.isFirstOnDrawFrame) {
            this.isFirstOnDrawFrame = false;
            Activity activity = (Activity) this.dmapActivity.get();
            if (activity != null) {
                final MapView mapView = this;
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        if (MapView.this.mapViewEventListener != null) {
                            MapViewEventListener listener = (MapViewEventListener) MapView.this.mapViewEventListener.get();
                            if (listener != null) {
                                ViewParent parent = mapView.getParent();
                                if (parent != null && (parent instanceof ViewGroup)) {
                                    ViewGroup viewGroup = (ViewGroup) parent;
                                    View viewToShowBeforeMapViewInitialized = viewGroup.findViewWithTag(MapLayout.VIEW_TO_SHOW_BEFORE_MAP_VIEW_INITIALIZED);
                                    if (viewToShowBeforeMapViewInitialized != null) {
                                        viewGroup.removeView(viewToShowBeforeMapViewInitialized);
                                    }
                                }
                                listener.onMapViewInitialized(mapView);
                            }
                        }
                    }
                });
            }
        }
    }

    void onMapViewCenterPointMoved(double centerPointX, double centerPointY) {
        Activity activity = (Activity) this.dmapActivity.get();
        if (activity != null && this.mapViewEventListener != null && this.mapViewEventListener.get() != null) {
            final MapView mapView = this;
            final double d = centerPointX;
            final double d2 = centerPointY;
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    if (MapView.this.mapViewEventListener != null) {
                        MapViewEventListener listener = (MapViewEventListener) MapView.this.mapViewEventListener.get();
                        if (listener != null) {
                            if (!(MapView.this.dragStarted || MapView.this.dragStartedMapPoint == null)) {
                                MapView.this.dragStarted = true;
                                listener.onMapViewDragStarted(mapView, MapView.this.dragStartedMapPoint);
                            }
                            listener.onMapViewCenterPointMoved(mapView, MapPoint.mapPointWithWCONGCoord(d, d2));
                        }
                    }
                }
            });
        }
    }

    void onMapViewZoomLevelChanged(final int zoomLevel) {
        Activity activity = (Activity) this.dmapActivity.get();
        if (activity != null && this.mapViewEventListener != null && this.mapViewEventListener.get() != null) {
            final MapView mapView = this;
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    if (MapView.this.mapViewEventListener != null) {
                        MapViewEventListener listener = (MapViewEventListener) MapView.this.mapViewEventListener.get();
                        if (listener != null) {
                            listener.onMapViewZoomLevelChanged(mapView, zoomLevel);
                        }
                    }
                }
            });
        }
    }

    void onMapViewSingleTapped(double mapPointX, double mapPointY) {
        Activity activity = (Activity) this.dmapActivity.get();
        if (activity != null && this.mapViewEventListener != null && this.mapViewEventListener.get() != null) {
            final MapView mapView = this;
            final double d = mapPointX;
            final double d2 = mapPointY;
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    if (MapView.this.mapViewEventListener != null) {
                        MapViewEventListener listener = (MapViewEventListener) MapView.this.mapViewEventListener.get();
                        if (listener != null) {
                            listener.onMapViewSingleTapped(mapView, MapPoint.mapPointWithWCONGCoord(d, d2));
                        }
                    }
                }
            });
        }
    }

    void onMapViewDoubleTapped(double mapPointX, double mapPointY) {
        Activity activity = (Activity) this.dmapActivity.get();
        if (activity != null && this.mapViewEventListener != null && this.mapViewEventListener.get() != null) {
            final MapView mapView = this;
            final double d = mapPointX;
            final double d2 = mapPointY;
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    if (MapView.this.mapViewEventListener != null) {
                        MapViewEventListener listener = (MapViewEventListener) MapView.this.mapViewEventListener.get();
                        if (listener != null) {
                            listener.onMapViewDoubleTapped(mapView, MapPoint.mapPointWithWCONGCoord(d, d2));
                        }
                    }
                }
            });
        }
    }

    void onMapViewLongPressed(double mapPointX, double mapPointY) {
        Activity activity = (Activity) this.dmapActivity.get();
        if (activity != null && this.mapViewEventListener != null && this.mapViewEventListener.get() != null) {
            final MapView mapView = this;
            final double d = mapPointX;
            final double d2 = mapPointY;
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    if (MapView.this.mapViewEventListener != null) {
                        MapViewEventListener listener = (MapViewEventListener) MapView.this.mapViewEventListener.get();
                        if (listener != null) {
                            listener.onMapViewLongPressed(mapView, MapPoint.mapPointWithWCONGCoord(d, d2));
                        }
                    }
                }
            });
        }
    }

    void onMapViewDragStarted(double mapPointX, double mapPointY) {
        stopAnimation();
        this.dragStartedMapPoint = MapPoint.mapPointWithWCONGCoord(mapPointX, mapPointY);
    }

    void onMapViewDragEnded(double mapPointX, double mapPointY) {
        Activity activity = (Activity) this.dmapActivity.get();
        if (activity != null && this.dragStarted) {
            this.dragStarted = false;
            if (this.mapViewEventListener != null && this.mapViewEventListener.get() != null) {
                final MapView mapView = this;
                final double d = mapPointX;
                final double d2 = mapPointY;
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        if (MapView.this.mapViewEventListener != null) {
                            MapViewEventListener listener = (MapViewEventListener) MapView.this.mapViewEventListener.get();
                            if (listener != null) {
                                listener.onMapViewDragEnded(mapView, MapPoint.mapPointWithWCONGCoord(d, d2));
                            }
                        }
                    }
                });
            }
        }
    }

    void onMapViewMoveFinished(double mapPointX, double mapPointY) {
        Activity activity = (Activity) this.dmapActivity.get();
        if (activity != null && this.mapViewEventListener != null && this.mapViewEventListener.get() != null) {
            final MapView mapView = this;
            final double d = mapPointX;
            final double d2 = mapPointY;
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    if (MapView.this.mapViewEventListener != null) {
                        MapViewEventListener listener = (MapViewEventListener) MapView.this.mapViewEventListener.get();
                        if (listener != null) {
                            listener.onMapViewMoveFinished(mapView, MapPoint.mapPointWithWCONGCoord(d, d2));
                        }
                    }
                }
            });
        }
    }

    void onCurrentLocationUpdate(double latitude, double longitude, float accuracy) {
        Activity activity = (Activity) this.dmapActivity.get();
        if (activity != null && this.currentLocationEventListener != null && this.currentLocationEventListener.get() != null) {
            final MapView mapView = this;
            final double d = latitude;
            final double d2 = longitude;
            final float f = accuracy;
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    if (MapView.this.currentLocationEventListener != null) {
                        CurrentLocationEventListener listener = (CurrentLocationEventListener) MapView.this.currentLocationEventListener.get();
                        if (listener != null) {
                            listener.onCurrentLocationUpdate(mapView, MapPoint.mapPointWithGeoCoord(d, d2), f);
                        }
                    }
                }
            });
        }
    }

    void onCurrentLocationDeviceHeadingUpdate(final float headingAngle) {
        Activity activity = (Activity) this.dmapActivity.get();
        if (activity != null && this.currentLocationEventListener != null && this.currentLocationEventListener.get() != null) {
            final MapView mapView = this;
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    if (MapView.this.currentLocationEventListener != null) {
                        CurrentLocationEventListener listener = (CurrentLocationEventListener) MapView.this.currentLocationEventListener.get();
                        if (listener != null) {
                            listener.onCurrentLocationDeviceHeadingUpdate(mapView, headingAngle);
                        }
                    }
                }
            });
        }
    }

    void onCurrentLocationUpdateFailed() {
        Activity activity = (Activity) this.dmapActivity.get();
        if (activity != null && this.currentLocationEventListener != null && this.currentLocationEventListener.get() != null) {
            final MapView mapView = this;
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    if (MapView.this.currentLocationEventListener != null) {
                        CurrentLocationEventListener listener = (CurrentLocationEventListener) MapView.this.currentLocationEventListener.get();
                        if (listener != null) {
                            listener.onCurrentLocationUpdateFailed(mapView);
                        }
                    }
                }
            });
        }
    }

    void onCurrentLocationUpdateCancelled() {
        Activity activity = (Activity) this.dmapActivity.get();
        if (activity != null && this.currentLocationEventListener != null && this.currentLocationEventListener.get() != null) {
            final MapView mapView = this;
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    if (MapView.this.currentLocationEventListener != null) {
                        CurrentLocationEventListener listener = (CurrentLocationEventListener) MapView.this.currentLocationEventListener.get();
                        if (listener != null) {
                            listener.onCurrentLocationUpdateCancelled(mapView);
                        }
                    }
                }
            });
        }
    }

    void onPOIItemSelected(final int poiMarkerId) {
        Activity activity = (Activity) this.dmapActivity.get();
        if (activity != null && this.poiItemEventListener != null && this.poiItemEventListener.get() != null) {
            final MapView mapView = this;
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    if (MapView.this.poiItemEventListener != null) {
                        POIItemEventListener listener = (POIItemEventListener) MapView.this.poiItemEventListener.get();
                        if (listener != null) {
                            for (MapPOIItem poiItem : MapView.this.poiItems) {
                                if (poiItem.getId() == poiMarkerId) {
                                    listener.onPOIItemSelected(mapView, poiItem);
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    LinearLayout wrapViewWithLinearLayout(View view) {
        if (view == null) {
            return null;
        }
        Activity activity = (Activity) this.dmapActivity.get();
        if (activity == null) {
            return null;
        }
        LinearLayout ll = new LinearLayout(activity);
        ll.setLayoutParams(new LayoutParams(-2, -2));
        ll.addView(view);
        return ll;
    }

    void prepareCalloutBalloonImageAndCallback(int poiMarkerId, boolean animated) {
        if (this.poiItems != null && this.poiItems.size() != 0 && this.calloutBalloonAdapter != null) {
            for (MapPOIItem poiItem : this.poiItems) {
                if (poiItem.getId() == poiMarkerId) {
                    LinearLayout ll;
                    Bitmap customCalloutBalloonBitmap = null;
                    Bitmap customPressedCalloutBalloonBitmap = null;
                    View customCalloutBalloonView = this.calloutBalloonAdapter.getCalloutBalloon(poiItem);
                    if (customCalloutBalloonView != null) {
                        ll = wrapViewWithLinearLayout(customCalloutBalloonView);
                        if (ll != null) {
                            customCalloutBalloonBitmap = BitmapUtils.createBitmapFromView(ll);
                            ll.removeAllViews();
                        } else {
                            customCalloutBalloonBitmap = BitmapUtils.createBitmapFromView(customCalloutBalloonView);
                        }
                    }
                    View customPressedCalloutBalloonView = this.calloutBalloonAdapter.getPressedCalloutBalloon(poiItem);
                    if (customPressedCalloutBalloonView != null) {
                        ll = wrapViewWithLinearLayout(customPressedCalloutBalloonView);
                        if (ll != null) {
                            customPressedCalloutBalloonBitmap = BitmapUtils.createBitmapFromView(ll);
                            ll.removeAllViews();
                        } else {
                            customPressedCalloutBalloonBitmap = BitmapUtils.createBitmapFromView(customPressedCalloutBalloonView);
                        }
                    }
                    if (customCalloutBalloonBitmap != null) {
                        String customInfoWindowImageFileAbsolutePath = null;
                        String customPressedInfoWindowImageFileAbsolutePath = null;
                        File file = BitmapUtils.saveBitmapAsPngFile(getContext(), customCalloutBalloonBitmap, "image/custom_info_window", poiItem.getCustomCalloutBalloonImageFileName());
                        if (file.exists()) {
                            customInfoWindowImageFileAbsolutePath = ResourceUtils.getResourceAbsolutePath(file.getAbsolutePath());
                        }
                        if (customPressedCalloutBalloonBitmap == null && customCalloutBalloonView != null) {
                            Drawable bgBackup = customCalloutBalloonView.getBackground().getConstantState().newDrawable();
                            customCalloutBalloonView.getBackground().setColorFilter(858993459, Mode.SRC_ATOP);
                            customPressedCalloutBalloonBitmap = BitmapUtils.createBitmapFromView(customCalloutBalloonView);
                            customCalloutBalloonView.setBackgroundDrawable(bgBackup);
                        }
                        if (customPressedCalloutBalloonBitmap != null) {
                            File filePressed = BitmapUtils.saveBitmapAsPngFile(getContext(), customPressedCalloutBalloonBitmap, "image/custom_pressed_info_window", poiItem.getCustomPressedCalloutBalloonImageFileName());
                            if (filePressed.exists()) {
                                customPressedInfoWindowImageFileAbsolutePath = ResourceUtils.getResourceAbsolutePath(filePressed.getAbsolutePath());
                            }
                        }
                        final String customInfoWindowImageFileAbsolutePath_F = customInfoWindowImageFileAbsolutePath;
                        final String customPressedInfoWindowImageFileAbsolutePath_F = customPressedInfoWindowImageFileAbsolutePath;
                        if (customInfoWindowImageFileAbsolutePath_F != null) {
                            final int i = poiMarkerId;
                            final boolean z = animated;
                            MapTaskManager.getInstance().queueTask(new Runnable() {
                                public void run() {
                                    if (customInfoWindowImageFileAbsolutePath_F != null) {
                                        NativePOIItemMarkerManager.setCustomCalloutBalloonImageFilePath(i, customInfoWindowImageFileAbsolutePath_F);
                                    }
                                    if (customPressedInfoWindowImageFileAbsolutePath_F != null) {
                                        NativePOIItemMarkerManager.setCustomPressedCalloutBalloonImageFilePath(i, customPressedInfoWindowImageFileAbsolutePath_F);
                                    }
                                    NativePOIItemMarkerManager.callbackAfterPrepareCalloutBalloonImage(i, z);
                                }
                            }, MapEngineManager.getInstance().getStopGlSwap());
                        }
                    } else {
                        final int i2 = poiMarkerId;
                        final boolean z2 = animated;
                        MapTaskManager.getInstance().queueTask(new Runnable() {
                            public void run() {
                                NativePOIItemMarkerManager.callbackAfterPrepareCalloutBalloonImage(i2, z2);
                            }
                        }, MapEngineManager.getInstance().getStopGlSwap());
                    }
                }
            }
        }
    }

    void onCalloutBalloonOfPOIItemTouched(final int poiMarkerId, int buttonType) {
        Activity activity = (Activity) this.dmapActivity.get();
        if (activity != null && this.poiItemEventListener != null && this.poiItemEventListener.get() != null) {
            CalloutBalloonButtonType calloutBalloonButtonType = CalloutBalloonButtonType.MainButton;
            if (buttonType == 1) {
                calloutBalloonButtonType = CalloutBalloonButtonType.MainButton;
            } else if (buttonType == 2) {
                calloutBalloonButtonType = CalloutBalloonButtonType.LeftSideButton;
            } else if (buttonType == 3) {
                calloutBalloonButtonType = CalloutBalloonButtonType.RightSideButton;
            } else {
                calloutBalloonButtonType = CalloutBalloonButtonType.MainButton;
            }
            final CalloutBalloonButtonType calloutBalloonButtonTypeFinal = calloutBalloonButtonType;
            final MapView mapView = this;
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    if (MapView.this.poiItemEventListener != null) {
                        POIItemEventListener listener = (POIItemEventListener) MapView.this.poiItemEventListener.get();
                        if (listener != null) {
                            for (MapPOIItem poiItem : MapView.this.poiItems) {
                                if (poiItem.getId() == poiMarkerId) {
                                    if (poiItem.getLeftSideButtonResourceIdOnCalloutBalloon() == 0 && poiItem.getRightSideButtonResourceIdOnCalloutBalloon() == 0) {
                                        listener.onCalloutBalloonOfPOIItemTouched(mapView, poiItem);
                                    }
                                    listener.onCalloutBalloonOfPOIItemTouched(mapView, poiItem, calloutBalloonButtonTypeFinal);
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    void onDraggablePOIItemMoved(int poiMarkerId, double newMapPointX, double newMapPointY) {
        Activity activity = (Activity) this.dmapActivity.get();
        if (activity != null && this.poiItemEventListener != null && this.poiItemEventListener.get() != null) {
            final MapView mapView = this;
            final int i = poiMarkerId;
            final double d = newMapPointX;
            final double d2 = newMapPointY;
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    if (MapView.this.poiItemEventListener != null) {
                        POIItemEventListener listener = (POIItemEventListener) MapView.this.poiItemEventListener.get();
                        if (listener != null) {
                            for (MapPOIItem poiItem : MapView.this.poiItems) {
                                if (poiItem.getId() == i) {
                                    listener.onDraggablePOIItemMoved(mapView, poiItem, MapPoint.mapPointWithWCONGCoord(d, d2));
                                }
                            }
                        }
                    }
                }
            });
        }
    }
}
