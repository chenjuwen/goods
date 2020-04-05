package com.heasy.map.service;

import android.content.Context;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.model.LatLng;

/**
 * Created by Administrator on 2020/4/2.
 */
public class ServiceEngine {
    private BaiduMap mBaiduMap;
    private Context context;

    private MapLocationService mapLocationService;
    private MapMarkerService mapMarkerService;
    private MapRoutePlanService mapRoutePlanService;
    private MapSearchService mapSearchService;

    private static ServiceEngine serviceEngine = null;
    public static ServiceEngine getInstance(BaiduMap mBaiduMap, Context context){
        if(serviceEngine == null){
            serviceEngine = new ServiceEngine(mBaiduMap, context);
        }
        return serviceEngine;
    }

    public static ServiceEngine getInstance(){
        if(serviceEngine == null){
            throw new RuntimeException("serviceEngine is null");
        }
        return serviceEngine;
    }

    private ServiceEngine(BaiduMap mBaiduMap, final Context context){
        this.mBaiduMap = mBaiduMap;
        this.context = context;
        init();
    }

    private void init(){
        //路径规划
        mapRoutePlanService = new MapRoutePlanService(this);
        mapRoutePlanService.init();

        //地理编码
        mapSearchService = new MapSearchService(this);
        mapSearchService.init();

        mapSearchService.setReverseGeoCodeResultCallback(new ReverseGeoCodeResultCallback(){
            @Override
            public void execute(String address, LatLng latLng) {
                Toast.makeText(context, address, Toast.LENGTH_LONG).show();
            }
        });

        mapSearchService.setGeoCodeResultCallback(new GeoCodeResultCallback(){
            @Override
            public void execute(LatLng latLng) {
                String message = "纬度：" + latLng.latitude + "，经度：" + latLng.longitude;
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });

        //覆盖物
        mapMarkerService = new MapMarkerService(this);
        mapMarkerService.init();

        //定位
        mapLocationService = new MapLocationService(this);
        mapLocationService.init();
    }

    public void destroy(){
        if(mapLocationService != null) mapLocationService.destroy();
        if(mapMarkerService != null) mapMarkerService.destroy();
        if(mapSearchService != null) mapSearchService.destroy();
        if(mapRoutePlanService != null) mapRoutePlanService.destroy();
    }

    public BaiduMap getBaiduMap() {
        return mBaiduMap;
    }

    public Context getContext() {
        return context;
    }

    public MapLocationService getLocationService() {
        return mapLocationService;
    }

    public MapMarkerService getMarkerService() {
        return mapMarkerService;
    }

    public MapRoutePlanService getRoutePlanService() {
        return mapRoutePlanService;
    }

    public MapSearchService getSearchService() {
        return mapSearchService;
    }

}
