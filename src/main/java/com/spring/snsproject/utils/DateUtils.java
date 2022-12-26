package com.spring.snsproject.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public final class DateUtils {
    public static String dateFormat(Timestamp date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        return format.format(date);
    }
}
