package com.heasy.map.service;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

/**
 * 百度地图定位服务
 */
public class CompassLocationService extends AbstractMapLocationService{
    public CompassLocationService(BaiduMap baiduMap, Context context){
        super(baiduMap, context);
    }

    @Override
    public void handleRealtimeLocation(BDLocation location) {
        if(ServiceEngine.getInstance().getMarkerService().getCurrentMarker() != null) {
            LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
            LatLng destPosition = ServiceEngine.getInstance().getMarkerService().getCurrentMarker().getPosition();

            //计算最新的距离
            long distance = new Double(DistanceUtil.getDistance(currentPosition, destPosition)).longValue();
            Log.i("CompassLocationService", String.valueOf(distance) + " 米");
        }
    }

}
