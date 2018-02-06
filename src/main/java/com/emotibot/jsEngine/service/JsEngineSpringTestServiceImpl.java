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
    
    class MyConstants
    {
        public static final String JS_TEST_CONTROLLER_URL_KEY = "JS_TEST_CONTROLLER_URL";
        public static final String JS_TEST_CONTROLLER_REQUEST = "?appid=%s&cmd=%s&userid=%s&text=%s";
        public static final int TIMEOUT = 2000;
    }
    
}
