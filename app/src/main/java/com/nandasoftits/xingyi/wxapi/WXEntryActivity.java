package com.nandasoftits.xingyi.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;
import com.nandasoftits.xingyi.XywlApplication;
import com.nandasoftits.xingyi.activity.H5Activity;
import com.nandasoftits.xingyi.activity.WeChatActivity;
import com.nandasoftits.xingyi.utils.ActivityJumpHelper;
import com.nandasoftits.xingyi.utils.Logger;
import com.nandasoftits.xingyi.utils.StatusBarUtils;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String LOG_TAG = "WXEntryActivity";

    private static final int RETURN_MSG_TYPE_LOGIN = 1;
    private static final int RETURN_MSG_TYPE_SHARE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        super.onCreate(savedInstanceState);
        Logger.d(LOG_TAG, "onCreate");
        XywlApplication.mWxApi.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq req) {
        Logger.d(LOG_TAG, "onReq");
    }

    @Override
    public void onResp(BaseResp resp) {
        Logger.d(LOG_TAG, "onResp");
        Logger.d(LOG_TAG, "resp.errCode : " + resp.errCode + "");
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                if (RETURN_MSG_TYPE_SHARE == resp.getType()) {
                    Toast.makeText(WXEntryActivity.this, "分享失败", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(WXEntryActivity.this, "登录失败", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            case BaseResp.ErrCode.ERR_OK:
                switch (resp.getType()) {
                    case RETURN_MSG_TYPE_LOGIN:
                        String code = ((SendAuth.Resp) resp).code;
                        Logger.d(LOG_TAG, "code = " + code);
                        H5Activity.WXCdoe = code;
                        finish();
                        break;
                    case RETURN_MSG_TYPE_SHARE:
                        Toast.makeText(WXEntryActivity.this, "微信分享成功", Toast.LENGTH_LONG).show();
                        finish();
                        break;
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        Logger.d(LOG_TAG, "onDestroy");
        super.onDestroy();
    }
}
