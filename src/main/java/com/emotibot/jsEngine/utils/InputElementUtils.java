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
     * 将semantic中的key转换成小写
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
            newSemantic.put(entry.getKey().toLowerCase(), entry.getValue());
        }
        element.setSemantic(newSemantic);
    }
}
