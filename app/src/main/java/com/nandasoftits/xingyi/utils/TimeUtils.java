package com.nandasoftits.xingyi.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimeUtils {

    private static final long MINUTE_5 = 5 * 60 * 1000;

    private static final long HOUR_1 = 60 * 60 * 1000;

    private static final long DAY_1 = 24 * HOUR_1;

    private static final long DAY_7 = 7 * DAY_1;

    private static final String TIME_FORMAT = "yyyy年MM月dd日 HH时mm分";

    private static SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);

    public static String getTimeStr(long time) {
        return sdf.format(new Date(time));
    }

    public static long getEstimaedTime() {
        Calendar calendar = GregorianCalendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        Calendar retCal = GregorianCalendar.getInstance();
        retCal.setLenient(true);
        retCal.set(year, month, day + 1, 10, 0);
        return retCal.getTimeInMillis();
    }

    public static String getPreviousTimeString(long time) {
        if (time > System.currentTimeMillis()) {
            return "未知";
        }
        long difference = System.currentTimeMillis() - time;
        if (difference < MINUTE_5) {
            return "刚刚";
        }
        if (difference < HOUR_1) {
            return difference / (60 * 1000) + "分钟前";
        }
        if (difference < DAY_1) {
            return difference / HOUR_1 + "小时前";
        }
        if (difference < DAY_7) {
            return difference / DAY_1 + "天前";
        }
        return getTimeStr(time);
    }

}
