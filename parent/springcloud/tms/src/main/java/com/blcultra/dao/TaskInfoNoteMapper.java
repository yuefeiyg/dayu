package com.blcultra.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TaskInfoNoteMapper {

    int insertSelective(Map<String, Object> taskinfonote) throws Exception;

    List<Map<String,Object>> getNotesByTask(Map<String, Object> map);

    List<Map<String,Object>> getNotesByUser(String userid);

    int getUnreadNum(String userid);

    int updateTaskInfoNote(Map<String, Object> map) throws Exception;

    int deleteNote(Map<String, Object> map) throws Exception;

}