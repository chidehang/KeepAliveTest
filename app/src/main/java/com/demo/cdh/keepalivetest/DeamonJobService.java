package com.demo.cdh.keepalivetest;

import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

/**
 * Created by hang on 2017/5/11.
 */

public class DeamonJobService extends JobService {

    private static final String TAG = DeamonJobService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "DeamonJobService onStartCommand");
        scheduleJob();
        return START_NOT_STICKY;
    }

    private void scheduleJob() {
        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        scheduler.schedule(getJobInfo());
    }

    private JobInfo getJobInfo() {
        JobInfo job = new JobInfo.Builder(1, new ComponentName(this, DeamonJobService.class))
                .setPersisted(true) //设置持续存在
                .setPeriodic(100)   //设置调用周期
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)   //设置网络环境为任意网络
                .setRequiresCharging(false) //充电环境
                .setRequiresDeviceIdle(false)   //设备空闲
                .build();
        return job;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        boolean isLocalAlive = isServiceAlive("com.demo.cdh.keepalivetest.LocalService");
        boolean isRemoteAlive = isServiceAlive("com.demo.cdh.keepalivetest.RemoteService");
        if(!isLocalAlive || !isRemoteAlive) {
            startService(new Intent(this, LocalService.class));
            startService(new Intent(this, RemoteService.class));
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        scheduleJob();
        return true;
    }

    public boolean isServiceAlive(String serviceName) {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> list = am.getRunningServices(128);
        for (ActivityManager.RunningServiceInfo item : list) {
            if(serviceName.equals(item.service.getClassName().toString()))
                return true;
        }
        return false;
    }
}
