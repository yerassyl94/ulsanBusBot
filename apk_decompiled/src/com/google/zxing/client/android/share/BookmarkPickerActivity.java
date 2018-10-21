package com.google.zxing.client.android.share;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.google.zxing.client.android.share.Browser.BookmarkColumns;

public final class BookmarkPickerActivity extends ListActivity {
    private static final String[] BOOKMARK_PROJECTION = new String[]{BookmarkColumns.TITLE, BookmarkColumns.URL};
    private static final String BOOKMARK_SELECTION = "bookmark = 1";
    static final int TITLE_COLUMN = 0;
    static final int URL_COLUMN = 1;
    private Cursor cursor = null;

    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.cursor = getContentResolver().query(Browser.BOOKMARKS_URI, BOOKMARK_PROJECTION, BOOKMARK_SELECTION, null, null);
        startManagingCursor(this.cursor);
        setListAdapter(new BookmarkAdapter(this, this.cursor));
    }

    protected void onListItemClick(ListView l, View view, int position, long id) {
        if (this.cursor.moveToPosition(position)) {
            Intent intent = new Intent();
            intent.addFlags(524288);
            intent.putExtra(BookmarkColumns.TITLE, this.cursor.getString(0));
            intent.putExtra(BookmarkColumns.URL, this.cursor.getString(1));
            setResult(-1, intent);
        } else {
            setResult(0);
        }
        finish();
    }
}
