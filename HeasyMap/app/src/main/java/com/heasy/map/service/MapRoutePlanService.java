package com.heasy.map.service;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.BikingRouteOverlay;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteLine;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import java.util.List;

/**
 * 百度地图路径规划服务
 */
public class MapRoutePlanService implements OnGetRoutePlanResultListener {
    private RoutePlanSearch mSearch = null;
    private OverlayManager overlayManager;

    private ServiceEngine serviceEngine;
    public MapRoutePlanService(ServiceEngine serviceEngine){
        this.serviceEngine = serviceEngine;
    }

    public void init(){
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
    }

    /**
     * 步行
     * @param from
     * @param to
     */
    public void walkingSearch(LatLng from, LatLng to){
        PlanNode fromNode = PlanNode.withLocation(from);
        PlanNode toNode = PlanNode.withLocation(to);
        mSearch.walkingSearch((new WalkingRoutePlanOption()).from(fromNode).to(toNode));
    }

    /**
     * 步行
     * @param walkingRouteResult
     */
    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
        if(walkingRouteResult == null || walkingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR){
            return;
        }

        if (walkingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
            List<WalkingRouteLine> listList = walkingRouteResult.getRouteLines();
            if(listList != null && listList.size() > 0){
                WalkingRouteOverlay overlay = new WalkingRouteOverlay(serviceEngine.getBaiduMap());

                //删除旧的路径
                removeOverlay();
                overlayManager = overlay;

                overlay.setData(listList.get(0));
                overlay.addToMap();
                //overlay.zoomToSpan();

                //更新地图中心点
                //MapStatus mapStatus = new MapStatus.Builder().target(listList.get(0).getTerminal().getLocation()).zoom(MapLocationService.DEFAULT_ZOOM).build();
                //mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));
            }
        }
    }

    /**
     * 骑行
     * @param from
     * @param to
     */
    public void bikingSearch(LatLng from, LatLng to){
        PlanNode fromNode = PlanNode.withLocation(from);
        PlanNode toNode = PlanNode.withLocation(to);
        mSearch.bikingSearch((new BikingRoutePlanOption()).from(fromNode).to(toNode));
    }

    /**
     * 骑行
     * @param bikingRouteResult
     */
    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {
        if(bikingRouteResult == null || bikingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR){
            return;
        }

        if (bikingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
            List<BikingRouteLine> listList = bikingRouteResult.getRouteLines();
            if(listList != null && listList.size() > 0){
                BikingRouteOverlay overlay = new BikingRouteOverlay(serviceEngine.getBaiduMap());

                //删除旧的路径
                removeOverlay();
                overlayManager = overlay;

                overlay.setData(listList.get(0));
                overlay.addToMap();
            }
        }
    }

    /**
     * 驾车
     * @param from
     * @param to
     */
    public void drivingSearch(LatLng from, LatLng to){
        PlanNode fromNode = PlanNode.withLocation(from);
        PlanNode toNode = PlanNode.withLocation(to);
        mSearch.drivingSearch((new DrivingRoutePlanOption())
                .from(fromNode)
                .to(toNode)
                .policy(DrivingRoutePlanOption.DrivingPolicy.ECAR_DIS_FIRST) //驾乘检索策略常量：最短距离
                .trafficPolicy(DrivingRoutePlanOption.DrivingTrafficPolicy.ROUTE_PATH_AND_TRAFFIC));
    }

    /**
     * 驾车
     * @param drivingRouteResult
     */
    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
        if(drivingRouteResult == null || drivingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR){
            return;
        }

        if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
            List<DrivingRouteLine> listList = drivingRouteResult.getRouteLines();
            if(listList != null && listList.size() > 0){
                DrivingRouteOverlay overlay = new DrivingRouteOverlay(serviceEngine.getBaiduMap());

                //删除旧的路径
                removeOverlay();
                overlayManager = overlay;

                overlay.setData(listList.get(0));
                overlay.addToMap();
            }
        }
    }

    /**
     * 公交
     * @param city
     * @param from
     * @param to
     */
    public void transitSearch(String city, LatLng from, LatLng to){
        PlanNode fromNode = PlanNode.withLocation(from);
        PlanNode toNode = PlanNode.withLocation(to);
        mSearch.transitSearch((new TransitRoutePlanOption()).from(fromNode).city(city).to(toNode));
    }

    /**
     * 公交
     * @param transitRouteResult
     */
    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
        if(transitRouteResult == null || transitRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR){
            return;
        }

        if (transitRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
            List<TransitRouteLine> listList = transitRouteResult.getRouteLines();
            if(listList != null && listList.size() > 0){
                TransitRouteOverlay overlay = new TransitRouteOverlay(serviceEngine.getBaiduMap());

                //删除旧的路径
                removeOverlay();
                overlayManager = overlay;

                overlay.setData(listList.get(0));
                overlay.addToMap();
            }
        }
    }

    /**
     * 室内
     * @param indoorRouteResult
     */
    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

    }

    /**
     * 跨城公交
     * @param massTransitRouteResult
     */
    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

    }

    public void destroy(){
        if (mSearch != null) {
            mSearch.destroy();
        }
    }

    public void removeOverlay() {
        if(overlayManager != null){
            overlayManager.removeFromMap();
        }
    }
}
