/** 模板方法 */
function isTriggered(input)
{
	return false;
}

/** 模板方法 */
function getReply(semantic)
{
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