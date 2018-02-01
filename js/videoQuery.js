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

/******************************* common.js end ********************************/

var videoTemplateElement = buildTemplateElement(NAME_TEMPLATE_ELEMENT_TAG, true);
var actorTemplateElement = buildTemplateElement(ACTOR_TEMPLATE_ELEMENT_TAG, true);
var directorTemplateElement = buildTemplateElement(DIRECTOR_TEMPLATE_ELEMENT_TAG, true);
var typeTemplateElement = buildTemplateElement(TYPE_TEMPLATE_ELEMENT_TAG, true);
var areaTemplateElement = buildTemplateElement(AREA_TEMPLATE_ELEMENT_TAG, false);
var yearTemplateElement = buildTemplateElement(YEAR_TEMPLATE_ELEMENT_TAG, false);
var roleTemplateElement = buildTemplateElement(ROLE_TEMPLATE_ELEMENT_TAG, false);
var languageTemplateElement = buildTemplateElement(LANGUAGE_TEMPLATE_ELEMENT_TAG, false);
var rateTemplateElement = buildTemplateElement(RATE_TEMPLATE_ELEMENT_TAG, false);
var publisherTemplateElement = buildTemplateElement(PUBLISHER_TEMPLATE_ELEMENT_TAG, false);
var awardTemplateElement = buildTemplateElement(AWARD_TEMPLATE_ELEMENT_TAG, false);
var sub_awardTemplateElement = buildTemplateElement(SUBAWARD_TEMPLATE_ELEMENT_TAG, false);
var categoryTemplateElement = buildTemplateElement(CATEGORY_TEMPLATE_ELEMENT_TAG, false);
var tagTemplateElement = buildTemplateElement(TAG_TEMPLATE_ELEMENT_TAG, false);
var beforeTemplateElement = buildTemplateElement(BEFORE_TEMPLATE_ELEMENT_TAG, false);

var elementList = [videoTemplateElement, actorTemplateElement, directorTemplateElement, typeTemplateElement,
	areaTemplateElement, yearTemplateElement, roleTemplateElement, languageTemplateElement, rateTemplateElement, 
	publisherTemplateElement, awardTemplateElement, sub_awardTemplateElement, categoryTemplateElement, tagTemplateElement,
	beforeTemplateElement];

var videoQueryWithNameFeature = buildTemplateFeature(
		[NAME_TEMPLATE_ELEMENT_TAG, ACTOR_TEMPLATE_ELEMENT_TAG, DIRECTOR_TEMPLATE_ELEMENT_TAG, YEAR_TEMPLATE_ELEMENT_TAG],
		2,
		[ACTOR_TEMPLATE_ELEMENT_TAG, NAME_TEMPLATE_ELEMENT_TAG, DIRECTOR_TEMPLATE_ELEMENT_TAG],
		1,
		true,
		true,
		false
	);

var videoQueryWithoutNameFeature = buildTemplateFeature(
		[ACTOR_TEMPLATE_ELEMENT_TAG, DIRECTOR_TEMPLATE_ELEMENT_TAG, TYPE_TEMPLATE_ELEMENT_TAG, AREA_TEMPLATE_ELEMENT_TAG, YEAR_TEMPLATE_ELEMENT_TAG, ROLE_TEMPLATE_ELEMENT_TAG, LANGUAGE_TEMPLATE_ELEMENT_TAG, RATE_TEMPLATE_ELEMENT_TAG, PUBLISHER_TEMPLATE_ELEMENT_TAG, AWARD_TEMPLATE_ELEMENT_TAG, SUBAWARD_TEMPLATE_ELEMENT_TAG],
		-1,
		[ACTOR_TEMPLATE_ELEMENT_TAG, DIRECTOR_TEMPLATE_ELEMENT_TAG],
		1,
		true,
		true,
		true
	);

var videoQueryWithCommonTemplateFeature = buildTemplateFeature(
		[ACTOR_TEMPLATE_ELEMENT_TAG, DIRECTOR_TEMPLATE_ELEMENT_TAG, TYPE_TEMPLATE_ELEMENT_TAG, AREA_TEMPLATE_ELEMENT_TAG, YEAR_TEMPLATE_ELEMENT_TAG, ROLE_TEMPLATE_ELEMENT_TAG, LANGUAGE_TEMPLATE_ELEMENT_TAG, RATE_TEMPLATE_ELEMENT_TAG, PUBLISHER_TEMPLATE_ELEMENT_TAG, AWARD_TEMPLATE_ELEMENT_TAG, SUBAWARD_TEMPLATE_ELEMENT_TAG],
		-1,
		[],
		-1,
		true,
		false,
		true
	);

/** 模板变量 */
var templateElementTagToElementMap = {};
/** 模板变量 */
var templateFeature = null;
/** 模板变量 */
var templateTag = null;
/** 模板变量 */
var tempalteElementTagToModifyTagMap = null;


