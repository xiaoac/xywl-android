package com.nandasoftits.xingyi.activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.nandasoftits.xingyi.view.ImgSelectPicker.PICK_PHOTO;
import static com.nandasoftits.xingyi.view.ImgSelectPicker.TACK_PHOTO;

public class H5Activity extends BasicActivity {

    private static final String LOG_TAG = "H5Activity";

    private WebView mWebView;

    private ImgSelectPicker mImgSelectPicker;

    private ValueCallback<Uri> mUploadMessage;

    private LocationClient mLocationClient;
    private BDLocationListener mBDLocationListener;

    private boolean loadError;

    private String mPath;

    private long mFinishTime;

    public static String WXCdoe = "";

    public String doWXCompleteMethodName = "";

    private String mImgPath;

    private boolean mUseToken;

    private Handler mHandle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.h5_activity);
        mWebView = findViewById(R.id.main_web_view);

        mHandle = new Handler();
        Intent intent = getIntent();
        if (intent != null) {
            mPath = intent.getStringExtra(Constant.TARGET_PATH);
            mUseToken = intent.getBooleanExtra(Constant.USE_TOKEN,false);
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
                        // 获取SD卡路径
                        mImgPath = Environment.getExternalStorageDirectory().getPath();

                        SimpleDateFormat t = new SimpleDateFormat("yyyyMMddssSSS");
                        String filename = "MT" + (t.format(new Date())) + ".png";

                        // 保存图片的文件名
                        mImgPath = mImgPath + "/" + filename;

                        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                            takePhotoBiggerThan7((new File(mImgPath)).getAbsolutePath());
                        }else {
                            // 指定拍照意图
                            Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            // 加载路径图片路径
                            Uri mUri = Uri.fromFile(new File(mImgPath));
                            // 指定存储路径，这样就可以保存原图了
                            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
                            startActivityForResult(openCameraIntent, TACK_PHOTO);
                        }
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

    private void takePhotoBiggerThan7(String absolutePath) {
        Uri mCameraTempUri;
        try {
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            values.put(MediaStore.Images.Media.DATA, absolutePath);
            mCameraTempUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            if (mCameraTempUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraTempUri);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            }
            startActivityForResult(intent, TACK_PHOTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        String token = UserSharedPreferences.getMsg(H5Activity.this,UserSharedPreferences.SPHelp.USER_TAG);

        if(mUseToken && !TextUtils.isEmpty(token)) {
            Map<String,String> extraHeaders = new HashMap<String, String>();
            extraHeaders.put("token", token);
            mWebView.loadUrl(mPath, extraHeaders);
        }else {
            //加载需要显示的网页
            mWebView.loadUrl(mPath);
        }

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

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //handler.cancel(); 默认的处理方式，WebView变成空白页
                handler.proceed();//接受证书

                //handleMessage(Message msg); 其他处理
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
            if (TextUtils.isEmpty(doWXCompleteMethodName)) {
                Logger.d(LOG_TAG, "doWXCompleteMethodName is empty!");
                return;
            }
            mWebView.loadUrl("javascript: " + doWXCompleteMethodName + "('" + WXCdoe + "')");
            WXCdoe = "";
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Logger.d(LOG_TAG, "requestCode[" + requestCode + "]," + "resultCode[" + resultCode + "],");
        if (resultCode != RESULT_OK) {
            Logger.d(LOG_TAG, "RETURN ->" + resultCode);
            return;
        }

        FileInputStream is = null;

        try {
            Bitmap relImg = null;
            Bitmap endImg = null;
            if(data != null){
                Uri uri = data.getData();
                Logger.d(LOG_TAG, "RETURN -> uri ->"+uri);
                if(uri == null) {
                    uploadImgFail();
                    Logger.d(LOG_TAG, "RETURN -> uri = null");
                    return;
                }
                //获取图库图片
                relImg = MediaStore.Images.Media.getBitmap(H5Activity.this.getContentResolver(),uri);
            } else {
                //获取拍照图片
                if (TextUtils.isEmpty(mImgPath) || !new File(mImgPath).exists()) {
                    Logger.d(LOG_TAG, "RETURN -> mImgPath = null");
                    uploadImgFail();
                    return;
                }
                Logger.d(LOG_TAG, " -> 11111");
                // 获取输入流
                is = new FileInputStream(mImgPath);
                // 把流解析成bitmap,此时就得到了清晰的原图
                //接下来就可以展示了（或者做上传处理
                relImg = BitmapFactory.decodeStream(is);
                Logger.d(LOG_TAG, " -> 22222");
                mImgPath = null;
            }
            Matrix matrix = new Matrix();
            matrix.setScale(0.3f, 0.3f);
            endImg = Bitmap.createBitmap(relImg, 0, 0, relImg.getWidth(), relImg.getHeight(), matrix, false);
            if(relImg != null && !relImg.isRecycled()){
                relImg.recycle();
                relImg = null;
            }
            System.gc();

            if (endImg == null) {
                uploadImgFail();
                Logger.d(LOG_TAG, "RETURN -> bitmap = null");
                return;
            }

            mWebView.loadUrl("javascript: showLoading('图片上传中...')");

            final Bitmap fBitmap = endImg;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    disposeImg(fBitmap);
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void disposeImg(Bitmap bitmap) {
        //压缩图片
        //bitmap = ImgUtils.bitmapCompress(bitmap);

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

//        TODO
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
            H5Activity.this.getLocation();
        }

        @JavascriptInterface
        public void getUserId(String methodName) {
            String retString = UserSharedPreferences.getMsg(H5Activity.this, UserSharedPreferences.SPHelp.USER_TAG);
            doJSFunction(methodName,retString);
        }

        @JavascriptInterface
        public void getUserGroup(String methodName) {
            String retString = UserSharedPreferences.getMsg(H5Activity.this, UserSharedPreferences.SPHelp.USER_GROUP);
            doJSFunction(methodName,retString);
        }

        @JavascriptInterface
        public void getUserMsg(String methodName) {
            String retString = UserSharedPreferences.getMsg(H5Activity.this, UserSharedPreferences.SPHelp.USER_MSG);
            doJSFunction(methodName,retString);
        }

        @JavascriptInterface
        public void setUserId(String userTag) {
            UserSharedPreferences.saveMsg(H5Activity.this, UserSharedPreferences.SPHelp.USER_TAG, userTag);
            startJPush();
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
        public void savePushAlias(String pushAlias) {
            //Toast.makeText(H5Activity.this,"save alias:" + pushAlias + "success",Toast.LENGTH_LONG).show();
            UserSharedPreferences.saveMsg(H5Activity.this, UserSharedPreferences.SPHelp.PUSH_ALIAS, pushAlias);
            PushUtils.initJPush(H5Activity.this);
        }

        @JavascriptInterface
        public void savePushTag(String pushTag) {
            //Toast.makeText(H5Activity.this,"save tag:" + pushTag + "success",Toast.LENGTH_LONG).show();
            UserSharedPreferences.saveMsg(H5Activity.this, UserSharedPreferences.SPHelp.PUSH_TAG, pushTag);
            PushUtils.initJPush(H5Activity.this);
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
        public void doWXLogin(String methodName) {
            doWXCompleteMethodName = methodName;
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

    private void doJSFunction(final String functionName, final String param){
        if(mHandle == null ||mWebView == null){
            return;
        }
        mHandle.postDelayed(new Runnable() {
            @Override
            public void run() {
                mWebView.loadUrl("javascript:" + functionName + "('" + param + "')");
            }
        },500);
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
        Logger.d(LOG_TAG, "onDestroy");
        super.onDestroy();
        // 取消监听函数
        if (mLocationClient != null) {
            mLocationClient.unRegisterLocationListener(mBDLocationListener);
        }
    }

    private void startJPush() {
        Logger.d(LOG_TAG, "startJPush");
        startService(new Intent(this, H5Activity.class));
        if (!CommonUtils.isBelowLOLLIPOP()) {
            JobSchedulerManager.getJobSchedulerInstance(this).startJobScheduler();
        }
    }
}
