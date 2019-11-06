package com.blcultra.service.impl;

import com.alibaba.fastjson.JSON;
import com.blcultra.dao.TaskInfoNoteMapper;
import com.blcultra.service.TaskInfoNoteService;
import com.blcultra.shiro.JWTUtil;
import com.blcultra.support.JsonModel;
import com.blcultra.support.ReturnCode;
import com.dayu.util.DateFormatUtil;
import com.dayu.util.StringUtil;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("taskInfoNoteService")
public class TaskInfoNoteServiceImpl implements TaskInfoNoteService {

    private Logger log= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TaskInfoNoteMapper taskInfoNoteMapper;


    @Override
    public String addTaskNote(Map<String, Object> note) {
        String res = null;
        try {
            String uuid = StringUtil.getUUID();
            String userid = JWTUtil.getUserId(SecurityUtils.getSubject().getPrincipals().toString());
            note.put("noteid",uuid);
            note.put("createuserid", userid);
            note.put("receiveuserid", note.get("performerid"));
            note.put("createtime", DateFormatUtil.DateFormat());
            note.put("notestate", "1");
            note.put("readstate", "1");
            taskInfoNoteMapper.insertSelective(note);
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

    @Override
    public String getNotesByTask(Map<String, Object> map) {
        String res = null;
        try {
            List<Map<String,Object>> notes = taskInfoNoteMapper.getNotesByTask(map);
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
    public String updateNote(Map<String, Object> noteinfo) {
        String res = null;
        try {
            if(StringUtil.empty(noteinfo.get("noteid") + "")){
                JsonModel jm = new JsonModel(true, "缺少小纸条id", ReturnCode.ERROR_CODE_1111.getKey(),null);
                res = JSON.toJSONString(jm);
                return res;
            }
            taskInfoNoteMapper.updateTaskInfoNote(noteinfo);
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

    @Override
    public String deleteNote(String noteid) {
        String res = null;
        try {
            String uuid = StringUtil.getUUID();
            String userid = JWTUtil.getUserId(SecurityUtils.getSubject().getPrincipals().toString());
            Map<String,Object> map = new HashMap<>(2);
            map.put("noteid",noteid);
            map.put("userid",userid);
            int n = taskInfoNoteMapper.deleteNote(map);
            if(n > 0){
                JsonModel jm = new JsonModel(true, "删除成功", ReturnCode.SUCESS_CODE_0000.getKey(),null);
                res = JSON.toJSONString(jm);
            }else{
                JsonModel jm = new JsonModel(true, "没有删除权限", ReturnCode.SUCESS_CODE_0000.getKey(),null);
                res = JSON.toJSONString(jm);
            }
            return res;
        }catch (Exception e){
            log.error(e.getMessage());
            JsonModel jm = new JsonModel(false, ReturnCode.ERROR_CODE_1111.getValue(), ReturnCode.ERROR_CODE_1111.getKey(),null);
            res = JSON.toJSONString(jm);
            return res;
        }
    }
}
