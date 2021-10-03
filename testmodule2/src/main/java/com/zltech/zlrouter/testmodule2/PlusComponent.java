package com.zltech.zlrouter.testmodule2;

import android.util.Log;

import androidx.annotation.Nullable;

import com.zltech.zlrouter.annotation.Route;
import com.zltech.zlrouter.inject.callback.OnResultCallback;
import com.zltech.zlrouter.inject.template.AbsComponent;
import com.zltech.zlrouter.inject.template.RtResult;

@Route(path = "/module2/plusServer")
public class PlusComponent extends AbsComponent {
    @Override
    public RtResult handle(@Nullable OnResultCallback callback) {
        Log.d("PlusComponent","thread "+Thread.currentThread().toString());
        int key1 = getParam("key1");
        int key2 = getParam("key2");
        RtResult result = new RtResult();
        result.putResult("result",key1+key2);
        return result;
    }
}
