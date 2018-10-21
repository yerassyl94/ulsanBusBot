package com.neighbor.ulsanbus.ui;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import com.neighbor.ulsanbus.C0258R;
import com.neighbor.ulsanbus.data.DataConst;
import net.daum.android.map.location.MapViewLocationManager;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPOIItem.CalloutBalloonButtonType;
import net.daum.mf.map.api.MapPOIItem.ImageOffset;
import net.daum.mf.map.api.MapPOIItem.MarkerType;
import net.daum.mf.map.api.MapPOIItem.ShowAnimationType;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;
import net.daum.mf.map.api.MapView.CurrentLocationEventListener;
import net.daum.mf.map.api.MapView.MapType;
import net.daum.mf.map.api.MapView.MapViewEventListener;
import net.daum.mf.map.api.MapView.POIItemEventListener;

public class UiMyPosition extends Activity implements MapViewEventListener, POIItemEventListener, CurrentLocationEventListener {
    public static UiMyPosition ROOT_POSITION = null;
    private static final String TAG = "[UlsanBus]UiMyPosition";
    private static boolean bFindCurrent = false;
    private static boolean bShowTcard = false;
    private MapViewLocationManager LocationManager;
    private float density;
    private LayoutInflater inflater;
    LocationManager locationManager;
    private ViewGroup mMainInformation;
    private Button mMyposBtn;
    private MapView mapView;
    private MapPOIItem poiItem;

    /* renamed from: com.neighbor.ulsanbus.ui.UiMyPosition$1 */
    class C02831 implements OnClickListener {
        C02831() {
        }

