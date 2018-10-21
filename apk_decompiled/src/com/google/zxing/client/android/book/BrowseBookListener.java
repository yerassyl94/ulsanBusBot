package com.google.zxing.client.android.book;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.google.zxing.client.android.LocaleManager;
import java.util.List;

final class BrowseBookListener implements OnItemClickListener {
    private final SearchBookContentsActivity activity;
    private final List<SearchBookContentsResult> items;

    BrowseBookListener(SearchBookContentsActivity activity, List<SearchBookContentsResult> items) {
        this.activity = activity;
        this.items = items;
    }

    public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
        String pageId = ((SearchBookContentsResult) this.items.get(position - 1)).getPageId();
        String query = SearchBookContentsResult.getQuery();
        if (this.activity.getISBN().startsWith("http://google.com/books?id=") && pageId.length() > 0) {
            String uri = this.activity.getISBN();
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("http://books.google." + LocaleManager.getBookSearchCountryTLD() + "/books?id=" + uri.substring(uri.indexOf(61) + 1) + "&pg=" + pageId + "&vq=" + query));
            intent.addFlags(524288);
            this.activity.startActivity(intent);
        }
    }
}
