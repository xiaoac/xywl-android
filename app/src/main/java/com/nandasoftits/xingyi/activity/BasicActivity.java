package com.nandasoftits.xingyi.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.nandasoftits.xingyi.R;
import com.nandasoftits.xingyi.utils.CommonUtils;
import com.nandasoftits.xingyi.utils.Logger;
import com.nandasoftits.xingyi.utils.StatusBarUtils;

public class BasicActivity extends Activity {

    private static final String LOG_TAG = "BasicActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.statusBarLightMode(this);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        View statusBar = findViewById(R.id.status_bar);
        if (statusBar != null) {
            ViewGroup.LayoutParams lp = statusBar.getLayoutParams();
            lp.height = CommonUtils.getStatusBarHeight(this);
            statusBar.setLayoutParams(lp);
            Logger.d(LOG_TAG, "StatusBarHeight -> " + CommonUtils.getStatusBarHeight(this));
        } else {
            Logger.d(LOG_TAG, "statusBar is null ,return ");
        }
    }
}
