package com.zltech.zlrouter.testmodule2;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.zltech.testmodule2.R;
import com.zltech.zlrouter.annotation.Route;

@Route(path = "/module2/module2main")
public class Module2MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module2_main);
    }
}
