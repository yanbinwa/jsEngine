package com.emotibot.jsEngine.utils;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TemplateUtilsTest
{

    @Test
    public void test()
    {
        test2();
    }
    
    @SuppressWarnings("unused")
    private void test1()
    {
        List<String> tempalteTags = new ArrayList<String>();
        tempalteTags.add(TemplateUtils.NAME_TEMPLATE_TAG);
        tempalteTags.add(TemplateUtils.ACTOR_TEMPLATE_TAG);
        tempalteTags.add(TemplateUtils.BEFORE_TEMPLATE_TAG);
        String tempalte = TemplateUtils.getConfig(TemplateUtils.WITH_NAME_TAG, tempalteTags);
        System.out.println(tempalte);
    }

    @SuppressWarnings("unused")
    private void test2()
    {
        List<String> tempalteTags = new ArrayList<String>();
        tempalteTags.add("喜剧");
        String tempalte = TemplateUtils.getConfig(TemplateUtils.TYPE_MODIFY_TAG, tempalteTags);
        System.out.println(tempalte);
    }
}
