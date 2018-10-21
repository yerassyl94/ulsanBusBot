package net.daum.mf.map.p000n.api.internal;

import net.daum.mf.map.p000n.api.NativeMapLibraryLoader;

/* renamed from: net.daum.mf.map.n.api.internal.NativeMapTrafficManager */
public class NativeMapTrafficManager {
    public native boolean isRealtimeTrafficLayerEnabled();

    public native void setRealtimeTrafficLayerEnabled(boolean z);

    static {
        NativeMapLibraryLoader.loadLibrary();
    }
}
