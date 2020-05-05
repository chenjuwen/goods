package com.heasy.map.service;

import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import org.json.JSONObject;

import java.util.List;

/**
 * 百度地图地理编码服务
 */
public class MapSearchService implements OnGetGeoCoderResultListener{
    private static final String TAG = MapSearchService.class.getName();

    private GeoCoder geoCoder = null;
    private ReverseGeoCodeResultCallback reverseGeoCodeResultCallback;
    private ReverseGeoCodeResultCallback reverseGeoCodeResultCallback2;

    private GeoCodeResultCallback geoCodeResultCallback;
    private GeoCodeResultCallback geoCodeResultCallback2;

    private ServiceEngine serviceEngine;
    public MapSearchService(ServiceEngine serviceEngine){
        this.serviceEngine = serviceEngine;
    }

    public void init(){
        geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(this);
    }

    /**
     * 根据经纬度信息获取地址信息
     * @param latitude 纬度
     * @param longitude 经度
     */
    public void reverseGeoCode(double latitude, double longitude){
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(latitude, longitude))
                .newVersion(1)
                .radius(500));
    }

    public void reverseGeoCode(double latitude, double longitude, ReverseGeoCodeResultCallback reverseGeoCodeResultCallback2){
        this.reverseGeoCodeResultCallback2 = reverseGeoCodeResultCallback2;
        reverseGeoCode(latitude, longitude);
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result != null && result.error == SearchResult.ERRORNO.NO_ERROR) {
            Log.i(TAG, "1. " + result.getAddress());
            Log.i(TAG, "3. " + result.getSematicDescription());
            //Log.i(TAG, "4. " + result.getAdcode());
            //Log.i(TAG, "6. " + result.getCityCode());

            List<PoiInfo> list = result.getPoiList();
            if(list != null){
                for(PoiInfo info : list){
                    //Log.i(TAG, info.toString());
                    Log.i(TAG, info.name + ", " + info.address + " " + info.direction + " " + info.distance + "米, " + info.location.latitude + ", " + info.location.longitude);
                }
            }

            //地址信息
            String address = result.getAddress();
            if(address.indexOf("省") > 0){
                address = address.substring(address.indexOf("省")+1);
            }
            if(result.getSematicDescription() != null){
                address += result.getSematicDescription();
            }

            LatLng latLng = result.getLocation();

            if(reverseGeoCodeResultCallback2 != null){
                reverseGeoCodeResultCallback2.execute(address, latLng);
                reverseGeoCodeResultCallback2 = null;
            }else if(reverseGeoCodeResultCallback != null){
                reverseGeoCodeResultCallback.execute(address, latLng);
            }
        }
    }

    /**
     * 根据地址获取经纬度信息
     * @param city
     * @param address
     */
    public void geocode(String city, String address){
        geoCoder.geocode(new GeoCodeOption().city(city).address(address));
    }

    public void geocode(String city, String address, GeoCodeResultCallback geoCodeResultCallback2){
        this.geoCodeResultCallback2 = geoCodeResultCallback2;
        geocode(city, address);
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result != null && result.error == SearchResult.ERRORNO.NO_ERROR) {
            LatLng latLng = result.getLocation();

            if(geoCodeResultCallback2 != null){
                geoCodeResultCallback2.execute(latLng);
                geoCodeResultCallback2 = null;
            }else if(geoCodeResultCallback != null){
                geoCodeResultCallback.execute(latLng);
            }
        }
    }

    public void destroy(){
        if(geoCoder != null){
            geoCoder.destroy();
        }
    }

    public void setReverseGeoCodeResultCallback(ReverseGeoCodeResultCallback reverseGeoCodeResultCallback) {
        this.reverseGeoCodeResultCallback = reverseGeoCodeResultCallback;
    }

    public void setGeoCodeResultCallback(GeoCodeResultCallback geoCodeResultCallback) {
        this.geoCodeResultCallback = geoCodeResultCallback;
    }
}
