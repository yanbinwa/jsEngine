package com.emotibot.jsEngine.consul;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emotibot.configclient.ConfigClientOptions;
import com.emotibot.configclient.ConfigResponseCallback;
import com.emotibot.configclient.ConsulConfigClient;
import com.emotibot.jsEngine.consul.bean.JsTemplateData;
import com.emotibot.jsEngine.exception.ConfigNotFoundException;
import com.emotibot.jsEngine.utils.JsonUtils;
import com.emotibot.jsEngine.utils.ReadWriteMap;
import com.emotibot.jsEngine.utils.consul.Base64CoderUtil;
import com.emotibot.jsEngine.utils.consul.HttpUtils;
import com.emotibot.middleware.conf.ConfigManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.util.JSONUtils;

/**
 * 线程同步consul数据，根据更新时间，以及check对应版本md5
 * @author liguorui
 */
public class JsConsulServiceImpl implements JsConsulService {
	private static final Logger logger = LoggerFactory.getLogger(JsConsulServiceImpl.class);
	
	private int round = 0;
	private ReadWriteMap<String,String> appidLocalMD5Map = new ReadWriteMap<String,String>();//更新对象时使用的md5码值
	private ReentrantLock lock = new ReentrantLock();
	
	private String consulServiceURL = null;//consul url地址
	private String consulKeyPrefix = null;//consul 数据key

	private ConsulConfigClient consulConfigClient = null;//consul执行client
	private ConfigResponseCallback callback = new ConsulCallback();//consul返回回调callback
	private AtomicBoolean isStart = new AtomicBoolean(false);
	private AtomicBoolean isReady = new AtomicBoolean(false);
	
//	private Map<String, JsTemplateData> appidJsTemplates = new HashMap<String,JsTemplateData>();//appid 与js模版
	
	private JSTemplateListener listener;
	
	
	public JsConsulServiceImpl() throws ConfigNotFoundException {
		init();
	}
	/**
	 * 初始化配置参数
	 * @throws ConfigNotFoundException
	 */
	private void init() throws ConfigNotFoundException {
		//consul url地址
		consulServiceURL = ConfigManager.INSTANCE.getPropertyString(JsConsulServiceImpl.Constant.CONSUL_SERVICE_URL_KEY);
		if (consulServiceURL == null || consulServiceURL.trim().isEmpty()) {
			logger.error("Can not get consulServiceURL by key " + JsConsulServiceImpl.Constant.CONSUL_SERVICE_URL_KEY);
			throw new ConfigNotFoundException();
		}
		consulKeyPrefix = ConfigManager.INSTANCE.getPropertyString(JsConsulServiceImpl.Constant.CONSUL_KEY_PREFIX_KEY);
		if (consulKeyPrefix == null || consulKeyPrefix.trim().isEmpty()) {
			logger.error("Can not get consulKeyPrefix by key " + JsConsulServiceImpl.Constant.CONSUL_KEY_PREFIX_KEY);
			throw new ConfigNotFoundException();
		}
	}
	/**
	 * 开始执行consul
	 */
	@Override
	public void start() {
		if (!isStart.get()) {
			logger.debug("Start DictionaryService ... ");
			isStart.set(true);
			registerConsul(consulServiceURL, consulKeyPrefix, callback);
		} else {
			logger.debug("DictionaryService has ready started");
		}
	}
	/**
	 * 设置监听器
	 */
	public void setListener(JSTemplateListener listener) {
		this.listener = listener;
	}
	/**
	 * 停止consul运行
	 */
	@Override
	public void stop() {
		if (isStart.get()) {
			logger.debug("Stop DictionaryService ... ");
			isStart.set(false);
			if (consulConfigClient != null) {
				consulConfigClient.interrupt();
				consulConfigClient = null;
			}
			isReady.set(false);
		} else {
			logger.debug("DictionaryService has ready stopped");
		}
	}
	/**
	 * 判断当前consul心跳是否在运行中
	 */
	@Override
	public boolean isReady() {
		return isServiceReady();
	}
	
