package com.emotibot.jsEngine.execute;

import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.log4j.Logger;

public class JSTask{
	private static Logger  LOGGER = Logger.getLogger(JSTask.class);
	private volatile String id;//task唯一键
	private volatile String template;//js模版数据
	private volatile ScriptEngineManager scriptManager;//
	private volatile ScriptEngine        scriptEngine;//javascript 执行引擎
	
	public JSTask(String id,String template){
		this.id = id;
		this.template = template;
	}

	/**
	 * js task 初始化动作
	 * 1、数据封装.
	 * 2、加载模版内容
	 */
	public void init(){
		scriptManager = new ScriptEngineManager();
		scriptEngine = scriptManager.getEngineByName("js");
		try {
			scriptEngine.eval("1+1");
		} catch (ScriptException e) {
			LOGGER.info("JSTask init eval");
		}
	}
	/**
	 * 模版任务执行
	 */
	public String eval(Map<String, String> dataMap) {
		try {
			//初始化数据
			for(String key:dataMap.keySet()){
				scriptEngine.put(key, dataMap.get(key));
			}
			//执行模版引擎
			String result = (String) scriptEngine.eval(template);
			return result;
		} catch (ScriptException e) {
			LOGGER.info("JSTask eval failse");
		}
		return null;
	}
//	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}
}
