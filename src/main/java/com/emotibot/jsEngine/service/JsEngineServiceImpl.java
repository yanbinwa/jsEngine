package com.emotibot.jsEngine.service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.log4j.Logger;

import com.emotibot.jsEngine.common.Constants;
import com.emotibot.jsEngine.element.InputElement;
import com.emotibot.middleware.conf.ConfigManager;
import com.emotibot.middleware.utils.JsonUtils;

/**
 * 需要有工具类先将数据加载，之后
 * 
 * @author emotibot
 *
 */
public class JsEngineServiceImpl implements JsEngineService
{
    private static Logger logger = Logger.getLogger(JsEngineServiceImpl.class);
    private ScriptEngineManager manager = new ScriptEngineManager();
    private String jsFilePath = ConfigManager.INSTANCE.getPropertyString(Constants.JS_FILE_KEY);
    
    @Override
    public String getReplay(String dataStr)
    {
        long startTime = System.currentTimeMillis();
        InputElement inputElement = (InputElement)JsonUtils.getObject(dataStr, InputElement.class);
        if (inputElement == null)
        {
            logger.error("input element is invalied");
            return null;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(Constants.INPUT_ELEMENT_NAME, inputElement);
        params.put(Constants.SERVICE_NAME, this);
        long endTime = System.currentTimeMillis();
        logger.info("cost time: [" + (endTime - startTime) + "]");
        return invokeJs(jsFilePath, params);
    }
    
    private String invokeJs(String jsFile, Map<String, Object> params)
    {
        ScriptEngine engine = manager.getEngineByName("js");
        if (params != null)
        {
            for(Map.Entry<String, Object> entry : params.entrySet())
            {
                engine.put(entry.getKey(), entry.getValue());
            }
        }
        try
        {
            String result = (String) engine.eval(new FileReader(jsFile));
            return result;
        } 
        catch (ScriptException | FileNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
    }   
    
    /**
     * 这里是调用knowledge，用以返回特定对象的修饰词
     * 
     * 对于片名，在提取修饰语时，需要参考category的值，如果修饰语与category的值相左，则放弃修饰语
     * 例如: 我想看大话西游电视版，修饰语中是经典电影大话西游，这时就会出现奇异
     * 
     * 目前有修饰语的tag为:name, actor, director, type
     * 
     * @param str
     * @return
     */
    public String getModifier(String semanticTag, Map<String, Object> semantic)
    {
        return "modifier";
    }
    
    /**
     * 
     * @param tag  这个是模板的tag
     * @param semantic
     * @return
     */
    public String getTemplateByTag(String templateTag, List<String> chooseTag)
    {
        return "template";
    }
    
    public void logError(String msg)
    {
        logger.error(msg);
    }
}
