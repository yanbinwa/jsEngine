/** 模板元素的标志，大小写不敏感 */
var NAME_TEMPLATE_ELEMENT_TAG = "name";
var LIKELY_NAME_TEMPLATE_ELEMENT_TAG = "likely_name";
var ACTOR_TEMPLATE_ELEMENT_TAG = "actor";
var DIRECTOR_TEMPLATE_ELEMENT_TAG = "director";
var TYPE_TEMPLATE_ELEMENT_TAG = "type";
var AREA_TEMPLATE_ELEMENT_TAG = "area";
var YEAR_TEMPLATE_ELEMENT_TAG = "year";
var ROLE_TEMPLATE_ELEMENT_TAG = "role";
var LANGUAGE_TEMPLATE_ELEMENT_TAG = "language";
var RATE_TEMPLATE_ELEMENT_TAG = "rate";
var PUBLISHER_TEMPLATE_ELEMENT_TAG = "publisher";
var AWARD_TEMPLATE_ELEMENT_TAG = "award";
var SUBAWARD_TEMPLATE_ELEMENT_TAG = "sub_award";
var CATEGORY_TEMPLATE_ELEMENT_TAG = "category";
var TAG_TEMPLATE_ELEMENT_TAG = "tag";
var SOURCE_TEMPLATE_ELEMENT_TAG = "source";
var VALUE_TEMPLATE_ELEMENT_TAG = "value";
var OPERANDS_TEMPLATE_ELEMENT_TAG = "operands";
var DIRECT_TEMPLATE_ELEMENT_TAG = "direct";

function hasName(semantic)
{
	return semantic.get(NAME_TEMPLATE_ELEMENT_TAG) != null;
}

function getName(semantic)
{
	return semantic.get(NAME_TEMPLATE_ELEMENT_TAG);
}

function hasLikelyName(semantic)
{
	return semantic.get(NAME_TEMPLATE_ELEMENT_TAG) != null;
}

function getLikelyName(semantic)
{
	return semantic.get(NAME_TEMPLATE_ELEMENT_TAG);
}

function hasActor(semantic)
{
	return semantic.get(ACTOR_TEMPLATE_ELEMENT_TAG) != null;
}

function getActor(semantic)
{
	return semantic.get(ACTOR_TEMPLATE_ELEMENT_TAG);
}

function hasDirector(semantic)
{
	return semantic.get(DIRECTOR_TEMPLATE_ELEMENT_TAG) != null;
}

function getDirector(semantic)
{
	return semantic.get(DIRECTOR_TEMPLATE_ELEMENT_TAG);
}

function hasType(semantic)
{
	return semantic.get(TYPE_TEMPLATE_ELEMENT_TAG) != null;
}

function getType(semantic)
{
	return semantic.get(TYPE_TEMPLATE_ELEMENT_TAG);
}

function hasArea(semantic)
{
	return semantic.get(AREA_TEMPLATE_ELEMENT_TAG) != null;
}

function getArea(semantic)
{
	return semantic.get(AREA_TEMPLATE_ELEMENT_TAG);
}

function hasYear(semantic)
{
	return semantic.get(YEAR_TEMPLATE_ELEMENT_TAG) != null;
}

function getYear(semantic)
{
	return semantic.get(YEAR_TEMPLATE_ELEMENT_TAG);
}

function hasRole(semantic)
{
	return semantic.get(ROLE_TEMPLATE_ELEMENT_TAG) != null;
}

function getRole(semantic)
{
	return semantic.get(ROLE_TEMPLATE_ELEMENT_TAG);
}

function hasLanguage(semantic)
{
	return semantic.get(LANGUAGE_TEMPLATE_ELEMENT_TAG) != null;
}

function getLanguage(semantic)
{
	return semantic.get(LANGUAGE_TEMPLATE_ELEMENT_TAG);
}

function hasRate(semantic)
{
	return semantic.get(RATE_TEMPLATE_ELEMENT_TAG) != null;
}

function getRate(semantic)
{
	return semantic.get(RATE_TEMPLATE_ELEMENT_TAG);
}

function hasPublisher(semantic)
{
	return semantic.get(PUBLISHER_TEMPLATE_ELEMENT_TAG) != null;
}

