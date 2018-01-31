/** 模板元素的标志，大小写不敏感 */
var NAME_TEMPLATE_ELEMENT_TAG = service.getLowCaseString("<$Name$>");
var ACTOR_TEMPLATE_ELEMENT_TAG = service.getLowCaseString("<$Actor$>");
var DIRECTOR_TEMPLATE_ELEMENT_TAG = service.getLowCaseString("<$Director$>");
var TYPE_TEMPLATE_ELEMENT_TAG = service.getLowCaseString("<$Type$>");
var AREA_TEMPLATE_ELEMENT_TAG = service.getLowCaseString("<$Area$>");
var YEAR_TEMPLATE_ELEMENT_TAG = service.getLowCaseString("<$Year$>");
var ROLE_TEMPLATE_ELEMENT_TAG = service.getLowCaseString("<$Role$>");
var LANGUAGE_TEMPLATE_ELEMENT_TAG = service.getLowCaseString("<$Language$>");
var RATE_TEMPLATE_ELEMENT_TAG = service.getLowCaseString("<$Rate$>");
var PUBLISHER_TEMPLATE_ELEMENT_TAG = service.getLowCaseString("<$Publisher$>");
var AWARD_TEMPLATE_ELEMENT_TAG = service.getLowCaseString("<$Award$>");
var SUBAWARD_TEMPLATE_ELEMENT_TAG = service.getLowCaseString("<$SubAward$>");
var CATEGORY_TEMPLATE_ELEMENT_TAG = service.getLowCaseString("<$Category$>");
var TAG_TEMPLATE_ELEMENT_TAG = service.getLowCaseString("<$Tag$>");
var SOURCE_TEMPLATE_ELEMENT_TAG = service.getLowCaseString("<$Source$>");
var BEFORE_TEMPLATE_ELEMENT_TAG = service.getLowCaseString("<$Before$>");

var BEFORE_TEMPLATE_TAG = "0";
var AFTER_TEMPLATE_TAG = "1";

/**
 * @param _templateElementTag   元素对应的templateElement
 * @param _hasModify	  该元素是否有修饰词
 * @returns
 */
function buildTemplateElement(_templateElementTag, _hasModify)
{
	var element = {
		hasModify : _hasModify,
		templateElementTag : _templateElementTag
	}
	return element;
}

/**
 * @param _chooseTemplateElements 该类template关注的模板参数，按优先级排序
 * @param _maxTemplateElement 该类template需要的最多模板参数，-1则没有数目限制
 * @param _chooseModifyElements 该类template关注的修饰词参数，按优先级排序
 * @param _maxModifyCount 该类template需要的最多修饰词参数，-1则没有数目限制
 * @param _hasBefore 该类template是否需要前导词
 * @param _hasAfter 该类template是否需要结词（包括记忆播报）
 * @param _hasCategoryOrTag 该类template是否显示category或tag模板参数
 * @returns
 */
function buildTemplateFeature(_chooseTemplateElements, _maxTemplateElement, _chooseModifyElements, _maxModifyCount, _hasBefore, _hasAfter, _hasCategoryOrTag)
{
	var feature = {
		chooseTemplateElements : _chooseTemplateElements,
		maxTemplateElement : _maxTemplateElement,
		chooseModifyElements : _chooseModifyElements,
		maxModifyCount : _maxModifyCount,
		hasBefore : _hasBefore,
		hasAfter : _hasAfter,
		hasCategoryOrTag : _hasCategoryOrTag
	}
	return feature;
}

/**
 * @param _templateFeature	
 * @param _templateTag
 * @param _templateElementTagToElementMap
 * @param _tempalteElementTagToModifyTagMap
 * @returns
 */
function buildProcessContext(_templateFeature, _templateTag, _templateElementTagToElementMap, _tempalteElementTagToModifyTagMap)
{
	var context = {
		templateFeature : _templateFeature,
		templateTag : _templateTag,
		templateElementTagToElementMap : _templateElementTagToElementMap,
		tempalteElementTagToModifyTagMap : _tempalteElementTagToModifyTagMap
	}
	return context;
}

/**
 * 1. 判断输入是否有效
 * 2. 判断是否属于当前的脚本
 * 3. 进行预处理，获取context
 * 4. 对于semantic进行预处理（例如片名和后缀合并，生成点播的文字等）
 * 5. 获取关注的templateElementTag
 * 6. 调整templateElementTag（例如TVSet，需要添加新的Tag元素）
 * 
 * @param input
 * @returns
 */
function getReply(input)
{
	if (!validateInputElement(input))
	{
		return null;
	}
	if (!isMatchCurrentProcess(input))
	{
		return null;
	}
	var processContext = getProcessContext(input);
    
    var semantic = input.getSemantic();
	translateSemanticValue(semantic);
	var templateElementTagList = getTemplateElementTagList(processContext.templateFeature, semantic);
	adjustTemplateElementTagList(templateElementTagList);
	var template = getTemplate(processContext.templateTag, templateElementTagList);
	if (isStringEmpty(template))
	{
		template = getBackupTemplate(templateElementTagList);
	}
	if (isStringEmpty(template))
	{
		return null;
	}
	var modifyElementTagList = getModifyElementTagList(processContext.templateFeature, semantic, processContext.templateElementTagToElementMap);
	adjustModifyElementTagList(modifyElementTagList);
	var modifyElementTagToModifyMap = getModify(modifyElementTagList, processContext.tempalteElementTagToModifyTagMap, semantic);
	return assembleReply(processContext.templateTag, processContext.templateFeature, templateElementTagList, template, modifyElementTagList, modifyElementTagToModifyMap, semantic);
}

/**
 * 判断输入参数是否合法
 * 
 * @param data
 * @returns
 */
