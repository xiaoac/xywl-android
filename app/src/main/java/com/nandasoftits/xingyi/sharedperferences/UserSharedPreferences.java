package com.nandasoftits.xingyi.sharedperferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class UserSharedPreferences {

    public static final String SHARED_PREFERENCES_NAME = "USER";

    public static boolean saveMsg(Context context, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);

        return editor.commit();
    }

    public static boolean removeMsg(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(
                SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        return editor.commit();
    }

    public static String getMsg(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(
                SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        return preferences.getString(key, "");
    }

    public class SPHelp {
        public static final String USER_GROUP = "USER_GROUP";

        public static final String USER_TAG = "USER_TAG";

        public static final String USER_MSG = "USER_MSG";

        public static final String IS_FIRST = "IS_FIRST";

        public static final String PUSH_TAG = "PUSH_TAG";

        public static final String PUSH_ALIAS = "PUSH_ALIAS";
    }

}
