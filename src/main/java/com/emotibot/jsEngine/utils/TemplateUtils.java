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

/** 
 * semantic tag 转化为 template tag
 * 
 * 1. 将semantic tag小写，
 * 2. 在semantic tag两侧加入<:和:>
 * 
 * @author emotibot
 *
 */
public class TemplateUtils
{
    private static Logger logger = Logger.getLogger(TemplateUtils.class);
    private static ReentrantLock lock = new ReentrantLock();
    /**
     * 模板类型->模板的组成->模板的list
     * 模板类型是：求视频模板，U盘资源模板，点播模板，TVSET模板
     */
    private static Map<String, Map<String, List<String>>> templateTagToTemplateElementsToTemplateListMap;
    private static List<String> commonTemplateList;
    /** 前缀例句*/
    private static List<String> beforeList;
    /** 结词例句*/
    private static List<String> afterList;
    /** 其他修饰语，不包含在knowledge中的，扩展了TVSET的template*/
    private static Map<String, Map<String, List<String>>> templateTagToSemanticValueToModifyListMap;
    
    //Template and Modify
    public static final String VIDEO_QUERY_TEMPLATE_TAG = "Case1:";
    public static final String VIDEO_QUERY_COMMON_TEMPLATE_TAG = "Case2:";
    public static final String U_DIST_TEMPLATE_TAG = "Case3:";
    public static final String SELECT_TEMPLATE_TAG = "Case4:";
    public static final String TV_SET_TEMPLATE_TAG = "Case5:";
    public static final String BEFORE_TAG = "Case6:";
    public static final String AFTER_TAG = "Case7:";
    
    public static final String TYPE_MODIFY_TAG = "Case8:";
    
    private static final String TEMPLATE_ELEMENT_START_TAG = "<$";
    private static final String TEMPLATE_ELEMENT_END_TAG = "$>";
    
    private static final String MODIFY_START_TAG = "<";
    private static final String MODIFY_END_TAG = ">";
    private static final String MODIFY_SPLIT_TAG = ",";
    
    //String.split()分割是正则表达式，所以为\\+
    private static final String COMMON_TEMPLATE_SPLIT_TAG = "\\+";
        
    private static final String[] allTemplateAndModifyTags = {VIDEO_QUERY_TEMPLATE_TAG, VIDEO_QUERY_COMMON_TEMPLATE_TAG, 
            U_DIST_TEMPLATE_TAG, SELECT_TEMPLATE_TAG, TV_SET_TEMPLATE_TAG, BEFORE_TAG, AFTER_TAG, TYPE_MODIFY_TAG};
    
    private static Set<String> allTemplateAndModifyTagSet;
    
    public static final String SEMANTIC_TYPE_TAG = "type";
    
    private static Map<String, String> templateElementTagToTemplateTagMap;
    
    private static Random random = new Random();
    
    static
    {
        allTemplateAndModifyTagSet = new HashSet<String>();
        for (String tag : allTemplateAndModifyTags)
        {
            allTemplateAndModifyTagSet.add(tag);
        }
        templateElementTagToTemplateTagMap = new HashMap<String, String>();
        templateElementTagToTemplateTagMap.put(convertSemanticTagToTemplateTag(SEMANTIC_TYPE_TAG), TYPE_MODIFY_TAG);
        loadConfigs();
    }
    
