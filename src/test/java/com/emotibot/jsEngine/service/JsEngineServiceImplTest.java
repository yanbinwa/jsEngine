package com.emotibot.jsEngine.service;

import org.junit.Test;

import com.google.gson.JsonObject;

public class JsEngineServiceImplTest
{    
    @Test
    public void test()
    {
        JsonObject result = getVideoQueryWithUSBObj();
        JsEngineServiceImpl service = new JsEngineServiceImpl();
        String ret = service.getReplay(result.toString());
        ret = service.getReplay(result.toString());
        ret = service.getReplay(result.toString());
        ret = service.getReplay(result.toString());
        ret = service.getReplay(result.toString());
        ret = service.getReplay(result.toString());
        System.out.println(ret);
    }
    
    @SuppressWarnings("unused")
    private JsonObject getVideoQueryObj()
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("domain", "VIDEO");
        jsonObject.addProperty("intent", "QUERY");
        JsonObject semanticObj = new JsonObject();
        semanticObj.addProperty("name", "大话西游");
        jsonObject.add("semantic", semanticObj);
        return jsonObject;
    }
    
    @SuppressWarnings("unused")
    private JsonObject getVideoQueryWithUSBObj()
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("domain", "VIDEO");
        jsonObject.addProperty("intent", "QUERY");
        JsonObject semanticObj = new JsonObject();
        semanticObj.addProperty("name", "大话西游");
        semanticObj.addProperty("source", "MEDIA");
        jsonObject.add("semantic", semanticObj);
        return jsonObject;
    }
}
