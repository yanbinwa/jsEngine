package com.emotibot.jsEngine.utils;

import java.util.ArrayList;
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

/**
 * 加载模板
 * 加载同义词
 * 
 * @author emotibot
 *
 */
public class TemplateUtils
{
    private static Logger logger = Logger.getLogger(TemplateUtils.class);
    private static ReentrantLock lock = new ReentrantLock();
    private static Random random = new Random();
    
    /******************** template ********************/
    public static final String TEMPLATE_TYPE = "Type";
    public static final String TEMPLATE_FORMAT_TYPE = "FormatType";
    public static final String TEMPLATE_DATA = "Data";
    
    public static final String FORMAT_TYPE_0 = "0";
    public static final String FORMAT_TYPE_1 = "1";
    public static final String FORMAT_TYPE_2 = "2";
    
    /**
     * 数据格式0: 前导词和结词...
     */
    private static Map<String, List<String>> format0Map;
    /**
     * 数据格式2: 通用模板句...
     */
    private static Map<String, List<String>> format1Map;
    /**
     * 数据格式3: TVSet模板句,Type自定义后缀...
     */
    private static Map<String, Map<String, List<String>>> format2Map;
    
    private static Map<String, String> templateTagToFormatMap;
    
    private static final String COMMON_START_TAG = "<";
    private static final String COMMON_END_TAG = ">";
    
    private static final String TEMPLATE_ELEMENT_START_TAG = "[$";
    private static final String TEMPLATE_ELEMENT_END_TAG = "$]";
    
    private static final String TEMPLATE_START_TAG = "<$";
    private static final String TEMPLATE_END_TAG = "$>";
    
    //String.split()分割是正则表达式，所以为\\+
    private static final String COMMON_TEMPLATE_SPLIT_TAG = "\\+";
    
    private static String[] supportFormatTypes = {FORMAT_TYPE_0, FORMAT_TYPE_1, FORMAT_TYPE_2};
    private static Set<String> supportFormatTypeSet;
    
    /******************** synonym ********************/
    private static final String SYNONYM_TEMPLATE_ELEMENT_TAG = "tag";
    private static final String SYNONYM_DATA = "data";
    private static final String SYNONYM_VALUE_TAG = "value";
    private static final String SYNONYM_SYNONYM_TAG = "synonym";
    
    private static final String SYNONYM_SPLIT_TAG = ",";
    
    private static Map<String, Map<String, List<String>>> synonymMap;
    
    static
    {
        supportFormatTypeSet = new HashSet<String>();
        for (String formatType : supportFormatTypes)
        {
            supportFormatTypeSet.add(formatType);
        }
        String templateFile = ConfigManager.INSTANCE.getPropertyString(Constants.TEMPLATE_FILE_KEY);
        loadConfigsFromFile(templateFile);
        String synonymFile = ConfigManager.INSTANCE.getPropertyString(Constants.SYNONYM_FILE_KEY);
        loadSynonymFromFile(synonymFile);
    }
    
    public static void loadConfigsFromFile(String filePath)
    {
        String jsonString = FileUtils.readFileToString(filePath);
        loadConfigs(jsonString);
    }
    
    public static void loadConfigs(String jsonString)
    {
        if (StringUtils.isEmpty(jsonString)) 
        {
            return;
        }
        lock.lock();
        try
        {
            JsonArray jsonArray = (JsonArray) JsonUtils.getObject(jsonString, JsonArray.class);
            Map<String, List<String>> format0MapTmp = new HashMap<String, List<String>>();
            Map<String, List<String>> format1MapTmp = new HashMap<String, List<String>>();
            Map<String, Map<String, List<String>>> format2MapTmp = new HashMap<String, Map<String, List<String>>>();
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
                tempalteType = adjustTemplate(tempalteType);
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
                }
                templateTagToFormatMapTmp.put(tempalteType, formatType);
            }
            format0Map = format0MapTmp;
            format1Map = format1MapTmp;
            format2Map = format2MapTmp;
            templateTagToFormatMap = templateTagToFormatMapTmp;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.error("load config str failed: " + jsonString);
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
                template = adjustTemplate(template);
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
    
    private static void parserFormatType1(String templateTag, JsonObject templateObj, Map<String, List<String>> format1MapTmp)
    {
        parserFormatType0(templateTag, templateObj, format1MapTmp);
    }
    