    public static void loadConfigs()
    {
        lock.lock();
        try
        {
            String templateFile = ConfigManager.INSTANCE.getPropertyString(Constants.TEMPLATE_FILE_KEY);
            Map<String, Map<String, List<String>>> templateTagToTemplateElementsToTemplateListMapTmp = 
                            new HashMap<String, Map<String, List<String>>>();
            List<String> commonTemplateListTmp = new ArrayList<String>();
            List<String> beforeListTmp = new ArrayList<String>();
            List<String> afterListTmp = new ArrayList<String>();
            Map<String, Map<String, List<String>>> templateTagToSemanticValueToModifyListMapTmp = 
                            new HashMap<String, Map<String, List<String>>>();
            String currentTag = null;
            List<String> lines = FileUtils.readFile(templateFile);
            for (String line : lines)
            {
                line = line.trim();
                if (StringUtils.isEmpty(line) || line.startsWith("#"))
                {
                    continue;
                }
                if (allTemplateAndModifyTagSet.contains(line))
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
                case VIDEO_QUERY_TEMPLATE_TAG:
                case U_DIST_TEMPLATE_TAG:
                case SELECT_TEMPLATE_TAG:
                    loadTemplates(currentTag, line, templateTagToTemplateElementsToTemplateListMapTmp);
                    break;
                case VIDEO_QUERY_COMMON_TEMPLATE_TAG:
                    loadTemplates1(line, commonTemplateListTmp);
                    break;
                case BEFORE_TAG:
                    loadTemplates1(line, beforeListTmp);
                    break;
                case AFTER_TAG:
                    loadTemplates1(line, afterListTmp);
                    break;
                case TYPE_MODIFY_TAG:
                case TV_SET_TEMPLATE_TAG:
                    loadModify(currentTag, line, templateTagToSemanticValueToModifyListMapTmp);
                    break;
                default:
                    logger.error("unsupport tag " + currentTag);
                    break;
                }
            }
            templateTagToTemplateElementsToTemplateListMap = templateTagToTemplateElementsToTemplateListMapTmp;
            commonTemplateList = commonTemplateListTmp;
            beforeList = beforeListTmp;
            afterList = afterListTmp;
            templateTagToSemanticValueToModifyListMap = templateTagToSemanticValueToModifyListMapTmp;
        }
        finally
        {
            lock.unlock();
        }
    }
    
    private static void loadTemplates(String templateTag, String line, 
            Map<String, Map<String, List<String>>> templateTagToTemplateElementsToTemplateListMapTmp)
    {
        List<String> templateElementTagList = getTemplateTagFromInput(line);
        if (templateElementTagList == null || templateElementTagList.isEmpty())
        {
            return;
        }
        line = adjustTemplateLine(line, templateElementTagList);
        Map<String, List<String>> templateElementsToTemplateListMapTmp = templateTagToTemplateElementsToTemplateListMapTmp.get(templateTag);
        if (templateElementsToTemplateListMapTmp == null)
        {
            templateElementsToTemplateListMapTmp = new HashMap<String, List<String>>();
            templateTagToTemplateElementsToTemplateListMapTmp.put(templateTag, templateElementsToTemplateListMapTmp);
        }
        String key = generateTemplateTagKey(templateElementTagList);
        List<String> tempalteList = templateElementsToTemplateListMapTmp.get(key);
        if (tempalteList == null)
        {
            tempalteList = new ArrayList<String>();
            templateElementsToTemplateListMapTmp.put(key, tempalteList);
        }
        tempalteList.add(line);
    }
    
    private static List<String> getTemplateTagFromInput(String line)
    {
        if (StringUtils.isEmpty(line))
        {
            return null;
        }
        List<String> ret = new ArrayList<String>();
        int startIndex = line.indexOf(TEMPLATE_ELEMENT_START_TAG);
        int cursor = 0;
        while(startIndex >= 0)
        {
            int endIndex = line.indexOf(TEMPLATE_ELEMENT_END_TAG, startIndex);
            if (endIndex < 0)
            {
                return null;
            }
            String tempalteTag = line.substring(startIndex, endIndex + TEMPLATE_ELEMENT_END_TAG.length());
            ret.add(tempalteTag.toLowerCase());
            cursor = endIndex + TEMPLATE_ELEMENT_END_TAG.length();
            if (cursor >= line.length())
            {
                break;
            }
            startIndex = line.indexOf(TEMPLATE_ELEMENT_START_TAG, cursor);
        }
        return ret;
    }
    
    /**
     * 将template中的templateElementTag改写成小写
     * 
     * @param line
     * @param templateElementTagList
     * @return
     */
    private static String adjustTemplateLine(String line, List<String> templateElementTagList)
    {
        String ret = line.toLowerCase();
        int start = 0;
        for (String templateElementTag : templateElementTagList)
        {
            int index = ret.indexOf(templateElementTag, start);
            if (index < 0)
            {
                break;
            }
            line = line.substring(0, index) + templateElementTag + line.substring(index + templateElementTag.length());
            start = index + templateElementTag.length();
            if (start >= line.length())
            {
                break;
            }
        }
        return line;
    }
    
    private static void loadTemplates1(String line, List<String> arrrayList)
    {
        List<String> templateElementTagList = getTemplateTagFromInput(line);
        if (templateElementTagList != null)
        {
            line = adjustTemplateLine(line, templateElementTagList);
        }
        arrrayList.add(line);
    }
    
    private static void loadModify(String templateTag, String line, 
            Map<String, Map<String, List<String>>> templateTagToSemanticValueToModifyListMapTmp)
    {
        Map<String, List<String>> semanticValueToModifyListMapTmp = 
                templateTagToSemanticValueToModifyListMapTmp.get(templateTag);
        if (semanticValueToModifyListMapTmp == null)
        {
            semanticValueToModifyListMapTmp = new HashMap<String, List<String>>();
            templateTagToSemanticValueToModifyListMapTmp.put(templateTag, semanticValueToModifyListMapTmp);
        }
        String modifyTag = getModifyTag(line);
        if (StringUtils.isEmpty(modifyTag))
        {
            return;
        }
        modifyTag = modifyTag.toLowerCase();
        List<String> modifyList = getModify(line);
        if (modifyList == null || modifyList.isEmpty())
        {
            return;
        }
        List<String> modifyListTmp = semanticValueToModifyListMapTmp.get(modifyTag);
        if (modifyListTmp == null)
        {
            modifyListTmp = new ArrayList<String>();
            semanticValueToModifyListMapTmp.put(modifyTag, modifyListTmp);
        }
        modifyListTmp.addAll(modifyList);
    }
    
    /**
     * 截取修饰词头部信息
     * 
     * 例如: <爱情>动人的,感人的
     * 
     * 提取: 爱情
     * 
     * @param line
     * @return
     */
    private static String getModifyTag(String line)
    {
        int startIndex = line.indexOf(MODIFY_START_TAG);
        if (startIndex != 0)
        {
            return null;
        }
        int endIndex = line.indexOf(MODIFY_END_TAG);
        if (endIndex < 0)
        {
            return null;
        }
        return line.substring(startIndex + 1, endIndex);
    }
    
    /**
     * 截取修饰词内容，并按照","来分割
     * 
     * 例如: <爱情>动人的,感人的
     * 
     * 提取: [动人的, 感人的]
     * 
     * @param line
     * @return
     */
    private static List<String> getModify(String line)
    {
        int startIndex = line.indexOf(MODIFY_END_TAG) + 1;
        String modifyStr = line.substring(startIndex);
        String[] modifis = modifyStr.split(MODIFY_SPLIT_TAG);
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
   
    /**
     * 获取模板或者修饰词
     * 
     * @param templateTag
     * @param templateElementTags
     * @return
     */
    public static String getConfig(String templateTag, List<String> templateElementTags)
    {
        switch(templateTag)
        {
        case VIDEO_QUERY_TEMPLATE_TAG:
        case U_DIST_TEMPLATE_TAG:
        case SELECT_TEMPLATE_TAG:
            return getTemplates(templateTag, templateElementTags);
        case VIDEO_QUERY_COMMON_TEMPLATE_TAG:
            return getCommonTemplate(templateElementTags);
        case BEFORE_TAG:
            return getBefore();
        case AFTER_TAG:
            return getAfter();
        case TYPE_MODIFY_TAG:
        case TV_SET_TEMPLATE_TAG:
            return getModify(templateTag, templateElementTags);
        default:
            logger.error("unsupport tag " + templateTag);
            return null;
        }
    }
    
    private static String getTemplates(String tag, List<String> templateTags)
    {
        if (StringUtils.isEmpty(tag) || templateTags == null || templateTags.isEmpty())
        {
            return null;
        }
        Map<String, List<String>> templateElementsToTemplateListMap = templateTagToTemplateElementsToTemplateListMap.get(tag);
        if (templateElementsToTemplateListMap == null)
        {
            return null;
        }
        String key = generateTemplateTagKey(templateTags);
        List<String> templateList = templateElementsToTemplateListMap.get(key);
        if (templateList == null || templateList.isEmpty())
        {
            return null;
        }
        int randomIndex = random.nextInt(templateList.size());
        return templateList.get(randomIndex);
    }
    
    /**
     * 按照"+"分段，之后看每段中是否包含templateElementTags中的元素，如果包含，就将该段提取
     */
    private static String getCommonTemplate(List<String> templateElementTags)
    {
        List<String> chooseTemplateList = new ArrayList<String>();
        for (String template : commonTemplateList)
        {
            boolean tag = true;
            for (String templateElement : templateElementTags)
            {
                if (!template.contains(templateElement))
                {
                    tag = false;
                    break;
                }
            }
            if (tag)
            {
                chooseTemplateList.add(template);
            }
        }
        if (chooseTemplateList.size() == 0)
        {
            return null;
        }
        int randomIndex = random.nextInt(chooseTemplateList.size());
        String chooseTemplate = chooseTemplateList.get(randomIndex);
        String template = "";
        String[] templateSections = chooseTemplate.split(COMMON_TEMPLATE_SPLIT_TAG);
        for (String templateSection : templateSections)
        {
            for (String templateElement : templateElementTags)
            {
                if (templateSection.contains(templateElement))
                {
                    template += templateSection;
                    break;
                }
            }
        }
        return template;
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
    
    private static String getModify(String modifyTag, List<String> semanticValueList)
    {
        if (StringUtils.isEmpty(modifyTag) || semanticValueList == null || semanticValueList.isEmpty())
        {
            return null;
        }
        Map<String, List<String>> semanticValueToModifyListMap = templateTagToSemanticValueToModifyListMap.get(modifyTag);
        if (semanticValueToModifyListMap == null)
        {
            return null;
        }
        String semanticValue = semanticValueList.get(0);
        List<String> modifyList = semanticValueToModifyListMap.get(semanticValue);
        if (modifyList == null || modifyList.isEmpty())
        {
            return null;
        }
        int randomIndex = random.nextInt(modifyList.size());
        return modifyList.get(randomIndex);
    }
    
    public static String convertSemanticTagToTemplateTag(String semanticTag)
    {
        return TEMPLATE_ELEMENT_START_TAG + semanticTag.toLowerCase() + TEMPLATE_ELEMENT_END_TAG;
    }
    
    public static String getModifyWithTemplateElementTag(String templateElementTag)
    {
        return templateElementTagToTemplateTagMap.get(templateElementTag);
    }
}
