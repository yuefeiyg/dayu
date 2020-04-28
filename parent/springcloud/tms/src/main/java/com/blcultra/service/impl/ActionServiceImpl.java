package com.blcultra.service.impl;

import com.blcultra.dao.ActionMapper;
import com.blcultra.model.Action;
import com.blcultra.service.ActionService;
import javafx.beans.binding.ObjectExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by sgy05 on 2019/3/6.
 */
@Service("actionService")
public class ActionServiceImpl implements ActionService {

    @Autowired
    private ActionMapper actionMapper;
    @Override
    public List<String> getActionMoudles() {
        List<String> list = actionMapper.getActionMoudles();
        return list;
    }

    @Override
    public List<Map<String,Object>> getActionsListByMoudle(String moudle) {
        List<Map<String,Object>> list = actionMapper.getActionsByMoudle(moudle.trim());

        return list;
    }
}
