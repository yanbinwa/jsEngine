package com.emotibot.jsEngine.jsExecute.taskPool;

import java.util.List;
import java.util.Map;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.emotibot.jsEngine.jsExecute.task.JsTask;
import com.emotibot.jsEngine.jsExecute.task.JsTaskFactory;

public class JsTaskPool
{
    GenericObjectPool<JsTask> objectPool;
    
    public JsTaskPool()
    {
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxIdle(8);
        poolConfig.setMaxTotal(10);
        poolConfig.setMinIdle(2);
        objectPool = new GenericObjectPool<JsTask>(new JsTaskFactory(), poolConfig);
    }
    
    public List<Object> eval(String appid, Map<String, Object> params)
    {
        JsTask jsTask = null;
        try
        {
            jsTask = objectPool.borrowObject();
            List<Object> ret = jsTask.eval(appid, params);
            return ret;
        } 
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        finally
        {
            if (jsTask != null)
            {
                objectPool.returnObject(jsTask);
            }
        }
    }
}
