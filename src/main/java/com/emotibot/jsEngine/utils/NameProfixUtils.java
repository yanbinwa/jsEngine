package com.emotibot.jsEngine.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 将第几集，第几季与片名合并起来，首先要调用modify方法获取修饰语，之后再调用本方法，将片名进行修改
 * 
 * @author emotibot
 *
 */
public class NameProfixUtils
{
    public static String[] nameProfixTags = {TemplateUtils.SEMANTIC_EPISODE_TAG, TemplateUtils.SEMANTIC_PART_TAG,
            TemplateUtils.SEMANTIC_SEASON_TAG, TemplateUtils.SEMANTIC_TERM_TAG};
    
    public static String PROFIX_EPISODE_TEMPALTE = "第XXX集";
    public static String PROFIX_PART_TEMPALTE = "第XXX部";
    public static String PROFIX_SEASON_TEMPALTE = "第XXX季";
    public static String PROFIX_TERM_TEMPALTE = "第XXX期";
    
    public static String PROFIX_TEMPLATE_TAG = "XXX";
    
    private static Map<String, String> profixTagToTempalteMap;
    
    static
    {
        profixTagToTempalteMap = new HashMap<String, String>();
        profixTagToTempalteMap.put(TemplateUtils.SEMANTIC_EPISODE_TAG, PROFIX_EPISODE_TEMPALTE);
        profixTagToTempalteMap.put(TemplateUtils.SEMANTIC_PART_TAG, PROFIX_PART_TEMPALTE);
        profixTagToTempalteMap.put(TemplateUtils.SEMANTIC_SEASON_TAG, PROFIX_SEASON_TEMPALTE);
        profixTagToTempalteMap.put(TemplateUtils.SEMANTIC_TERM_TAG, PROFIX_TERM_TEMPALTE);
    }
    
    public static String getMergedName(Map<String, Object> semantic)
    {
        if (!semantic.containsKey(TemplateUtils.SEMANTIC_NAME_TAG))
        {
            return null;
        }
        String name = (String) semantic.get(TemplateUtils.SEMANTIC_NAME_TAG);
        for (String nameProfixTag : nameProfixTags)
        {
            if (semantic.containsKey(nameProfixTag))
            {
                String numStr = (String) semantic.get(nameProfixTag);
                String profix = null;
                try
                {
                    Integer.parseInt(numStr);
                    profix = profixTagToTempalteMap.get(nameProfixTag).replaceAll(PROFIX_TEMPLATE_TAG, numStr);
                }
                catch(Exception e)
                {
                    profix = numStr;
                }
                return name + profix;
            }
        }
        return name;
    }
}
