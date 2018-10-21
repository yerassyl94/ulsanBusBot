package net.daum.mf.map.p000n.api.internal;

import net.daum.mf.map.p000n.api.NativeMapLibraryLoader;

/* renamed from: net.daum.mf.map.n.api.internal.NativeMapEnvironmentType */
public class NativeMapEnvironmentType {
    public static final int MAP_ENVIRONMENT_TYPE_ALPHA = 1;
    public static final int MAP_ENVIRONMENT_TYPE_BETA = 2;
    public static final int MAP_ENVIRONMENT_TYPE_PRODUCTION = 3;

    public native String getHostAddress();

    public native boolean isAlpha();

    public native boolean isBeta();

    public native boolean isProduction();

    public native void setMapEnvironmentType(int i);

    static {
        NativeMapLibraryLoader.loadLibrary();
    }
}
