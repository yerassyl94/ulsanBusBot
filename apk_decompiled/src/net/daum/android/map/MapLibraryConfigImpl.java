package net.daum.android.map;

import java.util.ArrayList;
import java.util.List;

public class MapLibraryConfigImpl implements MapLibraryNativeConfig {
    private static final ArrayList<String> libraryNames = new C03541();

    /* renamed from: net.daum.android.map.MapLibraryConfigImpl$1 */
    static class C03541 extends ArrayList<String> {
        private static final long serialVersionUID = -8458824031345810218L;

        C03541() {
            add("DaumMapEngineApi");
        }
    }

    public List<String> getLibraryNames() {
        return libraryNames;
    }
}
