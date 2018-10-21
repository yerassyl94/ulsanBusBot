package com.neighbor.ulsanbus.ui;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.neighbor.ulsanbus.C0258R;
import com.neighbor.ulsanbus.Position;
import com.neighbor.ulsanbus.TransitionBus;
import com.neighbor.ulsanbus.data.DataConst;
import java.util.ArrayList;
import java.util.Iterator;
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

public class UiTransferRouteMap extends Activity implements MapViewEventListener, POIItemEventListener, CurrentLocationEventListener {
    private static final String TAG = "[UiTransferRouteMap]";
    private MapViewLocationManager LocationManager;
    private LayoutInflater inflater;
    private ViewGroup mMainInformation;
    private String mRouteBusType;
    private String mRouteID;
    private ArrayList<TransitionBus> mTranferInfo;
    private ArrayList<ChangeRoute> mTransferStops;
    private String mVertaxCnt;
    private Context m_context;
    private MapView mapView;

    class ChangeRoute {
        String mBusType;
        String mFirstStopId;
        String mLastStopId;
        String mRouteId;
        ArrayList<Position> mVertax;
        String mVertaxCount;

        ChangeRoute() {
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("test", "mapa");
        this.mTransferStops = new ArrayList();
        this.mTranferInfo = (ArrayList) getIntent().getExtras().getSerializable(DataConst.TABLE_ROUTE_LIST);
        Iterator it = this.mTranferInfo.iterator();
        while (it.hasNext()) {
            TransitionBus item = (TransitionBus) it.next();
            ChangeRoute RouteInfo = new ChangeRoute();
            RouteInfo.mRouteId = item.getRouteId();
            Cursor routecursor = getContentResolver().query(DataConst.CONTENT_ROUTE_URI, new String[]{DataConst.KEY_NOTICE_ID, "route_id", "route_bus_type"}, "route_id= " + RouteInfo.mRouteId, null, null);
            try {
                if (routecursor.moveToFirst()) {
                    RouteInfo.mBusType = routecursor.getString(routecursor.getColumnIndex("route_bus_type"));
                }
                routecursor.close();
                RouteInfo.mVertax = item.getPositonList();
                RouteInfo.mVertaxCount = item.getVertaxCount();
                RouteInfo.mFirstStopId = (String) item.getStoplist().get(0);
                this.mTransferStops.add(RouteInfo);
            } catch (Exception e) {
            }
        }
    }

