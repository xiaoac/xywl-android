package com.nandasoftits.xingyi.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;
import com.nandasoftits.xingyi.R;
import com.nandasoftits.xingyi.XywlApplication;
import com.nandasoftits.xingyi.utils.ActivityJumpHelper;
import com.nandasoftits.xingyi.utils.Logger;
import com.tencent.mm.opensdk.modelmsg.SendAuth;

public class WeChatActivity extends Activity {

    private static String LOG_TAG = "WeChatActivity";

    public static boolean FINISH_TAG = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.d(LOG_TAG, "onCreate");
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wechat_activity);
        Logger.d(LOG_TAG, "doWXLogin");
        if (!XywlApplication.mWxApi.isWXAppInstalled()) {
            Toast.makeText(WeChatActivity.this, "您还未安装微信客户端", Toast.LENGTH_LONG).show();
            return;
        }
        // send oauth request
        Logger.d(LOG_TAG, "doWXLogin start");
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "xywl_state";
        XywlApplication.mWxApi.sendReq(req);
        FINISH_TAG = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.d(LOG_TAG, "onResume");
        if (FINISH_TAG) {
            finish();
            ActivityJumpHelper.goH5Activity(this,"");
        }
    }

    @Override
    protected void onDestroy() {
        Logger.d(LOG_TAG, "onDestroy");
        super.onDestroy();
    }
}
