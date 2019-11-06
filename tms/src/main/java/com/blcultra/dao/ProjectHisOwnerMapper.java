package com.blcultra.dao;

import com.blcultra.model.ProjectHisOwner;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProjectHisOwnerMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ProjectHisOwner record);

    int insertSelective(ProjectHisOwner record) throws Exception;

    ProjectHisOwner selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProjectHisOwner record);

    int updateByPrimaryKey(ProjectHisOwner record);
}