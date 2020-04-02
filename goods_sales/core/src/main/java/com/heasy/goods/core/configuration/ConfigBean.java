package com.heasy.goods.core.configuration;

/**
 * Created by Administrator on 2018/11/10.
 */
public class ConfigBean {
    public static final String DEFAULT_CHARSET_UTF8 = "UTF-8";
    public static final String CONFIG_FILE_NAME = "config.xml";

    private String sdcardRootPath = "/sdcard/pplay/";

    private String dbName = "pplay.db";
    private String dbPassword = "";
    private String dbVersion = "1";

    private String actionBasePackage = "com.heasy.app.action";
    private String serviceBasePackage = "com.heasy.app";

    private String webviewLoadBasePath = "file:///android_asset/html/";
    private String webviewMainPage = "main.html";

    public String getDBFilePath(){
        return sdcardRootPath + dbName;
    }

    public String getSdcardRootPath() {
        return sdcardRootPath;
    }

    public void setSdcardRootPath(String sdcardRootPath) {
        this.sdcardRootPath = sdcardRootPath;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getDbVersion() {
        return dbVersion;
    }

    public void setDbVersion(String dbVersion) {
        this.dbVersion = dbVersion;
    }

    public String getActionBasePackage() {
        return actionBasePackage;
    }

    public void setActionBasePackage(String actionBasePackage) {
        this.actionBasePackage = actionBasePackage;
    }

    public String getServiceBasePackage() {
        return serviceBasePackage;
    }

    public void setServiceBasePackage(String serviceBasePackage) {
        this.serviceBasePackage = serviceBasePackage;
    }

    public String getWebviewLoadBasePath() {
        return webviewLoadBasePath;
    }

    public void setWebviewLoadBasePath(String webviewLoadBasePath) {
        this.webviewLoadBasePath = webviewLoadBasePath;
    }

    public String getWebviewMainPage() {
        return webviewMainPage;
    }

    public void setWebviewMainPage(String webviewMainPage) {
        this.webviewMainPage = webviewMainPage;
    }

}
