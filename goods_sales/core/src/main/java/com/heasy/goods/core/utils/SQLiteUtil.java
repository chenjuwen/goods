package com.heasy.goods.core.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/10/14.
 */
public class SQLiteUtil {
    public static Map<String, Object> toMap(String key, Object value){
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    public static Map<String, Object> toMap(String key1, Object value1, String key2, Object value2){
        Map<String, Object> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

    public static Map<String, Object> toMap(String key1, Object value1, String key2, Object value2, String key3, Object value3){
        Map<String, Object> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        return map;
    }

    public static Map<String, Object> toMap(String key1, Object value1, String key2, Object value2, String key3, Object value3,
                                            String key4, Object value4){
        Map<String, Object> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        return map;
    }

    public static Map<String, Object> toMap(String key1, Object value1, String key2, Object value2, String key3, Object value3,
                                            String key4, Object value4, String key5, Object value5){
        Map<String, Object> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        map.put(key5, value5);
        return map;
    }

    public static Map<String, Object> toMap(String key1, Object value1, String key2, Object value2, String key3, Object value3,
                                            String key4, Object value4, String key5, Object value5, String key6, Object value6){
        Map<String, Object> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        map.put(key5, value5);
        map.put(key6, value6);
        return map;
    }

    public static Map<String, Object> toMap(String key1, Object value1, String key2, Object value2, String key3, Object value3,
                                            String key4, Object value4, String key5, Object value5, String key6, Object value6,
                                            String key7, Object value7){
        Map<String, Object> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        map.put(key5, value5);
        map.put(key6, value6);
        map.put(key7, value7);
        return map;
    }

}
