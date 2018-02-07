package com.emotibot.jsEngine.consul;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.emotibot.configclient.ConfigClientOptions;
import com.emotibot.configclient.ConfigResponseCallback;
import com.emotibot.configclient.ConsulConfigClient;
import com.emotibot.jsEngine.common.Constants;
import com.emotibot.jsEngine.utils.JsUtils;
import com.emotibot.jsEngine.utils.TemplateUtils;
import com.emotibot.jsEngine.utils.consul.Base64CoderUtils;
import com.emotibot.jsEngine.utils.consul.ConsoleUtils;
import com.emotibot.jsEngine.utils.consul.HttpUtils;
import com.emotibot.middleware.conf.ConfigManager;
import com.emotibot.middleware.utils.JsonUtils;
import com.emotibot.middleware.utils.StringUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * 监听consul特定kv，如果有改变，读取其内容，这里只是针对特定的appid
 * 
 * @author emotibot
 *
 */
public class SynonymServiceImpl implements SynonymService
{
    private static Logger logger = Logger.getLogger(SynonymServiceImpl.class);
    
    private boolean isRun = false;
    private ReentrantLock lock = new ReentrantLock();
    
    private String consulServiceURL = null;
    private String consulKeyPrefix = null;
    private boolean isRunLocal = false;
    private Map<String, String> localVersionMap = new HashMap<String, String>();
    
    private ConsulConfigClient consulConfigClient = null;
    private ConfigResponseCallback callback = new ConsulCallback();
    
    private int round = 0;
    
    public SynonymServiceImpl()
    {
        init();
    }
    
    @Override
    public void start()
    {
        if (!isRun)
        {
            isRun = true;
            registerConsul(consulServiceURL, consulKeyPrefix, callback);
            logger.info("SynonymService is started");
        }
        else
        {
            logger.warn("SynonymService has already running");
        }
    }

    @Override
    public void stop()
    {
        if(isRun)
        {
            isRun = false;
            if (consulConfigClient != null)
            {
                consulConfigClient.interrupt();
                consulConfigClient = null;
            }
            logger.info("SynonymService is stopped");
        }
        else
        {
            logger.warn("SynonymService is not running yet");
        }
    }
    
    private void init()
    {
        consulServiceURL = ConfigManager.INSTANCE.getPropertyString(Constants.CONSUL_SERVICE_URL_KEY);
        consulKeyPrefix = ConfigManager.INSTANCE.getPropertyString(Constants.CONSUL_KEY_PREFIX_KEY);
        isRunLocal = ConfigManager.INSTANCE.getPropertyBoolean(Constants.RUN_ON_LOCAL_KEY);
    }
    
    private boolean registerConsul(String consulServiceURL, String consulKeyPrefix, ConfigResponseCallback callback)
    {
        try 
        {
            ConfigClientOptions options = new ConfigClientOptions();
            options.setRecurse(true);
            options.setInterval(Constants.CONSUL_INTERVAL_TIME);
            options.setWait(Constants.CONSUL_WAIT_TIME);

            String hostAndPort = HttpUtils.getHostAndPort(consulServiceURL);
            consulConfigClient = new ConsulConfigClient(hostAndPort, consulKeyPrefix, callback, options);
            consulConfigClient.start();
            consulConfigClient.join(Constants.CONSUL_JOIN_TIME);
            return true;
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return false;
        }
    }
    
