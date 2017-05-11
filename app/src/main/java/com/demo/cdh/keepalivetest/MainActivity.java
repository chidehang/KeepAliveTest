package com.demo.cdh.keepalivetest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //启动两个服务(位于不同进程)
        startService(new Intent(this, LocalService.class));
        startService(new Intent(this, RemoteService.class));

        startService(new Intent(this, DeamonJobService.class));
    }
}
