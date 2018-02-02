package com.emotibot.jsEngine.execute;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.emotibot.jsEngine.consul.JsConsulServiceImpl;
import com.emotibot.jsEngine.consul.JsConsulServiceImpl.JSTemplateListener;
import com.emotibot.jsEngine.consul.bean.JsTemplateData;
import com.emotibot.jsEngine.exception.ConfigNotFoundException;
import com.emotibot.jsEngine.utils.ReadWriteMap;
/**
 * 1、通知线程次执行多个TASK任务
 * 2、维护更新task队列
 * 
 * @author liguorui
 */
public class JSTaskManager implements  JSTemplateListener{
	private static Logger  LOGGER = Logger.getLogger(JSTaskManager.class);
	private JsConsulServiceImpl consulService;
	private JSExecutorService   executorService;
	//带锁map
	private volatile ReadWriteMap<String,List<JSTask>> appidTask = new ReadWriteMap<String,List<JSTask>>();
	
	public JSTaskManager() throws ConfigNotFoundException {
		consulService   = new JsConsulServiceImpl();
		executorService = new JSExecutorService();
	}
	public void start(){
		consulService.setListener(this);
		consulService.start();
		
		executorService.init();
	}
	public void eval(String appid,Map<String,String> dataMap,JSTaskCallBack callback) throws InterruptedException, ExecutionException{
		//待执行的task
		List<JSTask> allTask = appidTask.get(appid);
		List<Future<String>> futureList = new ArrayList<Future<String>>();
		List<String> values = new ArrayList<String>();
		/**
		 * step 1:轮训并发执行task
		 */
		LOGGER.info("开始执行 all js task");
		for(JSTask task:allTask){
			futureList.add(executorService.execute(task,new JSTaskCallable(task,dataMap)));
		}
		/**
		 * step 2:遍历futureList,高速轮询（模拟实现了并发）获取future状态成功完成后获取结果，退出当前循环
		 */
		for (Future<String> future : futureList) {
			//每个future 并发轮循，判断完成状态然后获取结果
			while (true) {
				if (future.isDone() && !future.isCancelled()) {// 获取future成功完成状态，如果想要限制每个任务的超时时间，取消本行的状态判断
					String value = future.get();
					LOGGER.info("js task eval finish,value is "+value);
					values.add(value);
					break;// 当前future执行完成，跳出while
				} else {
					Thread.sleep(10);// 每次轮询休息1毫秒（CPU纳秒级），避免CPU高速轮循耗空CPU
				}
			}
		}
		/**
		 * step 3:返回并发执行结果
		 */
		if(callback!=null)
			callback.onCallback(values);
		
		LOGGER.info("当前 appid:"+appid+"执行完成");
	}
	public void close(){
		consulService.stop();
	}
	@Override
	public void onUpdate(String appid,List<JsTemplateData> templates) {
		List<JSTask> taskes = new ArrayList<JSTask>();
		for(JsTemplateData template:templates){
			JSTask task = new JSTask(template.getAppid()+"_"+template.getName(),template.getTemplate().toString());
			taskes.add(task);
		}
		appidTask.put(appid, taskes);
	}
	public interface JSTaskCallBack{
		public void onCallback(List<String> values);
	}
}
