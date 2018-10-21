package com.neighbor.ulsanbus.ui;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.neighbor.ulsanbus.C0258R;
import com.neighbor.ulsanbus.Position;
import com.neighbor.ulsanbus.data.DataConst;
import java.util.ArrayList;
import net.daum.android.map.location.MapViewLocationManager;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPOIItem.CalloutBalloonButtonType;
import net.daum.mf.map.api.MapPOIItem.MarkerType;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;
import net.daum.mf.map.api.MapView.CurrentLocationEventListener;
import net.daum.mf.map.api.MapView.MapType;
import net.daum.mf.map.api.MapView.MapViewEventListener;
import net.daum.mf.map.api.MapView.POIItemEventListener;

public class UiDirectRouteMap extends Activity implements MapViewEventListener, POIItemEventListener, CurrentLocationEventListener {
    private MapViewLocationManager LocationManager;
    private LayoutInflater inflater;
    private ViewGroup mMainInformation;
    private String mRouteBusType;
    private String mRouteID;
    private ArrayList<Position> mVertaxList;
    private MapView mapView;

    void dispDirectRoute() {
        int i;
        int vertaxCount = this.mVertaxList.size();
        int total = vertaxCount / 200;
        int remain = vertaxCount % 200;
        String strcolor = null;
        switch (Integer.parseInt(this.mRouteBusType)) {
            case 0:
                strcolor = "#E5A244";
                break;
            case 1:
                strcolor = "#376FCC";
                break;
            case 2:
                strcolor = "#A24951";
                break;
            case 3:
                strcolor = "#2B9793";
                break;
            case 4:
                strcolor = "#736CB1";
                break;
        }
        for (i = 0; i < total; i++) {
            MapPolyline polyline1 = new MapPolyline(200);
            for (int j = i * 200; j < (i + 1) * 200; j++) {
                String strPosX = ((Position) this.mVertaxList.get(j)).posX;
                String strPosY = ((Position) this.mVertaxList.get(j)).posY;
                polyline1.addPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(strPosY), Double.parseDouble(strPosX)));
            }
            polyline1.setLineColor(Color.parseColor(strcolor));
            this.mapView.addPolyline(polyline1);
        }
        if (remain > 0) {
            polyline1 = new MapPolyline(remain);
            for (i = total * 200; i < (total * 200) + remain; i++) {
                strPosX = ((Position) this.mVertaxList.get(i)).posX;
                strPosY = ((Position) this.mVertaxList.get(i)).posY;
                polyline1.addPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(strPosY), Double.parseDouble(strPosX)));
            }
            polyline1.setLineColor(Color.parseColor(strcolor));
            this.mapView.addPolyline(polyline1);
        }
    }

    void dispFlag() {
        String startPosX = ((Position) this.mVertaxList.get(0)).posX;
        String startPosY = ((Position) this.mVertaxList.get(0)).posY;
        MapPOIItem poiItem = new MapPOIItem();
        poiItem.setItemName("출발");
        poiItem.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(startPosY), Double.parseDouble(startPosX)));
        poiItem.setMarkerType(MarkerType.CustomImage);
        poiItem.setCustomImageResourceId(C0258R.drawable.icon_map_start);
        poiItem.setShowDisclosureButtonOnCalloutBalloon(false);
        poiItem.setShowCalloutBalloonOnTouch(false);
        this.mapView.addPOIItem(poiItem);
        int endIdx = this.mVertaxList.size() - 1;
        String endPosX = ((Position) this.mVertaxList.get(endIdx)).posX;
        String endPosY = ((Position) this.mVertaxList.get(endIdx)).posY;
        MapPOIItem poiItem1 = new MapPOIItem();
        poiItem1.setItemName("도착");
        poiItem1.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(endPosY), Double.parseDouble(endPosX)));
        poiItem.setMarkerType(MarkerType.CustomImage);
        poiItem.setCustomImageResourceId(C0258R.drawable.icon_map_arrival);
        poiItem1.setShowDisclosureButtonOnCalloutBalloon(false);
        poiItem1.setShowCalloutBalloonOnTouch(false);
        this.mapView.addPOIItem(poiItem1);
        this.mapView.setZoomLevel(3, false);
        this.mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(endPosY), Double.parseDouble(endPosX)), false);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mVertaxList = (ArrayList) getIntent().getExtras().getSerializable("Dirvertax");
        this.mRouteID = getIntent().getExtras().getString("DirRouteId");
        Cursor cursor = getContentResolver().query(DataConst.CONTENT_ROUTE_URI, new String[]{DataConst.KEY_NOTICE_ID, "route_bus_type"}, "route_id = " + this.mRouteID, null, null);
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            this.mRouteBusType = cursor.getString(cursor.getColumnIndex("route_bus_type"));
        }
    }

    protected void onResume() {
        super.onResume();
        try {
            this.inflater = (LayoutInflater) getSystemService("layout_inflater");
            this.mMainInformation = (ViewGroup) this.inflater.inflate(C0258R.layout.map_container, null);
            this.mMainInformation.addView(this.inflater.inflate(C0258R.layout.transfer_route_map, null));
            setContentView(this.mMainInformation);
            this.mapView = (MapView) this.mMainInformation.findViewById(C0258R.id.tranfer_route_map);
            this.mapView.setDaumMapApiKey("387ce52ded8b6df718c2bed32fe314d48f859370");
            this.mapView.setMapType(MapType.Standard);
            this.mapView.setMapViewEventListener(this);
            this.mapView.setPOIItemEventListener(this);
            this.mapView.setCurrentLocationEventListener(this);
            this.mapView.setShowCurrentLocationMarker(false);
            this.LocationManager = MapViewLocationManager.getInstance();
            this.LocationManager.startResolveCurrentLocation();
            this.LocationManager.stopTracking();
            dispDirectRoute();
            dispFlag();
        } catch (Exception e) {
            Log.d("UiDirectRouteMap", e.getMessage());
        }
    }

    protected void onPause() {
        super.onPause();
        this.mMainInformation.removeAllViews();
        if (this.LocationManager != null) {
            this.LocationManager.stopResolveCurrentLocation();
            this.LocationManager.stopTracking();
        }
    }

    public void onCurrentLocationDeviceHeadingUpdate(MapView arg0, float arg1) {
        this.LocationManager.stopTracking();
    }

    public void onCurrentLocationUpdate(MapView arg0, MapPoint arg1, float arg2) {
        this.LocationManager.stopTracking();
        this.mapView.setShowCurrentLocationMarker(true);
    }

    public void onCurrentLocationUpdateCancelled(MapView arg0) {
        this.LocationManager.stopTracking();
    }

    public void onCurrentLocationUpdateFailed(MapView arg0) {
        this.LocationManager.stopTracking();
    }

    public void onCalloutBalloonOfPOIItemTouched(MapView arg0, MapPOIItem arg1) {
    }

    public void onDraggablePOIItemMoved(MapView arg0, MapPOIItem arg1, MapPoint arg2) {
    }

    public void onPOIItemSelected(MapView arg0, MapPOIItem arg1) {
    }

    public void onMapViewCenterPointMoved(MapView arg0, MapPoint arg1) {
    }

    public void onMapViewDoubleTapped(MapView arg0, MapPoint arg1) {
    }

    public void onMapViewInitialized(MapView arg0) {
    }

    public void onMapViewLongPressed(MapView arg0, MapPoint arg1) {
    }

    public void onMapViewSingleTapped(MapView arg0, MapPoint arg1) {
    }

    public void onMapViewZoomLevelChanged(MapView arg0, int arg1) {
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
