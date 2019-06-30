package com.nandasoftits.xingyi.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.*;
import android.widget.Toast;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.nandasoftits.xingyi.R;
import com.nandasoftits.xingyi.XywlApplication;
import com.nandasoftits.xingyi.entity.Location;
import com.nandasoftits.xingyi.sharedperferences.UserSharedPreferences;
import com.nandasoftits.xingyi.utils.*;
import com.nandasoftits.xingyi.view.ImgSelectPicker;
import com.tencent.mm.opensdk.modelmsg.SendAuth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.nandasoftits.xingyi.view.ImgSelectPicker.PICK_PHOTO;
import static com.nandasoftits.xingyi.view.ImgSelectPicker.TACK_PHOTO;

public class H5Activity extends BasicActivity {

    private static final String LOG_TAG = "H5Activity";

    //TODO Test
    private static final String DEFAULT_PATH = "http://192.168.2.103:8080/views/test/android/main-view.html";

    private WebView mWebView;

    private ImgSelectPicker mImgSelectPicker;

    private ValueCallback<Uri[]> mMultiFileCallback;

    private ValueCallback<Uri> mUploadMessage;

    private LocationClient mLocationClient;
    private BDLocationListener mBDLocationListener;

    private boolean loadError;

    private String mPath;

    private long mFinishTime;

