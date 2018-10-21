package com.google.zxing.client.android;

import android.support.v4.app.NotificationCompat;

public final class Contents {
    public static final String[] EMAIL_KEYS = new String[]{NotificationCompat.CATEGORY_EMAIL, "secondary_email", "tertiary_email"};
    public static final String[] PHONE_KEYS = new String[]{"phone", "secondary_phone", "tertiary_phone"};

    public static final class Type {
        public static final String CONTACT = "CONTACT_TYPE";
        public static final String EMAIL = "EMAIL_TYPE";
        public static final String LOCATION = "LOCATION_TYPE";
        public static final String PHONE = "PHONE_TYPE";
        public static final String SMS = "SMS_TYPE";
        public static final String TEXT = "TEXT_TYPE";

        private Type() {
        }
    }

    private Contents() {
    }
}
