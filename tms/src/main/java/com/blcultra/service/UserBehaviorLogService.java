package com.blcultra.service;

import com.blcultra.model.UserBehaviorLogWithBLOBs;

/**
 * Created by sgy05 on 2019/3/27.
 */
public interface UserBehaviorLogService {

    void saveLogs(UserBehaviorLogWithBLOBs userBehaviorLogWithBLOBs);
}
