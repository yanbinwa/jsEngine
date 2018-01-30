/** 常量 */

/** 模板或修饰词类型，如果增加，需要同时修改Java代码*/
var VIDEO_QUERY_TEMPLATE_TAG = "Case1:";
var VIDEO_QUERY_COMMON_TEMPLATE_TAG = "Case2:";
var U_DIST_TEMPLATE_TAG = "Case3:";
var SELECT_TEMPLATE_TAG = "Case4:";
var TV_SET_TEMPLATE_TAG = "Case5:";
var BEFORE_TAG = "Case6:";
var AFTER_TAG = "Case7:";
var TYPE_MODIFY_TAG = "Case8:";

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
/** 点播,U盘,TVSET*/
var LIST_NUM_TEMPLATE_ELEMENT_TAG = service.getLowCaseString("<$ListNum$>");

/** 点播元素 */
var INDEX_ELEMENT_TAG = service.getLowCaseString("<$Index$>");
/** U盘 TVSET*/
var VALUE_ELEMENT_TAG = service.getLowCaseString("<$Value$>");
/** TVSET */
var OPERANDS_ELEMENT_TAG = service.getLowCaseString("<$Operands$>");
var DIRECT_ELEMENT_TAG = service.getLowCaseString("<$Direct$>");
var VOLUME_UP_TAG = service.getLowCaseString("VolumeUp");
var VOLUME_DOWN_TAG = service.getLowCaseString("VolumeDown");
var LIGHT_UP_TAG = service.getLowCaseString("LightUp");
var LIGHT_DOWN_TAG = service.getLowCaseString("LightDown");
var VOLUME_UP_THRESHOLD = 50;
var VOLUME_DOWN_THRESHOLD = 5;
var LIGHT_UP_THRESHOLD = 70;
var LIGHT_DOWN_THRESHOLD = 5;
var TVSET_VOLUME_BEFORE = "调节音量至<$ListNum$>，";
var TVSET_LIGHT_BEFORE = "亮度调节至<$ListNum$>，";

function getTemplateElement(_templateElementTag, _hasModify)
{
	var element = {
		hasModify : _hasModify,
		templateElementTag : _templateElementTag
	}
	return element;
}

/** 定义元素结构体 */
var videoTemplateElement = getTemplateElement(NAME_TEMPLATE_ELEMENT_TAG, true);
var actorTemplateElement = getTemplateElement(ACTOR_TEMPLATE_ELEMENT_TAG, true);
var directorTemplateElement = getTemplateElement(DIRECTOR_TEMPLATE_ELEMENT_TAG, true);
var typeTemplateElement = getTemplateElement(TYPE_TEMPLATE_ELEMENT_TAG, true);
var areaTemplateElement = getTemplateElement(AREA_TEMPLATE_ELEMENT_TAG, false);
var yearTemplateElement = getTemplateElement(YEAR_TEMPLATE_ELEMENT_TAG, false);
var roleTemplateElement = getTemplateElement(ROLE_TEMPLATE_ELEMENT_TAG, false);
var languageTemplateElement = getTemplateElement(LANGUAGE_TEMPLATE_ELEMENT_TAG, false);
var rateTemplateElement = getTemplateElement(RATE_TEMPLATE_ELEMENT_TAG, false);
var publisherTemplateElement = getTemplateElement(PUBLISHER_TEMPLATE_ELEMENT_TAG, false);
var awardTemplateElement = getTemplateElement(AWARD_TEMPLATE_ELEMENT_TAG, false);
var sub_awardTemplateElement = getTemplateElement(SUBAWARD_TEMPLATE_ELEMENT_TAG, false);
var categoryTemplateElement = getTemplateElement(CATEGORY_TEMPLATE_ELEMENT_TAG, false);
var tagTemplateElement = getTemplateElement(TAG_TEMPLATE_ELEMENT_TAG, false);
var resourceTemplateElement = getTemplateElement(SOURCE_TEMPLATE_ELEMENT_TAG, false);
/* 前导语*/
var beforeTemplateElement = getTemplateElement(BEFORE_TEMPLATE_ELEMENT_TAG, false);
var listNumTemplateElement = getTemplateElement(LIST_NUM_TEMPLATE_ELEMENT_TAG, false);

