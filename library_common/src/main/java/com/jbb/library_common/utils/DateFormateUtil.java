package com.jbb.library_common.utils;

import android.text.TextUtils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 类名:DateFormateUtil
 * 描述:日期格式类
 */
public class DateFormateUtil {

    /**
     * 获取今天是周几
     *
     * @return
     */
    public static String getCurrentDayOfWeek() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getDayOfWeek(calendar);
    }

    /**
     * 根据日历获取周几
     *
     * @param calendar
     * @return
     */
    public static String getDayOfWeek(Calendar calendar) {
        String weekString = "";
        final String dayNames[] = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        weekString = dayNames[dayOfWeek - 1];
        return weekString;
    }

    public static String getDayOfWeek(String dateStr) {
        if (TextUtils.isEmpty(dateStr)) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            SimpleDateFormat dateFormt = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            try {
                date = dateFormt.parse(dateStr);
            } catch (ParseException e1) {
            }
        }
        if (date == null) {
            return "";
        }
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.setTimeInMillis(date.getTime());
        return getDayOfWeek(tempCalendar);
    }


    /**
     * 描述:将字符串时间转换成时间戳
     * 作者:nn
     * 时间:2016/1/18 21:50
     * 版本:3.1.6
     */
    public static long time2Stamp(String time) {
        String format = "yyyy-MM-dd HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = new Date();
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            return 0;
        }

        return date.getTime();
    }

    /**
     * 描述:获取当前格式化时间
     * 作者:nn
     * 时间:2016/1/24 13:41
     * 版本:3.1.6
     */
    public static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(System.currentTimeMillis());
        return dateString;
    }

    /**
     * 描述:得到当前日期
     * 作者:nn
     * 时间:2016/5/18 18:04
     * 版本:3.2.3
     */
    public static String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(System.currentTimeMillis());
        return dateString;
    }

    /**
     * 描述:得到日期
     * 作者:nn
     * 时间:2016/5/18 18:04
     * 版本:3.2.3
     */
    public static String getCurrentDate(String time) {
        String dateString = "";
        SimpleDateFormat initFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = initFormat.parse(time);
            dateString = formatter.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }


    /**
     * 描述:得到日期
     * 作者:nn
     * time  : 时间毫秒
     * 时间:2016/5/18 18:04
     * 版本:3.2.3
     */
    public static String getCurrentDate2(String time) {
        String dateString = "";
        try {
            long timel = Long.parseLong(time);
            Date date = new Date(timel);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            dateString = formatter.format(date);
        } catch (Exception e) {
            return "";
        }

        return dateString;
    }


    /**
     * 描述:得到日期
     * 作者:nn
     * time  : 时间毫秒
     * 时间:2016/5/18 18:04
     * 版本:3.2.3
     */
    public static String getCurrentDate(long time) {
        String dateString = "";

        Date date = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateString = formatter.format(date);
        return dateString;
    }


    /**
     * 描述:得到日期
     * 作者:nn
     * time  : 时间毫秒
     * 时间:2016/5/18 18:04
     * 版本:3.2.3
     */
    public static String formatDate(long time, SimpleDateFormat format) {
        String dateString = "";

        Date date = new Date(time);
        dateString = format.format(date);
        return dateString;
    }


    /**
     * 描述:得到日期
     * 作者:nn
     * time  : 时间毫秒
     * 时间:2016/5/18 18:04
     * 版本:3.2.3
     */
    public static String formatTime(String time, SimpleDateFormat format, SimpleDateFormat format2) {
        try {
            Date date = format.parse(time);
            return format2.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }

    }


    /**
     * @param time    要格式的时间
     * @param lastStr 添加到末尾的字符串
     * @return
     */
    public static String getTimeStr(long time, String lastStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
        SimpleDateFormat format3 = new SimpleDateFormat("MM月dd日");

        Date lastDate = new Date(time);
        String today = format.format(new Date());
        String last = format.format(lastDate);

        StringBuffer sb = new StringBuffer();
        if (today.equals(last)) {//今天
            sb.append("今天");
            sb.append(format2.format(lastDate));
        } else {
            sb.append(format3.format(lastDate));
        }
        sb.append(lastStr);

        return sb.toString();
    }


    public static Date getLongTime(String endDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date endData = format.parse(endDate);
            return endData;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    //
    public static ArrayList<String> getMonthBetween(Date minDate, Date maxDate) {
        ArrayList<String> result = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");//格式化为年月
        result.add("全部");
        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();

        try {
            min.setTime(minDate);
            min.set(min.get(Calendar.YEAR), 1, 1);

            max.setTime(maxDate);
            max.set(max.get(Calendar.YEAR), 12, 30);

            Calendar curr = max;
            while (curr.after(min)) {
                result.add(sdf.format(curr.getTime()));
                curr.add(Calendar.YEAR, -1);
            }
        } catch (Exception e) {
            return null;
        }
        return result;
    }


    public static ArrayList<String> getMonthBetween(String minDate, String maxDate) {
        ArrayList<String> result = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");//格式化为年月
        result.add("全部");
        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();

        try {
            min.setTime(sdf.parse(minDate));
            min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);

            max.setTime(sdf.parse(maxDate));
            max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

            Calendar curr = max;
            while (curr.after(min)) {
                result.add(sdf.format(curr.getTime()));
                curr.add(Calendar.MONTH, -1);
            }
        } catch (Exception e) {
            return null;
        }
        return result;
    }


}
