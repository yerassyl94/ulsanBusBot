package com.google.zxing.client.android;

import android.content.Intent;
import android.net.Uri;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.android.Intents.Scan;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

final class DecodeFormatManager {
    private static final Pattern COMMA_PATTERN = Pattern.compile(",");
    static final Vector<BarcodeFormat> DATA_MATRIX_FORMATS = new Vector(1);
    static final Vector<BarcodeFormat> ONE_D_FORMATS = new Vector(PRODUCT_FORMATS.size() + 4);
    static final Vector<BarcodeFormat> PRODUCT_FORMATS = new Vector(5);
    static final Vector<BarcodeFormat> QR_CODE_FORMATS = new Vector(1);

    static {
        PRODUCT_FORMATS.add(BarcodeFormat.UPC_A);
        PRODUCT_FORMATS.add(BarcodeFormat.UPC_E);
        PRODUCT_FORMATS.add(BarcodeFormat.EAN_13);
        PRODUCT_FORMATS.add(BarcodeFormat.EAN_8);
        PRODUCT_FORMATS.add(BarcodeFormat.RSS_14);
        ONE_D_FORMATS.addAll(PRODUCT_FORMATS);
        ONE_D_FORMATS.add(BarcodeFormat.CODE_39);
        ONE_D_FORMATS.add(BarcodeFormat.CODE_93);
        ONE_D_FORMATS.add(BarcodeFormat.CODE_128);
        ONE_D_FORMATS.add(BarcodeFormat.ITF);
        QR_CODE_FORMATS.add(BarcodeFormat.QR_CODE);
        DATA_MATRIX_FORMATS.add(BarcodeFormat.DATA_MATRIX);
    }

    private DecodeFormatManager() {
    }

    static Vector<BarcodeFormat> parseDecodeFormats(Intent intent) {
        List<String> scanFormats = null;
        String scanFormatsString = intent.getStringExtra(Scan.FORMATS);
        if (scanFormatsString != null) {
            scanFormats = Arrays.asList(COMMA_PATTERN.split(scanFormatsString));
        }
        return parseDecodeFormats(scanFormats, intent.getStringExtra(Scan.MODE));
    }

    static Vector<BarcodeFormat> parseDecodeFormats(Uri inputUri) {
        List<String> formats = inputUri.getQueryParameters(Scan.FORMATS);
        if (!(formats == null || formats.size() != 1 || formats.get(0) == null)) {
            formats = Arrays.asList(COMMA_PATTERN.split((CharSequence) formats.get(0)));
        }
        return parseDecodeFormats(formats, inputUri.getQueryParameter(Scan.MODE));
    }

    private static Vector<BarcodeFormat> parseDecodeFormats(Iterable<String> scanFormats, String decodeMode) {
        if (scanFormats != null) {
            Vector<BarcodeFormat> vector = new Vector();
            try {
                for (String format : scanFormats) {
                    vector.add(BarcodeFormat.valueOf(format));
                }
                return vector;
            } catch (IllegalArgumentException e) {
            }
        }
        if (decodeMode != null) {
            if (Scan.PRODUCT_MODE.equals(decodeMode)) {
                return PRODUCT_FORMATS;
            }
            if (Scan.QR_CODE_MODE.equals(decodeMode)) {
                return QR_CODE_FORMATS;
            }
            if (Scan.DATA_MATRIX_MODE.equals(decodeMode)) {
                return DATA_MATRIX_FORMATS;
            }
            if (Scan.ONE_D_MODE.equals(decodeMode)) {
                return ONE_D_FORMATS;
            }
        }
        return null;
    }
}
