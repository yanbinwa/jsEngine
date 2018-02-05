package com.emotibot.jsEngine.common;

public class Constants
{
    public static final String INPUT_ELEMENT_NAME = "data";
    public static final String SERVICE_NAME = "service";
    public static final String HELPER_NAME = "helper";
    public static final String USER_ID = "userid";
    public static final String CONTEXT_NAME = "context";
    public static final String APPID = "appid";
    
    public static final String JS_FILE_KEY = "JS_FILE";
    public static final String COMMON_JS_FILE_KEY = "COMMON_JS_FILE";
    
    public static final String TEMPLATE_FILE_KEY = "TEMPLATE_FILE";
    public static final String SYNONYM_FILE_KEY = "SYNONYM_FILE";
    
    //Modify step
    public static final String MODIFY_TAG_TO_VALUE_MAP_KEY = "MODIFY_TAG_TO_VALUE_MAP";
    public static final String MODIFY_TAG_TO_MODIFY_MAP_KEY = "MODIFY_TAG_TO_MODIFY_MAP_KEY";
    
    //Knowledge
    public static final String KNOWLEDGE_URL_KEY = "KNOWLEDGE_URL";
    
    //Consul
    public static final String CONSUL_SERVICE_URL_KEY = "JSENGINE_CONSUL_SERVICE_URL_KEY";
    public static final String CONSUL_KEY_PREFIX_KEY = "JSENGINE_CONSUL_KEY_PREFIX_KEY";
    public static final String RUN_ON_LOCAL_KEY = "JSENGINE_RUN_ON_LOCAL_KEY";
    
    public static final int CONSUL_INTERVAL_TIME = 2;
    public static final int CONSUL_WAIT_TIME = 2;
    
    public static final String CONSUL_VALUE_JSON_KEY_VERSION = "version";
    public static final String CONSUL_VALUE_JSON_KEY_SYNONYM_URL = "synonym_url";
    public static final String CONSUL_VALUE_JSON_KEY_TEMPLATE_URL = "template_url";
    public static final String CONSUL_VALUE_JSON_KEY_JS_COMMON_LIST = "common";
    public static final String CONSUL_VALUE_JSON_KEY_JS_LIST = "templates";
    public static final String CONSUL_VALUE_JSON_KEY_JS_URL = "url";
    public static final String CONSUL_VALUE_JSON_KEY_JS_NAME = "name";
    public static final String LINE_SPLIT_REGEX = "\\r\\n|\\n|\\r";
    public static final int CONSUL_JOIN_TIME = 20;
    
    //JsUtils
    public static final String COMMON_JS_NAME = "common";
    
}
