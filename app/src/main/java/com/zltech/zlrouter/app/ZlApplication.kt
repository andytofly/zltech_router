package com.zltech.zlrouter.app

import android.app.Application
import android.util.Log
import com.zltech.zlrouter.inject.BuildConfig
import com.zltech.zlrouter.inject.ZlRouter
import com.zltech.zlrouter.inject.utils.LogUtil

class ZlApplication :Application(){
    override fun onCreate() {
        super.onCreate()
        // 注意，如果设置isDebug = true，那么每次都会从dexFile加载，非常耗时间，仅用于调试；所以在调式模式，也要根据实际情况设置该参数;
        if(BuildConfig.DEBUG){
            ZlRouter.init(true,this){
                LogUtil.d("ZlApplication","DEBUG 初始化完毕的回调")
            }
        }else{
            ZlRouter.init(false,this){
                LogUtil.d("ZlApplication","RELEASE 初始化完毕的回调")
            }
        }
    }


}