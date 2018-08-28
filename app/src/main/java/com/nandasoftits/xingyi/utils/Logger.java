package com.nandasoftits.xingyi.utils;

import android.util.Log;

public class Logger {

    private static final String LOG_TAG = "XYWL";

    private static final boolean IS_TEST = true;

    public static void e(String tag, String msg) {
        if (IS_TEST) {
            Log.e(LOG_TAG, "[" + tag + "]  " + msg);
        }
    }

    public static void e(String tag, Exception e) {
        if (IS_TEST) {
            Log.e(LOG_TAG, "[" + tag + "]  " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void d(String tag, String msg) {
        if (IS_TEST) {
            Log.e(LOG_TAG, "[" + tag + "]  " + msg);
        }
    }

}
