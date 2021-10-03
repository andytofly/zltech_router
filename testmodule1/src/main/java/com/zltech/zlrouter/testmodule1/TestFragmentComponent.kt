package com.zltech.zlrouter.testmodule1

import android.util.Log
import com.zltech.zlrouter.annotation.Route
import com.zltech.zlrouter.inject.callback.OnResultCallback
import com.zltech.zlrouter.inject.template.AbsComponent
import com.zltech.zlrouter.inject.template.RtResult

@Route(path = "/module1/testFragment")
class TestFragmentComponent : AbsComponent() {
    override fun handle(callback: OnResultCallback?): RtResult {
        Log.d("TestFragmentComponent", "thread " + Thread.currentThread().toString())

        val param = getParam<String>("key")
        Log.d("TestFragmentComponent",param)

        val result = RtResult()
        result.putResult("result", TestFragment())
        return result
    }
}