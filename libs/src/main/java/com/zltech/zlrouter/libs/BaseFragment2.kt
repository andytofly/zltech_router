package com.zltech.zlrouter.libs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 *
 * @ClassName: TFragment
 * @Description: kotlin类作用描述
 * @Author: xulm
 * @CreateDate: 2021/6/2 15:28
 */
abstract class BaseFragment2 :Fragment(){
    private var isFirstLoad = true
    protected lateinit var mRootView:View
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
    }

    override fun onStart() {

        super.onStart()
    }

    override fun onAttach(context: Context) {

        super.onAttach(context)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mRootView = inflater.inflate(getLayoutId(), container, false)
        return mRootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSubs()
    }

    protected abstract fun getLayoutId(): Int

    protected abstract fun initSubs()

    override fun onResume() {

        super.onResume()
        if (isFirstLoad) {
            isFirstLoad = false

            loadData()
        }
    }

    /**
     * 延迟加载数据
     */
    abstract fun loadData()

    override fun onStop() {

        super.onStop()
    }

    override fun onPause() {

        super.onPause()
    }

    override fun onDestroy() {

        super.onDestroy()
    }

    override fun onDetach() {

        super.onDetach()
    }
}