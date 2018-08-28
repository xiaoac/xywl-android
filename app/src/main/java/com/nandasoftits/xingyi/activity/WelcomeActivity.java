package com.nandasoftits.xingyi.activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import com.nandasoftits.xingyi.R;
import com.nandasoftits.xingyi.XywlApplication;
import com.nandasoftits.xingyi.sharedperferences.UserSharedPreferences;
import com.nandasoftits.xingyi.utils.*;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.List;

public class WelcomeActivity extends BasicActivity {

    private static final String LOG_TAG = "WelcomeActivity";

    private boolean mRequestPermission = false;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(LOG_TAG, "onCreate");
        setContentView(R.layout.welcome_activity);
        //判断权限

        //是否为第一次启动
        if (TextUtils.isEmpty(UserSharedPreferences.getMsg(this, UserSharedPreferences.SPHelp.IS_FIRST))) {
            //第一次启动，进行权限申请
            //自启权限
//            CommonUtils.openStart(this);

            //网络权限
            //定位权限
            //电话权限
            List<String> list = PermissionUtils.checkPermission(this);
            if (list != null && !list.isEmpty()) {
                PermissionUtils.requestPermission(this, list);
                mRequestPermission = true;
                Logger.d(LOG_TAG, "首次开启申请权限");
            }
            UserSharedPreferences.saveMsg(this, UserSharedPreferences.SPHelp.IS_FIRST, "TRUE");
        } else {
            //非第一次启动，进行权限判断
            //如果有网络权限，进入下一级界面
            if (!PermissionUtils.checkInternetPermission(this)) {
                PermissionUtils.requestInternetPermission(this);
                mRequestPermission = true;
                Logger.d(LOG_TAG, "当前无网络");
                Logger.d(LOG_TAG, "非首次开启申请权限");
                startErrPage();
            }
            //无网络权限，提醒开启网络权限
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRequestPermission) {
            Logger.d(LOG_TAG, "onResume return");
            mRequestPermission = false;
            return;
        }
        if (!PermissionUtils.checkInternetPermission(this) || !CommonUtils.isNetworkConnected(this)) {
            Logger.d(LOG_TAG, "无法连接网络。");
            startErrPage();
        } else {
            OKHttpUtils.judgeInternet(new HttpCallback() {
                @Override
                public void onResponse(String str) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            initWX();
                            ActivityJumpHelper.goLoginActivity(WelcomeActivity.this);
//                            ActivityJumpHelper.goH5Activity(WelcomeActivity.this,Constant.DEV_PATH_URL);
                            finish();
                        }
                    }, 2000);
                }

                @Override
                public void onFailed(String str) {
                    Logger.d(LOG_TAG, "onFailed -> " + str);
                    //这边可以通过监听jpush的广播来实时获取网络状态
                    startErrPage();
                }
            });

        }
    }

    private void initWX() {
        Logger.d(LOG_TAG, "XywlApplication -> initWX");
        XywlApplication.mWxApi = WXAPIFactory.createWXAPI(this, Constant.WX_APPID, false);
        XywlApplication.mWxApi.registerApp(Constant.WX_APPID);
    }

    private void startErrPage() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ActivityJumpHelper.goH5Activity(WelcomeActivity.this, Constant.ERROR_PAGE);
                finish();
            }
        }, 2000);
    }
}
