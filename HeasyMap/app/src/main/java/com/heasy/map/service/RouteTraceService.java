package com.heasy.map.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.heasy.map.utils.PreferenceUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 实时记录、描绘路径轨迹
 */
public class RouteTraceService extends AbstractMapLocationService{
    private static final String TAG = RouteTraceService.class.getName();

    private List<LatLng> positionList = new ArrayList<>(); //路径轨迹关键坐标点
    private long totalDistance = 0; //总路程
    private boolean enableTrace = false; //是否记录路径轨迹
    private Polyline polyline; //折线

    private BitmapDescriptor mRedTexture = BitmapDescriptorFactory.fromAsset("Icon_road_red_arrow.png");
    private BitmapDescriptor mBlueTexture = BitmapDescriptorFactory.fromAsset("Icon_road_blue_arrow.png");
    private BitmapDescriptor mGreenTexture = BitmapDescriptorFactory.fromAsset("Icon_road_green_arrow.png");

    public RouteTraceService(BaiduMap baiduMap, Context context){
        super(baiduMap, context);
        this.realtimeLocation = true;
    }

    @Override
    public void handleRealtimeLocation(BDLocation location) {
        if(enableTrace){
            LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

            if(positionList.isEmpty()){
                positionList.add(currentPosition);
            }else{
                LatLng provPosition = positionList.get(positionList.size()-1);

                //计算两点之间的距离，单位为 米
                long distance = new Double(DistanceUtil.getDistance(provPosition, currentPosition)).longValue();
                Log.i(TAG, "distance=" + distance);

                String traceInterval = PreferenceUtil.getStringValue(ServiceEngine.getInstance().getContext(), "traceInterval");
                Log.d(TAG, "traceInterval=" + traceInterval);

                //两点距离大于n米时，才记录轨迹点
                if(distance > Integer.parseInt(traceInterval)){
                    positionList.add(currentPosition);
                    drowTrace();
                    totalDistance += distance; //累计路程数
                }
            }
        }
    }

    /**
     * 绘制路线轨迹
     */
    private void drowTrace(){
        baiduMap.clear();

        List<BitmapDescriptor> textureList = new ArrayList<>();
        textureList.add(mRedTexture);

        List<Integer> textureIndexList = new ArrayList<>();
        textureIndexList.add(0);

        OverlayOptions overlayOptions = new PolylineOptions()
                .width(10)
                .points(positionList)
                .dottedLine(true)
                .customTextureList(textureList)
                .textureIndex(textureIndexList);

        polyline = (Polyline) baiduMap.addOverlay(overlayOptions);
    }

    private void saveData(){
        baiduMap.snapshot(new BaiduMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                File file = new File("/mnt/sdcard/heasy_map.png");
                FileOutputStream out;
                try {
                    out = new FileOutputStream(file);
                    if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                        out.flush();
                        out.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void clear(){
        totalDistance = 0;
        baiduMap.clear();
        positionList.clear();
    }

    /**
     * 开始记录轨迹
     */
    public void startTrace(){
        clear();
        this.enableTrace = true;
    }

    /**
     * 暂停记录轨迹
     */
    public void pauseTrace(){
        this.enableTrace = false;
    }

    /**
     * 继续记录轨迹
     */
    public void continueTrace(){
        this.enableTrace = true;
    }

    /**
     * 结束记录轨迹
     */
    public void stopTrace(){
        this.enableTrace = false;
        saveData();
        clear();
    }

    public List<LatLng> getPositionList() {
        return positionList;
    }

    public long getTotalDistance() {
        return totalDistance;
    }
}
