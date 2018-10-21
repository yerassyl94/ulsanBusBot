package com.google.zxing.client.android.share;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.zxing.client.android.C0224R;

final class BookmarkAdapter extends BaseAdapter {
    private final Context context;
    private final Cursor cursor;

    public BookmarkAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    public int getCount() {
        return this.cursor.getCount();
    }

    public Object getItem(int index) {
        return null;
    }

    public long getItemId(int index) {
        return (long) index;
    }

    public View getView(int index, View view, ViewGroup viewGroup) {
        LinearLayout layout;
        if (view == null || !(view instanceof LinearLayout)) {
            layout = (LinearLayout) LayoutInflater.from(this.context).inflate(C0224R.layout.bookmark_picker_list_item, viewGroup, false);
        } else {
            layout = (LinearLayout) view;
        }
        this.cursor.moveToPosition(index);
        ((TextView) layout.findViewById(C0224R.id.bookmark_title)).setText(this.cursor.getString(0));
        ((TextView) layout.findViewById(C0224R.id.bookmark_url)).setText(this.cursor.getString(1));
        return layout;
    }
}
