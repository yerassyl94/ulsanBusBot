package net.daum.mf.map.p000n.api.internal;

import net.daum.mf.map.api.MapView;

/* renamed from: net.daum.mf.map.n.api.internal.NativeDeviceCheckUtilsMapLibrary */
public class NativeDeviceCheckUtilsMapLibrary {
    public static boolean canUseDiskCache() {
        return MapView.isMapTilePersistentCacheEnabled();
    }
}
