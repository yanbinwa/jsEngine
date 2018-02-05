package com.emotibot.jsEngine.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.emotibot.middleware.utils.StringUtils;

public class JsUtils
{
    private static ReentrantLock lock = new ReentrantLock();
    
    private static Map<String, Map<String, String>> jsNameToContextMap = new HashMap<String, Map<String, String>>();
    private static Map<String, Map<String, String>> commonJsNameToContextMap = new HashMap<String, Map<String, String>>();
    
    public static void loadJs(String appid, Map<String, String> jsDataMap)
    {
        if (jsDataMap == null)
        {
            return;
        }
        lock.lock();
        try
        {
            Map<String, String> jsNameToContextMapTmp = new HashMap<String, String>();
            for (Map.Entry<String, String> entry : jsDataMap.entrySet())
            {
                String jsName = entry.getKey();
                String jsContext = entry.getValue();
                if (StringUtils.isEmpty(jsName) || StringUtils.isEmpty(jsContext))
                {
                    continue;
                }
                jsNameToContextMapTmp.put(jsName, jsContext);
            }
            jsNameToContextMap.put(appid, jsNameToContextMapTmp);
        }
        finally
        {
            lock.unlock();
        }
        
    }
    
    public static void loadCommonJs(String appid, Map<String, String> jsDataMap)
    {
        if (jsDataMap == null)
        {
            return;
        }
        lock.lock();
        try
        {
            Map<String, String> commonJsNameToContextMapTmp = new HashMap<String, String>();
            for (Map.Entry<String, String> entry : jsDataMap.entrySet())
            {
                String jsName = entry.getKey();
                String jsContext = entry.getValue();
                if (StringUtils.isEmpty(jsName) || StringUtils.isEmpty(jsContext))
                {
                    continue;
                }
                commonJsNameToContextMapTmp.put(jsName, jsContext);
            }
            commonJsNameToContextMap.put(appid, commonJsNameToContextMapTmp);
        }
        finally
        {
            lock.unlock();
        }
        
    }
    
    
    public static List<String> getJsList(String appid)
    {
        Map<String, String> jsNameToContextMapTmp = jsNameToContextMap.get(appid);
        if (jsNameToContextMapTmp == null)
        {
            return null;
        }
        Map<String, String> commonJsNameToContextMapTmp = commonJsNameToContextMap.get(appid);
        String commonJsContext = "";
        if (commonJsNameToContextMapTmp != null)
        {
            for(String jsContext : commonJsNameToContextMapTmp.values())
            {
                commonJsContext += jsContext;
            }
        }
        List<String> jsList = new ArrayList<String>();
        for(String jsContext : jsNameToContextMapTmp.values())
        {
            jsList.add(commonJsContext + jsContext);
        }
        return jsList;
    }
}
