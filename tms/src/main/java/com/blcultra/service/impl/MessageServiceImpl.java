package com.blcultra.service.impl;

import com.alibaba.fastjson.JSON;
import com.blcultra.dao.TaskInfoNoteMapper;
import com.blcultra.service.MessageService;
import com.blcultra.shiro.JWTUtil;
import com.blcultra.support.JsonModel;
import com.blcultra.support.ReturnCode;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("messageService")
public class MessageServiceImpl implements MessageService {

    private Logger log= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TaskInfoNoteMapper taskInfoNoteMapper;

    @Override
    public String getUnreadNum() {
        String res = null;
        try{
            String userid = JWTUtil.getUserId(SecurityUtils.getSubject().getPrincipals().toString());
            int notesNum =  taskInfoNoteMapper.getUnreadNum(userid);
            JsonModel jm = new JsonModel(true, ReturnCode.SUCESS_CODE_0000.getValue(), ReturnCode.SUCESS_CODE_0000.getKey(),notesNum);
            res = JSON.toJSONString(jm);
            return res;
        }catch (Exception e){
            log.error(e.getMessage());
            JsonModel jm = new JsonModel(false, ReturnCode.ERROR_CODE_1111.getValue(), ReturnCode.ERROR_CODE_1111.getKey(),null);
            res = JSON.toJSONString(jm);
            return res;
        }
    }

    @Override
    public String getUnreadNotes() {
        String res = null;
        try{
            String userid = JWTUtil.getUserId(SecurityUtils.getSubject().getPrincipals().toString());
            List<Map<String,Object>> notes =  taskInfoNoteMapper.getNotesByUser(userid);
            JsonModel jm = new JsonModel(true, ReturnCode.SUCESS_CODE_0000.getValue(), ReturnCode.SUCESS_CODE_0000.getKey(),notes);
            res = JSON.toJSONString(jm);
            return res;
        }catch (Exception e){
            log.error(e.getMessage());
            JsonModel jm = new JsonModel(false, ReturnCode.ERROR_CODE_1111.getValue(), ReturnCode.ERROR_CODE_1111.getKey(),null);
            res = JSON.toJSONString(jm);
            return res;
        }
    }

    @Override
    public String readMessage(Map<String, Object> map) {
        String res = null;
        try{
            if("note".equals(map.get("type"))){
                map.put("readstate","0");
                map.put("noteid",map.get("messageid"));
                taskInfoNoteMapper.updateTaskInfoNote(map);
            }
            JsonModel jm = new JsonModel(true, ReturnCode.SUCESS_CODE_0000.getValue(), ReturnCode.SUCESS_CODE_0000.getKey(),null);
            res = JSON.toJSONString(jm);
            return res;
        }catch (Exception e){
            log.error(e.getMessage());
            JsonModel jm = new JsonModel(false, ReturnCode.ERROR_CODE_1111.getValue(), ReturnCode.ERROR_CODE_1111.getKey(),null);
            res = JSON.toJSONString(jm);
            return res;
        }
    }
}
