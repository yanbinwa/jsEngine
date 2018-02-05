package com.emotibot.jsEngine.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.gson.JsonObject;

public class JsEngineServiceImplTest
{    
    JsEngineServiceImpl service = new JsEngineServiceImpl();
    
    private int ThreadNum = 10;
    
    @Test
    public void test() throws InterruptedException
    {
        long startTime = System.currentTimeMillis();
        text2();
        long endTime = System.currentTimeMillis();
        System.out.println("Total time is: " + (endTime - startTime));
    }
    
    @SuppressWarnings("unused")
    private void text1() throws InterruptedException
    {
        List<JsonObject> testCases = new ArrayList<JsonObject>();
        testCases.add(getVideoQueryObj1());
        testCases.add(getVideoQueryObj2());
        testCases.add(getVideoQueryObj3());
        testCases.add(getVideoQueryObj4());
        testCases.add(getVideoQueryObj5());
        testCases.add(getVideoQueryObj6());
        testCases.add(getVideoQueryObj7());
        testCases.add(getVideoQueryObj8());
        testCases.add(getUSBObj1());
        testCases.add(getUSBObj2());
        testCases.add(getSelectObj1());
        testCases.add(getSelectObj2());
        testCases.add(getTVSetObj1());
        testCases.add(getTVSetObj2());
        testCases.add(getTVSetObj3());
        testCases.add(getTVSetObj4());
        Thread.sleep(2000);
        for (JsonObject testCase : testCases)
        {
            String ret = service.getReplay(testCase.toString(), "111", "5a200ce8e6ec3a6506030e54ac3b970e");
            System.out.println(ret);
        }
    }
    
    @SuppressWarnings("unused")
    private void text2() throws InterruptedException
    {
        List<JsonObject> testCases = new ArrayList<JsonObject>();
        testCases.add(getVideoQueryObj1());
        testCases.add(getVideoQueryObj2());
        testCases.add(getVideoQueryObj3());
        testCases.add(getVideoQueryObj4());
        testCases.add(getVideoQueryObj5());
        testCases.add(getVideoQueryObj6());
        testCases.add(getVideoQueryObj7());
        testCases.add(getVideoQueryObj8());
        testCases.add(getUSBObj1());
        testCases.add(getUSBObj2());
        testCases.add(getSelectObj1());
        testCases.add(getSelectObj2());
        testCases.add(getTVSetObj1());
        testCases.add(getTVSetObj2());
        testCases.add(getTVSetObj3());
        testCases.add(getTVSetObj4());
        try
        {
            Thread.sleep(2000);
        } 
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        List<Thread> threadList = new ArrayList<Thread>();
        List<TestTask> taskList = new ArrayList<TestTask>();
        for (int i = 0; i < ThreadNum; i ++)
        {
            TestTask test = new TestTask(testCases);
            Thread thread = new Thread(test);
            thread.start();
            threadList.add(thread);
            taskList.add(test);
        }
        for (Thread thread : threadList)
        {
            thread.join();
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
        //semanticObj.addProperty("type", "喜剧");
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
        semanticObj.addProperty("name", "忠犬八公");
        jsonObject.add("semantic", semanticObj);
        return jsonObject;
    }
    
    @SuppressWarnings("unused")
    private JsonObject getVideoQueryObj8()
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
        jsonObject.addProperty("intent", "PLAY");
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
        semanticObj.addProperty("source", "EPG");
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
    
    class TestTask implements Runnable
    {
        private List<JsonObject> testObjectList = null;
        private int cycleNum = 100;
        
        public TestTask(List<JsonObject> testObjectList)
        {
            this.testObjectList = testObjectList;
        }
        
        @Override
        public void run()
        {
            int num = 0;
            while(num < cycleNum)
            {
                for (JsonObject testCase : testObjectList)
                {
                    String ret = service.getReplay(testCase.toString(), "111", "5a200ce8e6ec3a6506030e54ac3b970e");
                    System.out.println(ret);
                }
                num ++;
            }
        }
    }
}
