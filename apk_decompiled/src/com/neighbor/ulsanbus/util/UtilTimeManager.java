package com.neighbor.ulsanbus.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UtilTimeManager {
    static double KILOMETERS = 0.001d;
    static double METERS = 1.0d;
    String fromUnit;
    String toUnit;

    public static String getZeroString(int i, int len) {
        String s = Integer.toString(i);
        int gap = len - s.length();
        for (int j = 0; j < gap; j++) {
            s = "0" + s;
        }
        return s;
    }

    public static String getZeroString(long i, int len) {
        String s = Long.toString(i);
        int gap = len - s.length();
        for (int j = 0; j < gap; j++) {
            s = "0" + s;
        }
        return s;
    }

    public static int getHour(long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timeStamp));
        return calendar.get(11);
    }

    public static int getMinute(long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timeStamp));
        return calendar.get(12);
    }

    public static int getDate(long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        StringBuffer buffer = new StringBuffer();
        calendar.setTime(new Date(timeStamp));
        buffer.append(getZeroString(calendar.get(5), 2));
        return Integer.parseInt(buffer.toString());
    }

    public static int getMonth(long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        StringBuffer buffer = new StringBuffer();
        calendar.setTime(new Date(timeStamp));
        buffer.append(getZeroString(calendar.get(2) + 1, 2));
        return Integer.parseInt(buffer.toString());
    }

    public static String getTimeInString(long timeStamp) {
        StringBuffer buffer = new StringBuffer();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timeStamp));
        buffer.append(getZeroString(calendar.get(1), 4));
        buffer.append(getZeroString(calendar.get(2) + 1, 2));
        buffer.append(getZeroString(calendar.get(5), 2));
        buffer.append(getZeroString(calendar.get(11), 2));
        buffer.append(getZeroString(calendar.get(12), 2));
        buffer.append(getZeroString(calendar.get(13), 2));
        return buffer.toString();
    }

    public static int getDayOfWeek() {
        return Calendar.getInstance().get(7);
    }

    public static String getCurHourMin() {
        return new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREAN).format(new Date(System.currentTimeMillis())).substring(8, 12);
    }

    public static int getIntDayOfWeek() {
        long time = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.KOREAN);
        return new Date(time).getDay();
    }

    public static String convertTimeSearchList(long milli) {
        long temptimeminute = (System.currentTimeMillis() - milli) / 1000;
        if (temptimeminute < 60) {
            return "1분미만";
        }
        if (temptimeminute < 3600) {
            return (temptimeminute / 60) + "분 전";
        }
        if (temptimeminute < 86400) {
            return (temptimeminute / 3600) + "시간 전";
        }
        if (temptimeminute < 2505600) {
            return (temptimeminute / 86400) + "일 전";
        }
        return "1달 이상";
    }

    public static String convertTimeArrival(int remaintime, String flag) {
        String endtext;
        if (flag.equals("arrival")) {
            endtext = " 후  도착";
        } else {
            endtext = " 소요";
        }
        if (remaintime < 61) {
            if (remaintime == 0) {
                return "운행 종료";
            }
            return "1분 이내 도착";
        } else if (remaintime < 3601) {
            return Integer.toString(remaintime / 60) + " 분 " + endtext;
        } else {
            return "약 " + Integer.toString(remaintime / 3600) + " 시간 소요";
        }
    }

    public static String convertfromMeters(double meters) {
        return new DecimalFormat(".##").format((double) ((float) (meters / 1000.0d)));
    }
}
