package com.heasy.map.service;

import android.content.Context;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

/**
 * 百度地图定位服务
 */
public abstract class AbstractMapLocationService extends BDAbstractLocationListener{
    public static final String TAG = AbstractMapLocationService.class.getSimpleName();
    public static float DEFAULT_ZOOM =  16.0f;

    LocationClient mLocationClient;
    boolean isFirstLocation = true; // 是否首次定位
    boolean realtimeLocation = false; //是否实时定位

    float radius;
    double latitude = 0.0; //纬度
    double longitude = 0.0; //经度
    String city = "";
    String address;
    int direction = 0;

    BaiduMap baiduMap;
    Context context;

    public AbstractMapLocationService(BaiduMap baiduMap, Context context){
        this.baiduMap = baiduMap;
        this.context = context;
    }

    public void init(){
        // 开启定位图层
        baiduMap.setMyLocationEnabled(true);

        // 定位初始化
        mLocationClient = new LocationClient(context);
        mLocationClient.registerLocationListener(this);

        mLocationClient.setLocOption(getLocationClientOption());
        mLocationClient.start();
    }

    public LocationClientOption getLocationClientOption(){
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型：百度经纬度坐标
        option.setScanSpan(2000); //发起定位请求的间隔，单位毫秒ms，默认0，即仅定位一次
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy); //定位模式：高精度
        option.setIsNeedAddress(true);
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，结果类似于“在北京天安门附近”
        option.setIgnoreKillProcess(false); //stop的时候杀死这个进程
        return option;
    }

    public MyLocationData getLocationData(){
        MyLocationData locationData = new MyLocationData.Builder()
                .accuracy(radius)
                .direction(direction)  // 方向信息，顺时针0-360
                .latitude(latitude)
                .longitude(longitude)
                .build();
        return locationData;
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        if (location == null) {
            return;
        }

        radius = location.getRadius(); //定位精度
        latitude = location.getLatitude(); //纬度坐标
        longitude = location.getLongitude(); //经度坐标
        //Log.i(TAG, radius + " : " + latitude +" : " + longitude);

        //Log.i(TAG, "1. " + location.getProvince()); //省
        //Log.i(TAG, "2. " + location.getCity()); //市
        //Log.i(TAG, "3. " + location.getDistrict()); //区县
        //Log.i(TAG, "4. " + location.getStreet()); //街道信息
        //Log.i(TAG, "5. " + location.getAddrStr()); //详细地址信息
        //Log.i(TAG, "6. " + location.getLocationDescribe()); //位置语义化结果

        String tmpAddr = location.getAddrStr();
        if(tmpAddr != null){
            address = tmpAddr.replaceFirst("中国", "");

            String locationDescribe = location.getLocationDescribe();
            if(locationDescribe != null){
                locationDescribe = locationDescribe.replaceFirst("在", "");
                address += locationDescribe;
            }
        }

        String tmpCity = location.getCity();
        if(tmpCity != null && tmpCity.length() > 0){
            city = tmpCity.replaceFirst("市", "");
        }

        baiduMap.setMyLocationData(getLocationData());

        //首次定位
        if (isFirstLocation) {
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(new LatLng(latitude, longitude)).zoom(DEFAULT_ZOOM); //初始缩放
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build())); //定位到指定位置
        }

        if(realtimeLocation){ //实时更新路径规划
            handleRealtimeLocation(location);
        }else{
            if (isFirstLocation) {
                isFirstLocation = false;
            }
        }
    }

    public abstract void handleRealtimeLocation(BDLocation location);

    public void destroy(){
        // 退出时销毁定位
        if(mLocationClient != null) {
            mLocationClient.stop();
        }
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public LatLng getPosition(){
        return new LatLng(latitude, longitude);
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public void setRealtimeLocation(boolean realtimeLocation) {
        this.realtimeLocation = realtimeLocation;
    }
}
