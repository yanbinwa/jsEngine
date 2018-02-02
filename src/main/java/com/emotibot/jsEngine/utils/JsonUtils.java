package com.emotibot.jsEngine.utils;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

/**
 * 1.json string 转换为 map
 * 2.json string 转换为 对象
 * @author zhangtengda
 *
 */
public class JsonUtils {
    /**
     * json string 转换为 map 对象
     * @param jsonObj
     * @return
     */
    public static Map<String, String> jsonToMap(Object jsonObj) {
        JSONObject jsonObject = JSONObject.fromObject(jsonObj);
        Map<String, String> map = new HashMap<String,String>();
        for(Object key:jsonObject.keySet()){
        	if(map.get(key)!=null)
        		map.put((String)key,map.get(key).toString());
        }
        return map;
    }

    /**json string 转换为 对象
     * @param jsonObj
     * @param type
     * @return
     */
    public  static <T>  T jsonToBean(Object jsonObj, Class<T> type) {
        JSONObject jsonObject = JSONObject.fromObject(jsonObj);
        T obj =(T)JSONObject.toBean(jsonObject, type);
        return obj;
    } 

}