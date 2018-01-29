/** 常量 */
var SEMANTIC_NAME_TAG = "name";
var SEMANTIC_ACTOR_TAG = "actor";
var SEMANTIC_DIRECTOR_TAG = "director";
var SEMANTIC_TYPE_TAG = "type";
var SEMANTIC_EPISODE_TAG = "episode";
var SEMANTIC_SEASON_TAG = "season";
var SEMANTIC_PART_TAG = "part";
var SEMANTIC_TERM_TAG = "term";

var WITH_NAME_TEMPALTE_TAG = "Case1";
var SINGLE_ELEMENT_TEMPALTE_TAG = "Case2";
var TWICE_ELEMENT_TEMPALTE_TAG = "Case3";
var TRIPLE_AND_ABOVE_ELEMENT_TEMPALTE_TAG = "Case4";
var SELECT_TEMPALTE_TAG = "Case5";
var U_DISK_TEMPALTE_TAG = "Case6";
var TV_SET_TEMPALTE_TAG = "Case7";
var BEFORE_TEMPALTE_TAG = "Case8";
var AFTER_TEMPALTE_TAG = "Case9";

function getElement(_semanticTag, _hasModify, _templateElementTag)
{
	var element = {
		semanticTag : _semanticTag,
		hasModify : _hasModify,
		templateElementTag : _templateElementTag
	}
	return element;
}

/** 定义元素结构体 */
var videoModel = getElement("name", true, "<Name>");
var actorModel = getElement("actor", true, "<Actor>");
var directorModel = getElement("director", true, "<Director>");
var typeModel = getElement("type", true, "<Type>");
var areaModel = getElement("area", false, "<Area>");
var yearModel = getElement("year", false, "<Year>");
var roleModel = getElement("role", false, "<Role>");
var languageModel = getElement("language", false, "<Language>");
var rateModel = getElement("rate", false, "<Rate>");
var publisherModel = getElement("publisher", false, "<Publisher>");
var awardModel = getElement("award", false, "<Award>");
var sub_awardModel = getElement("sub_award", false, "<SubAward>");
var categoryModel = getElement("category", false, "<Category>");
var tagModel = getElement("tag", false, "<Tag>");
/** 其他结构体 */
/* 前导语*/
var beforeModel = getElement(null, false, "<Before>");

/** 定义标签map */
var elementList = [videoModel, actorModel, directorModel, typeModel,
	areaModel, yearModel, roleModel, languageModel, rateModel, publisherModel,
	awardModel, sub_awardModel, beforeModel];
var tempalteTagToElementMap = {};
for (var i = 0; i < elementList.length; i ++)
{
	element = elementList[i];
	tempalteTagToElementMap[element.templateElementTag] = element;
}

var semanticTagToElementMap = {};
for (var i = 0; i < elementList.length; i ++)
{
	element = elementList[i];
	if (element.semanticTag != null)
	{
		semanticTagToElementMap[element.semanticTag] = element;
	}
}

/**
 *  基本逻辑:如果前面出现修饰语，那后面不加结词，如果没有修饰语，后面同义加结词
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
	switch(tag)
	{
	case "VIDEO_QUERY":
		return getReplyForVideoQuery(data);
	case "TV_SET":
		return getReplyForTVSet(data);
	case "CONTROL_SELECT":
		return getReplyForSelect(data);
	default:
		service.logError("无法识别的Domain和Intent");
	    return "";
	}
}

function getReplyForVideoQuery(data)
{
	var semantic = data.getSemantic();
	if (semantic.get("source") != null)
	{
		return getReplyForVideoQueryWithUSB(semantic);
	}
	else if (semantic.get("name") != null)
	{
		return getReplyForVideoQueryWithName(semantic);
	}
	else
	{
		return getReplyForVideoQueryWithoutName(semantic);
	}
}

/**
 * 这里只是获取name，不加修饰词
 * 
 * @param semantic
 * @returns
 */
function getReplyForVideoQueryWithUSB(semantic)
{
	/** 每种reply的策略, 可以提前*/
	var chooseSemanticTagList = [SEMANTIC_NAME_TAG];
	//包含了modify提取的优先级顺序
	var needModifySemanticTagList = [];
	var maxModifyCount = 0;
	
	var sematicTagList = getSematicTagList(needModifySemanticTagList, semantic);
	var tempalte = getTemplate(U_DISK_TEMPALTE_TAG, sematicTagList);
	if (tempalte == null)
	{
		return "";
	}
	var modifyList = getModifyList(chooseSemanticTagList, semantic);
	var modifyMap = getModify(modifyList, semantic);
	return tempalte;
}

function getReplyForVideoQueryWithName(semantic)
{
	return "";
}

/**
 * 三个参数以上的不加截词
 */
function getReplyForVideoQueryWithoutName(semantic)
{
	return "";
}

function getReplyForSelect(data)
{
	return "";
}

function getReplyForTVSet(data)
{
	return "";
}

function getSematicTagList(chooseSemanticTagList, semantic)
{
	var sematicTagList = service.createEmptyList();
	for (var i = 0; i < chooseSemanticTagList.length; i ++)
	{
		if (semantic.get(chooseSemanticTagList[i]) != null)
		{
			sematicTagList.add(chooseSemanticTagList[i]);
		}
	}
	return sematicTagList;
}

function getModifyList(chooseSemanticTagList, semantic)
{
	var modifyList = service.createEmptyList();
	for (var i = 0; i < chooseSemanticTagList.length; i ++)
	{
		if (semantic.get(chooseSemanticTagList[i]) != null && semanticTagToElementMap[chooseSemanticTagList[i]].hasModify)
		{
			modifyList.add(chooseSemanticTagList[i]);
		}
	}
}

function getTemplate(tempalteTag, sematicTagList)
{
	var templateElementTagList = service.createEmptyList();
	for(var i = 0; i < sematicTagList.size(); i ++)
	{
		var templateElementTag = semanticTagToElementMap[sematicTagList.get(i)].templateElementTag;
		templateElementTagList.add(templateElementTag);
	}
	return service.getTemplateByTag(tempalteTag, templateElementTagList);
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

function assembleReply()
{
	
}

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