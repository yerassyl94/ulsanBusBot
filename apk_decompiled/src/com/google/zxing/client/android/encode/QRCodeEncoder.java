package com.google.zxing.client.android.encode;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.android.C0224R;
import com.google.zxing.client.android.Contents;
import com.google.zxing.client.android.Contents.Type;
import com.google.zxing.client.android.Intents.Encode;
import com.google.zxing.client.result.AddressBookParsedResult;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;
import com.google.zxing.common.BitMatrix;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

final class QRCodeEncoder {
    private static final int BLACK = -16777216;
    private static final String TAG = QRCodeEncoder.class.getSimpleName();
    private static final int WHITE = -1;
    private final Activity activity;
    private String contents;
    private final int dimension;
    private String displayContents;
    private BarcodeFormat format;
    private String title;

    QRCodeEncoder(Activity activity, Intent intent, int dimension) {
        this.activity = activity;
        if (intent == null) {
            throw new IllegalArgumentException("No valid data to encode.");
        }
        String action = intent.getAction();
        if (action.equals(Encode.ACTION)) {
            if (!encodeContentsFromZXingIntent(intent)) {
                throw new IllegalArgumentException("No valid data to encode.");
            }
        } else if (action.equals("android.intent.action.SEND") && !encodeContentsFromShareIntent(intent)) {
            throw new IllegalArgumentException("No valid data to encode.");
        }
        this.dimension = dimension;
    }

    public String getContents() {
        return this.contents;
    }

    public String getDisplayContents() {
        return this.displayContents;
    }

    public String getTitle() {
        return this.title;
    }

    private boolean encodeContentsFromZXingIntent(Intent intent) {
        try {
            this.format = BarcodeFormat.valueOf(intent.getStringExtra(Encode.FORMAT));
        } catch (IllegalArgumentException e) {
            this.format = null;
        }
        if (this.format == null || BarcodeFormat.QR_CODE.equals(this.format)) {
            String type = intent.getStringExtra(Encode.TYPE);
            if (type == null || type.length() == 0) {
                return false;
            }
            this.format = BarcodeFormat.QR_CODE;
            encodeQRCodeContents(intent, type);
        } else {
            String data = intent.getStringExtra(Encode.DATA);
            if (data != null && data.length() > 0) {
                this.contents = data;
                this.displayContents = data;
                this.title = this.activity.getString(C0224R.string.contents_text);
            }
        }
        if (this.contents == null || this.contents.length() <= 0) {
            return false;
        }
        return true;
    }

    private boolean encodeContentsFromShareIntent(Intent intent) {
        if (intent.hasExtra("android.intent.extra.TEXT")) {
            return encodeContentsFromShareIntentPlainText(intent);
        }
        return encodeContentsFromShareIntentDefault(intent);
    }

    private boolean encodeContentsFromShareIntentPlainText(Intent intent) {
        this.contents = intent.getStringExtra("android.intent.extra.TEXT");
        if (this.contents == null) {
            return false;
        }
        this.contents = this.contents.trim();
        if (this.contents.length() == 0) {
            return false;
        }
        this.format = BarcodeFormat.QR_CODE;
        if (intent.hasExtra("android.intent.extra.SUBJECT")) {
            this.displayContents = intent.getStringExtra("android.intent.extra.SUBJECT");
        } else if (intent.hasExtra("android.intent.extra.TITLE")) {
            this.displayContents = intent.getStringExtra("android.intent.extra.TITLE");
        } else {
            this.displayContents = this.contents;
        }
        this.title = this.activity.getString(C0224R.string.contents_text);
        return true;
    }

    private boolean encodeContentsFromShareIntentDefault(Intent intent) {
        this.format = BarcodeFormat.QR_CODE;
        try {
            InputStream stream = this.activity.getContentResolver().openInputStream((Uri) intent.getExtras().getParcelable("android.intent.extra.STREAM"));
            int length = stream.available();
            if (length <= 0) {
                Log.w(TAG, "Content stream is empty");
                return false;
            }
            byte[] vcard = new byte[length];
            int bytesRead = stream.read(vcard, 0, length);
            if (bytesRead < length) {
                Log.w(TAG, "Unable to fully read available bytes from content stream");
                return false;
            }
            String vcardString = new String(vcard, 0, bytesRead, "UTF-8");
            Log.d(TAG, "Encoding share intent content:");
            Log.d(TAG, vcardString);
            ParsedResult parsedResult = ResultParser.parseResult(new Result(vcardString, vcard, null, BarcodeFormat.QR_CODE));
            if (!(parsedResult instanceof AddressBookParsedResult)) {
                Log.d(TAG, "Result was not an address");
                return false;
            } else if (!encodeQRCodeContents((AddressBookParsedResult) parsedResult)) {
                Log.d(TAG, "Unable to encode contents");
                return false;
            } else if (this.contents == null || this.contents.length() <= 0) {
                return false;
            } else {
                return true;
            }
        } catch (IOException e) {
            Log.w(TAG, e);
            return false;
        } catch (NullPointerException e2) {
            Log.w(TAG, e2);
            return false;
        }
    }

