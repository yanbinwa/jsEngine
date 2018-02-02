package com.emotibot.jsEngine.execute;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import com.emotibot.middleware.conf.ConfigManager;

public class JSExecutorService {
	private static Logger  LOGGER = Logger.getLogger(JSExecutorService.class);
//	private volatile boolean allowCoreThreadTimeOut;
	private volatile int   corePoolSize    = 8;  
	private volatile int   maximumPoolSize = 20;
	/**
	 * 以纳秒为单位的超时时间，如果allowCoreThreadTimeOut设置为true,则空闲的多余corePoolSize的线程将会在存活keepAliveTime时长后终止。
	 */
	private volatile long  keepAliveTime = 60000;
	
	private ThreadPoolExecutor executor;
	private BlockingQueue<Runnable> workQueues;
	
	/**
	 * step1:读取配置文件获取核心线程、最大线程数量以及空闲线程存活时间
	 * step2:初始化线程队列
	 */
	public void init(){
		corePoolSize    = ConfigManager.INSTANCE.getPropertyInt(JSExecutorService.Constants.JS_CORE_POOL_SIZE_KEY);
		maximumPoolSize = ConfigManager.INSTANCE.getPropertyInt(JSExecutorService.Constants.JS_MAX_MUM_SIZE_KEY);
		keepAliveTime   = ConfigManager.INSTANCE.getPropertyInt(JSExecutorService.Constants.JS_KEEP_ALIVE_TIME_LEY);
		
		workQueues      = new LinkedBlockingQueue<Runnable>();
		executor        = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, workQueues);
	}
	public Future<String> execute(JSTask task,Callable<String> callable){
		return executor.submit(callable);
	}
	public void close(){
		executor.shutdown();
	}
	public static class Constants {
		static String JS_CORE_POOL_SIZE_KEY     = "JS_CORE_POOL_SIZE";
		static String JS_MAX_MUM_SIZE_KEY       = "JS_MAX_MUM_SIZE";
		static String JS_KEEP_ALIVE_TIME_LEY    = "JS_KEEP_ALIVE_TIME";
	}
}
