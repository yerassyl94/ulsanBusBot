package net.daum.mf.map.p000n.api;

import android.util.Log;
import net.daum.android.map.MapBuildSettings;
import net.daum.android.map.MapLibraryNativeConfig;

/* renamed from: net.daum.mf.map.n.api.NativeMapLibraryLoader */
public class NativeMapLibraryLoader {
    public static final String classPath = "net.daum.android.map.MapLibraryConfigImpl";
    private static boolean isLoaded = false;

    public static void loadLibrary() {
        if (!isLoaded) {
            try {
                Class<? extends MapLibraryNativeConfig> clazz = Class.forName(classPath).asSubclass(MapLibraryNativeConfig.class);
                if (clazz == null && !MapBuildSettings.getInstance().isDistribution()) {
                    Log.e(NativeMapLibraryLoader.class.getName(), "Please add a NativeMapLibraryLoader implementation class : net.daum.android.map.MapLibraryConfigImpl");
                }
                for (String name : ((MapLibraryNativeConfig) clazz.newInstance()).getLibraryNames()) {
                    try {
                        System.loadLibrary(name);
                        isLoaded = true;
                        break;
                    } catch (UnsatisfiedLinkError e) {
                        Log.e(NativeMapLibraryLoader.class.getName(), "Can`t load " + name + ".so file");
                    }
                }
                if (!isLoaded) {
                    throw new UnsatisfiedLinkError();
                }
            } catch (Exception e2) {
                throw new IllegalStateException(e2);
            }
        }
    }
}
