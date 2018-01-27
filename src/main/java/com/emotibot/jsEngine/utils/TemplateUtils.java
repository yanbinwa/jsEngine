package com.emotibot.jsEngine.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.emotibot.jsEngine.common.Constants;
import com.emotibot.middleware.conf.ConfigManager;
import com.emotibot.middleware.utils.FileUtils;
import com.emotibot.middleware.utils.StringUtils;

public class TemplateUtils
{
    private static Logger logger = Logger.getLogger(TemplateUtils.class);
    private static ReentrantLock lock = new ReentrantLock();
    /**
     * 模板类型->模板的组成->模板的list
     * 模板类型是：单片名，一个参数，两个参数，三个参数，点播模板，U盘资源模板，TVSET模板
     */
    private static Map<String, Map<String, List<String>>> templateMap;
    /** 前缀例句*/
    private static List<String> beforeList;
    /** 结词例句*/
    private static List<String> afterList;
    /** 其他修饰语，不包含在knowledge中的*/
    private static Map<String, Map<String, List<String>>> modifyMap;
    
    public static final String WITH_NAME_TAG = "Case1";
    public static final String SINGLE_ELEMENT_TAG = "Case2";
    public static final String TWICE_ELEMENT_TAG = "Case3";
    public static final String TRIPLE_AND_ABOVE_ELEMENT_TAG = "Case4";
    public static final String SELECT_TAG = "Case5";
    public static final String U_DISK_TAG = "Case6";
    public static final String TV_SET_TAG = "Case7";
    public static final String BEFORE_TAG = "Case8";
    public static final String AFTER_TAG = "Case9";
    public static final String TYPE_MODIFY_TAG = "Case10";
    
    public static final String NAME_TEMPLATE_TAG = "<Name>";
    public static final String ACTOR_TEMPLATE_TAG = "<Actor>";
    public static final String DIRECTOR_TEMPLATE_TAG = "<Director>";
    public static final String YEAR_TEMPLATE_TAG = "<Year>";
    public static final String CATEGORY_TEMPLATE_TAG = "<Category>";
    public static final String TAG_TEMPLATE_TAG = "<Tag>";
    public static final String RATE_TEMPLATE_TAG = "<Rate>";
    public static final String AWARD_TEMPLATE_TAG = "<Award>";
    public static final String ROLE_TEMPLATE_TAG = "<Role>";
    public static final String AREA_TEMPLATE_TAG = "<Area>";
    public static final String TYPE_TEMPLATE_TAG = "<Type>";
    public static final String SUBAWARD_TEMPLATE_TAG = "<SubAward>";
    public static final String LANGUAGE_TEMPLATE_TAG = "<Language>";
    public static final String PUBLISHER_TEMPLATE_TAG = "<Publisher>";
    public static final String BEFORE_TEMPLATE_TAG = "<Before>";
    
    private static final String TYPE_MODIFY_START_TAG = "<";
    private static final String TYPE_MODIFY_END_TAG = ">";
    private static final String TYPE_MODIFY_SPLIT_TAG = ",";
    
    private static final String[] allTagList = {WITH_NAME_TAG, SINGLE_ELEMENT_TAG, TWICE_ELEMENT_TAG,
            TRIPLE_AND_ABOVE_ELEMENT_TAG, SELECT_TAG, U_DISK_TAG, TV_SET_TAG, BEFORE_TAG, AFTER_TAG, TYPE_MODIFY_TAG};
    private static Set<String> allTagSet;
    
    private static final String[] allTemplateTagList = {NAME_TEMPLATE_TAG, ACTOR_TEMPLATE_TAG, DIRECTOR_TEMPLATE_TAG,
            YEAR_TEMPLATE_TAG, CATEGORY_TEMPLATE_TAG, TAG_TEMPLATE_TAG, RATE_TEMPLATE_TAG, AWARD_TEMPLATE_TAG,
            ROLE_TEMPLATE_TAG, AREA_TEMPLATE_TAG, TYPE_TEMPLATE_TAG, SUBAWARD_TEMPLATE_TAG, LANGUAGE_TEMPLATE_TAG,
            PUBLISHER_TEMPLATE_TAG, BEFORE_TEMPLATE_TAG};
    private static Set<String> allTemplateTagSet;
    
