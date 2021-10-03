package com.zltech.zlrouter.inject.template;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.zltech.zlrouter.inject.callback.OnResultCallback;
import com.zltech.zlrouter.inject.thread.DefaultPoolExecutor;
import com.zltech.zlrouter.inject.utils.HandlerUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 区别于Activity以外的服务对象, 调用方式，有3种
 */
public abstract class AbsComponent {

    public enum InvokingType{
        Sync,
        Async,
        MainThread
    }

    protected Bundle params;

    public void setParams(Bundle _params){
        params = _params;
    }

    protected <T> T getParam(String key){
        Object param = params.get(key);
        if(param != null){
            return (T)param;
        }
        return null;
    }

    protected Context mContext;
    public void setContext(Context context){
        mContext = context;
    }


    /**
     * 同步调用
     * @return
     */
    public RtResult call() {
        return handle(null);
    }


    /**
     * 子线程调用
     */
    public void callAsync(final OnResultCallback callback) {
        DefaultPoolExecutor.executor.execute(new Runnable() {
            @Override
            public void run() {
                handle(callback);
            }
        });
    }

    /**
     * 主线程调用
     */
    public void callOnMainThread(final OnResultCallback callback) {
        HandlerUtil.handler.post(new Runnable() {
            @Override
            public void run() {
                handle(callback);
            }
        });
    }

    /**
     *
     * @param callback 如果是异步调用，callAsync或者 callOnMainThread，需要用callback返回结果，同步调用，直接return结果
     * @return
     */
    public abstract RtResult handle(@Nullable final OnResultCallback callback);
}
