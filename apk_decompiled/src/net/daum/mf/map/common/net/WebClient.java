package net.daum.mf.map.common.net;

import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.daum.android.map.MapBuildSettings;
import net.daum.mf.map.common.io.CloseableUtils;
import net.daum.mf.map.common.io.InputStreamUtils;
import org.apache.http.Header;
import org.apache.http.NameValuePair;

public class WebClient {
    private static ExecutorService executor = new ThreadPoolExecutor(2, ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED, 60, TimeUnit.SECONDS, new SynchronousQueue());
    NetWebClient _httpClient;

    public void execute(Runnable task) {
        executor.execute(task);
    }

    private void _createHttpClientIfNeeded() {
        if (this._httpClient == null) {
            this._httpClient = new NetWebClient();
        }
    }

    private boolean _requestGet(String url, Map<String, String> header) {
        _createHttpClientIfNeeded();
        if (header != null) {
            for (String key : header.keySet()) {
                this._httpClient.addHeader(key, (String) header.get(key));
            }
        }
        this._httpClient.setUserAgent(HttpProtocolUtils.getUserAgent());
        try {
            this._httpClient.requestGetWithCookie(url, null);
            return true;
        } catch (Exception e) {
            Log.e(WebClient.class.getName(), "" + e.getMessage());
            return false;
        }
    }

    public boolean requestGet(String url) {
        return _requestGet(url, null);
    }

    public boolean requestGetWithHeader(String url, Map<String, String> header) {
        return _requestGet(url, header);
    }

    public boolean requestPost(String url, ArrayList<NameValuePair> params, String cookie) {
        _createHttpClientIfNeeded();
        this._httpClient.setUserAgent(HttpProtocolUtils.getUserAgent());
        try {
            this._httpClient.requestPost(url, params, cookie);
            return true;
        } catch (Exception e) {
            Log.e(WebClient.class.getName(), "" + e.getMessage());
            return false;
        }
    }

    public boolean requestPost(String url, ArrayList<NameValuePair> params) {
        return requestPost(url, params, null);
    }

    public void cancel() {
        if (this._httpClient != null) {
            this._httpClient.cancel();
        }
        if (!MapBuildSettings.getInstance().isDistribution()) {
            Log.d(WebClient.class.getName(), "NetworkCanceled");
        }
    }

    public InputStream openContentStream() throws IllegalStateException, IOException {
        InputStream inputStream = null;
        if (this._httpClient != null) {
            try {
                inputStream = this._httpClient.openContentStream();
            } catch (Exception e) {
                Log.e(WebClient.class.getName(), "" + e.getMessage());
            }
        }
        return inputStream;
    }

    public String getContentString(String encoding) {
        if (this._httpClient == null) {
            return null;
        }
        long contentLength = this._httpClient.getContentLength();
        InputStream is = null;
        String result = null;
        try {
            is = this._httpClient.openContentStream();
            result = InputStreamUtils.toString(is, encoding, (int) contentLength);
            return result;
        } catch (Exception e) {
            Log.e(WebClient.class.getName(), "" + e.getMessage());
            return result;
        } finally {
            CloseableUtils.closeQuietly(is);
        }
    }

    public Header[] getHeaders(String name) {
        if (this._httpClient == null) {
            return null;
        }
        return this._httpClient.getHeaders(name);
    }

    public int getStatusCode() {
        if (this._httpClient == null) {
            return -1;
        }
        return this._httpClient.getStatusCode();
    }

    public long getContentLength() {
        if (this._httpClient == null) {
            return -1;
        }
        return this._httpClient.getContentLength();
    }
}
