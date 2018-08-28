package com.nandasoftits.xingyi.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtils {

    private static final int REQUEST_CODE = 0xFFF;

    private static final String LOG_TAG = "PermissionUtils";

    private static final String[] NEED_PERMISSION = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CALL_PHONE
    };

    public static List<String> checkPermission(Context context) {
        List<String> noPermission = new ArrayList<>();
        for (String permission : NEED_PERMISSION) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                Logger.d(LOG_TAG, "无" + permission + "权限");
                noPermission.add(permission);
            } else {
                Logger.d(LOG_TAG, "有" + permission + "权限");
            }
        }
        return noPermission;
    }

    public static void requestPermission(Activity activity, List<String> permissionList) {
        if (permissionList == null || permissionList.isEmpty()) {
            return;
        }
        ActivityCompat.requestPermissions(activity, permissionList.toArray(new String[permissionList.size()]), REQUEST_CODE);
    }

    public static boolean checkInternetPermission(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            Logger.d(LOG_TAG,"no internet permission! return false");
            return false;
        } else {
            Logger.d(LOG_TAG,"have internet permission! return true");
            return true;
        }
    }

    public static void requestInternetPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.INTERNET}, REQUEST_CODE);
    }

}
