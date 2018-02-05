/** 模板元素的标志，大小写不敏感 */
var NAME_TEMPLATE_ELEMENT_TAG = "name".toLowerCase();
var LIKELY_NAME_TEMPLATE_ELEMENT_TAG = "likely_name".toLowerCase();
var ACTOR_TEMPLATE_ELEMENT_TAG = "actor".toLowerCase();
var DIRECTOR_TEMPLATE_ELEMENT_TAG = "director".toLowerCase();
var TYPE_TEMPLATE_ELEMENT_TAG = "type".toLowerCase();
var AREA_TEMPLATE_ELEMENT_TAG = "area".toLowerCase();
var YEAR_TEMPLATE_ELEMENT_TAG = "year".toLowerCase();
var ROLE_TEMPLATE_ELEMENT_TAG = "role".toLowerCase();
var LANGUAGE_TEMPLATE_ELEMENT_TAG = "language".toLowerCase();
var RATE_TEMPLATE_ELEMENT_TAG = "rate".toLowerCase();
var PUBLISHER_TEMPLATE_ELEMENT_TAG = "publisher".toLowerCase();
var AWARD_TEMPLATE_ELEMENT_TAG = "award".toLowerCase();
var SUBAWARD_TEMPLATE_ELEMENT_TAG = "sub_award".toLowerCase();
var CATEGORY_TEMPLATE_ELEMENT_TAG = "category".toLowerCase();
var TAG_TEMPLATE_ELEMENT_TAG = "tag".toLowerCase();
var SOURCE_TEMPLATE_ELEMENT_TAG = "source".toLowerCase();
var VALUE_TEMPLATE_ELEMENT_TAG = "value".toLowerCase();
var OPERANDS_TEMPLATE_ELEMENT_TAG = "operands".toLowerCase();
var DIRECT_TEMPLATE_ELEMENT_TAG = "direct".toLowerCase();
var EPISODE_TEMPLATE_ELEMENT_TAG = "episode".toLowerCase();

var END_TEMPLATE_TAG = "<$End$>".toLowerCase();

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

function hasEpisode(semantic)
{
	return semantic.get(EPISODE_TEMPLATE_ELEMENT_TAG) != null;
}

function getEpisode(semantic)
{
	return semantic.get(EPISODE_TEMPLATE_ELEMENT_TAG);
}

/**
 * 直接播报Format0模板
 */
function getText(semantic, templateTag)
{
	return service.getText(appid, semantic, templateTag);
}

/**
 * 直接播报Format1和2模板，根据templateElementTags的类型
 * 
 * 1. 如果为List<String>, 播报Format1
 * 2. 如果为String, 播报Format2
 * 
 */
function getText(semantic, templateTag, templateElementTags)
{
	return service.getText(appid, semantic, templateTag, templateElementTags);
}


function isEmptyStr(line)
{
	if (line == null || line.trim() == "")
	{
		return true;
	}
	return false;
}