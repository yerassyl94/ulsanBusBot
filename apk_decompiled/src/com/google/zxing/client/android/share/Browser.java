package com.google.zxing.client.android.share;

import android.net.Uri;
import android.provider.BaseColumns;

public class Browser {
    public static final Uri BOOKMARKS_URI = Uri.parse("content://browser/bookmarks");

    public static class BookmarkColumns implements BaseColumns {
        public static final String BOOKMARK = "bookmark";
        public static final String CREATED = "created";
        public static final String DATE = "date";
        public static final String FAVICON = "favicon";
        public static final String THUMBNAIL = "thumbnail";
        public static final String TITLE = "title";
        public static final String TOUCH_ICON = "touch_icon";
        public static final String URL = "url";
        public static final String USER_ENTERED = "user_entered";
        public static final String VISITS = "visits";
    }
}
