package com.emotibot.jsEngine.task;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.emotibot.jsEngine.common.Constants;
import com.emotibot.jsEngine.response.modify.ModifyResponse;
import com.emotibot.middleware.conf.ConfigManager;
import com.emotibot.middleware.request.HttpRequest;
import com.emotibot.middleware.request.HttpRequestType;
import com.emotibot.middleware.response.HttpResponse;
import com.emotibot.middleware.response.Response;
import com.emotibot.middleware.task.AbstractTask;
import com.emotibot.middleware.utils.HttpUtils;
import com.emotibot.middleware.utils.JsonUtils;
import com.emotibot.middleware.utils.StringUtils;
import com.emotibot.middleware.utils.UrlUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class FetchModifyTask extends AbstractTask
{
    private static Random random = new Random();
    
    private String tag;
    private String value;
    
    public FetchModifyTask(String tag, String value)
    {
        this.tag = tag;
        this.value = value;
    }
    
    @Override
    public Response call() throws Exception
    {
        String url = ConfigManager.INSTANCE.getPropertyString(Constants.KNOWLEDGE_URL_KEY);
        String endpoint = String.format(MyConstants.MODIFY_ENDPOINT, UrlUtils.urlEncode(value), UrlUtils.urlEncode(MyConstants.MODIFY_PROPERTY_KEY));
        HttpRequest request = new HttpRequest(url + endpoint, null, HttpRequestType.GET);
        HttpResponse response = HttpUtils.call(request, MyConstants.TIMEOUT);
        int stateCode = response.getStateCode();
        if (stateCode != HttpURLConnection.HTTP_OK)
        {
            return null;
        }
        String res = response.getResponse();
        if (StringUtils.isEmpty(res))
        {
            return null;
        }
        JsonObject obj = (JsonObject) JsonUtils.getObject(res, JsonObject.class);
        if (obj == null || !obj.has("entityA"))
        {
            return null;
        }
        JsonArray modifyListObj = obj.get("entityA").getAsJsonArray();
        List<String> modifyList = new ArrayList<String>();
        for (int i = 0; i < modifyListObj.size(); i ++)
        {
            JsonObject modifyObj = modifyListObj.get(i).getAsJsonObject();
            if (modifyObj.has(MyConstants.MODIFY_PROPERTY_KEY))
            {
                modifyList.add(modifyObj.get(MyConstants.MODIFY_PROPERTY_KEY).getAsString());
            }
        }
        if (modifyList.isEmpty())
        {
            return null;
        }
        int index = random.nextInt(modifyList.size());
        String modify = modifyList.get(index);
        return new ModifyResponse(tag, modify);
    }
    
    class MyConstants
    {
        public static final String MODIFY_ENDPOINT = "?entityA=%s&propertyKey=%s";
        public static final String MODIFY_PROPERTY_KEY = "修饰";
        public static final int TIMEOUT = 100;
    }

}
