package net.daum.mf.map.common.net;

import android.text.TextUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import javax.net.ssl.SSLException;
import net.daum.android.map.MapController;
import net.daum.mf.map.common.io.InputStreamUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.HeaderGroup;

public class NetWebClient {
    static final int DEFAULT_CONNECTION_TIMEOUT = 30000;
    static final int DEFAULT_SOCKET_TIMEOUT = 60000;
    static final String HEADER_NAME_COOKIE = "Cookie";
    static final String HEADER_NAME_USER_AGENT = "User-Agent";
    static final String METHOD_GET = "GET";
    static final String METHOD_HEAD = "HEAD";
    static final String METHOD_POST = "POST";
    static final String REQUEST_ENCODING = "utf-8";
    private HttpURLConnection _connection;
    private int _connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
    private HeaderGroup _headergroup;
    private boolean _isResponseHandled = false;
    boolean _keepAlive = true;
    private ArrayList<NameValuePair> _params;
    private int _responseCode;
    private int _socketTimeout = DEFAULT_SOCKET_TIMEOUT;
    private ArrayList<HeaderItem> headers = new ArrayList();
    protected String userAgent;

    protected static class HeaderItem {
        private HeaderItemMethod method;
        private String name;
        private String value;

        public HeaderItem(HeaderItemMethod method, String name, String value) {
            this.method = method;
            this.name = name;
            this.value = value;
        }

        public HeaderItemMethod getMethod() {
            return this.method;
        }

        public String getName() {
            return this.name;
        }

        public String getValue() {
            return this.value;
        }
    }

    protected enum HeaderItemMethod {
        ADD,
        SET
    }

    protected static class HeaderItemAdding extends HeaderItem {
        public HeaderItemAdding(String name, String value) {
            super(HeaderItemMethod.ADD, name, value);
        }
    }

    protected static class HeaderItemSetting extends HeaderItem {
        public HeaderItemSetting(String name, String value) {
            super(HeaderItemMethod.SET, name, value);
        }
    }

    public String getUserAgent() {
        return this.userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public static boolean isSuccessStatusCode(int statusCode) {
        return statusCode >= 200 && statusCode < MapController.MAP_LAYER_TYPE_ROAD_VIEW;
    }

    public int getConnectionTimeout() {
        return this._connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this._connectionTimeout = connectionTimeout;
    }

    public int getSocketTimeout() {
        return this._socketTimeout;
    }

    private void _setTimeout(int connectTimeout, int readTimeout) {
        this._connection.setReadTimeout(readTimeout);
        this._connection.setConnectTimeout(connectTimeout);
    }

    private void setHeaderItems(HttpURLConnection conn) {
        Iterator i$ = this.headers.iterator();
        while (i$.hasNext()) {
            HeaderItem item = (HeaderItem) i$.next();
            if (item.getMethod() == HeaderItemMethod.ADD) {
                conn.addRequestProperty(item.getName(), item.getValue());
            } else {
                conn.setRequestProperty(item.getName(), item.getValue());
            }
        }
    }

    private boolean _request(String requestMethod, String url, String cookie) throws SSLException {
        this._isResponseHandled = false;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(URI.create(url).toASCIIString()).openConnection();
            this._connection = connection;
            connection.setRequestMethod(requestMethod);
            if (this._keepAlive) {
                connection.setRequestProperty("connection", "Keep-Alive");
            } else {
                connection.setRequestProperty("connection", "Close");
            }
            _setTimeout(getConnectionTimeout(), getSocketTimeout());
            if (!TextUtils.isEmpty(cookie)) {
                connection.addRequestProperty(HEADER_NAME_COOKIE, cookie);
            }
            if (!TextUtils.isEmpty(this.userAgent)) {
                connection.addRequestProperty(HEADER_NAME_USER_AGENT, this.userAgent);
            }
            if (this._params != null) {
                HttpEntity entity = new UrlEncodedFormEntity(this._params, REQUEST_ENCODING);
                Header contentType = entity.getContentType();
                if (contentType != null) {
                    connection.addRequestProperty("Content-Type", contentType.getValue());
                }
                Header contentEncoding = entity.getContentEncoding();
                if (contentEncoding != null) {
                    connection.addRequestProperty("Content-Encoding", contentEncoding.getValue());
                }
                long contentLength = entity.getContentLength();
                if (contentLength >= 0) {
                    connection.addRequestProperty("Content-Length", Long.toString(contentLength));
                }
                setHeaderItems(connection);
                if (contentLength != 0) {
                    connection.setDoOutput(true);
                    if (contentLength < 0 || contentLength > 2147483647L) {
                        connection.setChunkedStreamingMode(0);
                    } else {
                        connection.setFixedLengthStreamingMode((int) contentLength);
                    }
                    entity.writeTo(connection.getOutputStream());
                }
            } else {
                setHeaderItems(connection);
            }
            connection.connect();
            return true;
        } catch (MalformedURLException e) {
            return false;
        } catch (UnsupportedEncodingException e2) {
            return false;
        } catch (SSLException e3) {
            throw e3;
        } catch (IOException e4) {
            return false;
        } catch (NullPointerException e5) {
            return false;
        }
    }

    public boolean requestGetWithCookie(String url, String cookie) throws SSLException {
        return _request(METHOD_GET, url, cookie);
    }

    public boolean requestHead(String url) throws SSLException {
        return _request(METHOD_HEAD, url, null);
    }

    public boolean requestPost(String url, ArrayList<NameValuePair> params, String cookie) throws SSLException {
        this._params = params;
        return _request(METHOD_POST, url, cookie);
    }

    public void cancel() {
        if (this._connection != null) {
            this._connection.disconnect();
        }
    }

    private void _getResponse() {
        if (!this._isResponseHandled) {
            this._isResponseHandled = true;
            this._responseCode = 0;
            HttpURLConnection connection = this._connection;
            try {
                this._responseCode = connection.getResponseCode();
                this._headergroup = new HeaderGroup();
                for (Entry<String, List<String>> entry : connection.getHeaderFields().entrySet()) {
                    String key = (String) entry.getKey();
                    if (key != null) {
                        for (String value : (List) entry.getValue()) {
                            if (value != null) {
                                this._headergroup.addHeader(new BasicHeader(key, value));
                            }
                        }
                    }
                }
            } catch (IOException e) {
            }
        }
    }

    public InputStream openContentStream() throws IllegalStateException, IOException {
        _getResponse();
        return isSuccessStatusCode(this._responseCode) ? this._connection.getInputStream() : this._connection.getErrorStream();
    }

    public String getContentString(String encoding) {
        _getResponse();
        String result = null;
        try {
            result = InputStreamUtils.toString(openContentStream(), encoding, (int) getContentLength());
        } catch (IllegalStateException e) {
        } catch (IOException e2) {
        }
        return result;
    }

    public int getStatusCode() {
        _getResponse();
        return this._responseCode;
    }

    public long getContentLength() {
        _getResponse();
        String string = this._connection.getHeaderField("Content-Length");
        return string == null ? -1 : Long.parseLong(string);
    }

    public Header[] getHeaders(String name) {
        _getResponse();
        return this._headergroup == null ? null : this._headergroup.getHeaders(name);
    }

    public void addHeader(String name, String value) {
        this.headers.add(new HeaderItemAdding(name, value));
    }

    public void setHeader(String name, String value) {
        this.headers.add(new HeaderItemSetting(name, value));
    }
}