    private static void parserFormatType2(String templateTag, JsonObject templateObj, Map<String, Map<String, List<String>>> format2MapTmp)
    {
        JsonArray dataObj = templateObj.get(TEMPLATE_DATA).getAsJsonArray();
        for (int i = 0; i < dataObj.size(); i ++)
        {
            String template = dataObj.get(i).getAsString();
            if (StringUtils.isEmpty(template))
            {
                continue;
            }
            Map<String, List<String>> commonElementTagToCommonElementListMapTmp = format2MapTmp.get(templateTag);
            if (commonElementTagToCommonElementListMapTmp == null)
            {
                commonElementTagToCommonElementListMapTmp = new HashMap<String, List<String>>();
                format2MapTmp.put(templateTag, commonElementTagToCommonElementListMapTmp);
            }
            String commonElementTag = getCommonElementTag(template);
            if (StringUtils.isEmpty(commonElementTag))
            {
                continue;
            }
            template = template.replace(COMMON_START_TAG + commonElementTag + COMMON_END_TAG, "");
            template = adjustTemplate(template);
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
    
    private static String adjustTemplate(String line)
    {
        if (StringUtils.isEmpty(line))
        {
            return line;
        }
        List<String> templateElementTags = getTemplateElementTagsFromInput(line, false);
        if (templateElementTags != null)
        {
            for (String templateElementTag : templateElementTags)
            {
                String templateElementTagWithStartAndEnd = buildTemplateElementTagWithBeginAndAfter(templateElementTag);
                line = line.replace(templateElementTagWithStartAndEnd, templateElementTagWithStartAndEnd.toLowerCase());
            }
        }
        List<String> templateTags = getTemplateTagsFromInput(line, false);
        if (templateTags != null)
        {
            for (String templateTag : templateTags)
            {
                line = line.replace(templateTag, templateTag.toLowerCase());
            }
        }
        return line;
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
    
    public static void loadSynonymFromFile(String filePath)
    {
        String jsonString = FileUtils.readFileToString(filePath);
        loadSynonym(jsonString);
    }
    
    public static void loadSynonym(String jsonString)
    {
        if (StringUtils.isEmpty(jsonString))
        {
            return;
        }
        lock.lock();
        try
        {   
            JsonArray jsonArray = (JsonArray) JsonUtils.getObject(jsonString, JsonArray.class);
            Map<String, Map<String, List<String>>> synonymMapTmp = new HashMap<String, Map<String, List<String>>>();
            for (int i = 0; i < jsonArray.size(); i ++)
            {
                JsonObject templateObj = jsonArray.get(i).getAsJsonObject();
                if (!templateObj.has(SYNONYM_TEMPLATE_ELEMENT_TAG) || !templateObj.has(SYNONYM_DATA))
                {
                    logger.error("Invalid synonym obj: " + templateObj.toString());
                    continue;
                }
                String templateElementTag = templateObj.get(SYNONYM_TEMPLATE_ELEMENT_TAG).getAsString().toLowerCase();
                Map<String, List<String>> templateElementValueToSynonymListMap = synonymMapTmp.get(templateElementTag);
                if (templateElementValueToSynonymListMap == null)
                {
                    templateElementValueToSynonymListMap = new HashMap<String, List<String>>();
                    synonymMapTmp.put(templateElementTag, templateElementValueToSynonymListMap);
                }
                JsonArray dataArray = templateObj.get(SYNONYM_DATA).getAsJsonArray();
                for (int j = 0; j < dataArray.size(); j ++)
                {
                    JsonObject synonymObj = dataArray.get(j).getAsJsonObject();
                    if (!synonymObj.has(SYNONYM_VALUE_TAG) || !synonymObj.has(SYNONYM_SYNONYM_TAG))
                    {
                        logger.error("Invalid synonym element obj: " + synonymObj.toString());
                        continue;
                    }
                    String value = synonymObj.get(SYNONYM_VALUE_TAG).getAsString();
                    if (StringUtils.isEmpty(value))
                    {
                        continue;
                    }
                    List<String> synonymList = templateElementValueToSynonymListMap.get(value);
                    if (synonymList == null)
                    {
                        synonymList = new ArrayList<String>();
                        templateElementValueToSynonymListMap.put(value, synonymList);
                    }
                    String synonymStr = synonymObj.get(SYNONYM_SYNONYM_TAG).getAsString();
                    String[] synonyms = synonymStr.split(SYNONYM_SPLIT_TAG);
                    for (String synonym : synonyms)
                    {
                        if (StringUtils.isEmpty(synonym))
                        {
                            continue;
                        }
                        synonymList.add(synonym);
                    }
                }
            }
            synonymMap = synonymMapTmp;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.error("load config file failed: " + jsonString);
        }
        finally
        {
            lock.unlock();
        }
    }
    
    /**
     * 获取模板内容
     * 
     * @param templateTag
     * @param templateElementTags
     * @return
     */
    public static String getTemplate(String templateTag, List<String> templateOrCommonElementTags)
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
            return getConfigForFormatType0(templateTag, format0Map);
        case FORMAT_TYPE_1:
            return getConfigForFormatType1(templateTag, templateOrCommonElementTags, format1Map);
        case FORMAT_TYPE_2:
            return getConfigForFormatType2(templateTag, templateOrCommonElementTags, format2Map);
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    private static String getConfigForFormatType0(String templateTag, Object map)
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
        Map<String, List<String>> formatMap = (Map<String, List<String>>) map;
        if (templateElementTags == null || templateElementTags.isEmpty())
        {
            logger.error("templateElementTags should not be empty");
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
            for (String templateElementTag : templateElementTags)
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
                if (templateSection.contains(buildTemplateElementTagWithBeginAndAfter(templateElement)))
                {
                    template += templateSection;
                    break;
                }
            }
        }
        return template;
    }
    
    @SuppressWarnings("unchecked")
    private static String getConfigForFormatType2(String templateTag, List<String> commonElementTags, Object map)
    {
        Map<String, Map<String, List<String>>> formatMap = (Map<String, Map<String, List<String>>>) map;
        if (commonElementTags == null || commonElementTags.isEmpty())
        {
            logger.error("templateElementTags should not be empty");
            return null;
        }
        Map<String, List<String>> commonElementTagToCommonElementList = formatMap.get(templateTag);
        if (commonElementTagToCommonElementList == null)
        {
            return null;
        }
        String commonElementTag = commonElementTags.get(0).toLowerCase();
        List<String> commonElementList = commonElementTagToCommonElementList.get(commonElementTag);
        if (commonElementList == null || commonElementList.isEmpty())
        {
            return null;
        }
        int randomIndex = random.nextInt(commonElementList.size());
        return commonElementList.get(randomIndex);
    }
    
    /**
     * 从模板中提取semantic中的element的tag
     * 
     * @param line
     * @return
     */
    public static List<String> getTemplateElementTagsFromInput(String line)
    {
        return getTemplateElementTagsFromInput(line, true);
    }
    
    public static List<String> getTemplateElementTagsFromInput(String line, boolean isLowerCase)
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
            String tempalteElementTag = line.substring(startIndex + TEMPLATE_ELEMENT_START_TAG.length(), endIndex);
            if (isLowerCase)
            {
                tempalteElementTag = tempalteElementTag.toLowerCase();
            }
            ret.add(tempalteElementTag);
            cursor = endIndex + TEMPLATE_ELEMENT_END_TAG.length();
            if (cursor >= line.length())
            {
                break;
            }
            startIndex = line.indexOf(TEMPLATE_ELEMENT_START_TAG, cursor);
        }
        return ret;
    }
    
    public static String buildTemplateElementTagWithBeginAndAfter(String templateElementTag)
    {
        if (StringUtils.isEmpty(templateElementTag))
        {
            return null;
        }
        return TEMPLATE_ELEMENT_START_TAG + templateElementTag + TEMPLATE_ELEMENT_END_TAG;
    }
    
    /**
     * 从模板中提取到其他模板的Tag，返回小写
     * 
     * @param line
     * @return
     */
    public static List<String> getTemplateTagsFromInput(String line)
    {
        return getTemplateTagsFromInput(line, true);
    }
    
    public static List<String> getTemplateTagsFromInput(String line, boolean isLowerCase)
    {
        if (StringUtils.isEmpty(line))
        {
            return null;
        }
        List<String> ret = new ArrayList<String>();
        int startIndex = line.indexOf(TEMPLATE_START_TAG);
        int cursor = 0;
        while(startIndex >= 0)
        {
            int endIndex = line.indexOf(TEMPLATE_END_TAG, startIndex);
            if (endIndex < 0)
            {
                return null;
            }
            String tempalteTag = line.substring(startIndex, endIndex + TEMPLATE_END_TAG.length());
            if (isLowerCase)
            {
                tempalteTag = tempalteTag.toLowerCase();
            }
            ret.add(tempalteTag);
            cursor = endIndex + TEMPLATE_END_TAG.length();
            if (cursor >= line.length())
            {
                break;
            }
            startIndex = line.indexOf(TEMPLATE_START_TAG, cursor);
        }
        return ret;
    }
    
    public static String getSynonym(String templateElementTag, String value)
    {
        if (StringUtils.isEmpty(templateElementTag) || StringUtils.isEmpty(value))
        {
            return null;
        }
        Map<String, List<String>> templateElementValueToSynonymListMap = synonymMap.get(templateElementTag);
        if (templateElementValueToSynonymListMap == null)
        {
            return null;
        }
        List<String> synonymList = templateElementValueToSynonymListMap.get(value);
        if (synonymList == null || synonymList.isEmpty())
        {
            return null;
        }
        int randomIndex = random.nextInt(synonymList.size());
        return synonymList.get(randomIndex);
    }
}
