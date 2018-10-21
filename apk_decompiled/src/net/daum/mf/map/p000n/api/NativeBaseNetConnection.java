package net.daum.mf.map.p000n.api;

import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;
import net.daum.android.map.MapBuildSettings;
import net.daum.mf.map.common.MapConnectionSettingManager;
import net.daum.mf.map.common.MapThreadSettings;
import net.daum.mf.map.task.MapTaskManager;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/* renamed from: net.daum.mf.map.n.api.NativeBaseNetConnection */
public class NativeBaseNetConnection {
    public static final int NETCONNECTION4_STATE_CANCELED = 4;
    public static final int NETCONNECTION4_STATE_CONNECTED = 1;
    public static final int NETCONNECTION4_STATE_FINISHED = 3;
    public static final int NETCONNECTION4_STATE_READING = 2;
    public static final int NETCONNECTION4_STATE_READY = 0;
    private static final String TAG = "NativeBaseNetConnection";
    protected AtomicInteger _state = new AtomicInteger();
    protected AtomicBoolean aborted = new AtomicBoolean(false);
    protected ConcurrentLinkedQueue<NetBuffer> bufferQueue = new ConcurrentLinkedQueue();
    public long delegate;
    protected HttpClient httpclient;
    protected HttpGet httpget;
    private boolean retry;
    protected String url;

    /* renamed from: net.daum.mf.map.n.api.NativeBaseNetConnection$1 */
    class C03901 implements Runnable {
        C03901() {
        }

        public void run() {
            try {
                Thread.currentThread().setPriority(MapThreadSettings.getNetworkConnectionPriority());
            } catch (Exception e) {
                if (e != null) {
                    Log.e(NativeBaseNetConnection.class.getName(), "" + e.getMessage() + ",url=" + NativeBaseNetConnection.this.url);
                }
            }
            NativeBaseNetConnection.this.httpget = new HttpGet(NativeBaseNetConnection.this.url);
            NativeBaseNetConnection.this.httpget.addHeader("Accept-Encoding", "gzip");
            NativeBaseNetConnection.this.httpget.setHeader("User-Agent", NativeBaseNetConnection.this.getUserAgent());
            try {
                HttpResponse response = NativeBaseNetConnection.this.httpclient.execute(NativeBaseNetConnection.this.httpget);
                if (NativeBaseNetConnection.this.getState() != 4) {
                    NativeBaseNetConnection.this.setState(1);
                    NativeBaseNetConnection.this.notifyFinishConnection(response.getEntity(), response.getStatusLine().getStatusCode(), response.getAllHeaders());
                }
            } catch (SocketException e2) {
                if (NativeBaseNetConnection.this.retry) {
                    if (e2 != null) {
                        Log.e(NativeBaseNetConnection.class.getName(), "" + e2.getMessage() + ",url=" + NativeBaseNetConnection.this.url);
                    }
                    NativeBaseNetConnection.this.notifyFinishConnection(null, 0, null);
                    return;
                }
                NativeBaseNetConnection.this.retry = true;
                NativeBaseNetConnection.this.start();
            } catch (InterruptedIOException e3) {
                if (e3 != null) {
                    Log.e(NativeBaseNetConnection.class.getName(), "" + e3.getMessage() + ",url=" + NativeBaseNetConnection.this.url);
                }
                NativeBaseNetConnection.this.notifyFinishConnection(null, 0, null);
            } catch (AssertionError e4) {
                if (e4 != null) {
                    Log.e(NativeBaseNetConnection.class.getName(), "" + e4.getMessage() + ",url=" + NativeBaseNetConnection.this.url);
                }
                NativeBaseNetConnection.this.notifyFinishConnection(null, 0, null);
            } catch (Exception e5) {
                if (e5 != null) {
                    Log.e(NativeBaseNetConnection.class.getName(), "" + e5.getMessage() + ",url=" + NativeBaseNetConnection.this.url);
                }
                NativeBaseNetConnection.this.notifyFinishConnection(null, 0, null);
            }
        }
    }

    /* renamed from: net.daum.mf.map.n.api.NativeBaseNetConnection$NetBuffer */
    private static final class NetBuffer {
        public byte[] _buffer;
        public int _dataSize;
        public int _processedBytes = 0;

        public NetBuffer(byte[] buffer, int dataSize) {
            this._dataSize = dataSize;
            this._buffer = buffer;
        }
    }

    protected native void onFinishConnection(int i);

    protected native int onNetworkDataAsync(byte[] bArr, int i, int i2, int i3);

    protected native void onResponseHeader(int i, Header[] headerArr);

    static {
        NativeMapLibraryLoader.loadLibrary();
    }

