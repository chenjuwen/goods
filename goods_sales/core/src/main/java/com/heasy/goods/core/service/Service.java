package com.heasy.goods.core.service;

import com.heasy.goods.core.HeasyContext;

/**
 * Created by Administrator on 2017/12/28.
 */
public interface Service {
    void init();
    boolean isInit();
    void unInit();
    HeasyContext getHeasyContext();
    void setHeasyContext(HeasyContext heasyContext);
}