    private void updateSynonym(Map<String, String> kvs)
    {
        lock.lock();
        try 
        {
            logger.info(String.format("一共%d个第三方词典", kvs.entrySet().size()));
            int count = 0;
            /**
             * 1. synonym文件
             * 2. template文件
             * 3. js文件
             */
            for (Map.Entry<String, String> thisEntry : kvs.entrySet()) 
            {
                String key = thisEntry.getKey();
                String value = thisEntry.getValue();
                count++;
                logger.info(String.format("检查第%d/%d个词典 key:%s, value:%s\n", count, kvs.size(), key, value));

                // 解析得到第三方条目的appid, version, synonyn.json, template.json, common js脚本, 其它js脚本
                logger.info("步骤一：appid, version, synonyn.json, template.json, common js脚本, 其它js脚本");
                String appid = getAppid(key);
                logger.debug(String.format("appid = [%s]\n", appid));
                
                if (StringUtils.isEmpty(appid))
                {
                    logger.info("appid为空");
                    return;
                }
                
                logger.info("步骤二：对比本地version和远程version");
                JsonObject valuesObj = getUrlMd5(value);

                String version = valuesObj.has(Constants.CONSUL_VALUE_JSON_KEY_VERSION) ? 
                    valuesObj.get(Constants.CONSUL_VALUE_JSON_KEY_VERSION).getAsString() : null;
                    
                if (version == null)
                {
                    logger.error("version为空");
                    return;
                }
                String localVersion = localVersionMap.get(appid);
                if (version.equals(localVersion))
                {
                    logger.info("version号一致，不需要更新");
                    return;
                }
                
                if (valuesObj.has(Constants.CONSUL_VALUE_JSON_KEY_SYNONYM_URL))
                {
                    String synonymUrl = valuesObj.get(Constants.CONSUL_VALUE_JSON_KEY_SYNONYM_URL).getAsString();
                    updateForSynonym(appid, synonymUrl);
                }
                
                if (valuesObj.has(Constants.CONSUL_VALUE_JSON_KEY_TEMPLATE_URL))
                {
                    String tempalteUrl = valuesObj.get(Constants.CONSUL_VALUE_JSON_KEY_TEMPLATE_URL).getAsString();
                    updateForTemplate(appid, tempalteUrl);
                }
                
                if (valuesObj.has(Constants.CONSUL_VALUE_JSON_KEY_JS_COMMON_LIST))
                {
                    JsonArray jsonArray = valuesObj.get(Constants.CONSUL_VALUE_JSON_KEY_JS_COMMON_LIST).getAsJsonArray();
                    updateForCommonJs(appid, jsonArray);
                }
                
                if (valuesObj.has(Constants.CONSUL_VALUE_JSON_KEY_JS_LIST))
                {
                    JsonArray jsonArray = valuesObj.get(Constants.CONSUL_VALUE_JSON_KEY_JS_LIST).getAsJsonArray();
                    updateForJs(appid, jsonArray);
                }
                
                localVersionMap.put(appid, version);
            }
            logger.info("步骤四：更新本地文件完成");
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            logger.error("更新本地词典失败");
            return;
        }
        finally
        {
            lock.unlock();
        }
    }
    
    private boolean updateForSynonym(String appid, String url)
    {
        // 如果为设置对应的url或md5码，则不做后续处理
        if(null == url)
        {
            logger.error("url for synonym should not be null");
            return false;
        }
        url = getAdjustUrl(url);
        String str = getRemoteSynonymContent(url);
        
        if (StringUtils.isEmpty(str))
        {
            logger.error("fail to get the synonym content");
            return false;
        }
        
        TemplateUtils.loadSynonym(appid, str);
        return true;
    }
    
    private boolean updateForTemplate(String appid, String url)
    {
        // 如果为设置对应的url或md5码，则不做后续处理
        if(null == url)
        {
            logger.error("url for template should not be null");
            return false;
        }
        url = getAdjustUrl(url);
        String str = getRemoteSynonymContent(url);
        
        if (StringUtils.isEmpty(str))
        {
            logger.error("fail to get the template content");
            return false;
        }
        
        TemplateUtils.loadConfigs(appid, str);
        return true;
    }
    
    private boolean updateForCommonJs(String appid, JsonArray jsonArray)
    {
        Map<String, String> jsNameToJsContextMap = new HashMap<String, String>();
        for(int i = 0; i < jsonArray.size(); i ++)
        {
            JsonObject obj = jsonArray.get(i).getAsJsonObject();
            if (obj.has(Constants.CONSUL_VALUE_JSON_KEY_JS_NAME) 
                    && obj.has(Constants.CONSUL_VALUE_JSON_KEY_JS_URL))
            {
                String jsName = obj.get(Constants.CONSUL_VALUE_JSON_KEY_JS_NAME).getAsString();
                String url = obj.get(Constants.CONSUL_VALUE_JSON_KEY_JS_URL).getAsString();
                url = getAdjustUrl(url);
                String str = getRemoteSynonymContent(url);
                
                if (StringUtils.isEmpty(str))
                {
                    logger.error("fail to get the common js context");
                    continue;
                }
                jsNameToJsContextMap.put(jsName, str);
            }
        }
        JsUtils.loadCommonJs(appid, jsNameToJsContextMap);
        return true;
    }
    
