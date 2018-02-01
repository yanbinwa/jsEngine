
/** 模板方法 */
function isTriggered(input)
{
	if (input.getDomain() == "TV" && input.getIntent() == "SET")
	{
		return true;
	}
	return false;
}

/** 模板方法 */
function getReply(semantic)
{
	//如果不含有operands或value，或者含有direct，均无法播报
	if (!hasOperands(semantic) || !hasValue(semantic) || hasDirect(semantic))
	{
		return null;
	}
	var value = parseInt(getValue(semantic));
	if (getOperands(semantic) == "OBJ_VOLUMN")
	{
		if (value > 50)
		{
			return service.getText(semantic, "VolumnTooHigh");
		}
		else if (value < 5)
		{
			return service.getText(semantic, "VolumnTooLow");
		}
	}
	else if (getOperands(semantic) == "OBJ_BRIGHTNESS")
	{
		if (value > 50)
		{
			return service.getText(semantic, "BrightTooHigh");
		}
		else if (value < 5)
		{
			return service.getText(semantic, "BrightTooLow");
		}
	}
	return null;
}