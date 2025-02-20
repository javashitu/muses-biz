package com.muses.common.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * @ClassName DateTimeUtils
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/7 9:59
 */
public class DateTimeUtils {
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public static String getNowDateTime() {
        Date now = new Date();
        return formatter.format(now);
    }

    public static long currentTime() {
        return System.currentTimeMillis();
    }

    public static String formatAsDate(long millionTime){
        Date now = new Date(millionTime);
        return formatter.format(now);
    }

    public static long getTodayMilliSecond(){
        LocalDate localDate = LocalDate.now();
        //有大坑，UTC时间转换出来是从每天早上8点
        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

}
