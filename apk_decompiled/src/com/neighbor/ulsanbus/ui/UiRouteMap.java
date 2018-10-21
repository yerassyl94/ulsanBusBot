package com.neighbor.ulsanbus.ui;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import com.neighbor.ulsanbus.BusLocation;
import com.neighbor.ulsanbus.C0258R;
import com.neighbor.ulsanbus.Stop;
import com.neighbor.ulsanbus.Vertax;
import com.neighbor.ulsanbus.data.DataConst;
import com.neighbor.ulsanbus.data.DataSAXParser;
import com.neighbor.ulsanbus.util.Util;
import com.neighbor.ulsanbus.util.UtilNetworkConnection;
import java.util.ArrayList;
import java.util.Iterator;
import net.daum.android.map.location.MapViewLocationManager;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPOIItem.CalloutBalloonButtonType;
import net.daum.mf.map.api.MapPOIItem.ImageOffset;
import net.daum.mf.map.api.MapPOIItem.MarkerType;
import net.daum.mf.map.api.MapPOIItem.ShowAnimationType;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;
import net.daum.mf.map.api.MapView.CurrentLocationEventListener;
import net.daum.mf.map.api.MapView.MapType;
import net.daum.mf.map.api.MapView.MapViewEventListener;
import net.daum.mf.map.api.MapView.POIItemEventListener;

public class UiRouteMap extends Activity implements MapViewEventListener, POIItemEventListener, CurrentLocationEventListener {
    private static final int BUS_TYPE_JISUN = 4;
    private static final int BUS_TYPE_LIMOUSINE = 2;
    private static final int BUS_TYPE_NORMAL = 0;
    private static final int BUS_TYPE_SPECIAL = 1;
    private static final int BUS_TYPE_TOWN = 3;
    private static final int RESULT_DATA_EMPTY = 2;
    private static final int RESULT_DATA_OK = 0;
    private static final int RESULT_SERVER_ERROR = 1;
    private MapViewLocationManager LocationManager;
    private float density;
    private Handler handler = new C03001();
    private LayoutInflater inflater;
    private ArrayList<BusLocation> mBusLocationList;
    private BusPositonRequest mBusPositionRequest;
    private ArrayList<Vertax> mBusRoute;
    private ViewGroup mMainInformation;
    private Button mMyPosBtn;
    private ProgressBar mPg;
    private Button mRefreshBtn;
    private DataSAXParser mSaxHelper;
    private ArrayList<Stop> mStopThrouthRoute;
    private Button mSwitchBtn;
    private Context m_context;
    private MapView mapView;
    private SharedPreferences prefs;

