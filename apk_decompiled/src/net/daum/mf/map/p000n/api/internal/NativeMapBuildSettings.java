package net.daum.mf.map.p000n.api.internal;

import net.daum.mf.map.p000n.api.NativeMapLibraryLoader;

/* renamed from: net.daum.mf.map.n.api.internal.NativeMapBuildSettings */
public class NativeMapBuildSettings {
    public static native boolean isMapLibraryBuild();

    public static native boolean isOpenAPIMapLibraryBuild();

    public native boolean isDebug();

    public native boolean isDev();

    public native boolean isDistribution();

    public native boolean isRelease();

    static {
        NativeMapLibraryLoader.loadLibrary();
    }
}
