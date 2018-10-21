package com.google.zxing.client.android.result;

import android.app.Activity;
import android.telephony.PhoneNumberUtils;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import com.google.zxing.client.android.C0224R;
import com.google.zxing.client.result.AddressBookParsedResult;
import com.google.zxing.client.result.ParsedResult;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class AddressBookResultHandler extends ResultHandler {
    private static final DateFormat[] DATE_FORMATS = new DateFormat[]{new SimpleDateFormat("yyyyMMdd"), new SimpleDateFormat("yyyyMMdd'T'HHmmss"), new SimpleDateFormat("yyyy-MM-dd"), new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")};
    private int buttonCount;
    private final boolean[] fields;

    private int mapIndexToAction(int index) {
        if (index < this.buttonCount) {
            int count = -1;
            for (int x = 0; x < 4; x++) {
                if (this.fields[x]) {
                    count++;
                }
                if (count == index) {
                    return x;
                }
            }
        }
        return -1;
    }

    public AddressBookResultHandler(Activity activity, ParsedResult result) {
        boolean hasAddress;
        boolean hasPhoneNumber;
        boolean hasEmailAddress;
        super(activity, result);
        AddressBookParsedResult addressResult = (AddressBookParsedResult) result;
        String[] addresses = addressResult.getAddresses();
        if (addresses == null || addresses.length <= 0 || addresses[0].length() <= 0) {
            hasAddress = false;
        } else {
            hasAddress = true;
        }
        String[] phoneNumbers = addressResult.getPhoneNumbers();
        if (phoneNumbers == null || phoneNumbers.length <= 0) {
            hasPhoneNumber = false;
        } else {
            hasPhoneNumber = true;
        }
        String[] emails = addressResult.getEmails();
        if (emails == null || emails.length <= 0) {
            hasEmailAddress = false;
        } else {
            hasEmailAddress = true;
        }
        this.fields = new boolean[4];
        this.fields[0] = true;
        this.fields[1] = hasAddress;
        this.fields[2] = hasPhoneNumber;
        this.fields[3] = hasEmailAddress;
        this.buttonCount = 0;
        for (int x = 0; x < 4; x++) {
            if (this.fields[x]) {
                this.buttonCount++;
            }
        }
    }

    public int getButtonCount() {
        return this.buttonCount;
    }

    public int getButtonText(int index) {
        switch (mapIndexToAction(index)) {
            case 0:
                return C0224R.string.button_add_contact;
            case 1:
                return C0224R.string.button_show_map;
            case 2:
                return C0224R.string.button_dial;
            case 3:
                return C0224R.string.button_email;
            default:
                throw new ArrayIndexOutOfBoundsException();
        }
    }

    public void handleButtonPress(int index) {
        AddressBookParsedResult addressResult = (AddressBookParsedResult) getResult();
        String[] addresses = addressResult.getAddresses();
        String address1 = (addresses == null || addresses.length < 1) ? null : addresses[0];
        switch (mapIndexToAction(index)) {
            case 0:
                addContact(addressResult.getNames(), addressResult.getPhoneNumbers(), addressResult.getEmails(), addressResult.getNote(), address1, addressResult.getOrg(), addressResult.getTitle());
                return;
            case 1:
                String title;
                String[] names = addressResult.getNames();
                if (names != null) {
                    title = names[0];
                } else {
                    title = null;
                }
                searchMap(address1, title);
                return;
            case 2:
                dialPhone(addressResult.getPhoneNumbers()[0]);
                return;
            case 3:
                sendEmail(addressResult.getEmails()[0], null, null);
                return;
            default:
                return;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.util.Date parseDate(java.lang.String r7) {
        /*
        r2 = 0;
        r3 = DATE_FORMATS;
        r4 = r3.length;
    L_0x0004:
        if (r2 >= r4) goto L_0x0022;
    L_0x0006:
        r0 = r3[r2];
        monitor-enter(r0);
        r5 = 0;
        r0.setLenient(r5);	 Catch:{ all -> 0x001f }
        r5 = new java.text.ParsePosition;	 Catch:{ all -> 0x001f }
        r6 = 0;
        r5.<init>(r6);	 Catch:{ all -> 0x001f }
        r1 = r0.parse(r7, r5);	 Catch:{ all -> 0x001f }
        if (r1 == 0) goto L_0x001b;
    L_0x0019:
        monitor-exit(r0);	 Catch:{ all -> 0x001f }
    L_0x001a:
        return r1;
    L_0x001b:
        monitor-exit(r0);	 Catch:{ all -> 0x001f }
        r2 = r2 + 1;
        goto L_0x0004;
    L_0x001f:
        r2 = move-exception;
        monitor-exit(r0);	 Catch:{ all -> 0x001f }
        throw r2;
    L_0x0022:
        r1 = 0;
        goto L_0x001a;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.client.android.result.AddressBookResultHandler.parseDate(java.lang.String):java.util.Date");
    }

    public CharSequence getDisplayContents() {
        AddressBookParsedResult result = (AddressBookParsedResult) getResult();
        StringBuffer contents = new StringBuffer(100);
        ParsedResult.maybeAppend(result.getNames(), contents);
        int namesLength = contents.length();
        String pronunciation = result.getPronunciation();
        if (pronunciation != null && pronunciation.length() > 0) {
            contents.append("\n(");
            contents.append(pronunciation);
            contents.append(')');
        }
        ParsedResult.maybeAppend(result.getTitle(), contents);
        ParsedResult.maybeAppend(result.getOrg(), contents);
        ParsedResult.maybeAppend(result.getAddresses(), contents);
        String[] numbers = result.getPhoneNumbers();
        if (numbers != null) {
            for (String number : numbers) {
                ParsedResult.maybeAppend(PhoneNumberUtils.formatNumber(number), contents);
            }
        }
        ParsedResult.maybeAppend(result.getEmails(), contents);
        ParsedResult.maybeAppend(result.getURL(), contents);
        String birthday = result.getBirthday();
        if (birthday != null && birthday.length() > 0) {
            Date date = parseDate(birthday);
            if (date != null) {
                ParsedResult.maybeAppend(DateFormat.getDateInstance().format(Long.valueOf(date.getTime())), contents);
            }
        }
        ParsedResult.maybeAppend(result.getNote(), contents);
        if (namesLength <= 0) {
            return contents.toString();
        }
        Spannable styled = new SpannableString(contents.toString());
        styled.setSpan(new StyleSpan(1), 0, namesLength, 0);
        return styled;
    }

    public int getDisplayTitle() {
        return C0224R.string.result_address_book;
    }
}