function validateInputElement(input)
{
	if (input == null)
	{
		return false;
	}
	if (input.getDomain() == null || input.getIntent() == null || input.getSemantic() == null)
	{
		return false;
	}
	return true;
}

/**
 * 调用getTargetTag()方法得到的，需要实现
 * 
 * @returns
 */
function isMatchCurrentProcess(input)
{
	var targetTagList = getTargetTagList();
	if (targetTagList == null)
	{
		return false;
	}
	var tag = input.getDomain() + "_" + input.getIntent();
	for (var i = 0; i < targetTagList.length; i ++)
	{
		if (targetTagList[i] == tag)
		{
			return true;
		}
	}
	return false;
}

/**
 * 1. 如果含有before，要加上，同时将before赋值给semantic
 * 2. 如果含有category或者tag，要加上
 * 
 * @param templateFeature
 * @param semantic
 * @returns
 */
function getTemplateElementTagList(templateFeature, semantic)
{
	var chooseTemplateElements = templateFeature.chooseTemplateElements;
	var maxTemplateElement = templateFeature.maxTemplateElement;
	var templateElementTagList = service.createEmptyList();
	var count = 0;
	for (var i = 0; i < chooseTemplateElements.length; i ++)
	{
		if (semantic.get(chooseTemplateElements[i]) != null)
		{
			templateElementTagList.add(chooseTemplateElements[i]);
			count ++;
			if (maxTemplateElement > 0 && count >= maxTemplateElement)
			{
				break;
			}
		}
	}
	if (templateFeature.hasBefore)
	{
		templateElementTagList.add(BEFORE_TEMPLATE_ELEMENT_TAG);
		var before = service.getConfigByTag(BEFORE_TEMPLATE_TAG, null);
		semantic.put(BEFORE_TEMPLATE_ELEMENT_TAG, before);
	}
	if (semantic.get(CATEGORY_TEMPLATE_ELEMENT_TAG) != null && templateFeature.hasCategoryOrTag)
	{
		templateElementTagList.add(CATEGORY_TEMPLATE_ELEMENT_TAG);
	}
	else if (semantic.get(TAG_TEMPLATE_ELEMENT_TAG) != null && templateFeature.hasCategoryOrTag)
	{
		templateElementTagList.add(TAG_TEMPLATE_ELEMENT_TAG);
	}
	return templateElementTagList;
}

function getTemplate(templateTag, templateElementTagList)
{
	return service.getConfigByTag(templateTag, templateElementTagList);
}

/**
 * 会将所有选择的modify tag获取，因为有些tag可能无法找到modify信息
 * 
 * @param templateFeature
 * @param semantic
 * @returns
 */
function getModifyElementTagList(templateFeature, semantic, templateElementTagToElementMap)
{
	var chooseModifyElements = templateFeature.chooseModifyElements;
	var modifyElementTagList = service.createEmptyList();
	for (var i = 0; i < chooseModifyElements.length; i ++)
	{
		if (semantic.get(chooseModifyElements[i]) != null && 
				templateElementTagToElementMap[chooseModifyElements[i]].hasModify)
		{
			modifyElementTagList.add(chooseModifyElements[i]);
		}
	}
	return modifyElementTagList;
}

/**
 * 通过semantic list来获取的
 * @param sematicTagList
 * @param sematic
 * @returns
 */
function getModify(sematicTagList, sematic, tempalteElementTagToModifyTagMap)
{
	return service.getModifier(sematicTagList, sematic, tempalteElementTagToModifyTagMap);
}

/**
 * 先要根据feature来处理修饰词，在modifyTagList中是按优先级来排序的
 * 把最终得到的信息组合起来，之后再考虑是否加后缀信息
 * 
 * 加后缀信息的条件：
 * 1. feature中是否有后缀
 * 2. 是否之前已经有修饰词
 * 3. 是否需要记忆播报（需要通过条件判断）
 * 
 * @param templateTag
 * @param templateFeature
 * @param template
 * @param modifyTagToModifyMap
 * @param semantic
 * @returns
 */
function assembleReply(templateTag, templateFeature, templateElementTagList, template, modifyElementTagList, modifyElementTagToModifyMap, semantic)
{
	service.logInfo("templateTag: " + templateTag + "; templateElementTagList: " + templateElementTagList + "; template: " + template + "; modifyElementTagList: " + modifyElementTagList + "; modifyElementTagToModifyMap: " + modifyElementTagToModifyMap + "; semantic: " + semantic);
	var maxModifyCount = templateFeature.maxModifyCount;
	var chooseModifyElementTagList = service.createEmptyList();
	var count = 0;
	for(var i = 0; i < modifyElementTagList.size(); i ++)
	{
		if (modifyElementTagToModifyMap.get(modifyElementTagList.get(i)) != null)
		{
			chooseModifyElementTagList.add(modifyElementTagList.get(i));
			count ++;
			if (maxModifyCount > 0 && count >= maxModifyCount)
			{
				break;
			}
		}
	}
	
	var reply = service.assembleReply(template, templateElementTagList, chooseModifyElementTagList, modifyElementTagToModifyMap, semantic);
	var hasAfter = templateFeature.hasAfter;
	if (hasAfter)
	{
		var after = "";
		var memoryReplyTag = getMemoryReplyTag(templateElementTagList);
		if (memoryReplyTag != null)
		{
			after = getMemoryReply(memoryReplyTag);
		}
		if (chooseModifyElementTagList.size() == 0 && isStringEmpty(after))
		{
			after = getCustomizationAfter();
			if (isStringEmpty(after))
			{
				after = service.getConfigByTag(AFTER_TEMPLATE_TAG, null);
			}
		}
		reply += "，" + after;
	}
	return reply;
}

function isStringEmpty(string)
{
	return service.isStringEmpty(string);
}