/** 定义标签map */
var elementList = [videoTemplateElement, actorTemplateElement, directorTemplateElement, typeTemplateElement,
	areaTemplateElement, yearTemplateElement, roleTemplateElement, languageTemplateElement, rateTemplateElement, 
	publisherTemplateElement, awardTemplateElement, sub_awardTemplateElement, categoryTemplateElement, tagTemplateElement,
	resourceTemplateElement, beforeTemplateElement, listNumTemplateElement];
var templateElementTagToElementMap = {};
for (var i = 0; i < elementList.length; i ++)
{
	element = elementList[i];
	templateElementTagToElementMap[element.templateElementTag] = element;
}

function getTemplateFeature(_chooseTemplateElements, _maxTemplateElement, _chooseModifyElements, _maxModifyCount, _hasBefore, _hasAfter)
{
	var feature = {
		chooseTemplateElements : _chooseTemplateElements,
		maxTemplateElement : _maxTemplateElement,
		chooseModifyElements : _chooseModifyElements,
		maxModifyCount : _maxModifyCount,
		hasBefore : _hasBefore,
		hasAfter : _hasAfter
	}
	return feature;
}

/** 定义每种模板的feature */
var videoQueryWithNameFeature = getTemplateFeature(
		[NAME_TEMPLATE_ELEMENT_TAG, ACTOR_TEMPLATE_ELEMENT_TAG, DIRECTOR_TEMPLATE_ELEMENT_TAG, YEAR_TEMPLATE_ELEMENT_TAG],
		2,
		[ACTOR_TEMPLATE_ELEMENT_TAG, NAME_TEMPLATE_ELEMENT_TAG, DIRECTOR_TEMPLATE_ELEMENT_TAG],
		1,
		true,
		true
	);

var videoQueryWithoutNameFeature = getTemplateFeature(
		[ACTOR_TEMPLATE_ELEMENT_TAG, DIRECTOR_TEMPLATE_ELEMENT_TAG, TYPE_TEMPLATE_ELEMENT_TAG, AREA_TEMPLATE_ELEMENT_TAG, YEAR_TEMPLATE_ELEMENT_TAG, ROLE_TEMPLATE_ELEMENT_TAG, LANGUAGE_TEMPLATE_ELEMENT_TAG, RATE_TEMPLATE_ELEMENT_TAG, PUBLISHER_TEMPLATE_ELEMENT_TAG, AWARD_TEMPLATE_ELEMENT_TAG, SUBAWARD_TEMPLATE_ELEMENT_TAG],
		-1,
		[ACTOR_TEMPLATE_ELEMENT_TAG, DIRECTOR_TEMPLATE_ELEMENT_TAG, TYPE_TEMPLATE_ELEMENT_TAG],
		1,
		true,
		true
	);

var videoQueryWithCommonTemplateFeature = getTemplateFeature(
		[ACTOR_TEMPLATE_ELEMENT_TAG, DIRECTOR_TEMPLATE_ELEMENT_TAG, TYPE_TEMPLATE_ELEMENT_TAG, AREA_TEMPLATE_ELEMENT_TAG, YEAR_TEMPLATE_ELEMENT_TAG, ROLE_TEMPLATE_ELEMENT_TAG, LANGUAGE_TEMPLATE_ELEMENT_TAG, RATE_TEMPLATE_ELEMENT_TAG, PUBLISHER_TEMPLATE_ELEMENT_TAG, AWARD_TEMPLATE_ELEMENT_TAG, SUBAWARD_TEMPLATE_ELEMENT_TAG],
		-1,
		[],
		-1,
		false,
		false
	);

var usbDistTemplateFeature = getTemplateFeature(
		[NAME_TEMPLATE_ELEMENT_TAG, LIST_NUM_TEMPLATE_ELEMENT_TAG, SOURCE_TEMPLATE_ELEMENT_TAG],
		-1,
		[],
		-1,
		true,
		false
	);

