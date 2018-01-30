package com.emotibot.jsEngine.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.gson.JsonObject;

public class JsEngineServiceImplTest
{    
    @Test
    public void test()
    {
        List<JsonObject> testCases = new ArrayList<JsonObject>();
        testCases.add(getVideoQueryObj1());
        testCases.add(getVideoQueryObj2());
        testCases.add(getVideoQueryObj3());
        testCases.add(getVideoQueryObj4());
        testCases.add(getVideoQueryObj5());
        testCases.add(getVideoQueryObj6());
        testCases.add(getVideoQueryObj7());
        testCases.add(getUSBObj1());
        testCases.add(getUSBObj2());
        testCases.add(getSelectObj1());
        testCases.add(getSelectObj2());
        testCases.add(getTVSetObj1());
        testCases.add(getTVSetObj2());
        testCases.add(getTVSetObj3());
        testCases.add(getTVSetObj4());
        JsEngineServiceImpl service = new JsEngineServiceImpl();
        for (JsonObject testCase : testCases)
        {
            String ret = service.getReplay(testCase.toString());
            System.out.println(ret);
        }
    }
    
    @SuppressWarnings("unused")
    private JsonObject getVideoQueryObj1()
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("domain", "VIDEO");
        jsonObject.addProperty("intent", "QUERY");
        JsonObject semanticObj = new JsonObject();
        semanticObj.addProperty("name", "大话西游");
        semanticObj.addProperty("category", "电影");
        semanticObj.addProperty("actor", "周星驰");
        semanticObj.addProperty("director", "周星驰");
        semanticObj.addProperty("type", "喜剧");
        jsonObject.add("semantic", semanticObj);
        return jsonObject;
    }
    
    @SuppressWarnings("unused")
    private JsonObject getVideoQueryObj2()
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("domain", "VIDEO");
        jsonObject.addProperty("intent", "QUERY");
        JsonObject semanticObj = new JsonObject();
        semanticObj.addProperty("name", "大话西游");
        semanticObj.addProperty("tag", "美剧");
        semanticObj.addProperty("actor", "周星驰");
        semanticObj.addProperty("director", "王晶");
        jsonObject.add("semantic", semanticObj);
        return jsonObject;
    }
    
    @SuppressWarnings("unused")
    private JsonObject getVideoQueryObj3()
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("domain", "VIDEO");
        jsonObject.addProperty("intent", "QUERY");
        JsonObject semanticObj = new JsonObject();
        semanticObj.addProperty("name", "大话西游");
        semanticObj.addProperty("season", "1");
        semanticObj.addProperty("director", "王晶");
        jsonObject.add("semantic", semanticObj);
        return jsonObject;
    }
    
    @SuppressWarnings("unused")
    private JsonObject getVideoQueryObj4()
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("domain", "VIDEO");
        jsonObject.addProperty("intent", "QUERY");
        JsonObject semanticObj = new JsonObject();
        semanticObj.addProperty("actor", "周星驰");
        semanticObj.addProperty("type", "喜剧");
        jsonObject.add("semantic", semanticObj);
        return jsonObject;
    }
    
    @SuppressWarnings("unused")
    private JsonObject getVideoQueryObj5()
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("domain", "VIDEO");
        jsonObject.addProperty("intent", "QUERY");
        JsonObject semanticObj = new JsonObject();
        semanticObj.addProperty("type", "喜剧");
        semanticObj.addProperty("area", "美国");
        semanticObj.addProperty("category", "电影");
        semanticObj.addProperty("tag", "美剧");
        jsonObject.add("semantic", semanticObj);
        return jsonObject;
    }
    
    @SuppressWarnings("unused")
    private JsonObject getVideoQueryObj6()
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("domain", "VIDEO");
        jsonObject.addProperty("intent", "QUERY");
        JsonObject semanticObj = new JsonObject();
        semanticObj.addProperty("year", "1990");
        semanticObj.addProperty("area", "美国");
        semanticObj.addProperty("category", "电影");
        semanticObj.addProperty("actor", "成龙");
        jsonObject.add("semantic", semanticObj);
        return jsonObject;
    }
    
    @SuppressWarnings("unused")
    private JsonObject getVideoQueryObj7()
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("domain", "VIDEO");
        jsonObject.addProperty("intent", "QUERY");
        JsonObject semanticObj = new JsonObject();
        semanticObj.addProperty("director", "王晶");
        semanticObj.addProperty("actor", "周星驰");
        semanticObj.addProperty("rate", "9.0");
        semanticObj.addProperty("award", "奥斯卡");
        semanticObj.addProperty("role", "至尊宝");
        semanticObj.addProperty("year", "1990");
        semanticObj.addProperty("type", "喜剧");
        semanticObj.addProperty("area", "中国");
        semanticObj.addProperty("category", "电影");
        semanticObj.addProperty("tag", "国产剧");
        jsonObject.add("semantic", semanticObj);
        return jsonObject;
    }
    
    @SuppressWarnings("unused")
    private JsonObject getUSBObj1()
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
    
    @SuppressWarnings("unused")
    private JsonObject getUSBObj2()
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("domain", "VIDEO");
        jsonObject.addProperty("intent", "PLAY");
        JsonObject semanticObj = new JsonObject();
        semanticObj.addProperty("value", "-1");
        semanticObj.addProperty("source", "MEDIA");
        jsonObject.add("semantic", semanticObj);
        return jsonObject;
    }
    
    @SuppressWarnings("unused")
    private JsonObject getSelectObj1()
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("domain", "CONTROL");
        jsonObject.addProperty("intent", "SELECT");
        JsonObject semanticObj = new JsonObject();
        semanticObj.addProperty("index", "-1");
        semanticObj.addProperty("row", "-1");
        jsonObject.add("semantic", semanticObj);
        return jsonObject;
    }
    
    @SuppressWarnings("unused")
    private JsonObject getSelectObj2()
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("domain", "CONTROL");
        jsonObject.addProperty("intent", "SELECT");
        JsonObject semanticObj = new JsonObject();
        semanticObj.addProperty("index", "1");
        jsonObject.add("semantic", semanticObj);
        return jsonObject;
    }
    
    @SuppressWarnings("unused")
    private JsonObject getTVSetObj1()
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("domain", "TV");
        jsonObject.addProperty("intent", "SET");
        JsonObject semanticObj = new JsonObject();
        semanticObj.addProperty("operands", "OBJ_VOLUMN");
        semanticObj.addProperty("value", "90");
        jsonObject.add("semantic", semanticObj);
        return jsonObject;
    }
    
    @SuppressWarnings("unused")
    private JsonObject getTVSetObj2()
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("domain", "TV");
        jsonObject.addProperty("intent", "SET");
        JsonObject semanticObj = new JsonObject();
        semanticObj.addProperty("operands", "OBJ_VOLUMN");
        semanticObj.addProperty("value", "3");
        jsonObject.add("semantic", semanticObj);
        return jsonObject;
    }
    
    @SuppressWarnings("unused")
    private JsonObject getTVSetObj3()
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("domain", "TV");
        jsonObject.addProperty("intent", "SET");
        JsonObject semanticObj = new JsonObject();
        semanticObj.addProperty("operands", "OBJ_VOLUMN");
        semanticObj.addProperty("value", "3");
        semanticObj.addProperty("direct", "+");
        jsonObject.add("semantic", semanticObj);
        return jsonObject;
    }
    
    @SuppressWarnings("unused")
    private JsonObject getTVSetObj4()
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("domain", "TV");
        jsonObject.addProperty("intent", "SET");
        JsonObject semanticObj = new JsonObject();
        semanticObj.addProperty("operands", "OBJ_BRIGHTNESS");
        semanticObj.addProperty("value", "3");
        jsonObject.add("semantic", semanticObj);
        return jsonObject;
    }
}
