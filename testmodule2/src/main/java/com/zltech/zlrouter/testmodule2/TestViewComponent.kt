package com.zltech.zlrouter.testmodule2

import android.util.Log
import com.zltech.zlrouter.annotation.Route
import com.zltech.zlrouter.inject.callback.OnResultCallback
import com.zltech.zlrouter.inject.template.AbsComponent
import com.zltech.zlrouter.inject.template.RtResult

@Route(path = "/module2/testView")
class TestViewComponent : AbsComponent() {
    override fun handle(callback: OnResultCallback?): RtResult {
        Log.d("TestViewComponent", "thread " + Thread.currentThread().toString())
        val result = RtResult()
        result.putResult("result", XzlTextView(mContext))
        return result
    }
}