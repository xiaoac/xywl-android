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
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatusBarUtils.statusBarLightMode(this);
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
