package com.emotibot.jsEngine.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.emotibot.jsEngine.utils.NumUtils;
import com.emotibot.middleware.utils.StringUtils;

public class JsHelper
{
    private static Logger logger = Logger.getLogger(JsHelper.class);
    
    public static String TEMPLATE_REPLACE_TAG = "XXX";
    
    //VIDEO_QUERY
    public static final String SEMANTIC_NAME_TAG = "name".toLowerCase();    
    public static final String SEMANTIC_EPISODE_TAG = "episode".toLowerCase();
    public static final String SEMANTIC_SEASON_TAG = "season".toLowerCase();
    public static final String SEMANTIC_PART_TAG = "part".toLowerCase();
    public static final String SEMANTIC_TERM_TAG = "term".toLowerCase();
        
    public static String PROFIX_EPISODE_TEMPALTE = "第XXX集";
    public static String PROFIX_PART_TEMPALTE = "第XXX部";
    public static String PROFIX_SEASON_TEMPALTE = "第XXX季";
    public static String PROFIX_TERM_TEMPALTE = "第XXX期";
    
    private static Map<String, String> profixTagToTempalteMap;
    
    //U盘，点播
    public static final String BACKWARD_STRING = "倒数";
    
    //U盘第几部
    public static final String SEMANTIC_USB_VALUE_TAG = "value".toLowerCase();
    public static final String USB_INDEX_STRING = "第XXX部";
    
    //点播
    public static final String SEMANTIC_INDEX_VALUE_TAG = "index".toLowerCase();
    public static final String SEMANTIC_ROW_VALUE_TAG = "row".toLowerCase();
    public static final String PLAY_INDEX_STRING = "第XXX个";
    public static final String PLAY_ROW_STRING = "第XXX行";
    
    static
    {
        profixTagToTempalteMap = new HashMap<String, String>();
        profixTagToTempalteMap.put(SEMANTIC_EPISODE_TAG, PROFIX_EPISODE_TEMPALTE);
        profixTagToTempalteMap.put(SEMANTIC_SEASON_TAG, PROFIX_PART_TEMPALTE);
        profixTagToTempalteMap.put(SEMANTIC_PART_TAG, PROFIX_SEASON_TEMPALTE);
        profixTagToTempalteMap.put(SEMANTIC_TERM_TAG, PROFIX_TERM_TEMPALTE);
    }
    
    public void addNameProfix(Map<String, Object> semantic)
    {
        if (semantic.containsKey(SEMANTIC_NAME_TAG))
        {
            String name = (String) semantic.get(SEMANTIC_NAME_TAG);
            for (String nameProfixTag : profixTagToTempalteMap.keySet())
            {
                if (semantic.containsKey(nameProfixTag))
                {
                    String numStr = (String) semantic.get(nameProfixTag);
                    String profix = "";
                    try
                    {
                        int num = Integer.parseInt(numStr);
                        if (num < 0)
                        {
                            profix += BACKWARD_STRING;
                        }
                        num = Math.abs(num);
                        profix += profixTagToTempalteMap.get(nameProfixTag).replaceAll(TEMPLATE_REPLACE_TAG, NumUtils.numberToChinese(num));
                    }
                    catch(Exception e)
                    {
                        profix = numStr;
                    }
                    String newName = name + profix;
                    semantic.put(SEMANTIC_NAME_TAG, newName);
                }
            }
        }
    }
    
    public void addListNumber(Map<String, Object> semantic, String listNumberTag)
    {
        //如果包含value，解析成中文表达后增加新字段ListNum
        if (semantic.containsKey(SEMANTIC_USB_VALUE_TAG))
        {
            String listNumStr = "";
            int value = Integer.parseInt((String) semantic.get(SEMANTIC_USB_VALUE_TAG));
            if (value < 0)
            {
                listNumStr += BACKWARD_STRING;
                value = Math.abs(value);
            }
            listNumStr += USB_INDEX_STRING.replace(TEMPLATE_REPLACE_TAG, NumUtils.numberToChinese(value));
            semantic.put(listNumberTag.toLowerCase(), listNumStr);
        }
        //如果包含row或者index，解析成中文表达后增加新字段ListNum
        if (semantic.containsKey(SEMANTIC_ROW_VALUE_TAG) || semantic.containsKey(SEMANTIC_INDEX_VALUE_TAG))
        {
            String listNumStr = "";
            if (semantic.get(SEMANTIC_ROW_VALUE_TAG) != null)
            {
                int row = Integer.parseInt((String) semantic.get(SEMANTIC_ROW_VALUE_TAG));
                if (row < 0)
                {
                    listNumStr += BACKWARD_STRING;
                    row = Math.abs(row);
                }
                listNumStr += PLAY_ROW_STRING.replace(TEMPLATE_REPLACE_TAG, NumUtils.numberToChinese(row));
            }
            if (semantic.get(SEMANTIC_INDEX_VALUE_TAG) != null)
            {
                int index = Integer.parseInt((String) semantic.get(SEMANTIC_INDEX_VALUE_TAG));
                if (index < 0)
                {
                    listNumStr += BACKWARD_STRING;
                    index = Math.abs(index);
                }
                listNumStr += PLAY_INDEX_STRING.replace(TEMPLATE_REPLACE_TAG, NumUtils.numberToChinese(index));
            }
            if(!StringUtils.isEmpty(listNumStr))
            {
                semantic.put(listNumberTag.toLowerCase(), listNumStr);
            }
        }
    }
    
    public void logInfo(String msg)
    {
        logger.info(msg);
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
    
    public List<String> getTemplateElementTagsFromSemantic(Map<String, Object> semantic)
    {
        if (semantic == null)
        {
            return null;
        }
        return new ArrayList<String>(semantic.keySet());
    }
}
