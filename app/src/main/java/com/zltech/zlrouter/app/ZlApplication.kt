package com.zltech.zlrouter.app

import android.app.Application
import com.zltech.zlrouter.inject.BuildConfig
import com.zltech.zlrouter.inject.ZlRouter

class ZlApplication :Application(){
    override fun onCreate() {
        super.onCreate()
        // 注意，如果设置isDebug = true，那么每次都会从dexFile加载，非常耗时间，仅用于调试；所以在调式模式，也要根据实际情况设置该参数;
        if(BuildConfig.DEBUG){
            ZlRouter.init(true,this)
        }else{
            ZlRouter.init(false,this)
        }
    }


}