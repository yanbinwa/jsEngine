/** 模板方法 */
function isTriggered(input)
{
	if (input.getDomain == "VIDEO" && input.getIntent == "QUERY")
	{
		return true;
	}
	return false;
}

/** 
 * 1. 修改semantic中的value
 * 2. 判断是否有name，如果有，走第一个
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
	if (service.isStringEmpty(reply) && !useCommonTemplate)
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
	//2. 去掉多余的element，例如episode等
	
	
	//3. 将片名和片名后缀合并
	service.translateSemanticValueForVideoQuery(semantic);
}

function getReplyWithName(semantic)
{
	if (hasActor(semantic))
	{
		return getText(semantic, "Name_Actor");
	}
	if (hasDirector(semantic))
	{
		return getText(semantic, "Name_Director");
	}
	if (hasYear(semantic))
	{
		return getText(semantic, "Name_Year");
	}
	return getText(semantic, "Name");
}

function getReplyWithoutNameNum1(semantic)
{
	if(hasDirector(semantic) && hasCategory(semantic))
	{
		return getText(semantic, "Director_Category");
	}
	if(hasDirector(semantic))
	{
		return getText(semantic, "Director");
	}
	if(hasActor(semantic) && hasCategory(semantic))
	{
		return getText(semantic, "Actor_Category");
	}
	if(hasActor(semantic))
	{
		return getText(semantic, "Actor");
	}
	return null;
}

function getReplyWithoutNameNum2(semantic)
{
	if(hasDirector(semantic) && hasType(semantic) && hasCategory(semantic))
	{
		return getText(semantic, "Director_Type_Category");
	}
	return null;
}

function getReplyWithCommontTemplate(semantic)
{
	return service.getText(semantic, "Common_Template");
}