        public void onClick(View v) {
            UiMyPosition.this.LocationManager.startTracking();
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiMyPosition$2 */
    class C02842 implements OnClickListener {
        C02842() {
        }

        public void onClick(View arg0) {
            if (UiMyPosition.bShowTcard) {
                UiMyPosition.bShowTcard = false;
            } else {
                UiMyPosition.bShowTcard = true;
            }
            if (UiMyPosition.this.mapView.getZoomLevel() >= 3) {
                return;
            }
            if (UiMyPosition.bShowTcard) {
                UiMyPosition.this.dispCardStore(UiMyPosition.this.mapView.getMapCenterPoint());
                return;
            }
            UiMyPosition.this.mapView.removeAllPOIItems();
            UiMyPosition.this.dispStop(UiMyPosition.this.mapView.getMapCenterPoint());
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiMyPosition$4 */
    class C02864 implements DialogInterface.OnClickListener {
        C02864() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        this.density = dm.density;
        ROOT_POSITION = this;
    }

    public String getSearchAroundStopList(String coord_x, String coord_y) {
        return "stop_x < " + coord_x + "+ 0.005  and " + DataConst.KEY_STOP_X + " > " + coord_x + "-0.005  and " + DataConst.KEY_STOP_Y + " < " + coord_y + "+ 0.005  and " + DataConst.KEY_STOP_Y + " > " + coord_y + "-0.005";
    }

    public String getSearchAroundTcardStoreList(String coord_x, String coord_y) {
        return "tcardstore_cardX < " + coord_x + "+ 0.005  and " + DataConst.KEY_TCARDSTORE_CARDX + " > " + coord_x + "-0.005 and " + DataConst.KEY_TCARDSTORE_CARDY + " < " + coord_y + "+ 0.005  and " + DataConst.KEY_TCARDSTORE_CARDY + " > " + coord_y + "- 0.005";
    }

    protected void onResume() {
        super.onResume();
        try {
            this.inflater = (LayoutInflater) getSystemService("layout_inflater");
            this.mMainInformation = (ViewGroup) this.inflater.inflate(C0258R.layout.map_container, null);
            this.mMainInformation.addView(this.inflater.inflate(C0258R.layout.myposition, null));
            setContentView(this.mMainInformation);
            this.mapView = (MapView) this.mMainInformation.findViewById(C0258R.id.map);
            this.mapView.setDaumMapApiKey("387ce52ded8b6df718c2bed32fe314d48f859370");
            this.mapView.setMapType(MapType.Standard);
            this.mapView.setMapViewEventListener(this);
            this.mapView.setPOIItemEventListener(this);
            this.mapView.setCurrentLocationEventListener(this);
            this.mapView.setShowCurrentLocationMarker(false);
            this.mMyposBtn = (Button) this.mMainInformation.findViewById(C0258R.id.btn_mypos);
            this.mMyposBtn.setOnClickListener(new C02831());
            this.LocationManager = MapViewLocationManager.getInstance();
            this.LocationManager.startResolveCurrentLocation();
            this.LocationManager.stopTracking();
            ((Button) findViewById(C0258R.id.btn_tcard_map)).setOnClickListener(new C02842());
        } catch (Exception e) {
            Log.d("UiMyPostion", e.getMessage());
        }
    }

    private void dispCardStore(MapPoint point) {
        double lon = point.getMapPointGeoCoord().longitude;
        double lat = point.getMapPointGeoCoord().latitude;
        String Select = getSearchAroundTcardStoreList(new Double(lon).toString(), new Double(lat).toString());
        Log.d(TAG, "Select" + Select);
        Cursor cursor = getContentResolver().query(DataConst.CONTENT_TSTORE_URI, null, Select, null, null);
        if (cursor.getCount() <= 0 || !cursor.moveToFirst()) {
            cursor.close();
        }
        Log.d(TAG, "cursor count " + cursor.getCount());
        do {
            MapPOIItem poiItem = new MapPOIItem();
            poiItem.setItemName(cursor.getString(cursor.getColumnIndex(DataConst.KEY_TCARDSTORE_NAME)));
            poiItem.setUserObject(cursor.getString(cursor.getColumnIndex(DataConst.KEY_TCARDSTORE_ADDRESS)));
            poiItem.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(cursor.getString(cursor.getColumnIndex(DataConst.KEY_TCARDSTORE_CARDY))), Double.parseDouble(cursor.getString(cursor.getColumnIndex(DataConst.KEY_TCARDSTORE_CARDX)))));
            poiItem.setMarkerType(MarkerType.CustomImage);
            poiItem.setShowAnimationType(ShowAnimationType.SpringFromGround);
            if (((double) this.density) > 1.0d) {
                if (this.density > 2.0f) {
                    poiItem.setCustomImageResourceId(C0258R.drawable.tcardstore_xhdpi);
                } else {
                    poiItem.setCustomImageResourceId(C0258R.drawable.tcardstore);
                }
            }
            poiItem.setCustomImageAnchorPointOffset(new ImageOffset(22, 0));
            poiItem.setShowDisclosureButtonOnCalloutBalloon(false);
            poiItem.setShowCalloutBalloonOnTouch(true);
            this.mapView.addPOIItem(poiItem);
        } while (cursor.moveToNext());
        cursor.close();
    }

    private void dispStop(MapPoint point) {
        double lon = point.getMapPointGeoCoord().longitude;
        double lat = point.getMapPointGeoCoord().latitude;
        Cursor stopCursor = getContentResolver().query(DataConst.CONTENT_STOPS_URI, null, getSearchAroundStopList(new Double(lon).toString(), new Double(lat).toString()), null, null);
        if (stopCursor.getCount() <= 0 || !stopCursor.moveToFirst()) {
            stopCursor.close();
        }
        do {
            MapPOIItem poiItem = new MapPOIItem();
            String BusposX = stopCursor.getString(stopCursor.getColumnIndex(DataConst.KEY_STOP_X));
            String BusposY = stopCursor.getString(stopCursor.getColumnIndex(DataConst.KEY_STOP_Y));
            poiItem.setItemName(stopCursor.getString(stopCursor.getColumnIndex("stop_name")));
            poiItem.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(BusposY), Double.parseDouble(BusposX)));
            poiItem.setMarkerType(MarkerType.CustomImage);
            poiItem.setShowAnimationType(ShowAnimationType.SpringFromGround);
            String bLimusine = stopCursor.getString(stopCursor.getColumnIndex(DataConst.KEY_STOP_LIMOUSINE));
            if (((double) this.density) > 1.0d) {
                if (this.density > 2.0f) {
                    if (bLimusine.equals("1")) {
                        poiItem.setCustomImageResourceId(C0258R.drawable.map_icon_limousine_nor_xhdpi);
                    } else {
                        poiItem.setCustomImageResourceId(C0258R.drawable.map_icon_stop_nor_xhdpi);
                    }
                } else if (bLimusine.equals("1")) {
                    poiItem.setCustomImageResourceId(C0258R.drawable.map_icon_limousine_nor);
                } else {
                    poiItem.setCustomImageResourceId(C0258R.drawable.map_icon_stop_nor);
                }
            }
            poiItem.setCustomImageAnchorPointOffset(new ImageOffset(22, 0));
            poiItem.setShowDisclosureButtonOnCalloutBalloon(true);
            poiItem.setShowCalloutBalloonOnTouch(true);
            poiItem.setTag(Integer.parseInt(stopCursor.getString(stopCursor.getColumnIndex("stop_id"))));
            this.mapView.addPOIItem(poiItem);
        } while (stopCursor.moveToNext());
        stopCursor.close();
    }

    protected void onPause() {
        super.onPause();
        this.mMainInformation.removeAllViews();
        if (this.LocationManager != null) {
            this.LocationManager.stopTracking();
            this.LocationManager.stopResolveCurrentLocation();
        }
    }

    public void onMapViewCenterPointMoved(MapView arg0, MapPoint arg1) {
        Log.d(TAG, "onMapViewCenterPointMoved");
        try {
            if (arg0.getZoomLevel() < 3) {
                this.mapView.removeAllPOIItems();
                if (bShowTcard) {
                    dispCardStore(arg0.getMapCenterPoint());
                }
                dispStop(arg0.getMapCenterPoint());
            } else if (this.mapView.getPOIItems().length > 0) {
                this.mapView.removeAllPOIItems();
            }
        } catch (Exception e) {
            Log.d("UiMyPositon", e.getMessage());
        }
    }

    public void onMapViewDoubleTapped(MapView arg0, MapPoint arg1) {
        Log.d(TAG, "onMapViewDoubleTapped");
    }