    private void encodeQRCodeContents(Intent intent, String type) {
        String data;
        if (type.equals(Type.TEXT)) {
            data = intent.getStringExtra(Encode.DATA);
            if (data != null && data.length() > 0) {
                this.contents = data;
                this.displayContents = data;
                this.title = this.activity.getString(C0224R.string.contents_text);
            }
        } else if (type.equals(Type.EMAIL)) {
            data = trim(intent.getStringExtra(Encode.DATA));
            if (data != null) {
                this.contents = "mailto:" + data;
                this.displayContents = data;
                this.title = this.activity.getString(C0224R.string.contents_email);
            }
        } else if (type.equals(Type.PHONE)) {
            data = trim(intent.getStringExtra(Encode.DATA));
            if (data != null) {
                this.contents = "tel:" + data;
                this.displayContents = PhoneNumberUtils.formatNumber(data);
                this.title = this.activity.getString(C0224R.string.contents_phone);
            }
        } else if (type.equals(Type.SMS)) {
            data = trim(intent.getStringExtra(Encode.DATA));
            if (data != null) {
                this.contents = "sms:" + data;
                this.displayContents = PhoneNumberUtils.formatNumber(data);
                this.title = this.activity.getString(C0224R.string.contents_sms);
            }
        } else if (type.equals(Type.CONTACT)) {
            bundle = intent.getBundleExtra(Encode.DATA);
            if (bundle != null) {
                StringBuilder newContents = new StringBuilder(100);
                StringBuilder newDisplayContents = new StringBuilder(100);
                newContents.append("MECARD:");
                String name = trim(bundle.getString("name"));
                if (name != null) {
                    newContents.append("N:").append(escapeMECARD(name)).append(';');
                    newDisplayContents.append(name);
                }
                String address = trim(bundle.getString("postal"));
                if (address != null) {
                    newContents.append("ADR:").append(escapeMECARD(address)).append(';');
                    newDisplayContents.append('\n').append(address);
                }
                for (String string : Contents.PHONE_KEYS) {
                    String phone = trim(bundle.getString(string));
                    if (phone != null) {
                        newContents.append("TEL:").append(escapeMECARD(phone)).append(';');
                        newDisplayContents.append('\n').append(PhoneNumberUtils.formatNumber(phone));
                    }
                }
                for (String string2 : Contents.EMAIL_KEYS) {
                    String email = trim(bundle.getString(string2));
                    if (email != null) {
                        newContents.append("EMAIL:").append(escapeMECARD(email)).append(';');
                        newDisplayContents.append('\n').append(email);
                    }
                }
                if (newDisplayContents.length() > 0) {
                    newContents.append(';');
                    this.contents = newContents.toString();
                    this.displayContents = newDisplayContents.toString();
                    this.title = this.activity.getString(C0224R.string.contents_contact);
                    return;
                }
                this.contents = null;
                this.displayContents = null;
            }
        } else if (type.equals(Type.LOCATION)) {
            bundle = intent.getBundleExtra(Encode.DATA);
            if (bundle != null) {
                float latitude = bundle.getFloat("LAT", Float.MAX_VALUE);
                float longitude = bundle.getFloat("LONG", Float.MAX_VALUE);
                if (latitude != Float.MAX_VALUE && longitude != Float.MAX_VALUE) {
                    this.contents = "geo:" + latitude + ',' + longitude;
                    this.displayContents = latitude + "," + longitude;
                    this.title = this.activity.getString(C0224R.string.contents_location);
                }
            }
        }
    }

    private boolean encodeQRCodeContents(AddressBookParsedResult contact) {
        StringBuilder newContents = new StringBuilder(100);
        StringBuilder newDisplayContents = new StringBuilder(100);
        newContents.append("MECARD:");
        String[] names = contact.getNames();
        if (names != null && names.length > 0) {
            String name = trim(names[0]);
            if (name != null) {
                newContents.append("N:").append(escapeMECARD(name)).append(';');
                newDisplayContents.append(name);
            }
        }
        String[] addresses = contact.getAddresses();
        if (addresses != null) {
            for (String address : addresses) {
                String address2 = trim(address2);
                if (address2 != null) {
                    newContents.append("ADR:").append(escapeMECARD(address2)).append(';');
                    newDisplayContents.append('\n').append(address2);
                }
            }
        }
        String[] phoneNumbers = contact.getPhoneNumbers();
        if (phoneNumbers != null) {
            for (String phone : phoneNumbers) {
                String phone2 = trim(phone2);
                if (phone2 != null) {
                    newContents.append("TEL:").append(escapeMECARD(phone2)).append(';');
                    newDisplayContents.append('\n').append(PhoneNumberUtils.formatNumber(phone2));
                }
            }
        }
        String[] emails = contact.getEmails();
        if (emails != null) {
            for (String email : emails) {
                String email2 = trim(email2);
                if (email2 != null) {
                    newContents.append("EMAIL:").append(escapeMECARD(email2)).append(';');
                    newDisplayContents.append('\n').append(email2);
                }
            }
        }
        String url = trim(contact.getURL());
        if (url != null) {
            newContents.append("URL:").append(escapeMECARD(url)).append(';');
            newDisplayContents.append('\n').append(url);
        }
        if (newDisplayContents.length() > 0) {
            newContents.append(';');
            this.contents = newContents.toString();
            this.displayContents = newDisplayContents.toString();
            this.title = this.activity.getString(C0224R.string.contents_contact);
            return true;
        }
        this.contents = null;
        this.displayContents = null;
        return false;
    }

    Bitmap encodeAsBitmap() throws WriterException {
        Hashtable<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(this.contents);
        if (encoding != null) {
            hints = new Hashtable(2);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        BitMatrix result = new MultiFormatWriter().encode(this.contents, this.format, this.dimension, this.dimension, hints);
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[(width * height)];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? -16777216 : -1;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 'ÿ') {
                return "UTF-8";
            }
        }
        return null;
    }

    private static String trim(String s) {
        if (s == null) {
            return null;
        }
        s = s.trim();
        if (s.length() == 0) {
            s = null;
        }
        return s;
    }

    private static String escapeMECARD(String input) {
        if (input == null) {
            return input;
        }
        if (input.indexOf(58) < 0 && input.indexOf(59) < 0) {
            return input;
        }
        int length = input.length();
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);
            if (c == ':' || c == ';') {
                result.append('\\');
            }
            result.append(c);
        }
        return result.toString();
    }
}
