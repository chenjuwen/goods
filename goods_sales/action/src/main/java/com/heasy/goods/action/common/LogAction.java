package com.heasy.goods.action.common;

import com.heasy.goods.core.annotation.JSActionAnnotation;
import com.heasy.goods.core.utils.StringUtil;
import com.heasy.goods.action.ActionNames;
import com.heasy.goods.core.webview.Action;
import com.heasy.goods.core.HeasyContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志记录
 */
@JSActionAnnotation(name = ActionNames.Log)
public class LogAction implements Action {
    private static final Logger logger = LoggerFactory.getLogger(LogAction.class);

    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        String logLevel = StringUtil.trimToEmpty(extend).toUpperCase();
        String logMessage = StringUtil.trimToEmpty(jsonData);

        switch (logLevel){
            case "INFO":
                logger.info(logMessage);
                break;
            case "WARN":
                logger.warn(logMessage);
                break;
            case "ERROR":
                logger.error(logMessage);
                break;
            default:
                logger.debug(logMessage);
        }

        return SUCCESS;
    }

}
