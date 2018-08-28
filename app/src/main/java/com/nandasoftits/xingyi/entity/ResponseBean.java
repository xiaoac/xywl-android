package com.nandasoftits.xingyi.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class ResponseBean {

    private boolean success;

    private int errCode;

    private String msg;

    private String data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    private ResponseBean() {
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public static ResponseBean getResponseBean(String str) {
        ResponseBean retBean = new ResponseBean();
        try {
            JSONObject job = new JSONObject(str);
            retBean.success = job.getBoolean("success");
            retBean.errCode = job.getInt("errCode");
            retBean.msg = job.getString("msg");
            retBean.data = job.getString("data");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return retBean;
    }
}
