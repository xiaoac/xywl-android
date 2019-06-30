package com.nandasoftits.xingyi;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.SyncStateContract;
import com.nandasoftits.xingyi.service.JPushActivateService;
import com.nandasoftits.xingyi.sharedperferences.UserSharedPreferences;
import com.nandasoftits.xingyi.utils.CommonUtils;
import com.nandasoftits.xingyi.utils.JobSchedulerManager;
import com.nandasoftits.xingyi.utils.Logger;
import com.tencent.mm.opensdk.openapi.IWXAPI;

public class XywlApplication extends Application {

    private static final String LOG_TAG = "XywlApplication";

    public static IWXAPI mWxApi;

    public static Context mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        startJPush();
    }

    private void startJPush() {
        Logger.d(LOG_TAG, "startJPush");
        startService(new Intent(this, JPushActivateService.class));
        if (!CommonUtils.isBelowLOLLIPOP()) {
            JobSchedulerManager.getJobSchedulerInstance(this).startJobScheduler();
        }
        //创建NOtification对象
    }

}