function getPublisher(semantic)
{
	return semantic.get(PUBLISHER_TEMPLATE_ELEMENT_TAG);
}

function hasAward(semantic)
{
	return semantic.get(AWARD_TEMPLATE_ELEMENT_TAG) != null;
}

function getAward(semantic)
{
	return semantic.get(AWARD_TEMPLATE_ELEMENT_TAG);
}

function hasSubAward(semantic)
{
	return semantic.get(SUBAWARD_TEMPLATE_ELEMENT_TAG) != null;
}

function getSubAward(semantic)
{
	return semantic.get(SUBAWARD_TEMPLATE_ELEMENT_TAG);
}

function hasCategory(semantic)
{
	return semantic.get(CATEGORY_TEMPLATE_ELEMENT_TAG) != null;
}

function getCategory(semantic)
{
	return semantic.get(CATEGORY_TEMPLATE_ELEMENT_TAG);
}

function hasTag(semantic)
{
	return semantic.get(TAG_TEMPLATE_ELEMENT_TAG) != null;
}

function getTag(semantic)
{
	return semantic.get(TAG_TEMPLATE_ELEMENT_TAG);
}

function hasSource(semantic)
{
	return semantic.get(SOURCE_TEMPLATE_ELEMENT_TAG) != null;
}

function getSource(semantic)
{
	return semantic.get(SOURCE_TEMPLATE_ELEMENT_TAG);
}

function hasValue(semantic)
{
	return semantic.get(VALUE_TEMPLATE_ELEMENT_TAG) != null;
}

function getValue(semantic)
{
	return semantic.get(VALUE_TEMPLATE_ELEMENT_TAG);
}

function hasOperands(semantic)
{
	return semantic.get(OPERANDS_TEMPLATE_ELEMENT_TAG) != null;
}

function getOperands(semantic)
{
	return semantic.get(OPERANDS_TEMPLATE_ELEMENT_TAG);
}

function hasDirect(semantic)
{
	return semantic.get(DIRECT_TEMPLATE_ELEMENT_TAG) != null;
}

function getDirect(semantic)
{
	return semantic.get(DIRECT_TEMPLATE_ELEMENT_TAG);
}

/** VideoQuery的公告类*/

/**
 * 1. 获取模板（同时将标签替换（before标签））
 * 2. 获取模板中的element
 * 3. 判断是否有记忆播报，如果有，直接替换，添加记忆播报，返回
 * 4. 判断element中是否有type，如果有，取出自定义结词，直接替换，返回
 * 5. 找出需要修饰词的element
 * 6. 获取修饰词
 * 7. 如果有修饰词，将修饰词按优先级排序，取第一个，与element合并; 如果没有，添加结词
 * 8. 替换词，返回
 * 
 * @param semantic
 * @param templateTag
 * @returns
 */
function getText(semantic, templateTag)
{
	var template = service.getTemplate(templateTag);
	var templateTagList = getTemplateTagList(template);
	
}

function getSemanticElementNum(semantic)
{
	var needElementTags = [ACTOR_TEMPLATE_ELEMENT_TAG, DIRECTOR_TEMPLATE_ELEMENT_TAG, TYPE_TEMPLATE_ELEMENT_TAG, 
		AREA_TEMPLATE_ELEMENT_TAG, YEAR_TEMPLATE_ELEMENT_TAG, ROLE_TEMPLATE_ELEMENT_TAG, 
		LANGUAGE_TEMPLATE_ELEMENT_TAG, RATE_TEMPLATE_ELEMENT_TAG, PUBLISHER_TEMPLATE_ELEMENT_TAG, 
		AWARD_TEMPLATE_ELEMENT_TAG, SUBAWARD_TEMPLATE_ELEMENT_TAG];
	var count = 0;
	for (var i = 0; i < needElementTags.length; i ++)
	{
		if (semantic.get(needElementTags[i]) != null)
		{
			count ++;
		}
	}
	return count;
}

/** 执行main */

if(isTriggered(data))
{
	var semantic = data.getSemantic();
	var reply = getReply(semantic);
	reply;
}
else
{
	null;
}