    public NativeBaseNetConnection() {
        if (MapConnectionSettingManager.isKeepAliveEnabled().booleanValue()) {
            this.httpclient = new DefaultHttpClient(MapConnectionSettingManager.getNetworkConnectionManager(), MapConnectionSettingManager.getNetworkHttpParams());
        } else {
            this.httpclient = new DefaultHttpClient(MapConnectionSettingManager.getHttpParams());
        }
        this.httpget = null;
        this.retry = false;
        setState(0);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean start() {
        MapTaskManager.getInstance().execute(new C03901());
        return true;
    }

    public void cancel() {
        if (this.httpget != null) {
            setState(4);
            this.aborted.set(true);
            this.httpget.abort();
        }
    }

    public void setState(int state) {
        this._state.set(state);
    }

    public int getState() {
        return this._state.get();
    }

    void queueResponseHeader(final int statusCode, final Header[] httpResponse) {
        queueTask(new Runnable() {
            public void run() {
                if (!NativeBaseNetConnection.this.aborted.get()) {
                    NativeBaseNetConnection.this.onResponseHeader(statusCode, httpResponse);
                }
            }
        });
    }

    void queueNetworkData(byte[] data, int size) {
        this.bufferQueue.add(new NetBuffer(data, size));
    }

    void queueFinish(final int statusCode) {
        queueTask(new Runnable() {
            public void run() {
                if (!NativeBaseNetConnection.this.aborted.get()) {
                    NativeBaseNetConnection.this.onFinishConnection(statusCode);
                }
            }
        });
    }

    private void notifyFinishConnection(HttpEntity entity, int statusCode, Header[] httpResponse) {
        if (entity == null) {
            queueFinish(-1);
            return;
        }
        try {
            queueResponseHeader(statusCode, httpResponse);
            InputStream contentStream = entity.getContent();
            Header contentEncoding = null;
            for (Header header : httpResponse) {
                if (header.getName().equalsIgnoreCase("Content-Encoding")) {
                    contentEncoding = header;
                    break;
                }
            }
            if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                contentStream = new GZIPInputStream(contentStream);
            }
            setState(2);
            final int i = statusCode;
            queueTask(new Runnable() {
                protected void rerun() {
                    NativeBaseNetConnection.this.queueTask(this);
                }

                public void run() {
                    int state = NativeBaseNetConnection.this.getState();
                    if (state == 2 || NativeBaseNetConnection.this.bufferQueue.size() > 0) {
                        NetBuffer b = (NetBuffer) NativeBaseNetConnection.this.bufferQueue.peek();
                        if (b != null) {
                            int processed = NativeBaseNetConnection.this.onNetworkDataAsync(b._buffer, b._dataSize, b._processedBytes, b._dataSize + 1);
                            if (processed == -1) {
                                NativeBaseNetConnection.this.bufferQueue.clear();
                                if (!MapBuildSettings.getInstance().isDistribution()) {
                                    Log.i(NativeBaseNetConnection.class.getName(), "NETCONNECTION4 processed == -1");
                                    return;
                                }
                                return;
                            } else if (processed < b._dataSize) {
                                b._processedBytes = processed;
                            } else if (processed == b._dataSize) {
                                NativeBaseNetConnection.this.bufferQueue.remove();
                            }
                        }
                        rerun();
                    } else if (state == 3) {
                        NativeBaseNetConnection.this.queueFinish(i);
                    } else if (state == 4) {
                        NativeBaseNetConnection.this.queueFinish(i);
                    }
                }
            });
            try {
                byte[] data = readFromInputStream(contentStream, entity.getContentLength());
                if (data != null) {
                    queueNetworkData(data, data.length);
                    setState(3);
                } else {
                    setState(4);
                }
                contentStream.close();
                entity.consumeContent();
            } catch (IOException e) {
                Log.e(TAG, null, e);
                Log.e(TAG, "url=" + this.url);
            }
        } catch (Exception e2) {
            if (e2 != null) {
                Log.e(NativeBaseNetConnection.class.getName(), "" + e2.getMessage() + ",url=" + this.url);
            }
        }
    }

    private byte[] readFromInputStream(InputStream inputStream, long contentLength) throws IOException {
        int contentLengthInt = 0;
        try {
            contentLengthInt = safeLongToInt(contentLength);
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "", e);
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(contentLengthInt > 0 ? contentLengthInt : 32);
        ReadableByteChannel inputChannel = Channels.newChannel(inputStream);
        WritableByteChannel outputChannel = Channels.newChannel(byteArrayOutputStream);
        fastChannelCopy(inputChannel, outputChannel);
        byte[] data = byteArrayOutputStream.toByteArray();
        try {
            byteArrayOutputStream.close();
        } catch (IOException e2) {
            Log.d(TAG, "", e2);
        }
        try {
            inputChannel.close();
        } catch (IOException e22) {
            Log.d(TAG, "", e22);
        }
        try {
            outputChannel.close();
        } catch (IOException e222) {
            Log.d(TAG, "", e222);
        }
        return data;
    }

    private void fastChannelCopy(ReadableByteChannel inputChannel, WritableByteChannel outputChannel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocateDirect(16384);
        while (inputChannel.read(buffer) != -1) {
            buffer.flip();
            outputChannel.write(buffer);
            buffer.compact();
        }
        buffer.flip();
        while (buffer.hasRemaining()) {
            outputChannel.write(buffer);
        }
    }

    private int safeLongToInt(long l) throws IllegalArgumentException {
        if (l >= -2147483648L && l <= 2147483647L) {
            return (int) l;
        }
        throw new IllegalArgumentException(l + " cannot be cast to int without changing its value.");
    }

    protected void queueTask(Runnable task) {
    }

    protected String getUserAgent() {
        return "";
    }
}
