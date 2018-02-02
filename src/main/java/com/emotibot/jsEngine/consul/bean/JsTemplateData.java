package com.emotibot.jsEngine.consul.bean;

import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emotibot.jsEngine.utils.consul.HttpUtils;
import com.emotibot.jsEngine.utils.consul.MD5Util;

/**
 * js模版数据:
 * 1、主要存储consule中appid、md5以及获取的js模版数据(通过二次网络请求获取)
 * 2、更新对象属性时加锁，避免线程更新时候，所数据不一致
 * 3、更趋consul返回的js模版url获取js模版数据
 * @author liguorui
 *
 */
public class JsTemplateData {
	private static final Logger logger = LoggerFactory.getLogger(JsTemplateData.class);
	private String appid;//js模版对应的appid
	private String name;//模版名称
//	private String localMD5;//更新对象时使用的md5码值
	private StringBuffer  template = new StringBuffer();//js模版文件
	
	private ReentrantLock lock = new ReentrantLock();//对象操作锁
	
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
    public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public StringBuffer getTemplate() {
		return template;
	}
	public void setTemplate(StringBuffer template) {
		this.template = template;
	}
	public boolean updateJSTemplate(String appid, String url){
        if (appid == null || !appid.equals(this.appid)){
            logger.error("appid shoud not be null or appid is not equal to current id");
            return false;
        }
        lock.lock();
        try{
            boolean ret = updateJsDataConsul(url);
            return ret;
        }finally{
            lock.unlock(); 
        }
    }
	/**
	 * 加载js模版
	 * @param url
	 * @return
	 */
    private boolean updateJsDataConsul(String url)
    {
        // 如果为设置对应的url或md5码，则不做后续处理
        if(null == url){
            logger.error("url should not be null");
            return false;
        }
        String jsData = getRemoteJsContent(url);
        
        if (jsData == null || jsData.length() <= 0){
            logger.error("fail to get the JsContent");
            return false;
        }
        template.delete(0, template.length());
        template.append(jsData);
        return true;
    }
    /**
     * 根据consul返回的参数，远程获取js模版
     * @param url
     * @return
     */
    private String getRemoteJsContent(String url){
    	logger.info("步骤一：下载远程JS模版文件");
        byte[] out = HttpUtils.getContent(url);
        if (null == out){
            logger.info(String.format("下载文件出错，跳过"));
            return null;
        }else{
            logger.info("下载完成");
        }
        logger.info("步骤二：远程JS模版文件下载完成");
        String content = new String(out);
        return content;
    }
}
