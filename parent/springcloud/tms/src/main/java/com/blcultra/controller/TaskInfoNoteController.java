package com.blcultra.controller;


import com.blcultra.service.TaskInfoNoteService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "task/note")
@CrossOrigin
public class TaskInfoNoteController {
    private static final Logger log = LoggerFactory.getLogger(TaskInfoNoteController.class);

    @Autowired
    private TaskInfoNoteService taskInfoNoteService;


    /**
     * 新增note小纸条
     * @param note
     * projectid
     * taskid
     * objectdataid
     * performerid
     * note
     * @return
     */
    @PostMapping(value = "/add",produces = "application/json;charset=UTF-8")
    @RequiresAuthentication
    public String addTaskNote(@RequestBody Map note){
        return taskInfoNoteService.addTaskNote(note);
    }

    /**
     * 根据任务id查询小纸条
     * @param taskid
     * @return
     */
    @GetMapping(value = "/getNotes",produces = "application/json;charset=UTF-8")
    public String getNotesByTask( @RequestParam (value = "taskid" ,required =true ) String taskid) {
        Map<String,Object> map = new HashMap<>(1);
        map.put("taskid",taskid);
        String res = taskInfoNoteService.getNotesByTask(map);
        return res;
    }

    /**
     * 更新小纸条
     * @return
     */
    @PostMapping(value = "/update",produces = "application/json;charset=UTF-8")
    public String updateNote(@RequestBody Map<String,Object> noteinfo) {
        String res = taskInfoNoteService.updateNote(noteinfo);
        return res;
    }

    /**
     * 删除小纸条
     * @return
     */
    @GetMapping (value = "/delete",produces = "application/json;charset=UTF-8")
    public String deleteNote(@RequestParam (value = "noteid" ,required =true ) String noteid) {
        String res = taskInfoNoteService.deleteNote(noteid);
        return res;
    }

}