    private static Random random = new Random();
    
    static
    {
        allTagSet = new HashSet<String>();
        for (String tag : allTagList)
        {
            allTagSet.add(tag);
        }
        allTemplateTagSet = new HashSet<String>();
        for (String templateTag : allTemplateTagList)
        {
            allTemplateTagSet.add(templateTag);
        }
        loadConfigs();
    }
    
    public static void loadConfigs()
    {
        lock.lock();
        try
        {
            String templateFile = ConfigManager.INSTANCE.getPropertyString(Constants.TEMPLATE_FILE_KEY);
            Map<String, Map<String, List<String>>> templateMapTmp = new HashMap<String, Map<String, List<String>>>();
            List<String> beforeListTmp = new ArrayList<String>();
            List<String> afterListTmp = new ArrayList<String>();
            Map<String, Map<String, List<String>>> modifyMapTmp = new HashMap<String, Map<String, List<String>>>();
            String currentTag = null;
            List<String> lines = FileUtils.readFile(templateFile);
            for (String line : lines)
            {
                line = line.trim();
                if (StringUtils.isEmpty(line) || line.startsWith("#"))
                {
                    continue;
                }
                if (allTagSet.contains(line))
                {
                    currentTag = line;
                    continue;
                }
                if (StringUtils.isEmpty(currentTag))
                {
                    continue;
                }
                switch(currentTag)
                {
                case WITH_NAME_TAG:
                case SINGLE_ELEMENT_TAG:
                case TWICE_ELEMENT_TAG:
                case TRIPLE_AND_ABOVE_ELEMENT_TAG:
                case SELECT_TAG:
                case U_DISK_TAG:
                case TV_SET_TAG:
                    loadTemplates(currentTag, line, templateMapTmp);
                    break;
                case BEFORE_TAG:
                    loadTemplates1(line, beforeListTmp);
                    break;
                case AFTER_TAG:
                    loadTemplates1(line, afterListTmp);
                    break;
                case TYPE_MODIFY_TAG:
                    loadModify(currentTag, line, modifyMapTmp);
                    break;
                default:
                    logger.error("unsupport tag " + currentTag);
                    break;
                }
            }
            templateMap = templateMapTmp;
            beforeList = beforeListTmp;
            afterList = afterListTmp;
            modifyMap = modifyMapTmp;
        }
        finally
        {
            lock.unlock();
        }
    }
    
    private static void loadTemplates(String tag, String line, Map<String, Map<String, List<String>>> templateMapTmp)
    {
        List<String> templateTagList = new ArrayList<String>();
        for(String templateTag : allTemplateTagList)
        {
            if (line.contains(templateTag))
            {
                templateTagList.add(templateTag);
            }
        }
        if (templateTagList.isEmpty())
        {
            return;
        }
        Map<String, List<String>> singleTemplateMapTmp = templateMapTmp.get(tag);
        if (singleTemplateMapTmp == null)
        {
            singleTemplateMapTmp = new HashMap<String, List<String>>();
            templateMapTmp.put(tag, singleTemplateMapTmp);
        }
        String templateTagKey = generateTemplateTagKey(templateTagList);
        List<String> tempalteList = singleTemplateMapTmp.get(templateTagKey);
        if (tempalteList == null)
        {
            tempalteList = new ArrayList<String>();
            singleTemplateMapTmp.put(templateTagKey, tempalteList);
        }
        tempalteList.add(line);
    }
    
    private static void loadTemplates1(String line, List<String> arrrayList)
    {
        arrrayList.add(line);
    }
    
