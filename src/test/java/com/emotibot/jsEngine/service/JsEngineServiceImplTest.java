package com.emotibot.jsEngine.service;

import org.junit.Test;

import com.google.gson.JsonObject;

public class JsEngineServiceImplTest
{    
    @Test
    public void test()
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("domain", "VIDEO");
        jsonObject.addProperty("intent", "QUERY");
        JsonObject semanticObj = new JsonObject();
        semanticObj.addProperty("name", "大话西游");
        jsonObject.add("semantic", semanticObj);
        JsEngineServiceImpl service = new JsEngineServiceImpl();
        String ret = service.getReplay(jsonObject.toString());
        System.out.println(ret);
    }

}
