package com.heasy.goods;

import com.heasy.goods.core.HeasyApplication;
import com.heasy.goods.core.HeasyContext;
import com.heasy.goods.core.configuration.AbstractComponentScanner;
import com.heasy.goods.core.webview.DefaultActionDispatcher;
import com.heasy.goods.core.webview.DefaultWebChromeClient;
import com.heasy.goods.core.webview.DefaultWebViewClient;
import com.heasy.goods.core.webview.JSInterfaceImpl;
import com.heasy.goods.core.webview.WebViewWrapper;
import com.heasy.goods.action.ActionScanner;

/**
 * Created by Administrator on 2018/1/23.
 */
class WebViewWrapperFactory {
    private static DefaultActionDispatcher actionDispatcher = null;
    private static WebViewWrapper webViewWrapper = null;

    /**
     * 初始化DefaultJSActionRouter
     * @param heasyContext
     * @param actionBasePackages 多个包路径用 分号 分隔
     */
    public static void initActionDispatcher(HeasyContext heasyContext, String actionBasePackages){
        AbstractComponentScanner actionScanner = new ActionScanner();
        actionScanner.setContext(heasyContext.getServiceEngine().getAndroidContext());
        actionScanner.setBasePackages(actionBasePackages);

        //DefaultActionDispatcher
        actionDispatcher = new DefaultActionDispatcher();
        actionDispatcher.setActionScanner(actionScanner);
        actionDispatcher.init();
    }

    /**
     * @param heasyContext
     */
    public static void build(HeasyContext heasyContext){
        //JSInterfaceImpl
        JSInterfaceImpl jsInterface = new JSInterfaceImpl();
        jsInterface.setActionDispatcher(actionDispatcher);
        jsInterface.setHeasyContext(heasyContext);

        //DefaultWebViewClient
        DefaultWebViewClient webViewClient = new DefaultWebViewClient();

        //保证select控件能弹出下拉框
        HeasyApplication app = (HeasyApplication)heasyContext.getServiceEngine().getAndroidContext();
        webViewWrapper = new WebViewWrapper.Builder(app.getMainActivity())
                .setWebViewClient(webViewClient)
                .setWebChromeClient(new DefaultWebChromeClient())
                .setJSInterface(jsInterface)
                .build();

        heasyContext.setJsInterface(jsInterface);
    }

    public static WebViewWrapper getWebViewWrapper() {
        return webViewWrapper;
    }

}
