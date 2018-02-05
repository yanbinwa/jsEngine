package com.emotibot.jsEngine.jsExecute.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.emotibot.jsEngine.jsExecute.taskPool.JsTaskPool;
import com.emotibot.middleware.utils.StringUtils;

public class JsManager
{
    private Map<String, JsTaskPool> appidToJsTaskPoolMap;
    
    public JsManager()
    {
        appidToJsTaskPoolMap = new HashMap<String, JsTaskPool>();
    }
    
    public List<Object> eval(String appid, Map<String, Object> params)
    {
        if (StringUtils.isEmpty(appid))
        {
            return null;
        }
        JsTaskPool jsTaskPool = appidToJsTaskPoolMap.get(appid);
        if (jsTaskPool == null)
        {
            jsTaskPool = new JsTaskPool();
            appidToJsTaskPoolMap.put(appid, jsTaskPool);
        }
        return jsTaskPool.eval(appid, params);
    }
}
