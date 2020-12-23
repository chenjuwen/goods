package com.heasy.map.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2020/9/20.
 */
public class TaskBean {
    /**
     * 流水号
     */
    private Long id;

    /**
     * 任务标题
     */
    private String title;

    /**
     * 任务描述
     */
    private String comments;

    /**
     * 创建者
     */
    private Long creator;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 任务状态:1-草稿，2-进行中，3-结束
     */
    private int status;

    /**
     * 任务的参与者
     */
    private List<TaskMemberBean> members = new ArrayList<>();

}
