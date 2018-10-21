package net.daum.android.map;

import java.io.InputStream;
import net.daum.mf.map.common.net.WebClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class MapTileVersionCheckWebService {
    private WebClient httpClient;
    private String hybridTileVersion;
    private String imageTileVersion;
    private MapTileVersionCheckResultListener resultListener;
    private String roadViewTileVersion;

    /* renamed from: net.daum.android.map.MapTileVersionCheckWebService$1 */
    class C03551 extends Thread {
        C03551() {
        }

        public void run() {
            MapTileVersionCheckWebService.this.httpClient = new WebClient();
            if (MapTileVersionCheckWebService.this.httpClient.requestGet("http://dmaps.daum.net/map_js_init/maps_version.xml")) {
                if (MapTileVersionCheckWebService.this.resultListener != null) {
                    try {
                        MapTileVersionCheckWebService.this._parseResultXML(MapTileVersionCheckWebService.this.httpClient.openContentStream());
                        MapTileVersionCheckWebService.this.resultListener.onMapTileVersionCheckResultReceived(MapTileVersionCheckWebService.this.imageTileVersion, MapTileVersionCheckWebService.this.hybridTileVersion, MapTileVersionCheckWebService.this.roadViewTileVersion);
                    } catch (Exception e) {
                        MapTileVersionCheckWebService.this.resultListener.onMapTileVersionCheckServiceErrorOccured();
                    }
                }
            } else if (MapTileVersionCheckWebService.this.resultListener != null) {
                MapTileVersionCheckWebService.this.resultListener.onMapTileVersionCheckServiceErrorOccured();
            }
        }
    }

    public interface MapTileVersionCheckResultListener {
        void onMapTileVersionCheckResultReceived(String str, String str2, String str3);

        void onMapTileVersionCheckServiceErrorOccured();
    }

    public MapTileVersionCheckWebService(MapTileVersionCheckResultListener resultListener) {
        this.resultListener = resultListener;
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
                    if (tag.compareTo("version") != 0) {
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
                    if (tag.compareTo("version") != 0) {
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

    public void requestMapTileVersionCheckService() {
        new C03551().start();
    }
}
