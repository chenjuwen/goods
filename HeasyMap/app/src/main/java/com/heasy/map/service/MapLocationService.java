package com.heasy.map.service;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

/**
 * 百度地图定位服务
 */
public class MapLocationService extends AbstractMapLocationService{
    private ServiceEngine serviceEngine;
    public MapLocationService(ServiceEngine serviceEngine){
        super(serviceEngine.getBaiduMap(), serviceEngine.getContext());
        this.serviceEngine = serviceEngine;
    }

    @Override
    public void handleRealtimeLocation(BDLocation location) {
        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        LatLng destPosition = serviceEngine.getMarkerService().getCurrentMarker().getPosition();

        //步行路径规划
        serviceEngine.getRoutePlanService().walkingSearch(currentPosition, destPosition);

        //计算最新的距离
        long distance = new Double(DistanceUtil.getDistance(currentPosition, destPosition)).longValue();
        serviceEngine.getMarkerService().getTxtDistance().setText(String.valueOf(distance) + " 米（实时导航）");
    }

}
