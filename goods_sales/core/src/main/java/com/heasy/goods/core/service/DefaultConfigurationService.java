package com.heasy.goods.core.service;

import com.heasy.goods.core.configuration.ConfigBean;
import com.heasy.goods.core.configuration.DefaultConfigLoader;

/**
 * Created by Administrator on 2018/11/10.
 */
public class DefaultConfigurationService extends AbstractService implements ConfigurationService {
    private ConfigBean configBean;

    @Override
    public void init() {
        initConfigBean();
        successInit = true;
    }

    @Override
    public boolean isInit() {
        return successInit;
    }

    private void initConfigBean(){
        DefaultConfigLoader loader = new DefaultConfigLoader();
        configBean = loader.loadFromAssets(getHeasyContext().getServiceEngine().getAndroidContext(), ConfigBean.CONFIG_FILE_NAME);
    }

    @Override
    public void unInit() {
        successInit = false;
        configBean = null;
    }

    @Override
    public ConfigBean getConfigBean() {
        return configBean;
    }

}
