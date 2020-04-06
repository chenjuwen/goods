package com.heasy.goods.action.goods;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.heasy.goods.core.HeasyContext;
import com.heasy.goods.core.annotation.JSActionAnnotation;
import com.heasy.goods.core.datastorage.PageInfo;
import com.heasy.goods.core.datastorage.SQLCipherManager;
import com.heasy.goods.core.utils.DatetimeUtil;
import com.heasy.goods.core.utils.FastjsonUtil;
import com.heasy.goods.core.utils.ParameterUtil;
import com.heasy.goods.core.utils.SQLiteUtil;
import com.heasy.goods.core.utils.StringUtil;
import com.heasy.goods.core.webview.Action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JSActionAnnotation(name = "SalesAction")
public class SalesAction implements Action {
    private static final Logger logger = LoggerFactory.getLogger(SalesAction.class);

    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        try {
            JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);
            final SQLCipherManager cipherManager = heasyContext.getServiceEngine().getDataService().getSQLCipherManager();

            if ("listInfo".equalsIgnoreCase(extend)) {
                String queryContent = FastjsonUtil.getString(jsonObject, "queryContent");

                String[] columns = {"id","user_name","address","phone","sales_type", "comments","create_time","total_price"};

                String selection = null;
                if(StringUtil.isNotEmpty(queryContent)){
                    selection = "user_name like '%" + queryContent + "%' or address like '%" + queryContent + "%'";
                    selection += " or phone like '%" + queryContent + "%'";
                }

                PageInfo<Map<String, String>> pageInfo = cipherManager
                        .queryForPage("sales_info", columns, selection, null, "create_time desc", 1, 100);
                if(pageInfo.getList() != null) {
                    String result = FastjsonUtil.object2String(pageInfo.getList());
                    return result;
                }else{
                    return FastjsonUtil.object2String(JSONArray.parse("[]"));
                }
            } else if ("saveInfo".equalsIgnoreCase(extend)) {
                String userName = FastjsonUtil.getString(jsonObject, "userName");
                String address = FastjsonUtil.getString(jsonObject, "address");
                String phone = FastjsonUtil.getString(jsonObject, "phone");
                String sales_type = FastjsonUtil.getString(jsonObject, "sales_type");
                String comments = FastjsonUtil.getString(jsonObject, "comments");

                //get next id value
                Integer id = new Integer(1000);
                List<Map<String, String>> dataList = cipherManager.rawQuery("select max(id) as id from sales_info", null);
                if(dataList != null && dataList.size() > 0){
                    String tmpValue = dataList.get(0).get("id");
                    if(tmpValue != null){
                        id = new Integer(Integer.parseInt(tmpValue) + 1);
                    }
                }
                System.out.print("id=" + id);

                Map dataMap = SQLiteUtil.toMap("id", id, "user_name", userName, "address", address,
                        "phone", phone, "sales_type", sales_type, "comments", comments,
                        "create_time", DatetimeUtil.getToday(DatetimeUtil.DEFAULT_PATTERN_DT2));
                cipherManager.insert("sales_info", dataMap);

            } else if ("deleteInfo".equalsIgnoreCase(extend)) {
                String id = FastjsonUtil.getString(jsonObject, "id");
                cipherManager.delete("sales_detail", "sales_id=?", new String[]{String.valueOf(id)});
                cipherManager.delete("sales_info", "id=?", new String[]{String.valueOf(id)});

            } else if ("getInfo".equalsIgnoreCase(extend)) {
                String id = FastjsonUtil.getString(jsonObject, "id");
                String url = FastjsonUtil.getString(jsonObject, "url");

                String sql = "select id,user_name,address,phone,sales_type,comments,create_time,total_price from sales_info where id=" + id;

                Map<String, String> params = new HashMap<>();
                List<Map<String, String>> dataList = cipherManager.rawQuery(sql, null);
                if (dataList != null && dataList.size() > 0) {
                    Map<String, String> map = dataList.get(0);
                    params.put("id", map.get("id"));
                    params.put("userName", map.get("user_name"));
                    params.put("address", map.get("address"));
                    params.put("phone", map.get("phone"));
                    params.put("sales_type", map.get("sales_type"));
                    params.put("comments", StringUtil.trimToEmpty(map.get("comments")).replaceAll("\n", "\\\\n"));
                    params.put("createTime", map.get("create_time"));
                    params.put("totalPrice", map.get("total_price"));
                }

                return heasyContext.getJsInterface().pageTransfer(url, ParameterUtil.toParamString(params, false));

            } else if ("update".equalsIgnoreCase(extend)) {
                String id = FastjsonUtil.getString(jsonObject, "id");
                String userName = FastjsonUtil.getString(jsonObject, "userName");
                String address = FastjsonUtil.getString(jsonObject, "address");
                String phone = FastjsonUtil.getString(jsonObject, "phone");
                String sales_type = FastjsonUtil.getString(jsonObject, "sales_type");
                String comments = FastjsonUtil.getString(jsonObject, "comments");

                Map dataMap = SQLiteUtil.toMap("user_name", userName, "address", address, "phone", phone,
                        "sales_type", sales_type, "comments", comments,
                        "create_time", DatetimeUtil.getToday(DatetimeUtil.DEFAULT_PATTERN_DT2));

                cipherManager.update("sales_info", dataMap, "id=?", new String[]{String.valueOf(id)});

            }else if("calculateTotalPrice".equalsIgnoreCase(extend)){
                String id = FastjsonUtil.getString(jsonObject, "id");

                //calculate total price
                Double total_price = 0.0;
                String sql = "select sum(product_price * product_weight) as total_price from sales_detail where sales_id=?";
                List<Map<String, String>> dataList = cipherManager.rawQuery(sql, new String[]{String.valueOf(id)});
                if(dataList != null && dataList.size() > 0){
                    String tmpValue = dataList.get(0).get("total_price");
                    if(tmpValue != null){
                        total_price = new Double(Double.parseDouble(tmpValue));
                    }
                }
                System.out.print("total_price=" + total_price);

                cipherManager.executeSQL("update sales_info set total_price=" + total_price + " where id=" + id);

            }else if("listDetail".equalsIgnoreCase(extend)){
                String id = FastjsonUtil.getString(jsonObject, "id");

                StringBuilder sb = new StringBuilder();
                sb.append("select id,product_name,product_price,product_weight,(product_price*product_weight) as total_price from sales_detail where sales_id=" + id + " order by create_time desc");

                JSONArray array = new JSONArray();
                List<Map<String, String>> dataList = cipherManager.rawQuery(sb.toString(), null);
                if (dataList != null) {
                    for (int i = 0; i < dataList.size(); i++) {
                        Map<String, String> map = dataList.get(i);
                        JSONObject obj = new JSONObject();
                        obj.put("id", map.get("id"));
                        obj.put("sales_id", id);
                        obj.put("name", map.get("product_name"));
                        obj.put("price", map.get("product_price"));
                        obj.put("weight", map.get("product_weight"));
                        obj.put("total_price", map.get("total_price"));
                        array.add(obj);
                    }
                }
                return array.toString();

            }else if("deleteDetail".equalsIgnoreCase(extend)){
                String id = FastjsonUtil.getString(jsonObject, "id");
                String salesId = FastjsonUtil.getString(jsonObject, "salesId");

                cipherManager.executeSQL("delete from sales_detail where id=" + id);

            }else if("saveSalesProduct".equalsIgnoreCase(extend)){
                String salesId = FastjsonUtil.getString(jsonObject, "salesId");
                String name = FastjsonUtil.getString(jsonObject, "name");
                String price = FastjsonUtil.getString(jsonObject, "price");
                String weight = FastjsonUtil.getString(jsonObject, "weight");

                Map dataMap = SQLiteUtil.toMap("sales_id", salesId, "product_name", name, "product_price", price,
                        "product_weight", weight, "create_time", DatetimeUtil.getToday(DatetimeUtil.DEFAULT_PATTERN_DT2));

                cipherManager.insert("sales_detail", dataMap);
            }else if("salesStat".equalsIgnoreCase(extend)){
                String selectedMonth = FastjsonUtil.getString(jsonObject, "selectedMonth");

                StringBuilder sb = new StringBuilder();
                sb.append(" select strftime('%Y-%m', create_time) as ym,product_name, ");
                sb.append(" sum(product_weight) as total_weight,sum(product_price*product_weight) as total_price ");
                sb.append(" from sales_detail ");

                if(StringUtil.isNotEmpty(selectedMonth)){
                    sb.append(" where ym='" + selectedMonth + "' ");
                }
                sb.append(" group by ym,product_name ");
                sb.append(" order by ym,product_name ");

                JSONArray array = new JSONArray();
                List<Map<String, String>> dataList = cipherManager.rawQuery(sb.toString(), null);
                if (dataList != null) {
                    for (int i = 0; i < dataList.size(); i++) {
                        Map<String, String> map = dataList.get(i);
                        JSONObject obj = new JSONObject();
                        obj.put("ym", map.get("ym"));
                        obj.put("name", map.get("product_name"));
                        obj.put("weight", map.get("total_weight"));
                        obj.put("price", map.get("total_price"));
                        array.add(obj);
                    }
                }
                return array.toString();
            }else if("getOptionsData".equalsIgnoreCase(extend)){
                StringBuilder sb = new StringBuilder(" select distinct strftime('%Y-%m', create_time) as ym from sales_detail order by ym desc ");

                JSONArray array = new JSONArray();
                List<Map<String, String>> dataList = cipherManager.rawQuery(sb.toString(), null);
                if (dataList != null && dataList.size() > 0) {
                    for (int i = 0; i < dataList.size(); i++) {
                        Map<String, String> map = dataList.get(i);
                        JSONObject obj = new JSONObject();
                        obj.put("ym", map.get("ym"));
                        array.add(obj);
                    }
                }else{
                    JSONObject obj = new JSONObject();
                    obj.put("ym", DatetimeUtil.getToday("yyyy-MM"));
                    array.add(obj);
                }
                return array.toString();
            }

            return SUCCESS;
        }catch (Exception ex){
            logger.error("SalesAction", ex);
            return ex.getMessage();
        }
    }

}
