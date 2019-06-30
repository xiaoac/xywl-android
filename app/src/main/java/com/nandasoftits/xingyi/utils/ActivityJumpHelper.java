package com.nandasoftits.xingyi.utils;

import android.content.Context;
import android.content.Intent;
import com.nandasoftits.xingyi.activity.H5Activity;
import com.nandasoftits.xingyi.activity.LoginActivity;
import com.nandasoftits.xingyi.activity.WeChatActivity;
import com.nandasoftits.xingyi.activity.WelcomeActivity;

public class ActivityJumpHelper {

    public static void goWelcomeActivity(Context context){
        Intent intent = new Intent(context, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void goLoginActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void goH5Activity(Context context, String url) {
        Intent intent = new Intent(context, H5Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Constant.TARGET_PATH, url);
        context.startActivity(intent);
    }

    public static void goWeChatActivity(Context context) {
        Intent intent = new Intent(context, WeChatActivity.class);
        context.startActivity(intent);
    }

}
