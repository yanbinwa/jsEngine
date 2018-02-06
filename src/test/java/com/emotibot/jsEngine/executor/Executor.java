package com.emotibot.jsEngine.executor;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.junit.Test;

/**
 * 这里判断同一个ScriptEngine，在写入参数不同的情况下，是否会相互影响
 * 
 * @author emotibot
 *
 */
public class Executor
{
    private static ScriptEngineManager manager = new ScriptEngineManager();
    private static ScriptEngine engine = manager.getEngineByName("js");
    
    @Test
    public void test() throws InterruptedException
    {
        test1();
    }
    
    public void test1() throws InterruptedException
    {
        List<Thread> threadList = new ArrayList<Thread>();
        List<TestTask> taskList = new ArrayList<TestTask>();
        for (int i = 1; i < 3; i ++)
        {
            TestTask test = new TestTask(i, 1000 * i);
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
    
    class TestTask implements Runnable
    {
        private int index;
        private int minSec;
        
        public TestTask(int index, int minSec)
        {
            this.index = index;
            this.minSec = minSec;
        }
        
        @Override
        public void run()
        {
            engine.put("index", index);
            engine.put("minSec", minSec);
            try
            {
                System.out.println(engine.eval(new FileReader("/Users/emotibot/Documents/workspace/other/myJsEngine/test/test.js")));
            } 
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
