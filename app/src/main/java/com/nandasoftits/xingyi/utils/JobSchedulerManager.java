package com.nandasoftits.xingyi.utils;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import com.nandasoftits.xingyi.service.PushJobService;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class JobSchedulerManager {

    private static final String LOG_TAG = "JobSchedulerManager";

    private static final int JOB_ID = 1;
    private static JobSchedulerManager mJobManager;
    private JobScheduler mJobScheduler;
    private static Context mContext;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private JobSchedulerManager(Context ctxt) {
        this.mContext = ctxt;
        mJobScheduler = (JobScheduler) ctxt.getSystemService(Context.JOB_SCHEDULER_SERVICE);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public final static JobSchedulerManager getJobSchedulerInstance(Context ctxt) {
        if (mJobManager == null) {
            mJobManager = new JobSchedulerManager(ctxt);
        }
        return mJobManager;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void startJobScheduler() {
        // 如果JobService已经启动或API<21，返回
        if (PushJobService.isJobServiceAlive() || CommonUtils.isBelowLOLLIPOP()) {
            return;
        }
        // 构建JobInfo对象，传递给JobSchedulerService
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, new ComponentName(mContext, PushJobService.class));
        // 设置每3秒执行一下任务
        builder.setPeriodic(60000);
        // 设置设备重启时，执行该任务
        builder.setPersisted(true);
        // 当插入充电器，执行该任务
        builder.setRequiresCharging(true);
        JobInfo info = builder.build();
        //开始定时执行该系统任务
        mJobScheduler.schedule(info);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void stopJobScheduler() {
        if (CommonUtils.isBelowLOLLIPOP()) {
            return;
        }
        mJobScheduler.cancelAll();
    }
}
