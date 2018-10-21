package com.google.zxing.client.android;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.Intents.Scan;
import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.client.android.history.HistoryManager;
import com.google.zxing.client.android.result.ResultButtonListener;
import com.google.zxing.client.android.result.ResultHandler;
import com.google.zxing.client.android.result.ResultHandlerFactory;
import com.google.zxing.client.android.result.supplement.SupplementalInfoRetriever;
import com.google.zxing.client.android.share.ShareActivity;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

public final class CaptureActivity extends Activity implements Callback {
    private static final int ABOUT_ID = 5;
    private static final long BULK_MODE_SCAN_DELAY_MS = 1000;
    private static final Set<ResultMetadataType> DISPLAYABLE_METADATA_TYPES = new HashSet(5);
    private static final int HELP_ID = 4;
    private static final int HISTORY_ID = 2;
    private static final long INTENT_RESULT_DURATION = 1500;
    private static final String PACKAGE_NAME = "com.google.zxing.client.android";
    private static final String PRODUCT_SEARCH_URL_PREFIX = "http://www.google";
    private static final String PRODUCT_SEARCH_URL_SUFFIX = "/m/products/scan";
    private static final String RETURN_CODE_PLACEHOLDER = "{CODE}";
    private static final String RETURN_URL_PARAM = "ret";
    private static final int SETTINGS_ID = 3;
    private static final int SHARE_ID = 1;
    private static final String TAG = CaptureActivity.class.getSimpleName();
    private static final String ZXING_URL = "http://zxing.appspot.com/scan";
    private final OnClickListener aboutListener = new C02181();
    private BeepManager beepManager;
    private String characterSet;
    private boolean copyToClipboard;
    private Vector<BarcodeFormat> decodeFormats;
    private CaptureActivityHandler handler;
    private boolean hasSurface;
    private HistoryManager historyManager;
    private InactivityTimer inactivityTimer;
    private Result lastResult;
    private View resultView;
    private String returnUrlTemplate;
    private Source source;
    private String sourceUrl;
    private TextView statusView;
    private String versionName;
    private ViewfinderView viewfinderView;

