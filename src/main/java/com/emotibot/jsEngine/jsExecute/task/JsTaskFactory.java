package com.emotibot.jsEngine.jsExecute.task;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class JsTaskFactory extends BasePooledObjectFactory<JsTask>
{
    private ScriptEngineManager manager = new ScriptEngineManager();
    
    @Override
    public JsTask create() throws Exception
    {
        ScriptEngine engine = manager.getEngineByName("js");
        JsTask jsTask = new JsTask(engine);
        return jsTask;
    }

    @Override
    public PooledObject<JsTask> wrap(JsTask obj)
    {
        return new DefaultPooledObject<JsTask>(obj);
    }
}
