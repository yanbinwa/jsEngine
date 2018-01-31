package com.emotibot.jsEngine.utils;

import java.util.HashMap;
import java.util.Map;

import com.emotibot.jsEngine.element.InputElement;

/**
 * 
 * @author emotibot
 *
 */
public class InputElementUtils
{
    /**
     * 将semantic中的key转换为templateElementTag
     * 
     * @param element
     */
    public static void adjustInputElement(InputElement element)
    {
        Map<String, Object> semantic = element.getSemantic();
        if (semantic == null)
        {
            return;
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
