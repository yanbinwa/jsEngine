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
import com.emotibot.middleware.utils.JsonUtils;
import com.emotibot.middleware.utils.StringUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class TemplateNewUtils
{
    private static Logger logger = Logger.getLogger(TemplateUtils.class);
    private static ReentrantLock lock = new ReentrantLock();
    
    public static final String TEMPLATE_TYPE = "Type";
    public static final String TEMPLATE_FORMAT_TYPE = "FormatType";
    public static final String TEMPLATE_DATA = "Data";
    
    public static final String FORMAT_TYPE_0 = "0";
    public static final String FORMAT_TYPE_1 = "1";
    public static final String FORMAT_TYPE_2 = "2";
    public static final String FORMAT_TYPE_3 = "3";
    public static final String FORMAT_TYPE_4 = "4";
    
    /**
     * 数据格式0: 前导词和结词...
     */
    private static Map<String, List<String>> format0Map;
    /**
     * 数据格式1: 带有semantic element的模板句...
     */
    private static Map<String, Map<String, List<String>>> format1Map;
    /**
     * 数据格式2: 通用模板句...
     */
    private static Map<String, List<String>> format2Map;
    /**
     * 数据格式3: TVSet模板句...
     */
    private static Map<String, Map<String, List<String>>> format3Map;
    /**
     * 数据格式4: Type修饰词模板句...
     */
    private static Map<String, Map<String, List<String>>> format4Map;
    
    private static Map<String, String> templateTagToFormatMap;
    
    private static final String TEMPLATE_ELEMENT_START_TAG = "<$";
    private static final String TEMPLATE_ELEMENT_END_TAG = "$>";
    
    private static final String COMMON_START_TAG = "<";
    private static final String COMMON_END_TAG = ">";
    private static final String COMMON_SPLIT_TAG = ",";
    
    //String.split()分割是正则表达式，所以为\\+
    private static final String COMMON_TEMPLATE_SPLIT_TAG = "\\+";
    
    private static String[] supportFormatTypes = {FORMAT_TYPE_0, FORMAT_TYPE_1, FORMAT_TYPE_2, FORMAT_TYPE_3, FORMAT_TYPE_4};
    private static Set<String> supportFormatTypeSet;
    
    private static Random random = new Random();
    
    static
    {
        supportFormatTypeSet = new HashSet<String>();
        for (String formatType : supportFormatTypes)
        {
            supportFormatTypeSet.add(formatType);
        }
        loadConfigs();
    }
    
    public static void loadConfigs()
    {
        lock.lock();
        String templateFile = ConfigManager.INSTANCE.getPropertyString(Constants.TEMPLATE_FILE_KEY);
        try
        {
            String jsonString = FileUtils.readFileToString(templateFile);
            JsonArray jsonArray = (JsonArray) JsonUtils.getObject(jsonString, JsonArray.class);
            Map<String, List<String>> format0MapTmp = new HashMap<String, List<String>>();
            Map<String, Map<String, List<String>>> format1MapTmp = new HashMap<String, Map<String, List<String>>>();
            Map<String, List<String>> format2MapTmp = new HashMap<String, List<String>>();
            Map<String, Map<String, List<String>>> format3MapTmp = new HashMap<String, Map<String, List<String>>>();
            Map<String, Map<String, List<String>>> format4MapTmp = new HashMap<String, Map<String, List<String>>>();
            Map<String, String> templateTagToFormatMapTmp = new HashMap<String, String>();
            for (int i = 0; i < jsonArray.size(); i ++)
            {
                JsonObject templateObj = jsonArray.get(i).getAsJsonObject();
                if (!templateObj.has(TEMPLATE_TYPE) || !templateObj.has(TEMPLATE_FORMAT_TYPE) || 
                        !templateObj.has(TEMPLATE_DATA))
                {
                    logger.error("Invalid template obj: " + templateObj.toString());
                    continue;
                }
                String tempalteType = templateObj.get(TEMPLATE_TYPE).getAsString();
                String formatType = templateObj.get(TEMPLATE_FORMAT_TYPE).getAsString();
                if (!supportFormatTypeSet.contains(formatType))
                {
                    logger.error("Invalid format type: " + tempalteType + "; templateObj is: " + templateObj.toString());
                    continue;
                }
                switch(formatType)
                {
                case FORMAT_TYPE_0:
                    parserFormatType0(tempalteType, templateObj, format0MapTmp);
                    break;
                case FORMAT_TYPE_1:
                    parserFormatType1(tempalteType, templateObj, format1MapTmp);
                    break;
                case FORMAT_TYPE_2:
                    parserFormatType2(tempalteType, templateObj, format2MapTmp);
                    break;
                case FORMAT_TYPE_3:
                    parserFormatType3(tempalteType, templateObj, format3MapTmp);
                    break;
                case FORMAT_TYPE_4:
                    parserFormatType4(tempalteType, templateObj, format4MapTmp);
                    break;
                }
                templateTagToFormatMapTmp.put(tempalteType, formatType);
            }
            format0Map = format0MapTmp;
            format1Map = format1MapTmp;
            format2Map = format2MapTmp;
            format3Map = format3MapTmp;
            format4Map = format4MapTmp;
            templateTagToFormatMap = templateTagToFormatMapTmp;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.error("load config file failed: " + templateFile);
        }
        finally
        {
            lock.unlock();
        }
    }
    
    private static void parserFormatType0(String templateTag, JsonObject templateObj, Map<String, List<String>> format0MapTmp)
    {
        List<String> addTemplateList = new ArrayList<String>();
        JsonArray dataObj = templateObj.get(TEMPLATE_DATA).getAsJsonArray();
        for (int i = 0; i < dataObj.size(); i ++)
        {
            String template = dataObj.get(i).getAsString();
            if (!StringUtils.isEmpty(template))
            {
                addTemplateList.add(template);
            }
        }
        if (addTemplateList.isEmpty())
        {
            return;
        }
        List<String> templateList = format0MapTmp.get(templateTag);
        if (templateList == null)
        {
            templateList = new ArrayList<String>();
            format0MapTmp.put(templateTag, templateList);
        }
        templateList.addAll(addTemplateList);
    }
    
    private static void parserFormatType1(String templateTag, JsonObject templateObj, Map<String, Map<String, List<String>>> format1MapTmp)
    {
        JsonArray dataObj = templateObj.get(TEMPLATE_DATA).getAsJsonArray();
        for (int i = 0; i < dataObj.size(); i ++)
        {
            String template = dataObj.get(i).getAsString();
            if (StringUtils.isEmpty(template))
            {
                continue;
            }
            List<String> templateElementTagList = getTemplateTagFromInput(template);
            if (templateElementTagList == null || templateElementTagList.isEmpty())
            {
                continue;
            }
            template = adjustTemplateLine(template, templateElementTagList);
            Map<String, List<String>> templateElementsToTemplateListMapTmp = format1MapTmp.get(templateTag);
            if (templateElementsToTemplateListMapTmp == null)
            {
                templateElementsToTemplateListMapTmp = new HashMap<String, List<String>>();
                format1MapTmp.put(templateTag, templateElementsToTemplateListMapTmp);
            }
            String key = generateTemplateTagKey(templateElementTagList);
            List<String> tempalteList = templateElementsToTemplateListMapTmp.get(key);
            if (tempalteList == null)
            {
                tempalteList = new ArrayList<String>();
                templateElementsToTemplateListMapTmp.put(key, tempalteList);
            }
            tempalteList.add(template);
        }
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
    
    private static String generateTemplateTagKey(List<String> templateTagList)
    {
        templateTagList = new ArrayList<String>(new HashSet<String>(templateTagList));
        String key = "";
        Collections.sort(templateTagList);
        for (String templateTag : templateTagList)
        {
            key += templateTag;
        }
        return key;
    }
    
    private static void parserFormatType2(String templateTag, JsonObject templateObj, Map<String, List<String>> format2MapTmp)
    {
        parserFormatType0(templateTag, templateObj, format2MapTmp);
    }
    
    private static void parserFormatType3(String templateTag, JsonObject templateObj, Map<String, Map<String, List<String>>> format3MapTmp)
    {
        JsonArray dataObj = templateObj.get(TEMPLATE_DATA).getAsJsonArray();
        for (int i = 0; i < dataObj.size(); i ++)
        {
            String template = dataObj.get(i).getAsString();
            if (StringUtils.isEmpty(template))
            {
                continue;
            }
            Map<String, List<String>> commonElementTagToCommonElementListMapTmp = format3MapTmp.get(templateTag);
            if (commonElementTagToCommonElementListMapTmp == null)
            {
                commonElementTagToCommonElementListMapTmp = new HashMap<String, List<String>>();
                format3MapTmp.put(templateTag, commonElementTagToCommonElementListMapTmp);
            }
            String commonElementTag = getCommonElementTag(template);
            if (StringUtils.isEmpty(commonElementTag))
            {
                continue;
            }
            template = template.replace(COMMON_START_TAG + commonElementTag + COMMON_END_TAG, "");
            commonElementTag = commonElementTag.toLowerCase();
            List<String> commonElementList = commonElementTagToCommonElementListMapTmp.get(commonElementTag);
            if (commonElementList == null)
            {
                commonElementList = new ArrayList<String>();
                commonElementTagToCommonElementListMapTmp.put(commonElementTag, commonElementList);
            }
            commonElementList.add(template);
        }
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
    private static String getCommonElementTag(String line)
    {
        int startIndex = line.indexOf(COMMON_START_TAG);
        if (startIndex != 0)
        {
            return null;
        }
        int endIndex = line.indexOf(COMMON_END_TAG);
        if (endIndex < 0)
        {
            return null;
        }
        return line.substring(startIndex + 1, endIndex);
    }
    
    private static void parserFormatType4(String templateTag, JsonObject templateObj, Map<String, Map<String, List<String>>> format4MapTmp)
    {
        JsonArray dataObj = templateObj.get(TEMPLATE_DATA).getAsJsonArray();
        for (int i = 0; i < dataObj.size(); i ++)
        {
            String line = dataObj.get(i).getAsString();
            if (StringUtils.isEmpty(line))
            {
                continue;
            }
            Map<String, List<String>> commonElementTagToCommonElementListMapTmp = format4MapTmp.get(templateTag);
            if (commonElementTagToCommonElementListMapTmp == null)
            {
                commonElementTagToCommonElementListMapTmp = new HashMap<String, List<String>>();
                format4MapTmp.put(templateTag, commonElementTagToCommonElementListMapTmp);
            }
            String commonElementTag = getCommonElementTag(line);
            if (StringUtils.isEmpty(commonElementTag))
            {
                continue;
            }
            List<String> templates = getCommonElementList(line);
            commonElementTag = commonElementTag.toLowerCase();
            List<String> commonElementList = commonElementTagToCommonElementListMapTmp.get(commonElementTag);
            if (commonElementList == null)
            {
                commonElementList = new ArrayList<String>();
                commonElementTagToCommonElementListMapTmp.put(commonElementTag, commonElementList);
            }
            commonElementList.addAll(templates);
        }
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
    private static List<String> getCommonElementList(String line)
    {
        int startIndex = line.indexOf(COMMON_END_TAG) + 1;
        String modifyStr = line.substring(startIndex);
        String[] modifis = modifyStr.split(COMMON_SPLIT_TAG);
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
    
    /**
     * 获取模板内容
     * 
     * @param templateTag
     * @param templateElementTags
     * @return
     */
    public static String getConfig(String templateTag, List<String> templateElementTags)
    {
        String formatType = templateTagToFormatMap.get(templateTag);
        if (StringUtils.isEmpty(formatType))
        {
            logger.error("unsupport format type. template tag is: " +  templateTag);
            return null;
        }
        switch(formatType)
        {
        case FORMAT_TYPE_0:
            return getConfigForFormatType0(templateTag, templateElementTags, format0Map);
        case FORMAT_TYPE_1:
            return getConfigForFormatType1(templateTag, templateElementTags, format1Map);
        case FORMAT_TYPE_2:
            return getConfigForFormatType2(templateTag, templateElementTags, format2Map);
        case FORMAT_TYPE_3:
            return getConfigForFormatType3(templateTag, templateElementTags, format3Map);
        case FORMAT_TYPE_4:
            return getConfigForFormatType4(templateTag, templateElementTags, format4Map);
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    private static String getConfigForFormatType0(String templateTag, List<String> templateElementTags, Object map)
    {
        Map<String, List<String>> formatMap = (Map<String, List<String>>) map;
        List<String> templateList = formatMap.get(templateTag);
        if (templateList == null || templateList.isEmpty())
        {
            return null;
        }
        int randomIndex = random.nextInt(templateList.size());
        return templateList.get(randomIndex);
    }
    
    @SuppressWarnings("unchecked")
    private static String getConfigForFormatType1(String templateTag, List<String> templateElementTags, Object map)
    {
        Map<String, Map<String, List<String>>> formatMap = (Map<String, Map<String, List<String>>>) map;
        if (templateElementTags == null || templateElementTags.isEmpty())
        {
            logger.error("templateElementTags should not be empty");
            return null;
        }
        List<String> validTemplateElementTagList = new ArrayList<String>();
        for (String templateElementTag : templateElementTags)
        {
            if (isTemplateElementTag(templateElementTag))
            {
                validTemplateElementTagList.add(templateElementTag);
            }
        }
        if (validTemplateElementTagList.isEmpty())
        {
            logger.error("validTemplateElementTagList should not be empty");
            return null;
        }
        Map<String, List<String>> templateElementsToTemplateListMap = formatMap.get(templateTag);
        if (templateElementsToTemplateListMap == null)
        {
            return null;
        }
        String key = generateTemplateTagKey(validTemplateElementTagList);
        List<String> templateList = templateElementsToTemplateListMap.get(key);
        if (templateList == null || templateList.isEmpty())
        {
            return null;
        }
        int randomIndex = random.nextInt(templateList.size());
        return templateList.get(randomIndex);
    }
    
    @SuppressWarnings("unchecked")
    private static String getConfigForFormatType2(String templateTag, List<String> templateElementTags, Object map)
    {
        Map<String, List<String>> formatMap = (Map<String, List<String>>) map;
        if (templateElementTags == null || templateElementTags.isEmpty())
        {
            logger.error("templateElementTags should not be empty");
            return null;
        }
        List<String> validTemplateElementTagList = new ArrayList<String>();
        for (String templateElementTag : templateElementTags)
        {
            if (isTemplateElementTag(templateElementTag))
            {
                validTemplateElementTagList.add(templateElementTag);
            }
        }
        if (validTemplateElementTagList.isEmpty())
        {
            logger.error("validTemplateElementTagList should not be empty");
            return null;
        }
        List<String> commonTemplateList = formatMap.get(templateTag);
        if (commonTemplateList == null || commonTemplateList.isEmpty())
        {
            return null;
        }
        List<String> chooseCommonTemplateList = new ArrayList<String>();
        for (String template : commonTemplateList)
        {
            boolean tag = true;
            for (String templateElementTag : validTemplateElementTagList)
            {
                if (!template.contains(templateElementTag))
                {
                    tag = false;
                    break;
                }
            }
            if (tag)
            {
                chooseCommonTemplateList.add(template);
            }
        }
        if (chooseCommonTemplateList.size() == 0)
        {
            return null;
        }
        int randomIndex = random.nextInt(chooseCommonTemplateList.size());
        String chooseTemplate = chooseCommonTemplateList.get(randomIndex);
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
    
    @SuppressWarnings("unchecked")
    private static String getConfigForFormatType3(String templateTag, List<String> templateElementTags, Object map)
    {
        Map<String, Map<String, List<String>>> formatMap = (Map<String, Map<String, List<String>>>) map;
        if (templateElementTags == null || templateElementTags.isEmpty())
        {
            logger.error("templateElementTags should not be empty");
            return null;
        }
        List<String> validCommonElementTagList = new ArrayList<String>();
        for (String templateElementTag : templateElementTags)
        {
            if (!isTemplateElementTag(templateElementTag))
            {
                validCommonElementTagList.add(templateElementTag);
            }
        }
        Map<String, List<String>> commonElementTagToCommonElementList = formatMap.get(templateTag);
        if (commonElementTagToCommonElementList == null)
        {
            return null;
        }
        String commonElementTag = validCommonElementTagList.get(0);
        List<String> commonElementList = commonElementTagToCommonElementList.get(commonElementTag);
        if (commonElementList == null || commonElementList.isEmpty())
        {
            return null;
        }
        int randomIndex = random.nextInt(commonElementList.size());
        return commonElementList.get(randomIndex);
    }
    
    private static String getConfigForFormatType4(String templateTag, List<String> templateElementTags, Object map)
    {
        return getConfigForFormatType3(templateTag, templateElementTags, map);
    }
    
    public static boolean isTemplateElementTag(String tag)
    {
        if (tag.startsWith(TEMPLATE_ELEMENT_START_TAG) && tag.endsWith(TEMPLATE_ELEMENT_END_TAG))
        {
            return true;
        }
        return false;
    }
    
    public static String convertSemanticTagToTemplateTag(String semanticTag)
    {
        return TEMPLATE_ELEMENT_START_TAG + semanticTag.toLowerCase() + TEMPLATE_ELEMENT_END_TAG;
    }
}
