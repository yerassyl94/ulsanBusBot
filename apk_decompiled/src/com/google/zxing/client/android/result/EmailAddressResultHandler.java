package com.google.zxing.client.android.result;

import android.app.Activity;
import com.google.zxing.client.android.C0224R;
import com.google.zxing.client.result.EmailAddressParsedResult;
import com.google.zxing.client.result.ParsedResult;

public final class EmailAddressResultHandler extends ResultHandler {
    private static final int[] buttons = new int[]{C0224R.string.button_email, C0224R.string.button_add_contact};

    public EmailAddressResultHandler(Activity activity, ParsedResult result) {
        super(activity, result);
    }

    public int getButtonCount() {
        return buttons.length;
    }

    public int getButtonText(int index) {
        return buttons[index];
    }

    public void handleButtonPress(int index) {
        EmailAddressParsedResult emailResult = (EmailAddressParsedResult) getResult();
        switch (index) {
            case 0:
                sendEmailFromUri(emailResult.getMailtoURI(), emailResult.getEmailAddress(), emailResult.getSubject(), emailResult.getBody());
                return;
            case 1:
                addContact(null, null, new String[]{emailResult.getEmailAddress()}, null, null, null, null);
                return;
            default:
                return;
        }
    }

    public int getDisplayTitle() {
        return C0224R.string.result_email_address;
    }
}