    private static void loadModify(String tag, String line, Map<String, Map<String, List<String>>> modifyMapTmp)
    {
        Map<String, List<String>> singleModifyMapTmp = modifyMapTmp.get(tag);
        if (singleModifyMapTmp == null)
        {
            singleModifyMapTmp = new HashMap<String, List<String>>();
            modifyMapTmp.put(tag, singleModifyMapTmp);
        }
        String typeModifyTag = getTypeModifyTag(line);
        if (StringUtils.isEmpty(typeModifyTag))
        {
            return;
        }
        List<String> typeModifyList = getTypeModify(line);
        if (typeModifyList == null || typeModifyList.isEmpty())
        {
            return;
        }
        List<String> modifyListTmp = singleModifyMapTmp.get(typeModifyTag);
        if (modifyListTmp == null)
        {
            modifyListTmp = new ArrayList<String>();
            singleModifyMapTmp.put(typeModifyTag, modifyListTmp);
        }
        modifyListTmp.addAll(typeModifyList);
    }
    
    private static String getTypeModifyTag(String line)
    {
        int startIndex = line.indexOf(TYPE_MODIFY_START_TAG);
        if (startIndex != 0)
        {
            return null;
        }
        int endIndex = line.indexOf(TYPE_MODIFY_END_TAG);
        if (endIndex < 0)
        {
            return null;
        }
        return line.substring(startIndex + 1, endIndex);
    }
    
    private static List<String> getTypeModify(String line)
    {
        int startIndex = line.indexOf(TYPE_MODIFY_END_TAG) + 1;
        String modifyStr = line.substring(startIndex);
        String[] modifis = modifyStr.split(TYPE_MODIFY_SPLIT_TAG);
        List<String> ret = new ArrayList<String>();
        for(String modify : modifis)
        {
            if(!StringUtils.isEmpty(modify))
            {
                ret.add(modify);
            }
        }
        return ret;
    }
    
    private static String generateTemplateTagKey(List<String> templateTagList)
    {
        String key = "";
        Collections.sort(templateTagList);
        for (String templateTag : templateTagList)
        {
            key += templateTag;
        }
        return key;
    }
    
    public static String getConfig(String tag, List<String> templateTags)
    {
        switch(tag)
        {
        case WITH_NAME_TAG:
        case SINGLE_ELEMENT_TAG:
        case TWICE_ELEMENT_TAG:
        case TRIPLE_AND_ABOVE_ELEMENT_TAG:
        case SELECT_TAG:
        case U_DISK_TAG:
        case TV_SET_TAG:
            return getTemplates(tag, templateTags);
        case BEFORE_TAG:
            return getBefore();
        case AFTER_TAG:
            return getAfter();
        case TYPE_MODIFY_TAG:
            return getModify(tag, templateTags);
        default:
            logger.error("unsupport tag " + tag);
            return null;
        }
    }
    
    private static String getTemplates(String tag, List<String> templateTags)
    {
        if (StringUtils.isEmpty(tag) || templateTags == null || templateTags.isEmpty())
        {
            return null;
        }
        Map<String, List<String>> singleTemplateMap = templateMap.get(tag);
        if (singleTemplateMap == null)
        {
            return null;
        }
        String key = generateTemplateTagKey(templateTags);
        List<String> templateList = singleTemplateMap.get(key);
        if (templateList == null || templateList.isEmpty())
        {
            return null;
        }
        int randomIndex = random.nextInt(templateList.size());
        return templateList.get(randomIndex);
    }
    
    private static String getBefore()
    {
        int randomIndex = random.nextInt(beforeList.size());
        return beforeList.get(randomIndex);
    }
    
    private static String getAfter()
    {
        int randomIndex = random.nextInt(afterList.size());
        return afterList.get(randomIndex);
    }
    
    /**
     * 这里只是传入一个type名称，但是为了统一格式，所以传入一个list
     * @param typeList
     * @return
     */
    public static String getModify(String tag, List<String> typeList)
    {
        if(StringUtils.isEmpty(tag) || typeList == null || typeList.isEmpty())
        {
            return null;
        }
        Map<String, List<String>> singleModifyMap = modifyMap.get(tag);
        if (singleModifyMap == null)
        {
            return null;
        }
        String type = typeList.get(0);
        List<String> modifyList = singleModifyMap.get(type);
        if (modifyList == null || modifyList.isEmpty())
        {
            return null;
        }
        int randomIndex = random.nextInt(modifyList.size());
        return modifyList.get(randomIndex);
    }
}
