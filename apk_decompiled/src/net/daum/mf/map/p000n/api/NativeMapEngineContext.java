package net.daum.mf.map.p000n.api;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/* renamed from: net.daum.mf.map.n.api.NativeMapEngineContext */
public final class NativeMapEngineContext {
    private static final NativeMapEngineContext instance = new NativeMapEngineContext();
    private Context appContext;

    public static NativeMapEngineContext getInstance() {
        return instance;
    }

    public void setApplicationContext(Context context) {
        if (context instanceof Application) {
            this.appContext = context;
            return;
        }
        throw new IllegalArgumentException("Please set Applcation Context.");
    }

    public Context getApplicationContext() {
        return this.appContext;
    }

    public String getApplicationRoot() {
        return this.appContext.getApplicationInfo().publicSourceDir;
    }

    public String getApplicationDataDirectory() {
        return this.appContext.getApplicationInfo().dataDir;
    }

    public String getCacheDirectory() {
        return this.appContext.getCacheDir().getAbsolutePath();
    }

    public String getExternalCacheDirectory() {
        return getCacheDirectory();
    }

    public String getApplicationCacheDirectory() {
        return getCacheDirectory();
    }

    public String getApplicationPackageName() {
        return this.appContext.getApplicationInfo().packageName.replace(".", "/");
    }

    public DisplayMetrics getMainScreenSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) this.appContext.getSystemService("window")).getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }
}
