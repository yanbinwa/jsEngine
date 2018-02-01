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
	if (hasSource(semantic) && hasName(semantic))
	{
		return serivce.getText(semantic, "Name_Source");
	}
	if (hasSource(semantic) && semantic.get(LIST_NUMBER_TEMPLATE_ELEMENT_TAG) != null)
	{
		return serivce.getText(semantic, "ListNumber_Source");
	}
	if (semantic.get(LIST_NUMBER_TEMPLATE_ELEMENT_TAG) != null)
	{
		return service.getText(semantic, "ListNumber")
	}
}

function translateSemanticValue(semantic)
{
	//1. 如果有value：U盘中的第一部
	
	//2. 如果有row或者index，播第几行第几个
	
}