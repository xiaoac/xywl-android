package com.nandasoftits.xingyi.utils;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LocatinUtils {

    private static final String LOG_TAG = "LocatinUtils";

    public static int calMapLevel(List<LatLng> latLngList) {

        if (latLngList == null || latLngList.isEmpty()) {
            //没有坐标，显示全中国
//            map.centerAndZoom(new BMap.Point(103.388611,35.563611), 5);
            Logger.d(LOG_TAG, " calMapLevel -> latLngList is empty return ");
            return 5;
        }

        //找出最大的经纬度
        ArrayList<Double> latitudeList = new ArrayList<>();
        ArrayList<Double> longitudeList = new ArrayList<>();
        for (int i = 0; i < latLngList.size(); i++) {
            double latitude = latLngList.get(i).latitude;
            double longitude = latLngList.get(i).longitude;
            latitudeList.add(latitude);
            longitudeList.add(longitude);
        }
        Double maxLatitude = Collections.max(latitudeList);
        Double minLatitude = Collections.min(latitudeList);
        Double maxLongitude = Collections.max(longitudeList);
        Double minLongitude = Collections.min(longitudeList);

        LatLng max = new LatLng(maxLatitude, maxLongitude);
        LatLng min = new LatLng(minLatitude, minLongitude);

        //计算两点之间距离
        double distance = DistanceUtil.getDistance(max, min);

        int zoom[] = {10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 1000, 2000, 25000, 50000, 100000, 200000, 500000, 1000000, 2000000};
        Logger.d(LOG_TAG, "maxLatitude==" + maxLatitude + ";minLatitude==" + minLatitude + ";maxLongitude==" + maxLongitude + ";minLongitude==" + minLongitude);
        Logger.d(LOG_TAG, "distance==" + distance);
        for (int i = 0; i < zoom.length; i++) {
            int zoomNow = zoom[i];
            if (zoomNow - distance > 0) {
                int level = 18 - i + 6;
                //设置地图显示级别为计算所得level
                Logger.d(LOG_TAG, "level==" + level);
                return level;
            }
        }
        return 0;
    }

    public static LatLng calCentralPoint(List<LatLng> latLngList) {
        //找出最大的经纬度
        if (latLngList == null || latLngList.isEmpty()) {
            //没有坐标，显示全中国
            Logger.d(LOG_TAG, "calCentralPoint -> latLngList is empty return ");
            return new LatLng(103.388611, 35.563611);
        }

        double lanSum = 0;
        double lonSum = 0;
        for (int i = 0; i < latLngList.size(); i++) {
            LatLng ll = latLngList.get(i);
            lanSum += ll.latitude;
            lonSum += ll.longitude;
        }
        return new LatLng(lanSum / latLngList.size(), lonSum / latLngList.size());
    }

}
