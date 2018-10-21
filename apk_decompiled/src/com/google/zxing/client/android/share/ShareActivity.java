package com.google.zxing.client.android.share;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.TextView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.android.C0224R;
import com.google.zxing.client.android.Contents;
import com.google.zxing.client.android.Contents.Type;
import com.google.zxing.client.android.Intents.Encode;
import com.google.zxing.client.android.share.Browser.BookmarkColumns;
import com.neighbor.ulsanbus.data.DataConst;

public final class ShareActivity extends Activity {
    private static final int METHODS_DATA_COLUMN = 2;
    private static final int METHODS_KIND_COLUMN = 1;
    private static final String[] METHODS_PROJECTION = new String[]{DataConst.KEY_NOTICE_ID, "kind", "data"};
    private static final int PHONES_NUMBER_COLUMN = 1;
    private static final String[] PHONES_PROJECTION = new String[]{DataConst.KEY_NOTICE_ID, "number"};
    private static final int PICK_APP = 2;
    private static final int PICK_BOOKMARK = 0;
    private static final int PICK_CONTACT = 1;
    private static final String TAG = ShareActivity.class.getSimpleName();
    private final OnClickListener appListener = new C02393();
    private final OnClickListener bookmarkListener = new C02382();
    private Button clipboardButton;
    private final OnClickListener clipboardListener = new C02404();
    private final OnClickListener contactListener = new C02371();
    private final OnKeyListener textListener = new C02415();

    /* renamed from: com.google.zxing.client.android.share.ShareActivity$1 */
    class C02371 implements OnClickListener {
        C02371() {
        }

        public void onClick(View v) {
            Intent intent = new Intent("android.intent.action.PICK", People.CONTENT_URI);
            intent.addFlags(524288);
            ShareActivity.this.startActivityForResult(intent, 1);
        }
    }

    /* renamed from: com.google.zxing.client.android.share.ShareActivity$2 */
    class C02382 implements OnClickListener {
        C02382() {
        }

        public void onClick(View v) {
            Intent intent = new Intent("android.intent.action.PICK");
            intent.addFlags(524288);
            intent.setClassName(ShareActivity.this, BookmarkPickerActivity.class.getName());
            ShareActivity.this.startActivityForResult(intent, 0);
        }
    }

    /* renamed from: com.google.zxing.client.android.share.ShareActivity$3 */
    class C02393 implements OnClickListener {
        C02393() {
        }

        public void onClick(View v) {
            Intent intent = new Intent("android.intent.action.PICK");
            intent.addFlags(524288);
            intent.setClassName(ShareActivity.this, AppPickerActivity.class.getName());
            ShareActivity.this.startActivityForResult(intent, 2);
        }
    }

    /* renamed from: com.google.zxing.client.android.share.ShareActivity$4 */
    class C02404 implements OnClickListener {
        C02404() {
        }

        public void onClick(View v) {
            ClipboardManager clipboard = (ClipboardManager) ShareActivity.this.getSystemService("clipboard");
            if (clipboard.hasText()) {
                ShareActivity.this.launchSearch(clipboard.getText().toString());
            }
        }
    }

    /* renamed from: com.google.zxing.client.android.share.ShareActivity$5 */
    class C02415 implements OnKeyListener {
        C02415() {
        }

        public boolean onKey(View view, int keyCode, KeyEvent event) {
            if (keyCode != 66 || event.getAction() != 0) {
                return false;
            }
            String text = ((TextView) view).getText().toString();
            if (text != null && text.length() > 0) {
                ShareActivity.this.launchSearch(text);
            }
            return true;
        }
    }

    private void launchSearch(String text) {
        Intent intent = new Intent(Encode.ACTION);
        intent.addFlags(524288);
        intent.putExtra(Encode.TYPE, Type.TEXT);
        intent.putExtra(Encode.DATA, text);
        intent.putExtra(Encode.FORMAT, BarcodeFormat.QR_CODE.toString());
        startActivity(intent);
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(C0224R.layout.share);
        findViewById(C0224R.id.share_contact_button).setOnClickListener(this.contactListener);
        findViewById(C0224R.id.share_bookmark_button).setOnClickListener(this.bookmarkListener);
        findViewById(C0224R.id.share_app_button).setOnClickListener(this.appListener);
        this.clipboardButton = (Button) findViewById(C0224R.id.share_clipboard_button);
        this.clipboardButton.setOnClickListener(this.clipboardListener);
        findViewById(C0224R.id.share_text_view).setOnKeyListener(this.textListener);
    }

