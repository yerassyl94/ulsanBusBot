package net.daum.mf.map.p000n.api;

import net.daum.android.map.coord.MapCoord;

/* renamed from: net.daum.mf.map.n.api.NativeMapCoordConverter */
public class NativeMapCoordConverter {
    public native NativeConvertibleMapCoord convert(NativeConvertibleMapCoord nativeConvertibleMapCoord, int i);

    static {
        NativeMapLibraryLoader.loadLibrary();
    }

    public MapCoord convertMapCoord(MapCoord coord, int toType) {
        NativeConvertibleMapCoord newCoord = convert(new NativeConvertibleMapCoord(coord), toType);
        if (newCoord == null) {
            return null;
        }
        return newCoord.toMapCoord();
    }
}
