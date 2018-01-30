package com.emotibot.jsEngine.utils;

import java.util.HashMap;
import java.util.Map;

import com.emotibot.middleware.utils.StringUtils;

/**
 * 将第几集，第几季与片名合并起来，首先要调用modify方法获取修饰语，之后再调用本方法，将片名进行修改
 * 
 * @author emotibot
 *
 */
public class SemanticUtils
{
    public static String TEMPLATE_REPLACE_TAG = "XXX";
    
    //片名后缀
    public static final String SEMANTIC_NAME_TAG = "name";
    public static final String SEMANTIC_EPISODE_TAG = "episode";
    public static final String SEMANTIC_SEASON_TAG = "season";
    public static final String SEMANTIC_PART_TAG = "part";
    public static final String SEMANTIC_TERM_TAG = "term";
        
    public static String PROFIX_EPISODE_TEMPALTE = "第XXX集";
    public static String PROFIX_PART_TEMPALTE = "第XXX部";
    public static String PROFIX_SEASON_TEMPALTE = "第XXX季";
    public static String PROFIX_TERM_TEMPALTE = "第XXX期";
    
    private static Map<String, String> profixTagToTempalteMap;
    
    //U盘，点播，TVSET
    public static final String LIST_NUM_TEMPLATE_ELEMENT_TAG = "<$ListNum$>".toLowerCase();
    public static final String BACKWARD_STRING = "倒数";
    
    //U盘第几部
    public static final String SEMANTIC_USB_VALUE_TAG = "value";
    public static final String USB_INDEX_STRING = "第XXX部";
    
    //点播
    public static final String SEMANTIC_INDEX_VALUE_TAG = "index";
    public static final String SEMANTIC_ROW_VALUE_TAG = "row";
    public static final String PLAY_INDEX_STRING = "第XXX个";
    public static final String PLAY_ROW_STRING = "第XXX行";
    
    //TVSET
    public static final String SEMANTIC_TVSET_VALUE_TAG = "value";
    
    static
    {
        profixTagToTempalteMap = new HashMap<String, String>();
        profixTagToTempalteMap.put(TemplateUtils.convertSemanticTagToTemplateTag(SEMANTIC_EPISODE_TAG), PROFIX_EPISODE_TEMPALTE);
        profixTagToTempalteMap.put(TemplateUtils.convertSemanticTagToTemplateTag(SEMANTIC_SEASON_TAG), PROFIX_PART_TEMPALTE);
        profixTagToTempalteMap.put(TemplateUtils.convertSemanticTagToTemplateTag(SEMANTIC_PART_TAG), PROFIX_SEASON_TEMPALTE);
        profixTagToTempalteMap.put(TemplateUtils.convertSemanticTagToTemplateTag(SEMANTIC_TERM_TAG), PROFIX_TERM_TEMPALTE);
    }
    
    public static void translateSemanticValue(String templateTag, Map<String, Object> semantic)
    {
        switch(templateTag)
        {
        case TemplateUtils.VIDEO_QUERY_TEMPLATE_TAG:
            translateSemanticValueForName(semantic);
            break;
        case TemplateUtils.SELECT_TEMPLATE_TAG:
            translateSemanticValueForSelect(semantic);
            break;
        case TemplateUtils.U_DIST_TEMPLATE_TAG:
            translateSemanticValueForUSB(semantic);
            break;
        case TemplateUtils.TV_SET_TEMPLATE_TAG:
            translateSemanticValueForTVSet(semantic);
            break;
        }
    }
    
    private static void translateSemanticValueForName(Map<String, Object> semantic)
    {
        String nameTemplateElementTag = TemplateUtils.convertSemanticTagToTemplateTag(SEMANTIC_NAME_TAG);
        if (!semantic.containsKey(nameTemplateElementTag))
        {
            return;
        }
        String name = (String) semantic.get(nameTemplateElementTag);
        for (String nameProfixTag : profixTagToTempalteMap.keySet())
        {
            if (semantic.containsKey(nameProfixTag))
            {
                String numStr = (String) semantic.get(nameProfixTag);
                String profix = null;
                try
                {
                    Integer.parseInt(numStr);
                    profix = profixTagToTempalteMap.get(nameProfixTag).replaceAll(TEMPLATE_REPLACE_TAG, numStr);
                }
                catch(Exception e)
                {
                    profix = numStr;
                }
                String newName = name + profix;
                semantic.put(nameTemplateElementTag, newName);
            }
        }
    }
    
    private static void translateSemanticValueForSelect(Map<String, Object> semantic)
    {
        String rowTemplateElementTag = TemplateUtils.convertSemanticTagToTemplateTag(SEMANTIC_ROW_VALUE_TAG);
        String semanticValue = "";
        if (semantic.get(rowTemplateElementTag) != null)
        {
            int row = Integer.parseInt((String) semantic.get(rowTemplateElementTag));
            if (row < 0)
            {
                semanticValue += BACKWARD_STRING;
                row = Math.abs(row);
            }
            semanticValue += PLAY_ROW_STRING.replace(TEMPLATE_REPLACE_TAG, NumUtils.numberToChinese(row));
        }
        String indexTemplateElementTag = TemplateUtils.convertSemanticTagToTemplateTag(SEMANTIC_INDEX_VALUE_TAG);
        if (semantic.get(indexTemplateElementTag) != null)
        {
            int index = Integer.parseInt((String) semantic.get(indexTemplateElementTag));
            if (index < 0)
            {
                semanticValue += BACKWARD_STRING;
                index = Math.abs(index);
            }
            semanticValue += PLAY_INDEX_STRING.replace(TEMPLATE_REPLACE_TAG, NumUtils.numberToChinese(index));
        }
        if(StringUtils.isEmpty(semanticValue))
        {
            return;
        }
        semantic.put(LIST_NUM_TEMPLATE_ELEMENT_TAG, semanticValue);
    }
    
    private static void translateSemanticValueForUSB(Map<String, Object> semantic)
    {
        String valueTemplateElementTag = TemplateUtils.convertSemanticTagToTemplateTag(SEMANTIC_USB_VALUE_TAG);
        String semanticValue = "";
        if (semantic.get(valueTemplateElementTag) != null)
        {
            int value = Integer.parseInt((String) semantic.get(valueTemplateElementTag));
            if (value < 0)
            {
                semanticValue += BACKWARD_STRING;
                value = Math.abs(value);
            }
            semanticValue += USB_INDEX_STRING.replace(TEMPLATE_REPLACE_TAG, NumUtils.numberToChinese(value));
        }
        if(StringUtils.isEmpty(semanticValue))
        {
            return;
        }
        semantic.put(LIST_NUM_TEMPLATE_ELEMENT_TAG, semanticValue);
    }

    private static void translateSemanticValueForTVSet(Map<String, Object> semantic)
    {
        String valueTemplateElementTag = TemplateUtils.convertSemanticTagToTemplateTag(SEMANTIC_TVSET_VALUE_TAG);
        if (semantic.get(valueTemplateElementTag) != null)
        {
            semantic.put(LIST_NUM_TEMPLATE_ELEMENT_TAG, semantic.get(valueTemplateElementTag));
        }
    }
}
