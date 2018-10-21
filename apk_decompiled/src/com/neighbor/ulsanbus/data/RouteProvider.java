package com.neighbor.ulsanbus.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.util.Log;

public class RouteProvider extends ContentProvider {
    private static final int CODE_ROUTE_ALL = 1;
    private static final int CODE_ROUTE_SEARCH = 2;
    public static final String CREATE_ULSAN_BUSROUTE = "create table busroute (_id integer primary key autoincrement, route_id text not null, route_no text,route_dir text,route_type text, route_bus_type text, route_bus_company text, route_company_tel text, route_fstop_name text,route_tstop_name text, route_wd_start_time text,route_wd_end_time text, route_wd_max_interval text, route_wd_min_interval text, route_we_start_time text, route_we_end_time text, route_we_max_interval text, route_we_min_interval text, route_ws_start_time text, route_ws_end_time text, route_ws_max_interval text, route_ws_min_interval text,route_interval text, route_travel_time text, route_length text, route_operation_cnt text, route_remark text,brt_timeflag text, brt_app_remark text, brt_app_waypoint_desc text ,brt_class_seqno text);";
    public static final String CREATE_ULSAN_ROUTE_SEARCHLIST = "create table routesearchlist (_id integer primary key autoincrement, route_id text not null unique ,millitime long);";
    private static final UriMatcher URI_MATCHER = new UriMatcher(-1);
    private final String Tag = "[UlsanBus : RouteProvider]";
    private SQLiteDatabase mDb;
    private RouteHelper mDbhelper;
    private SharedPreferences prefs;

