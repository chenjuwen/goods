package com.heasy.goods.action.goods;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.heasy.goods.core.HeasyContext;
import com.heasy.goods.core.annotation.JSActionAnnotation;
import com.heasy.goods.core.datastorage.SQLCipherManager;
import com.heasy.goods.core.utils.FastjsonUtil;
import com.heasy.goods.core.utils.FileUtil;
import com.heasy.goods.core.utils.ParameterUtil;
import com.heasy.goods.core.utils.SQLiteUtil;
import com.heasy.goods.core.utils.StringUtil;
import com.heasy.goods.core.webview.Action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JSActionAnnotation(name = "ProductAction")
public class ProductAction implements Action {
    private static final Logger logger = LoggerFactory.getLogger(ProductAction.class);

    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);
        final SQLCipherManager cipherManager = heasyContext.getServiceEngine().getDataService().getSQLCipherManager();

        if("list".equalsIgnoreCase(extend)){
            StringBuilder sb = new StringBuilder();
            sb.append("select id,name,price,group_price,status from product_info");

            JSONArray array = new JSONArray();
            List<Map<String, String>> dataList = cipherManager.rawQuery(sb.toString(), null);
            if(dataList != null) {
                for (int i = 0; i < dataList.size(); i++) {
                    Map<String, String> map = dataList.get(i);
                    JSONObject obj = new JSONObject();
                    obj.put("id", map.get("id"));
                    obj.put("name", map.get("name"));
                    obj.put("price", map.get("price"));
                    obj.put("group_price", map.get("group_price"));
                    obj.put("status", "1".equals(map.get("status")) ? "有效" : "无效");
                    array.add(obj);
                }
            }
            return array.toString();

        }else if("save".equalsIgnoreCase(extend)){
            String name = FastjsonUtil.getString(jsonObject, "name");
            String price = FastjsonUtil.getString(jsonObject, "price");
            String group_price = FastjsonUtil.getString(jsonObject, "group_price");
            String comments = FastjsonUtil.getString(jsonObject, "comments");

            Map dataMap = SQLiteUtil.toMap("name", name, "price", price, "group_price", group_price, "status", "1", "comments", comments);
            cipherManager.insert("product_info", dataMap);

        }else if("updateStatus".equalsIgnoreCase(extend)){
            String id = FastjsonUtil.getString(jsonObject, "id");
            String status = FastjsonUtil.getString(jsonObject, "status");

            Map dataMap = SQLiteUtil.toMap("status", status);
            cipherManager.update("product_info", dataMap, "id=?", new String[]{String.valueOf(id)});

        }else if("detail".equalsIgnoreCase(extend)){
            String id = FastjsonUtil.getString(jsonObject, "id");
            String url = FastjsonUtil.getString(jsonObject, "url");

            String sql = "select id,name,price,group_price,status,comments from product_info where id=" + id;

            Map<String, String> params = new HashMap<>();
            List<Map<String, String>> dataList = cipherManager.rawQuery(sql, null);
            if(dataList != null && dataList.size() > 0) {
                Map<String, String> dataMap = dataList.get(0);
                params.put("id", dataMap.get("id"));
                params.put("name", dataMap.get("name"));
                params.put("price", dataMap.get("price"));
                params.put("group_price", dataMap.get("group_price"));
                params.put("comments", StringUtil.trimToEmpty(dataMap.get("comments")).replaceAll("\n", "\\\\n"));
            }

            return heasyContext.getJsInterface().pageTransfer(url, ParameterUtil.toParamString(params, false));

        }else if("update".equalsIgnoreCase(extend)){
            String id = FastjsonUtil.getString(jsonObject, "id");
            String name = FastjsonUtil.getString(jsonObject, "name");
            String price = FastjsonUtil.getString(jsonObject, "price");
            String group_price = FastjsonUtil.getString(jsonObject, "group_price");
            String comments = FastjsonUtil.getString(jsonObject, "comments");

            Map dataMap = SQLiteUtil.toMap("name", name, "price", price, "group_price", group_price, "comments", comments);
            cipherManager.update("product_info", dataMap, "id=?", new String[]{String.valueOf(id)});
        }

        return SUCCESS;
    }

}
