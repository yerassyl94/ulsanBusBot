package net.daum.android.map.geocoding;

import android.content.Context;
import com.kakao.util.maps.helper.CommonProtocol;
import com.kakao.util.maps.helper.SystemInfo;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPoint.PlainCoordinate;
import net.daum.mf.map.common.net.WebClient;
import net.daum.mf.map.p000n.api.internal.NativeMapBuildSettings;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class ReverseGeoCodingWebService {
    private static final String HEADER_NAME_X_APPID = "x-appid";
    private static final String HEADER_NAME_X_PLATFORM = "x-platform";
    private static final String HEADER_VALUE_X_PLATFORM_ANDROID = "android";
    private static final String TAG = "ReverseGeoCodingWebService";
    private String apiKey;
    private boolean canceled = false;
    private WebClient httpClient;
    private MapPoint mapPoint;
    private String packageName;
    private ResultFormat resultFormat = ResultFormat.Simple;
    private ReverseGeoCodingResultListener resultListener;

    /* renamed from: net.daum.android.map.geocoding.ReverseGeoCodingWebService$1 */
    class C03651 extends Thread {
        C03651() {
        }

        public void run() {
            String resultString;
            if (ReverseGeoCodingWebService.this.resultFormat == ResultFormat.Simple) {
                boolean ok;
                ReverseGeoCodingWebService.this.httpClient = new WebClient();
                String requestUrl = ReverseGeoCodingWebService.this.getRequestUrl();
                if (NativeMapBuildSettings.isOpenAPIMapLibraryBuild()) {
                    Map<String, String> header = new HashMap();
                    SystemInfo.initialize(ReverseGeoCodingWebService.this.resultListener.getContext());
                    String KA = SystemInfo.getKAHeader();
                    header.put(CommonProtocol.KA_AUTH_HEADER_KEY, "KakaoAK " + ReverseGeoCodingWebService.this.apiKey);
                    header.put(CommonProtocol.KA_HEADER_KEY, KA);
                    ok = ReverseGeoCodingWebService.this.httpClient.requestGetWithHeader(requestUrl, header);
                } else {
                    ok = ReverseGeoCodingWebService.this.httpClient.requestGet(requestUrl);
                }
                if (!ok && !ReverseGeoCodingWebService.this.canceled && ReverseGeoCodingWebService.this.resultListener != null) {
                    ReverseGeoCodingWebService.this.resultListener.onFailedToFindAddress();
                    return;
                } else if (!ReverseGeoCodingWebService.this.canceled && ReverseGeoCodingWebService.this.resultListener != null) {
                    resultString = ReverseGeoCodingWebService.this.getResultString();
                    if (resultString == null) {
                        ReverseGeoCodingWebService.this.resultListener.onFailedToFindAddress();
                        return;
                    } else {
                        ReverseGeoCodingWebService.this.resultListener.onAddressFound(resultString);
                        return;
                    }
                } else if (!ReverseGeoCodingWebService.this.canceled && ReverseGeoCodingWebService.this.resultListener != null) {
                    ReverseGeoCodingWebService.this.resultListener.onFailedToFindAddress();
                    return;
                } else {
                    return;
                }
            }
            boolean error = false;
            ReverseGeoCodingWebService.this.httpClient = new WebClient();
            if (!ReverseGeoCodingWebService.this.httpClient.requestGet(ReverseGeoCodingWebService.this.getRequestUrl())) {
                error = true;
            }
            if (!(ReverseGeoCodingWebService.this.canceled || ReverseGeoCodingWebService.this.resultListener == null)) {
                resultString = ReverseGeoCodingWebService.this.getResultString();
                if (resultString == null || resultString.length() == 0) {
                    error = true;
                } else {
                    ReverseGeoCodingWebService.this.resultListener.onAddressFound(resultString);
                }
            }
            if (error) {
                ReverseGeoCodingWebService.this.resultFormat = ResultFormat.Simple;
                ReverseGeoCodingWebService.this.requestReverseGeoCodingService();
            }
        }
    }

    public enum ResultFormat {
        Simple,
        Full
    }

    public interface ReverseGeoCodingResultListener {
        Context getContext();

        void onAddressFound(String str);

        void onFailedToFindAddress();
    }

    public ReverseGeoCodingWebService(String apiKey, String packageName, MapPoint mapPoint, ReverseGeoCodingResultListener resultListener) {
        this.resultListener = resultListener;
        this.apiKey = apiKey;
        this.packageName = packageName;
        this.mapPoint = mapPoint;
    }

    public ReverseGeoCodingWebService(String apiKey, String packageName, MapPoint mapPoint, ResultFormat resultFormat, ReverseGeoCodingResultListener resultListener) {
        this.resultListener = resultListener;
        this.apiKey = apiKey;
        this.packageName = packageName;
        this.mapPoint = mapPoint;
        this.resultFormat = resultFormat;
    }

    private String _makeFullAddressRequestUrl() {
        PlainCoordinate coord = this.mapPoint.getMapPointWCONGCoord();
        return String.format(Locale.US, "http://pg.daum.net/congsoa/pointsearch/getPointAllAddress.service?x=%f&y=%f&inputCoordSystem=WCONGNAMUL&output=json&service=and_internal_open_api", new Object[]{Double.valueOf(coord.f13x), Double.valueOf(coord.f14y)});
    }

    private String _makeSimpleAddressRequestUrl() {
        PlainCoordinate coord = this.mapPoint.getMapPointWCONGCoord();
        return String.format(Locale.US, "https://dapi.kakao.com/v2/local/geo/coord2address.json?x=%f&y=%f&input_coord=WCONGNAMUL", new Object[]{Double.valueOf(coord.f13x), Double.valueOf(coord.f14y)});
    }

    private String _makeSimpleAddressRequestUrlInternal() {
        PlainCoordinate coord = this.mapPoint.getMapPointWCONGCoord();
        return String.format(Locale.US, "http://rapi.daum.net/regioncode/getHCode.json?x=%f&y=%f&format=normal&inputCoordSystem=WCONGNAMUL&outputCoordSystem=WCONGNAMUL&service=and_internal_open_api", new Object[]{Double.valueOf(coord.f13x), Double.valueOf(coord.f14y)});
    }

    private String convertInputStreamToString(InputStream inputStream) {
        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        while (true) {
            try {
                int n = inputStream.read(b);
                if (n == -1) {
                    break;
                }
                out.append(new String(b, 0, n));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return out.toString();
    }

    private String _parseFullAddressResultJson(InputStream contentStream) throws Exception {
        JSONObject old = new JSONObject(convertInputStreamToString(contentStream)).getJSONObject("old");
        if (old == null) {
            return null;
        }
        String name = old.getString("name");
        String ho = old.getString("ho");
        if (name == null) {
            return null;
        }
        if (ho != null && ho.equals("0") && name.endsWith("-0")) {
            return name.substring(0, name.length() - 2);
        }
        return name;
    }

    private String _parseSimpleAddressResultXML(InputStream contentStream) throws Exception {
        String resultString = null;
        XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
        parser.setInput(contentStream, "utf-8");
        for (int parserEvent = parser.getEventType(); parserEvent != 1; parserEvent = parser.next()) {
            switch (parserEvent) {
                case 2:
                    String tag = parser.getName();
                    if (tag.compareTo("rcode") != 0) {
                        if (tag.compareTo("apierror") != 0) {
                            break;
                        }
                        throw new Exception("apierror");
                    }
                    String name1 = parser.getAttributeValue(null, "name1");
                    String name2 = parser.getAttributeValue(null, "name2");
                    String name3 = parser.getAttributeValue(null, "name3");
                    StringBuilder strBuilder = new StringBuilder(30);
                    strBuilder.append(name1);
                    if (name2 != null) {
                        strBuilder.append(' ');
                        strBuilder.append(name2);
                    }
                    if (name3 != null) {
                        strBuilder.append(' ');
                        strBuilder.append(name3);
                    }
                    resultString = strBuilder.toString();
                    break;
                default:
                    break;
            }
        }
        return resultString;
    }

    private String _parseSimpleAddressResultJSON(InputStream contentStream) throws Exception {
        String resultString = null;
        try {
            JSONArray document = new JSONObject(convertInputStreamToString(contentStream)).getJSONArray("documents");
            if (document.length() > 0) {
                JSONObject addressDocument = (JSONObject) document.get(0);
                if (addressDocument != null) {
                    JSONObject addressObj = addressDocument.getJSONObject("address");
                    if (addressObj != null) {
                        resultString = addressObj.getString("address_name");
                    }
                }
            }
        } catch (Exception e) {
        }
        return resultString;
    }

    private String _parseSimpleAddressIternalResultJson(InputStream contentStream) throws Exception {
        return new JSONObject(convertInputStreamToString(contentStream)).getString("fullName");
    }

    private String getRequestUrl() {
        if (this.resultFormat != ResultFormat.Simple) {
            return _makeFullAddressRequestUrl();
        }
        if (NativeMapBuildSettings.isOpenAPIMapLibraryBuild()) {
            return _makeSimpleAddressRequestUrl();
        }
        return _makeSimpleAddressRequestUrlInternal();
    }

    private String getResultString() {
        try {
            String resultString;
            InputStream stream = this.httpClient.openContentStream();
            if (this.resultFormat != ResultFormat.Simple) {
                resultString = _parseFullAddressResultJson(stream);
            } else if (NativeMapBuildSettings.isOpenAPIMapLibraryBuild()) {
                resultString = _parseSimpleAddressResultJSON(stream);
            } else {
                resultString = _parseSimpleAddressIternalResultJson(stream);
            }
            return resultString;
        } catch (Exception e) {
            return null;
        }
    }

    public String requestReverseGeoCodingServiceSync() {
        boolean ok;
        this.httpClient = new WebClient();
        String requestUrl = getRequestUrl();
        if (NativeMapBuildSettings.isOpenAPIMapLibraryBuild()) {
            Map<String, String> header = new HashMap();
            header.put(HEADER_NAME_X_APPID, this.packageName);
            header.put(HEADER_NAME_X_PLATFORM, "android");
            ok = this.httpClient.requestGetWithHeader(requestUrl, header);
        } else {
            ok = this.httpClient.requestGet(requestUrl);
        }
        if (ok) {
            return getResultString();
        }
        return null;
    }

    public void requestReverseGeoCodingService() {
        this.canceled = false;
        new C03651().start();
    }

    public void cancelRequest() {
        this.canceled = true;
        if (this.httpClient != null) {
            this.httpClient.cancel();
        }
    }
}
