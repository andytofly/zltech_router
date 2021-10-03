package com.zltech.zlrouter.testmodule1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zltech.testmodule1.R
import com.zltech.zlrouter.annotation.Route

//@Route(path = "/testmodule1/entry")
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
