package com.zltech.zlrouter.testmodule1

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.zltech.zlrouter.libs.BaseFragment2

class TestFragment :BaseFragment2(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getLayoutId(): Int {
        return 0
    }

    override fun initSubs() {

    }

    override fun loadData() {

    }


}