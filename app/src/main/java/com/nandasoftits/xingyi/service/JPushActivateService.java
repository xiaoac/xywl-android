package com.nandasoftits.xingyi.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import cn.jpush.android.api.JPushInterface;
import com.nandasoftits.xingyi.R;
import com.nandasoftits.xingyi.activity.WelcomeActivity;
import com.nandasoftits.xingyi.sharedperferences.UserSharedPreferences;
import com.nandasoftits.xingyi.utils.Logger;
import com.nandasoftits.xingyi.utils.PushUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class JPushActivateService extends Service {

    private static final String LOG_TAG = "JPushActivateService";

    private final static int GRAY_SERVICE_ID = 1001;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(LOG_TAG, "onStartCommand");
        if (Build.VERSION.SDK_INT < 18) {
            startForeground(GRAY_SERVICE_ID, new Notification());//API < 18 ，此方法能有效隐藏Notification上的图标
            PushUtils.initTestJPush(this.getApplicationContext());
        } else {
            Intent innerIntent = new Intent(this, GrayInnerService.class);
            startService(innerIntent);
            startForeground(GRAY_SERVICE_ID, new Notification());
        }
        //将service设置为前台service
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Logger.d(LOG_TAG, "onBind");
        return null;
    }

    //不做任何事情，只为启动application 从而开启推送
    @Override
    public void onCreate() {
        super.onCreate();
        Logger.d(LOG_TAG, "onCreate");
    }

}
