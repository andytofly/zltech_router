package com.zltech.zlrouter.testmodule1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zltech.testmodule1.R;
import com.zltech.zlrouter.annotation.Route;
import com.zltech.zlrouter.inject.ZlRouter;
import com.zltech.zlrouter.inject.template.RtResult;

@Route(path = "/module1/module1main")
public class Module1MainActivity extends AppCompatActivity {


    Button buttonCall;
    Button buttonCallAsync;
    Button buttonCallMainThread;
    Button buttonFinish;

    private static final String TAG = "Module1MainActivity ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module1_main);
//        ZlRouter.getInstance().inject(this);
//        Toast.makeText(this, "msg=" + msg, Toast.LENGTH_SHORT).show();

        String subMsg = getIntent().getStringExtra("msg");
        Toast.makeText(this, "subMsg=" + subMsg, Toast.LENGTH_SHORT).show();

        Log.d(TAG,"~onCreate");

        buttonCall = findViewById(R.id.buttonCall);
        buttonCall.setOnClickListener(v -> {
            RtResult call = ZlRouter.getInstance().build("/module2/plusServer")
                    .withInt("key1",68)
                    .withInt("key2",134)
                    .call();
            Toast.makeText(Module1MainActivity.this, "plus=" + call.getResult("result"), Toast.LENGTH_SHORT).show();
        });

        buttonCallAsync = findViewById(R.id.buttonCallAsync);
        buttonCallAsync.setOnClickListener(v -> {
            ZlRouter.getInstance().build("/app/aa_com")
                    .withInt("key1",134)
                    .withInt("key2",35)
                    .callAsync(result -> {
                        Log.d(TAG,"thread~"+Thread.currentThread()+":"+result.getResult("result"));

                    });
        });

        buttonCallMainThread = findViewById(R.id.buttonCallMainThread);
        buttonCallMainThread.setOnClickListener(v -> {
            ZlRouter.getInstance().build("/app/bb_com")
                    .withInt("key1",134)
                    .withInt("key2",35)
                    .callOnMainThread(result -> {
                        Log.d(TAG,"thread~"+Thread.currentThread()+":"+result.getResult("result"));

                    });
        });

        buttonFinish = findViewById(R.id.buttonFinish);
        buttonFinish.setOnClickListener(v->{
//            Intent intent = new Intent();
//            intent.putExtra("back","123aaabbb");
//            setResult(1002,intent);
            RtResult result = new RtResult();
            result.putResult("back_value", "123aaabbb啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊.....");
            finish();
            ZlRouter.getInstance().getCallback(Module1MainActivity.this).onResult(result);
        });
    }
}
