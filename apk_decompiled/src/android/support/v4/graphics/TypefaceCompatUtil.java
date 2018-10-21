package android.support.v4.graphics;

import android.content.Context;
import android.content.res.Resources;
import android.os.Process;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.util.Log;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

@RestrictTo({Scope.LIBRARY_GROUP})
public class TypefaceCompatUtil {
    private static final String CACHE_FILE_PREFIX = ".font";
    private static final String TAG = "TypefaceCompatUtil";

    private TypefaceCompatUtil() {
    }

    public static File getTempFile(Context context) {
        String prefix = CACHE_FILE_PREFIX + Process.myPid() + "-" + Process.myTid() + "-";
        int i = 0;
        while (i < 100) {
            File file = new File(context.getCacheDir(), prefix + i);
            try {
                if (file.createNewFile()) {
                    return file;
                }
                i++;
            } catch (IOException e) {
            }
        }
        return null;
    }

    @RequiresApi(19)
    private static ByteBuffer mmap(File file) {
        Throwable th;
        try {
            Throwable th2;
            FileInputStream fis = new FileInputStream(file);
            Throwable th3 = null;
            try {
                FileChannel channel = fis.getChannel();
                ByteBuffer map = channel.map(MapMode.READ_ONLY, 0, channel.size());
                if (fis == null) {
                    return map;
                }
                if (null != null) {
                    try {
                        fis.close();
                        return map;
                    } catch (Throwable th4) {
                        th3.addSuppressed(th4);
                        return map;
                    }
                }
                fis.close();
                return map;
            } catch (Throwable th42) {
                Throwable th5 = th42;
                th42 = th2;
                th2 = th5;
            }
            if (fis != null) {
                if (th42 != null) {
                    try {
                        fis.close();
                    } catch (Throwable th6) {
                        th42.addSuppressed(th6);
                    }
                } else {
                    fis.close();
                }
            }
            throw th2;
            throw th2;
        } catch (IOException e) {
            return null;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @android.support.annotation.RequiresApi(19)
    public static java.nio.ByteBuffer mmap(android.content.Context r13, android.os.CancellationSignal r14, android.net.Uri r15) {
        /*
        r9 = r13.getContentResolver();
        r1 = "r";
        r8 = r9.openFileDescriptor(r15, r1, r14);	 Catch:{ IOException -> 0x0047 }
        r11 = 0;
        r7 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x0039, all -> 0x004e }
        r1 = r8.getFileDescriptor();	 Catch:{ Throwable -> 0x0039, all -> 0x004e }
        r7.<init>(r1);	 Catch:{ Throwable -> 0x0039, all -> 0x004e }
        r10 = 0;
        r0 = r7.getChannel();	 Catch:{ Throwable -> 0x005a, all -> 0x007a }
        r4 = r0.size();	 Catch:{ Throwable -> 0x005a, all -> 0x007a }
        r1 = java.nio.channels.FileChannel.MapMode.READ_ONLY;	 Catch:{ Throwable -> 0x005a, all -> 0x007a }
        r2 = 0;
        r1 = r0.map(r1, r2, r4);	 Catch:{ Throwable -> 0x005a, all -> 0x007a }
        if (r7 == 0) goto L_0x002c;
    L_0x0027:
        if (r10 == 0) goto L_0x004a;
    L_0x0029:
        r7.close();	 Catch:{ Throwable -> 0x0034, all -> 0x004e }
    L_0x002c:
        if (r8 == 0) goto L_0x0033;
    L_0x002e:
        if (r11 == 0) goto L_0x0056;
    L_0x0030:
        r8.close();	 Catch:{ Throwable -> 0x0051 }
    L_0x0033:
        return r1;
    L_0x0034:
        r2 = move-exception;
        r10.addSuppressed(r2);	 Catch:{ Throwable -> 0x0039, all -> 0x004e }
        goto L_0x002c;
    L_0x0039:
        r1 = move-exception;
        throw r1;	 Catch:{ all -> 0x003b }
    L_0x003b:
        r2 = move-exception;
        r12 = r2;
        r2 = r1;
        r1 = r12;
    L_0x003f:
        if (r8 == 0) goto L_0x0046;
    L_0x0041:
        if (r2 == 0) goto L_0x0076;
    L_0x0043:
        r8.close();	 Catch:{ Throwable -> 0x0071 }
    L_0x0046:
        throw r1;	 Catch:{ IOException -> 0x0047 }
    L_0x0047:
        r6 = move-exception;
        r1 = 0;
        goto L_0x0033;
    L_0x004a:
        r7.close();	 Catch:{ Throwable -> 0x0039, all -> 0x004e }
        goto L_0x002c;
    L_0x004e:
        r1 = move-exception;
        r2 = r11;
        goto L_0x003f;
    L_0x0051:
        r2 = move-exception;
        r11.addSuppressed(r2);	 Catch:{ IOException -> 0x0047 }
        goto L_0x0033;
    L_0x0056:
        r8.close();	 Catch:{ IOException -> 0x0047 }
        goto L_0x0033;
    L_0x005a:
        r1 = move-exception;
        throw r1;	 Catch:{ all -> 0x005c }
    L_0x005c:
        r2 = move-exception;
        r12 = r2;
        r2 = r1;
        r1 = r12;
    L_0x0060:
        if (r7 == 0) goto L_0x0067;
    L_0x0062:
        if (r2 == 0) goto L_0x006d;
    L_0x0064:
        r7.close();	 Catch:{ Throwable -> 0x0068, all -> 0x004e }
    L_0x0067:
        throw r1;	 Catch:{ Throwable -> 0x0039, all -> 0x004e }
    L_0x0068:
        r3 = move-exception;
        r2.addSuppressed(r3);	 Catch:{ Throwable -> 0x0039, all -> 0x004e }
        goto L_0x0067;
    L_0x006d:
        r7.close();	 Catch:{ Throwable -> 0x0039, all -> 0x004e }
        goto L_0x0067;
    L_0x0071:
        r3 = move-exception;
        r2.addSuppressed(r3);	 Catch:{ IOException -> 0x0047 }
        goto L_0x0046;
    L_0x0076:
        r8.close();	 Catch:{ IOException -> 0x0047 }
        goto L_0x0046;
    L_0x007a:
        r1 = move-exception;
        r2 = r10;
        goto L_0x0060;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.graphics.TypefaceCompatUtil.mmap(android.content.Context, android.os.CancellationSignal, android.net.Uri):java.nio.ByteBuffer");
    }

    @RequiresApi(19)
    public static ByteBuffer copyToDirectBuffer(Context context, Resources res, int id) {
        ByteBuffer byteBuffer = null;
        File tmpFile = getTempFile(context);
        if (tmpFile != null) {
            try {
                if (copyToFile(tmpFile, res, id)) {
                    byteBuffer = mmap(tmpFile);
                    tmpFile.delete();
                }
            } finally {
                tmpFile.delete();
            }
        }
        return byteBuffer;
    }

    public static boolean copyToFile(File file, InputStream is) {
        IOException e;
        Throwable th;
        FileOutputStream fileOutputStream = null;
        try {
            FileOutputStream os = new FileOutputStream(file, false);
            try {
                byte[] buffer = new byte[1024];
                while (true) {
                    int readLen = is.read(buffer);
                    if (readLen != -1) {
                        os.write(buffer, 0, readLen);
                    } else {
                        closeQuietly(os);
                        fileOutputStream = os;
                        return true;
                    }
                }
            } catch (IOException e2) {
                e = e2;
                fileOutputStream = os;
                try {
                    Log.e(TAG, "Error copying resource contents to temp file: " + e.getMessage());
                    closeQuietly(fileOutputStream);
                    return false;
                } catch (Throwable th2) {
                    th = th2;
                    closeQuietly(fileOutputStream);
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                fileOutputStream = os;
                closeQuietly(fileOutputStream);
                throw th;
            }
        } catch (IOException e3) {
            e = e3;
            Log.e(TAG, "Error copying resource contents to temp file: " + e.getMessage());
            closeQuietly(fileOutputStream);
            return false;
        }
    }

    public static boolean copyToFile(File file, Resources res, int id) {
        InputStream inputStream = null;
        try {
            inputStream = res.openRawResource(id);
            boolean copyToFile = copyToFile(file, inputStream);
            return copyToFile;
        } finally {
            closeQuietly(inputStream);
        }
    }

    public static void closeQuietly(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
            }
        }
    }
}
