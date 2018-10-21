package com.google.zxing.client.android.result;

import android.app.Activity;
import com.google.zxing.client.android.C0224R;
import com.google.zxing.client.result.CalendarParsedResult;
import com.google.zxing.client.result.ParsedResult;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public final class CalendarResultHandler extends ResultHandler {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
    private static final int[] buttons = new int[]{C0224R.string.button_add_calendar};

    public CalendarResultHandler(Activity activity, ParsedResult result) {
        super(activity, result);
    }

    public int getButtonCount() {
        return buttons.length;
    }

    public int getButtonText(int index) {
        return buttons[index];
    }

    public void handleButtonPress(int index) {
        CalendarParsedResult calendarResult = (CalendarParsedResult) getResult();
        if (index == 0) {
            addCalendarEvent(calendarResult.getSummary(), calendarResult.getStart(), calendarResult.getEnd(), calendarResult.getLocation(), calendarResult.getDescription());
        }
    }

    public CharSequence getDisplayContents() {
        CalendarParsedResult calResult = (CalendarParsedResult) getResult();
        StringBuffer result = new StringBuffer(100);
        ParsedResult.maybeAppend(calResult.getSummary(), result);
        appendTime(calResult.getStart(), result);
        String endString = calResult.getEnd();
        if (endString == null) {
            endString = calResult.getStart();
        }
        appendTime(endString, result);
        ParsedResult.maybeAppend(calResult.getLocation(), result);
        ParsedResult.maybeAppend(calResult.getAttendee(), result);
        ParsedResult.maybeAppend(calResult.getDescription(), result);
        return result.toString();
    }

    private static void appendTime(String when, StringBuffer result) {
        Date date;
        if (when.length() == 8) {
            synchronized (DATE_FORMAT) {
                date = DATE_FORMAT.parse(when, new ParsePosition(0));
            }
            ParsedResult.maybeAppend(DateFormat.getDateInstance().format(Long.valueOf(date.getTime())), result);
            return;
        }
        synchronized (DATE_TIME_FORMAT) {
            date = DATE_TIME_FORMAT.parse(when.substring(0, 15), new ParsePosition(0));
        }
        long milliseconds = date.getTime();
        if (when.length() == 16 && when.charAt(15) == 'Z') {
            Calendar calendar = new GregorianCalendar();
            milliseconds += (long) (calendar.get(15) + calendar.get(16));
        }
        ParsedResult.maybeAppend(DateFormat.getDateTimeInstance().format(Long.valueOf(milliseconds)), result);
    }

    public int getDisplayTitle() {
        return C0224R.string.result_calendar;
    }
}
