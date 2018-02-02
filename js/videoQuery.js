
/** 模板方法 */
function isTriggered(input)
{
	if (input.getDomain() == "VIDEO" && input.getIntent() == "QUERY")
	{
		var semantic = input.getSemantic();
		if (hasSource(semantic))
		{
			return false;
		}
		return true;
	}
	return false;
}

/** 
 * 1. 修改semantic中的value
 * 2. 判断是否有name，如果有，走getReplyWithName
 * 3. 判断其他重要参数的个数，根据不同的参数走不同的路径
 * 
 * 模板方法 
 */
function getReply(semantic)
{
	//修改semantic中的value
	translateSemanticValue(semantic);
	
	var reply = null;
	var useCommonTemplate = false;
	var semanticElementNum = getSemanticElementNum(semantic);
	if (hasName(semantic))
	{
		//具有片名
		reply = getReplyWithName(semantic);
	}
	else if (semanticElementNum == 1)
	{
		//不具有片名，且除Category和Tag之外只有一个参数
		reply = getReplyWithoutNameNum1(semantic);
	}
	else if (semanticElementNum == 2)
	{
		//不具有片名，且除Category和Tag之外只有两个参数
		reply = getReplyWithoutNameNum2(semantic);
	}
	else
	{
		//不具有片名，且除Category和Tag之外有三个以上的参数
		reply = getReplyWithCommontTemplate(semantic);
		useCommonTemplate = true
	}
	if (isEmptyStr(reply) && !useCommonTemplate)
	{
		reply = getReplyWithCommontTemplate(semantic);
	}
	return reply;
}

function translateSemanticValue(semantic)
{
	//1. 如果有likelyName，将likelyName赋值给name
	if (hasLikelyName(semantic))
	{
		semantic.put(NAME_TEMPLATE_ELEMENT_TAG, getLikelyName(semantic));
		semantic.remove(LIKELY_NAME_TEMPLATE_ELEMENT_TAG);
	}
	//2. 如果有actor和director，并且actor与director一致，去掉director
	if (hasActor(semantic) && hasDirector(semantic) && getActor(semantic) == getDirector(semantic))
	{
		semantic.remove(DIRECTOR_TEMPLATE_ELEMENT_TAG);
	}
	
	//3. 如果同时有category和tag，删除tag
	if (hasCategory(semantic) && hasTag(semantic))
	{
		semantic.remove(TAG_TEMPLATE_ELEMENT_TAG);
	}
	
	//4. 将片名和片名后缀合并
	helper.addNameProfix(semantic);
}

function getReplyWithName(semantic)
{
	if (hasActor(semantic))
	{
		return getTextForVideoQuery(semantic, "Name_Actor");
	}
	if (hasDirector(semantic))
	{
		return getTextForVideoQuery(semantic, "Name_Director");
	}
	if (hasYear(semantic))
	{
		return getTextForVideoQuery(semantic, "Name_Year");
	}
	return getTextForVideoQuery(semantic, "Name");
}

function getReplyWithoutNameNum1(semantic)
{
	if(hasDirector(semantic) && hasCategory(semantic))
	{
		return getTextForVideoQuery(semantic, "Director_Category");
	}
	if(hasDirector(semantic))
	{
		return getTextForVideoQuery(semantic, "Director");
	}
	if(hasActor(semantic) && hasCategory(semantic))
	{
		return getTextForVideoQuery(semantic, "Actor_Category");
	}
	if(hasActor(semantic))
	{
		return getTextForVideoQuery(semantic, "Actor");
	}
	if(hasType(semantic) && hasCategory(semantic))
	{
		return getTextForVideoQuery(semantic, "Type_Category");
	}
	if(hasType(semantic))
	{
		return getTextForVideoQuery(semantic, "Type");
	}
	if(hasArea(semantic) && hasCategory(semantic))
	{
		return getTextForVideoQuery(semantic, "Area_Category");
	}
	if(hasYear(semantic) && hasCategory(semantic))
	{
		return getTextForVideoQuery(semantic, "Year_Category");
	}
	return null;
}

