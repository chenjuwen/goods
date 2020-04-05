package com.heasy.map.service;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by Administrator on 2020/3/28.
 */
public interface ReverseGeoCodeResultCallback {
    void execute(String address, LatLng latLng);
}
