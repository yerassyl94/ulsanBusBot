package com.google.zxing.client.android.result;

import android.app.Activity;
import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;
import com.google.zxing.client.result.ResultParser;

public final class ResultHandlerFactory {
    private ResultHandlerFactory() {
    }

    public static ResultHandler makeResultHandler(Activity activity, Result rawResult) {
        ParsedResult result = parseResult(rawResult);
        ParsedResultType type = result.getType();
        if (type.equals(ParsedResultType.ADDRESSBOOK)) {
            return new AddressBookResultHandler(activity, result);
        }
        if (type.equals(ParsedResultType.EMAIL_ADDRESS)) {
            return new EmailAddressResultHandler(activity, result);
        }
        if (type.equals(ParsedResultType.PRODUCT)) {
            return new ProductResultHandler(activity, result, rawResult);
        }
        if (type.equals(ParsedResultType.URI)) {
            return new URIResultHandler(activity, result);
        }
        if (type.equals(ParsedResultType.WIFI)) {
            return new WifiResultHandler(activity, result);
        }
        if (type.equals(ParsedResultType.TEXT)) {
            return new TextResultHandler(activity, result, rawResult);
        }
        if (type.equals(ParsedResultType.GEO)) {
            return new GeoResultHandler(activity, result);
        }
        if (type.equals(ParsedResultType.TEL)) {
            return new TelResultHandler(activity, result);
        }
        if (type.equals(ParsedResultType.SMS)) {
            return new SMSResultHandler(activity, result);
        }
        if (type.equals(ParsedResultType.CALENDAR)) {
            return new CalendarResultHandler(activity, result);
        }
        if (type.equals(ParsedResultType.ISBN)) {
            return new ISBNResultHandler(activity, result, rawResult);
        }
        return new TextResultHandler(activity, result, rawResult);
    }

    private static ParsedResult parseResult(Result rawResult) {
        return ResultParser.parseResult(rawResult);
    }
}
