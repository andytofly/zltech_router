package com.zltech.zlrouter.app;

import android.util.Log;

import androidx.annotation.Nullable;

import com.zltech.zlrouter.annotation.Route;
import com.zltech.zlrouter.inject.callback.OnResultCallback;
import com.zltech.zlrouter.inject.template.AbsComponent;
import com.zltech.zlrouter.inject.template.RtResult;

@Route(path = "/app/bb_com")
public class BBComponent extends AbsComponent {
    @Override
    public RtResult handle(@Nullable OnResultCallback callback) {
        Log.d("BBComponent", "thread " + Thread.currentThread().toString());
        int key1 = getParam("key1");
        int key2 = getParam("key2");
        RtResult result = new RtResult();
        result.putResult("result", key1 * 1.0f / key2 * 1.0f);
        if (callback != null) {
            callback.onResult(result);
        }
        return null;
    }
}
