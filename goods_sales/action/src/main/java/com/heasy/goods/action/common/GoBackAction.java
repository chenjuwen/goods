package com.heasy.goods.action.common;

import com.heasy.goods.core.HeasyContext;
import com.heasy.goods.core.annotation.JSActionAnnotation;
import com.heasy.goods.action.ActionNames;
import com.heasy.goods.core.webview.Action;

/**
 * 返回上一页
 */
@JSActionAnnotation(name = ActionNames.GoBack)
public class GoBackAction implements Action {
    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        heasyContext.getJsInterface().goBack();
        return SUCCESS;
    }
}
