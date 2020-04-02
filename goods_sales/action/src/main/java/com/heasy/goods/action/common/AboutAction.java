package com.heasy.goods.action.common;

import com.heasy.goods.action.ActionNames;
import com.heasy.goods.core.HeasyContext;
import com.heasy.goods.core.annotation.JSActionAnnotation;
import com.heasy.goods.core.utils.VersionUtil;
import com.heasy.goods.core.webview.Action;

/**
 * 关于
 */
@JSActionAnnotation(name = ActionNames.About)
public class AboutAction implements Action {
    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        String versionName = VersionUtil.getVersionName(heasyContext.getServiceEngine().getAndroidContext());
        return versionName;
    }

}