    public void onMapViewInitialized(MapView arg0) {
        Log.d(TAG, "onMapViewInitialized");
        this.mapView.setZoomLevel(2, false);
        this.mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(35.539123d, 129.311452d), false);
        this.LocationManager.startTracking();
        dispStop(MapPoint.mapPointWithGeoCoord(35.539123d, 129.311452d));
    }

    public void onMapViewLongPressed(MapView arg0, MapPoint arg1) {
        Log.d(TAG, "onMapViewLongPressed");
    }

    public void onMapViewSingleTapped(MapView arg0, MapPoint arg1) {
        Log.d(TAG, "onMapViewSingleTapped");
    }

    public void onCalloutBalloonOfPOIItemTouched(MapView arg0, MapPOIItem arg1) {
        Log.d(TAG, "onCalloutBalloonOfPOIItemTouched");
        MapView mapview = arg0;
        final String stopId = String.valueOf(arg1.getTag());
        Log.d(TAG, "stopId" + stopId);
        try {
            if (!stopId.equals("0")) {
                try {
                    Builder alertDialog = new Builder(getParent());
                    StringBuffer strbf = new StringBuffer();
                    strbf.append(arg1.getItemName());
                    strbf.append(" ( " + stopId + " )");
                    alertDialog.setTitle(strbf.toString());
                    alertDialog.setMessage(getResources().getString(C0258R.string.go_to_stop_information));
                    alertDialog.setPositiveButton(getResources().getString(C0258R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Cursor cursor = UiMyPosition.this.getContentResolver().query(DataConst.CONTENT_STOPS_URI, new String[]{"stop_id", "stop_name", "stop_remark"}, "stop_id = " + stopId, null, null);
                            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                                Intent intent = new Intent(UiMyPosition.this, UiStopInformation.class);
                                intent.putExtra("stop_name", cursor.getString(cursor.getColumnIndex("stop_name")));
                                intent.putExtra("stop_id", cursor.getString(cursor.getColumnIndex("stop_id")));
                                intent.putExtra("stop_remark", cursor.getString(cursor.getColumnIndex("stop_remark")));
                                TabFiveth.TAB5.replaceActivity("postoarrival", intent, UiMyPosition.this);
                            }
                            cursor.close();
                            dialog.dismiss();
                        }
                    });
                    alertDialog.setNegativeButton(getResources().getString(C0258R.string.cancel), new C02864());
                    alertDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e2) {
            Log.d("UiMyPosition", e2.getMessage());
        }
    }

    public void onDraggablePOIItemMoved(MapView arg0, MapPOIItem arg1, MapPoint arg2) {
    }

    public void onPOIItemSelected(MapView arg0, MapPOIItem arg1) {
        Log.d(TAG, "onPOIItemSelected");
    }

    public void onMapViewZoomLevelChanged(MapView arg0, int arg1) {
        Log.d(TAG, "zoom level :" + arg1);
        if (arg1 < 3) {
            try {
                if (this.mapView.getPOIItems().length == 0) {
                    this.mapView.removeAllPOIItems();
                    if (bShowTcard) {
                        dispCardStore(arg0.getMapCenterPoint());
                    }
                    dispStop(arg0.getMapCenterPoint());
                }
            } catch (Exception e) {
                Log.d("UiMyPositon", e.getMessage());
            }
        } else if (this.mapView.getPOIItems().length > 0) {
            this.mapView.removeAllPOIItems();
        }
    }

    public void onCurrentLocationDeviceHeadingUpdate(MapView arg0, float arg1) {
        Log.d(TAG, "onCurrentLocationDeviceHeadingUpdate");
        this.LocationManager.stopTracking();
    }

    public void onCurrentLocationUpdate(MapView arg0, MapPoint arg1, float arg2) {
        Log.d(TAG, "onCurrentLocationUpdate");
        try {
            this.LocationManager.stopTracking();
            if (arg0.getZoomLevel() < 3) {
                this.mapView.removeAllPOIItems();
                if (bShowTcard) {
                    dispCardStore(arg1);
                }
                dispStop(arg1);
            } else if (this.mapView.getPOIItems().length > 0) {
                this.mapView.removeAllPOIItems();
            }
        } catch (Exception e) {
            Log.d("UiMyPosition", e.getMessage());
        }
    }

    public void onCurrentLocationUpdateCancelled(MapView arg0) {
        Log.d(TAG, "onCurrentLocationUpdateCancelled");
        this.LocationManager.stopTracking();
    }

    public void onCurrentLocationUpdateFailed(MapView arg0) {
        Log.d(TAG, "onCurrentLocationUpdateFailed");
        this.LocationManager.stopTracking();
    }

    public void onCalloutBalloonOfPOIItemTouched(MapView arg0, MapPOIItem arg1, CalloutBalloonButtonType arg2) {
    }

    public void onMapViewDragEnded(MapView arg0, MapPoint arg1) {
    }

    public void onMapViewDragStarted(MapView arg0, MapPoint arg1) {
    }

    public void onMapViewMoveFinished(MapView arg0, MapPoint arg1) {
    }
}
