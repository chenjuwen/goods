package com.heasy.map.service;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

/**
 * 百度地图定位服务
 */
public class CompassLocationService extends AbstractMapLocationService{
    private TextView txtMessage;

    public CompassLocationService(BaiduMap baiduMap, Context context, TextView txtMessage){
        super(baiduMap, context);
        this.txtMessage = txtMessage;
    }

    @Override
    public void handleRealtimeLocation(BDLocation location) {
        if(ServiceEngine.getInstance().getMarkerService().getCurrentMarker() != null) {
            LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
            LatLng destPosition = ServiceEngine.getInstance().getMarkerService().getCurrentMarker().getPosition();

            //计算最新的距离
            long distance = new Double(DistanceUtil.getDistance(currentPosition, destPosition)).longValue();
            txtMessage.setText("距离目标 " + distance + " 米");
        }
    }

}
