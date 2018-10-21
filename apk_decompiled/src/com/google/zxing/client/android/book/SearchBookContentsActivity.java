package com.google.zxing.client.android.book;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.google.zxing.client.android.AndroidHttpClient;
import com.google.zxing.client.android.C0224R;
import com.google.zxing.client.android.Intents.SearchBookContents;
import com.google.zxing.client.android.LocaleManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class SearchBookContentsActivity extends Activity {
    private static final Pattern GT_ENTITY_PATTERN = Pattern.compile("&gt;");
    private static final Pattern LT_ENTITY_PATTERN = Pattern.compile("&lt;");
    private static final Pattern QUOTE_ENTITY_PATTERN = Pattern.compile("&#39;");
    private static final Pattern QUOT_ENTITY_PATTERN = Pattern.compile("&quot;");
    private static final String TAG = SearchBookContentsActivity.class.getSimpleName();
    private static final Pattern TAG_PATTERN = Pattern.compile("\\<.*?\\>");
    private static final String USER_AGENT = "ZXing (Android)";
    private final OnClickListener buttonListener = new C02262();
    private final Handler handler = new C02251();
    private TextView headerView;
    private String isbn;
    private final OnKeyListener keyListener = new C02273();
    private NetworkThread networkThread;
    private Button queryButton;
    private EditText queryTextView;
    private ListView resultListView;

    /* renamed from: com.google.zxing.client.android.book.SearchBookContentsActivity$1 */
    class C02251 extends Handler {
        C02251() {
        }

        public void handleMessage(Message message) {
            if (message.what == C0224R.id.search_book_contents_succeeded) {
                SearchBookContentsActivity.this.handleSearchResults((JSONObject) message.obj);
                SearchBookContentsActivity.this.resetForNewQuery();
            } else if (message.what == C0224R.id.search_book_contents_failed) {
                SearchBookContentsActivity.this.resetForNewQuery();
                SearchBookContentsActivity.this.headerView.setText(C0224R.string.msg_sbc_failed);
            }
        }
    }

    /* renamed from: com.google.zxing.client.android.book.SearchBookContentsActivity$2 */
    class C02262 implements OnClickListener {
        C02262() {
        }

        public void onClick(View view) {
            SearchBookContentsActivity.this.launchSearch();
        }
    }

    /* renamed from: com.google.zxing.client.android.book.SearchBookContentsActivity$3 */
    class C02273 implements OnKeyListener {
        C02273() {
        }

        public boolean onKey(View view, int keyCode, KeyEvent event) {
            if (keyCode != 66 || event.getAction() != 0) {
                return false;
            }
            SearchBookContentsActivity.this.launchSearch();
            return true;
        }
    }

    private static final class NetworkThread extends Thread {
        private final Handler handler;
        private final String isbn;
        private final String query;

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find block by offset: 0x00af in list [B:9:0x00ac]
	at jadx.core.utils.BlockUtils.getBlockByOffset(BlockUtils.java:43)
	at jadx.core.dex.instructions.IfNode.initBlocks(IfNode.java:60)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.initBlocksInIfNodes(BlockFinish.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.visit(BlockFinish.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/1813666644.run(Unknown Source)
*/
            /*
            r19 = this;
            r9 = 0;
            r0 = r19;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r2 = r0.isbn;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r3 = "http://google.com/books?id=";	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r2 = r2.startsWith(r3);	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            if (r2 == 0) goto L_0x00b0;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
        L_0x000d:
            r0 = r19;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r2 = r0.isbn;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r3 = 61;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r12 = r2.indexOf(r3);	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r0 = r19;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r2 = r0.isbn;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r3 = r12 + 1;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r18 = r2.substring(r3);	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r1 = new java.net.URI;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r2 = "http";	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r3 = 0;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r4 = "www.google.com";	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r5 = -1;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r6 = "/books";	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r7.<init>();	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r8 = "id=";	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r7 = r7.append(r8);	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r0 = r18;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r7 = r7.append(r0);	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r8 = "&jscmd=SearchWithinVolume2&q=";	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r7 = r7.append(r8);	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r0 = r19;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r8 = r0.query;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r7 = r7.append(r8);	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r7 = r7.toString();	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r8 = 0;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r1.<init>(r2, r3, r4, r5, r6, r7, r8);	 Catch:{ Exception -> 0x011f, all -> 0x013d }
        L_0x0052:
            r13 = new org.apache.http.client.methods.HttpGet;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r13.<init>(r1);	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r2 = "cookie";	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r3 = r1.toString();	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r3 = getCookie(r3);	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r13.setHeader(r2, r3);	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r2 = "ZXing (Android)";	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r9 = com.google.zxing.client.android.AndroidHttpClient.newInstance(r2);	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r17 = r9.execute(r13);	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r2 = r17.getStatusLine();	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r2 = r2.getStatusCode();	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r3 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            if (r2 != r3) goto L_0x00e5;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
        L_0x007a:
            r11 = r17.getEntity();	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r15 = new java.io.ByteArrayOutputStream;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r15.<init>();	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r11.writeTo(r15);	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r15.flush();	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r14 = new org.json.JSONObject;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r2 = getEncoding(r11);	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r2 = r15.toString(r2);	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r14.<init>(r2);	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r15.close();	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r0 = r19;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r2 = r0.handler;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r3 = com.google.zxing.client.android.C0224R.id.search_book_contents_succeeded;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r16 = android.os.Message.obtain(r2, r3);	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r0 = r16;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r0.obj = r14;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r16.sendToTarget();	 Catch:{ Exception -> 0x011f, all -> 0x013d }
        L_0x00aa:
            if (r9 == 0) goto L_0x00af;
        L_0x00ac:
            r9.close();
        L_0x00af:
            return;
        L_0x00b0:
            r1 = new java.net.URI;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r2 = "http";	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r3 = 0;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r4 = "www.google.com";	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r5 = -1;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r6 = "/books";	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r7.<init>();	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r8 = "vid=isbn";	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r7 = r7.append(r8);	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r0 = r19;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r8 = r0.isbn;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r7 = r7.append(r8);	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r8 = "&jscmd=SearchWithinVolume2&q=";	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r7 = r7.append(r8);	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r0 = r19;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r8 = r0.query;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r7 = r7.append(r8);	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r7 = r7.toString();	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r8 = 0;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r1.<init>(r2, r3, r4, r5, r6, r7, r8);	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            goto L_0x0052;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
        L_0x00e5:
            r2 = com.google.zxing.client.android.book.SearchBookContentsActivity.TAG;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r3.<init>();	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r4 = "HTTP returned ";	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r3 = r3.append(r4);	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r4 = r17.getStatusLine();	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r4 = r4.getStatusCode();	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r3 = r3.append(r4);	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r4 = " for ";	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r3 = r3.append(r4);	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r3 = r3.append(r1);	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r3 = r3.toString();	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            android.util.Log.w(r2, r3);	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r0 = r19;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r2 = r0.handler;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r3 = com.google.zxing.client.android.C0224R.id.search_book_contents_failed;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r16 = android.os.Message.obtain(r2, r3);	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r16.sendToTarget();	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            goto L_0x00aa;
        L_0x011f:
            r10 = move-exception;
            r2 = com.google.zxing.client.android.book.SearchBookContentsActivity.TAG;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r3 = "Error accessing book search";	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            android.util.Log.w(r2, r3, r10);	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r0 = r19;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r2 = r0.handler;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r3 = com.google.zxing.client.android.C0224R.id.search_book_contents_failed;	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r16 = android.os.Message.obtain(r2, r3);	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            r16.sendToTarget();	 Catch:{ Exception -> 0x011f, all -> 0x013d }
            if (r9 == 0) goto L_0x00af;
        L_0x0138:
            r9.close();
            goto L_0x00af;
        L_0x013d:
            r2 = move-exception;
            if (r9 == 0) goto L_0x0143;
        L_0x0140:
            r9.close();
        L_0x0143:
            throw r2;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.client.android.book.SearchBookContentsActivity.NetworkThread.run():void");
        }

        NetworkThread(String isbn, String query, Handler handler) {
            this.isbn = isbn;
            this.query = query;
            this.handler = handler;
        }

        private static String getCookie(String url) {
            String cookie = CookieManager.getInstance().getCookie(url);
            if (cookie == null || cookie.length() == 0) {
                Log.d(SearchBookContentsActivity.TAG, "Book Search cookie was missing or expired");
                HttpUriRequest head = new HttpHead(url);
                AndroidHttpClient client = AndroidHttpClient.newInstance(SearchBookContentsActivity.USER_AGENT);
                try {
                    HttpResponse response = client.execute(head);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        for (Header theCookie : response.getHeaders("set-cookie")) {
                            CookieManager.getInstance().setCookie(url, theCookie.getValue());
                        }
                        CookieSyncManager.getInstance().sync();
                        cookie = CookieManager.getInstance().getCookie(url);
                    }
                } catch (IOException e) {
                    Log.w(SearchBookContentsActivity.TAG, "Error setting book search cookie", e);
                }
                client.close();
            }
            return cookie;
        }

        private static String getEncoding(HttpEntity entity) {
            return "windows-1252";
        }
    }

    String getISBN() {
        return this.isbn;
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        CookieSyncManager.createInstance(this);
        CookieManager.getInstance().removeExpiredCookie();
        Intent intent = getIntent();
        if (intent == null || !intent.getAction().equals(SearchBookContents.ACTION)) {
            finish();
            return;
        }
        this.isbn = intent.getStringExtra(SearchBookContents.ISBN);
        if (LocaleManager.isBookSearchUrl(this.isbn)) {
            setTitle(getString(C0224R.string.sbc_name));
        } else {
            setTitle(getString(C0224R.string.sbc_name) + ": ISBN " + this.isbn);
        }
        setContentView(C0224R.layout.search_book_contents);
        this.queryTextView = (EditText) findViewById(C0224R.id.query_text_view);
        String initialQuery = intent.getStringExtra(SearchBookContents.QUERY);
        if (initialQuery != null && initialQuery.length() > 0) {
            this.queryTextView.setText(initialQuery);
        }
        this.queryTextView.setOnKeyListener(this.keyListener);
        this.queryButton = (Button) findViewById(C0224R.id.query_button);
        this.queryButton.setOnClickListener(this.buttonListener);
        this.resultListView = (ListView) findViewById(C0224R.id.result_list_view);
        this.headerView = (TextView) LayoutInflater.from(this).inflate(C0224R.layout.search_book_contents_header, this.resultListView, false);
        this.resultListView.addHeaderView(this.headerView);
    }

    protected void onResume() {
        super.onResume();
        this.queryTextView.selectAll();
    }

    private void resetForNewQuery() {
        this.networkThread = null;
        this.queryTextView.setEnabled(true);
        this.queryTextView.selectAll();
        this.queryButton.setEnabled(true);
    }

    private void launchSearch() {
        if (this.networkThread == null) {
            String query = this.queryTextView.getText().toString();
            if (query != null && query.length() > 0) {
                this.networkThread = new NetworkThread(this.isbn, query, this.handler);
                this.networkThread.start();
                this.headerView.setText(C0224R.string.msg_sbc_searching_book);
                this.resultListView.setAdapter(null);
                this.queryTextView.setEnabled(false);
                this.queryButton.setEnabled(false);
            }
        }
    }

    private void handleSearchResults(JSONObject json) {
        try {
            int count = json.getInt("number_of_results");
            this.headerView.setText("Found " + (count == 1 ? "1 result" : count + " results"));
            if (count > 0) {
                JSONArray results = json.getJSONArray(MediaBrowserServiceCompat.KEY_SEARCH_RESULTS);
                SearchBookContentsResult.setQuery(this.queryTextView.getText().toString());
                List<SearchBookContentsResult> items = new ArrayList(count);
                for (int x = 0; x < count; x++) {
                    items.add(parseResult(results.getJSONObject(x)));
                }
                this.resultListView.setOnItemClickListener(new BrowseBookListener(this, items));
                this.resultListView.setAdapter(new SearchBookContentsAdapter(this, items));
                return;
            }
            if ("false".equals(json.optString("searchable"))) {
                this.headerView.setText(C0224R.string.msg_sbc_book_not_searchable);
            }
            this.resultListView.setAdapter(null);
        } catch (JSONException e) {
            Log.w(TAG, "Bad JSON from book search", e);
            this.resultListView.setAdapter(null);
            this.headerView.setText(C0224R.string.msg_sbc_failed);
        }
    }

    private SearchBookContentsResult parseResult(JSONObject json) {
        try {
            String pageId = json.getString("page_id");
            String pageNumber = json.getString("page_number");
            if (pageNumber.length() > 0) {
                pageNumber = getString(C0224R.string.msg_sbc_page) + ' ' + pageNumber;
            } else {
                pageNumber = getString(C0224R.string.msg_sbc_unknown_page);
            }
            String snippet = json.optString("snippet_text");
            boolean valid = true;
            if (snippet.length() > 0) {
                snippet = QUOT_ENTITY_PATTERN.matcher(QUOTE_ENTITY_PATTERN.matcher(GT_ENTITY_PATTERN.matcher(LT_ENTITY_PATTERN.matcher(TAG_PATTERN.matcher(snippet).replaceAll("")).replaceAll("<")).replaceAll(">")).replaceAll("'")).replaceAll("\"");
            } else {
                snippet = '(' + getString(C0224R.string.msg_sbc_snippet_unavailable) + ')';
                valid = false;
            }
            return new SearchBookContentsResult(pageId, pageNumber, snippet, valid);
        } catch (JSONException e) {
            return new SearchBookContentsResult(getString(C0224R.string.msg_sbc_no_page_returned), "", "", false);
        }
    }
}
