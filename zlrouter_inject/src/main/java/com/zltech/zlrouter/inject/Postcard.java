package com.zltech.zlrouter.inject;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;


import androidx.annotation.Nullable;

import com.zltech.zlrouter.annotation.RouteMeta;
import com.zltech.zlrouter.inject.callback.OnResultCallback;
import com.zltech.zlrouter.inject.template.AbsComponent;
import com.zltech.zlrouter.inject.template.RtResult;

import java.util.ArrayList;


public class Postcard extends RouteMeta {
    private Bundle mBundle;
    private int flags = -1;
    //新版风格
    private Bundle optionsCompat;

    //服务
    private AbsComponent service;

    public Postcard(String path, String group) {
        this(path, group, null);
    }

    public Postcard(String path, String group, Bundle bundle) {
        setPath(path);
        setGroup(group);
        this.mBundle = (null == bundle ? new Bundle() : bundle);
    }

    public Bundle getExtras() {
        return mBundle;
    }

    public AbsComponent getComponent() {
        return service;
    }

    public void setService(AbsComponent service) {
        this.service = service;
    }

    /**
     * Intent.FLAG_ACTIVITY**
     *
     * @param flag
     * @return
     */
    public Postcard withFlags(int flag) {
        this.flags = flag;
        return this;
    }

    public int getFlags() {
        return flags;
    }


    public Postcard withString(@Nullable String key, @Nullable String value) {
        mBundle.putString(key, value);
        return this;
    }


    public Postcard withBoolean(@Nullable String key, boolean value) {
        mBundle.putBoolean(key, value);
        return this;
    }


    public Postcard withShort(@Nullable String key, short value) {
        mBundle.putShort(key, value);
        return this;
    }


    public Postcard withInt(@Nullable String key, int value) {
        mBundle.putInt(key, value);
        return this;
    }


    public Postcard withLong(@Nullable String key, long value) {
        mBundle.putLong(key, value);
        return this;
    }


    public Postcard withDouble(@Nullable String key, double value) {
        mBundle.putDouble(key, value);
        return this;
    }


    public Postcard withByte(@Nullable String key, byte value) {
        mBundle.putByte(key, value);
        return this;
    }


    public Postcard withChar(@Nullable String key, char value) {
        mBundle.putChar(key, value);
        return this;
    }


    public Postcard withFloat(@Nullable String key, float value) {
        mBundle.putFloat(key, value);
        return this;
    }


    public Postcard withParcelable(@Nullable String key, @Nullable Parcelable value) {
        mBundle.putParcelable(key, value);
        return this;
    }


    public Postcard withStringArray(@Nullable String key, @Nullable String[] value) {
        mBundle.putStringArray(key, value);
        return this;
    }


    public Postcard withBooleanArray(@Nullable String key, boolean[] value) {
        mBundle.putBooleanArray(key, value);
        return this;
    }


    public Postcard withShortArray(@Nullable String key, short[] value) {
        mBundle.putShortArray(key, value);
        return this;
    }


    public Postcard withIntArray(@Nullable String key, int[] value) {
        mBundle.putIntArray(key, value);
        return this;
    }


    public Postcard withLongArray(@Nullable String key, long[] value) {
        mBundle.putLongArray(key, value);
        return this;
    }


    public Postcard withDoubleArray(@Nullable String key, double[] value) {
        mBundle.putDoubleArray(key, value);
        return this;
    }


    public Postcard withByteArray(@Nullable String key, byte[] value) {
        mBundle.putByteArray(key, value);
        return this;
    }


    public Postcard withCharArray(@Nullable String key, char[] value) {
        mBundle.putCharArray(key, value);
        return this;
    }


    public Postcard withFloatArray(@Nullable String key, float[] value) {
        mBundle.putFloatArray(key, value);
        return this;
    }


    public Postcard withParcelableArray(@Nullable String key, @Nullable Parcelable[] value) {
        mBundle.putParcelableArray(key, value);
        return this;
    }

    public Postcard withParcelableArrayList(@Nullable String key, @Nullable ArrayList<? extends
            Parcelable> value) {
        mBundle.putParcelableArrayList(key, value);
        return this;
    }

    public Postcard withIntegerArrayList(@Nullable String key, @Nullable ArrayList<Integer> value) {
        mBundle.putIntegerArrayList(key, value);
        return this;
    }

    public Postcard withStringArrayList(@Nullable String key, @Nullable ArrayList<String> value) {
        mBundle.putStringArrayList(key, value);
        return this;
    }

    /**
     * Activity跳转专属方法
     */
    public void navigate() {
//        if(this.getType() != Type.ACTIVITY){
//            throw new RuntimeException("服务请调用call方法");
//        }
        this.navigate(null);
    }

    /**
     * Activity跳转专属方法
     */
    public void navigate(Context context) {
//        if(this.getType() != Type.ACTIVITY){
//            throw new RuntimeException("服务请调用call方法");
//        }
        this.navigate(context,-1);
    }

    /**
     * Activity跳转专属方法
     */
    public void navigate(Context context, int requestCode) {
//        if(this.getType() != Type.ACTIVITY){
//            throw new RuntimeException("服务请调用call方法");
//        }
        ZlRouter.getInstance().navigate(context, this, requestCode);
    }

    /***
     * 调用AbsComponent专属方法
     * @return
     */
    public RtResult call() {
//        if(this.getType() != Type.COMPONENT){
//            throw new RuntimeException("跳转Activity请调用navigate方法");
//        }
        return this.call(null);
    }

    public RtResult call(OnResultCallback callback) {
//        if(this.getType() != Type.COMPONENT){
//            throw new RuntimeException("跳转Activity请调用navigate方法");
//        }
        return ZlRouter.getInstance().call(this, AbsComponent.InvokingType.Sync, callback);
    }

    public void callAsync() {
//        if(this.getType() != Type.COMPONENT){
//            throw new RuntimeException("跳转Activity请调用navigate方法");
//        }
        this.callAsync(null);
    }

    public void callAsync(OnResultCallback callback) {
//        if(this.getType() != Type.COMPONENT){
//            throw new RuntimeException("跳转Activity请调用navigate方法");
//        }
        ZlRouter.getInstance().call(this, AbsComponent.InvokingType.Async, callback);
    }

    public void callOnMainThread() {
//        if(this.getType() != Type.COMPONENT){
//            throw new RuntimeException("跳转Activity请调用navigate方法");
//        }
        this.callOnMainThread(null);
    }

    public void callOnMainThread(OnResultCallback callback) {
//        if(this.getType() != Type.COMPONENT){
//            throw new RuntimeException("跳转Activity请调用navigate方法");
//        }
        ZlRouter.getInstance().call(this, AbsComponent.InvokingType.MainThread, callback);
    }
}
