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
    
    //VIDEO_QUERY
    public static final String SEMANTIC_LIKELY_NAME_TAG = TemplateUtils.convertSemanticTagToTemplateTag("likely_name");
    public static final String SEMANTIC_NAME_TAG = TemplateUtils.convertSemanticTagToTemplateTag("name");
    public static final String SEMANTIC_ACTOR_TAG = TemplateUtils.convertSemanticTagToTemplateTag("actor");
    public static final String SEMANTIC_DIRECTOR_TAG = TemplateUtils.convertSemanticTagToTemplateTag("director");
    public static final String SEMANTIC_SOURCE_TAG = TemplateUtils.convertSemanticTagToTemplateTag("source");
    public static final String SEMANTIC_YEAR_TAG = TemplateUtils.convertSemanticTagToTemplateTag("year");
    public static final String SEMANTIC_RATE_TAG = TemplateUtils.convertSemanticTagToTemplateTag("rate");
    
    public static final String SEMANTIC_EPISODE_TAG = TemplateUtils.convertSemanticTagToTemplateTag("episode");
    public static final String SEMANTIC_SEASON_TAG = TemplateUtils.convertSemanticTagToTemplateTag("season");
    public static final String SEMANTIC_PART_TAG = TemplateUtils.convertSemanticTagToTemplateTag("part");
    public static final String SEMANTIC_TERM_TAG = TemplateUtils.convertSemanticTagToTemplateTag("term");
        
    public static String PROFIX_EPISODE_TEMPALTE = "第XXX集";
    public static String PROFIX_PART_TEMPALTE = "第XXX部";
    public static String PROFIX_SEASON_TEMPALTE = "第XXX季";
    public static String PROFIX_TERM_TEMPALTE = "第XXX期";
    
    private static Map<String, String> profixTagToTempalteMap;
    
    //U盘，点播，TVSET
    public static final String LIST_NUM_TEMPLATE_ELEMENT_TAG = TemplateUtils.convertSemanticTagToTemplateTag("ListNum");
    public static final String BACKWARD_STRING = "倒数";
    
    //U盘第几部
    public static final String SEMANTIC_USB_VALUE_TAG = TemplateUtils.convertSemanticTagToTemplateTag("value");
    public static final String USB_INDEX_STRING = "第XXX部";
    
    //点播
    public static final String SEMANTIC_INDEX_VALUE_TAG = TemplateUtils.convertSemanticTagToTemplateTag("index");
    public static final String SEMANTIC_ROW_VALUE_TAG = TemplateUtils.convertSemanticTagToTemplateTag("row");
    public static final String PLAY_INDEX_STRING = "第XXX个";
    public static final String PLAY_ROW_STRING = "第XXX行";
    
    //TVSET
    public static final String SEMANTIC_TVSET_VALUE_TAG = TemplateUtils.convertSemanticTagToTemplateTag("value");
    
    static
    {
        profixTagToTempalteMap = new HashMap<String, String>();
        profixTagToTempalteMap.put(SEMANTIC_EPISODE_TAG, PROFIX_EPISODE_TEMPALTE);
        profixTagToTempalteMap.put(SEMANTIC_SEASON_TAG, PROFIX_PART_TEMPALTE);
        profixTagToTempalteMap.put(SEMANTIC_PART_TAG, PROFIX_SEASON_TEMPALTE);
        profixTagToTempalteMap.put(SEMANTIC_TERM_TAG, PROFIX_TERM_TEMPALTE);
    }
    
    /**
     * 1. 如果有likelyName，将likelyName的值赋值给name
     * 2. 如果actor与director一样，去掉director
     * 3. 如果包含year，需要在后面加一个"年"字
     * 4. 如果包含rate，需要在后面加"评分"
     * 5. 如果包含episode，season，part, term，将其转化为片名的后缀
     * 
     * @param semantic
     */
    public static void translateSemanticValueForVideoQuery(Map<String, Object> semantic)
    {
        //如果有likelyName，将likelyName的值赋值给name
        if (semantic.containsKey(SEMANTIC_LIKELY_NAME_TAG) && !semantic.containsKey(SEMANTIC_NAME_TAG))
        {
            String likely = (String) semantic.get(SEMANTIC_LIKELY_NAME_TAG);
            semantic.put(SEMANTIC_NAME_TAG, likely);
        }
        
        //如果actor与director一样，去掉director
        String actor = (String) semantic.get(SEMANTIC_ACTOR_TAG);
        String director = (String) semantic.get(SEMANTIC_DIRECTOR_TAG);
        if (!StringUtils.isEmpty(actor) && !StringUtils.isEmpty(director) && actor.equals(director))
        {
            semantic.remove(SEMANTIC_DIRECTOR_TAG);
        }
        
        //如果包含year，需要在后面加一个"年"字
        if (semantic.containsKey(SEMANTIC_YEAR_TAG))
        {
            semantic.put(SEMANTIC_YEAR_TAG, semantic.get(SEMANTIC_YEAR_TAG) + "年");
        }
        
        //如果包含rate，需要在后面加"评分"
        if (semantic.containsKey(SEMANTIC_RATE_TAG))
        {
            semantic.put(SEMANTIC_RATE_TAG, semantic.get(SEMANTIC_RATE_TAG) + "评分");
        }
        
        //如果包含episode，season，part, term，且包含片名，将其转化为片名的后缀
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
    
    /**
     * 1. 如果包含source，将内容替换成U盘
     * 2. 如果包含value，解析成中文表达后增加新字段ListNum
     * 
     * @param semantic
     */
    public static void translateSemanticValueForUSB(Map<String, Object> semantic)
    {
        //如果包含source，将内容替换成U盘
        if (semantic.containsKey(SEMANTIC_SOURCE_TAG))
        {
            semantic.put(SEMANTIC_SOURCE_TAG, "U盘");
        }
        
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
            semantic.put(LIST_NUM_TEMPLATE_ELEMENT_TAG, listNumStr);
        }
    }
    
    /**
     * 1. 如果包含row或者index，解析成中文表达后增加新字段ListNum
     * 
     * @param semantic
     */
    public static void translateSemanticValueForSelectQuery(Map<String, Object> semantic)
    {
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
                semantic.put(LIST_NUM_TEMPLATE_ELEMENT_TAG, listNumStr);
            }
        }
    }
    
    /**
     * 1. 如果包含value，增加新字段ListNum，并将value赋值给ListNum
     * 
     * @param semantic
     */
    public static void translateSemanticValueForTVSet(Map<String, Object> semantic)
    {
        //如果包含value，增加新字段ListNum，并将value赋值给ListNum
        if (semantic.containsKey(SEMANTIC_TVSET_VALUE_TAG))
        {
            semantic.put(LIST_NUM_TEMPLATE_ELEMENT_TAG, semantic.get(SEMANTIC_TVSET_VALUE_TAG));
        }
    }
}
