package com.nandasoftits.xingyi.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.nandasoftits.xingyi.service.JPushActivateService;
import com.nandasoftits.xingyi.utils.Logger;

public class JPushActivateReceiver extends BroadcastReceiver {
    private static final String TAG = "JPushActivateReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d(TAG, "Boot this system , BootBroadcastReceiver onReceive()");
        Logger.d(TAG, "intent action -> " + intent.getAction());
        //初始化sdk
        context.startService(new Intent(context, JPushActivateService.class));
    }
}
