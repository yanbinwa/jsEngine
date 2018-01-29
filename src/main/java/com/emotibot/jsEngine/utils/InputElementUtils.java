package com.emotibot.jsEngine.utils;

import java.util.Map;

import com.emotibot.jsEngine.element.InputElement;

/**
 * 
 * 
 * @author emotibot
 *
 */
public class InputElementUtils
{
    public static final String SEMANTIC_LIKELY_NAME_TAG = "likely_name";
    /**
     * 将likely name转换为name
     * @param element
     */
    public static void adjustInputElement(InputElement element)
    {
        Map<String, Object> semantic = element.getSemantic();
        if (semantic == null)
        {
            return;
        }
        if (!semantic.containsKey(SEMANTIC_LIKELY_NAME_TAG))
        {
            return;
        }
        String likely = (String) semantic.get(SEMANTIC_LIKELY_NAME_TAG);
        semantic.put(TemplateUtils.SEMANTIC_NAME_TAG, likely);
    }
}