    public static String WXCdoe = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.h5_activity);
        mWebView = findViewById(R.id.main_web_view);

        Intent intent = getIntent();
        if (intent != null) {
            mPath = intent.getStringExtra(Constant.TARGET_PATH);
        }
        if (TextUtils.isEmpty(mPath)) {
            mPath = UserSharedPreferences.getMsg(this, "TEST_PATH");
        }

        initWebView();
        initImgSelect();

    }

    private void initImgSelect() {
        mImgSelectPicker = new ImgSelectPicker(H5Activity.this);
        mImgSelectPicker.setImgSelectPickCallback(new ImgSelectPicker.ImgSelectPickerCallback() {
            @Override
            public void doSelect(String imgTag, int select) {
                switch (select) {
                    case TACK_PHOTO:
                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //系统常量， 启动相机的关键
                        startActivityForResult(openCameraIntent, TACK_PHOTO); // 参数常量为自定义的request code, 在取返回结果时有用
                        break;
                    case PICK_PHOTO:
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, PICK_PHOTO);

                        break;
                    default:
                }
            }
        });
    }

    private void initLocation() {
        // 声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        mBDLocationListener = new MyBDLocationListener();
        // 注册监听
        mLocationClient.registerLocationListener(mBDLocationListener);
    }

    /**
     * 获得所在位置经纬度及详细地址
     */
    public void getLocation() {
        initLocation();
        // 声明定位参数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式 高精度
        option.setCoorType("bd09ll");// 设置返回定位结果是百度经纬度 默认gcj02
        option.setScanSpan(5000);// 设置发起定位请求的时间间隔 单位ms
        option.setIsNeedAddress(true);// 设置定位结果包含地址信息
        option.setNeedDeviceDirect(true);// 设置定位结果包含手机机头 的方向
        option.setOpenGps(true);
        option.setIsNeedLocationDescribe(true);
        // 设置定位参数
        mLocationClient.setLocOption(option);
        // 启动定位
        mLocationClient.start();
    }

    private void initWebView() {
        String testPath = UserSharedPreferences.getMsg(this, "TEST_PATH");

        WebSettings webSettings = mWebView.getSettings();
        //设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        //设置可以访问文件
        webSettings.setAllowFileAccess(true);
        //设置支持缩放
        webSettings.setBuiltInZoomControls(true);
        //缓存模式设置
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //加载需要显示的网页
        mWebView.loadUrl(mPath);
        //设置WebViewClient用来辅助WebView处理各种通知请求事件等，如更新历史记录、网页开始加载/完毕、报告错误信息等
        mWebView.setWebViewClient(new WebViewClient() {

            // 以下方法避免 自动打开系统自带的浏览器，而是让新打开的网页在当前的WebView中显示
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                if (mUploadMessage != null)
                    return;
                mUploadMessage = uploadMsg;
            }

            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooser(uploadMsg, "");
            }

            // For Android > 4.1.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooser(uploadMsg, acceptType);
            }

            /**
             * 网页加载结束的时候执行的回调方法
             * @param view
             * @param url
             */
            @Override
            public void onPageFinished(WebView view, String url) {//网页加载结束的时候
                if (loadError) {//当网页加载成功的时候判断是否加载成功
                    webViewLoadErrPage();
                    loadError = false;
                }
            }


            /**
             * 页面加载错误时执行的方法，但是在6.0以下，有时候会不执行这个方法
             * @param view
             * @param errorCode
             * @param description
             * @param failingUrl
             */
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                loadError = true;
            }
        });

        // 用于辅助WebView处理JavaScript的对话框、网站图标、网站标题以及网页加载进度等
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
//                tv_title.setText(title);
            }
        });
        // 使 H5可调用Native方法： android.nativeMethod()
        mWebView.addJavascriptInterface(new H5JsInterface(), "XYNativeClient");
        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Logger.d(LOG_TAG, "event.getAction() -> " + event.getAction());
                Logger.d(LOG_TAG, "keyCode -> " + keyCode);
                if (keyCode == KeyEvent.KEYCODE_BACK) {  //表示按返回键
                    if (event.getAction() == KeyEvent.ACTION_UP) {
                        Logger.d(LOG_TAG, "do back");
                        mWebView.loadUrl("javascript: doBack()");
                    }
                    return true;
                }
                return false;
            }
        });

        mWebView.setHorizontalScrollBarEnabled(false);//水平不显示
        mWebView.setVerticalScrollBarEnabled(false); //垂直不显示
        String ua = mWebView.getSettings().getUserAgentString();
        mWebView.getSettings().setUserAgentString(ua + "; nandasoft_its_xywl_android /" + CommonUtils.getAppVersionName(this));
    }

    public void setH5Img(final String imgTag, final String result) {
        Logger.d(LOG_TAG, "do setPlatformType");

        CommonUtils.doCallBackInMainThread(new Runnable() {
            @Override
            public void run() {
                //android调用H5代码
                mWebView.loadUrl("javascript: showH5Img('" + imgTag + "','" + result + "')");
            }
        });
    }

    public void setH5Location(final Location location) {
        Logger.d(LOG_TAG, "setH5Location retString = " + location);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                //android调用H5代码
                mWebView.loadUrl("javascript: showH5Location('" + location.getLat() + "','" + location.getLng() + "','" + location.getAddress() + "','" + location.getCity() + "')");
            }
        }, 50);
    }

    private void webViewLoadErrPage() {
        mWebView.loadUrl(Constant.ERROR_PAGE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(WXCdoe) && mWebView != null) {
            mWebView.loadUrl("javascript: doWXLoginComplete('" + WXCdoe + "')");
            WXCdoe = "";
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e(LOG_TAG, "requestCode[" + requestCode + "]," + "resultCode[" + resultCode + "],");
        if (resultCode != RESULT_OK) {
            Log.e(LOG_TAG, "RETURN ->" + resultCode);
            return;
        }

        if (data == null) {
            Log.e(LOG_TAG, "RETURN -> data = null");
            uploadImgFail();
            return;
        }
        try {
            Uri uri = data.getData();
            Bitmap bitmap = null;
            if (uri != null) {
                Bitmap image = null;

                image = MediaStore.Images.Media.getBitmap(H5Activity.this.getContentResolver(), uri);

                if (image != null) {
                    Matrix matrix = new Matrix();
                    matrix.setScale(0.5f, 0.5f);
                    bitmap = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, false);
                }
            } else {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    //这里是有些拍照后的图片是直接存放到Bundle中的所以我们可以从这里面获取Bitmap图片
                    bitmap = extras.getParcelable("data");
                }
            }

            if (bitmap == null) {
                uploadImgFail();
                Log.e(LOG_TAG, "RETURN -> bitmap = null");
                return;
            }

            mWebView.loadUrl("javascript: showLoading('图片上传中...')");

            final Bitmap fBitmap = bitmap;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    disposeImg(fBitmap);
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disposeImg(Bitmap bitmap) {
        //压缩图片
        bitmap = ImgUtils.bitmapCompress(bitmap);

        //保存图片
        FileOutputStream fileOutputStream = null;
        String picPath = null;
        try {
            // 获取 SD 卡根目录
            String saveDir = Environment.getExternalStorageDirectory().getPath() + "/xywl_photos";
            // 新建目录
            File dir = new File(saveDir);
            if (!dir.exists()) dir.mkdir();
            // 生成文件名
            SimpleDateFormat t = new SimpleDateFormat("yyyyMMddssSSS");
            String filename = "MT" + (t.format(new Date())) + ".png";
            // 新建文件
            File file = new File(saveDir, filename);
            // 打开文件输出流
            fileOutputStream = new FileOutputStream(file);
            // 生成图片文件
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            // 相片的完整路径
            picPath = file.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (TextUtils.isEmpty(picPath)) {
            uploadImgFail();
            return;
        }
        Logger.d(LOG_TAG, "picPath -> " + picPath);

        List<String> imgList = new ArrayList<>();
        imgList.add(picPath);

        //上传图片
        OKHttpUtils.uploadImg(H5Activity.this, new HttpCallback() {
            @Override
            public void onResponse(String str) {
                setH5Img(mImgSelectPicker.getImgTarget(), str);
            }

            @Override
            public void onFailed(String str) {
                Logger.d(LOG_TAG, "图片上传失败。" + str);
                uploadImgFail();
            }
        }, imgList, mImgSelectPicker.getToken(), mImgSelectPicker.getServiceUrl());
        //删除临时图片文件

        Logger.d(LOG_TAG, "delete file -> " + picPath);

//
//        File file = new File(picPath);
//        if (file.exists() && file.isFile()) {
//            file.delete();
//        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private void uploadImgFail() {
        if (mImgSelectPicker == null) {
            return;
        }
        setH5Img(mImgSelectPicker.getImgTarget(), null);
    }

    public class H5JsInterface {
        @JavascriptInterface
        public void toastMessage(String msg) {
            Toast.makeText(H5Activity.this, msg, Toast.LENGTH_SHORT).show();
        }

        //异步过程，需要JS提供方法
        @JavascriptInterface
        public void showImgSelectDialog(final int model, final String imgId, final String token, final String sercuveUrl) {
            CommonUtils.doCallBackInMainThread(new Runnable() {
                @Override
                public void run() {
                    Logger.d(LOG_TAG, "imgId -> " + imgId);
                    mImgSelectPicker.setImgTarget(imgId);
                    mImgSelectPicker.setToken(token);
                    mImgSelectPicker.setServiceUrl(sercuveUrl);
                    mImgSelectPicker.show(model);
                }
            });
        }

        @JavascriptInterface
        public void getLocation() {
            //TODO
            H5Activity.this.getLocation();
        }

        @JavascriptInterface
        public void getUserTag(String methodName) {
            String retString = UserSharedPreferences.getMsg(H5Activity.this, UserSharedPreferences.SPHelp.USER_TAG);
            mWebView.loadUrl("javascript: " + methodName + "('" + retString + "')");
        }

        @JavascriptInterface
        public void getUserGroup(String methodName) {
            String retString = UserSharedPreferences.getMsg(H5Activity.this, UserSharedPreferences.SPHelp.USER_GROUP);
            mWebView.loadUrl("javascript: " + methodName + "('" + retString + "')");
        }

        @JavascriptInterface
        public void getUserMsg(String methodName) {
            String retString = UserSharedPreferences.getMsg(H5Activity.this, UserSharedPreferences.SPHelp.USER_MSG);
            mWebView.loadUrl("javascript: " + methodName + "('" + retString + "')");
        }

        @JavascriptInterface
        public void saveUserTag(String userTag) {
            UserSharedPreferences.saveMsg(H5Activity.this, UserSharedPreferences.SPHelp.USER_TAG, userTag);
        }

        @JavascriptInterface
        public void saveUserGroup(String userGroup) {
            UserSharedPreferences.saveMsg(H5Activity.this, UserSharedPreferences.SPHelp.USER_GROUP, userGroup);
        }

        @JavascriptInterface
        public void saveUserMsg(String userMsg) {
            UserSharedPreferences.saveMsg(H5Activity.this, UserSharedPreferences.SPHelp.USER_MSG, userMsg);
        }


        @JavascriptInterface
        public void doFinish() {
            //app关闭确认
            Logger.d(LOG_TAG, "do finish");
            if (System.currentTimeMillis() - mFinishTime > 3500) {
                mFinishTime = System.currentTimeMillis();
                Toast.makeText(H5Activity.this, "再次点击返回关闭应用", Toast.LENGTH_LONG).show();
            } else {
                finish();
            }
            return;
        }

        @JavascriptInterface
        public void doCallPhone(String phoneNumber) {
            if (ContextCompat.checkSelfPermission(H5Activity.this, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(H5Activity.this, "已复制到剪切板", Toast.LENGTH_LONG).show();
                return;
            }
            Intent intent = new Intent(Intent.ACTION_CALL);
            Uri data = Uri.parse("tel:" + phoneNumber);
            intent.setData(data);
            startActivity(intent);
        }

        @JavascriptInterface
        public void doWXLogin() {
            CommonUtils.doCallBackInMainThread(new Runnable() {
                @Override
                public void run() {
                    Logger.d(LOG_TAG, "doWXLogin");
                    if (!XywlApplication.mWxApi.isWXAppInstalled()) {
                        Toast.makeText(H5Activity.this, "您还未安装微信客户端", Toast.LENGTH_LONG).show();
                        return;
                    }
                    // send oauth request
                    Logger.d(LOG_TAG, "doWXLogin start");
                    final SendAuth.Req req = new SendAuth.Req();
                    req.scope = "snsapi_userinfo";
                    req.state = "xywl_state";
                    XywlApplication.mWxApi.sendReq(req);
                }
            });
        }
    }

    private class MyBDLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // 非空判断
            if (location != null) {
                // 根据BDLocation 对象获得经纬度以及详细地址信息
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                String address = location.getAddrStr();
                String city = location.getCity();
                Logger.d(LOG_TAG, "address:" + address +
                        " \nlocation.getDistrict():" + location.getDistrict() +
                        " \nlocation.getStreet():" + location.getStreet() +
                        " \nlocation.getStreetNumber():" + location.getStreetNumber() +
                        " \nlocation.getLocationDescribe():" + location.getLocationDescribe() +
                        " \nlatitude:" + latitude +
                        " longitude:" + longitude + "---");

                Location loc = new Location();
                loc.setCity(city);
                loc.setAddress(location.getCity() + location.getDistrict() + location.getStreet() + location.getStreetNumber());
                loc.setLat(latitude);
                loc.setLng(longitude);

                setH5Location(loc);

                if (mLocationClient.isStarted()) {
                    // 获得位置之后停止定位
                    mLocationClient.stop();
                    mLocationClient.unRegisterLocationListener(mBDLocationListener);
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Logger.d(LOG_TAG, "keyCode -> " + keyCode);
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            mWebView.loadUrl("javascript: doBack()");
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        Logger.d(LOG_TAG, "onDestroy");
        super.onDestroy();
        // 取消监听函数
        if (mLocationClient != null) {
            mLocationClient.unRegisterLocationListener(mBDLocationListener);
        }
    }
}
