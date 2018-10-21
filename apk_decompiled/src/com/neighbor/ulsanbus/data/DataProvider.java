package com.neighbor.ulsanbus.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
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

public class DataProvider extends ContentProvider {
    private static final int CODE_ALARM = 2;
    private static final int CODE_EMERGENCY = 3;
    private static final int CODE_FAVORITES = 4;
    private static final int CODE_VERSION = 1;
    private static final int CODE_WIDGET1 = 5;
    private static final int CODE_WIDGET2 = 6;
    private static final int CODE_WIDGET3 = 7;
    private static final int CODE_WIDGET3_LIST = 8;
    private static final String CREATE_ULSAN_ALARM = "create table alarm ( _id integer primary key autoincrement, alarm_id text ,favor_routeid text, favor_stopid text, alarm_time_hour text,alarm_time_minu text, day_0 text,day_1 text,day_2 text,day_3 text,day_4 text,day_5 text, day_6 text); ";
    private static final String CREATE_ULSAN_DATABASE_VERSION = "create table databaseversion (_id integer primary key autoincrement, version_route text, version_stop text, version_notice text, version_time text , version_emergency text, emergency_mode text, version_tcardstore text ,version_notice_interal integer , version_tcardstore_internal integer);";
    private static final String CREATE_ULSAN_EMERGENCY = "create table emergency (_id integer primary key autoincrement, route_id text, start_date text, end_date text);";
    private static final String CREATE_ULSAN_FAVORITE = "create table favorite (_id integer primary key autoincrement, category text, favor_routeid text, favor_stopid text, favor_alarm_status text);";
    private static final String CREATE_ULSAN_WIDGET1 = " create table widget1 ( _id integer primary key autoincrement , widget_id integer, route_id text ,route_no text , stop_id text, stop_name text , remain_time text , route_bus_type text );";
    private static final String CREATE_ULSAN_WIDGET2 = " create table widget2 ( _id integer primary key autoincrement , widget_id integer, route_id text ,route_no text , stop_id text, stop_name text , stop_remark text , remain_time1 text , route_bus_type text, remain_time2 text );";
    private static final String CREATE_ULSAN_WIDGET3 = "create table widget3( _id integer primary key autoincrement, widget_id integer, stop_id text, stop_name text , stop_remark text );";
    private static final String CREATE_ULSAN_WIDGET3_LIST = "create table widget3_list ( _id integer primary key autoincrement , route_id text , stop_name text, stop_id text, stop_remark text, widget_id integer , remain_time text, status text );";
    private static final UriMatcher MATCHER = new UriMatcher(-1);
    private final String Tag = "[UlsanBus]";
    private SQLiteDatabase mDb;
    private DabaseHelper mDbhelper;

    class DabaseHelper extends SQLiteOpenHelper {
        public synchronized void close() {
            super.close();
        }

