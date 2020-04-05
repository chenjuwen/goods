package com.heasy.map.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by Administrator on 2020/4/5.
 */
public class PreferenceUtil {
    private static SharedPreferences sharedPreferences = null;

    private static void init(Context context){
        if(sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
    }

    public static boolean getBooleanValue(Context context, String key, boolean defValue){
        init(context);
        return sharedPreferences.getBoolean(key, defValue);
    }

    public static float getFloatValue(Context context, String key){
        init(context);
        return sharedPreferences.getFloat(key, 0);
    }

    public static int getIntValue(Context context, String key){
        init(context);
        return sharedPreferences.getInt(key, 0);
    }

    public static long getLongValue(Context context, String key){
        init(context);
        return sharedPreferences.getLong(key, 0);
    }

    public static String getStringValue(Context context, String key){
        init(context);
        return sharedPreferences.getString(key, "");
    }

    public static String getStringSetValue(Context context, String key){
        init(context);
        Set<String> set = sharedPreferences.getStringSet(key, null);
        StringBuffer sb = new StringBuffer();
        if(set != null){
            int i = 0;
            for(Iterator<String> it=set.iterator(); it.hasNext();){
                if(i > 0){
                    sb.append(",");
                }
                sb.append(it.next());
                ++i;
            }
        }
        return sb.toString();
    }
}
