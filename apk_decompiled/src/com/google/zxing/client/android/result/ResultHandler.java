package com.google.zxing.client.android.result;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Contacts;
import android.util.Log;
import android.view.View;
import com.google.zxing.Result;
import com.google.zxing.client.android.C0224R;
import com.google.zxing.client.android.Contents;
import com.google.zxing.client.android.Intents.SearchBookContents;
import com.google.zxing.client.android.Intents.WifiConnect;
import com.google.zxing.client.android.LocaleManager;
import com.google.zxing.client.android.PreferencesActivity;
import com.google.zxing.client.android.book.SearchBookContentsActivity;
import com.google.zxing.client.android.share.Browser.BookmarkColumns;
import com.google.zxing.client.android.wifi.WifiActivity;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;
import com.google.zxing.client.result.WifiParsedResult;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public abstract class ResultHandler {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
    private static final String GOOGLE_SHOPPER_ACTIVITY = "com.google.android.apps.shopper.results.SearchResultsActivity";
    private static final String GOOGLE_SHOPPER_PACKAGE = "com.google.android.apps.shopper";
    private static final String MARKET_REFERRER_SUFFIX = "&referrer=utm_source%3Dbarcodescanner%26utm_medium%3Dapps%26utm_campaign%3Dscan";
    private static final String MARKET_URI_PREFIX = "market://search?q=pname:";
    public static final int MAX_BUTTON_COUNT = 4;
    private static final String TAG = ResultHandler.class.getSimpleName();
    private final Activity activity;
    private final String customProductSearch;
    private final Result rawResult;
    private final ParsedResult result;
    private final OnClickListener shopperMarketListener;

    /* renamed from: com.google.zxing.client.android.result.ResultHandler$1 */
    class C02321 implements OnClickListener {
        C02321() {
        }

        public void onClick(DialogInterface dialogInterface, int which) {
            ResultHandler.this.launchIntent(new Intent("android.intent.action.VIEW", Uri.parse("market://search?q=pname:com.google.android.apps.shopper&referrer=utm_source%3Dbarcodescanner%26utm_medium%3Dapps%26utm_campaign%3Dscan")));
        }
    }

    public abstract int getButtonCount();

    public abstract int getButtonText(int i);

    public abstract int getDisplayTitle();

    public abstract void handleButtonPress(int i);

    static {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    ResultHandler(Activity activity, ParsedResult result) {
        this(activity, result, null);
    }

    ResultHandler(Activity activity, ParsedResult result, Result rawResult) {
        this.shopperMarketListener = new C02321();
        this.result = result;
        this.activity = activity;
        this.rawResult = rawResult;
        this.customProductSearch = parseCustomSearchURL();
        activity.findViewById(C0224R.id.shopper_button).setVisibility(8);
    }

    public ParsedResult getResult() {
        return this.result;
    }

    boolean hasCustomProductSearch() {
        return this.customProductSearch != null;
    }

    public boolean areContentsSecure() {
        return false;
    }

    protected void showGoogleShopperButton(View.OnClickListener listener) {
        View shopperButton = this.activity.findViewById(C0224R.id.shopper_button);
        shopperButton.setVisibility(0);
        shopperButton.setOnClickListener(listener);
    }

    public CharSequence getDisplayContents() {
        return this.result.getDisplayResult().replace("\r", "");
    }

    public final ParsedResultType getType() {
        return this.result.getType();
    }

    final void addCalendarEvent(String summary, String start, String end, String location, String description) {
        Intent intent = new Intent("android.intent.action.EDIT");
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", calculateMilliseconds(start));
        if (start.length() == 8) {
            intent.putExtra("allDay", true);
        } else {
            if (end == null) {
                end = start;
            }
            intent.putExtra("endTime", calculateMilliseconds(end));
        }
        intent.putExtra(BookmarkColumns.TITLE, summary);
        intent.putExtra("eventLocation", location);
        intent.putExtra("description", description);
        launchIntent(intent);
    }

    private static long calculateMilliseconds(String when) {
        Date date;
        if (when.length() == 8) {
            synchronized (DATE_FORMAT) {
                date = DATE_FORMAT.parse(when, new ParsePosition(0));
            }
            return date.getTime();
        }
        synchronized (DATE_TIME_FORMAT) {
            date = DATE_TIME_FORMAT.parse(when.substring(0, 15), new ParsePosition(0));
        }
        long milliseconds = date.getTime();
        if (when.length() != 16 || when.charAt(15) != 'Z') {
            return milliseconds;
        }
        Calendar calendar = new GregorianCalendar();
        return milliseconds + ((long) (calendar.get(15) + calendar.get(16)));
    }

    final void addContact(String[] names, String[] phoneNumbers, String[] emails, String note, String address, String org, String title) {
        int length;
        int x;
        int i = 0;
        Intent intent = new Intent("android.intent.action.INSERT_OR_EDIT", Contacts.CONTENT_URI);
        intent.setType("vnd.android.cursor.item/person");
        putExtra(intent, "name", names != null ? names[0] : null);
        if (phoneNumbers != null) {
            length = phoneNumbers.length;
        } else {
            length = 0;
        }
        int phoneCount = Math.min(length, Contents.PHONE_KEYS.length);
        for (x = 0; x < phoneCount; x++) {
            putExtra(intent, Contents.PHONE_KEYS[x], phoneNumbers[x]);
        }
        if (emails != null) {
            i = emails.length;
        }
        int emailCount = Math.min(i, Contents.EMAIL_KEYS.length);
        for (x = 0; x < emailCount; x++) {
            putExtra(intent, Contents.EMAIL_KEYS[x], emails[x]);
        }
        putExtra(intent, "notes", note);
        putExtra(intent, "postal", address);
        putExtra(intent, "company", org);
        putExtra(intent, "job_title", title);
        launchIntent(intent);
    }

    final void shareByEmail(String contents) {
        sendEmailFromUri("mailto:", null, this.activity.getString(C0224R.string.msg_share_subject_line), contents);
    }

    final void sendEmail(String address, String subject, String body) {
        sendEmailFromUri("mailto:" + address, address, subject, body);
    }

    final void sendEmailFromUri(String uri, String email, String subject, String body) {
        Intent intent = new Intent("android.intent.action.SEND", Uri.parse(uri));
        if (email != null) {
            intent.putExtra("android.intent.extra.EMAIL", new String[]{email});
        }
        putExtra(intent, "android.intent.extra.SUBJECT", subject);
        putExtra(intent, "android.intent.extra.TEXT", body);
        intent.setType("text/plain");
        launchIntent(intent);
    }

    final void shareBySMS(String contents) {
        sendSMSFromUri("smsto:", this.activity.getString(C0224R.string.msg_share_subject_line) + ":\n" + contents);
    }

    final void sendSMS(String phoneNumber, String body) {
        sendSMSFromUri("smsto:" + phoneNumber, body);
    }

    final void sendSMSFromUri(String uri, String body) {
        Intent intent = new Intent("android.intent.action.SENDTO", Uri.parse(uri));
        putExtra(intent, "sms_body", body);
        intent.putExtra("compose_mode", true);
        launchIntent(intent);
    }

    final void sendMMS(String phoneNumber, String subject, String body) {
        sendMMSFromUri("mmsto:" + phoneNumber, subject, body);
    }

    final void sendMMSFromUri(String uri, String subject, String body) {
        Intent intent = new Intent("android.intent.action.SENDTO", Uri.parse(uri));
        if (subject == null || subject.length() == 0) {
            putExtra(intent, "subject", this.activity.getString(C0224R.string.msg_default_mms_subject));
        } else {
            putExtra(intent, "subject", subject);
        }
        putExtra(intent, "sms_body", body);
        intent.putExtra("compose_mode", true);
        launchIntent(intent);
    }

    final void dialPhone(String phoneNumber) {
        launchIntent(new Intent("android.intent.action.DIAL", Uri.parse("tel:" + phoneNumber)));
    }

    final void dialPhoneFromUri(String uri) {
        launchIntent(new Intent("android.intent.action.DIAL", Uri.parse(uri)));
    }

    final void openMap(String geoURI) {
        launchIntent(new Intent("android.intent.action.VIEW", Uri.parse(geoURI)));
    }

    final void searchMap(String address, String title) {
        String query = address;
        if (title != null && title.length() > 0) {
            query = query + " (" + title + ')';
        }
        launchIntent(new Intent("android.intent.action.VIEW", Uri.parse("geo:0,0?q=" + Uri.encode(query))));
    }

    final void getDirections(double latitude, double longitude) {
        launchIntent(new Intent("android.intent.action.VIEW", Uri.parse("http://maps.google." + LocaleManager.getCountryTLD() + "/maps?f=d&daddr=" + latitude + ',' + longitude)));
    }

    final void openProductSearch(String upc) {
        launchIntent(new Intent("android.intent.action.VIEW", Uri.parse("http://www.google." + LocaleManager.getProductSearchCountryTLD() + "/m/products?q=" + upc + "&source=zxing")));
    }

    final void openBookSearch(String isbn) {
        launchIntent(new Intent("android.intent.action.VIEW", Uri.parse("http://books.google." + LocaleManager.getBookSearchCountryTLD() + "/books?vid=isbn" + isbn)));
    }

    final void searchBookContents(String isbnOrUrl) {
        Intent intent = new Intent(SearchBookContents.ACTION);
        intent.setClassName(this.activity, SearchBookContentsActivity.class.getName());
        putExtra(intent, SearchBookContents.ISBN, isbnOrUrl);
        launchIntent(intent);
    }

    final void wifiConnect(WifiParsedResult wifiResult) {
        Intent intent = new Intent(WifiConnect.ACTION);
        intent.setClassName(this.activity, WifiActivity.class.getName());
        putExtra(intent, WifiConnect.SSID, wifiResult.getSsid());
        putExtra(intent, WifiConnect.TYPE, wifiResult.getNetworkEncryption());
        putExtra(intent, WifiConnect.PASSWORD, wifiResult.getPassword());
        launchIntent(intent);
    }

    final void openURL(String url) {
        launchIntent(new Intent("android.intent.action.VIEW", Uri.parse(url)));
    }

    final void webSearch(String query) {
        Intent intent = new Intent("android.intent.action.WEB_SEARCH");
        intent.putExtra("query", query);
        launchIntent(intent);
    }

    final void openGoogleShopper(String query) {
        try {
            this.activity.getPackageManager().getPackageInfo(GOOGLE_SHOPPER_PACKAGE, 0);
            Intent intent = new Intent("android.intent.action.SEARCH");
            intent.setClassName(GOOGLE_SHOPPER_PACKAGE, GOOGLE_SHOPPER_ACTIVITY);
            intent.putExtra("query", query);
            this.activity.startActivity(intent);
        } catch (NameNotFoundException e) {
            Builder builder = new Builder(this.activity);
            builder.setTitle(C0224R.string.msg_google_shopper_missing);
            builder.setMessage(C0224R.string.msg_install_google_shopper);
            builder.setIcon(C0224R.drawable.shopper_icon);
            builder.setPositiveButton(C0224R.string.button_ok, this.shopperMarketListener);
            builder.setNegativeButton(C0224R.string.button_cancel, null);
            builder.show();
        }
    }

    void launchIntent(Intent intent) {
        if (intent != null) {
            intent.addFlags(524288);
            Log.d(TAG, "Launching intent: " + intent + " with extras: " + intent.getExtras());
            try {
                this.activity.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Builder builder = new Builder(this.activity);
                builder.setTitle(C0224R.string.app_name);
                builder.setMessage(C0224R.string.msg_intent_failed);
                builder.setPositiveButton(C0224R.string.button_ok, null);
                builder.show();
            }
        }
    }

    private static void putExtra(Intent intent, String key, String value) {
        if (value != null && value.length() > 0) {
            intent.putExtra(key, value);
        }
    }

    protected void showNotOurResults(int index, OnClickListener proceedListener) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.activity);
        if (prefs.getBoolean(PreferencesActivity.KEY_NOT_OUR_RESULTS_SHOWN, false)) {
            proceedListener.onClick(null, index);
            return;
        }
        prefs.edit().putBoolean(PreferencesActivity.KEY_NOT_OUR_RESULTS_SHOWN, true).commit();
        Builder builder = new Builder(this.activity);
        builder.setMessage(C0224R.string.msg_not_our_results);
        builder.setPositiveButton(C0224R.string.button_ok, proceedListener);
        builder.show();
    }

    private String parseCustomSearchURL() {
        String customProductSearch = PreferenceManager.getDefaultSharedPreferences(this.activity).getString(PreferencesActivity.KEY_CUSTOM_PRODUCT_SEARCH, null);
        if (customProductSearch == null || customProductSearch.trim().length() != 0) {
            return customProductSearch;
        }
        return null;
    }

    String fillInCustomSearchURL(String text) {
        String url = this.customProductSearch.replace("%s", text);
        if (this.rawResult != null) {
            return url.replace("%f", this.rawResult.getBarcodeFormat().toString());
        }
        return url;
    }
}
