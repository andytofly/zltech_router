package com.zltech.zlrouter.inject

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import com.zltech.zlrouter.annotation.RouteMeta
import com.zltech.zlrouter.inject.callback.OnResultCallback
import com.zltech.zlrouter.inject.template.AbsComponent
import com.zltech.zlrouter.inject.template.RtResult
import java.io.Serializable
import java.lang.ref.WeakReference
import java.util.*

class JumpCard @JvmOverloads constructor(path: String?, group: String?, bundle: Bundle? = null) :
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


    private var mContext: Context? = null

    fun withContext(context: Context): JumpCard {
        mContext = context
        return this
    }

    /**
     * Intent.FLAG_ACTIVITY**
     *
     * @param flag
     * @return
     */
    fun withFlags(flag: Int): JumpCard {
        flags = flag
        return this
    }

    fun withString(key: String?, value: String?): JumpCard {
        extras.putString(key, value)
        return this
    }

    fun withBoolean(key: String?, value: Boolean): JumpCard {
        extras.putBoolean(key, value)
        return this
    }

    fun withShort(key: String?, value: Short): JumpCard {
        extras.putShort(key, value)
        return this
    }

    fun withInt(key: String?, value: Int): JumpCard {
        extras.putInt(key, value)
        return this
    }

    fun withLong(key: String?, value: Long): JumpCard {
        extras.putLong(key, value)
        return this
    }

    fun withDouble(key: String?, value: Double): JumpCard {
        extras.putDouble(key, value)
        return this
    }

    fun withByte(key: String?, value: Byte): JumpCard {
        extras.putByte(key, value)
        return this
    }

    fun withChar(key: String?, value: Char): JumpCard {
        extras.putChar(key, value)
        return this
    }

    fun withFloat(key: String?, value: Float): JumpCard {
        extras.putFloat(key, value)
        return this
    }

    fun withSerializable(key: String?, value: Serializable): JumpCard {
        extras.putSerializable(key, value)
        return this
    }

    fun withParcelable(key: String?, value: Parcelable?): JumpCard {
        extras.putParcelable(key, value)
        return this
    }

    fun withStringArray(key: String?, value: Array<String?>?): JumpCard {
        extras.putStringArray(key, value)
        return this
    }

    fun withBooleanArray(key: String?, value: BooleanArray?): JumpCard {
        extras.putBooleanArray(key, value)
        return this
    }

    fun withShortArray(key: String?, value: ShortArray?): JumpCard {
        extras.putShortArray(key, value)
        return this
    }

    fun withIntArray(key: String?, value: IntArray?): JumpCard {
        extras.putIntArray(key, value)
        return this
    }

    fun withLongArray(key: String?, value: LongArray?): JumpCard {
        extras.putLongArray(key, value)
        return this
    }

    fun withDoubleArray(key: String?, value: DoubleArray?): JumpCard {
        extras.putDoubleArray(key, value)
        return this
    }

    fun withByteArray(key: String?, value: ByteArray?): JumpCard {
        extras.putByteArray(key, value)
        return this
    }

    fun withCharArray(key: String?, value: CharArray?): JumpCard {
        extras.putCharArray(key, value)
        return this
    }

    fun withFloatArray(key: String?, value: FloatArray?): JumpCard {
        extras.putFloatArray(key, value)
        return this
    }

    fun withParcelableArray(key: String?, value: Array<Parcelable?>?): JumpCard {
        extras.putParcelableArray(key, value)
        return this
    }

    fun withParcelableArrayList(key: String?, value: ArrayList<out Parcelable?>?): JumpCard {
        extras.putParcelableArrayList(key, value)
        return this
    }

    fun withIntegerArrayList(key: String?, value: ArrayList<Int?>?): JumpCard {
        extras.putIntegerArrayList(key, value)
        return this
    }

    fun withStringArrayList(key: String?, value: ArrayList<String?>?): JumpCard {
        extras.putStringArrayList(key, value)
        return this
    }


    /***
     * 调用AbsComponent专属方法
     * @return
     */
    @JvmOverloads
    fun call(callback: OnResultCallback? = null): RtResult? {
        return ZlRouter.getInstance()
            .dispatch(mContext, this, AbsComponent.InvokingType.Sync, callback)
    }

    @JvmOverloads
    fun callAsync(callback: OnResultCallback? = null) {
        ZlRouter.getInstance().dispatch(mContext, this, AbsComponent.InvokingType.Async, callback)
    }

    @JvmOverloads
    fun callOnMainThread(callback: OnResultCallback? = null) {
        ZlRouter.getInstance()
            .dispatch(mContext, this, AbsComponent.InvokingType.MainThread, callback)
    }

    init {
        setPath(path)
        setGroup(group)
        extras = bundle ?: Bundle()
    }
}