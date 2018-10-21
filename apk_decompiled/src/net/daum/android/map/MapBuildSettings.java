package net.daum.android.map;

import net.daum.mf.map.p000n.api.internal.NativeMapBuildSettings;

public class MapBuildSettings {
    private static MapBuildSettings _instance = new MapBuildSettings();
    private NativeMapBuildSettings nativeMapBuildSettings = new NativeMapBuildSettings();

    public static MapBuildSettings getInstance() {
        return _instance;
    }

    private MapBuildSettings() {
    }

    public boolean isDebug() {
        return this.nativeMapBuildSettings.isDebug();
    }

    public boolean isRelease() {
        return this.nativeMapBuildSettings.isRelease();
    }

    public boolean isDistribution() {
        return this.nativeMapBuildSettings.isDistribution();
    }

    public boolean isDev() {
        return this.nativeMapBuildSettings.isDev();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("build config(");
        if (isDebug()) {
            stringBuilder.append("Debug)");
        } else if (isRelease()) {
            stringBuilder.append("Release)");
        } else if (isDistribution()) {
            stringBuilder.append("Distribution)");
        }
        return stringBuilder.toString();
    }
}
