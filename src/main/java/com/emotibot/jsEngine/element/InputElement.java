package com.emotibot.jsEngine.element;

import java.util.Map;

import com.emotibot.middleware.utils.JsonUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InputElement
{
    @SerializedName("domain")
    @Expose
    String domain;
    
    @SerializedName("intent")
    @Expose
    String intent;
    
    @SerializedName("score")
    @Expose
    String score;
    
    @SerializedName("mergeActor")
    @Expose
    String mergeActor;
    
    @SerializedName("matchSentenceType")
    @Expose
    String matchSentenceType;
    
    @SerializedName("src_txt")
    @Expose
    String src_txt;
    
    @SerializedName("semantic")
    @Expose
    Map<String, Object> semantic;
    
    public InputElement()
    {
        
    }
    
    public String getDomain()
    {
        return this.domain;
    }
    
    public String getIntent()
    {
        return this.intent;
    }
    
    public String getScore()
    {
        return this.score;
    }
    
    public String getMergeActor()
    {
        return this.mergeActor;
    }
    
    public String getMatchSentenceType()
    {
        return this.matchSentenceType;
    }
    
    public String getSrc_txt()
    {
        return this.src_txt;
    }
    
    public Map<String, Object> getSemantic()
    {
        return this.semantic;
    }
    
    public void setSemantic(Map<String, Object> semantic)
    {
        this.semantic = semantic;
    }
    
    @Override
    public String toString()
    {
        return JsonUtils.getJsonStr(this);
    }
}
