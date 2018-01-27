function getElement(_hasModify, _name)
{
	var element = {
		hasModify : _hasModify,
		name : _name
	}
	return element;
}

/** 定义元素结构体 */
var videoModel = getElement(true, "<NAME>");
var actorModel = getElement(true, "<ACTOR>");
var directorModel = getElement(true, "<Director>");
var typeModel = getElement(true, "<Type>");
var areaModel = getElement(false, "<Area>");
var yearModel = getElement(false, "<Year>");
var roleModel = getElement(false, "<Role>");
var languageModel = getElement(false, "<Language>");
var rateModel = getElement(false, "<Rate>");
var publisherModel = getElement(false, "<Publisher>");
var awardModel = getElement(false, "<Award>");
var sub_awardModel = getElement(false, "<SubAward>");
var categoryModel = getElement(false, "<Category>");
var tagModel = getElement(false, "<Tag>");
/** 其他结构体 */
/* 前导语*/
var beforeModel = getElement(false, "<Before>");

/** 定义标签map */
var elementList = [videoModel, actorModel, directorModel, typeModel,
	areaModel, yearModel, roleModel, languageModel, rateModel, publisherModel,
	awardModel, sub_awardModel, beforeModel];
var elementMap = {};
for (var i = 0; i < elementList.length; i ++)
{
	element = elementList[i];
	elementMap[element.name] = element;
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

function getReplyForVideoQueryWithUSB(semantic)
{
	
}

function getReplyForVideoQueryWithName(semantic)
{
	
}

/**
 * 三个参数以上的不加截词
 */
function getReplyForVideoQueryWithoutName(semantic)
{
	
}

function getReplyForSelect(data)
{
	
}

function getReplyForTVSet(data)
{
	
}

var d = validateInputElement(data);
if (d)
{
	var semantic = data.getSemantic();
	service.logError("有效的输入");
	semantic.get("name");
}
else
{
	service.logError("无效的输入");
	videoModel.name;
}