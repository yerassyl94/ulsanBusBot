package net.daum.mf.map.api;

import android.app.Activity;
import android.content.Context;
import java.lang.ref.WeakReference;
import net.daum.android.map.geocoding.ReverseGeoCodingWebService;
import net.daum.android.map.geocoding.ReverseGeoCodingWebService.ResultFormat;
import net.daum.mf.map.p000n.api.internal.NativeMapBuildSettings;

public class MapReverseGeoCoder implements net.daum.android.map.geocoding.ReverseGeoCodingWebService.ReverseGeoCodingResultListener {
    private Activity contextActivity;
    private MapPoint mapPoint;
    private String openAPIKey;
    private WeakReference<ReverseGeoCodingResultListener> resultListener;
    private ReverseGeoCodingWebService reverseGeoCodingWebService = null;

    /* renamed from: net.daum.mf.map.api.MapReverseGeoCoder$2 */
    class C03792 implements Runnable {
        C03792() {
        }

        public void run() {
            if (MapReverseGeoCoder.this.resultListener != null && MapReverseGeoCoder.this.resultListener.get() != null) {
                ((ReverseGeoCodingResultListener) MapReverseGeoCoder.this.resultListener.get()).onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder.this);
            }
        }
    }

    public enum AddressType {
        ShortAddress,
        FullAddress
    }

    public interface ReverseGeoCodingResultListener {
        void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder);

        void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String str);
    }

    public MapReverseGeoCoder(String openAPIKey, MapPoint mapPoint, ReverseGeoCodingResultListener resultListener, Activity contextActivity) {
        this.contextActivity = contextActivity;
        this.openAPIKey = openAPIKey;
        this.mapPoint = mapPoint;
        this.resultListener = new WeakReference(resultListener);
    }

    public void startFindingAddress() {
        this.reverseGeoCodingWebService = new ReverseGeoCodingWebService(this.openAPIKey, this.contextActivity.getPackageName(), this.mapPoint, this);
        this.reverseGeoCodingWebService.requestReverseGeoCodingService();
    }

    public void startFindingAddress(AddressType addressType) {
        ResultFormat resultFormat;
        if (addressType == AddressType.ShortAddress) {
            resultFormat = ResultFormat.Simple;
        } else if (NativeMapBuildSettings.isOpenAPIMapLibraryBuild()) {
            resultFormat = ResultFormat.Simple;
        } else {
            resultFormat = ResultFormat.Full;
        }
        this.reverseGeoCodingWebService = new ReverseGeoCodingWebService(this.openAPIKey, this.contextActivity.getPackageName(), this.mapPoint, resultFormat, this);
        this.reverseGeoCodingWebService.requestReverseGeoCodingService();
    }

    public void cancelFindingAddress() {
        if (this.reverseGeoCodingWebService != null) {
            this.reverseGeoCodingWebService.cancelRequest();
            this.reverseGeoCodingWebService = null;
        }
    }

    @Deprecated
    public static String findAddressForMapPoint(String openAPIKey, MapPoint mapPoint) {
        return new ReverseGeoCodingWebService(openAPIKey, null, mapPoint, null).requestReverseGeoCodingServiceSync();
    }

    @Deprecated
    public static String findAddressForMapPoint(String openAPIKey, MapPoint mapPoint, AddressType addressType) {
        ResultFormat resultFormat;
        if (addressType == AddressType.ShortAddress) {
            resultFormat = ResultFormat.Simple;
        } else {
            resultFormat = ResultFormat.Full;
        }
        return new ReverseGeoCodingWebService(openAPIKey, null, mapPoint, resultFormat, null).requestReverseGeoCodingServiceSync();
    }

    public String findAddressForMapPointSync(String openAPIKey, MapPoint mapPoint) {
        return new ReverseGeoCodingWebService(openAPIKey, this.contextActivity.getPackageName(), mapPoint, null).requestReverseGeoCodingServiceSync();
    }

    public String findAddressForMapPointSync(String openAPIKey, MapPoint mapPoint, AddressType addressType) {
        ResultFormat resultFormat;
        if (addressType == AddressType.ShortAddress) {
            resultFormat = ResultFormat.Simple;
        } else {
            resultFormat = ResultFormat.Full;
        }
        return new ReverseGeoCodingWebService(openAPIKey, this.contextActivity.getPackageName(), mapPoint, resultFormat, null).requestReverseGeoCodingServiceSync();
    }

    public void onAddressFound(final String address) {
        if (!(this.resultListener == null || this.resultListener.get() == null)) {
            if (this.contextActivity != null) {
                this.contextActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        if (MapReverseGeoCoder.this.resultListener != null && MapReverseGeoCoder.this.resultListener.get() != null) {
                            ((ReverseGeoCodingResultListener) MapReverseGeoCoder.this.resultListener.get()).onReverseGeoCoderFoundAddress(MapReverseGeoCoder.this, address);
                        }
                    }
                });
            } else {
                ((ReverseGeoCodingResultListener) this.resultListener.get()).onReverseGeoCoderFoundAddress(this, address);
            }
        }
        this.reverseGeoCodingWebService = null;
    }

    public void onFailedToFindAddress() {
        if (!(this.resultListener == null || this.resultListener.get() == null)) {
            if (this.contextActivity != null) {
                this.contextActivity.runOnUiThread(new C03792());
            } else {
                ((ReverseGeoCodingResultListener) this.resultListener.get()).onReverseGeoCoderFailedToFindAddress(this);
            }
        }
        this.reverseGeoCodingWebService = null;
    }

    public Context getContext() {
        return this.contextActivity;
    }
}
