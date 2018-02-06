package com.emotibot.jsEngine.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.emotibot.jsEngine.service.JsEngineSpringService;
import com.emotibot.jsEngine.service.JsEngineSpringTestService;

@RestController
@RequestMapping("/reply")
public class JsEngineController
{
    @Autowired
    JsEngineSpringService jsEngineSpringService;
    
    @Autowired
    JsEngineSpringTestService jsEngineSpringTestService;
    
    @RequestMapping(value="/getReply", method = RequestMethod.POST)
    public String getReply(@RequestParam(value="userid", required=true) String userid,
                                     @RequestParam(value="appid", required=true) String appid,
                                     @RequestBody String dataStr)
    {
        return jsEngineSpringService.getReply(userid, appid, dataStr);
    }
    
    @RequestMapping(value="/getReplyTest", method = RequestMethod.GET)
    public String getReplyTest(@RequestParam(value="userid", required=true) String userid,
                                     @RequestParam(value="appid", required=true) String appid,
                                     @RequestParam(value="cmd", required=true) String cmd,
                                     @RequestParam(value="nocache", required=false) String nocache,
                                     @RequestParam(value="text", required=true) String text)
    {
        return jsEngineSpringTestService.test(appid, userid, cmd, nocache, text);
    }
}
