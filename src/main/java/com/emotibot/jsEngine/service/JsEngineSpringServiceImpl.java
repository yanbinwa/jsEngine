package com.emotibot.jsEngine.service;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service("jsEngineSpringService")
public class JsEngineSpringServiceImpl implements JsEngineSpringService
{
    private static Logger logger = Logger.getLogger(JsEngineSpringServiceImpl.class);
    private JsEngineService service;
    
    public JsEngineSpringServiceImpl()
    {
        service = new JsEngineServiceImpl();
    }

    @Override
    public String getReply(String userid, String appid, String dataStr)
    {
        if (service == null)
        {
            logger.error("jsEngine objest is null");
            return null;
        }
        return service.getReplay(dataStr, userid, appid);
    }
}
