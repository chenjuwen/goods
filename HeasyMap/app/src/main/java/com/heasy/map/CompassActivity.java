package com.heasy.map;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.heasy.map.service.CompassLocationService;
import com.heasy.map.service.ConfigBean;
import com.heasy.map.service.ConfigService;
import com.heasy.map.service.ServiceEngine;

import java.util.Collection;

public class CompassActivity extends AppCompatActivity implements SensorEventListener {
    private CompassLocationService compassLocationService;

    // Map相关
    MapView mMapView;
    BaiduMap mBaiduMap;
    private BitmapDescriptor icon3;

    // 传感器相关
    private SensorManager mSensorManager;
    private Double lastX = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_compass);

        // 获取传感器管理服务
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        icon3 = BitmapDescriptorFactory.fromResource(R.drawable.icon3);

        initBaiduMap();
        initOveraly();
        initLocationService();
    }

    private void initBaiduMap() {
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.mapView);
        mBaiduMap = mMapView.getMap();

        //定位模式为罗盘仪、默认图标
        MyLocationConfiguration myLocationConfiguration = new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.COMPASS, true, null);
        mBaiduMap.setMyLocationConfiguration(myLocationConfiguration);

        MapStatus.Builder builder = new MapStatus.Builder();
        builder.overlook(0); //俯仰角度
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    public void initOveraly(){
        mBaiduMap.clear();

        if(ServiceEngine.getInstance().getMarkerService().getCurrentMarker() != null){
            Marker marker = ServiceEngine.getInstance().getMarkerService().getCurrentMarker();
            MarkerOptions markerOptions = new MarkerOptions().position(marker.getPosition()).icon(icon3).zIndex(10);
            mBaiduMap.addOverlay(markerOptions);
        }
    }

    private void initLocationService(){
        compassLocationService = new CompassLocationService(mBaiduMap, getApplicationContext());
        compassLocationService.init();
        compassLocationService.setRealtimeLocation(true);
    }

    /**
     * 传感器方向变化，定位方向同步变化
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            int mCurrentDirection = (int) x;
            compassLocationService.setDirection(mCurrentDirection);
            mBaiduMap.setMyLocationData(compassLocationService.getLocationData());
        }
        lastX = x;
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();

        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onStop() {
        //取消注册传感器监听
        mSensorManager.unregisterListener(this);

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(icon3 != null){
            icon3.recycle();
        }

        if(compassLocationService != null) {
            compassLocationService.destroy();
        }

        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;

        super.onDestroy();
    }
}
