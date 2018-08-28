package com.nandasoftits.xingyi.utils;

import com.baidu.mapapi.model.LatLng;

public class MapUtils {

    public static boolean judgeSamePosition(LatLng latLng1, LatLng latLng2) {
        if (latLng1 == null || latLng2 == null) {
            return false;
        }
        return Math.abs(latLng1.latitude - latLng2.latitude) < 0.00000000000000000001
                && Math.abs(latLng1.longitude - latLng2.longitude) < 0.00000000000000000001;
    }

}
