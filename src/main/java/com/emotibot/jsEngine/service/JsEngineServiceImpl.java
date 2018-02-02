package com.emotibot.jsEngine.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.log4j.Logger;

import com.emotibot.jsEngine.common.Constants;
import com.emotibot.jsEngine.element.InputElement;
import com.emotibot.jsEngine.exception.ConfigNotFoundException;
import com.emotibot.jsEngine.execute.JSTaskManager;
import com.emotibot.jsEngine.helper.JsHelper;
import com.emotibot.jsEngine.step.FetchModifyStep;
import com.emotibot.jsEngine.utils.InputElementUtils;
import com.emotibot.jsEngine.utils.TemplateUtils;
import com.emotibot.middleware.conf.ConfigManager;
import com.emotibot.middleware.context.Context;
import com.emotibot.middleware.utils.FileUtils;
import com.emotibot.middleware.utils.JsonUtils;
import com.emotibot.middleware.utils.StringUtils;

/**
 * 需要有工具类先将数据加载，可以并行起多个jsEngine，从而实现并发调用
 * 
 * @author emotibot
 *
 */
public class JsEngineServiceImpl implements JsEngineService
{
    private static Logger logger = Logger.getLogger(JsEngineServiceImpl.class);
    private ScriptEngineManager manager;
    private String jsFilePath;
    private JsHelper helper;
    private FetchModifyStep fetchModifyStep;
    
    public JsEngineServiceImpl() throws ConfigNotFoundException
    {
        init();
    }
    