        public DabaseHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, DataConst.DATABASE_NAME, null, version);
        }

        public void onCreate(SQLiteDatabase database) {
            database.execSQL(DataProvider.CREATE_ULSAN_FAVORITE);
            database.execSQL(DataProvider.CREATE_ULSAN_DATABASE_VERSION);
            database.execSQL(DataProvider.CREATE_ULSAN_EMERGENCY);
            database.execSQL(DataProvider.CREATE_ULSAN_WIDGET1);
            database.execSQL(DataProvider.CREATE_ULSAN_WIDGET2);
            database.execSQL(DataProvider.CREATE_ULSAN_WIDGET3);
            database.execSQL(DataProvider.CREATE_ULSAN_WIDGET3_LIST);
            database.execSQL(DataProvider.CREATE_ULSAN_ALARM);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion < newVersion && newVersion == 21) {
                db.execSQL("DROP TABLE IF EXISTS alarm");
            }
            db.execSQL(DataProvider.CREATE_ULSAN_ALARM);
        }
    }

    static {
        MATCHER.addURI("com.neighbor.ulsanbus", DataConst.TABLE_DATABASE_VERSION_INFO, 1);
        MATCHER.addURI("com.neighbor.ulsanbus", "alarm", 2);
        MATCHER.addURI("com.neighbor.ulsanbus", DataConst.TABLE_EMERGENCY_INFO, 3);
        MATCHER.addURI("com.neighbor.ulsanbus", DataConst.TABLE_FAVORITE_INFO, 4);
        MATCHER.addURI("com.neighbor.ulsanbus", DataConst.TABLE_WIDGET1, 5);
        MATCHER.addURI("com.neighbor.ulsanbus", DataConst.TABLE_WIDGET2, 6);
        MATCHER.addURI("com.neighbor.ulsanbus", DataConst.TABLE_WIDGET3, 7);
        MATCHER.addURI("com.neighbor.ulsanbus", DataConst.TABLE_WIDGET3_LIST, 8);
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
        Log.d("[UlsanBus]", "bulkInsert" + uri);
        switch (MATCHER.match(uri)) {
            case 8:
                SQLiteStatement statement = this.mDb.compileStatement(createInsert(DataConst.TABLE_WIDGET3_LIST, new String[]{"widget_id", "stop_id", "stop_name", "stop_remark", "remain_time", "route_id", "status"}));
                this.mDb.beginTransaction();
                try {
                    int length = values.length;
                    while (i < length) {
                        ContentValues bean = values[i];
                        statement.clearBindings();
                        statement.bindString(1, bean.getAsString("widget_id"));
                        statement.bindString(2, bean.getAsString("stop_id"));
                        statement.bindString(3, bean.getAsString("stop_name"));
                        statement.bindString(4, bean.getAsString("stop_remark"));
                        statement.bindString(5, bean.getAsString("remain_time"));
                        statement.bindString(6, bean.getAsString("route_id"));
                        statement.bindString(7, bean.getAsString("status"));
                        statement.execute();
                        i++;
                    }
                    this.mDb.setTransactionSuccessful();
                    break;
                } finally {
                    this.mDb.endTransaction();
                    getContext().getContentResolver().notifyChange(uri, null);
                    Log.d("[UlsanBus]", "End insertion");
                }
        }
        return values.length;
    }

    public int delete(Uri arg0, String arg1, String[] arg2) {
        switch (MATCHER.match(arg0)) {
            case 1:
                this.mDb.delete(DataConst.TABLE_DATABASE_VERSION_INFO, arg1, arg2);
                break;
            case 2:
                this.mDb.delete("alarm", arg1, arg2);
                break;
            case 3:
                this.mDb.delete(DataConst.TABLE_EMERGENCY_INFO, arg1, arg2);
                break;
            case 4:
                this.mDb.delete(DataConst.TABLE_FAVORITE_INFO, arg1, arg2);
                break;
            case 5:
                this.mDb.delete(DataConst.TABLE_WIDGET1, arg1, arg2);
                break;
            case 6:
                this.mDb.delete(DataConst.TABLE_WIDGET2, arg1, arg2);
                break;
            case 7:
                this.mDb.delete(DataConst.TABLE_WIDGET3, arg1, arg2);
                break;
            case 8:
                this.mDb.delete(DataConst.TABLE_WIDGET3_LIST, arg1, arg2);
                break;
        }
        getContext().getContentResolver().notifyChange(arg0, null);
        return 0;
    }

    public String getType(Uri arg0) {
        return null;
    }

    public Uri insert(Uri arg0, ContentValues arg1) {
        Log.d("[UlsanBus]", "insert" + arg0.toString());
        switch (MATCHER.match(arg0)) {
            case 1:
                this.mDb.insert(DataConst.TABLE_DATABASE_VERSION_INFO, null, arg1);
                break;
            case 2:
                this.mDb.insert("alarm", null, arg1);
                break;
            case 3:
                this.mDb.insert(DataConst.TABLE_EMERGENCY_INFO, null, arg1);
                break;
            case 4:
                this.mDb.insert(DataConst.TABLE_FAVORITE_INFO, null, arg1);
                break;
            case 5:
                this.mDb.insert(DataConst.TABLE_WIDGET1, null, arg1);
                break;
            case 6:
                this.mDb.insert(DataConst.TABLE_WIDGET2, null, arg1);
                break;
            case 7:
                this.mDb.insert(DataConst.TABLE_WIDGET3, null, arg1);
                break;
            case 8:
                this.mDb.insert(DataConst.TABLE_WIDGET3_LIST, null, arg1);
                break;
        }
        getContext().getContentResolver().notifyChange(arg0, null);
        return arg0;
    }

    public boolean onCreate() {
        Log.d("[UlsanBus]", "onCreate");
        this.mDbhelper = new DabaseHelper(getContext(), DataConst.DATABASE_NAME, null, 21);
        open();
        return true;
    }

    public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3, String arg4) {
        SQLiteQueryBuilder sqlbuilder = new SQLiteQueryBuilder();
        Log.d("[UlsanBus]", arg0.toString());
        switch (MATCHER.match(arg0)) {
            case 1:
                sqlbuilder.setTables(DataConst.TABLE_DATABASE_VERSION_INFO);
                break;
            case 2:
                sqlbuilder.setTables("alarm");
                break;
            case 3:
                sqlbuilder.setTables(DataConst.TABLE_EMERGENCY_INFO);
                break;
            case 4:
                sqlbuilder.setTables(DataConst.TABLE_FAVORITE_INFO);
                break;
            case 5:
                sqlbuilder.setTables(DataConst.TABLE_WIDGET1);
                break;
            case 6:
                sqlbuilder.setTables(DataConst.TABLE_WIDGET2);
                break;
            case 7:
                sqlbuilder.setTables(DataConst.TABLE_WIDGET3);
                break;
            case 8:
                sqlbuilder.setTables(DataConst.TABLE_WIDGET3_LIST);
                break;
        }
        Cursor cursor = sqlbuilder.query(this.mDb, arg1, arg2, arg3, null, null, arg4);
        cursor.setNotificationUri(getContext().getContentResolver(), arg0);
        return cursor;
    }

    public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
        int retValue = 0;
        switch (MATCHER.match(arg0)) {
            case 1:
                retValue = this.mDb.update(DataConst.TABLE_DATABASE_VERSION_INFO, arg1, arg2, arg3);
                break;
            case 2:
                retValue = this.mDb.update("alarm", arg1, arg2, arg3);
                break;
            case 5:
                retValue = this.mDb.update(DataConst.TABLE_WIDGET1, arg1, arg2, arg3);
                break;
            case 6:
                retValue = this.mDb.update(DataConst.TABLE_WIDGET2, arg1, arg2, arg3);
                break;
            case 7:
                retValue = this.mDb.update(DataConst.TABLE_WIDGET3, arg1, arg2, arg3);
                break;
            case 8:
                retValue = this.mDb.update(DataConst.TABLE_WIDGET3_LIST, arg1, arg2, arg3);
                break;
        }
        getContext().getContentResolver().notifyChange(arg0, null);
        return retValue;
    }

    public void open() throws SQLException {
        try {
            this.mDb = this.mDbhelper.getWritableDatabase();
        } catch (SQLException e) {
            this.mDb = this.mDbhelper.getReadableDatabase();
        }
    }
}
