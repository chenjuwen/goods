package com.heasy.map.service;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

/**
 * 百度地图定位服务
 */
public class MapLocationService extends BDAbstractLocationListener{
    public static float DEFAULT_ZOOM =  16.0f;

    private LocationClient mLocationClient;
    boolean isFirstLocation = true; // 是否首次定位
    boolean onlyOneLocation = false;

    private float radius;
    private double latitude = 0.0; //纬度
    private double longitude = 0.0; //经度
    private String city = "";
    private String address;
    private int direction = 0;

    private BaiduMap mBaiduMap;

    public MapLocationService(BaiduMap mBaiduMap){
        this.mBaiduMap = mBaiduMap;
    }

    /**
     *
     * @param context
     * @param onlyOneLocation 是否只定位一次
     */
    public void start(Context context, boolean onlyOneLocation){
        this.onlyOneLocation = onlyOneLocation;

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        //定位模式、默认图标
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, true, null));
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.overlook(0);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

        // 定位初始化
        mLocationClient = new LocationClient(context);
        mLocationClient.registerLocationListener(this);

        mLocationClient.setLocOption(getLocationClientOption());
        mLocationClient.start();
    }

    private LocationClientOption getLocationClientOption(){
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型：百度经纬度坐标

        //发起定位请求的间隔，单位毫秒ms，默认0，即仅定位一次
        if(onlyOneLocation){
            option.setScanSpan(0);
        }else{
            option.setScanSpan(2000);
        }

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
        Log.i("LocationDemo", radius + " : " + latitude +" : " + longitude);

        Log.i("LocationDemo", "1. " + location.getProvince()); //省
        Log.i("LocationDemo", "2. " + location.getCity()); //市
        Log.i("LocationDemo", "3. " + location.getDistrict()); //区县
        Log.i("LocationDemo", "4. " + location.getStreet()); //街道信息
        Log.i("LocationDemo", "5. " + location.getAddrStr()); //详细地址信息
        Log.i("LocationDemo", "6. " + location.getLocationDescribe()); //位置语义化结果

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

        mBaiduMap.setMyLocationData(getLocationData());

        //首次定位
        if (isFirstLocation) {
            isFirstLocation = false;
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(new LatLng(latitude, longitude)).zoom(DEFAULT_ZOOM); //初始缩放
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build())); //定位到指定位置
        }
    }

    public void stop(){
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
}
