package com.google.zxing.client.android;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public final class HelpActivity extends Activity {
    private static final String BASE_URL = "file:///android_asset/html/";
    private static final String[] BUGGY_MODEL_SUBSTRINGS = new String[]{"Desire", "Pulse", "Geeksphone", "supersonic"};
    private static final Uri BUGGY_URI = Uri.parse("http://code.google.com/p/zxing/wiki/FrequentlyAskedQuestions");
    public static final String DEFAULT_PAGE = "index.html";
    public static final String REQUESTED_PAGE_KEY = "requested_page_key";
    private static final String TAG = HelpActivity.class.getSimpleName();
    private static final String WEBVIEW_STATE_PRESENT = "webview_state_present";
    public static final String WHATS_NEW_PAGE = "whatsnew.html";
    private static boolean initialized = false;
    private Button backButton;
    private final OnClickListener backListener = new C02201();
    private final OnClickListener doneListener = new C02212();
    private final DialogInterface.OnClickListener groupsListener = new C02223();
    private WebView webView;

    /* renamed from: com.google.zxing.client.android.HelpActivity$1 */
    class C02201 implements OnClickListener {
        C02201() {
        }

        public void onClick(View view) {
            HelpActivity.this.webView.goBack();
        }
    }

    /* renamed from: com.google.zxing.client.android.HelpActivity$2 */
    class C02212 implements OnClickListener {
        C02212() {
        }

        public void onClick(View view) {
            HelpActivity.this.finish();
        }
    }

    /* renamed from: com.google.zxing.client.android.HelpActivity$3 */
    class C02223 implements DialogInterface.OnClickListener {
        C02223() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            Intent intent = new Intent("android.intent.action.VIEW", HelpActivity.BUGGY_URI);
            intent.addFlags(524288);
            HelpActivity.this.startActivity(intent);
        }
    }

    private final class HelpClient extends WebViewClient {
        private HelpClient() {
        }

        public void onPageFinished(WebView view, String url) {
            HelpActivity.this.setTitle(view.getTitle());
            HelpActivity.this.backButton.setEnabled(view.canGoBack());
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("file")) {
                return false;
            }
            HelpActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
            return true;
        }
    }

    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(C0224R.layout.help);
        this.webView = (WebView) findViewById(C0224R.id.help_contents);
        this.webView.setWebViewClient(new HelpClient());
        Intent intent = getIntent();
        if (icicle != null && icicle.getBoolean(WEBVIEW_STATE_PRESENT, false)) {
            this.webView.restoreState(icicle);
        } else if (intent != null) {
            String page = intent.getStringExtra(REQUESTED_PAGE_KEY);
            if (page == null || page.length() <= 0) {
                this.webView.loadUrl("file:///android_asset/html/index.html");
            } else {
                this.webView.loadUrl(BASE_URL + page);
            }
        } else {
            this.webView.loadUrl("file:///android_asset/html/index.html");
        }
        this.backButton = (Button) findViewById(C0224R.id.back_button);
        this.backButton.setOnClickListener(this.backListener);
        findViewById(C0224R.id.done_button).setOnClickListener(this.doneListener);
        if (!initialized) {
            initialized = true;
            checkBuggyDevice();
        }
    }

    private void checkBuggyDevice() {
        String model = Build.MODEL;
        Log.i(TAG, "Build model is " + model);
        if (model != null) {
            for (String buggyModelSubstring : BUGGY_MODEL_SUBSTRINGS) {
                if (model.contains(buggyModelSubstring)) {
                    Builder builder = new Builder(this);
                    builder.setMessage(C0224R.string.msg_buggy);
                    builder.setPositiveButton(C0224R.string.button_ok, this.groupsListener);
                    builder.setNegativeButton(C0224R.string.button_cancel, null);
                    builder.show();
                    return;
                }
            }
        }
    }

    protected void onSaveInstanceState(Bundle state) {
        String url = this.webView.getUrl();
        if (url != null && url.length() > 0) {
            this.webView.saveState(state);
            state.putBoolean(WEBVIEW_STATE_PRESENT, true);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4 || !this.webView.canGoBack()) {
            return super.onKeyDown(keyCode, event);
        }
        this.webView.goBack();
        return true;
    }
}
