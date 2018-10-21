package net.daum.mf.map.common;

import android.os.Build.VERSION;
import com.kakao.util.maps.helper.CommonProtocol;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

public class MapConnectionSettingManager {
    private static final int CONNECTION_TIMEOUT = 30000;
    private static final int SOCKET_TIMEOUT = 60000;
    private static ThreadSafeClientConnManager _connectionManager;
    private static BasicHttpParams _httpParams;
    private static Boolean _setTimeout = Boolean.valueOf(false);
    private static Boolean _useThreadSafeConnManager = Boolean.valueOf(false);

    static {
        _httpParams = null;
        _connectionManager = null;
        if (_useThreadSafeConnManager.booleanValue()) {
            _httpParams = new BasicHttpParams();
            if (_setTimeout.booleanValue()) {
                HttpConnectionParams.setConnectionTimeout(_httpParams, CONNECTION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(_httpParams, SOCKET_TIMEOUT);
                ConnManagerParams.setTimeout(_httpParams, 30000);
                ConnManagerParams.setMaxTotalConnections(_httpParams, 16);
            }
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            schemeRegistry.register(new Scheme(CommonProtocol.URL_SCHEME, SSLSocketFactory.getSocketFactory(), 443));
            _connectionManager = new ThreadSafeClientConnManager(_httpParams, schemeRegistry);
        }
    }

    public static Boolean isKeepAliveEnabled() {
        return _useThreadSafeConnManager;
    }

    public static BasicHttpParams getNetworkHttpParams() {
        return _httpParams;
    }

    public static BasicHttpParams getHttpParams() {
        BasicHttpParams httpParams = new BasicHttpParams();
        if (VERSION.SDK_INT <= 13) {
            HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
        }
        return httpParams;
    }

    public static ClientConnectionManager getNetworkConnectionManager() {
        return _connectionManager;
    }
}