for (var i = 0; i < elementList.length; i ++)
{
	element = elementList[i];
	templateElementTagToElementMap[element.templateElementTag] = element;
}
var videoQueryTemplateTag = "2";
var videoQueryCommonTemplateTag = "3";
var typeAfterTag = "7";
var processContext = null;

/**
 * 模板函数
 * 
 * 返回本脚本目标的Intent和Domain组合
 * @returns
 */
function getTargetTagList()
{
	return ["VIDEO_QUERY"];
}

/**
 * 模板函数
 * 
 * 返回执行的Context信息
 * @returns
 */
function getProcessContext(input)
{
	var semantic = input.getSemantic();
	if (semantic.get(NAME_TEMPLATE_ELEMENT_TAG) != null)
	{
		templateFeature = videoQueryWithNameFeature;
		templateTag = videoQueryTemplateTag;
	}
	else
	{
		var count = countTemplateElement(semantic);
		if (count < 3)
		{
			templateFeature = videoQueryWithoutNameFeature;
			templateTag = videoQueryTemplateTag;
		}
		else
	    {
			templateFeature = videoQueryWithCommonTemplateFeature;
			templateTag = videoQueryCommonTemplateTag;
	    }
	}
	tempalteElementTagToModifyTagMap = service.createEmptyMap();
	processContext = buildProcessContext(templateFeature, templateTag, templateElementTagToElementMap, tempalteElementTagToModifyTagMap);
	return processContext;
}

/**
 * 模板函数
 * 
 * 1. 如果Actor与Director一致，去掉Director
 * 2. 如果有片名，且有片名后缀，进行合并（Java代码提供工具）
 * 
 * @param semantic
 * @returns
 */
function translateSemanticValue(semantic)
{
	service.translateSemanticValueForVideoQuery(semantic);
}

/**
 * 模板函数
 * 
 * @param templateElementTagList
 * @returns
 */
function adjustTemplateElementTagList(templateElementTagList)
{
	
}

/**
 * 模板函数
 * 
 * 获取备选模板
 * @param semantic
 * @returns
 */
function getBackupTemplate(templateElementTagList)
{
	if (processContext.templateTag == videoQueryCommonTemplateTag)
	{
		return null;
	}
	processContext.templateFeature = videoQueryWithCommonTemplateFeature;
	processContext.templateTag = videoQueryCommonTemplateTag;
	return service.getConfigByTag(videoQueryCommonTemplateTag, templateElementTagList);
}

/**
 * 模板函数
 * 
 * @param modifyElementTagList
 * @returns
 */
function adjustModifyElementTagList(modifyElementTagList)
{
	if (processContext.templateTag == videoQueryCommonTemplateTag)
	{
		modifyElementTagList.clear();
	}
}

function countTemplateElement(semantic)
{
	var count = semantic.size();
	if (semantic.get(CATEGORY_TEMPLATE_ELEMENT_TAG))
	{
		count --;
	}
	if (semantic.get(TAG_TEMPLATE_ELEMENT_TAG))
	{
		count --;
	}
	return count;
}

/**
 * 模板函数
 * 
 * 除了category或tag外，只解析到year，type，area中的一种，并且满足一定的阈值条件，才进行记忆播报
 * 
 * 每种情况，再分情况判断
 * 
 */
function getMemoryReplyTag(templateElementTagList)
{
	var count = templateElementTagList.size();
	if (templateElementTagList.contains(BEFORE_TEMPLATE_ELEMENT_TAG))
	{
		count --;
	}
	if (templateElementTagList.contains(CATEGORY_TEMPLATE_ELEMENT_TAG))
	{
		count --;
	}
	if (templateElementTagList.contains(TAG_TEMPLATE_ELEMENT_TAG))	
	{
		count --;
	}
	if (count != 1)
	{
		return null;
	}
	if (templateElementTagList.contains(TYPE_TEMPLATE_ELEMENT_TAG))
	{
		return true;
	}
	else if (templateElementTagList.contains(AREA_TEMPLATE_ELEMENT_TAG))		
	{
		return true;
	}
	else if	(templateElementTagList.contains(YEAR_TEMPLATE_ELEMENT_TAG))
    {
		return true;
    }
	return null;
}

/**
 * 模板函数
 * 
 * @param memoryReplyTag
 * @returns
 */
function getMemoryReply(memoryReplyTag)
{
	return "记忆播报";
}

/**
 * 模板函数
 * 
 * 如果有type，更具type来获取定制化的After
 * 
 * @returns
 */
function getCustomizationAfter()
{
	var semantic = data.getSemantic();
	if (semantic.get(TYPE_TEMPLATE_ELEMENT_TAG) != null)
	{
		var templateElementTagList = service.createEmptyList();
		templateElementTagList.add(semantic.get(TYPE_TEMPLATE_ELEMENT_TAG));
		return service.getConfigByTag(typeAfterTag, templateElementTagList);
	}
	return null;
}

var reply = getReply(data);
reply