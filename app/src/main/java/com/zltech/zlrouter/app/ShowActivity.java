package com.zltech.zlrouter.app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.zltech.zlrouter.R;
import com.zltech.zlrouter.annotation.Route;


@Route(path = "/show/info")
public class ShowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
    }
}