    /* renamed from: com.google.zxing.client.android.CaptureActivity$1 */
    class C02181 implements OnClickListener {
        C02181() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CaptureActivity.this.getString(C0224R.string.zxing_url)));
            intent.addFlags(524288);
            CaptureActivity.this.startActivity(intent);
        }
    }

    private enum Source {
        NATIVE_APP_INTENT,
        PRODUCT_SEARCH_LINK,
        ZXING_LINK,
        NONE
    }

    static {
        DISPLAYABLE_METADATA_TYPES.add(ResultMetadataType.ISSUE_NUMBER);
        DISPLAYABLE_METADATA_TYPES.add(ResultMetadataType.SUGGESTED_PRICE);
        DISPLAYABLE_METADATA_TYPES.add(ResultMetadataType.ERROR_CORRECTION_LEVEL);
        DISPLAYABLE_METADATA_TYPES.add(ResultMetadataType.POSSIBLE_COUNTRY);
    }

    ViewfinderView getViewfinderView() {
        return this.viewfinderView;
    }

    public Handler getHandler() {
        return this.handler;
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        getWindow().addFlags(128);
        setContentView(C0224R.layout.capture);
        CameraManager.init(getApplication());
        this.viewfinderView = (ViewfinderView) findViewById(C0224R.id.viewfinder_view);
        this.resultView = findViewById(C0224R.id.result_view);
        this.statusView = (TextView) findViewById(C0224R.id.status_view);
        this.handler = null;
        this.lastResult = null;
        this.hasSurface = false;
        this.historyManager = new HistoryManager(this);
        this.historyManager.trimHistory();
        this.inactivityTimer = new InactivityTimer(this);
        this.beepManager = new BeepManager(this);
    }

    protected void onResume() {
        boolean z = true;
        super.onResume();
        resetStatusView();
        SurfaceHolder surfaceHolder = ((SurfaceView) findViewById(C0224R.id.preview_view)).getHolder();
        if (this.hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(3);
        }
        Intent intent = getIntent();
        String action = intent == null ? null : intent.getAction();
        String dataString = intent == null ? null : intent.getDataString();
        if (intent == null || action == null) {
            this.source = Source.NONE;
            this.decodeFormats = null;
            this.characterSet = null;
        } else {
            if (action.equals(Scan.ACTION)) {
                this.source = Source.NATIVE_APP_INTENT;
                this.decodeFormats = DecodeFormatManager.parseDecodeFormats(intent);
                if (intent.hasExtra(Scan.WIDTH) && intent.hasExtra(Scan.HEIGHT)) {
                    int width = intent.getIntExtra(Scan.WIDTH, 0);
                    int height = intent.getIntExtra(Scan.HEIGHT, 0);
                    if (width > 0 && height > 0) {
                        CameraManager.get().setManualFramingRect(width, height);
                    }
                }
            } else if (dataString != null && dataString.contains(PRODUCT_SEARCH_URL_PREFIX) && dataString.contains(PRODUCT_SEARCH_URL_SUFFIX)) {
                this.source = Source.PRODUCT_SEARCH_LINK;
                this.sourceUrl = dataString;
                this.decodeFormats = DecodeFormatManager.PRODUCT_FORMATS;
            } else if (dataString == null || !dataString.startsWith(ZXING_URL)) {
                this.source = Source.NONE;
                this.decodeFormats = null;
            } else {
                this.source = Source.ZXING_LINK;
                this.sourceUrl = dataString;
                Uri inputUri = Uri.parse(this.sourceUrl);
                this.returnUrlTemplate = inputUri.getQueryParameter(RETURN_URL_PARAM);
                this.decodeFormats = DecodeFormatManager.parseDecodeFormats(inputUri);
            }
            this.characterSet = intent.getStringExtra(Scan.CHARACTER_SET);
        }
        if (!(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PreferencesActivity.KEY_COPY_TO_CLIPBOARD, true) && (intent == null || intent.getBooleanExtra(Scan.SAVE_HISTORY, true)))) {
            z = false;
        }
        this.copyToClipboard = z;
        this.beepManager.updatePrefs();
        this.inactivityTimer.onResume();
    }

    protected void onPause() {
        super.onPause();
        if (this.handler != null) {
            this.handler.quitSynchronously();
            this.handler = null;
        }
        this.inactivityTimer.onPause();
        CameraManager.get().closeDriver();
    }

    protected void onDestroy() {
        this.inactivityTimer.shutdown();
        super.onDestroy();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            if (this.source == Source.NATIVE_APP_INTENT) {
                setResult(0);
                finish();
                return true;
            } else if ((this.source == Source.NONE || this.source == Source.ZXING_LINK) && this.lastResult != null) {
                resetStatusView();
                if (this.handler == null) {
                    return true;
                }
                this.handler.sendEmptyMessage(C0224R.id.restart_preview);
                return true;
            }
        } else if (keyCode == 80 || keyCode == 27) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 1, 0, C0224R.string.menu_share).setIcon(17301586);
        menu.add(0, 2, 0, C0224R.string.menu_history).setIcon(17301578);
        menu.add(0, 3, 0, C0224R.string.menu_settings).setIcon(17301577);
        menu.add(0, 4, 0, C0224R.string.menu_help).setIcon(17301568);
        menu.add(0, 5, 0, C0224R.string.menu_about).setIcon(17301569);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(1).setVisible(this.lastResult == null);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case 1:
                intent = new Intent("android.intent.action.VIEW");
                intent.addFlags(524288);
                intent.setClassName(this, ShareActivity.class.getName());
                startActivity(intent);
                break;
            case 2:
                this.historyManager.buildAlert().show();
                break;
            case 3:
                intent = new Intent("android.intent.action.VIEW");
                intent.addFlags(524288);
                intent.setClassName(this, PreferencesActivity.class.getName());
                startActivity(intent);
                break;
            case 4:
                intent = new Intent("android.intent.action.VIEW");
                intent.addFlags(524288);
                intent.setClassName(this, HelpActivity.class.getName());
                startActivity(intent);
                break;
            case 5:
                Builder builder = new Builder(this);
                builder.setTitle(getString(C0224R.string.title_about) + this.versionName);
                builder.setMessage(getString(C0224R.string.msg_about) + "\n\n" + getString(C0224R.string.zxing_url));
                builder.setIcon(C0224R.drawable.launcher_icon);
                builder.setPositiveButton(C0224R.string.button_open_browser, this.aboutListener);
                builder.setNegativeButton(C0224R.string.button_cancel, null);
                builder.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        if (!this.hasSurface) {
            this.hasSurface = true;
            initCamera(holder);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        this.hasSurface = false;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void handleDecode(Result rawResult, Bitmap barcode) {
        this.inactivityTimer.onActivity();
        this.lastResult = rawResult;
        ResultHandler resultHandler = ResultHandlerFactory.makeResultHandler(this, rawResult);
        this.historyManager.addHistoryItem(rawResult, resultHandler);
        if (barcode == null) {
            handleDecodeInternally(rawResult, resultHandler, null);
            return;
        }
        this.beepManager.playBeepSoundAndVibrate();
        drawResultPoints(barcode, rawResult);
        switch (this.source) {
            case NATIVE_APP_INTENT:
            case PRODUCT_SEARCH_LINK:
                handleDecodeExternally(rawResult, resultHandler, barcode);
                return;
            case ZXING_LINK:
                if (this.returnUrlTemplate == null) {
                    handleDecodeInternally(rawResult, resultHandler, barcode);
                    return;
                } else {
                    handleDecodeExternally(rawResult, resultHandler, barcode);
                    return;
                }
            case NONE:
                if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PreferencesActivity.KEY_BULK_MODE, false)) {
                    Toast.makeText(this, C0224R.string.msg_bulk_mode_scanned, 0).show();
                    if (this.handler != null) {
                        this.handler.sendEmptyMessageDelayed(C0224R.id.restart_preview, BULK_MODE_SCAN_DELAY_MS);
                    }
                    resetStatusView();
                    return;
                }
                handleDecodeInternally(rawResult, resultHandler, barcode);
                return;
            default:
                return;
        }
    }

    private void drawResultPoints(Bitmap barcode, Result rawResult) {
        int i = 0;
        ResultPoint[] points = rawResult.getResultPoints();
        if (points != null && points.length > 0) {
            Canvas canvas = new Canvas(barcode);
            Paint paint = new Paint();
            paint.setColor(getResources().getColor(C0224R.color.result_image_border));
            paint.setStrokeWidth(3.0f);
            paint.setStyle(Style.STROKE);
            canvas.drawRect(new Rect(2, 2, barcode.getWidth() - 2, barcode.getHeight() - 2), paint);
            paint.setColor(getResources().getColor(C0224R.color.result_points));
            if (points.length == 2) {
                paint.setStrokeWidth(4.0f);
                drawLine(canvas, paint, points[0], points[1]);
            } else if (points.length == 4 && (rawResult.getBarcodeFormat().equals(BarcodeFormat.UPC_A) || rawResult.getBarcodeFormat().equals(BarcodeFormat.EAN_13))) {
                drawLine(canvas, paint, points[0], points[1]);
                drawLine(canvas, paint, points[2], points[3]);
            } else {
                paint.setStrokeWidth(10.0f);
                int length = points.length;
                while (i < length) {
                    ResultPoint point = points[i];
                    canvas.drawPoint(point.getX(), point.getY(), paint);
                    i++;
                }
            }
        }
    }

    private static void drawLine(Canvas canvas, Paint paint, ResultPoint a, ResultPoint b) {
        canvas.drawLine(a.getX(), a.getY(), b.getX(), b.getY(), paint);
    }

    private void handleDecodeInternally(Result rawResult, ResultHandler resultHandler, Bitmap barcode) {
        this.statusView.setVisibility(8);
        this.viewfinderView.setVisibility(8);
        this.resultView.setVisibility(0);
        ImageView barcodeImageView = (ImageView) findViewById(C0224R.id.barcode_image_view);
        if (barcode == null) {
            barcodeImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), C0224R.drawable.launcher_icon));
        } else {
            barcodeImageView.setImageBitmap(barcode);
        }
        ((TextView) findViewById(C0224R.id.format_text_view)).setText(rawResult.getBarcodeFormat().toString());
        ((TextView) findViewById(C0224R.id.type_text_view)).setText(resultHandler.getType().toString());
        ((TextView) findViewById(C0224R.id.time_text_view)).setText(DateFormat.getDateTimeInstance(3, 3).format(new Date(rawResult.getTimestamp())));
        TextView metaTextView = (TextView) findViewById(C0224R.id.meta_text_view);
        View metaTextViewLabel = findViewById(C0224R.id.meta_text_view_label);
        metaTextView.setVisibility(8);
        metaTextViewLabel.setVisibility(8);
        Map<ResultMetadataType, Object> metadata = rawResult.getResultMetadata();
        if (metadata != null) {
            StringBuilder stringBuilder = new StringBuilder(20);
            for (Entry<ResultMetadataType, Object> entry : metadata.entrySet()) {
                if (DISPLAYABLE_METADATA_TYPES.contains(entry.getKey())) {
                    stringBuilder.append(entry.getValue()).append('\n');
                }
            }
            if (stringBuilder.length() > 0) {
                stringBuilder.setLength(stringBuilder.length() - 1);
                metaTextView.setText(stringBuilder);
                metaTextView.setVisibility(0);
                metaTextViewLabel.setVisibility(0);
            }
        }
        TextView contentsTextView = (TextView) findViewById(C0224R.id.contents_text_view);
        CharSequence displayContents = resultHandler.getDisplayContents();
        contentsTextView.setText(displayContents);
        contentsTextView.setTextSize(2, (float) Math.max(22, 32 - (displayContents.length() / 4)));
        TextView supplementTextView = (TextView) findViewById(C0224R.id.contents_supplement_text_view);
        supplementTextView.setText("");
        supplementTextView.setOnClickListener(null);
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PreferencesActivity.KEY_SUPPLEMENTAL, true)) {
            SupplementalInfoRetriever.maybeInvokeRetrieval(supplementTextView, resultHandler.getResult(), this.handler, this);
        }
        int buttonCount = resultHandler.getButtonCount();
        ViewGroup buttonView = (ViewGroup) findViewById(C0224R.id.result_button_view);
        buttonView.requestFocus();
        for (int x = 0; x < 4; x++) {
            TextView button = (TextView) buttonView.getChildAt(x);
            if (x < buttonCount) {
                button.setVisibility(0);
                button.setText(resultHandler.getButtonText(x));
                button.setOnClickListener(new ResultButtonListener(resultHandler, x));
            } else {
                button.setVisibility(8);
            }
        }
        if (this.copyToClipboard && !resultHandler.areContentsSecure()) {
            ((ClipboardManager) getSystemService("clipboard")).setText(displayContents);
        }
    }

    private void handleDecodeExternally(Result rawResult, ResultHandler resultHandler, Bitmap barcode) {
        this.viewfinderView.drawResultBitmap(barcode);
        this.statusView.setText(getString(resultHandler.getDisplayTitle()));
        if (this.copyToClipboard && !resultHandler.areContentsSecure()) {
            ((ClipboardManager) getSystemService("clipboard")).setText(resultHandler.getDisplayContents());
        }
        Message message;
        if (this.source == Source.NATIVE_APP_INTENT) {
            Intent intent = new Intent(getIntent().getAction());
            intent.addFlags(524288);
            intent.putExtra(Scan.RESULT, rawResult.toString());
            intent.putExtra(Scan.RESULT_FORMAT, rawResult.getBarcodeFormat().toString());
            byte[] rawBytes = rawResult.getRawBytes();
            if (rawBytes != null && rawBytes.length > 0) {
                intent.putExtra(Scan.RESULT_BYTES, rawBytes);
            }
            message = Message.obtain(this.handler, C0224R.id.return_scan_result);
            message.obj = intent;
            this.handler.sendMessageDelayed(message, INTENT_RESULT_DURATION);
        } else if (this.source == Source.PRODUCT_SEARCH_LINK) {
            message = Message.obtain(this.handler, C0224R.id.launch_product_query);
            message.obj = this.sourceUrl.substring(0, this.sourceUrl.lastIndexOf("/scan")) + "?q=" + resultHandler.getDisplayContents().toString() + "&source=zxing";
            this.handler.sendMessageDelayed(message, INTENT_RESULT_DURATION);
        } else if (this.source == Source.ZXING_LINK) {
            message = Message.obtain(this.handler, C0224R.id.launch_product_query);
            message.obj = this.returnUrlTemplate.replace(RETURN_CODE_PLACEHOLDER, resultHandler.getDisplayContents().toString());
            this.handler.sendMessageDelayed(message, INTENT_RESULT_DURATION);
        }
    }

    private boolean showHelpOnFirstLaunch() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.google.zxing.client.android", 0);
            int currentVersion = info.versionCode;
            this.versionName = info.versionName;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            int lastVersion = prefs.getInt(PreferencesActivity.KEY_HELP_VERSION_SHOWN, 0);
            if (currentVersion <= lastVersion) {
                return false;
            }
            prefs.edit().putInt(PreferencesActivity.KEY_HELP_VERSION_SHOWN, currentVersion).commit();
            Intent intent = new Intent(this, HelpActivity.class);
            intent.addFlags(524288);
            intent.putExtra(HelpActivity.REQUESTED_PAGE_KEY, lastVersion == 0 ? HelpActivity.DEFAULT_PAGE : HelpActivity.WHATS_NEW_PAGE);
            startActivity(intent);
            return true;
        } catch (NameNotFoundException e) {
            Log.w(TAG, e);
            return false;
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
            if (this.handler == null) {
                this.handler = new CaptureActivityHandler(this, this.decodeFormats, this.characterSet);
            }
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            Log.w(TAG, "Unexpected error initializating camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        Builder builder = new Builder(this);
        builder.setTitle(getString(C0224R.string.app_name));
        builder.setMessage(getString(C0224R.string.msg_camera_framework_bug));
        builder.setPositiveButton(C0224R.string.button_ok, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }

    private void resetStatusView() {
        this.resultView.setVisibility(8);
        this.statusView.setText(C0224R.string.msg_default_status);
        this.statusView.setVisibility(0);
        this.viewfinderView.setVisibility(0);
        this.lastResult = null;
    }

    public void drawViewfinder() {
        this.viewfinderView.drawViewfinder();
    }
}
