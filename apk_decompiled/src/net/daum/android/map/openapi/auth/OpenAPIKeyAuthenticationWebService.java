package net.daum.android.map.openapi.auth;

import android.content.Context;
import com.kakao.util.maps.helper.CommonProtocol;
import com.kakao.util.maps.helper.SystemInfo;
import com.kakao.util.maps.helper.Utility;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import net.daum.mf.map.api.BuildConfig;
import net.daum.mf.map.common.net.WebClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class OpenAPIKeyAuthenticationWebService {
    private static int INVALID_RESULT_CODE = -99999;
    private static String openAPILibraryVersion = BuildConfig.SDK_VERSION;
    private String KA;
    private String apiKey;
    private String appId;
    private String appVersion;
    private WebClient httpClient;
    private String hybridTileVersion;
    private String imageTileVersion;
    private int resultCode = INVALID_RESULT_CODE;
    private WeakReference<OpenAPIKeyAuthenticationResultListener> resultListener;
    private String resultMessage;
    private String roadViewTileVersion;

    /* renamed from: net.daum.android.map.openapi.auth.OpenAPIKeyAuthenticationWebService$1 */
    class C03721 extends Thread {
        C03721() {
        }

        public void run() {
            OpenAPIKeyAuthenticationWebService.this.httpClient = new WebClient();
            Map<String, String> header = new HashMap();
            header.put(CommonProtocol.KA_AUTH_HEADER_KEY, "KakaoAK " + OpenAPIKeyAuthenticationWebService.this.apiKey);
            header.put(CommonProtocol.KA_HEADER_KEY, OpenAPIKeyAuthenticationWebService.this.KA);
            if (OpenAPIKeyAuthenticationWebService.this.httpClient.requestGetWithHeader(String.format("https://dapi.kakao.com/v2/maps/auth.xml", new Object[0]), header)) {
                if (OpenAPIKeyAuthenticationWebService.this.resultListener != null && OpenAPIKeyAuthenticationWebService.this.resultListener.get() != null) {
                    try {
                        InputStream xmlStream = OpenAPIKeyAuthenticationWebService.this.httpClient.openContentStream();
                        OpenAPIKeyAuthenticationWebService.this.resultCode = OpenAPIKeyAuthenticationWebService.this.httpClient.getStatusCode();
                        OpenAPIKeyAuthenticationWebService.this.resultMessage = null;
                        OpenAPIKeyAuthenticationWebService.this._parseResultXML(xmlStream);
                    } catch (Exception e) {
                        ((OpenAPIKeyAuthenticationResultListener) OpenAPIKeyAuthenticationWebService.this.resultListener.get()).onAuthenticationErrorOccured();
                    }
                    if (OpenAPIKeyAuthenticationWebService.this.resultCode != 200) {
                        ((OpenAPIKeyAuthenticationResultListener) OpenAPIKeyAuthenticationWebService.this.resultListener.get()).onAuthenticationErrorOccured();
                    }
                    ((OpenAPIKeyAuthenticationResultListener) OpenAPIKeyAuthenticationWebService.this.resultListener.get()).onAuthenticationResultReceived(OpenAPIKeyAuthenticationWebService.this.resultCode, OpenAPIKeyAuthenticationWebService.this.resultMessage, OpenAPIKeyAuthenticationWebService.this.imageTileVersion, OpenAPIKeyAuthenticationWebService.this.hybridTileVersion, OpenAPIKeyAuthenticationWebService.this.roadViewTileVersion);
                }
            } else if (OpenAPIKeyAuthenticationWebService.this.resultListener != null && OpenAPIKeyAuthenticationWebService.this.resultListener.get() != null) {
                ((OpenAPIKeyAuthenticationResultListener) OpenAPIKeyAuthenticationWebService.this.resultListener.get()).onAuthenticationErrorOccured();
            }
        }
    }

    public interface OpenAPIKeyAuthenticationResultListener {
        void onAuthenticationErrorOccured();

        void onAuthenticationResultReceived(int i, String str, String str2, String str3, String str4);
    }

    public OpenAPIKeyAuthenticationWebService(Context ctxt, String apiKey, String appId, String appVersion, OpenAPIKeyAuthenticationResultListener resultListener) {
        this.appId = appId;
        this.appVersion = appVersion;
        this.resultListener = new WeakReference(resultListener);
        if (ctxt != null) {
            String appKey = Utility.getMetadata(ctxt, CommonProtocol.APP_KEY_PROPERTY);
            if (appKey == null || appKey.length() == 0) {
                this.apiKey = apiKey;
            } else {
                this.apiKey = appKey;
            }
            SystemInfo.initialize(ctxt);
            this.KA = SystemInfo.getKAHeader();
            return;
        }
        this.apiKey = apiKey;
    }

    private void _parseResultXML(InputStream contentStream) throws Exception {
        XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
        parser.setInput(contentStream, "utf-8");
        boolean inMapTileVersion = false;
        boolean inMapTileVersionImage = false;
        boolean inMapTileVersionHybrid = false;
        boolean inMapTileVersionRoadView = false;
        for (int parserEvent = parser.getEventType(); parserEvent != 1; parserEvent = parser.next()) {
            String tag;
            switch (parserEvent) {
                case 2:
                    tag = parser.getName();
                    if (tag.compareTo("mapTileVersion") != 0) {
                        if (inMapTileVersion) {
                            if (tag.compareTo("i") != 0) {
                                if (tag.compareTo("h") != 0) {
                                    if (tag.compareTo("t") != 0) {
                                        break;
                                    }
                                    inMapTileVersionRoadView = true;
                                    break;
                                }
                                inMapTileVersionHybrid = true;
                                break;
                            }
                            inMapTileVersionImage = true;
                            break;
                        }
                        break;
                    }
                    inMapTileVersion = true;
                    break;
                case 3:
                    tag = parser.getName();
                    if (tag.compareTo("mapTileVersion") != 0) {
                        if (inMapTileVersion) {
                            if (tag.compareTo("i") != 0) {
                                if (tag.compareTo("h") != 0) {
                                    if (tag.compareTo("t") != 0) {
                                        break;
                                    }
                                    inMapTileVersionRoadView = false;
                                    break;
                                }
                                inMapTileVersionHybrid = false;
                                break;
                            }
                            inMapTileVersionImage = false;
                            break;
                        }
                        break;
                    }
                    inMapTileVersion = false;
                    break;
                case 4:
                    if (inMapTileVersion) {
                        if (!inMapTileVersionImage) {
                            if (!inMapTileVersionHybrid) {
                                if (!inMapTileVersionRoadView) {
                                    break;
                                }
                                this.roadViewTileVersion = parser.getText().trim();
                                break;
                            }
                            this.hybridTileVersion = parser.getText().trim();
                            break;
                        }
                        this.imageTileVersion = parser.getText().trim();
                        break;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void requestOpenAPIKeyAuthenticationService() {
        if (this.apiKey != null && this.apiKey.length() != 0) {
            new C03721().start();
        } else if (this.resultListener != null && this.resultListener.get() != null) {
            ((OpenAPIKeyAuthenticationResultListener) this.resultListener.get()).onAuthenticationErrorOccured();
            ((OpenAPIKeyAuthenticationResultListener) this.resultListener.get()).onAuthenticationResultReceived(-101, "API Key를 설정하지 않았습니다.", null, null, null);
        }
    }
}
