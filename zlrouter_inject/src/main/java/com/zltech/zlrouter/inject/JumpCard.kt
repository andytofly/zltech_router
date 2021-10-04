package com.zltech.zlrouter.inject

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import com.zltech.zlrouter.annotation.RouteMeta
import com.zltech.zlrouter.inject.callback.OnResultCallback
import com.zltech.zlrouter.inject.template.AbsComponent
import com.zltech.zlrouter.inject.template.RtResult
import java.util.*

class Jumper @JvmOverloads constructor(path: String?, group: String?, bundle: Bundle? = null) :
    RouteMeta() {
    val extras: Bundle
    var flags = -1
        private set

    //新版风格
    private val optionsCompat: Bundle? = null

    //服务
    var component: AbsComponent? = null
        private set

    fun setService(service: AbsComponent?) {
        component = service
    }

    /**
     * Intent.FLAG_ACTIVITY**
     *
     * @param flag
     * @return
     */
    fun withFlags(flag: Int): Jumper {
        flags = flag
        return this
    }

    fun withString(key: String?, value: String?): Jumper {
        extras.putString(key, value)
        return this
    }

    fun withBoolean(key: String?, value: Boolean): Jumper {
        extras.putBoolean(key, value)
        return this
    }

    fun withShort(key: String?, value: Short): Jumper {
        extras.putShort(key, value)
        return this
    }

    fun withInt(key: String?, value: Int): Jumper {
        extras.putInt(key, value)
        return this
    }

    fun withLong(key: String?, value: Long): Jumper {
        extras.putLong(key, value)
        return this
    }

    fun withDouble(key: String?, value: Double): Jumper {
        extras.putDouble(key, value)
        return this
    }

    fun withByte(key: String?, value: Byte): Jumper {
        extras.putByte(key, value)
        return this
    }

    fun withChar(key: String?, value: Char): Jumper {
        extras.putChar(key, value)
        return this
    }

    fun withFloat(key: String?, value: Float): Jumper {
        extras.putFloat(key, value)
        return this
    }

    fun withParcelable(key: String?, value: Parcelable?): Jumper {
        extras.putParcelable(key, value)
        return this
    }

    fun withStringArray(key: String?, value: Array<String?>?): Jumper {
        extras.putStringArray(key, value)
        return this
    }

    fun withBooleanArray(key: String?, value: BooleanArray?): Jumper {
        extras.putBooleanArray(key, value)
        return this
    }

    fun withShortArray(key: String?, value: ShortArray?): Jumper {
        extras.putShortArray(key, value)
        return this
    }

    fun withIntArray(key: String?, value: IntArray?): Jumper {
        extras.putIntArray(key, value)
        return this
    }

    fun withLongArray(key: String?, value: LongArray?): Jumper {
        extras.putLongArray(key, value)
        return this
    }

    fun withDoubleArray(key: String?, value: DoubleArray?): Jumper {
        extras.putDoubleArray(key, value)
        return this
    }

    fun withByteArray(key: String?, value: ByteArray?): Jumper {
        extras.putByteArray(key, value)
        return this
    }

    fun withCharArray(key: String?, value: CharArray?): Jumper {
        extras.putCharArray(key, value)
        return this
    }

    fun withFloatArray(key: String?, value: FloatArray?): Jumper {
        extras.putFloatArray(key, value)
        return this
    }

    fun withParcelableArray(key: String?, value: Array<Parcelable?>?): Jumper {
        extras.putParcelableArray(key, value)
        return this
    }

    fun withParcelableArrayList(key: String?, value: ArrayList<out Parcelable?>?): Jumper {
        extras.putParcelableArrayList(key, value)
        return this
    }

    fun withIntegerArrayList(key: String?, value: ArrayList<Int?>?): Jumper {
        extras.putIntegerArrayList(key, value)
        return this
    }

    fun withStringArrayList(key: String?, value: ArrayList<String?>?): Jumper {
        extras.putStringArrayList(key, value)
        return this
    }
    /**
     * Activity跳转专属方法
     */
    /**
     * Activity跳转专属方法
     */
    /**
     * Activity跳转专属方法
     */
    @JvmOverloads
    fun navigate(context: Context? = null, requestCode: Int = -1) {
//        if(this.getType() != Type.ACTIVITY){
//            throw new RuntimeException("服务请调用call方法");
//        }
        ZlRouter.getInstance().navigate(context, this, requestCode)
    }

    /***
     * 调用AbsComponent专属方法
     * @return
     */
    @JvmOverloads
    fun call(callback: OnResultCallback? = null): RtResult? {
//        if(this.getType() != Type.COMPONENT){
//            throw new RuntimeException("跳转Activity请调用navigate方法");
//        }
        return ZlRouter.getInstance().call(this, AbsComponent.InvokingType.Sync, callback)
    }

    @JvmOverloads
    fun callAsync(callback: OnResultCallback? = null) {
//        if(this.getType() != Type.COMPONENT){
//            throw new RuntimeException("跳转Activity请调用navigate方法");
//        }
        ZlRouter.getInstance().call(this, AbsComponent.InvokingType.Async, callback)
    }

    @JvmOverloads
    fun callOnMainThread(callback: OnResultCallback? = null) {
//        if(this.getType() != Type.COMPONENT){
//            throw new RuntimeException("跳转Activity请调用navigate方法");
//        }
        ZlRouter.getInstance().call(this, AbsComponent.InvokingType.MainThread, callback)
    }

    init {
        setPath(path)
        setGroup(group)
        extras = bundle ?: Bundle()
    }
}