    @Override
    public String getReplay(String dataStr, String userid)
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
        params.put(Constants.HELPER_NAME, helper);
        params.put(Constants.USER_ID, userid);
        long endTime = System.currentTimeMillis();
        logger.info("cost time: [" + (endTime - startTime) + "]");
        return invokeJs(jsFilePath, params);
    }
    
    private void init() throws ConfigNotFoundException
    {
        JSTaskManager jsTaskManager = new JSTaskManager();
        jsTaskManager.start();
        
        jsFilePath = ConfigManager.INSTANCE.getPropertyString(Constants.JS_FILE_KEY);
        helper = new JsHelper();
        manager = new ScriptEngineManager();
        
        ExecutorService executorService = Executors.newFixedThreadPool(40);
        fetchModifyStep = new FetchModifyStep(executorService);
    }
    
    /**
     * 这里可以起多个jsEngine，进行并发调用
     * 
     * @param jsFile
     * @param params
     * @return
     */
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
            String commonJsFile = ConfigManager.INSTANCE.getPropertyString(Constants.COMMON_JS_FILE_KEY);
            String fileStr = FileUtils.readFileToString(commonJsFile);
            fileStr += FileUtils.readFileToString(jsFile);
            String result = (String) engine.eval(fileStr);
            return result;
        } 
        catch (ScriptException e)
        {
            e.printStackTrace();
            return null;
        }
    }   
    
    /**
     * 所有的修饰词均来自Knowledge，不需要从Template中获取，这里需要多线程进行query
     * 
     * @param templateElementTagList
     * @param semantic
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> getModifier(List<String> templateElementTagList, Map<String, Object> semantic)
    {
        if (templateElementTagList == null || templateElementTagList.isEmpty())
        {
            return null;
        }
        Map<String, String> modifyTagToValueMap = new HashMap<String, String>();
        for (String modifyTag : templateElementTagList)
        {
            if (semantic.containsKey(modifyTag))
            {
                modifyTagToValueMap.put(modifyTag, (String)semantic.get(modifyTag));
            }
        }
        if (modifyTagToValueMap.isEmpty())
        {
            return null;
        }
        Context context = new Context();
        context.setValue(Constants.MODIFY_TAG_TO_VALUE_MAP_KEY, modifyTagToValueMap);
        fetchModifyStep.execute(context);
        Map<String, String> modifyTagToModifyMap = 
                (Map<String, String>) context.getValue(Constants.MODIFY_TAG_TO_MODIFY_MAP_KEY);
        return modifyTagToModifyMap;
    }
    
    /**
     * 1. 获取template
     * 2. 提取template中的templateTag，获取该Tag的template，进行替换
     * 3. 获取template中的templateElementTag，进行替换
     * 
     * @param templateTag
     * @param semantic
     * @return
     */
    public String getText(Map<String, Object> semantic, String templateTag, List<String> templateElementOrCommonElementTags)
    {
        //获取template
        String template = getTemplate(templateTag, templateElementOrCommonElementTags);
        if (StringUtils.isEmpty(template))
        {
            return null;
        }
        
        //提取template中的templateTag，获取该Tag的template，进行替换
        template = replaceTemplateTagInTemplate(template);
        if (StringUtils.isEmpty(template))
        {
            return null;
        }
        
        //获取template中的templateElementTag，进行替换
        template = replaceTemplateElementTagInTemplate(template, semantic);
        return template;
    }
    
    public String getText(Map<String, Object> semantic, String templateTag, String commonTag)
    {
        List<String> commonElementTags = new ArrayList<String>();
        commonElementTags.add(commonTag);
        return getText(semantic, templateTag, commonElementTags);
    }
    
    public String getText(Map<String, Object> semantic, String templateTag)
    {
        return getText(semantic, templateTag, (List<String>)null);
    }
    
    
    public String getTemplate(String templateTag)
    {
        return getTemplate(templateTag, (String) null);
    }
    
    public String getTemplate(String templateTag, String commonElementTag)
    {
        List<String> commonElementTags = new ArrayList<String>();
        commonElementTags.add(commonElementTag);
        return getTemplate(templateTag, commonElementTags);
    }
    
    public String getTemplate(String templateTag, List<String> templateOrCommonElementTags)
    {
        return TemplateUtils.getTemplate(templateTag, templateOrCommonElementTags);
    }
    
    public String replaceTemplateTagInTemplate(String template)
    {
        List<String> innerTemplateTags = TemplateUtils.getTemplateTagsFromInput(template);
        if (innerTemplateTags != null)
        {
            for (String innerTemplateTag : innerTemplateTags)
            {
                String innerTemplate = getTemplate(innerTemplateTag);
                if (StringUtils.isEmpty(template))
                {
                    logger.error("should contain innerTemplateTag: " + innerTemplateTag + "; tempalte is: " + template);
                    return null;
                }
                template = template.replace(innerTemplateTag, innerTemplate);
            }
        }
        return template;
    }
    
    public String replaceTemplateElementTagInTemplate(String template, Map<String, Object> semantic)
    {
        List<String> templateElementTags = TemplateUtils.getTemplateElementTagsFromInput(template);
        if (templateElementTags != null)
        {
            for (String templateElementTag : templateElementTags)
            {
                if (!semantic.containsKey(templateElementTag))
                {
                    logger.error("should contain templateElementTag: " + templateElementTag + "; tempalte is: " + template + "; semantic is: " + semantic);
                    return null;
                }
                String value = (String) semantic.get(templateElementTag);
                String synonym = TemplateUtils.getSynonym(templateElementTag, value);
                if (!StringUtils.isEmpty(synonym))
                {
                    value = synonym;
                }
                template = template.replace(TemplateUtils.buildTemplateElementTagWithBeginAndAfter(templateElementTag), value);
            }
        }
        return template;
    }
    
    public List<String> getTemplateElementTagsFromInput(String template, Map<String, Object> semantic)
    {
        List<String> templateElementTags = TemplateUtils.getTemplateElementTagsFromInput(template);
        if (templateElementTags == null)
        {
            return null;
        }
        List<String> chooseTemplateElementTags = new ArrayList<String>();
        for (String templateElementTag : templateElementTags)
        {
            if (semantic.containsKey(templateElementTag))
            {
                chooseTemplateElementTags.add(templateElementTag);
            }
        }
        return chooseTemplateElementTags;
    }
    
    public Object getPersonas(String userid)
    {
        return null;
    }
}
