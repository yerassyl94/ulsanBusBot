package net.daum.mf.map.common;

public class ResourceUtils {
    public static String getResourcePath(int resourceId) {
        if (resourceId == 0) {
            return null;
        }
        return String.format("res:%d", new Object[]{Integer.valueOf(resourceId)});
    }

    public static String getResourceAbsolutePath(String path) {
        if (path == null) {
            return null;
        }
        return String.format("absolutePath:%s", new Object[]{path});
    }
}
