package com.heasy.goods.core.service;

import com.heasy.goods.core.event.Event;

/**
 * Created by Administrator on 2017/12/29.
 */
public interface EventService extends Service {
    void register(Object subscriber);
    void unregister(Object subscriber);
    void unregisterAll();
    void postEvent(Event event);
    void cancelEventDelivery(Event event);
}
