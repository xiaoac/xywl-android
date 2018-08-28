package com.nandasoftits.xingyi;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.nandasoftits.xingyi.service.JPushActivateService;
import com.nandasoftits.xingyi.sharedperferences.UserSharedPreferences;
import com.nandasoftits.xingyi.utils.CommonUtils;
import com.nandasoftits.xingyi.utils.Constant;
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
        String userTag = UserSharedPreferences.getMsg(this, UserSharedPreferences.SPHelp.PUSH_ALIAS);
        String userGroup = UserSharedPreferences.getMsg(this, UserSharedPreferences.SPHelp.PUSH_TAG);

        if(TextUtils.isEmpty(userTag) && TextUtils.isEmpty(userGroup)){
            Logger.d(LOG_TAG,"user tag and user group are empty. ->  return ");
            return;
        }

        Logger.d(LOG_TAG, "startJPush");
        startService(new Intent(this, JPushActivateService.class));
        if (!CommonUtils.isBelowLOLLIPOP()) {
            JobSchedulerManager.getJobSchedulerInstance(this).startJobScheduler();
        }
    }

}
