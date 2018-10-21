package net.daum.mf.map.api;

import android.view.View;

public interface CalloutBalloonAdapter {
    View getCalloutBalloon(MapPOIItem mapPOIItem);

    View getPressedCalloutBalloon(MapPOIItem mapPOIItem);
}