    /* renamed from: com.neighbor.ulsanbus.ui.UiRouteMap$1 */
    class C03001 extends Handler {
        C03001() {
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                switch (msg.arg1) {
                    case 0:
                        UiRouteMap.this.mapView.removeAllPOIItems();
                        UiRouteMap.this.mapView.removeAllPolylines();
                        UiRouteMap.this.drawFlag();
                        Vertax endvertax = (Vertax) UiRouteMap.this.mBusRoute.get(UiRouteMap.this.mBusRoute.size() - 1);
                        UiRouteMap.this.mapView.setZoomLevel(2, false);
                        UiRouteMap.this.mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(endvertax.getVertaxY()), Double.parseDouble(endvertax.getVertaxX())), false);
                        UiRouteMap.this.drawRoute();
                        UiRouteMap.this.dispBusMarker();
                        UiRouteMap.this.mBusPositionRequest = new BusPositonRequest(UiRouteMap.this, RouteInfo.mRouteID);
                        UiRouteMap.this.mBusPositionRequest.execute(new Void[0]);
                        return;
                    case 1:
                        UiRouteMap.this.mPg.setVisibility(8);
                        UiRouteMap.this.dispBusLoc();
                        return;
                    default:
                        return;
                }
            } else if (msg.what == 1) {
                UiRouteMap.this.mPg.setVisibility(8);
                UiRouteMap.this.dispError();
            } else if (msg.what == 2) {
                UiRouteMap.this.mPg.setVisibility(8);
                switch (msg.arg1) {
                    case 0:
                        UiRouteMap.this.dispError();
                        return;
                    default:
                        return;
                }
            }
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiRouteMap$2 */
    class C03012 implements OnClickListener {
        C03012() {
        }

        public void onClick(DialogInterface dialog, int which) {
            UiRouteMap.this.finish();
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiRouteMap$3 */
    class C03023 implements View.OnClickListener {
        C03023() {
        }

        public void onClick(View v) {
            if (UiRouteMap.this.mStopThrouthRoute == null) {
                UiRouteMap.this.mPg.setVisibility(8);
                UiRouteMap.this.dispError();
            } else if (UiRouteMap.this.mBusPositionRequest.getStatus() != Status.FINISHED && UiRouteMap.this.mStopThrouthRoute.size() > 0) {
                UiRouteMap.this.mBusPositionRequest = new BusPositonRequest(UiRouteMap.this.m_context, RouteInfo.mRouteID);
                UiRouteMap.this.mBusPositionRequest.execute(new Void[0]);
                UiRouteMap.this.mPg.setVisibility(0);
            }
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiRouteMap$4 */
    class C03034 implements View.OnClickListener {
        C03034() {
        }

        public void onClick(View v) {
            UiRouteMap.this.getReverseDirectionRoute();
            Editor editor = UiRouteMap.this.prefs.edit();
            if (Util.getCurrentTabNum() == 1) {
                editor.putString(DataConst.BUS_ID_TAB1, RouteInfo.mRouteID);
            } else if (Util.getCurrentTabNum() == 3) {
                editor.putString(DataConst.BUS_ID_TAB3, RouteInfo.mRouteID);
            }
            editor.commit();
            if (new UtilNetworkConnection().IsConnected(UiRouteMap.this.m_context)) {
                new Request().start();
                UiRouteMap.this.mPg.setVisibility(0);
                return;
            }
            UiRouteMap.this.mPg.setVisibility(8);
            UiRouteMap.this.dispError();
        }
    }

    /* renamed from: com.neighbor.ulsanbus.ui.UiRouteMap$5 */
    class C03045 implements View.OnClickListener {
        C03045() {
        }

        public void onClick(View v) {
            UiRouteMap.this.LocationManager.startTracking();
        }
    }

    private class BusPositonRequest extends AsyncTask<Void, Integer, Integer> {
        private ArrayList<BusLocation> mBusList;
        private Context mContext;
        private String mRouteId;
        private int responseCode = 0;

        public BusPositonRequest(Context context, String routeid) {
            this.mRouteId = routeid;
            this.mContext = context;
            this.mBusList = new ArrayList();
        }

        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        protected void onCancelled() {
            super.onCancelled();
            UiRouteMap.this.mBusPositionRequest.cancel(true);
        }

        protected Integer doInBackground(Void... params) {
            try {
                UiRouteMap.this.mSaxHelper.getName(DataConst.API_BUSLOCATIONINFO + this.mRouteId, DataConst.FLAG_BUSLOCATIONINFO);
            } catch (Exception e) {
                this.responseCode = 1;
            }
            return Integer.valueOf(this.responseCode);
        }

        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result.intValue() != 0) {
                Message msg = UiRouteMap.this.handler.obtainMessage();
                msg.what = result.intValue();
                msg.arg1 = 1;
                UiRouteMap.this.handler.sendMessage(msg);
                return;
            }
            this.mBusList.clear();
            this.mBusList = UiRouteMap.this.mSaxHelper.getBusLocationList();
            if (this.mBusList.size() > 0) {
                if (UiRouteMap.this.mBusLocationList.size() > 0) {
                    UiRouteMap.this.mBusLocationList.clear();
                }
                UiRouteMap.this.mBusLocationList = this.mBusList;
                msg = UiRouteMap.this.handler.obtainMessage();
                msg.what = 0;
                msg.arg1 = 1;
                UiRouteMap.this.handler.sendMessage(msg);
                return;
            }
            msg = UiRouteMap.this.handler.obtainMessage();
            msg.what = 2;
            msg.arg1 = 1;
            UiRouteMap.this.handler.sendMessage(msg);
        }
    }

    private class Request extends Thread {
        private Request() {
        }

        public void run() {
            try {
                UiRouteMap.this.mSaxHelper.getName("http://apis.its.ulsan.kr:8088/Service4.svc/RouteDetailInfo.xo?routeid=" + RouteInfo.mRouteID, DataConst.FLAG_ROUTESTOPS);
                UiRouteMap.this.mStopThrouthRoute.clear();
                UiRouteMap.this.mBusRoute.clear();
                UiRouteMap.this.mStopThrouthRoute = UiRouteMap.this.mSaxHelper.getRouteStopsList();
                UiRouteMap.this.mBusRoute = UiRouteMap.this.mSaxHelper.getVertaxlist();
                Message msg = UiRouteMap.this.handler.obtainMessage();
                msg.what = 0;
                msg.arg1 = 0;
                UiRouteMap.this.handler.sendMessage(msg);
            } catch (Exception e) {
            }
        }
    }

    private static class RouteInfo {
        static String mRouteBusType = "";
        static String mRouteDir = "";
        static String mRouteID = "";
        static String mRouteNumber = "";
        static String mRouteRemark = "";

        private RouteInfo() {
        }
    }

    private int getAzimuth(String busType, String low, String azimuth) {
        int quotient = (Integer.parseInt(azimuth) + 45) / 90;
        switch (Integer.parseInt(busType)) {
            case 0:
                if (low.equals("1")) {
                    switch (quotient) {
                        case 0:
                            if (((double) this.density) <= 1.0d) {
                                return 0;
                            }
                            if (this.density > 2.0f) {
                                return C0258R.drawable.icon_bus_map_low_north_xhdpi;
                            }
                            return C0258R.drawable.icon_bus_map_low_north;
                        case 1:
                            if (((double) this.density) <= 1.0d) {
                                return 0;
                            }
                            if (this.density > 2.0f) {
                                return C0258R.drawable.icon_bus_map_low_east_xhdpi;
                            }
                            return C0258R.drawable.icon_bus_map_low_east;
                        case 2:
                            if (((double) this.density) <= 1.0d) {
                                return 0;
                            }
                            if (this.density > 2.0f) {
                                return C0258R.drawable.icon_bus_map_low_south_xhdpi;
                            }
                            return C0258R.drawable.icon_bus_map_low_south;
                        case 3:
                            if (((double) this.density) <= 1.0d) {
                                return 0;
                            }
                            if (this.density > 2.0f) {
                                return C0258R.drawable.icon_bus_map_low_west_xhdpi;
                            }
                            return C0258R.drawable.icon_bus_map_low_west;
                        default:
                            return 0;
                    }
                }
                switch (quotient) {
                    case 0:
                        if (((double) this.density) <= 1.0d) {
                            return 0;
                        }
                        if (this.density > 2.0f) {
                            return C0258R.drawable.icon_bus_map_normal_north_xhdpi;
                        }
                        return C0258R.drawable.icon_bus_map_normal_north;
                    case 1:
                        if (((double) this.density) <= 1.0d) {
                            return 0;
                        }
                        if (this.density > 2.0f) {
                            return C0258R.drawable.icon_bus_map_normal_east_xhdpi;
                        }
                        return C0258R.drawable.icon_bus_map_normal_east;
                    case 2:
                        if (((double) this.density) <= 1.0d) {
                            return 0;
                        }
                        if (this.density > 2.0f) {
                            return C0258R.drawable.icon_bus_map_normal_south_xhdpi;
                        }
                        return C0258R.drawable.icon_bus_map_normal_south;
                    case 3:
                        if (((double) this.density) <= 1.0d) {
                            return 0;
                        }
                        if (this.density > 2.0f) {
                            return C0258R.drawable.icon_bus_map_normal_west_xhdpi;
                        }
                        return C0258R.drawable.icon_bus_map_normal_west;
                    default:
                        return 0;
                }
            case 1:
                switch (quotient) {
                    case 0:
                        if (((double) this.density) <= 1.0d) {
                            return 0;
                        }
                        if (this.density > 2.0f) {
                            return C0258R.drawable.icon_bus_map_special_north_xhdpi;
                        }
                        return C0258R.drawable.icon_bus_map_special_north;
                    case 1:
                        if (((double) this.density) <= 1.0d) {
                            return 0;
                        }
                        if (this.density > 2.0f) {
                            return C0258R.drawable.icon_bus_map_special_east_xhdpi;
                        }
                        return C0258R.drawable.icon_bus_map_special_east;
                    case 2:
                        if (((double) this.density) <= 1.0d) {
                            return 0;
                        }
                        if (this.density > 2.0f) {
                            return C0258R.drawable.icon_bus_map_special_south_xhdpi;
                        }
                        return C0258R.drawable.icon_bus_map_special_south;
                    case 3:
                        if (((double) this.density) <= 1.0d) {
                            return 0;
                        }
                        if (this.density > 2.0f) {
                            return C0258R.drawable.icon_bus_map_special_west_xhdpi;
                        }
                        return C0258R.drawable.icon_bus_map_special_west;
                    default:
                        return 0;
                }
            case 2:
                switch (quotient) {
                    case 0:
                        if (((double) this.density) <= 1.0d) {
                            return 0;
                        }
                        if (this.density > 2.0f) {
                            return C0258R.drawable.icon_bus_map_limousine_north_xhdpi;
                        }
                        return C0258R.drawable.icon_bus_map_limousine_north;
                    case 1:
                        if (((double) this.density) <= 1.0d) {
                            return 0;
                        }
                        if (this.density > 2.0f) {
                            return C0258R.drawable.icon_bus_map_limousine_east_xhdpi;
                        }
                        return C0258R.drawable.icon_bus_map_limousine_east;
                    case 2:
                        if (((double) this.density) <= 1.0d) {
                            return 0;
                        }
                        if (this.density > 2.0f) {
                            return C0258R.drawable.icon_bus_map_limousine_south_xhdpi;
                        }
                        return C0258R.drawable.icon_bus_map_limousine_south;
                    case 3:
                        if (((double) this.density) <= 1.0d) {
                            return 0;
                        }
                        if (this.density > 2.0f) {
                            return C0258R.drawable.icon_bus_map_limousine_west_xhdpi;
                        }
                        return C0258R.drawable.icon_bus_map_limousine_west;
                    default:
                        return 0;
                }
            case 3:
                switch (quotient) {
                    case 0:
                        if (((double) this.density) <= 1.0d) {
                            return 0;
                        }
                        if (this.density > 2.0f) {
                            return C0258R.drawable.icon_bus_map_town_north_xhdpi;
                        }
                        return C0258R.drawable.icon_bus_map_town_north;
                    case 1:
                        if (((double) this.density) <= 1.0d) {
                            return 0;
                        }
                        if (this.density > 2.0f) {
                            return C0258R.drawable.icon_bus_map_town_east_xhdpi;
                        }
                        return C0258R.drawable.icon_bus_map_town_east;
                    case 2:
                        if (((double) this.density) <= 1.0d) {
                            return 0;
                        }
                        if (this.density > 2.0f) {
                            return C0258R.drawable.icon_bus_map_town_south_xhdpi;
                        }
                        return C0258R.drawable.icon_bus_map_town_south;
                    case 3:
                        if (((double) this.density) <= 1.0d) {
                            return 0;
                        }
                        if (this.density > 2.0f) {
                            return C0258R.drawable.icon_bus_map_town_west_xhdpi;
                        }
                        return C0258R.drawable.icon_bus_map_town_west;
                    default:
                        return 0;
                }
            case 4:
                switch (quotient) {
                    case 0:
                        if (((double) this.density) <= 1.0d) {
                            return 0;
                        }
                        if (this.density > 2.0f) {
                            return C0258R.drawable.icon_bus_map_jisun_north_xhdpi;
                        }
                        return C0258R.drawable.icon_bus_map_jisun_north;
                    case 1:
                        if (((double) this.density) <= 1.0d) {
                            return 0;
                        }
                        if (this.density > 2.0f) {
                            return C0258R.drawable.icon_bus_map_jisun_east_xhdpi;
                        }
                        return C0258R.drawable.icon_bus_map_jisun_east;
                    case 2:
                        if (((double) this.density) <= 1.0d) {
                            return 0;
                        }
                        if (this.density > 2.0f) {
                            return C0258R.drawable.icon_bus_map_jisun_south_xhdpi;
                        }
                        return C0258R.drawable.icon_bus_map_jisun_south;
                    case 3:
                        if (((double) this.density) <= 1.0d) {
                            return 0;
                        }
                        if (this.density > 2.0f) {
                            return C0258R.drawable.icon_bus_map_jisun_west_xhdpi;
                        }
                        return C0258R.drawable.icon_bus_map_jisun_west;
                    default:
                        return 0;
                }
            default:
                return 0;
        }
    }

    private void dispError() {
        new Builder(getParent().getParent()).setTitle(" Warnning !").setMessage(getResources().getString(C0258R.string.request_dialog_fail)).setPositiveButton(getString(C0258R.string.ok), new C03012()).show();
    }

    private void dispBusLoc() {
        Iterator it = this.mBusLocationList.iterator();
        while (it.hasNext()) {
            BusLocation location = (BusLocation) it.next();
            Cursor cursor = getApplicationContext().getContentResolver().query(DataConst.CONTENT_STOPS_URI, new String[]{DataConst.KEY_NOTICE_ID, "stop_id", DataConst.KEY_STOP_LIMOUSINE, DataConst.KEY_STOP_X, DataConst.KEY_STOP_Y}, "stop_id=" + location.getStopID(), null, null);
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                String stopId = cursor.getString(cursor.getColumnIndex("stop_id"));
                double stopX = cursor.getDouble(cursor.getColumnIndex(DataConst.KEY_STOP_X));
                double stopY = cursor.getDouble(cursor.getColumnIndex(DataConst.KEY_STOP_Y));
                MapPOIItem poiItem = new MapPOIItem();
                poiItem.setItemName("");
                poiItem.setMarkerType(MarkerType.CustomImage);
                poiItem.setShowAnimationType(ShowAnimationType.NoAnimation);
                poiItem.setMapPoint(MapPoint.mapPointWithGeoCoord(stopY, stopX));
                int resId = 0;
                if (!(location.getBusType() == null || location.getLowType() == null || location.getBusAngle() == null)) {
                    resId = getAzimuth(location.getBusType(), location.getLowType(), location.getBusAngle());
                }
                poiItem.setCustomImageResourceId(resId);
                poiItem.setCustomImageAnchorPointOffset(new ImageOffset(22, 0));
                poiItem.setShowDisclosureButtonOnCalloutBalloon(false);
                poiItem.setShowCalloutBalloonOnTouch(false);
                this.mapView.addPOIItem(poiItem);
            }
            cursor.close();
        }
    }

    private void dispBusMarker() {
        Iterator it = this.mStopThrouthRoute.iterator();
        while (it.hasNext()) {
            Stop stop = (Stop) it.next();
            Cursor cursor = getApplicationContext().getContentResolver().query(DataConst.CONTENT_STOPS_URI, new String[]{DataConst.KEY_NOTICE_ID, DataConst.KEY_STOP_LIMOUSINE}, "stop_id = " + stop.getStopID(), null, null);
            if (((cursor.getCount() > 0 ? 1 : 0) & cursor.moveToFirst()) != 0) {
                String bLimusine = cursor.getString(cursor.getColumnIndex(DataConst.KEY_STOP_LIMOUSINE));
                MapPOIItem poiItem = new MapPOIItem();
                poiItem.setItemName(stop.getStopName());
                poiItem.setMarkerType(MarkerType.CustomImage);
                poiItem.setShowAnimationType(ShowAnimationType.SpringFromGround);
                poiItem.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(stop.getStopY()), Double.parseDouble(stop.getStopX())));
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
                poiItem.setTag(Integer.parseInt(stop.getStopID()));
                this.mapView.addPOIItem(poiItem);
            }
            cursor.close();
        }
    }

    private void drawRoute() {
        int i;
        int vertaxCount = this.mBusRoute.size();
        int total = vertaxCount / 200;
        int remain = vertaxCount % 200;
        String strcolor = null;
        switch (Integer.parseInt(RouteInfo.mRouteBusType)) {
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
                Vertax vertax = (Vertax) this.mBusRoute.get(j);
                polyline1.addPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(vertax.getVertaxY()), Double.parseDouble(vertax.getVertaxX())));
            }
            polyline1.setLineColor(Color.parseColor(strcolor));
            this.mapView.addPolyline(polyline1);
        }
        if (remain > 0) {
            polyline1 = new MapPolyline(remain);
            for (i = total * 200; i < (total * 200) + remain; i++) {
                vertax = (Vertax) this.mBusRoute.get(i);
                polyline1.addPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(vertax.getVertaxY()), Double.parseDouble(vertax.getVertaxX())));
            }
            polyline1.setLineColor(Color.parseColor(strcolor));
            this.mapView.addPolyline(polyline1);
        }
    }

    private void drawFlag() {
        int vertaxCount = this.mBusRoute.size();
        Vertax startvertax = (Vertax) this.mBusRoute.get(0);
        MapPOIItem poiItem = new MapPOIItem();
        poiItem.setItemName("기점");
        poiItem.setMarkerType(MarkerType.CustomImage);
        poiItem.setShowAnimationType(ShowAnimationType.NoAnimation);
        poiItem.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(startvertax.getVertaxY()), Double.parseDouble(startvertax.getVertaxX())));
        if (!RouteInfo.mRouteDir.equalsIgnoreCase("3")) {
            poiItem.setCustomImageResourceId(C0258R.drawable.icon_map_flag_start);
        }
        poiItem.setShowDisclosureButtonOnCalloutBalloon(false);
        poiItem.setShowCalloutBalloonOnTouch(false);
        this.mapView.addPOIItem(poiItem);
        Vertax endvertax = (Vertax) this.mBusRoute.get(vertaxCount - 1);
        MapPOIItem poiItem1 = new MapPOIItem();
        poiItem1.setItemName("종점");
        poiItem1.setMarkerType(MarkerType.CustomImage);
        poiItem1.setShowAnimationType(ShowAnimationType.NoAnimation);
        poiItem1.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(endvertax.getVertaxY()), Double.parseDouble(endvertax.getVertaxX())));
        if (RouteInfo.mRouteDir.equalsIgnoreCase("3")) {
            poiItem1.setCustomImageResourceId(C0258R.drawable.icon_map_flag_lotation);
        } else {
            poiItem1.setCustomImageResourceId(C0258R.drawable.icon_map_flag_end);
        }
        poiItem1.setShowDisclosureButtonOnCalloutBalloon(false);
        poiItem1.setShowCalloutBalloonOnTouch(false);
        this.mapView.addPOIItem(poiItem1);
    }

    void getReverseDirectionRoute() {
        Cursor cursor = getApplicationContext().getContentResolver().query(DataConst.CONTENT_ROUTE_URI, null, "route_no=" + RouteInfo.mRouteNumber + " AND " + DataConst.KEY_ROUTE_DIR + "= " + (RouteInfo.mRouteDir.equals("1") ? "2" : "1") + " AND " + "route_bus_type" + " = " + RouteInfo.mRouteBusType, null, null);
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            RouteInfo.mRouteID = cursor.getString(cursor.getColumnIndex("route_id"));
            RouteInfo.mRouteNumber = cursor.getString(cursor.getColumnIndex("route_no"));
            RouteInfo.mRouteDir = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_DIR));
            RouteInfo.mRouteBusType = cursor.getString(cursor.getColumnIndex("route_bus_type"));
            RouteInfo.mRouteRemark = cursor.getString(cursor.getColumnIndex(DataConst.KEY_ROUTE_REMARK));
        }
        cursor.close();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.m_context = this;
        this.mSaxHelper = new DataSAXParser();
        this.mStopThrouthRoute = new ArrayList();
        this.mBusRoute = new ArrayList();
        this.mBusLocationList = new ArrayList();
        this.prefs = getSharedPreferences("PreStatus", 0);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        this.density = dm.density;
        initialize();
    }

    void initialize() {
        if (Util.getCurrentTabNum() == 1) {
            RouteInfo.mRouteID = this.prefs.getString(DataConst.BUS_ID_TAB1, "");
        } else if (Util.getCurrentTabNum() == 3) {
            RouteInfo.mRouteID = this.prefs.getString(DataConst.BUS_ID_TAB3, "");
        }
        Cursor cs = getContentResolver().query(DataConst.CONTENT_ROUTE_URI, new String[]{"route_no", DataConst.KEY_ROUTE_DIR, "route_bus_type", DataConst.KEY_ROUTE_REMARK}, "route_id=" + RouteInfo.mRouteID, null, null);
        if (cs.moveToFirst()) {
            do {
                RouteInfo.mRouteNumber = cs.getString(cs.getColumnIndex(DataConst.KEY_ROUTE_DIR));
                RouteInfo.mRouteDir = cs.getString(cs.getColumnIndex(DataConst.KEY_ROUTE_DIR));
                RouteInfo.mRouteBusType = cs.getString(cs.getColumnIndex("route_bus_type"));
                RouteInfo.mRouteRemark = cs.getString(cs.getColumnIndex(DataConst.KEY_ROUTE_REMARK));
            } while (cs.moveToNext());
            cs.close();
        }
    }

    protected void onResume() {
        super.onResume();
        this.inflater = (LayoutInflater) getSystemService("layout_inflater");
        this.mMainInformation = (ViewGroup) this.inflater.inflate(C0258R.layout.map_container, null);
        setContentView(this.mMainInformation);
        try {
            this.mMainInformation.addView(this.inflater.inflate(C0258R.layout.route_map, null));
            this.mPg = (ProgressBar) this.mMainInformation.findViewById(C0258R.id.route_progressBar);
            this.mPg.setVisibility(8);
            this.mapView = (MapView) this.mMainInformation.findViewById(C0258R.id.route_map);
            this.mapView.setDaumMapApiKey("387ce52ded8b6df718c2bed32fe314d48f859370");
            this.mapView.setMapType(MapType.Standard);
            this.mapView.setMapViewEventListener(this);
            this.mapView.setPOIItemEventListener(this);
            this.mapView.setCurrentLocationEventListener(this);
            this.mapView.setShowCurrentLocationMarker(false);
            this.LocationManager = MapViewLocationManager.getInstance();
            this.LocationManager.startResolveCurrentLocation();
            this.LocationManager.stopTracking();
            this.mRefreshBtn = (Button) this.mMainInformation.findViewById(C0258R.id.btn_route_refresh);
            this.mRefreshBtn.setOnClickListener(new C03023());
            this.mSwitchBtn = (Button) this.mMainInformation.findViewById(C0258R.id.btn_switch_route);
            this.mSwitchBtn.setOnClickListener(new C03034());
            if (RouteInfo.mRouteDir.equalsIgnoreCase("3")) {
                this.mSwitchBtn.setVisibility(8);
            } else {
                this.mSwitchBtn.setVisibility(0);
            }
            this.mMyPosBtn = (Button) this.mMainInformation.findViewById(C0258R.id.btn_route_mypos);
            this.mMyPosBtn.setOnClickListener(new C03045());
            if (new UtilNetworkConnection().IsConnected(this.m_context)) {
                new Request().start();
            } else {
                dispError();
            }
        } catch (Exception e) {
            dispError();
        }
    }

    protected void onPause() {
        super.onPause();
        this.mMainInformation.removeAllViews();
        try {
            if (this.mBusPositionRequest.getStatus() != Status.FINISHED) {
                this.mBusPositionRequest.cancel(true);
            }
            if (this.LocationManager != null) {
                this.LocationManager.stopResolveCurrentLocation();
                this.LocationManager.stopTracking();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onCalloutBalloonOfPOIItemTouched(MapView arg0, MapPOIItem arg1) {
        final String stopId = String.valueOf(arg1.getTag());
        try {
            if (!stopId.equals("0")) {
                Builder alertDialog = new Builder(getParent().getParent());
                StringBuffer strbf = new StringBuffer();
                strbf.append(arg1.getItemName());
                strbf.append(" ( " + stopId + " )");
                alertDialog.setTitle(strbf.toString());
                alertDialog.setMessage(getResources().getString(C0258R.string.go_to_stop_information));
                alertDialog.setPositiveButton(getResources().getString(C0258R.string.ok), new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Cursor cursor = UiRouteMap.this.getContentResolver().query(DataConst.CONTENT_STOPS_URI, new String[]{"stop_id", "stop_name", "stop_remark"}, "stop_id = " + stopId, null, null);
                        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                            Intent intent = new Intent(UiRouteMap.this, UiStopInformation.class);
                            intent.putExtra("stop_name", cursor.getString(cursor.getColumnIndex("stop_name")));
                            intent.putExtra("stop_id", cursor.getString(cursor.getColumnIndex("stop_id")));
                            intent.putExtra("stop_remark", cursor.getString(cursor.getColumnIndex("stop_remark")));
                            TabFirst.TAB1.replaceActivity("routoarrival", intent, UiRouteMap.this);
                        }
                        dialog.dismiss();
                    }
                });
                alertDialog.setNegativeButton(getResources().getString(C0258R.string.cancel), null);
                alertDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        Log.d("UiRouteMap", "onMapViewZoomLevelChanged : " + arg1);
        if (arg1 < 3) {
            try {
                if (this.mapView.getPOIItems().length < this.mBusRoute.size() && this.mBusRoute.size() > 0) {
                    drawFlag();
                    drawRoute();
                    dispBusMarker();
                    dispBusLoc();
                }
            } catch (Exception e) {
                Log.d("UiRouteMap", e.getMessage());
            }
        } else if (this.mapView.getPOIItems().length > 3) {
            this.mapView.removeAllPOIItems();
            if (this.mBusRoute.size() > 0) {
                drawFlag();
                drawRoute();
                dispBusLoc();
            }
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

    public void onCalloutBalloonOfPOIItemTouched(MapView arg0, MapPOIItem arg1, CalloutBalloonButtonType arg2) {
    }

    public void onMapViewDragEnded(MapView arg0, MapPoint arg1) {
    }

    public void onMapViewDragStarted(MapView arg0, MapPoint arg1) {
    }

    public void onMapViewMoveFinished(MapView arg0, MapPoint arg1) {
    }
}
