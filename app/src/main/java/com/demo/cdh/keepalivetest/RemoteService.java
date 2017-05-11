package com.demo.cdh.keepalivetest;

import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class RemoteService extends Service {

    private static final String TAG = RemoteService.class.getSimpleName();

    private RemoteServiceBinder remoteBinder;
    private RemoteServiceConnection remoteConn;

    public RemoteService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "RemoteService onCreate");
        if(remoteBinder == null)
            remoteBinder = new RemoteServiceBinder();
        remoteConn = new RemoteServiceConnection();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startTimer();
        //设置成前景service
        Notification notification = new Notification.Builder(this).build();
        startForeground(0, notification);
        stopForeground(true);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return remoteBinder;
    }

    class RemoteServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "RemoteService onServiceDisconnected");
            //远程服务断开的时候启动本地服务
            startService(new Intent(RemoteService.this, LocalService.class));
            bindService(new Intent(RemoteService.this, LocalService.class), remoteConn, Context.BIND_IMPORTANT);
        }
    }

    class RemoteServiceBinder extends IDaemonAidlInterface.Stub {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }
    }

    private Timer timer;
    private TimerTask timerTask;
    long counter = 0;

    public void startTimer() {
        timer = new Timer();
        initTimerTask();
        timer.schedule(timerTask, 1000, 1000);
    }

    public void initTimerTask() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.e(TAG, "RemoteService=====:"+(counter++));
            }
        };
    }
}
