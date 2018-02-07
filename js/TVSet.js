
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
		if (value >= 40)
		{
			return getText(semantic, "TVSet", "VolumeTooHigh");
		}
		else if (value <= 5)
		{
			return getText(semantic, "TVSet", "VolumeTooLow");
		}
	}
	else if (getOperands(semantic) == "OBJ_BRIGHTNESS")
	{
		if (value >= 60)
		{
			return getText(semantic, "TVSet", "BrightTooHigh");
		}
		else if (value <= 15)
		{
			return getText(semantic, "TVSet", "BrightTooLow");
		}
	}
	return null;
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