    void drawRoute(String count, ArrayList<Position> pos, String BusType, int offset) {
        int i;
        int vertaxCount = (Integer.parseInt(count) - 1) - offset;
        int total = vertaxCount / 200;
        int remain = vertaxCount % 200;
        String strcolor = null;
        switch (Integer.parseInt(BusType)) {
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
            for (int j = (i * 200) + offset; j < ((i + 1) * 200) + offset; j++) {
                String strPosX = ((Position) pos.get(j)).posX;
                String strPosY = ((Position) pos.get(j)).posY;
                polyline1.addPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(strPosY), Double.parseDouble(strPosX)));
            }
            polyline1.setLineColor(Color.parseColor(strcolor));
            this.mapView.addPolyline(polyline1);
        }
        if (remain > 0) {
            polyline1 = new MapPolyline(remain);
            for (i = (total * 200) + offset; i < ((total * 200) + remain) + offset; i++) {
                strPosX = ((Position) pos.get(i)).posX;
                strPosY = ((Position) pos.get(i)).posY;
                polyline1.addPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(strPosY), Double.parseDouble(strPosX)));
            }
            polyline1.setLineColor(Color.parseColor(strcolor));
            this.mapView.addPolyline(polyline1);
        }
    }

    void dispTranferRoute() {
        int transferCnt = this.mTransferStops.size();
        Log.e("dispTranferRoute: ", "size: " + this.mTransferStops.size());
        Position mTransition = new Position();
        for (int i = 0; i < transferCnt; i++) {
            int searchPos = 0;
            if (i + 1 < transferCnt) {
                mTransition.posX = ((Position) ((ChangeRoute) this.mTransferStops.get(i + 1)).mVertax.get(1)).posX;
                mTransition.posY = ((Position) ((ChangeRoute) this.mTransferStops.get(i + 1)).mVertax.get(1)).posY;
                Iterator it = ((ChangeRoute) this.mTransferStops.get(i)).mVertax.iterator();
                while (it.hasNext()) {
                    Position pos = (Position) it.next();
                    if (mTransition.posX.equalsIgnoreCase(pos.posX) && mTransition.posY.equalsIgnoreCase(pos.posY)) {
                        break;
                    }
                    searchPos++;
                }
            }
            drawRoute(((ChangeRoute) this.mTransferStops.get(i)).mVertaxCount, ((ChangeRoute) this.mTransferStops.get(i)).mVertax, ((ChangeRoute) this.mTransferStops.get(i)).mBusType, 1);
        }
    }

    void dispFlag() {
        String startPosX = ((Position) ((ChangeRoute) this.mTransferStops.get(0)).mVertax.get(0)).posX;
        String startPosY = ((Position) ((ChangeRoute) this.mTransferStops.get(0)).mVertax.get(0)).posY;
        Log.e("startPosX: ", "startPosX: " + startPosX);
        Log.e("startPosY: ", "startPosY: " + startPosY);
        MapPOIItem poiItem = new MapPOIItem();
        poiItem.setItemName("출발");
        poiItem.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(startPosY), Double.parseDouble(startPosX)));
        poiItem.setMarkerType(MarkerType.CustomImage);
        poiItem.setCustomImageResourceId(C0258R.drawable.icon_map_start);
        poiItem.setShowDisclosureButtonOnCalloutBalloon(false);
        poiItem.setShowCalloutBalloonOnTouch(false);
        this.mapView.addPOIItem(poiItem);
        int endIdx = this.mTransferStops.size() - 1;
        int endVerIdx = Integer.parseInt(((ChangeRoute) this.mTransferStops.get(endIdx)).mVertaxCount) - 1;
        String endPosX = ((Position) ((ChangeRoute) this.mTransferStops.get(endIdx)).mVertax.get(endVerIdx)).posX;
        String endPosY = ((Position) ((ChangeRoute) this.mTransferStops.get(endIdx)).mVertax.get(endVerIdx)).posY;
        Log.e("endPosX: ", "endPosX: " + endPosX);
        Log.e("endPosY: ", "endPosY: " + endPosY);
        MapPOIItem poiItem1 = new MapPOIItem();
        poiItem1.setItemName("도착");
        poiItem1.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(endPosY), Double.parseDouble(endPosX)));
        poiItem1.setMarkerType(MarkerType.CustomImage);
        poiItem1.setCustomImageResourceId(C0258R.drawable.icon_map_arrival);
        poiItem1.setShowDisclosureButtonOnCalloutBalloon(false);
        poiItem1.setShowCalloutBalloonOnTouch(true);
        this.mapView.addPOIItem(poiItem1);
        this.mapView.setZoomLevel(3, false);
        this.mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(endPosY), Double.parseDouble(endPosX)), false);
        String transX = ((Position) ((ChangeRoute) this.mTransferStops.get(1)).mVertax.get(1)).posX;
        String transY = ((Position) ((ChangeRoute) this.mTransferStops.get(1)).mVertax.get(1)).posY;
        Log.e("transX: ", "transX: " + transX);
        Log.e("transY: ", "transY: " + transY);
        MapPOIItem transpoiItem = new MapPOIItem();
        transpoiItem.setItemName("환승");
        transpoiItem.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(transY), Double.parseDouble(transX)));
        transpoiItem.setMarkerType(MarkerType.CustomImage);
        transpoiItem.setCustomImageResourceId(C0258R.drawable.icon_map_transfer);
        transpoiItem.setShowDisclosureButtonOnCalloutBalloon(false);
        transpoiItem.setShowCalloutBalloonOnTouch(true);
        this.mapView.addPOIItem(transpoiItem);
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
        } catch (Exception e) {
            Log.d("UiTranferRouteMap", e.getMessage());
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

    protected void onDestroy() {
        super.onDestroy();
        this.mapView = null;
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
        Log.e("onMapViewInitialized: ", "jaewoo");
        dispTranferRoute();
        dispFlag();
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
