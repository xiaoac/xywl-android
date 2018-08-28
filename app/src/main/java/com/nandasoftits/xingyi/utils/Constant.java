package com.nandasoftits.xingyi.utils;

public class Constant {

    public static class URL {
        public static final String GET_ORDER_LIST = "GET_ORDER_LIST";
    }

    public static class BasicSetting {
        public static final int PAGE_SIZE = 10;
    }

    //待审核，待执行，待签收，已签收，已完成，已关闭
    public static class OrderState {
        public static final int AUDIT = 0;
        public static final int EXECUTE = 1;
        public static final int NOT_SIGN = 2;
        public static final int SIGNED = 3;
        public static final int COMPLETE = 4;
        public static final int CLOSED = 5;
    }

    public static final String BASE_URL = "http://xingyi.nandasoft-its.com:8080";

    public static final String DEV_PATH_URL = "http://xingyi.nandasoft-its.com/XYApp/index.html";

    public static final String NEWS_LIST_PATH_URL = "http://xingyi.nandasoft-its.com/XYApp/myNewsList.html";

    public static final String PRD_PATH_URL = "http://xingyi.nandasoft-its.com:8001/XYapp/index.html";

    public static final String UP_FILE_RUL = "/xyl/file/upfile";

    public static final String TARGET_PATH = "TARGET_PATH";

    public static final String USE_TOKEN = "USE_TOKEN";

    public static final String ERROR_PAGE = "file:///android_asset/error.html";

    public static final String WX_APPID = "wx716e867a8dfc27bb";

    public static final String APP_PAGENAME = "com.nandasoftits.xingyi";
}