function getReplyWithoutNameNum2(semantic)
{
	if(hasDirector(semantic) && hasType(semantic) && hasCategory(semantic))
	{
		return getTextForVideoQuery(semantic, "Director_Type_Category");
	}
	if(hasDirector(semantic) && hasType(semantic))
	{
		return getTextForVideoQuery(semantic, "Director_Type");
	}
	if(hasActor(semantic) && hasType(semantic) && hasCategory(semantic))
	{
		return getTextForVideoQuery(semantic, "Actor_Type_Category");
	}
	if(hasActor(semantic) && hasType(semantic))
	{
		return getTextForVideoQuery(semantic, "Actor_Type");
	}
	if(hasDirector(semantic) && hasActor(semantic) && hasCategory(semantic))
	{
		return getTextForVideoQuery(semantic, "Director_Actor_Category");
	}
	if(hasDirector(semantic) && hasActor(semantic))
	{
		return getTextForVideoQuery(semantic, "Director_Actor");
	}
	if(hasActor(semantic) && hasYear(semantic) && hasCategory(semantic))
	{
		return getTextForVideoQuery(semantic, "Actor_Year_Category");
	}
	if(hasActor(semantic) && hasYear(semantic))
	{
		return getTextForVideoQuery(semantic, "Actor_Year");
	}
	if(hasYear(semantic) && hasType(semantic) && hasCategory(semantic))
	{
		return getTextForVideoQuery(semantic, "Year_Type_Category");
	}
	if(hasYear(semantic) && hasType(semantic))
	{
		return getTextForVideoQuery(semantic, "Year_Type");
	}
	if(hasArea(semantic) && hasType(semantic) && hasCategory(semantic))
	{
		return getTextForVideoQuery(semantic, "Area_Type_Category");
	}
	if(hasArea(semantic) && hasType(semantic))
	{
		return getTextForVideoQuery(semantic, "Area_Type");
	}
	if(hasYear(semantic) && hasArea(semantic) && hasCategory(semantic))
	{
		return getTextForVideoQuery(semantic, "Year_Area_Category");
	}
	return null;
}

function getReplyWithCommontTemplate(semantic)
{
	//获取semantic中所有的element tag，调用common.js中的直接播报Format2
	var templateElementTags = helper.getTemplateElementTagsFromSemantic(semantic);
	if(hasActor(semantic) && hasRole(semantic))
	{
		return getText(semantic, "Common_Actor_Role", templateElementTags);
	}
	return getText(semantic, "Common_Other", templateElementTags);
}




/*********************************** START VideoQuery 特有的方法 ********************************/

//播报中需要关注的semantic中的元素
var needElementTags = [NAME_TEMPLATE_ELEMENT_TAG, ACTOR_TEMPLATE_ELEMENT_TAG, DIRECTOR_TEMPLATE_ELEMENT_TAG, TYPE_TEMPLATE_ELEMENT_TAG, 
	AREA_TEMPLATE_ELEMENT_TAG, YEAR_TEMPLATE_ELEMENT_TAG, ROLE_TEMPLATE_ELEMENT_TAG, 
	LANGUAGE_TEMPLATE_ELEMENT_TAG, RATE_TEMPLATE_ELEMENT_TAG, PUBLISHER_TEMPLATE_ELEMENT_TAG, 
	AWARD_TEMPLATE_ELEMENT_TAG, SUBAWARD_TEMPLATE_ELEMENT_TAG];

//播报中需要添加修饰词的元素
var needModifyTags = [NAME_TEMPLATE_ELEMENT_TAG, ACTOR_TEMPLATE_ELEMENT_TAG, DIRECTOR_TEMPLATE_ELEMENT_TAG];

/**
 * 1. 获取模板（同时将标签替换（before标签））
 * 2. 获取模板中的element
 * 3. 判断是否有记忆播报，如果有，直接替换，添加记忆播报，返回
 * 4. 判断element中是否有type，如果有，取出自定义结词，直接替换，返回
 * 5. 找出需要修饰词的element, 获取修饰词, 如果有修饰词，将修饰词按优先级排序，取第一个，与element合并; 
 * 6. 如果没有修饰词，添加结词, 替换词，返回
 * 
 * @param semantic
 * @param templateTag
 * @returns
 */
