package net.daum.mf.map.gen;

import net.daum.mf.map.api.BuildConfig;

public class DaumMapLibraryAndroidMeta {
    protected DaumMapLibraryAndroidMeta() {
    }

    public static String getVersion() {
        return BuildConfig.SDK_VERSION;
    }
}
