package com.nandasoftits.xingyi.utils;

import android.content.Context;
import cn.jpush.android.api.JPushInterface;
import com.nandasoftits.xingyi.sharedperferences.UserSharedPreferences;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PushUtils {

    public static final String LOG_TAG = "PushUtils";

    //极光推送 测试环境
    public static void initTestJPush(Context context) {
        Logger.d(LOG_TAG, "XywlApplication -> initJPush");
        String userTag = UserSharedPreferences.getMsg(context, UserSharedPreferences.SPHelp.PUSH_ALIAS);
        String userGroup = UserSharedPreferences.getMsg(context, UserSharedPreferences.SPHelp.PUSH_TAG);

        String[] userGroupArr = userGroup.split(",");
        JPushInterface.setDebugMode(true);//正式版的时候设置false，关闭调试
        JPushInterface.init(context);
        //建议添加tag标签，发送消息的之后就可以指定tag标签来发送了
        Set<String> set = new HashSet<String>(Collections.singletonList("XYWL_GROUP"));

        JPushInterface.setTags(context, set, null);//设置标签
        JPushInterface.setAlias(context, 0, "XYWL_TAG");
    }

    //极光推送 实际环境
    public static void initJPush(Context context) {
        Logger.d(LOG_TAG, "XywlApplication -> initJPush");
        String userTag = UserSharedPreferences.getMsg(context, UserSharedPreferences.SPHelp.PUSH_ALIAS);
        String userGroup = UserSharedPreferences.getMsg(context, UserSharedPreferences.SPHelp.PUSH_TAG);

        String[] userGroupArr = userGroup.split(",");

        Logger.d(LOG_TAG, "XywlApplication");
        JPushInterface.setDebugMode(false);//正式版的时候设置false，关闭调试
        JPushInterface.init(context);
        //建议添加tag标签，发送消息的之后就可以指定tag标签来发送了
        Set<String> set = new HashSet<String>(Arrays.asList(userGroup));

        JPushInterface.setTags(context, set, null);//设置标签
        JPushInterface.setAlias(context, 0, userTag);
    }

}
