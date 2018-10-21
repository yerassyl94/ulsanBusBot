package net.daum.android.map.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PersistentKeyValueStore {
    public static final String SETTING_KEY_LAST_HYBRID_TILE_VERSION = "daummap.lib.settings.tile.hybrid.lastVersion";
    public static final String SETTING_KEY_LAST_IMAGE_TILE_VERSION = "daummap.lib.settings.tile.image.lastVersion";
    public static final String SETTING_KEY_LAST_ROADVIEW_TILE_VERSION = "daummap.lib.settings.tile.roadview.lastVersion";
    private Context context;

    public PersistentKeyValueStore(Context context) {
        this.context = context;
    }

    private SharedPreferences getSharedPreferences() {
        return this.context.getSharedPreferences("Preference", 0);
    }

    public void put(String key, String value) {
        Editor editor = getSharedPreferences().edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void put(String key, boolean value) {
        Editor editor = getSharedPreferences().edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void put(String key, long value) {
        Editor editor = getSharedPreferences().edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public void put(String key, int value) {
        Editor editor = getSharedPreferences().edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void put(String key, float value) {
        Editor editor = getSharedPreferences().edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public String getString(String key) {
        return getString(key, null);
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public long getLong(String key) {
        return getLong(key, 0);
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public float getFloat(String key) {
        return getFloat(key, 0.0f);
    }

    public String getString(String key, String def) {
        return getSharedPreferences().getString(key, def);
    }

    public boolean getBoolean(String key, boolean def) {
        return getSharedPreferences().getBoolean(key, def);
    }

    public long getLong(String key, long def) {
        return getSharedPreferences().getLong(key, def);
    }

    public int getInt(String key, int def) {
        return getSharedPreferences().getInt(key, def);
    }

    public float getFloat(String key, float def) {
        return getSharedPreferences().getFloat(key, def);
    }

    public void setLastImageTileVersion(String ver) {
        put(SETTING_KEY_LAST_IMAGE_TILE_VERSION, ver);
    }

    public String getLastImageTileVersion() {
        return getString(SETTING_KEY_LAST_IMAGE_TILE_VERSION);
    }

    public void setLastHybridTileVersion(String ver) {
        put(SETTING_KEY_LAST_HYBRID_TILE_VERSION, ver);
    }

    public String getLastHybridTileVersion() {
        return getString(SETTING_KEY_LAST_HYBRID_TILE_VERSION);
    }

    public void setLastRoadViewTileVersion(String ver) {
        put(SETTING_KEY_LAST_ROADVIEW_TILE_VERSION, ver);
    }

    public String getLastRoadViewTileVersion() {
        return getString(SETTING_KEY_LAST_ROADVIEW_TILE_VERSION);
    }
}
