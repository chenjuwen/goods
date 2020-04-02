package com.heasy.map.service;

import android.util.Log;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2020/3/31.
 */
public class ConfigService {
    private static final String CONFIG_FILE_DIR = "/sdcard";
    private static final String CONFIG_FILE_NAME = "overlays.xml";
    private static Map<String, ConfigBean> configMap = new HashMap<>();

    public static String getConfigFileFullPath(){
        return CONFIG_FILE_DIR + File.separator + CONFIG_FILE_NAME;
    }

    private static Document openDocument() throws DocumentException, FileNotFoundException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(new FileInputStream(new File(getConfigFileFullPath())));
        return document;
    }

    private static void writeXmlFile(Document document) throws IOException {
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");

        XMLWriter writer2 = new XMLWriter(new FileOutputStream(new File(getConfigFileFullPath())), format);
        writer2.write(document);
        writer2.close();
    }

    /**
     * 复制assets的配置文件到sdcard
     */
    public static void copyConfigFile(){
        try{
            File dir = new File(CONFIG_FILE_DIR);
            if(!dir.exists()){
                dir.mkdirs();
            }

            File file = new File(getConfigFileFullPath());
            if(!file.exists()){
                OutputFormat format = OutputFormat.createPrettyPrint();
                format.setEncoding("UTF-8");

                SAXReader reader = new SAXReader();
                Document document = reader.read(ConfigService.class.getResourceAsStream("/assets/" + CONFIG_FILE_NAME));

                XMLWriter writer2 = new XMLWriter(new FileOutputStream(file), format);
                writer2.write(document);
                writer2.close();

                Log.i("ConfigService", "config file copy to " + getConfigFileFullPath());
            }

        }catch(Exception ex){
            throw new RuntimeException("Failed to copy config file!");
        }
    }

    public static void loadConfig(){
        try {
            configMap.clear();

            Document document = openDocument();
            Element root = document.getRootElement();

            List<Node> nodeList = root.selectNodes("overlay");
            if(nodeList != null){
                for(Node node : nodeList){
                    String id = getAttributeValue(node, "id");
                    String latitude = getNodeText(node.selectSingleNode("latitude"));
                    String longitude = getNodeText(node.selectSingleNode("longitude"));
                    String address = getNodeText(node.selectSingleNode("address"));
                    String comments = getNodeText(node.selectSingleNode("comments"));

                    ConfigBean bean = new ConfigBean();
                    bean.setId(id);
                    bean.setLatitude(Double.parseDouble(latitude));
                    bean.setLongitude(Double.parseDouble(longitude));
                    bean.setAddress(address);
                    bean.setComments(comments);

                    configMap.put(id, bean);
                }
            }

            Log.i("ConfigService", "config file loaded from " + getConfigFileFullPath());
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static ConfigBean addConfig(ConfigBean configBean){
        try{
            configBean.setId(String.valueOf(System.currentTimeMillis()));

            Document document = openDocument();
            Element root = document.getRootElement();

            Element e = root.addElement("overlay");
            e.addAttribute("id", configBean.getId());
            Element e1 = e.addElement("latitude");
            e1.addText(String.valueOf(configBean.getLatitude()));
            Element e2 = e.addElement("longitude");
            e2.addText(String.valueOf(configBean.getLongitude()));
            Element e3 = e.addElement("address");
            e3.addText(configBean.getAddress());
            Element e4 = e.addElement("comments");
            e4.addCDATA(configBean.getComments());

            writeXmlFile(document);
            configMap.put(configBean.getId(), configBean);

            Log.i("ConfigService", "config add ok");
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return configBean;
    }

    public static void updateConfig(ConfigBean configBean){
        try{
            Document document = openDocument();

            List<Element> list = document.selectNodes("/overlays/overlay[@id='" + configBean.getId() + "']");
            if(list != null && list.size() > 0){
                Element e = list.get(0);
                e.element("latitude").setText(String.valueOf(configBean.getLatitude()));
                e.element("longitude").setText(String.valueOf(configBean.getLongitude()));
                e.element("address").setText(configBean.getAddress());

                //更新CDATA数据时，要先清空再重新添加
                e.element("comments").clearContent();
                e.element("comments").addCDATA(configBean.getComments());
            }

            writeXmlFile(document);
            configMap.put(configBean.getId(), configBean);

            Log.i("ConfigService", "config update ok");

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static void deleteConfig(String id) {
        try{
            Document document = openDocument();
            List<Element> list = document.selectNodes("/overlays/overlay[@id='" + id + "']");
            if(list != null && list.size() > 0){
                Element e = list.get(0);
                e.getParent().remove(e);
            }

            writeXmlFile(document);
            configMap.remove(id);

            Log.i("ConfigService", "config delete ok");

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * 获取节点属性值
     */
    private static String getAttributeValue(Node node, String attrName){
        if(node == null){
            return "";
        }

        String value = node.valueOf("@"+attrName);
        if(value == null){
            return "";
        }else{
            return value.trim();
        }
    }

    private static void setAttributeValue(Element element, String attrName, String attrValue){
        if(element.attribute(attrName) != null){
            element.attribute(attrName).setValue(attrValue);
        }else{
            element.addAttribute(attrName, attrValue);
        }
    }

    /**
     * 获取节点文本内容
     */
    private static  String getNodeText(Node node){
        if(node == null){
            return "";
        }else{
            String value = node.getText();
            if(value == null){
                return "";
            }else{
                return value.trim();
            }
        }
    }

    public static Map<String, ConfigBean> getConfigMap() {
        return configMap;
    }

}

