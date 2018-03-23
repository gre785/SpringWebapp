
package com.fh.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil
{
    private final static SimpleDateFormat SDF_YEAR = new SimpleDateFormat("yyyy");

    private final static SimpleDateFormat SDF_DAY = new SimpleDateFormat("yyyy-MM-dd");

    private final static SimpleDateFormat SDF_DAYS = new SimpleDateFormat("yyyyMMdd");

    private final static SimpleDateFormat SDF_TIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getYear()
    {
        return SDF_YEAR.format(new Date());
    }

    public static String getDay()
    {
        return SDF_DAY.format(new Date());
    }

    public static String getDays()
    {
        return SDF_DAYS.format(new Date());
    }

    public static String getTime()
    {
        return SDF_TIME.format(new Date());
    }

    public static boolean compareDate(String s, String e)
    {
        if (fomatDate(s) == null || fomatDate(e) == null) {
            return false;
        }
        return fomatDate(s).getTime() >= fomatDate(e).getTime();
    }

    public static Date fomatDate(String date)
    {
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return fmt.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isValidDate(String s)
    {
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        try {
            fmt.parse(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static int getDiffYear(String startTime, String endTime)
    {
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        try {
            long aa = 0;
            int years = (int)(((fmt.parse(endTime).getTime() - fmt.parse(startTime).getTime()) / (1000 * 60 * 60 * 24)) / 365);
            return years;
        } catch (Exception e) {
            return 0;
        }
    }

    public static long getDaySub(String beginDateStr, String endDateStr)
    {
        long day = 0;
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.util.Date beginDate = null;
        java.util.Date endDate = null;

        try {
            beginDate = format.parse(beginDateStr);
            endDate = format.parse(endDateStr);
            day = (endDate.getTime() - beginDate.getTime()) / (24 * 60 * 60 * 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day;
    }

    public static String getAfterDayDate(String days)
    {
        int daysInt = Integer.parseInt(days);

        Calendar canlendar = Calendar.getInstance();
        canlendar.add(Calendar.DATE, daysInt);
        Date date = canlendar.getTime();
        SimpleDateFormat sdfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = sdfd.format(date);
        return dateStr;
    }

    public static String getAfterDayWeek(String days)
    {
        int daysInt = Integer.parseInt(days);

        Calendar canlendar = Calendar.getInstance();
        canlendar.add(Calendar.DATE, daysInt);
        Date date = canlendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("E");
        String dateStr = sdf.format(date);
        return dateStr;
    }

    public static void main(String[] args)
    {
        System.out.println(getDays());
        System.out.println(getAfterDayWeek("3"));
    }

}
