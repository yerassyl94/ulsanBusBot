package net.daum.mf.map.p000n.api;

/* renamed from: net.daum.mf.map.n.api.NativeMapLoopScheduling */
public class NativeMapLoopScheduling {
    public native boolean isBusyLoop();

    public native boolean isRoadViewInBusyLoop();

    public native void setBusyLoop(boolean z);

    public native void setRoadViewBusyLoop(boolean z);

    static {
        NativeMapLibraryLoader.loadLibrary();
    }
}
