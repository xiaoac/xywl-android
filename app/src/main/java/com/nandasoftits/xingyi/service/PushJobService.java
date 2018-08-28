package com.nandasoftits.xingyi.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import com.nandasoftits.xingyi.utils.CommonUtils;
import com.nandasoftits.xingyi.utils.Constant;
import com.nandasoftits.xingyi.utils.Logger;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class PushJobService extends JobService {
    private final static String TAG = "PushJobService";
    // 告知编译器，这个变量不能被优化
    private volatile static Service mKeepAliveService = null;

    public static boolean isJobServiceAlive() {
        return mKeepAliveService != null;
    }

    private static final int MESSAGE_ID_TASK = 0x01;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            // 具体任务逻辑
            if (CommonUtils.isAppAlive(getApplicationContext(), Constant.APP_PAGENAME)) {
                Logger.d(TAG, "app is alive");
            } else {
                Intent intent = new Intent(getApplicationContext(), JPushActivateService.class);
                startService(intent);
            }
            // 通知系统任务执行结束
            jobFinished((JobParameters) msg.obj, false);
            return true;
        }
    });

    @Override
    public boolean onStartJob(JobParameters params) {
        Logger.d(TAG, "JobService服务被启动...");
        mKeepAliveService = this;
        // 返回false，系统假设这个方法返回时任务已经执行完毕；
        // 返回true，系统假定这个任务正要被执行
        Message msg = Message.obtain(mHandler, MESSAGE_ID_TASK, params);
        mHandler.sendMessage(msg);
        return true;
    }


    @Override
    public boolean onStopJob(JobParameters params) {
        mHandler.removeMessages(MESSAGE_ID_TASK);
        Logger.d(TAG, "KeepAliveService----->JobService服务被关闭");
        return false;
    }

}