var selectTemplateFeature = getTemplateFeature(
		[LIST_NUM_TEMPLATE_ELEMENT_TAG],
		-1,
		[],
		-1,
		true,
		false
	);

var tvSetTemplateFeature = getTemplateFeature(
		[LIST_NUM_TEMPLATE_ELEMENT_TAG],
		-1,
		[],
		-1,
		false,
		false
	);
/**
 *  基本逻辑:
 *  1. 如果前面出现修饰语，那后面不加结词，如果没有修饰语，后面统一加结词
 *  2. 如果有智能结词，直接加到结尾，无需判断之前是否有修饰语
 *  3. 通用模板不需要加修饰语
 *  4. U盘资源片名不需要加修饰语
 *  5. 单片名如有其他元素，只选择一个，有优先级
 *  6. 如果有Category或Tag，将其添加搜索，这是默认选项
 */

/**
 * 判断输入的数据是否满足要求
 */
function validateInputElement(data)
{
	if (data == null)
	{
		return false;
	}
	if (data.getDomain() == null || data.getIntent() == null || data.getSemantic() == null)
	{
		return false;
	}
	return true;
}

/**
 * 通过Domain和Intent来获取
 */
function getReply(data)
{
	var tag = data.getDomain() + "_" + data.getIntent();
	var semantic = data.getSemantic();
	switch(tag)
	{
	case "VIDEO_QUERY":
		return getReplyForVideoQuery(semantic);
	case "VIDEO_PLAY":
		return getReplyForVideoQueryWithUSB(semantic);
	case "TV_SET":
		return getReplyForTVSet(semantic);
	case "CONTROL_SELECT":
		return getReplyForSelect(semantic);
	default:
		service.logError("无法识别的Domain和Intent");
	    return "";
	}
}

