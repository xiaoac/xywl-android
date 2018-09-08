package com.nandasoftits.xingyi.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.*;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.nandasoftits.xingyi.R;
import com.nandasoftits.xingyi.entity.Location;
import com.nandasoftits.xingyi.utils.LocatinUtils;
import com.nandasoftits.xingyi.utils.Logger;
import com.nandasoftits.xingyi.utils.MapUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapPicker implements BDLocationListener, OnGetGeoCoderResultListener, BaiduMap.OnMapStatusChangeListener {

    private static final String LOG_TAG = "MapPicker";
    private Context mContext;
    private Dialog mMapDialog;

    private View mCancelBtn;
    private View mSelectBtn;

    //地图控件
    public TextureMapView mMapView = null;
    //百度地图对象
    public BaiduMap mBaiduMap = null;
    //定位相关声明
    public LocationClient locationClient = null;
    //自定义图标
    BitmapDescriptor mCurrentMarket = null;
    //是否首次定位
    boolean isFirstLoc = true;

    /**
     * 定位坐标
     */
    private UiSettings mUiSettings;

    private PoiSearch mPoiSearch;

    private String address;

    private String city;

    /**
     * 反地理编码，搜索模块
     */
    private GeoCoder mGeoCoder;

    private MyLocationConfiguration.LocationMode mCurrentMode;

    private LatLng mLocationLatLng;

    //回到自己
    private ImageView mMyLocationBtn;

    private Polyline mPolyline;

    private LatLng target;

    public void setMapPickerCallback(MapPickerCallback mapPickerCallback) {
        this.mMapPickerCallback = mapPickerCallback;
    }

    private MapPickerCallback mMapPickerCallback;

    public MapPicker(Context context) {
        mContext = context;
        SDKInitializer.initialize(mContext.getApplicationContext());
        initDialog();
        initView();
        initData();
        initSearch();
        initTrack();
    }

    private static final String TEST_TRACK = "[{lng:118.77982,lat:32.133526" +
            "},{lng:118.884454,lat:32.183413" +
            "},{lng:118.972991,lat:32.179501" +
            "},{lng:119.043131,lat:32.196125" +
            "},{lng:119.096023,lat:32.247932" +
            "},{lng:119.18801,lat:32.246955" +
            "},{lng:119.291495,lat:32.201014" +
            "},{lng:119.39383,lat:32.224476" +
            "},{lng:119.39383,lat:32.224476" +
            "},{lng:119.513412,lat:32.259657" +
            "},{lng:119.619196,lat:32.235227" +
            "},{lng:119.631845,lat:32.199058" +
            "},{lng:119.713483,lat:32.277243" +
            "},{lng:119.783622,lat:32.329003" +
            "},{lng:119.885957,lat:32.259657" +
            "},{lng:119.914703,lat:32.147223" +
            "},{lng:119.967595,lat:32.024853}]";

    private void initTrack() {
        List<LatLng> locList = new ArrayList<>();
        try {
            JSONArray jsonArr = new JSONArray(TEST_TRACK);

            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject job = (JSONObject) jsonArr.get(i);
                LatLng loc = new LatLng(job.getDouble("lat"), job.getDouble("lng"));
                locList.add(loc);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logger.d(LOG_TAG, "test track");
        Logger.d(LOG_TAG, locList.toString());

        /**
         * 配置线段图层参数类： PolylineOptions
         * ooPolyline.width(13)：线宽
         * ooPolyline.color(0xAAFF0000)：线条颜色红色
         * ooPolyline.points(latLngs)：List<LatLng> latLngs位置点，将相邻点与点连成线就成了轨迹了
         */
        OverlayOptions ooPolyline = new PolylineOptions().width(13).color(0xAAFF0000).points(locList);

        //在地图上画出线条图层，mPolyline：线条图层
        mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
        mPolyline.setZIndex(3);

        //设置缩放中点LatLng target，和缩放比例
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(LocatinUtils.calCentralPoint(locList)).zoom(LocatinUtils.calMapLevel(locList));

        //地图设置缩放状态
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    private void initDialog() {
        if (mMapDialog == null) {
            mMapDialog = new Dialog(mContext, R.style.map_dialog);
            mMapDialog.setCancelable(false);
            mMapDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mMapDialog.setContentView(R.layout.map_picker);
            Window window = mMapDialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            manager.getDefaultDisplay().getMetrics(dm);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = dm.widthPixels;
            window.setAttributes(lp);

            mMapDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    Logger.d(LOG_TAG, "OnDismissListener ->");
                    onDestroy();
                }
            });
        }
    }

    private void initSearch() {
        mPoiSearch = PoiSearch.newInstance();
    }

    private void initData() {
        locationClient.start();//开始定位
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);//设置为一般地图
        //mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);//设置为卫星地图
        mBaiduMap.setTrafficEnabled(false);//开启交通图
        mUiSettings = mBaiduMap.getUiSettings();

        mUiSettings.setOverlookingGesturesEnabled(false);
    }


    private void initView() {
        mMapView = mMapDialog.findViewById(R.id.map_view);
        mBaiduMap = mMapView.getMap();

        mCancelBtn = mMapDialog.findViewById(R.id.tv_cancel);
        mSelectBtn = mMapDialog.findViewById(R.id.tv_select);

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        mSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location location = new Location();
                location.setAddress(address);
                location.setCity(city);
                location.setLat(mLocationLatLng.latitude);
                location.setLng(mLocationLatLng.longitude);

                if (mMapPickerCallback != null) {
                    mMapPickerCallback.locationSelect(location);
                }
                mMapDialog.hide();
            }
        });

        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder().zoom(18).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);

        //地图状态改变相关监听
        mBaiduMap.setOnMapStatusChangeListener(this);

        //开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        //定位图层显示方式
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;

        /**
         * 设置定位图层配置信息，只有先允许定位图层后设置定位图层配置信息才会生效
         * customMarker用户自定义定位图标
         * enableDirection是否允许显示方向信息
         * locationMode定位图层显示方式
         */
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, null));


        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.e(LOG_TAG, "onMapClick -> ");
                /**
                 * 发起反向搜索
                 */
                reverseSearch(latLng);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                Log.e(LOG_TAG, "onMapClick -> ");
                /**
                 * 发起反向搜索
                 */
                reverseSearch(mapPoi.getPosition());
                return false;
            }
        });

        locationClient = new LocationClient(mContext.getApplicationContext());//实例化LocationClient类
        locationClient.registerLocationListener(this);//注册监听函数
        //地图状态改变相关监听


        this.setLocationOption();//设置定位参数

        mMyLocationBtn = mMapDialog.findViewById(R.id.my_location);

        mMyLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(LOG_TAG, "mMyLocationBtn");
                backMyLocation();
            }
        });
    }


    /**
     * 地图状态改变结束
     *
     * @param mapStatus 地图状态改变结束后的地图状态
     */
    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {
//        Log.e(LOG_TAG, "onMapStatusChangeFinish -> ");
//        //地图操作的中心点
//        LatLng cenpt = mapStatus.target;
//        mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(cenpt));
    }


    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        Log.e(LOG_TAG, "onReceiveLocation -> ");

        //如果bdLocation为空或mapView销毁后不再处理新数据接收的位置
        if (bdLocation == null || mBaiduMap == null) {
            return;
        }
        //是否是第一次定位
        if (isFirstLoc) {
            isFirstLoc = false;
            MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngZoom(mLocationLatLng, 18);
            mBaiduMap.animateMapStatus(msu);
            changeMapPoint(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()), bdLocation.getAddrStr());
            if (locationClient.isStarted()) {
                // 获得位置之后停止定位
                locationClient.stop();
            }
        }

        //获取坐标，待会用于POI信息点与定位的距离

        //获取城市，待会用于POISearch
        city = bdLocation.getCity();

        address = bdLocation.getAddrStr();
        //创建GeoCoder实例对象
        mGeoCoder = GeoCoder.newInstance();
        //发起反地理编码请求(经纬度->地址信息)
        ReverseGeoCodeOption reverseGeoCodeOption = new ReverseGeoCodeOption();
        //设置反地理编码位置坐标
        reverseGeoCodeOption.location(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()));
        mGeoCoder.reverseGeoCode(reverseGeoCodeOption);

        //设置查询结果监听者
        mGeoCoder.setOnGetGeoCodeResultListener(this);

    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        Log.e(LOG_TAG, "onGetGeoCodeResult -> ");

        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(mContext, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        changeMapPoint(result.getLocation(), result.getAddress());
        String strInfo = String.format("纬度：%f 经度：%f",
                result.getLocation().latitude, result.getLocation().longitude);
        Toast.makeText(mContext, strInfo, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        Log.e(LOG_TAG, "onGetReverseGeoCodeResult -> ");
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(mContext, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }

        changeMapPoint(result.getLocation(), result.getAddress());
        address = result.getAddress();
        city = result.getAddressDetail().city;
        Toast.makeText(mContext, result.getAddress(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {
//        Log.e(LOG_TAG, "onMapStatusChangeStart -> ");
    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus, int i) {
//        Log.e(LOG_TAG, "onMapStatusChangeStart -> ");
    }

    @Override
    public void onMapStatusChange(MapStatus mapStatus) {
//        Log.e(LOG_TAG, "onMapStatusChange -> ");
    }


    //三个状态实现地图生命周期管理
    protected void onDestroy() {
        if (locationClient.isStarted()) {
            // 获得位置之后停止定位
            locationClient.stop();
        }

        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;

        mPoiSearch.destroy();
    }


    /**
     * 设置定位参数
     */
    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//打开GPS
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
        option.setCoorType("bd0911");//返回的定位结果是百度经纬度，默认值是gcj02
        option.setScanSpan(5000);//设置发起定位请求的时间间隔为5000ms
        option.setIsNeedAddress(true);//返回的定位结果饱饭地址信息
        option.setNeedDeviceDirect(true);// 返回的定位信息包含手机的机头方向
        option.setIsNeedLocationPoiList(true);
        locationClient.setLocOption(option);
    }


    private void changeMapPoint(LatLng latLng, String address) {
        showLocation(latLng);
        showLocation(mLocationLatLng);
        if (MapUtils.judgeSamePosition(mLocationLatLng, latLng)) {
            Log.e(LOG_TAG, "same position!");
            return;
        }
        mLocationLatLng = latLng;

        mBaiduMap.clear();
        //mBaiduMap.addOverlay(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory
        //        .fromResource(R.mipmap.icon_marka)));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(latLng));

    }

    private void reverseSearch(LatLng latLng) {
        mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption()
                .location(latLng));
    }

    private void backMyLocation() {
        isFirstLoc = true;
        locationClient = new LocationClient(mContext.getApplicationContext());//实例化LocationClient类
        locationClient.registerLocationListener(this);//注册监听函数
        setLocationOption();
        locationClient.start();
    }

    private void showLocation(LatLng latLng) {
        if (latLng == null) {
            Logger.e(LOG_TAG, "====================================");
            Logger.e(LOG_TAG, "latLng:null");
            Logger.e(LOG_TAG, "====================================");
            return;
        }
        Logger.e(LOG_TAG, "====================================");
        Logger.e(LOG_TAG, "latitude:" + latLng.latitude);
        Logger.e(LOG_TAG, "longitude:" + latLng.longitude);
        Logger.e(LOG_TAG, "toString:" + latLng.toString());
        Logger.e(LOG_TAG, "====================================");
    }

    public interface MapPickerCallback {
        public void locationSelect(Location location);
    }

    public void show() {
        mMapDialog.show();
    }

    public void cancel() {
        mMapDialog.hide();
    }

    public void dismiss() {
        mMapDialog.dismiss();
    }
}
