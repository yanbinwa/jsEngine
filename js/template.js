/** 模板变量 */
var templateElementTagToElementMap = {};
/** 模板变量 */
var templateFeature = null;
/** 模板变量 */
var templateTag = null;
/** 模板变量 */
var tempalteElementTagToModifyTagMap = null;
/** 模板变量 */
var processContext = null;

/**
 * 模板函数
 * 
 * 返回本脚本目标的Intent和Domain组合
 * @returns
 */
function getTargetTagList()
{
	return null;
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
 * 1. 如果Actor与Director一致，去掉Director
 * 2. 如果有片名，且有片名后缀，进行合并（Java代码提供工具）
 * 
 * @param semantic
 * @returns
 */
function translateSemanticValue(semantic)
{
	
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