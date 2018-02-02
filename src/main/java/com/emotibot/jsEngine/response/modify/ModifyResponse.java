package com.emotibot.jsEngine.response.modify;

import com.emotibot.jsEngine.response.MyResponseType;
import com.emotibot.middleware.response.AbstractResponse;

public class ModifyResponse extends AbstractResponse
{
    private String tag;
    private String modify;
    
    public ModifyResponse()
    {
        super(MyResponseType.MODIFY);
    }

    public ModifyResponse(String tag, String modify)
    {
        super(MyResponseType.MODIFY);
        this.tag = tag;
        this.modify = modify;
    }
    
    public void setTag(String tag)
    {
        this.tag = tag;
    }
    
    public String getTag()
    {
        return this.tag;
    }
    
    public void setModify(String modify)
    {
        this.modify = modify;
    }
    
    public String getModify()
    {
        return this.modify;
    }
}
