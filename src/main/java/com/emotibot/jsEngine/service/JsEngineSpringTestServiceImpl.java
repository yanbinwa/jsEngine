package com.emotibot.jsEngine.service;

import java.net.HttpURLConnection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.emotibot.middleware.conf.ConfigManager;
import com.emotibot.middleware.request.HttpRequest;
import com.emotibot.middleware.request.HttpRequestType;
import com.emotibot.middleware.response.HttpResponse;
import com.emotibot.middleware.utils.HttpUtils;
import com.emotibot.middleware.utils.JsonUtils;
import com.emotibot.middleware.utils.StringUtils;
import com.emotibot.middleware.utils.UrlUtils;
import com.google.gson.JsonObject;

@Service("jsEngineSpringTestService")
public class JsEngineSpringTestServiceImpl implements JsEngineSpringTestService
{

    @Autowired
    JsEngineSpringService jsEngineSpringService;
    
    @Override
    public String test(String appid, String userid, String cmd, String nocache, String text)
    {
        String response = getControllerResponse(appid, userid, cmd, nocache, text);
        if (StringUtils.isEmpty(response))
        {
            return response;
        }
        String data = getDataFromResponse(response);
        if (StringUtils.isEmpty(data))
        {
            return response;
        }
        data = adjustData(data);
        String reply = jsEngineSpringService.getReply(userid, appid, data);
        if (StringUtils.isEmpty(reply))
        {
            return response;
        }
        response = addReplyToResponse(reply, response);
        return response;
    }

    private String getControllerResponse(String appid, String userid, String cmd, String nocache, String text)
    {
        String url = ConfigManager.INSTANCE.getPropertyString(MyConstants.JS_TEST_CONTROLLER_URL_KEY) + 
                String.format(MyConstants.JS_TEST_CONTROLLER_REQUEST, appid, cmd, userid, UrlUtils.urlEncode(text));
        if (!StringUtils.isEmpty(nocache))
        {
            url += "&nocache=" + nocache;
        }
        HttpRequest request = new HttpRequest(url, null, HttpRequestType.GET);
        HttpResponse response = HttpUtils.call(request, MyConstants.TIMEOUT);
        int stateCode = response.getStateCode();
        if (stateCode != HttpURLConnection.HTTP_OK)
        {
            return null;
        }
        String res = response.getResponse();
        return res;
    }
    
    private String getDataFromResponse(String response)
    {
        JsonObject responseObj = (JsonObject) JsonUtils.getObject(response, JsonObject.class);
        try
        {
            JsonObject dataObj = responseObj.get("data").getAsJsonArray().get(0).getAsJsonObject().get("data").getAsJsonObject();
            if (!dataObj.has("semantic"))
            {
                return null;
            }
            return dataObj.toString();
        }
        catch(Exception e)
        {
            return null;
        }
    }
    
    private String addReplyToResponse(String reply, String response)
    {
        JsonObject responseObj = (JsonObject) JsonUtils.getObject(response, JsonObject.class);
        try
        {
            JsonObject dataObj = responseObj.get("data").getAsJsonArray().get(0).getAsJsonObject().get("data").getAsJsonObject();
            dataObj.addProperty("reply", reply);
            return responseObj.toString();
        }
        catch(Exception e)
        {
            return response;
        }
    }
    
    /**
     * 模拟平台层对于语义层的改变
     * 
     * @param dataStr
     * @return
     */
    private String adjustData(String dataStr)
    {
        JsonObject dataObj = (JsonObject) JsonUtils.getObject(dataStr, JsonObject.class);
        JsonObject semanticObj = dataObj.get("semantic").getAsJsonObject();
        String domain = dataObj.get("domain").getAsString();
        String intent = dataObj.get("intent").getAsString();
        String cmd = domain + "_" + intent;
        //1. likelyName -> name
        if (cmd.equals("VIDEO_QUERY") && semanticObj.has("likely_name"))
        {
            semanticObj.addProperty("name", semanticObj.get("likely_name").getAsString());
            semanticObj.remove("likely_name");
        }
        
        //2. VIDEO_QUERY name extra -> name_extra
        if (cmd.equals("VIDEO_QUERY") && semanticObj.has("name") && semanticObj.has("extra"))
        {
            semanticObj.addProperty("name", semanticObj.get("name").getAsString() + semanticObj.get("extra").getAsString());
            semanticObj.remove("extra");
        }
        
        //3. VIDEO_PLAY semantic有value无source时，新增source字段
        if (cmd.equals("VIDEO_PLAY") && semanticObj.has("value") && !semanticObj.has("source"))
        {
            semanticObj.addProperty("source", "EPG");
        }
        
        //4. VIDEO_QUERY 存在type但无category，新增category为电影
        if (cmd.equals("VIDEO_QUERY") && semanticObj.has("type") && !semanticObj.has("category"))
        {
            semanticObj.addProperty("category", "电影");
        }
        
        //5. VIDEO_QUERY term -> episode part -> season
        if (cmd.equals("VIDEO_QUERY") && semanticObj.has("term"))
        {
            semanticObj.addProperty("episode", semanticObj.get("term").getAsString());
            semanticObj.remove("term");
        }
        if (cmd.equals("VIDEO_QUERY") && semanticObj.has("part"))
        {
            semanticObj.addProperty("season", semanticObj.get("part").getAsString());
            semanticObj.remove("part");
        }
        
        //6. VIDEO_QUERY source name -> VIDEO_PLAY
        if (cmd.equals("VIDEO_QUERY") && semanticObj.has("source") && semanticObj.has("name"))
        {
            dataObj.addProperty("intent", "PLAY");
        }
        return dataObj.toString();
    }
    
    class MyConstants
    {
        public static final String JS_TEST_CONTROLLER_URL_KEY = "JS_TEST_CONTROLLER_URL";
        public static final String JS_TEST_CONTROLLER_REQUEST = "?appid=%s&cmd=%s&userid=%s&text=%s";
        public static final int TIMEOUT = 2000;
    }
    
}
