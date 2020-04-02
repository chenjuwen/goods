package com.heasy.goods.core.service;

import com.heasy.goods.core.datastorage.DataCache;
import com.heasy.goods.core.datastorage.SQLCipherManager;

/**
 * Created by Administrator on 2017/12/29.
 */
public interface DataService extends Service {
    SQLCipherManager getSQLCipherManager();
    DataCache getGlobalMemoryDataCache();
}