function getTextForVideoQuery(semantic, templateTag)
{
	var reply = null;
	
	//1. 提取模板，将模板中的标签替换掉
	var template = service.getTemplate(templateTag);
	if (isEmptyStr(template))
	{
		return null;
	}
	template = service.replaceTemplateTagInTemplate(template);
	if (isEmptyStr(template))
	{
		return null;
	}
	
	//2. 获取模板中的element
	var templateElementTags = service.getTemplateElementTagsFromInput(template, semantic);
	
	//3. 获取记忆播报
	var memoryReply = getMemoryReply(semantic);
	if (!isEmptyStr(memoryReply))
	{
		reply = service.replaceTemplateElementTagInTemplate(template, semantic);
		reply = addAfter(reply, memoryReply);
		return reply;
	}
	
	//4. 判断element中是否有type
	var hasTypeElementTag = false;
	for (var i = 0; i < templateElementTags.size(); i ++)
	{
		if (templateElementTags.get(i) == TYPE_TEMPLATE_ELEMENT_TAG)
		{
			hasTypeElementTag = true;
			break;
		}
	}
	if (hasTypeElementTag)
	{
		var typeEnd = service.getTemplate("Type_End", getType(semantic));
		if (!isEmptyStr(typeEnd))
		{
			reply = service.replaceTemplateElementTagInTemplate(template, semantic);
			reply = addAfter(reply, typeEnd);
			return reply;
		}
	}
	
	//5. 找出需要修饰词的element，按优先级来逐次提取，如果有，直接替换，返回
	var modifyElementTags = getModifyElementTags(templateElementTags);
	var modifyElementTagToModifyMap = service.getModifier(modifyElementTags, semantic);
	if (modifyElementTagToModifyMap != null)
	{
		for (var i = 0; i < modifyElementTags.size(); i ++)
		{
			var modifyTag = modifyElementTags.get(i);
			if (modifyElementTagToModifyMap.get(modifyTag) != null)
			{
				var modify = modifyElementTagToModifyMap.get(modifyTag);
				semantic.put(modifyTag, modify + semantic.get(modifyTag));
				reply = service.replaceTemplateElementTagInTemplate(template, semantic);
				return reply;
			}
		}
	}
	
	//6.如果没有修饰词，添加结词, 替换词，返回
	reply = service.replaceTemplateElementTagInTemplate(template, semantic);
	var after = service.getTemplate(END_TEMPLATE_TAG);
	reply = addAfter(reply, after);
	return reply;
}

function getSemanticElementNum(semantic)
{	
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

/**
 * 当semantic中除了category和tag以外，只存在type，area，year时，才激活记忆播报
 * 
 * @param semantic
 * @returns
 */
function getMemoryReply(semantic)
{
	var elementNum = getSemanticElementNum(semantic);
	if (elementNum != 1)
	{
		return null;
	}
	if (!hasType(semantic) && !hasArea(semantic) && !hasYear(semantic))
	{
		return null;
	}
	var personas = service.getPersonas(userid);
	if (personas == null)
	{
		return null;
	}
}

/**
 * 获取需要修饰词的元素
 * 
 * @param templateElementTags
 * @returns 返回结果有优先级
 */
function getModifyElementTags(templateElementTags)
{
	var modifyElementTags = helper.createEmptyList();
	for (var i = 0; i < needModifyTags.length; i ++)
	{
		var needModifyTag = needModifyTags[i];
		for (var j = 0; j < templateElementTags.size(); j ++)
		{
			if (templateElementTags.get(j) == needModifyTag)
			{
				modifyElementTags.add(needModifyTag);
				break;
			}
		}
	}
	return modifyElementTags;
}

function addAfter(reply, after)
{
	return reply + "，" + after;
}

/*********************************** END VideoQuery 特有的方法 ********************************/


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

