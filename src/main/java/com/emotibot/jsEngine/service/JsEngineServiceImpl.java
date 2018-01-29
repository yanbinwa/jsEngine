package com.emotibot.jsEngine.service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.log4j.Logger;

import com.emotibot.jsEngine.common.Constants;
import com.emotibot.jsEngine.element.InputElement;
import com.emotibot.jsEngine.utils.InputElementUtils;
import com.emotibot.jsEngine.utils.TemplateUtils;
import com.emotibot.middleware.conf.ConfigManager;
import com.emotibot.middleware.utils.JsonUtils;
import com.emotibot.middleware.utils.StringUtils;

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
        InputElementUtils.adjustInputElement(inputElement);
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
     * 这里需要并行处理，获取到
     * 
     * @param str
     * @return
     */
    public Map<String, String> getModifier(List<String> semanticTags, Map<String, Object> semantic)
    {
        Map<String, String> ret = new HashMap<String, String>();
        for(String semanticTag : semanticTags)
        {
            if (!semantic.containsKey(semanticTag))
            {
                continue;
            }
            Object value = semantic.get(semanticTag);
            if (!(value instanceof String))
            {
                continue;
            }
            String valueStr = (String) value;
            String tag = TemplateUtils.tryGetModifyTag(semanticTag);
            if (!StringUtils.isEmpty(tag))
            {
                List<String> chooseTag = new ArrayList<String>();
                chooseTag.add(valueStr);
                String modify = TemplateUtils.getConfig(tag, chooseTag);
                if (StringUtils.isEmpty(modify))
                {
                    continue;
                }
                ret.put(semanticTag, modify);
            }
            else
            {
                //TODO: 需要通过knowledge来调用
                continue;
            }
        }
        return ret;
    }
    
    /**
     * 
     * @param tag  这个是模板的tag
     * @param semantic
     * @return
     */
    public String getTemplateByTag(String templateTag, List<String> chooseTag)
    {
        return TemplateUtils.getConfig(templateTag, chooseTag);
    }
    
    public void logInfo(String msg)
    {
        logger.error(msg);
    }
    
    public void logError(String msg)
    {
        logger.error(msg);
    }
    
    public Map<String, String> createEmptyMap()
    {
        return new HashMap<String, String>();
    }
    
    public List<String> createEmptyList()
    {
        return new ArrayList<String>();
    }
}
