package com.nandasoftits.xingyi.utils;

import android.text.TextUtils;
import com.nandasoftits.xingyi.entity.ResponseBean;
import org.json.JSONException;
import org.json.JSONObject;

public class ResponseUtils {

    public static String getImgFileName(ResponseBean bean) {
        String retString = "";
        try {
            JSONObject job = new JSONObject(bean.getData());
            retString = job.getString("filePath");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(retString)) {
            return null;
        }
        return retString;
    }
}
