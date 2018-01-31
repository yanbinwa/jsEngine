
/******************************* common.js end ********************************/

var LIST_NUM_TEMPLATE_ELEMENT_TAG = service.getLowCaseString("<$ListNum$>");

var videoTemplateElement = buildTemplateElement(NAME_TEMPLATE_ELEMENT_TAG, false);
var sourceTemplateElement = buildTemplateElement(SOURCE_TEMPLATE_ELEMENT_TAG, false);
var listNumTemplateElement = buildTemplateElement(LIST_NUM_TEMPLATE_ELEMENT_TAG, false);

/** 模板变量 */
var templateElementTagToElementMap = {};
/** 模板变量 */
var templateFeature = buildTemplateFeature(
		[NAME_TEMPLATE_ELEMENT_TAG, LIST_NUM_TEMPLATE_ELEMENT_TAG, SOURCE_TEMPLATE_ELEMENT_TAG],
		-1,
		[],
		-1,
		true,
		false,
		false
	);

/** 模板变量 */
var templateTag = "4";
/** 模板变量 */
var tempalteElementTagToModifyTagMap = null;
/** 模板变量 */
var processContext = null;

var elementList = [videoTemplateElement, sourceTemplateElement, listNumTemplateElement];
for (var i = 0; i < elementList.length; i ++)
{
	element = elementList[i];
	templateElementTagToElementMap[element.templateElementTag] = element;
}

/**
 * 模板函数
 * 
 * 返回本脚本目标的Intent和Domain组合
 * @returns
 */
function getTargetTagList()
{
	return ["VIDEO_QUERY", "VIDEO_PLAY"];
}

/**
 * 模板函数
 * 
 * 返回执行的Context信息
 * @returns
 */
function getProcessContext(input)
{
	var tempalteElementTagToModifyTagMap = service.createEmptyMap();
	processContext = buildProcessContext(templateFeature, templateTag, templateElementTagToElementMap, tempalteElementTagToModifyTagMap);
	return processContext;
}

/**
 * 模板函数
 * 
 * @param semantic
 * @returns
 */
function translateSemanticValue(semantic)
{
	service.translateSemanticValueForUSB(semantic);
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
	return null;
}

/**
 * 模板函数
 * 
 * @param modifyElementTagList
 * @returns
 */
function adjustModifyElementTagList(modifyElementTagList)
{
	
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
	return null;
}

/**
 * 模板函数
 * 
 * @returns
 */
function getCustomizationAfter()
{
	return null;
}

var reply = getReply(data);
reply;