package com.zltech.zlrouter.app

import android.app.Application
import com.zltech.zlrouter.inject.ZlRouter

class ZlApplication :Application(){
    override fun onCreate() {
        super.onCreate()
        ZlRouter.init(this);
    }


}