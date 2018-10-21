package com.google.zxing.client.android.encode;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.zxing.WriterException;
import com.google.zxing.client.android.C0224R;
import com.google.zxing.client.android.FinishListener;
import com.google.zxing.client.android.Intents.Encode;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public final class EncodeActivity extends Activity {
    private static final int MAX_BARCODE_FILENAME_LENGTH = 24;
    private static final String TAG = EncodeActivity.class.getSimpleName();
    private QRCodeEncoder qrCodeEncoder;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Intent intent = getIntent();
        if (intent != null) {
            String action = intent.getAction();
            if (action.equals(Encode.ACTION) || action.equals("android.intent.action.SEND")) {
                setContentView(C0224R.layout.encode);
                return;
            }
        }
        finish();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 1, 0, C0224R.string.menu_share).setIcon(17301586);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        FileNotFoundException fnfe;
        Throwable th;
        if (this.qrCodeEncoder == null) {
            Log.w(TAG, "No existing barcode to send?");
        } else {
            String contents = this.qrCodeEncoder.getContents();
            try {
                Bitmap bitmap = this.qrCodeEncoder.encodeAsBitmap();
                File barcodesRoot = new File(new File(Environment.getExternalStorageDirectory(), "BarcodeScanner"), "Barcodes");
                if (barcodesRoot.exists() || barcodesRoot.mkdirs()) {
                    File barcodeFile = new File(barcodesRoot, makeBarcodeFileName(contents) + ".png");
                    barcodeFile.delete();
                    FileOutputStream fos = null;
                    try {
                        FileOutputStream fos2 = new FileOutputStream(barcodeFile);
                        try {
                            bitmap.compress(CompressFormat.PNG, 0, fos2);
                            if (fos2 != null) {
                                try {
                                    fos2.close();
                                } catch (IOException e) {
                                }
                            }
                            Intent intent = new Intent("android.intent.action.SEND", Uri.parse("mailto:"));
                            intent.putExtra("android.intent.extra.SUBJECT", getString(C0224R.string.app_name) + " - " + this.qrCodeEncoder.getTitle());
                            intent.putExtra("android.intent.extra.TEXT", this.qrCodeEncoder.getContents());
                            intent.putExtra("android.intent.extra.STREAM", Uri.parse("file://" + barcodeFile.getAbsolutePath()));
                            intent.setType("image/png");
                            intent.addFlags(524288);
                            startActivity(Intent.createChooser(intent, null));
                        } catch (FileNotFoundException e2) {
                            fnfe = e2;
                            fos = fos2;
                            try {
                                Log.w(TAG, "Couldn't access file " + barcodeFile + " due to " + fnfe);
                                showErrorMessage(C0224R.string.msg_unmount_usb);
                                if (fos != null) {
                                    try {
                                        fos.close();
                                    } catch (IOException e3) {
                                    }
                                }
                                return true;
                            } catch (Throwable th2) {
                                th = th2;
                                if (fos != null) {
                                    try {
                                        fos.close();
                                    } catch (IOException e4) {
                                    }
                                }
                                throw th;
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            fos = fos2;
                            if (fos != null) {
                                fos.close();
                            }
                            throw th;
                        }
                    } catch (FileNotFoundException e5) {
                        fnfe = e5;
                        Log.w(TAG, "Couldn't access file " + barcodeFile + " due to " + fnfe);
                        showErrorMessage(C0224R.string.msg_unmount_usb);
                        if (fos != null) {
                            fos.close();
                        }
                        return true;
                    }
                }
                Log.w(TAG, "Couldn't make dir " + barcodesRoot);
                showErrorMessage(C0224R.string.msg_unmount_usb);
            } catch (WriterException we) {
                Log.w(TAG, we);
            }
        }
        return true;
    }

    private static CharSequence makeBarcodeFileName(CharSequence contents) {
        int fileNameLength = Math.min(24, contents.length());
        StringBuilder fileName = new StringBuilder(fileNameLength);
        for (int i = 0; i < fileNameLength; i++) {
            char c = contents.charAt(i);
            if ((c < 'A' || c > 'Z') && ((c < 'a' || c > 'z') && (c < '0' || c > '9'))) {
                fileName.append('_');
            } else {
                fileName.append(c);
            }
        }
        return fileName;
    }

    protected void onResume() {
        int smallerDimension;
        super.onResume();
        Display display = ((WindowManager) getSystemService("window")).getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        if (width < height) {
            smallerDimension = width;
        } else {
            smallerDimension = height;
        }
        try {
            this.qrCodeEncoder = new QRCodeEncoder(this, getIntent(), (smallerDimension * 7) / 8);
            setTitle(getString(C0224R.string.app_name) + " - " + this.qrCodeEncoder.getTitle());
            ((ImageView) findViewById(C0224R.id.image_view)).setImageBitmap(this.qrCodeEncoder.encodeAsBitmap());
            ((TextView) findViewById(C0224R.id.contents_text_view)).setText(this.qrCodeEncoder.getDisplayContents());
        } catch (WriterException e) {
            Log.e(TAG, "Could not encode barcode", e);
            showErrorMessage(C0224R.string.msg_encode_contents_failed);
            this.qrCodeEncoder = null;
        } catch (IllegalArgumentException e2) {
            Log.e(TAG, "Could not encode barcode", e2);
            showErrorMessage(C0224R.string.msg_encode_contents_failed);
            this.qrCodeEncoder = null;
        }
    }

    private void showErrorMessage(int message) {
        Builder builder = new Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton(C0224R.string.button_ok, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }
}
