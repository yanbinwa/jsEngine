
var LIST_NUMBER_TEMPLATE_ELEMENT_TAG = "list_number".toLowerCase();

/** 模板方法 */
function isTriggered(input)
{
	var semantic = input.getSemantic();
	if (input.getDomain() == "VIDEO" && input.getIntent() == "PLAY")
	{
		//如果点播返回结果包含source，其值必须为MEDIA，因为平台层会自动用"EPG"填充source
		if (hasSource(semantic) && getSource(semantic) != "MEDIA")
		{
			return false;
		}
		return true;
	}
	if (input.getDomain() == "CONTROL" && input.getIntent() == "SELECT")
	{
		return true;
	}
	return false;
}

/** 模板方法 */
function getReply(semantic)
{
	translateSemanticValue(semantic);
	helper.logInfo(LIST_NUMBER_TEMPLATE_ELEMENT_TAG);
	if (hasSource(semantic) && hasName(semantic))
	{
		return getText(semantic, "Name_Source");
	}
	if (hasSource(semantic) && semantic.get(LIST_NUMBER_TEMPLATE_ELEMENT_TAG) != null)
	{
		return getText(semantic, "Source_ListNumber");
	}
	if (semantic.get(LIST_NUMBER_TEMPLATE_ELEMENT_TAG) != null)
	{
		return getText(semantic, "ListNumber")
	}
	return null;
}

function translateSemanticValue(semantic)
{
	helper.addListNumber(semantic, LIST_NUMBER_TEMPLATE_ELEMENT_TAG);
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