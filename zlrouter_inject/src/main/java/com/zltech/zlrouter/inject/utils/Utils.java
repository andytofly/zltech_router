package com.zltech.zlrouter.inject.utils;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.logging.SimpleFormatter;

public class Utils {

    private static final String timeFormat1 = "HH:mm:ss.SSS";
    private static final SimpleDateFormat formatter1 = new SimpleDateFormat(timeFormat1);

    public static boolean isEmpty(String str){
        return str == null || str.equals("") || str.isEmpty();
    }

    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    public static boolean isEmpty(final Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static String formatTime1(){
        return formatter1.format(new Date());
    }
}
