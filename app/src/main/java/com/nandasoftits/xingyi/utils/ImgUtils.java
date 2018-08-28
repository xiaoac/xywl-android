package com.nandasoftits.xingyi.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImgUtils {

    private static final String LOG_TAG = "ImgUtils";

    public static Bitmap bitmapCompress(Bitmap bitmap) {
        if (bitmap.getByteCount() / 1024 / 1024 < 100000) {
            Logger.d(LOG_TAG, "压缩后图片的大小" + (bitmap.getByteCount() / 1024 / 1024)
                    + "M宽度为" + bitmap.getWidth() + "高度为" + bitmap.getHeight()
                    + "bytes.length=  " + (bitmap.getByteCount() / 1024 / 1024) + "KB");
            return bitmap;
        }

        Matrix matrix = new Matrix();
        matrix.setScale(0.8f, 0.8f);
        Bitmap temp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        Logger.d(LOG_TAG, "压缩后图片的大小" + (temp.getByteCount() / 1024 / 1024)
                + "M宽度为" + temp.getWidth() + "高度为" + temp.getHeight()
                + "bytes.length=  " + (bitmap.getByteCount() / 1024 / 1024) + "KB");
        return bitmapCompress(temp);
    }

    public static Bitmap getBitmapFromImageView(ImageView imageView) {
        Bitmap bm = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        return bitmapCompress(bm);
    }

    // bitmap转base64
    public static String bitmapToBase64(Bitmap bitmap) {
        String result = "data:image/png;base64,";//必须加上“data:image/png;base64”图片的数据格式H5才能识别出来
        ByteArrayOutputStream bos = null;
        try {
            if (null != bitmap) {
                bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);// 将bitmap放入字节数组流中
                bos.flush();// 将bos流缓存在内存中的数据全部输出，清空缓存
                bos.close();
                byte[] bitmapByte = bos.toByteArray();
                result += Base64.encodeToString(bitmapByte, Base64.DEFAULT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d("it520", "result=" + result);
        Log.d("it520", "size=" + bos.toByteArray().length / 1024);//获取ByteArrayOutputStream的大小，单位kb，
        return result;
    }

    /**
     * base64转Bitmap
     *
     * @param base64String
     * @return
     */
    public static Bitmap base64ToBitmap(String base64String) {
        byte[] bytes = Base64.decode(base64String, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }

}
