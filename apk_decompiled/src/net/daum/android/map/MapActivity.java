package net.daum.android.map;

import android.app.Activity;
import android.os.Bundle;

public class MapActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapEngineManager.getInstance().onCreateMapActivity(this, new MapView(this));
    }

    protected void onStart() {
        super.onStart();
        MapEngineManager.getInstance().onStartMapActivity();
    }

    protected void onRestart() {
        super.onRestart();
        MapEngineManager.getInstance().onRestartMapActivity();
    }

    protected void onResume() {
        super.onResume();
        MapEngineManager.getInstance().onResumeMapActivity();
    }

    protected void onPause() {
        super.onPause();
        MapEngineManager.getInstance().onPauseMapActivity();
    }

    protected void onStop() {
        super.onStop();
        MapEngineManager.getInstance().onStopMapActivity();
    }

    protected void onDestroy() {
        super.onDestroy();
        MapEngineManager.getInstance().onDestroyMapActivity();
    }

    protected MapView getMapView() {
        return MapEngineManager.getInstance().getMapView();
    }
}
