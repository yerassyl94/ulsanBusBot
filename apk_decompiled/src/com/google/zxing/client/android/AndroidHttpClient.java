package com.google.zxing.client.android;

import com.kakao.util.maps.helper.CommonProtocol;
import java.io.IOException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

public final class AndroidHttpClient implements HttpClient {
    private final HttpClient delegate;

    private static class DelegateHttpClient extends DefaultHttpClient {
        private DelegateHttpClient(ClientConnectionManager ccm, HttpParams params) {
            super(ccm, params);
        }

        protected HttpContext createHttpContext() {
            HttpContext context = new BasicHttpContext();
            context.setAttribute("http.authscheme-registry", getAuthSchemes());
            context.setAttribute("http.cookiespec-registry", getCookieSpecs());
            context.setAttribute("http.auth.credentials-provider", getCredentialsProvider());
            return context;
        }
    }

    public static AndroidHttpClient newInstance(String userAgent) {
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setStaleCheckingEnabled(params, false);
        HttpConnectionParams.setConnectionTimeout(params, 20000);
        HttpConnectionParams.setSoTimeout(params, 20000);
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        HttpClientParams.setRedirecting(params, false);
        if (userAgent != null) {
            HttpProtocolParams.setUserAgent(params, userAgent);
        }
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme(CommonProtocol.URL_SCHEME, SSLSocketFactory.getSocketFactory(), 443));
        return new AndroidHttpClient(new ThreadSafeClientConnManager(params, schemeRegistry), params);
    }

    private AndroidHttpClient(ClientConnectionManager ccm, HttpParams params) {
        this.delegate = new DelegateHttpClient(ccm, params);
    }

    public void close() {
        getConnectionManager().shutdown();
    }

    public HttpParams getParams() {
        return this.delegate.getParams();
    }

    public ClientConnectionManager getConnectionManager() {
        return this.delegate.getConnectionManager();
    }

    public HttpResponse execute(HttpUriRequest request) throws IOException {
        return this.delegate.execute(request);
    }

    public HttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException {
        return this.delegate.execute(request, context);
    }

    public HttpResponse execute(HttpHost target, HttpRequest request) throws IOException {
        return this.delegate.execute(target, request);
    }

    public HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context) throws IOException {
        return this.delegate.execute(target, request, context);
    }

    public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException {
        return this.delegate.execute(request, responseHandler);
    }

    public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException {
        return this.delegate.execute(request, responseHandler, context);
    }

    public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler) throws IOException {
        return this.delegate.execute(target, request, responseHandler);
    }

    public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException {
        return this.delegate.execute(target, request, responseHandler, context);
    }
}
