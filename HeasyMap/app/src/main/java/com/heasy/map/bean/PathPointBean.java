package com.heasy.map.bean;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by Administrator on 2020/9/20.
 * 路径轨迹点
 *
 * 一个任务可以有多个参与者，每个参与者可以进行多次作业，每次作业对应一条路径轨迹，一条路径轨迹包含若干个轨迹点。
 */
public class PathPointBean {
    /**
     * 流水号
     */
    private Long id;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 批次值
     */
    private Long batch;

    /**
     * 轨迹点的经纬度
     */
    private LatLng latLng;

    /**
     * 当前时间的毫秒值
     */
    private Long times;

    /**
     * 是否已在地图上绘制
     */
    private boolean drawn;

}
