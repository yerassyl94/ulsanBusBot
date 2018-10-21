package com.google.zxing.client.android.history;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.client.android.C0224R;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.Intents.Scan;
import com.google.zxing.client.android.PreferencesActivity;
import com.google.zxing.client.android.result.ResultHandler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class HistoryManager {
    private static final String[] EXPORT_COL_PROJECTION = new String[]{"text", "display", "format", "timestamp"};
    private static final DateFormat EXPORT_DATE_TIME_FORMAT = DateFormat.getDateTimeInstance();
    private static final String[] GET_ITEM_COL_PROJECTION = new String[]{"text", "display", "format", "timestamp"};
    private static final String[] ID_COL_PROJECTION = new String[]{"id"};
    private static final int MAX_ITEMS = 500;
    private static final String TAG = HistoryManager.class.getSimpleName();
    private final CaptureActivity activity;

    public HistoryManager(CaptureActivity activity) {
        this.activity = activity;
    }

    public AlertDialog buildAlert() {
        Resources res;
        OnClickListener clickListener;
        Builder builder;
        SQLiteOpenHelper helper = new DBHelper(this.activity);
        List<Result> items = new ArrayList();
        List<String> dialogItems = new ArrayList();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = helper.getWritableDatabase();
            cursor = db.query("history", GET_ITEM_COL_PROJECTION, null, null, null, null, "timestamp DESC");
            while (cursor.moveToNext()) {
                Result result = new Result(cursor.getString(0), null, null, BarcodeFormat.valueOf(cursor.getString(2)), cursor.getLong(3));
                items.add(result);
                String display = cursor.getString(1);
                if (display == null || display.length() == 0) {
                    display = result.getText();
                }
                dialogItems.add(display);
            }
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        } catch (Throwable sqle) {
            Log.w(TAG, "Error while opening database", sqle);
            res = this.activity.getResources();
            dialogItems.add(res.getString(C0224R.string.history_send));
            dialogItems.add(res.getString(C0224R.string.history_clear_text));
            clickListener = new HistoryClickListener(this, this.activity, items);
            builder = new Builder(this.activity);
            builder.setTitle(C0224R.string.history_title);
            builder.setItems((CharSequence[]) dialogItems.toArray(new String[dialogItems.size()]), clickListener);
            return builder.create();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        res = this.activity.getResources();
        dialogItems.add(res.getString(C0224R.string.history_send));
        dialogItems.add(res.getString(C0224R.string.history_clear_text));
        clickListener = new HistoryClickListener(this, this.activity, items);
        builder = new Builder(this.activity);
        builder.setTitle(C0224R.string.history_title);
        builder.setItems((CharSequence[]) dialogItems.toArray(new String[dialogItems.size()]), clickListener);
        return builder.create();
    }

    public void addHistoryItem(Result result, ResultHandler handler) {
        if (this.activity.getIntent().getBooleanExtra(Scan.SAVE_HISTORY, true) && !handler.areContentsSecure()) {
            if (!PreferenceManager.getDefaultSharedPreferences(this.activity).getBoolean(PreferencesActivity.KEY_REMEMBER_DUPLICATES, false)) {
                deletePrevious(result.getText());
            }
            try {
                SQLiteDatabase db = new DBHelper(this.activity).getWritableDatabase();
                try {
                    ContentValues values = new ContentValues();
                    values.put("text", result.getText());
                    values.put("format", result.getBarcodeFormat().toString());
                    values.put("display", handler.getDisplayContents().toString());
                    values.put("timestamp", Long.valueOf(System.currentTimeMillis()));
                    db.insert("history", "timestamp", values);
                } finally {
                    db.close();
                }
            } catch (SQLiteException sqle) {
                Log.w(TAG, "Error while opening database", sqle);
            }
        }
    }

    private void deletePrevious(String text) {
        try {
            SQLiteDatabase db = new DBHelper(this.activity).getWritableDatabase();
            try {
                db.delete("history", "text=?", new String[]{text});
            } finally {
                db.close();
            }
        } catch (SQLiteException sqle) {
            Log.w(TAG, "Error while opening database", sqle);
        }
    }

    public void trimHistory() {
        try {
            SQLiteDatabase db = new DBHelper(this.activity).getWritableDatabase();
            Cursor cursor = null;
            try {
                cursor = db.query("history", ID_COL_PROJECTION, null, null, null, null, "timestamp DESC");
                for (int count = 0; count < MAX_ITEMS && cursor.moveToNext(); count++) {
                }
                while (cursor.moveToNext()) {
                    db.delete("history", "id=" + cursor.getString(0), null);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                db.close();
            }
        } catch (SQLiteException sqle) {
            Log.w(TAG, "Error while opening database", sqle);
        }
    }

    CharSequence buildHistory() {
        StringBuilder historyText = new StringBuilder(1000);
        try {
            SQLiteDatabase db = new DBHelper(this.activity).getWritableDatabase();
            Cursor cursor = null;
            try {
                cursor = db.query("history", EXPORT_COL_PROJECTION, null, null, null, null, "timestamp DESC");
                while (cursor.moveToNext()) {
                    for (int col = 0; col < EXPORT_COL_PROJECTION.length; col++) {
                        historyText.append('\"').append(massageHistoryField(cursor.getString(col))).append("\",");
                    }
                    historyText.append('\"').append(massageHistoryField(EXPORT_DATE_TIME_FORMAT.format(new Date(cursor.getLong(EXPORT_COL_PROJECTION.length - 1))))).append("\"\r\n");
                }
                return historyText;
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                db.close();
            }
        } catch (SQLiteException sqle) {
            Log.w(TAG, "Error while opening database", sqle);
            return "";
        }
    }

    static Uri saveHistory(String history) {
        IOException ioe;
        Throwable th;
        Uri uri = null;
        File historyRoot = new File(new File(Environment.getExternalStorageDirectory(), "BarcodeScanner"), "History");
        if (historyRoot.exists() || historyRoot.mkdirs()) {
            File historyFile = new File(historyRoot, "history-" + System.currentTimeMillis() + ".csv");
            OutputStreamWriter out = null;
            try {
                OutputStreamWriter out2 = new OutputStreamWriter(new FileOutputStream(historyFile), Charset.forName("UTF-8"));
                try {
                    out2.write(history);
                    uri = Uri.parse("file://" + historyFile.getAbsolutePath());
                    if (out2 != null) {
                        try {
                            out2.close();
                        } catch (IOException e) {
                        }
                    }
                } catch (IOException e2) {
                    ioe = e2;
                    out = out2;
                    try {
                        Log.w(TAG, "Couldn't access file " + historyFile + " due to " + ioe);
                        if (out != null) {
                            try {
                                out.close();
                            } catch (IOException e3) {
                            }
                        }
                        return uri;
                    } catch (Throwable th2) {
                        th = th2;
                        if (out != null) {
                            try {
                                out.close();
                            } catch (IOException e4) {
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    out = out2;
                    if (out != null) {
                        out.close();
                    }
                    throw th;
                }
            } catch (IOException e5) {
                ioe = e5;
                Log.w(TAG, "Couldn't access file " + historyFile + " due to " + ioe);
                if (out != null) {
                    out.close();
                }
                return uri;
            }
        }
        Log.w(TAG, "Couldn't make dir " + historyRoot);
        return uri;
    }

    private static String massageHistoryField(String value) {
        return value.replace("\"", "\"\"");
    }

    void clearHistory() {
        try {
            SQLiteDatabase db = new DBHelper(this.activity).getWritableDatabase();
            try {
                db.delete("history", null, null);
            } finally {
                db.close();
            }
        } catch (SQLiteException sqle) {
            Log.w(TAG, "Error while opening database", sqle);
        }
    }
}
