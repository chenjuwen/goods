package com.heasy.goods.core.webview;

import com.heasy.goods.core.HeasyContext;

/**
 * 图片裁剪后的回调接口类
 */
public interface PictureCropCallback {
    void execute(HeasyContext heasyContext, String jsonData, String imagePath);
}
