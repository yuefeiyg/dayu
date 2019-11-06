package com.blcultra.service.core;

import com.blcultra.cons.ActionEnum;
import com.blcultra.cons.Common;
import com.blcultra.cons.OperationalRole;
import com.blcultra.support.ActionsHelper;

import java.util.*;


/**
 * Created by sgy05 on 2019/3/7.
 */
public class ProjectAuxiliaryCalculation {

    private Map<String, List<String>> projectactions = new HashMap<>();

    private Map<String, Map<String, Object>> actions = new HashMap<>();

    private List<String> public_action = new ArrayList<>(Arrays.asList(ActionEnum.VIEW.getKey(), ActionEnum.EDIT.getKey(),
                                    ActionEnum.DELETE.getKey(),ActionEnum.MODIFYPROJECTADMIN.getKey()));


    public ProjectAuxiliaryCalculation(){
        List<String> public_action = new ArrayList<>(Arrays.asList(ActionEnum.VIEW.getKey(), ActionEnum.EDIT.getKey(),
                ActionEnum.DELETE.getKey(),ActionEnum.MODIFYPROJECTADMIN.getKey()));
        projectactions.put(Common.MODULE_PROJECT,public_action);
        actions = ActionsHelper.getInstance().actionDictionaries;

    }

    public  List<Map<String, Object>> getActions(String role, String table){

        List<Map<String, Object>> selectedActions = new ArrayList<>();

        List<String> candidateActions = projectactions.get(table);

        for (String candidateAction:candidateActions){
            Map<String, Object> action = actions.get(Common.MODULE_PROJECT + "_" + candidateAction);

            Map<String, Object> newaction = new HashMap<>(action);
            //角色为admin、manager时，action默认是显示的即hidden值为false
            if (Common.USER_ROLE_ADMIN.equals(role) || Common.USER_ROLE_MANAGER.equals(role)){
                selectedActions.add(newaction);
            }else {
                newaction.put(Common.HIDDEN, true);
                selectedActions.add(newaction);
            }

        }
        return selectedActions;
    }


}
