package com.emotibot.jsEngine.utils;

import java.util.HashMap;
import java.util.Map;

import com.emotibot.jsEngine.element.InputElement;
import com.emotibot.middleware.utils.StringUtils;

/**
 * 
 * @author emotibot
 *
 */
public class InputElementUtils
{
    public static final String SEMANTIC_LIKELY_NAME_TAG = "likely_name";
    public static final String SEMANTIC_NAME_TAG = "name";
    public static final String SEMANTIC_ACTOR_TAG = "actor";
    public static final String SEMANTIC_DIRECTOR_TAG = "director";
    public static final String SEMANTIC_SOURCE_TAG = "source";
    public static final String SEMANTIC_YEAR_TAG = "year";
    public static final String SEMANTIC_RATE_TAG = "rate";
    /**
     * 1. 将likely name转换为name
     * 2. 如果actor与director一样，去掉director
     * 3. 如果包含source，将内容替换成U盘
     * 4. 如果包含year，需要在后面加一个"年"字
     * 5. 如果包含rate，需要在后面加"评分"
     * 3. 将semantic中的key转换为templateElementTag
     * 
     * @param element
     */
    public static void adjustInputElement(InputElement element)
    {
        //调整likelyname
        Map<String, Object> semantic = element.getSemantic();
        if (semantic == null)
        {
            return;
        }
        if (semantic.containsKey(SEMANTIC_LIKELY_NAME_TAG))
        {
            String likely = (String) semantic.get(SEMANTIC_LIKELY_NAME_TAG);
            semantic.put(SEMANTIC_NAME_TAG, likely);
        }
        
        //调整actor与director一致
        String actor = (String) semantic.get(SEMANTIC_ACTOR_TAG);
        String director = (String) semantic.get(SEMANTIC_DIRECTOR_TAG);
        if (!StringUtils.isEmpty(actor) && !StringUtils.isEmpty(director) && actor.equals(director))
        {
            semantic.remove(SEMANTIC_DIRECTOR_TAG);
        }
        
        //如果包含source，将内容替换成U盘
        if (semantic.containsKey(SEMANTIC_SOURCE_TAG))
        {
            semantic.put(SEMANTIC_SOURCE_TAG, "U盘");
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
        
        //将semantic中的key转换为templateElementTag
        Map<String, Object> newSemantic = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : semantic.entrySet())
        {
            newSemantic.put(TemplateUtils.convertSemanticTagToTemplateTag(entry.getKey()), entry.getValue());
        }
        element.setSemantic(newSemantic);
    }
}
