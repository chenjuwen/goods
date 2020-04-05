package com.heasy.map.service;

import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.heasy.map.utils.PreferenceUtil;

/**
 * 百度地图定位服务
 */
public class MapLocationService extends AbstractMapLocationService{
    private static final String TAG = MapLocationService.class.getName();
    private ServiceEngine serviceEngine;

    public MapLocationService(ServiceEngine serviceEngine){
        super(serviceEngine.getBaiduMap(), serviceEngine.getContext());
        this.serviceEngine = serviceEngine;
    }

    @Override
    public void handleRealtimeLocation(BDLocation location) {
        if(serviceEngine.getMarkerService().getCurrentMarker() == null){
            return;
        }

        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        LatLng destPosition = serviceEngine.getMarkerService().getCurrentMarker().getPosition();

        String routePlanMode = PreferenceUtil.getStringValue(serviceEngine.getContext(), "routePlanMode");
        Log.d(TAG, "routePlanMode=" + routePlanMode);

        switch (routePlanMode){
            case "步行":
                serviceEngine.getRoutePlanService().walkingSearch(currentPosition, destPosition);
                break;
            case "骑行":
                serviceEngine.getRoutePlanService().bikingSearch(currentPosition, destPosition);
                break;
            case "驾车":
                serviceEngine.getRoutePlanService().drivingSearch(currentPosition, destPosition);
                break;
            case "公交":
                serviceEngine.getRoutePlanService().transitSearch(serviceEngine.getLocationService().city, currentPosition, destPosition);
                break;
            default:
                serviceEngine.getRoutePlanService().walkingSearch(currentPosition, destPosition);
        }

        //计算最新的距离
        long distance = new Double(DistanceUtil.getDistance(currentPosition, destPosition)).longValue();
        serviceEngine.getMarkerService().getTxtDistance().setText(String.valueOf(distance) + " 米");
    }

}
