package com.heasy.goods.core.service;

import com.heasy.goods.core.configuration.ConfigBean;

/**
 * Created by Administrator on 2017/12/29.
 */
public interface ConfigurationService extends Service {
    ConfigBean getConfigBean();
}
