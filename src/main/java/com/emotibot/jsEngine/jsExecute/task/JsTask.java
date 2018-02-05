package com.emotibot.jsEngine.jsExecute.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.emotibot.jsEngine.common.Constants;
import com.emotibot.jsEngine.utils.JsUtils;

/**
 * 内部包含一个
 * 
 * @author emotibot
 *
 */
public class JsTask
{
    private ScriptEngine engine;
    
    public JsTask()
    {
        
    }
    
    public JsTask(ScriptEngine engine)
    {
        this.engine = engine;
        try
        {
            engine.eval("1 + 1");
        } 
        catch (ScriptException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * 1. 获取执行的Js list
     * 2. 按顺序执行
     * 
     * @param appid
     * @param params
     * @return
     */
    public List<Object> eval(String appid, Map<String, Object> params)
    {
        if (params == null)
        {
            return null;
        }
        List<String> jsList = JsUtils.getJsList(appid);
        if (jsList == null || jsList.isEmpty())
        {
            return null;
        }
        params.put(Constants.APPID, appid);
        for(Map.Entry<String, Object> entry : params.entrySet())
        {
            engine.put(entry.getKey(), entry.getValue());
        }
        List<Object> retList = new ArrayList<Object>();
        for (String js : jsList)
        {
            try
            {
                Object ret = engine.eval(js);
                if (ret != null)
                {
                    retList.add(ret);
                }
            } 
            catch (ScriptException e)
            {
                e.printStackTrace();
                continue;
            }
        }
        return retList;
    }
}
