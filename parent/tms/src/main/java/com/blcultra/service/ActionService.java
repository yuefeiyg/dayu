package com.blcultra.service;

import com.blcultra.model.Action;

import java.util.List;
import java.util.Map;

/**
 * Created by sgy05 on 2019/3/6.
 */
public interface ActionService {

    List<String> getActionMoudles();

    List<Map<String ,Object>> getActionsListByMoudle(String moudle);
}
