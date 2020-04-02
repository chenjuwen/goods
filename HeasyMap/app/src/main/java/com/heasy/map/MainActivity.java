package com.heasy.map;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.heasy.map.service.ConfigBean;
import com.heasy.map.service.ConfigService;
import com.heasy.map.service.MapLocationService;
import com.heasy.map.service.MapMarkerService;
import com.heasy.map.service.MapRoutePlanService;
import com.heasy.map.service.MapSearchService;

import java.util.Collection;

public class MainActivity extends Activity implements SensorEventListener {
    private Button btnToolbar;
    private LinearLayout toolbarContainer;
    private Button btnAddCurrentOverlay;
    private Button btnClearRoute;
    private Button btnRefresh;

    // 传感器相关
    private SensorManager mSensorManager;
    private Double lastX = 0.0;

    // Map相关
    MapView mMapView;
    BaiduMap mBaiduMap;

    private MapLocationService mapLocationService;
    private MapMarkerService mapMarkerService;
    private MapSearchService mapSearchService;
    private MapRoutePlanService mapRoutePlanService;

    private ReverseGeoCodeResultCallback callback1 = new ReverseGeoCodeResultCallback(){
        @Override
        public void execute(String address, LatLng latLng) {
            Toast.makeText(MainActivity.this, address, Toast.LENGTH_LONG).show();
        }
    };
    private GeoCodeResultCallback callback2 = new GeoCodeResultCallback(){
        @Override
        public void execute(LatLng latLng) {
            String message = "纬度：" + latLng.latitude + "，经度：" + latLng.longitude;
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        // 获取传感器管理服务
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        initViewComponents();
        initBaiduMap();
        initService();
        initOveralys();
    }

    private void initViewComponents() {
        btnToolbar = (Button) findViewById(R.id.btnToolbar);
        toolbarContainer = (LinearLayout)findViewById(R.id.toolbarContainer);
        btnAddCurrentOverlay = (Button)findViewById(R.id.btnAddCurrentOverlay);
        btnClearRoute = (Button)findViewById(R.id.btnClearRoute);
        btnRefresh = (Button)findViewById(R.id.btnRefresh);

        btnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(toolbarContainer.getVisibility() == View.VISIBLE){
                    toolbarContainer.setVisibility(View.GONE);
                }else{
                    toolbarContainer.setVisibility(View.VISIBLE);
                }
            }
        });

        btnAddCurrentOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbarContainer.setVisibility(View.GONE);
                mapMarkerService.addOverlay(new ConfigBean(mapLocationService.getLatitude(), mapLocationService.getLongitude()));
            }
        });

        btnClearRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbarContainer.setVisibility(View.GONE);
                mapRoutePlanService.removeOverlay();
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbarContainer.setVisibility(View.GONE);
                initOveralys();
            }
        });
    }

    private void initBaiduMap() {
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.mapView);
        mBaiduMap = mMapView.getMap();

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                toolbarContainer.setVisibility(View.GONE);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });

        //长按地图
        mBaiduMap.setOnMapLongClickListener(new BaiduMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mapMarkerService.addOverlay(new ConfigBean(latLng.latitude, latLng.longitude));
            }
        });
    }

    private void initService() {
        //定位
        mapLocationService = new MapLocationService(mBaiduMap);
        mapLocationService.start(this, true);

        //地理编码
        mapSearchService = new MapSearchService();
        mapSearchService.init();
        mapSearchService.setReverseGeoCodeResultCallback(callback1);
        mapSearchService.setGeoCodeResultCallback(callback2);

        //路径规划
        mapRoutePlanService = new MapRoutePlanService(mBaiduMap);
        mapRoutePlanService.init();

        //覆盖物
        mapMarkerService = new MapMarkerService(mBaiduMap, MainActivity.this,
                mapLocationService, mapSearchService, mapRoutePlanService);
        mapMarkerService.init();
    }

    public void initOveralys(){
        mBaiduMap.clear();

        ConfigService.copyConfigFile();
        ConfigService.loadConfig();

        Collection<ConfigBean> configBeanList = ConfigService.getConfigMap().values();
        if(configBeanList != null){
            for(ConfigBean bean : configBeanList){
                mapMarkerService.addOverlay(bean);
            }
        }
    }

    /**
     * 传感器方向变化，定位方向同步变化
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            int mCurrentDirection = (int) x;
            mapLocationService.setDirection(mCurrentDirection);
            mBaiduMap.setMyLocationData(mapLocationService.getLocationData());
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
        mapLocationService.stop();

        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;

        mapMarkerService.destroy();
        mapSearchService.destroy();
        mapRoutePlanService.destroy();

        super.onDestroy();
    }
}
