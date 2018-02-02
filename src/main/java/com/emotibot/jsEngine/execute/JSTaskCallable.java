package com.emotibot.jsEngine.execute;

import java.util.Map;
import java.util.concurrent.Callable;
/**
 * future 执行回掉
 * @author liguorui
 *
 */
public class JSTaskCallable implements Callable<String>{
	private JSTask task;
	private Map<String, String> dataMap;
	public JSTaskCallable(JSTask task,Map<String, String> dataMap) {
		this.task = task;
		this.dataMap = dataMap;
	}
	@Override
	public String call() throws Exception {
		return task.eval(this.dataMap);
	}

}