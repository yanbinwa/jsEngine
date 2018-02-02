package com.emotibot.jsEngine.utils;

import org.junit.Test;

import com.emotibot.jsEngine.common.Constants;
import com.emotibot.middleware.conf.ConfigManager;

public class TemplateUtilsTest
{

    @Test
    public void test()
    {
        test3();
    }
    
    @SuppressWarnings("unused")
    private void test3()
    {
        TemplateUtils.loadConfigsFromFile(ConfigManager.INSTANCE.getPropertyString(Constants.TEMPLATE_FILE_KEY));
    }
}
