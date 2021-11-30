package com.zltech.zlrouter.inject.utils;

import android.util.Log;

import com.zltech.zlrouter.inject.BuildConfig;

public class LogUtil {
    public static void fd(String tag, String msg){
        if(BuildConfig.DEBUG){
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
