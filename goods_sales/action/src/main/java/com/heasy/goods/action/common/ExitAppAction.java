package com.heasy.goods.action.common;

import com.heasy.goods.core.HeasyContext;
import com.heasy.goods.core.annotation.JSActionAnnotation;
import com.heasy.goods.core.event.ExitAppEvent;
import com.heasy.goods.core.webview.Action;
import com.heasy.goods.action.ActionNames;

/**
 * 退出应用
 */
@JSActionAnnotation(name = ActionNames.ExitApp)
public class ExitAppAction implements Action {
    @Override
    public String execute(final HeasyContext heasyContext, String jsonData, String extend) {
        heasyContext.getServiceEngine().getEventService().postEvent(new ExitAppEvent(this));
        return SUCCESS;
    }

}
