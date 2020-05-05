package com.heasy.map;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.heasy.map.service.RouteTraceService;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RouteTraceActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {
    public static final String STATUS_START = "进行中";
    public static final String STATUS_PAUSE = "暂停";

    private RouteTraceService routeTraceService;

    //轨迹任务相关
    private Date startTime; //开始时间
    private Date endTime; //结束时间
    private long distance; //总路程
    private long usedTime = 0; //用时，单位秒
    private Long taskId; //任务ID
    private Long userId; //用户ID
    private String status = "";

    private Handler handler;
    private TaskThread taskThread;

    // Map相关
    MapView mMapView;
    BaiduMap mBaiduMap;

    // 传感器相关
    private SensorManager mSensorManager;
    private Double lastX = 0.0;

    private Button btnStart;
    private Button btnPause;
    private Button btnStop;
    private Button btnContinue;
    private Button btnHistory;
    private TextView txtStatus;
    private TextView txtTimes;
    private TextView txtDistance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_route_trace);

        // 获取传感器管理服务
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        handler = new Handler(){
            @Override
            public void handleMessage(Message message) {
                if(status.equalsIgnoreCase(STATUS_START)) {
                    int what = message.what;
                    if (what == 1) {
                        txtTimes.setText(usedTime + "秒");
                        txtDistance.setText(distance + "米");
                    }
                }
            }
        };

        initComponent();
        initBaiduMap();
        initLocationService();

        taskThread = new TaskThread();
        taskThread.setDaemon(true);
        taskThread.start();
    }

    private void initComponent(){
        btnStart = (Button)findViewById(R.id.btnStart);
        btnStart.setOnClickListener(this);

        btnPause = (Button)findViewById(R.id.btnPause);
        btnPause.setOnClickListener(this);

        btnStop = (Button)findViewById(R.id.btnStop);
        btnStop.setOnClickListener(this);

        btnContinue = (Button)findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(this);

        btnHistory = (Button)findViewById(R.id.btnHistory);
        btnHistory.setOnClickListener(this);

        txtStatus = (TextView)findViewById(R.id.txtStatus);
        txtTimes = (TextView)findViewById(R.id.txtTimes);
        txtDistance = (TextView)findViewById(R.id.txtDistance);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnStart:
                routeTraceService.startTrace();
                btnStart.setVisibility(View.GONE);
                btnPause.setVisibility(View.VISIBLE);
                btnStop.setVisibility(View.VISIBLE);
                btnContinue.setVisibility(View.GONE);
                btnHistory.setVisibility(View.GONE);

                usedTime = 0;
                distance = 0;
                status = STATUS_START;
                startTime = new Date();

                txtStatus.setText(status);
                break;
            case R.id.btnPause:
                routeTraceService.pauseTrace();
                btnStart.setVisibility(View.GONE);
                btnPause.setVisibility(View.GONE);
                btnStop.setVisibility(View.VISIBLE);
                btnContinue.setVisibility(View.VISIBLE);
                status = STATUS_PAUSE;
                txtStatus.setText(status);
                break;
            case R.id.btnStop:
                routeTraceService.stopTrace();
                btnStart.setVisibility(View.VISIBLE);
                btnPause.setVisibility(View.GONE);
                btnStop.setVisibility(View.GONE);
                btnContinue.setVisibility(View.GONE);
                btnHistory.setVisibility(View.VISIBLE);

                status = "";
                endTime = new Date();

                txtStatus.setText(status);
                break;
            case R.id.btnContinue:
                routeTraceService.continueTrace();
                btnStart.setVisibility(View.GONE);
                btnPause.setVisibility(View.VISIBLE);
                btnStop.setVisibility(View.VISIBLE);
                btnContinue.setVisibility(View.GONE);

                status = STATUS_START;

                txtStatus.setText(status);
                break;
            case R.id.btnHistory:
                break;
        }

        txtStatus.setText(status);
    }

    private void initBaiduMap() {
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.mapView);
        mBaiduMap = mMapView.getMap();

        //定位模式为普通、默认图标
        MyLocationConfiguration myLocationConfiguration = new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, true, null);
        mBaiduMap.setMyLocationConfiguration(myLocationConfiguration);
    }

    private void initLocationService(){
        routeTraceService = new RouteTraceService(mBaiduMap, getApplicationContext());
        routeTraceService.init();
    }

    /**
     * 传感器方向变化，定位方向同步变化
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(routeTraceService != null) {
            double x = sensorEvent.values[SensorManager.DATA_X];
            if (Math.abs(x - lastX) > 1.0) {
                int mCurrentDirection = (int) x;
                routeTraceService.setDirection(mCurrentDirection);
                mBaiduMap.setMyLocationData(routeTraceService.getLocationData());
            }
            lastX = x;
        }
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
        if(routeTraceService != null) {
            routeTraceService.destroy();
        }

        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;

        super.onDestroy();
    }

    class TaskThread extends Thread{
        @Override
        public void run() {
            usedTime = 0;

            while(true){
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);

                    //暂停时不算时间
                    if(status.equalsIgnoreCase(STATUS_START)) {
                        ++usedTime;
                        distance = routeTraceService.getTotalDistance();

                        Message message = Message.obtain();
                        message.what = 1;
                        handler.sendMessage(message);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
