package com.zltech.zlrouter.inject.utils;

import android.util.Log;

public class LogUtil {
    public static boolean logFlg = false;
    public static void setLog(boolean flg){
        logFlg = flg;
    }

    public static void fd(String tag, String msg){
        if(logFlg){
            Log.d(tag,msg);
        }
    }

    public static void d(String tag, String msg){
        Log.d(tag,msg);
    }

    public static void e(String tag, String msg){
        Log.e(tag,msg);
    }

    public static void e(String tag, String msg,Exception e){
        Log.e(tag,msg);
    }
}