    protected void onResume() {
        super.onResume();
        if (((ClipboardManager) getSystemService("clipboard")).hasText()) {
            this.clipboardButton.setEnabled(true);
            this.clipboardButton.setText(C0224R.string.button_share_clipboard);
            return;
        }
        this.clipboardButton.setEnabled(false);
        this.clipboardButton.setText(C0224R.string.button_clipboard_empty);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == -1) {
            switch (requestCode) {
                case 0:
                case 2:
                    showTextAsBarcode(intent.getStringExtra(BookmarkColumns.URL));
                    return;
                case 1:
                    showContactAsBarcode(intent.getData());
                    return;
                default:
                    return;
            }
        }
    }

    private void showTextAsBarcode(String text) {
        Log.i(TAG, "Showing text as barcode: " + text);
        if (text != null) {
            Intent intent = new Intent(Encode.ACTION);
            intent.addFlags(524288);
            intent.putExtra(Encode.TYPE, Type.TEXT);
            intent.putExtra(Encode.DATA, text);
            intent.putExtra(Encode.FORMAT, BarcodeFormat.QR_CODE.toString());
            startActivity(intent);
        }
    }

    private void showContactAsBarcode(Uri contactUri) {
        Log.i(TAG, "Showing contact URI as barcode: " + contactUri);
        if (contactUri != null) {
            ContentResolver resolver = getContentResolver();
            try {
                Cursor contactCursor = resolver.query(contactUri, null, null, null, null);
                Bundle bundle = new Bundle();
                if (contactCursor != null && contactCursor.moveToFirst()) {
                    int nameColumn = contactCursor.getColumnIndex("name");
                    if (nameColumn >= 0) {
                        String name = contactCursor.getString(nameColumn);
                        if (name != null && name.length() > 0) {
                            bundle.putString("name", massageContactData(name));
                        }
                    } else {
                        Log.w(TAG, "Unable to find column? name");
                    }
                    contactCursor.close();
                    Cursor phonesCursor = resolver.query(Uri.withAppendedPath(contactUri, "phones"), PHONES_PROJECTION, null, null, null);
                    if (phonesCursor != null) {
                        int foundPhone = 0;
                        while (phonesCursor.moveToNext()) {
                            String number = phonesCursor.getString(1);
                            if (foundPhone < Contents.PHONE_KEYS.length) {
                                bundle.putString(Contents.PHONE_KEYS[foundPhone], massageContactData(number));
                                foundPhone++;
                            }
                        }
                        phonesCursor.close();
                    }
                    ContentResolver contentResolver = resolver;
                    Cursor methodsCursor = contentResolver.query(Uri.withAppendedPath(contactUri, "contact_methods"), METHODS_PROJECTION, null, null, null);
                    if (methodsCursor != null) {
                        int foundEmail = 0;
                        boolean foundPostal = false;
                        while (methodsCursor.moveToNext()) {
                            int kind = methodsCursor.getInt(1);
                            String data = methodsCursor.getString(2);
                            switch (kind) {
                                case 1:
                                    if (foundEmail >= Contents.EMAIL_KEYS.length) {
                                        break;
                                    }
                                    bundle.putString(Contents.EMAIL_KEYS[foundEmail], massageContactData(data));
                                    foundEmail++;
                                    break;
                                case 2:
                                    if (!foundPostal) {
                                        bundle.putString("postal", massageContactData(data));
                                        foundPostal = true;
                                        break;
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                        methodsCursor.close();
                    }
                    Intent intent = new Intent(Encode.ACTION);
                    intent.addFlags(524288);
                    intent.putExtra(Encode.TYPE, Type.CONTACT);
                    intent.putExtra(Encode.DATA, bundle);
                    intent.putExtra(Encode.FORMAT, BarcodeFormat.QR_CODE.toString());
                    Log.i(TAG, "Sending bundle for encoding: " + bundle);
                    startActivity(intent);
                }
            } catch (IllegalArgumentException e) {
            }
        }
    }

    private static String massageContactData(String data) {
        if (data.indexOf(10) >= 0) {
            data = data.replace("\n", " ");
        }
        if (data.indexOf(13) >= 0) {
            return data.replace("\r", " ");
        }
        return data;
    }
}