	/**
	 * 可以异步更新字典，需要上锁
	 * 
	 * @param kvs
	 */
	private void updateDictionary(Map<String, String> kvs) {
		lock.lock();
		List<String> remoteAppidName = new ArrayList<String>();
		try {
			logger.info(String.format("一共%d个第三方词典", kvs.entrySet().size()));
			int count = 0;
			for (Map.Entry<String, String> thisEntry : kvs.entrySet()) {
				String key = thisEntry.getKey();
				String value = thisEntry.getValue();
				count++;
				logger.info(String.format("检查第%d/%d个词典 key:%s, value:%s\n", count, kvs.size(), key, value));

				// 解析得到第三方条目的appid, md5, url,synonym-md5,synonym-url
				logger.info("步骤一：获取appid，md5");
				String appid = getAppid(key);
				logger.debug(String.format("appid = [%s]\n", appid));

				Map<String, String> valuesMap = getTemlatesMd5(value);
				if (valuesMap.keySet().size() < 2) {
					logger.debug(String.format("信息不全,当前信息" + valuesMap.keySet()));
					continue;
				}
				
				/**
				 * check md5值
				 */
				logger.info("步骤二：检测MD5版本");
				String md5 = valuesMap.get(JsConsulServiceImpl.Constant.CONSUL_VALUE_JSON_KEY_MD5);
				if(skipMd5(appid,md5)){
					logger.debug(String.format("已经为最新版本，不需要更新"));
					return;
				}
				
				logger.info("步骤三：加载JS模版文件");
				//解析JS模版数据
				String templateJSONStr = kvs.get(Constant.CONSUL_VALUE_JSON_KEY_TEMPLATES);
				if(!JSONUtils.mayBeJSON(templateJSONStr)){
					logger.debug(String.format("模版文件信息异常"));
					return;
				}
				
				/**
				 * 解析和加载模版文件信息
				 */
				logger.info("步骤四：下载模版文件");
				JSONArray templatesJson = JSONArray.fromObject(templateJSONStr);
				List<Map<String,String>> templateList = new ArrayList<Map<String,String>>();
				for(int i=0;i<templatesJson.size();i++){
					JSONObject templateJson = templatesJson.getJSONObject(i);
					if(templateJson!=null){
						templateList.add(JsonUtils.jsonToMap(templateJson));
					}
				}
				List<JsTemplateData> templates = new ArrayList<JsTemplateData>();
				for(Map<String,String> param:templateList){
					String name = param.get(JsConsulServiceImpl.Constant.EMPLATE_VALUE_JSON_KEY_NAME);
					String url = param.get(JsConsulServiceImpl.Constant.EMPLATE_VALUE_JSON_KEY_URL);
					logger.debug(String.format("加载%d模版文件,url:%d",name,url));
					JsTemplateData jsTemplate = new JsTemplateData();
					jsTemplate.setName(name);
					jsTemplate.updateJSTemplate(appid, url);
					templates.add(jsTemplate);
				}
				/**
				 * 发送监听器通知
				 */
				if(listener!=null)
					listener.onUpdate(appid,templates);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("更新本地词典失败");
			return;
		} finally {
			lock.unlock();
		}
	}

	private String getAppid(String key) {
		String appid = key.replace(consulKeyPrefix + "/", "");
		return appid;
	}

	private Map<String, String> getTemlatesMd5(String value) {
		Map<String, String> templatesMd5Map = new HashMap<String, String>();
		// 解码value
		String valueDecoded = base64Decode(value);
		if (null == valueDecoded) {
			return templatesMd5Map;
		}

		// 解析得到json对象
		JSONObject thisEntryValueJson = new JSONObject();
		thisEntryValueJson = (JSONObject) JSONSerializer.toJSON(valueDecoded);
		// 从json对象得到key和value对
		Map<String, String> valuesMap = getValuesFromJson(thisEntryValueJson);
		if (!(valuesMap.keySet().contains(JsConsulServiceImpl.Constant.CONSUL_VALUE_JSON_KEY_TEMPLATES)
				&& valuesMap.keySet().contains(JsConsulServiceImpl.Constant.CONSUL_VALUE_JSON_KEY_MD5))) {
			return templatesMd5Map;
		}

		String templates = valuesMap.get(JsConsulServiceImpl.Constant.CONSUL_VALUE_JSON_KEY_TEMPLATES);
		templatesMd5Map.put(JsConsulServiceImpl.Constant.CONSUL_VALUE_JSON_KEY_TEMPLATES, templates);

		String md5 = valuesMap.get(JsConsulServiceImpl.Constant.CONSUL_VALUE_JSON_KEY_MD5);
		templatesMd5Map.put(JsConsulServiceImpl.Constant.CONSUL_VALUE_JSON_KEY_MD5, md5);

		return templatesMd5Map;
	}
	/**
	 * 根据MD5 check是否要更新
	 * @param remoteMd5
	 * @return
	 */
	private boolean skipMd5(String appid,String remoteMd5){
		String localMD5 = appidLocalMD5Map.get(appid);
		if(localMD5==null){
			appidLocalMD5Map.put(appid, localMD5);
			return false;
		}
		if(!localMD5.equals(remoteMd5)){
			appidLocalMD5Map.put(appid, localMD5);
			return false;
		}
		if(localMD5.equals(remoteMd5)){
			return true;
		}
		appidLocalMD5Map.put(appid, localMD5);
		return false;
	}
	private String base64Decode(String value) {
		try {
			String valueDecoded = Base64CoderUtil.decodeBase64ToString(value);
			if (null == valueDecoded || valueDecoded.isEmpty()) {
				logger.error(String.format("解码后值为空:[%s]", value));
				return null;
			}
			return valueDecoded;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(String.format("解码失败:[%s]", value));
			return null;
		}
	}

	private Map<String, String> getValuesFromJson(JSONObject thisEntryValueJson) {
		Map<String, String> valuesMap = new HashMap<String, String>();

		try {
			String templates = thisEntryValueJson.optString(JsConsulServiceImpl.Constant.CONSUL_VALUE_JSON_KEY_TEMPLATES, "");
			logger.debug(String.format("templates = [%s]\n", templates));
			if (!StringUtils.isEmpty(templates)) {
				valuesMap.put(JsConsulServiceImpl.Constant.CONSUL_VALUE_JSON_KEY_TEMPLATES, templates);
			}

			String md5 = thisEntryValueJson.optString(JsConsulServiceImpl.Constant.CONSUL_VALUE_JSON_KEY_MD5, "");
			logger.debug(String.format("md5 = [%s]\n", md5));
			if (!StringUtils.isEmpty(md5)) {
				valuesMap.put(JsConsulServiceImpl.Constant.CONSUL_VALUE_JSON_KEY_MD5, md5);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(String.format("json格式错误: [%s]\n", thisEntryValueJson));
		}

		return valuesMap;
	}
	/**
	 * 注册 consul
	 * @param consulServiceURL
	 * @param consulKeyPrefix
	 * @param callback
	 * @return
	 */
	private boolean registerConsul(String consulServiceURL, String consulKeyPrefix, ConfigResponseCallback callback) {
		try {
			ConfigClientOptions options = new ConfigClientOptions();
			options.setRecurse(true);
			options.setInterval(JsConsulServiceImpl.Constant.CONSUL_INTERVAL_TIME);
			options.setWait(JsConsulServiceImpl.Constant.CONSUL_WAIT_TIME);

			String hostAndPort = HttpUtils.getHostAndPort(consulServiceURL);
			consulConfigClient = new ConsulConfigClient(hostAndPort, consulKeyPrefix, callback, options);
			consulConfigClient.start();
			consulConfigClient.join(JsConsulServiceImpl.Constant.CONSUL_JOIN_TIME);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	private boolean isServiceReady() {
		return isStart.get() && isReady.get();
	}
	/**
	 * 
	 * @author liguorui
	 *
	 */
	class ConsulCallback implements ConfigResponseCallback {
		public void onUpdate(Map<String, String> kvs) {
			if (null == kvs || (kvs.size() <= 0)) {
				return;
			}
			round++;
			logger.info(String.format("第%d轮开始：%s,%s", round, "更新第三方词典", new Date()));
			
			if(!kvs.containsKey(Constant.CONSUL_VALUE_JSON_KEY_TEMPLATES)){
			    logger.info(kvs.toString());
				logger.info(String.format("第%d轮结束：%s,%s", round, "未发现需要更新的模版", new Date()));
				return;
			}
			updateDictionary(kvs);
			logger.info(String.format("第%d轮结束：%s,%s", round, "更新第三方词典", new Date()));
			isReady.set(true);
		}
	}
	public static class Constant {
		public static final String CONSUL_SERVICE_URL_KEY = "CONSUL_SERVICE_URL_KEY";
		public static final String CONSUL_KEY_PREFIX_KEY = "CONSUL_KEY_PREFIX_KEY";
		
		public static final String EMPLATE_VALUE_JSON_KEY_NAME= "name";
	    public static final String EMPLATE_VALUE_JSON_KEY_URL = "url";
	    
	    
	    public static final String CONSUL_VALUE_JSON_KEY_TEMPLATES= "templates";
	    public static final String CONSUL_VALUE_JSON_KEY_MD5 = "md5";
	    public static final String LINE_SPLIT_REGEX = "\\r\\n|\\n|\\r";
	    
	    public static final int CONSUL_INTERVAL_TIME = 2;
	    public static final int CONSUL_WAIT_TIME = 2;
	    public static final int CONSUL_JOIN_TIME = 20;
	   
	}
	/**
	 * 模版监听器，用于模版更新和删除
	 * @author liguorui
	 *
	 */
	public interface JSTemplateListener{
		public void onUpdate(String appid,List<JsTemplateData> templates);
	}
}