    private boolean updateForJs(String appid, JsonArray jsonArray)
    {
        Map<String, String> jsNameToJsContextMap = new HashMap<String, String>();
        for(int i = 0; i < jsonArray.size(); i ++)
        {
            JsonObject obj = jsonArray.get(i).getAsJsonObject();
            if (obj.has(Constants.CONSUL_VALUE_JSON_KEY_JS_NAME) 
                    && obj.has(Constants.CONSUL_VALUE_JSON_KEY_JS_URL))
            {
                String jsName = obj.get(Constants.CONSUL_VALUE_JSON_KEY_JS_NAME).getAsString();
                String url = obj.get(Constants.CONSUL_VALUE_JSON_KEY_JS_URL).getAsString();
                url = getAdjustUrl(url);
                String str = getRemoteSynonymContent(url);
                
                if (StringUtils.isEmpty(str))
                {
                    logger.error("fail to get the other js context");
                    continue;
                }
                jsNameToJsContextMap.put(jsName, str);
            }
        }
        JsUtils.loadJs(appid, jsNameToJsContextMap);
        return true;
    }
    
    private String getAdjustUrl(String url)
    {
        try
        {
            /**
             * 如果实在本地的MAC上调试时，synonymUrl返回的host ip是172.17.0.1, 需要改为目前sever地址
             */
            if (isRunLocal)
            {
                String remoteIpOld = ConsoleUtils.getHost(url);
                String remoteIpNew = ConsoleUtils.getHost(consulServiceURL);
                url = url.replaceAll(remoteIpOld, remoteIpNew);
                logger.debug("Replace the old host " + remoteIpOld + " with new one " 
                        + remoteIpNew + " for running on remote");
            }
            return url;
        }
        catch (Exception e)
        {
            return null;
        }
    }
    
    private String getRemoteSynonymContent(String url)
    {
        // 对于md5修改的条目，通过url得到词典文件，检查词典文件的md5是否正确
        logger.info("步骤三：下载远程文件 " + url);

        byte[] out = HttpUtils.getContent(url);
        if (null == out) 
        {
            logger.info(String.format("下载文件出错，跳过"));
            return null;
        } 
        else 
        {
            logger.info("下载完成");
        }

        String content = new String(out);
        return content;
    }
    
    private JsonObject getUrlMd5(String value) 
    {
        // 解码value
        String valueDecoded = base64Decode(value);
        if (null == valueDecoded)
        {
            return null;
        }

        // 解析得到json对象
        JsonObject thisEntryValueJson = (JsonObject) JsonUtils.getObject(valueDecoded, JsonObject.class);

        return thisEntryValueJson;
    }
    
    private String getAppid(String key) 
    {
        String appid = key.replace(consulKeyPrefix + "/", "");
        return appid;
    }
    
    private String base64Decode(String value) 
    {
        try 
        {
            String valueDecoded = Base64CoderUtils.decodeBase64ToString(value);
            if (null == valueDecoded || valueDecoded.isEmpty()) 
            {
                logger.error(String.format("解码后值为空:[%s]", value));
                return null;
            }
            return valueDecoded;
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            logger.error(String.format("解码失败:[%s]", value));
            return null;
        }
    }
    
    class ConsulCallback implements ConfigResponseCallback
    {
        public void onUpdate(Map<String, String> kvs)
        {
            if (null == kvs || (kvs.size() <= 0))
            {
                return;
            }
            round++;
            logger.info(String.format("第%d轮开始：%s,%s", round, "更新第三方词典", new Date()));
            updateSynonym(kvs);
            logger.info(String.format("第%d轮结束：%s,%s", round, "更新第三方词典", new Date()));
        }
    }
}
