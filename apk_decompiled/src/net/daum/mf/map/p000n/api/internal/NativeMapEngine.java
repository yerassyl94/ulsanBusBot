package net.daum.mf.map.p000n.api.internal;

import net.daum.mf.map.p000n.api.NativeMapLibraryLoader;

/* renamed from: net.daum.mf.map.n.api.internal.NativeMapEngine */
public class NativeMapEngine {
    public native void onInitializeMapEngine();

    public native void onLoopMapEngine();

    public native void onPauseMapEngine();

    public native void onResumeMapEngine();

    public native void onStartMapEngine();

    public native void onStopMapEngine();

    static {
        NativeMapLibraryLoader.loadLibrary();
    }
}
