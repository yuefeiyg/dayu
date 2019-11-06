package com.blcultra.controller;


import com.blcultra.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "message/")
@CrossOrigin
public class MessageController {
    private static final Logger log = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private MessageService messageService;

    /**
     * 查询未读消息数
     * @return
     */
    @GetMapping(value = "/unread/num",produces = "application/json;charset=UTF-8")
    public String getUnreadNum() {
        String res = messageService.getUnreadNum();
        return res;
    }

    /**
     * 查询未读消息
     * @return
     */
    @GetMapping(value = "/unread",produces = "application/json;charset=UTF-8")
    public String getUnreadNotes() {
        String res = messageService.getUnreadNotes();
        return res;
    }
    /**
     * 读消息
     * @return
     */
    @GetMapping(value = "/read",produces = "application/json;charset=UTF-8")
    public String readMessage(@RequestParam (value = "messageid" ,required =true ) String messageid,
                              @RequestParam (value = "type" ,required =true ) String type) {
        Map<String,Object> map = new HashMap<>(5);
        map.put("messageid",messageid);
        map.put("type",type);
        String res = messageService.readMessage(map);
        return res;
    }

}
