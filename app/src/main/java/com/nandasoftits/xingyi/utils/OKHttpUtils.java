package com.nandasoftits.xingyi.utils;

import android.content.Context;
import com.nandasoftits.xingyi.entity.ResponseBean;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class OKHttpUtils {

    private static final String LOG_TAG = "OKHttpUtils";

    public static void doHttpPost(final HttpCallback callback, String url, Map<String, String> args) {
        //创建okHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();

        //构建参数
        FormBody.Builder builder = new FormBody.Builder();

        if (args != null) {
            for (Map.Entry<String, String> entry : args.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }

        //创建一个Request
        final Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();


        //new call
        Call call = okHttpClient.newCall(request);

        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                final String errMsg = e.getMessage();
                CommonUtils.doCallBackInMainThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailed(errMsg);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Logger.d(LOG_TAG, "onResponse ->" + response);
                final String htmlStr = response.body().string();
                CommonUtils.doCallBackInMainThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(htmlStr);
                    }
                });
            }
        });
    }

    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    public static void uploadImg(final Context context, final HttpCallback callback, List<String> mImgUrls, String token, String serviceUrl) {

        final OkHttpClient client = new OkHttpClient();

        // mImgUrls为存放图片的url集合
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (int i = 0; i < mImgUrls.size(); i++) {
            File f = new File(mImgUrls.get(i));
            if (f != null) {
                builder.addFormDataPart("fileName", f.getName(), RequestBody.create(MEDIA_TYPE_PNG, f));
            }
        }
        //添加其它信息

        MultipartBody requestBody = builder.build();
        //构建请求
        Request request = new Request.Builder()
                .url(serviceUrl)//地址
                //TODO
                .addHeader("token", token)
                .post(requestBody)//添加请求体
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Logger.d(LOG_TAG, "上传失败:e.getLocalizedMessage() = " + e.getLocalizedMessage());
                callback.onFailed(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bodyString = response.body().string();
                ResponseBean bean = ResponseBean.getResponseBean(bodyString);

                if (bean == null) {
                    Logger.d(LOG_TAG, "请求消息解析失败：response = " + bodyString);
                    return;
                }

                if (!bean.isSuccess() || bean.getErrCode() != 200) {
                    callback.onFailed(bean.getMsg());
                    return;
                }

                Logger.d(LOG_TAG, "上传照片成功：response = " + bodyString);
                callback.onResponse(ResponseUtils.getImgFileName(bean));
            }
        });

    }


    public static void judgeInternet(HttpCallback callback) {
        doHttpPost(callback, "http://www.baidu.com", null);
    }

}
