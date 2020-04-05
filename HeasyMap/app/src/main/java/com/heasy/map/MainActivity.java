package com.heasy.map;

import android.app.Activity;
import android.content.Intent;
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

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.model.LatLng;
import com.heasy.map.service.ConfigBean;
import com.heasy.map.service.ConfigService;
import com.heasy.map.service.MapLocationService;
import com.heasy.map.service.ServiceEngine;

import java.util.Collection;

public class MainActivity extends Activity implements SensorEventListener {
    private Button btnSettings;
    private Button btnToolbar;
    private LinearLayout toolbarContainer;
    private Button btnAddCurrentOverlay;
    private Button btnClearRoute;
    private Button btnCompass;
    private Button btnTrace;
    private Button btnRefresh;

    // 传感器相关
    private SensorManager mSensorManager;
    private Double lastX = 0.0;

    // Map相关
    MapView mMapView;
    BaiduMap mBaiduMap;

    private ServiceEngine serviceEngine;

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
        initServiceEngine();
        initOveralys();
    }

    private void initViewComponents() {
        btnSettings = (Button)findViewById(R.id.btnSettings);
        btnToolbar = (Button) findViewById(R.id.btnToolbar);
        toolbarContainer = (LinearLayout)findViewById(R.id.toolbarContainer);
        btnAddCurrentOverlay = (Button)findViewById(R.id.btnAddCurrentOverlay);
        btnClearRoute = (Button)findViewById(R.id.btnClearRoute);
        btnCompass = (Button)findViewById(R.id.btnCompass);
        btnTrace = (Button)findViewById(R.id.btnTrace);
        btnRefresh = (Button)findViewById(R.id.btnRefresh);

        //设置
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        //工具
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

        //当前位置打标签
        btnAddCurrentOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
                ConfigBean configBean = new ConfigBean(serviceEngine.getLocationService().getLatitude(), serviceEngine.getLocationService().getLongitude());
                serviceEngine.getMarkerService().addOverlay(configBean);

                //更新地图中心点
                MapStatus mapStatus = new MapStatus.Builder()
                        .target(serviceEngine.getLocationService().getPosition())
                        .zoom(MapLocationService.DEFAULT_ZOOM)
                        .build();
                serviceEngine.getBaiduMap().animateMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));
            }
        });

        //清除路径
        btnClearRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
                serviceEngine.getRoutePlanService().removeOverlay();

                //停止导航
                serviceEngine.getLocationService().setRealtimeLocation(false);
            }
        });

        //罗盘仪
        btnCompass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
                Intent intent = new Intent(getApplicationContext(), CompassActivity.class);
                startActivity(intent);
            }
        });

        //路径轨迹
        btnTrace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
                Intent intent = new Intent(getApplicationContext(), RouteTraceActivity.class);
                startActivity(intent);
            }
        });

        //刷新
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
                initOveralys();
            }
        });
    }

    private void hide(){
        toolbarContainer.setVisibility(View.GONE);
        mBaiduMap.hideInfoWindow();
    }

    private void initBaiduMap() {
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.mapView);
        mBaiduMap = mMapView.getMap();

        //定位模式为普通LocationMode.NORMAL、默认图标
        MyLocationConfiguration myLocationConfiguration = new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, true, null);
        mBaiduMap.setMyLocationConfiguration(myLocationConfiguration);

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

        //单击地图
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
                serviceEngine.getMarkerService().addOverlay(new ConfigBean(latLng.latitude, latLng.longitude));
            }
        });
    }

    private void initServiceEngine() {
        serviceEngine = ServiceEngine.getInstance(mBaiduMap, MainActivity.this);
    }

    public void initOveralys(){
        mBaiduMap.clear();

        ConfigService.copyConfigFile();
        ConfigService.loadConfig();

        Collection<ConfigBean> configBeanList = ConfigService.getConfigMap().values();
        if(configBeanList != null){
            for(ConfigBean bean : configBeanList){
                serviceEngine.getMarkerService().addOverlay(bean);
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
            serviceEngine.getLocationService().setDirection(mCurrentDirection);
            mBaiduMap.setMyLocationData(serviceEngine.getLocationService().getLocationData());
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
        serviceEngine.destroy();

        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;

        super.onDestroy();
    }
}
