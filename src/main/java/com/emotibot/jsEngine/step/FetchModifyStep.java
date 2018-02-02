package com.emotibot.jsEngine.step;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import com.emotibot.jsEngine.common.Constants;
import com.emotibot.jsEngine.response.MyResponseType;
import com.emotibot.jsEngine.response.modify.ModifyResponse;
import com.emotibot.jsEngine.task.FetchModifyTask;
import com.emotibot.middleware.context.Context;
import com.emotibot.middleware.response.Response;
import com.emotibot.middleware.step.AbstractStep;

public class FetchModifyStep extends AbstractStep
{
    
    public FetchModifyStep()
    {
        
    }
    
    public FetchModifyStep(ExecutorService executorService)
    {
        super(executorService);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void beforeRun(Context context)
    {
        Map<String, String> modifyTagsToValueMap = 
                (Map<String, String>) context.getValue(Constants.MODIFY_TAG_TO_VALUE_MAP_KEY);
        if (modifyTagsToValueMap == null || modifyTagsToValueMap.isEmpty())
        {
            return;
        }
        
        for (Map.Entry<String, String> entry : modifyTagsToValueMap.entrySet())
        {
            String tag = entry.getKey();
            String value = entry.getValue();
            FetchModifyTask task = new FetchModifyTask(tag, value);
            task.setUniqId(context.getUniqId());
            this.addTask(context, task);
        }
    }

    @Override
    public void afterRun(Context context)
    {
        List<Response> responseList = getOutputMap(context).get(MyResponseType.MODIFY);
        if (responseList == null || responseList.isEmpty())
        {
            return;
        }
        Map<String, String> modifyTagToModifyMap = new HashMap<String, String>();
        for (Response response : responseList)
        {
            ModifyResponse modifyResponse = (ModifyResponse)response;
            if (modifyResponse == null)
            {
                continue;
            }
            modifyTagToModifyMap.put(modifyResponse.getTag(), modifyResponse.getModify());
        }
        
        context.setValue(Constants.MODIFY_TAG_TO_MODIFY_MAP_KEY, modifyTagToModifyMap);
    }

}
