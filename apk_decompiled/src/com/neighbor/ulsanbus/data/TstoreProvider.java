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

public class TstoreProvider extends ContentProvider {
    private static final int CODE_TSTORE = 1;
    private static final String CREATE_ULSAN_TCARDSTORE = "create table tcardstore (_id integer primary key autoincrement, tcardstore_name text, tcardstore_address text,tcardstore_cardX text, tcardstore_cardY text, tcardstore_storetype text);";
    private static final UriMatcher URI_MATCHER = new UriMatcher(-1);
    public static SQLiteDatabase mDb;
    private final String Tag = "[UlsanBus : TstoreProvider]";
    private TstoreHelper mDbhelper;

    class TstoreHelper extends SQLiteOpenHelper {
        public TstoreHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TstoreProvider.CREATE_ULSAN_TCARDSTORE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTStcardstore");
            onCreate(db);
        }
    }

    static {
        URI_MATCHER.addURI(DataConst.autho_tstore, DataConst.TABLE_TCARDSTORE_INFO, 1);
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
        Log.d("[UlsanBus : TstoreProvider]", "bulkInsert" + uri);
        switch (URI_MATCHER.match(uri)) {
            case 1:
                SQLiteStatement statement = mDb.compileStatement(createInsert(DataConst.TABLE_TCARDSTORE_INFO, new String[]{DataConst.KEY_TCARDSTORE_NAME, DataConst.KEY_TCARDSTORE_ADDRESS, DataConst.KEY_TCARDSTORE_CARDX, DataConst.KEY_TCARDSTORE_CARDY, DataConst.KEY_TCARDSTORE_STORETYPE}));
                mDb.beginTransaction();
                try {
                    int length = values.length;
                    while (i < length) {
                        ContentValues bean = values[i];
                        statement.clearBindings();
                        statement.bindString(1, bean.getAsString(DataConst.KEY_TCARDSTORE_NAME));
                        statement.bindString(2, bean.getAsString(DataConst.KEY_TCARDSTORE_ADDRESS));
                        statement.bindString(3, bean.getAsString(DataConst.KEY_TCARDSTORE_CARDX));
                        statement.bindString(4, bean.getAsString(DataConst.KEY_TCARDSTORE_CARDY));
                        statement.bindString(5, bean.getAsString(DataConst.KEY_TCARDSTORE_STORETYPE));
                        statement.execute();
                        i++;
                    }
                    mDb.setTransactionSuccessful();
                    break;
                } finally {
                    mDb.endTransaction();
                    Log.d("[UlsanBus : TstoreProvider]", "End insertion");
                    getContext().getContentResolver().notifyChange(uri, null);
                }
        }
        return values.length;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int value = 0;
        switch (URI_MATCHER.match(uri)) {
            case 1:
                value = mDb.delete(DataConst.TABLE_TCARDSTORE_INFO, selection, selectionArgs);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return value;
    }

    public String getType(Uri uri) {
        return null;
    }

    public Uri insert(Uri uri, ContentValues values) {
        Log.d("[UlsanBus : TstoreProvider]", "insert" + uri.toString());
        switch (URI_MATCHER.match(uri)) {
            case 1:
                mDb.insert(DataConst.TABLE_STOP_INFO, null, values);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return null;
    }

    public boolean onCreate() {
        this.mDbhelper = new TstoreHelper(getContext(), DataConst.DATABASE_TSTORE, null, DataConst.DATABASE_STORE_VER);
        try {
            mDb = this.mDbhelper.getWritableDatabase();
        } catch (SQLException e) {
            mDb = this.mDbhelper.getReadableDatabase();
        }
        return true;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqlbuilder = new SQLiteQueryBuilder();
        Log.d("[UlsanBus : TstoreProvider]", uri.toString());
        switch (URI_MATCHER.match(uri)) {
            case 1:
                sqlbuilder.setTables(DataConst.TABLE_TCARDSTORE_INFO);
                break;
        }
        Cursor cursor = sqlbuilder.query(mDb, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int result = 0;
        switch (URI_MATCHER.match(uri)) {
            case 1:
                result = mDb.update(DataConst.TABLE_TCARDSTORE_INFO, values, selection, selectionArgs);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }
}