function getReplyForVideoQuery(semantic)
{
	if (semantic.get(SOURCE_TEMPLATE_ELEMENT_TAG) != null)
	{
		return getReplyForVideoQueryWithUSB(semantic);
	}
	else if (semantic.get(NAME_TEMPLATE_ELEMENT_TAG) != null)
	{
		return getReplyForVideoQueryWithName(semantic);
	}
	else
	{
		//需要计算除category和tag之外的参数个数
		var count = countTemplateElement(semantic);
		if (count < 3)
		{
			return getReplyForVideoQueryWithoutName(semantic);
		}
		else
	    {
			return getReplyForVideoQueryWithCommonTemplate(semantic);
	    }
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
 * 具有片名
 * 
 * @param semantic
 * @returns
 */
function getReplyForVideoQueryWithName(semantic)
{
	return getReplyBase(semantic, videoQueryWithNameFeature, VIDEO_QUERY_TEMPLATE_TAG);
}

/**
 * 不具有片名，其除category和tag之外的参数个数小于3个
 * 
 * @param semantic
 * @returns
 */
function getReplyForVideoQueryWithoutName(semantic)
{
	return getReplyBase(semantic, videoQueryWithoutNameFeature, VIDEO_QUERY_TEMPLATE_TAG);
}

/**
 * 不具有片名，其除category和tag之外的参数个数大于等于3个
 * 
 * @param semantic
 * @returns
 */
function getReplyForVideoQueryWithCommonTemplate(semantic)
{
	return getReplyBase(semantic, videoQueryWithCommonTemplateFeature, VIDEO_QUERY_COMMON_TEMPLATE_TAG);
}

/**
 * 从U盘获取资源，只提取name或第几部，不加修饰词和结词，如果出现有第几部，添加template
 * 
 * @param semantic
 * @returns
 */
function getReplyForVideoQueryWithUSB(semantic)
{
	service.translateSemanticValue(U_DIST_TEMPLATE_TAG, semantic);
	var templateElementTagList = getTemplateElementTagList(usbDistTemplateFeature, semantic);
	if (semantic.get(VALUE_ELEMENT_TAG) != null)
	{
		templateElementTagList.add(LIST_NUM_TEMPLATE_ELEMENT_TAG);
	}
	var template = getTemplate(U_DIST_TEMPLATE_TAG, templateElementTagList);
	if (service.isStringEmpty(template))
	{
		return "";
	}
	var modifyTagList = getModifyTagList(usbDistTemplateFeature, semantic);
	var modifyTagToModifyMap = getModify(modifyTagList, semantic);
	return assembleReply(U_DIST_TEMPLATE_TAG, usbDistTemplateFeature, templateElementTagList, template, modifyTagList, modifyTagToModifyMap, semantic);
}

/**
 * 点播，在获取模板，这里需要特殊处理的，先判断是否有index，如果有，才进行
 * 
 * @param semantic
 * @returns
 */
function getReplyForSelect(semantic)
{
	service.translateSemanticValue(SELECT_TEMPLATE_TAG, semantic);
	var templateElementTagList = getTemplateElementTagList(selectTemplateFeature, semantic);
	templateElementTagList.add(LIST_NUM_TEMPLATE_ELEMENT_TAG);
	var template = getTemplate(SELECT_TEMPLATE_TAG, templateElementTagList);
	if (service.isStringEmpty(template))
	{
		return "";
	}
	var modifyTagList = getModifyTagList(selectTemplateFeature, semantic);
	var modifyTagToModifyMap = getModify(modifyTagList, semantic);
	return assembleReply(SELECT_TEMPLATE_TAG, selectTemplateFeature, templateElementTagList, template, modifyTagList, modifyTagToModifyMap, semantic);
}

/**
 * TV_SET, 只有达到阈值，才播报，先判断阈值类型，有音量和声音
 * 
 * @param semantic
 * @returns
 */
function getReplyForTVSet(semantic)
{
	if (semantic.get(OPERANDS_ELEMENT_TAG) == null || semantic.get(VALUE_ELEMENT_TAG) == null)
	{
		return "";
	}
	if (semantic.get(DIRECT_ELEMENT_TAG) != null)
	{
		return "";
	}
	var elementTag = null;
	var before = null;
	if (semantic.get(OPERANDS_ELEMENT_TAG) == "OBJ_VOLUMN")
	{
		var value = service.getIntFromString(semantic.get(VALUE_ELEMENT_TAG));
		before = TVSET_VOLUME_BEFORE;
		if (value < VOLUME_DOWN_THRESHOLD)
		{
			elementTag = VOLUME_DOWN_TAG;
		}
		else if (value > VOLUME_UP_THRESHOLD)
		{
			elementTag = VOLUME_UP_TAG;
		}
	}
	else if (semantic.get(OPERANDS_ELEMENT_TAG) == "OBJ_BRIGHTNESS")
	{
		var value = service.getIntFromString(semantic.get(VALUE_ELEMENT_TAG));
		before = TVSET_LIGHT_BEFORE;
		if (value < LIGHT_DOWN_THRESHOLD)
		{
			elementTag = LIGHT_DOWN_TAG;
		}
		else if (value > LIGHT_UP_THRESHOLD)
		{
			elementTag = LIGHT_UP_TAG;
		}
	}
	if (isStringEmpty(elementTag))
	{
		return "";
	}
	service.translateSemanticValue(TV_SET_TEMPLATE_TAG, semantic);
	var templateElementTagList = getTemplateElementTagList(tvSetTemplateFeature, semantic);
	templateElementTagList.add(elementTag);
	if (semantic.get(VALUE_ELEMENT_TAG) != null)
	{
		templateElementTagList.add(LIST_NUM_TEMPLATE_ELEMENT_TAG);
	}
	var template = getTemplate(TV_SET_TEMPLATE_TAG, templateElementTagList);
	template = before + template;
	if (template == null)
	{
		return "";
	}
	var modifyTagList = getModifyTagList(tvSetTemplateFeature, semantic);
	var modifyTagToModifyMap = getModify(modifyTagList, semantic);
	return assembleReply(TV_SET_TEMPLATE_TAG, tvSetTemplateFeature, templateElementTagList, template, modifyTagList, modifyTagToModifyMap, semantic);
}

/**
 * 主流程，先拼接片名后缀，获取模板，获取修饰词，如果需要后缀，先尝试获取记忆播报内容，如果记忆播报没有，且没有修饰词，获取后缀
 * 
 * @param semantic
 * @param templateFeature
 * @param templateTag
 * @returns
 */
function getReplyBase(semantic, templateFeature, templateTag)
{
	service.translateSemanticValue(SELECT_TEMPLATE_TAG, semantic);
	var templateElementTagList = getTemplateElementTagList(templateFeature, semantic);
	var template = getTemplate(templateTag, templateElementTagList);
	if (isStringEmpty(template))
	{
		return "";
	}
	var modifyTagList = getModifyTagList(templateFeature, semantic);
	var modifyTagToModifyMap = getModify(modifyTagList, semantic);
	return assembleReply(templateTag, templateFeature, templateElementTagList, template, modifyTagList, modifyTagToModifyMap, semantic);
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
		var before = service.getBefore(BEFORE_TAG);
		semantic.put(BEFORE_TEMPLATE_ELEMENT_TAG, before);
	}
	if (semantic.get(CATEGORY_TEMPLATE_ELEMENT_TAG) != null)
	{
		templateElementTagList.add(CATEGORY_TEMPLATE_ELEMENT_TAG);
	}
	else if (semantic.get(TAG_TEMPLATE_ELEMENT_TAG) != null)
	{
		templateElementTagList.add(TAG_TEMPLATE_ELEMENT_TAG);
	}
	return templateElementTagList;
}

function getTemplate(templateTag, templateElementTagList)
{
	return service.getTemplateByTag(templateTag, templateElementTagList);
}

/**
 * 会将所有选择的modify tag获取，因为有些tag可能无法找到modify信息
 * @param templateFeature
 * @param semantic
 * @returns
 */
function getModifyTagList(templateFeature, semantic)
{
	var chooseModifyElements = templateFeature.chooseModifyElements;
	var modifyTagList = service.createEmptyList();
	for (var i = 0; i < chooseModifyElements.length; i ++)
	{
		if (semantic.get(chooseModifyElements[i]) != null && 
				templateElementTagToElementMap[chooseModifyElements[i]].hasModify)
		{
			modifyTagList.add(chooseModifyElements[i]);
		}
	}
	return modifyTagList;
}

/**
 * 通过semantic list来获取的
 * @param sematicTagList
 * @param sematic
 * @returns
 */
function getModify(sematicTagList, sematic)
{
	return service.getModifier(sematicTagList, sematic);
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
function assembleReply(templateTag, templateFeature, templateElementTagList, template, modifyTagList, modifyTagToModifyMap, semantic)
{
	var maxModifyCount = templateFeature.maxModifyCount;
	var chooseModifyTagList = service.createEmptyList();
	var count = 0;
	for(var i = 0; i < modifyTagList.size(); i ++)
	{
		if (modifyTagToModifyMap.get(modifyTagList.get(i)) != null)
		{
			chooseModifyTagList.add(modifyTagList.get(i));
			count ++;
			if (maxModifyCount > 0 && count >= maxModifyCount)
			{
				break;
			}
		}
	}
	
	var reply = service.assembleReply(template, templateElementTagList, chooseModifyTagList, modifyTagToModifyMap, semantic);
	var hasAfter = templateFeature.hasAfter;
	if (hasAfter)
	{
		var after = "";
		var memoryReplyTag = getMemoryReplyTag(templateElementTagList);
		if (memoryReplyTag != null)
		{
			after = getMemoryReply(memoryReplyTag);
		}
		if (chooseModifyTagList.size() == 0 && isStringEmpty(after))
		{
			after = service.getAfter(AFTER_TAG);
		}
		reply += after;
	}
	return reply;
}

/**
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

function getMemoryReply(memoryReplyTag)
{
	return "记忆播报";
}

function isStringEmpty(string)
{
	return service
}

//主流程：
var d = validateInputElement(data);
if (d)
{
	service.logInfo("有效的输入");
	var reply = getReply(data);
	reply;
}
else
{
	service.logError("无效的输入");
	videoModel.templateElementTag;
}