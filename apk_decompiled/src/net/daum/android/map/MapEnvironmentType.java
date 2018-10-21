package net.daum.android.map;

import net.daum.mf.map.p000n.api.internal.NativeMapEnvironmentType;

public class MapEnvironmentType {
    private static MapEnvironmentType _instance = new MapEnvironmentType();
    private NativeMapEnvironmentType nativeMapEnvironmentType = new NativeMapEnvironmentType();

    public static MapEnvironmentType getInstance() {
        return _instance;
    }

    private MapEnvironmentType() {
        MapBuildSettings buildSetting = MapBuildSettings.getInstance();
        if (buildSetting.isDebug()) {
            setMapEnvironmentType(1);
        } else if (buildSetting.isRelease()) {
            setMapEnvironmentType(2);
        } else if (buildSetting.isDistribution()) {
            setMapEnvironmentType(3);
        }
    }

    public void setMapEnvironmentType(int type) {
        this.nativeMapEnvironmentType.setMapEnvironmentType(type);
    }

    public String getHostAddress() {
        return this.nativeMapEnvironmentType.getHostAddress();
    }

    public boolean isAlpha() {
        return this.nativeMapEnvironmentType.isAlpha();
    }

    public boolean isBeta() {
        return this.nativeMapEnvironmentType.isBeta();
    }

    public boolean isProduction() {
        return this.nativeMapEnvironmentType.isProduction();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("server type(");
        if (isAlpha()) {
            stringBuilder.append("alpha)");
        } else if (isBeta()) {
            stringBuilder.append("beta)");
        } else if (isProduction()) {
            stringBuilder.append("product)");
        }
        return stringBuilder.toString();
    }
}
