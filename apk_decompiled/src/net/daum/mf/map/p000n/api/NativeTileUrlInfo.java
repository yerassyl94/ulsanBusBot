package net.daum.mf.map.p000n.api;

/* renamed from: net.daum.mf.map.n.api.NativeTileUrlInfo */
public class NativeTileUrlInfo {
    public static native void resetToDefaultTileVersion();

    public static native void setHybridTileVersion(String str);

    public static native void setImageTileVersion(String str);

    public static native void setRoadViewTileVersion(String str);

    static {
        NativeMapLibraryLoader.loadLibrary();
    }
}
