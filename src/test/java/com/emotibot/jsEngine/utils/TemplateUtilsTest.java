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
        tempalteTags.add("<$Name$>");
        tempalteTags.add("<$Actor$>");
        tempalteTags.add("<$Before$>");
        String tempalte = TemplateUtils.getConfig(TemplateUtils.VIDEO_QUERY_TEMPLATE_TAG, tempalteTags);
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
