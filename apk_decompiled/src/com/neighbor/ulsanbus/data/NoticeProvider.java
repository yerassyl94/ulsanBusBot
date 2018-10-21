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

public class NoticeProvider extends ContentProvider {
    private static final int CODE_NOTICE = 1;
    private static final String CREATE_ULSAN_NOTICE = "create table notice (_id integer primary key autoincrement, route_notice_cnt text, route_notice_date text  not null, route_notice_title text, route_notice_content text , route_notice_file text);";
    private static final UriMatcher MATCHER = new UriMatcher(-1);
    private final String Tag = "[UlsanBus]";
    private SQLiteDatabase mDb;
    private NoticeHelper mDbhelper;

    class NoticeHelper extends SQLiteOpenHelper {
        public NoticeHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(NoticeProvider.CREATE_ULSAN_NOTICE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTSnotice");
            onCreate(db);
        }
    }

    static {
        MATCHER.addURI(DataConst.autho_notice, DataConst.TABLE_NOTICE_INFO, 1);
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
        Log.d("[UlsanBus]", "bulkInsert" + uri.toString());
        switch (MATCHER.match(uri)) {
            case 1:
                SQLiteStatement statement = this.mDb.compileStatement(createInsert(DataConst.TABLE_NOTICE_INFO, new String[]{DataConst.KEY_NOTICE_CNT, DataConst.KEY_NOTICE_DATE, DataConst.KEY_NOTICE_TITLE, DataConst.KEY_NOTICE_CONTENT, DataConst.KEY_NOTICE_FILE}));
                this.mDb.beginTransaction();
                try {
                    int length = values.length;
                    while (i < length) {
                        ContentValues bean = values[i];
                        statement.clearBindings();
                        statement.bindString(1, bean.getAsString(DataConst.KEY_NOTICE_CNT));
                        statement.bindString(2, bean.getAsString(DataConst.KEY_NOTICE_DATE));
                        statement.bindString(3, bean.getAsString(DataConst.KEY_NOTICE_TITLE));
                        statement.bindString(4, bean.getAsString(DataConst.KEY_NOTICE_CONTENT));
                        statement.bindString(5, bean.getAsString(DataConst.KEY_NOTICE_FILE));
                        statement.execute();
                        i++;
                    }
                    this.mDb.setTransactionSuccessful();
                    break;
                } finally {
                    this.mDb.endTransaction();
                    Log.d("[UlsanBus]", "End insertion");
                    getContext().getContentResolver().notifyChange(uri, null);
                }
        }
        return values.length;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int value = 0;
        switch (MATCHER.match(uri)) {
            case 1:
                value = this.mDb.delete(DataConst.TABLE_NOTICE_INFO, selection, selectionArgs);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return value;
    }

    public String getType(Uri uri) {
        return null;
    }

    public Uri insert(Uri uri, ContentValues values) {
        Log.d("[UlsanBus]", "insert" + uri.toString());
        switch (MATCHER.match(uri)) {
            case 1:
                this.mDb.insert(DataConst.TABLE_NOTICE_INFO, null, values);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return null;
    }

    public boolean onCreate() {
        this.mDbhelper = new NoticeHelper(getContext(), DataConst.DATABASE_NOTICE, null, DataConst.DATABASE_NOTICE_VER);
        try {
            this.mDb = this.mDbhelper.getWritableDatabase();
        } catch (SQLException e) {
            this.mDb = this.mDbhelper.getReadableDatabase();
        }
        return false;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqlbuilder = new SQLiteQueryBuilder();
        Log.d("[UlsanBus]", uri.toString());
        switch (MATCHER.match(uri)) {
            case 1:
                sqlbuilder.setTables(DataConst.TABLE_NOTICE_INFO);
                break;
        }
        Cursor cursor = sqlbuilder.query(this.mDb, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int result = 0;
        switch (MATCHER.match(uri)) {
            case 1:
                result = this.mDb.update(DataConst.TABLE_NOTICE_INFO, values, selection, selectionArgs);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }
}
