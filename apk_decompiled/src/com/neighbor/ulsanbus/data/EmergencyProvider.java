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

public class EmergencyProvider extends ContentProvider {
    private static final int CODE_EMERGENCY_ROUTE_ALL = 1;
    private static final String CREATE_ULSAN_EMERGENCY = "create table emergency (_id integer primary key autoincrement, route_id text, start_date text, end_date text);";
    private static final UriMatcher URI_MATCHER = new UriMatcher(-1);
    private final String Tag = "[UlsanBus : EmergencyProvider]";
    private SQLiteDatabase mDb;
    private RouteHelper mDbhelper;

    class RouteHelper extends SQLiteOpenHelper {
        public RouteHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(EmergencyProvider.CREATE_ULSAN_EMERGENCY);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTSemergency");
            onCreate(db);
        }
    }

    static {
        URI_MATCHER.addURI(DataConst.autho_emergency, DataConst.TABLE_EMERGENCY_INFO, 1);
    }

    public int delete(Uri arg0, String arg1, String[] arg2) {
        int value = 0;
        switch (URI_MATCHER.match(arg0)) {
            case 1:
                value = this.mDb.delete(DataConst.TABLE_EMERGENCY_INFO, arg1, arg2);
                break;
        }
        getContext().getContentResolver().notifyChange(arg0, null);
        return value;
    }

    public String getType(Uri uri) {
        return null;
    }

    public Uri insert(Uri uri, ContentValues values) {
        Log.d("[UlsanBus : EmergencyProvider]", "insert" + uri.toString());
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
        Log.d("[UlsanBus : EmergencyProvider]", "bulkInsert" + uri);
        switch (URI_MATCHER.match(uri)) {
            case 1:
                SQLiteStatement statement = this.mDb.compileStatement(createInsert(DataConst.TABLE_EMERGENCY_INFO, new String[]{"route_id", DataConst.KEY_EMER_START_DATE, DataConst.KEY_EMER_END_DATE}));
                this.mDb.beginTransaction();
                try {
                    int length = values.length;
                    while (i < length) {
                        ContentValues bean = values[i];
                        statement.clearBindings();
                        statement.bindString(1, bean.getAsString("route_id"));
                        statement.bindString(2, bean.getAsString(DataConst.KEY_EMER_START_DATE));
                        statement.bindString(3, bean.getAsString(DataConst.KEY_EMER_END_DATE));
                        statement.execute();
                        i++;
                    }
                    this.mDb.setTransactionSuccessful();
                    break;
                } finally {
                    this.mDb.endTransaction();
                    Log.d("[UlsanBus : EmergencyProvider]", "End insertion");
                    getContext().getContentResolver().notifyChange(uri, null);
                }
        }
        return values.length;
    }

    public boolean onCreate() {
        this.mDbhelper = new RouteHelper(getContext(), DataConst.DATABASE_EMERGENCY, null, DataConst.DATABASE_EMER_VER);
        try {
            this.mDb = this.mDbhelper.getWritableDatabase();
        } catch (SQLException e) {
            this.mDb = this.mDbhelper.getReadableDatabase();
        }
        return true;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqlbuilder = new SQLiteQueryBuilder();
        Log.d("[UlsanBus : EmergencyProvider]", uri.toString());
        switch (URI_MATCHER.match(uri)) {
            case 1:
                sqlbuilder.setTables(DataConst.TABLE_EMERGENCY_INFO);
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
                result = this.mDb.update(DataConst.TABLE_EMERGENCY_INFO, values, selection, selectionArgs);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }
}
