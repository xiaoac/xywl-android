package com.nandasoftits.xingyi.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cn.jpush.android.api.JPushInterface;
import com.nandasoftits.xingyi.activity.H5Activity;
import com.nandasoftits.xingyi.utils.ActivityJumpHelper;
import com.nandasoftits.xingyi.utils.Constant;
import com.nandasoftits.xingyi.utils.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class JiGuangReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Logger.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Logger.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Logger.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            processCustomMessage(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Logger.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Logger.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Logger.d(TAG, "[MyReceiver] 用户点击打开了通知");
//
            Bundle b = intent.getExtras();
            if (b != null) {
                disposeNotificationClick(context, b.getString("cn.jpush.android.EXTRA"));
            }

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Logger.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Logger.d(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            Logger.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    private void disposeNotificationClick(Context context, String extra) {
        //获取URL
        try {
            JSONObject job = new JSONObject(extra);
            String url = job.getString("ANDROID_EXTRA");
            ActivityJumpHelper.goH5ActivityWithToken(context, url);
        } catch (JSONException e) {
            e.printStackTrace();
            //NEWS_LIST_PATH_URL
            ActivityJumpHelper.goH5ActivityWithToken(context, Constant.NEWS_LIST_PATH_URL);
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
//        ...//省略了
        for (String key : bundle.keySet()) {
            Logger.d(TAG, "Key=" + key + ", content=" + bundle.getString(key));
        }
        return null;
    }

    //send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
//        ...//省略了
    }
}
