package com.google.zxing.client.android.share;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.google.zxing.client.android.share.Browser.BookmarkColumns;
import java.util.ArrayList;
import java.util.List;

public final class AppPickerActivity extends ListActivity {
    private final List<String[]> labelsPackages = new ArrayList();

    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        if (this.labelsPackages.isEmpty()) {
            new LoadPackagesAsyncTask(this).execute(new List[]{this.labelsPackages});
        }
    }

    protected void onListItemClick(ListView l, View view, int position, long id) {
        if (position < 0 || position >= this.labelsPackages.size()) {
            setResult(0);
        } else {
            String url = "market://search?q=pname:" + ((String[]) this.labelsPackages.get(position))[1];
            Intent intent = new Intent();
            intent.addFlags(524288);
            intent.putExtra(BookmarkColumns.URL, url);
            setResult(-1, intent);
        }
        finish();
    }
}
