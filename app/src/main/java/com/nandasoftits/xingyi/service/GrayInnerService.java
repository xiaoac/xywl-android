package com.nandasoftits.xingyi.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.nandasoftits.xingyi.utils.Logger;
import com.nandasoftits.xingyi.utils.PushUtils;

public class GrayInnerService extends Service {

    private static final String LOG_TAG = "GrayInnerService";

    private final static int GRAY_SERVICE_ID = 1001;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(LOG_TAG, "onStartCommand");
        startForeground(GRAY_SERVICE_ID, new Notification());
        stopForeground(true);
        stopSelf();
        PushUtils.initJPush(this.getApplicationContext());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Logger.d(LOG_TAG, "onDestroy");
        super.onDestroy();
    }
}
