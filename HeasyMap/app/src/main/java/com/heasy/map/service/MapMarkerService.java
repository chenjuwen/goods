package com.heasy.map.service;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.heasy.map.R;
import com.heasy.map.ReverseGeoCodeResultCallback;
import com.heasy.map.StringUtil;

import java.math.BigDecimal;

/**
 * 百度地图覆盖物服务
 */
public class MapMarkerService {
    private MapLocationService mapLocationService;
    private MapSearchService mapSearchService;
    private MapRoutePlanService mapRoutePlanService;
    private BaiduMap mBaiduMap;
    private Context context;

    private BitmapDescriptor icon1;
    private BitmapDescriptor icon2;
    private BitmapDescriptor icon3;

    private BitmapDescriptor mGreenTexture = BitmapDescriptorFactory.fromAsset("Icon_road_green_arrow.png");

    public MapMarkerService(BaiduMap mBaiduMap, Context context,MapLocationService mapLocationService,
                            MapSearchService mapSearchService, MapRoutePlanService mapRoutePlanService){
        this.mBaiduMap = mBaiduMap;
        this.context = context;
        this.mapLocationService = mapLocationService;
        this.mapSearchService = mapSearchService;
        this.mapRoutePlanService = mapRoutePlanService;
    }

    public void init(){
        icon1 = BitmapDescriptorFactory.fromResource(R.drawable.icon1);
        icon2 = BitmapDescriptorFactory.fromResource(R.drawable.icon2);
        icon3 = BitmapDescriptorFactory.fromResource(R.drawable.icon3);

        //覆盖物单击事件
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener(){
            @Override
            public boolean onMarkerClick(final Marker marker) {
                try {
                    if(marker.getExtraInfo() != null){
                        if(!"Y".equalsIgnoreCase(marker.getExtraInfo().getString("overlay"))){
                            return true;
                        }
                    }else{
                        return true;
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                    return true;
                }

                //更新地图中心点
                MapStatus mapStatus = new MapStatus.Builder().target(marker.getPosition()).zoom(MapLocationService.DEFAULT_ZOOM).build();
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));

                LayoutInflater inflater = LayoutInflater.from(context);
                View view = inflater.inflate(R.layout.layout_map_marker_popmenu, null);

                final EditText txtLatitude = (EditText)view.findViewById(R.id.txtLatitude);
                final EditText txtLongitude = (EditText)view.findViewById(R.id.txtLongitude);
                final EditText txtAddress = (EditText)view.findViewById(R.id.txtAddress);
                final EditText txtDistance = (EditText)view.findViewById(R.id.txtDistance);
                final EditText txtComments = (EditText)view.findViewById(R.id.txtComments);

                Button btnSave = (Button) view.findViewById(R.id.btnSave);
                Button btnDelete = (Button) view.findViewById(R.id.btnDelete);
                Button btnRoute = (Button)view.findViewById(R.id.btnRoute);
                Button btnClose = (Button) view.findViewById(R.id.btnClose);

                final String id = StringUtil.trimToEmpty(marker.getExtraInfo().getString("id"));

                mapSearchService.reverseGeoCode(marker.getPosition().latitude, marker.getPosition().longitude, new ReverseGeoCodeResultCallback() {
                    @Override
                    public void execute(String address, LatLng latLng) {
                        //距离，单位为米
                        long distance = new Double(DistanceUtil.getDistance(mapLocationService.getPosition(), latLng)).longValue();

                        double lat = new BigDecimal(latLng.latitude).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
                        double lon = new BigDecimal(latLng.longitude).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
                        txtLatitude.setText(String.valueOf(lat));
                        txtLongitude.setText(String.valueOf(lon));

                        txtDistance.setText(String.valueOf(distance) + " 米");
                        txtAddress.setText(address);

                        if(StringUtil.isNotEmpty(id)){
                            ConfigBean bean = ConfigService.getConfigMap().get(id);
                            if(bean != null){
                                txtComments.setText(bean.getComments());
                            }
                        }
                    }
                });

                //保存
                btnSave.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        ConfigBean bean = ConfigService.getConfigMap().get(id);
                        if(bean != null){
                            bean.setAddress(StringUtil.trimToEmpty(txtAddress.getText().toString()));
                            bean.setComments(StringUtil.trimToEmpty(txtComments.getText().toString()));
                            ConfigService.updateConfig(bean);
                        }else{
                            bean = new ConfigBean();
                            bean.setLatitude(Double.parseDouble(txtLatitude.getText().toString()));
                            bean.setLongitude(Double.parseDouble(txtLongitude.getText().toString()));
                            bean.setAddress(txtAddress.getText().toString());
                            bean.setComments(txtComments.getText().toString());
                            ConfigBean configBean = ConfigService.addConfig(bean);

                            marker.getExtraInfo().putString("id", configBean.getId());
                        }

                        Toast.makeText(context, "配置信息保存到:\n " + ConfigService.getConfigFileFullPath(), Toast.LENGTH_LONG).show();

                        mBaiduMap.hideInfoWindow();
                    }
                });

                //删除
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConfigBean bean = ConfigService.getConfigMap().get(id);
                        if(bean != null){
                            ConfigService.deleteConfig(id);
                        }

                        marker.remove();
                        mBaiduMap.hideInfoWindow();
                    }
                });

                //路径规划
                btnRoute.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mapRoutePlanService.walkingSearch(mapLocationService.getPosition(), marker.getPosition());
                        //mapRoutePlanService.bikingSearch(mapLocationService.getPosition(), marker.getPosition());
                        mBaiduMap.hideInfoWindow();
                    }
                });

                //关闭
                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBaiduMap.hideInfoWindow();
                    }
                });

                InfoWindow infoWindow = new InfoWindow(view, marker.getPosition(), -60);
                mBaiduMap.showInfoWindow(infoWindow);

                //返回false，marker会移动到地图中心
                return false;
            }
        });
    }

    public void addOverlay(ConfigBean bean){
        LatLng ll = new LatLng(bean.getLatitude(), bean.getLongitude());
        Bundle bundle = new Bundle();
        bundle.putString("id", bean.getId());
        bundle.putString("overlay", "Y");
        MarkerOptions mo = new MarkerOptions().position(ll).icon(icon2).zIndex(10).extraInfo(bundle);
        mBaiduMap.addOverlay(mo);
    }

    public void destroy(){
        if(icon1 != null){
            icon1.recycle();
        }
        if(icon2 != null){
            icon2.recycle();
        }
        if(icon3 != null){
            icon3.recycle();
        }
        if(mGreenTexture != null){
            mGreenTexture.recycle();
        }
    }
}
