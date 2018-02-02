
var LIST_NUMBER_TEMPLATE_ELEMENT_TAG = "list_number";

/** 模板方法 */
function isTriggered(input)
{
	if (input.getDomain() == "VIDEO" && input.getIntent() == "PLAY")
	{
		return true;
	}
	if (input.getDomain() == "CONTROL" && input.getIntent() == "SELECT")
	{
		return true;
	}
	var semantic = input.getSemantic();
	if (input.getDomain() == "VIDEO" && input.getIntent() == "QUERY" && hasSource(semantic))
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
	helper.addListNumber(semantic, "list_number");
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