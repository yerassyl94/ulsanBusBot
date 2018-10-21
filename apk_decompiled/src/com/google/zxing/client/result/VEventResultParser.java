package com.google.zxing.client.result;

import com.google.zxing.Result;

final class VEventResultParser extends ResultParser {
    private VEventResultParser() {
    }

    public static CalendarParsedResult parse(Result result) {
        String rawText = result.getText();
        if (rawText == null) {
            return null;
        }
        if (rawText.indexOf("BEGIN:VEVENT") < 0) {
            return null;
        }
        double latitude;
        double longitude;
        String summary = VCardResultParser.matchSingleVCardPrefixedField("SUMMARY", rawText, true);
        String start = VCardResultParser.matchSingleVCardPrefixedField("DTSTART", rawText, true);
        String end = VCardResultParser.matchSingleVCardPrefixedField("DTEND", rawText, true);
        String location = VCardResultParser.matchSingleVCardPrefixedField("LOCATION", rawText, true);
        String description = VCardResultParser.matchSingleVCardPrefixedField("DESCRIPTION", rawText, true);
        String geoString = VCardResultParser.matchSingleVCardPrefixedField("GEO", rawText, true);
        if (geoString == null) {
            latitude = Double.NaN;
            longitude = Double.NaN;
        } else {
            int semicolon = geoString.indexOf(59);
            try {
                latitude = Double.parseDouble(geoString.substring(0, semicolon));
                longitude = Double.parseDouble(geoString.substring(semicolon + 1));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        try {
            return new CalendarParsedResult(summary, start, end, location, null, description, latitude, longitude);
        } catch (IllegalArgumentException e2) {
            return null;
        }
    }
}