    public static class RouteHelper extends SQLiteOpenHelper {
        public RouteHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(RouteProvider.CREATE_ULSAN_BUSROUTE);
            db.execSQL(RouteProvider.CREATE_ULSAN_ROUTE_SEARCHLIST);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS busroute");
            db.execSQL("DROP TABLE IF EXISTS routesearchlist");
            onCreate(db);
        }
    }

    static {
        URI_MATCHER.addURI(DataConst.autho_route, DataConst.TABLE_ROUTE_INFO, 1);
        URI_MATCHER.addURI(DataConst.autho_route, DataConst.TABLE_ROUTE_SEARCHLIST_INFO, 2);
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int value = 0;
        switch (URI_MATCHER.match(uri)) {
            case 1:
                value = this.mDb.delete(DataConst.TABLE_ROUTE_INFO, selection, selectionArgs);
                break;
            case 2:
                value = this.mDb.delete(DataConst.TABLE_ROUTE_SEARCHLIST_INFO, selection, selectionArgs);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return value;
    }

    public String getType(Uri uri) {
        return null;
    }

    public Uri insert(Uri uri, ContentValues values) {
        Log.d("[UlsanBus : RouteProvider]", "insert" + uri.toString());
        switch (URI_MATCHER.match(uri)) {
            case 2:
                this.mDb.insert(DataConst.TABLE_ROUTE_SEARCHLIST_INFO, null, values);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return null;
    }

    static String createInsert(String tableName, String[] columnNames) {
        if (tableName == null || columnNames == null || columnNames.length == 0) {
            throw new IllegalArgumentException();
        }
        StringBuilder s = new StringBuilder();
        s.append("INSERT INTO ").append(tableName).append(" (");
        for (String column : columnNames) {
            s.append(column).append(" ,");
        }
        int length = s.length();
        s.delete(length - 2, length);
        s.append(") VALUES( ");
        for (int i = 0; i < columnNames.length; i++) {
            s.append(" ? ,");
        }
        length = s.length();
        s.delete(length - 2, length);
        s.append(")");
        return s.toString();
    }

    public int bulkInsert(Uri uri, ContentValues[] values) {
        int i = 0;
        Log.d("[UlsanBus : RouteProvider]", "bulkInsert" + uri);
        switch (URI_MATCHER.match(uri)) {
            case 1:
                SQLiteStatement statement = this.mDb.compileStatement(createInsert(DataConst.TABLE_ROUTE_INFO, new String[]{"route_id", "route_no", DataConst.KEY_ROUTE_DIR, DataConst.KEY_ROUTE_TYPE, "route_bus_type", DataConst.KEY_ROUTE_BUS_COMPANY, DataConst.KEY_ROUTE_COMPANYTEL, DataConst.KEY_ROUTE_FSTOP_NAME, DataConst.KEY_ROUTE_TSTOP_NAME, DataConst.KEY_ROUTE_WD_START_TIME, DataConst.KEY_ROUTE_WD_END_TIME, DataConst.KEY_ROUTE_WD_MAX_INTERVAL, DataConst.KEY_ROUTE_WD_MIN_INTERVAL, DataConst.KEY_ROUTE_WE_START_TIME, DataConst.KEY_ROUTE_WE_END_TIME, DataConst.KEY_ROUTE_WE_MAX_INTERVAL, DataConst.KEY_ROUTE_WE_MIN_INTERVAL, DataConst.KEY_ROUTE_WS_START_TIME, DataConst.KEY_ROUTE_WS_END_TIME, DataConst.KEY_ROUTE_WS_MAX_INTERVAL, DataConst.KEY_ROUTE_WS_MIN_INTERVAL, "route_interval", DataConst.KEY_ROUTE_TRAVEL_TIME, DataConst.KEY_ROUTE_LENGTH, DataConst.KEY_ROUTE_OPERATION_CNT, DataConst.KEY_ROUTE_REMARK, DataConst.KEY_BRT_TIME_FLAG, DataConst.KEY_BRT_APP_REMARK, DataConst.KEY_BRT_APP_WAYPOINT_DESC, DataConst.KEY_BRT_CLASS_SEQNO}));
                this.mDb.beginTransaction();
                try {
                    int length = values.length;
                    while (i < length) {
                        ContentValues bean = values[i];
                        statement.clearBindings();
                        statement.bindString(1, bean.getAsString("route_id"));
                        statement.bindString(2, bean.getAsString("route_no"));
                        statement.bindString(3, bean.getAsString(DataConst.KEY_ROUTE_DIR));
                        statement.bindString(4, bean.getAsString(DataConst.KEY_ROUTE_TYPE));
                        statement.bindString(5, bean.getAsString("route_bus_type"));
                        statement.bindString(6, bean.getAsString(DataConst.KEY_ROUTE_BUS_COMPANY));
                        statement.bindString(7, bean.getAsString(DataConst.KEY_ROUTE_COMPANYTEL));
                        statement.bindString(8, bean.getAsString(DataConst.KEY_ROUTE_FSTOP_NAME));
                        statement.bindString(9, bean.getAsString(DataConst.KEY_ROUTE_TSTOP_NAME));
                        statement.bindString(10, bean.getAsString(DataConst.KEY_ROUTE_WD_START_TIME));
                        statement.bindString(11, bean.getAsString(DataConst.KEY_ROUTE_WD_END_TIME));
                        statement.bindString(12, bean.getAsString(DataConst.KEY_ROUTE_WD_MAX_INTERVAL));
                        statement.bindString(13, bean.getAsString(DataConst.KEY_ROUTE_WD_MIN_INTERVAL));
                        statement.bindString(14, bean.getAsString(DataConst.KEY_ROUTE_WE_START_TIME));
                        statement.bindString(15, bean.getAsString(DataConst.KEY_ROUTE_WE_END_TIME));
                        statement.bindString(16, bean.getAsString(DataConst.KEY_ROUTE_WE_MAX_INTERVAL));
                        statement.bindString(17, bean.getAsString(DataConst.KEY_ROUTE_WE_MIN_INTERVAL));
                        statement.bindString(18, bean.getAsString(DataConst.KEY_ROUTE_WS_START_TIME));
                        statement.bindString(19, bean.getAsString(DataConst.KEY_ROUTE_WS_END_TIME));
                        statement.bindString(20, bean.getAsString(DataConst.KEY_ROUTE_WS_MAX_INTERVAL));
                        statement.bindString(21, bean.getAsString(DataConst.KEY_ROUTE_WS_MIN_INTERVAL));
                        statement.bindString(22, bean.getAsString("route_interval"));
                        statement.bindString(23, bean.getAsString(DataConst.KEY_ROUTE_TRAVEL_TIME));
                        statement.bindString(24, bean.getAsString(DataConst.KEY_ROUTE_LENGTH));
                        statement.bindString(25, bean.getAsString(DataConst.KEY_ROUTE_OPERATION_CNT));
                        statement.bindString(26, bean.getAsString(DataConst.KEY_ROUTE_REMARK));
                        statement.bindString(27, bean.getAsString(DataConst.KEY_BRT_TIME_FLAG));
                        statement.bindString(28, bean.getAsString(DataConst.KEY_BRT_APP_REMARK));
                        statement.bindString(29, bean.getAsString(DataConst.KEY_BRT_APP_WAYPOINT_DESC));
                        statement.bindString(30, bean.getAsString(DataConst.KEY_BRT_CLASS_SEQNO));
                        statement.execute();
                        i++;
                    }
                    this.mDb.setTransactionSuccessful();
                    break;
                } finally {
                    this.mDb.endTransaction();
                    Log.d("[UlsanBus : RouteProvider]", "End insertion");
                    getContext().getContentResolver().notifyChange(uri, null);
                }
        }
        return values.length;
    }

    public boolean onCreate() {
        this.mDbhelper = new RouteHelper(getContext(), DataConst.DATABASE_ROUTE, null, DataConst.DATABASE_ROUTE_VER);
        try {
            this.mDb = this.mDbhelper.getWritableDatabase();
            this.prefs = getContext().getSharedPreferences("PreStatus", 0);
            if (this.prefs.getBoolean(DataConst.IS_COLUMN_ADDED, true)) {
                Cursor compare = this.mDb.rawQuery("PRAGMA table_info(busroute)", null);
                if (compare != null) {
                    int value1 = compare.getColumnIndex(DataConst.KEY_BRT_APP_REMARK);
                    int value2 = compare.getColumnIndex(DataConst.KEY_BRT_APP_WAYPOINT_DESC);
                    int value3 = compare.getColumnIndex(DataConst.KEY_BRT_CLASS_SEQNO);
                    Editor editor = this.prefs.edit();
                    if (value1 == -1 || value2 == -1 || value3 == -1) {
                        this.mDb.execSQL("DROP TABLE IF EXISTS busroute");
                        this.mDb.execSQL(CREATE_ULSAN_BUSROUTE);
                        editor.putBoolean(DataConst.IS_COLUMN_ADDED, true);
                    } else {
                        editor.putBoolean(DataConst.IS_COLUMN_ADDED, false);
                    }
                    editor.commit();
                }
            }
        } catch (SQLException e) {
            this.mDb = this.mDbhelper.getReadableDatabase();
        }
        return true;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqlbuilder = new SQLiteQueryBuilder();
        Log.d("[UlsanBus : RouteProvider]", uri.toString());
        switch (URI_MATCHER.match(uri)) {
            case 1:
                sqlbuilder.setTables(DataConst.TABLE_ROUTE_INFO);
                break;
            case 2:
                sqlbuilder.setTables(DataConst.TABLE_ROUTE_SEARCHLIST_INFO);
                break;
        }
        Cursor cursor = sqlbuilder.query(this.mDb, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int result = 0;
        switch (URI_MATCHER.match(uri)) {
            case 1:
                result = this.mDb.update(DataConst.TABLE_ROUTE_INFO, values, selection, selectionArgs);
                break;
            case 2:
                result = this.mDb.update(DataConst.TABLE_ROUTE_SEARCHLIST_INFO, values, selection, selectionArgs);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }
}
