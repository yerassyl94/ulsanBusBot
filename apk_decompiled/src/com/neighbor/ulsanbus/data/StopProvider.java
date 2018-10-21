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

public class StopProvider extends ContentProvider {
    private static final int CODE_BUS_STOP_ALL = 1;
    private static final int CODE_BUS_STOP_SEARCHLIST = 2;
    private static final String CREATE_ULSAN_BUSSTOP = "create table bisnode (_id integer primary key autoincrement, stop_id text not null, stop_name text, stop_limousine text,stop_x text, stop_y text, stop_remark text);";
    private static final String CREATE_ULSAN_STOP_SEARCHLIST = "create table stopsearchlist (_id integer primary key autoincrement, search_stop_id text,millitime long);";
    private static final UriMatcher URI_MATCHER = new UriMatcher(-1);
    private final String Tag = "[UlsanBus : StopProvider]";
    private SQLiteDatabase mDb;
    private StopHelper mDbhelper;

    class StopHelper extends SQLiteOpenHelper {
        public StopHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(StopProvider.CREATE_ULSAN_BUSSTOP);
            db.execSQL(StopProvider.CREATE_ULSAN_STOP_SEARCHLIST);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTSbisnode");
            db.execSQL("DROP TABLE IF EXISTSstopsearchlist");
            onCreate(db);
        }
    }

    static {
        URI_MATCHER.addURI(DataConst.autho_stop, DataConst.TABLE_STOP_INFO, 1);
        URI_MATCHER.addURI(DataConst.autho_stop, DataConst.TABLE_STOP_SEARCHLIST_INFO, 2);
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
        Log.d("[UlsanBus : StopProvider]", "bulkInsert" + uri);
        switch (URI_MATCHER.match(uri)) {
            case 1:
                SQLiteStatement statement = this.mDb.compileStatement(createInsert(DataConst.TABLE_STOP_INFO, new String[]{"stop_id", "stop_name", DataConst.KEY_STOP_LIMOUSINE, DataConst.KEY_STOP_X, DataConst.KEY_STOP_Y, "stop_remark"}));
                this.mDb.beginTransaction();
                try {
                    int length = values.length;
                    while (i < length) {
                        ContentValues bean = values[i];
                        statement.clearBindings();
                        statement.bindString(1, bean.getAsString("stop_id"));
                        statement.bindString(2, bean.getAsString("stop_name"));
                        statement.bindString(3, bean.getAsString(DataConst.KEY_STOP_LIMOUSINE));
                        statement.bindString(4, bean.getAsString(DataConst.KEY_STOP_X));
                        statement.bindString(5, bean.getAsString(DataConst.KEY_STOP_Y));
                        statement.bindString(6, bean.getAsString("stop_remark"));
                        statement.execute();
                        i++;
                    }
                    this.mDb.setTransactionSuccessful();
                    break;
                } finally {
                    this.mDb.endTransaction();
                    Log.d("[UlsanBus : StopProvider]", "End insertion");
                    getContext().getContentResolver().notifyChange(uri, null);
                }
        }
        return values.length;
    }

    public int delete(Uri arg0, String arg1, String[] arg2) {
        int value = 0;
        Log.d("[UlsanBus : StopProvider]", "delete");
        switch (URI_MATCHER.match(arg0)) {
            case 1:
                value = this.mDb.delete(DataConst.TABLE_STOP_INFO, arg1, arg2);
                break;
            case 2:
                value = this.mDb.delete(DataConst.TABLE_STOP_SEARCHLIST_INFO, arg1, arg2);
                break;
        }
        getContext().getContentResolver().notifyChange(arg0, null);
        return value;
    }

    public String getType(Uri uri) {
        return null;
    }

    public Uri insert(Uri uri, ContentValues values) {
        Log.d("[UlsanBus : StopProvider]", "insert" + uri.toString());
        switch (URI_MATCHER.match(uri)) {
            case 2:
                this.mDb.insert(DataConst.TABLE_STOP_SEARCHLIST_INFO, null, values);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return null;
    }

    public boolean onCreate() {
        this.mDbhelper = new StopHelper(getContext(), DataConst.DATABASE_STOP, null, DataConst.DATABASE_STOP_VER);
        try {
            this.mDb = this.mDbhelper.getWritableDatabase();
        } catch (SQLException e) {
            this.mDb = this.mDbhelper.getReadableDatabase();
        }
        return true;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqlbuilder = new SQLiteQueryBuilder();
        Log.d("[UlsanBus : StopProvider]", uri.toString());
        switch (URI_MATCHER.match(uri)) {
            case 1:
                sqlbuilder.setTables(DataConst.TABLE_STOP_INFO);
                break;
            case 2:
                sqlbuilder.setTables(DataConst.TABLE_STOP_SEARCHLIST_INFO);
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
                result = this.mDb.update(DataConst.TABLE_STOP_INFO, values, selection, selectionArgs);
                break;
            case 2:
                result = this.mDb.update(DataConst.TABLE_STOP_SEARCHLIST_INFO, values, selection, selectionArgs);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }
}
