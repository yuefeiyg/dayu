package com.blcultra.service.core;

import com.blcultra.cons.ActionEnum;
import com.blcultra.cons.Common;
import com.blcultra.cons.OperationalRole;
import com.blcultra.support.ActionsHelper;

import java.util.*;

public class TaskAuxiliaryCalculation {

    private Map<String, List<String>> taskactins = new HashMap<>();

    private Map<String, Map<String, Object>> actins;

    private List<String> public_member = new ArrayList<>(Arrays.asList(ActionEnum.VIEW.getKey(), ActionEnum.RECEIVE.getKey(), ActionEnum.START.getKey()));

    private List<String> my_member = new ArrayList<>(Arrays.asList(ActionEnum.VIEW.getKey(), ActionEnum.START.getKey(), ActionEnum.PAUSE.getKey(),
            ActionEnum.RECALL.getKey(), ActionEnum.ENTER.getKey()));

    public TaskAuxiliaryCalculation() {
        List<String> publicTaskActions = new ArrayList<>(Arrays.asList(ActionEnum.VIEW.getKey(), ActionEnum.EDIT.getKey(),
                ActionEnum.DELETE.getKey(), ActionEnum.RECEIVE.getKey(), ActionEnum.START.getKey()));
        List<String> myTaskActions = new ArrayList<>(Arrays.asList(ActionEnum.VIEW.getKey(), ActionEnum.START.getKey()
                , ActionEnum.PAUSE.getKey(), ActionEnum.REVERSE.getKey(), ActionEnum.RECALL.getKey(),ActionEnum.INVALIDATE.getKey(),
                ActionEnum.ENTER.getKey(),ActionEnum.COMMIT.getKey()));
        List<String> relatedTaskActions = new ArrayList<>(Arrays.asList(ActionEnum.VIEW.getKey()
                , ActionEnum.INVALIDATE.getKey(), ActionEnum.ENTER.getKey()));
        taskactins.put(Common.MODULE_TASK_TABLE_PUBLIC, publicTaskActions);
        taskactins.put(Common.MODULE_TASK_TABLE_MY, myTaskActions);
        taskactins.put(Common.MODULE_TASK_TABLE_RELATED, relatedTaskActions);
        actins = ActionsHelper.getInstance().actionDictionaries;
//        List<String> allactions = new ArrayList<>(Arrays.asList(ActionEnum.VIEW.getKey(),ActionEnum.EDIT.getKey(),
//                ActionEnum.DELETE.getKey(), ActionEnum.RECEIVE.getKey(),ActionEnum.START.getKey()
//                , ActionEnum.PAUSE.getKey(), ActionEnum.REVERSE.getKey(), ActionEnum.RECALL.getKey(),
//                ActionEnum.INVALIDATE.getKey(),ActionEnum.ENTER.getKey()));
//        for (String action:allactions ) {
//            String key = "task_"+action;
//            Map<String, Object> item = new HashMap<>();
//            item.put("action",action);
//            item.put(Common.HIDDEN,false);
//            actins.put(key,item);
//        }


    }


    public boolean checkTaskOwnerIsMe(String userId,String taskOwnerId,String projectOwner,List<String> projectOwnerIds){
        if (userId.equals(projectOwner) && projectOwnerIds.contains(taskOwnerId)){
            return true;
        }else {
            return false;
        }
    }

    public List<Map<String, Object>> getActions(OperationalRole operationalRole, String table, String taskState,
                                                boolean performerIsMe,String classifyCode) {

        List<Map<String, Object>> selectedActions = new ArrayList<>();

        List<String> candidateActions = taskactins.get(table);

        for (String candidateAction : candidateActions) {

            Map<String, Object> action = actins.get(Common.MODULE_TASK + "_" + candidateAction);

            Map<String, Object> newaction = new HashMap<>(action);
            if ((OperationalRole.CREATOR != operationalRole && OperationalRole.ADMIN != operationalRole)
                    && ActionEnum.INVALIDATE.getKey().equals(candidateAction)){
                newaction.put(Common.HIDDEN, true);
            }

            if (OperationalRole.MEMBER == operationalRole || OperationalRole.AUDITOR == operationalRole) {
                if (Common.MODULE_TASK_TABLE_PUBLIC.equals(table) && public_member.contains(candidateAction)) {
                    selectedActions.add(newaction);
                } else if (Common.MODULE_TASK_TABLE_MY.equals(table) && my_member.contains(candidateAction)) {
                    if (Common.MODULE_TASK_STATE_RECEIVE.equals(taskState) &&
                            (ActionEnum.PAUSE.getKey().equals(candidateAction) ||
                                    ActionEnum.RECALL.getKey().equals(candidateAction) ||
                                    ActionEnum.COMMIT.getKey().equals(candidateAction) ||
                                    ActionEnum.ENTER.getKey().equals(candidateAction))) {
                        newaction.put(Common.HIDDEN, true);
                    } else if (Common.MODULE_TASK_STATE_START.equals(taskState) &&
                            (ActionEnum.START.getKey().equals(candidateAction) ||
                                    (ActionEnum.COMMIT.getKey().equals(candidateAction) &&
                                            Common.MODULE_TASK_LABEL_CLASSFYCODE.equals(classifyCode))||
                                    (ActionEnum.ENTER.getKey().equals(candidateAction) &&
                                            !Common.MODULE_TASK_LABEL_CLASSFYCODE.equals(classifyCode))||
                                    ActionEnum.RECALL.getKey().equals(candidateAction))) {
                        newaction.put(Common.HIDDEN, true);
                    } else if (Common.MODULE_TASK_STATE_PAUSE.equals(taskState) &&
                            (ActionEnum.PAUSE.getKey().equals(candidateAction) ||
                                    (ActionEnum.COMMIT.getKey().equals(candidateAction) &&
                                            Common.MODULE_TASK_LABEL_CLASSFYCODE.equals(classifyCode))||
                                    (ActionEnum.ENTER.getKey().equals(candidateAction) &&
                                            !Common.MODULE_TASK_LABEL_CLASSFYCODE.equals(classifyCode))||
                                    ActionEnum.RECALL.getKey().equals(candidateAction)
                            )) {
                        newaction.put(Common.HIDDEN, true);
                    } else if (Common.MODULE_TASK_STATE_COMPLETE.equals(taskState) &&
                            (ActionEnum.START.getKey().equals(candidateAction) ||
                                    ActionEnum.COMMIT.getKey().equals(candidateAction) ||
                                    (ActionEnum.ENTER.getKey().equals(candidateAction) &&
                                            !Common.MODULE_TASK_LABEL_CLASSFYCODE.equals(classifyCode))||
                                    ActionEnum.PAUSE.getKey().equals(candidateAction) ||
                                    (!performerIsMe && ActionEnum.RECALL.getKey().equals(candidateAction))
                            )) {
                        newaction.put(Common.HIDDEN, true);
                    } else if (Common.MODULE_TASK_STATE_CLOSED.equals(taskState) &&
                            (ActionEnum.START.getKey().equals(candidateAction) ||
                                    ActionEnum.PAUSE.getKey().equals(candidateAction) ||
                                    ActionEnum.RECALL.getKey().equals(candidateAction)||
                                    ActionEnum.COMMIT.getKey().equals(candidateAction) ||
                                    (ActionEnum.ENTER.getKey().equals(candidateAction) &&
                                            !Common.MODULE_TASK_LABEL_CLASSFYCODE.equals(classifyCode))
                            )) {
                        newaction.put(Common.HIDDEN, true);
                    }
                    selectedActions.add(newaction);
                }
            }else {
                if (Common.MODULE_TASK_TABLE_MY.equals(table)) {
                    if (performerIsMe) {
                        if (Common.MODULE_TASK_STATE_RECEIVE.equals(taskState) &&
                                (ActionEnum.PAUSE.getKey().equals(candidateAction) ||
                                        ActionEnum.REVERSE.getKey().equals(candidateAction) ||
                                        ActionEnum.RECALL.getKey().equals(candidateAction) ||
                                        ActionEnum.COMMIT.getKey().equals(candidateAction) ||
                                        ActionEnum.ENTER.getKey().equals(candidateAction))) {
                            newaction.put(Common.HIDDEN, true);
                        } else if (Common.MODULE_TASK_STATE_START.equals(taskState) &&
                                (ActionEnum.START.getKey().equals(candidateAction) ||
                                        ActionEnum.RECALL.getKey().equals(candidateAction) ||
                                        (ActionEnum.COMMIT.getKey().equals(candidateAction) &&
                                                Common.MODULE_TASK_LABEL_CLASSFYCODE.equals(classifyCode))||
                                        (ActionEnum.ENTER.getKey().equals(candidateAction) &&
                                                !Common.MODULE_TASK_LABEL_CLASSFYCODE.equals(classifyCode))||
                                        ActionEnum.REVERSE.getKey().equals(candidateAction))) {
                            newaction.put(Common.HIDDEN, true);
                        } else if (Common.MODULE_TASK_STATE_PAUSE.equals(taskState) &&
                                (ActionEnum.PAUSE.getKey().equals(candidateAction) ||
                                        ActionEnum.RECALL.getKey().equals(candidateAction) ||
                                        (ActionEnum.COMMIT.getKey().equals(candidateAction) &&
                                                Common.MODULE_TASK_LABEL_CLASSFYCODE.equals(classifyCode))||
                                        (ActionEnum.ENTER.getKey().equals(candidateAction) &&
                                                !Common.MODULE_TASK_LABEL_CLASSFYCODE.equals(classifyCode))||
                                        ActionEnum.REVERSE.getKey().equals(candidateAction)
                                )) {
                            newaction.put(Common.HIDDEN, true);
                        } else if (Common.MODULE_TASK_STATE_COMPLETE.equals(taskState) &&
                                (ActionEnum.START.getKey().equals(candidateAction) ||
                                        ActionEnum.COMMIT.getKey().equals(candidateAction) ||
                                        (ActionEnum.ENTER.getKey().equals(candidateAction) &&
                                                !Common.MODULE_TASK_LABEL_CLASSFYCODE.equals(classifyCode))||
                                        ActionEnum.PAUSE.getKey().equals(candidateAction)
                                )) {
                            newaction.put(Common.HIDDEN, true);
                        } else if (Common.MODULE_TASK_STATE_CLOSED.equals(taskState) &&
                                (ActionEnum.START.getKey().equals(candidateAction) ||
                                        ActionEnum.PAUSE.getKey().equals(candidateAction) ||
                                        ActionEnum.RECALL.getKey().equals(candidateAction) ||
                                        ActionEnum.COMMIT.getKey().equals(candidateAction) ||
                                        (ActionEnum.ENTER.getKey().equals(candidateAction) &&
                                                !Common.MODULE_TASK_LABEL_CLASSFYCODE.equals(classifyCode))||
                                        ActionEnum.REVERSE.getKey().equals(candidateAction)||
                                        ActionEnum.INVALIDATE.getKey().equals(candidateAction)
                                )) {
                            newaction.put(Common.HIDDEN, true);
                        }
                        selectedActions.add(newaction);

                    } else {
                        if (Common.MODULE_TASK_STATE_COMPLETE.equals(taskState) &&
                                (ActionEnum.START.getKey().equals(candidateAction) ||
                                        ActionEnum.PAUSE.getKey().equals(candidateAction) ||
                                        ActionEnum.COMMIT.getKey().equals(candidateAction) ||
                                        (ActionEnum.ENTER.getKey().equals(candidateAction) &&
                                                !Common.MODULE_TASK_LABEL_CLASSFYCODE.equals(classifyCode))||
                                        ActionEnum.RECALL.getKey().equals(candidateAction)
                                )) {
                            newaction.put(Common.HIDDEN, true);
                        }
                        selectedActions.add(newaction);
                    }

                } else {
                    if (Common.MODULE_TASK_STATE_CLOSED.equals(taskState) &&
                            (ActionEnum.INVALIDATE.getKey().equals(candidateAction)
                            )) {
                        newaction.put(Common.HIDDEN, true);
                    }

//                    if (!Common.MODULE_TASK_STATE_CLOSED.equals(taskState) &&
//                            ActionEnum.ENTER.getKey().equals(candidateAction)){
//                        newaction.put(Common.HIDDEN, true);
//                    }
                    selectedActions.add(newaction);
                }

            }
        }
        return selectedActions;
    }

    public List<Map<String, Object>> getActions1(OperationalRole operationalRole, String table, String taskState,
                                                boolean performerIsMe,boolean taskownerIsMe,String classifyCode) {

        List<Map<String, Object>> selectedActions = new ArrayList<>();

        List<String> candidateActions = taskactins.get(table);

        for (String candidateAction : candidateActions) {

            Map<String, Object> action = actins.get(Common.MODULE_TASK + "_" + candidateAction);

            Map<String, Object> newaction = new HashMap<>(action);
            if ((!taskownerIsMe || OperationalRole.ADMIN != operationalRole) && ActionEnum.INVALIDATE.getKey().equals(candidateAction)){
                newaction.put(Common.HIDDEN, true);
            }

            if (OperationalRole.MEMBER == operationalRole) {
                if (Common.MODULE_TASK_TABLE_PUBLIC.equals(table) && public_member.contains(candidateAction)) {
                    selectedActions.add(newaction);
                } else if (Common.MODULE_TASK_TABLE_MY.equals(table) && my_member.contains(candidateAction)) {
                    if (Common.MODULE_TASK_STATE_RECEIVE.equals(taskState) &&
                            (ActionEnum.PAUSE.getKey().equals(candidateAction) ||
                                    ActionEnum.RECALL.getKey().equals(candidateAction) ||
                                    ActionEnum.COMMIT.getKey().equals(candidateAction) ||
                                    ActionEnum.ENTER.getKey().equals(candidateAction))) {
                        newaction.put(Common.HIDDEN, true);
                    } else if (Common.MODULE_TASK_STATE_START.equals(taskState) &&
                            (ActionEnum.START.getKey().equals(candidateAction) ||
                                    (ActionEnum.COMMIT.getKey().equals(candidateAction) &&
                                            Common.MODULE_TASK_LABEL_CLASSFYCODE.equals(classifyCode))||
                                    (ActionEnum.ENTER.getKey().equals(candidateAction) &&
                                            !Common.MODULE_TASK_LABEL_CLASSFYCODE.equals(classifyCode))||
                                    ActionEnum.RECALL.getKey().equals(candidateAction))) {
                        newaction.put(Common.HIDDEN, true);
                    } else if (Common.MODULE_TASK_STATE_PAUSE.equals(taskState) &&
                            (ActionEnum.PAUSE.getKey().equals(candidateAction) ||
                                    (ActionEnum.COMMIT.getKey().equals(candidateAction) &&
                                            Common.MODULE_TASK_LABEL_CLASSFYCODE.equals(classifyCode))||
                                    (ActionEnum.ENTER.getKey().equals(candidateAction) &&
                                            !Common.MODULE_TASK_LABEL_CLASSFYCODE.equals(classifyCode))||
                                    ActionEnum.RECALL.getKey().equals(candidateAction)
                            )) {
                        newaction.put(Common.HIDDEN, true);
                    } else if (Common.MODULE_TASK_STATE_COMPLETE.equals(taskState) &&
                            (ActionEnum.START.getKey().equals(candidateAction) ||
                                    ActionEnum.COMMIT.getKey().equals(candidateAction) ||
                                    (ActionEnum.ENTER.getKey().equals(candidateAction) &&
                                            !Common.MODULE_TASK_LABEL_CLASSFYCODE.equals(classifyCode))||
                                    ActionEnum.PAUSE.getKey().equals(candidateAction)
                            )) {
                        newaction.put(Common.HIDDEN, true);
                    } else if (Common.MODULE_TASK_STATE_CLOSED.equals(taskState) &&
                            (ActionEnum.START.getKey().equals(candidateAction) ||
                                    ActionEnum.PAUSE.getKey().equals(candidateAction) ||
                                    ActionEnum.RECALL.getKey().equals(candidateAction)||
                                    ActionEnum.COMMIT.getKey().equals(candidateAction) ||
                                    (ActionEnum.ENTER.getKey().equals(candidateAction) &&
                                            !Common.MODULE_TASK_LABEL_CLASSFYCODE.equals(classifyCode))||
                                    ActionEnum.INVALIDATE.getKey().equals(candidateAction)
                            )) {
                        newaction.put(Common.HIDDEN, true);
                    }
                    selectedActions.add(newaction);
                }
            } else {
                if (Common.MODULE_TASK_TABLE_MY.equals(table)) {
                    if (performerIsMe) {
                        if (Common.MODULE_TASK_STATE_RECEIVE.equals(taskState) &&
                                (ActionEnum.PAUSE.getKey().equals(candidateAction) ||
                                        ActionEnum.REVERSE.getKey().equals(candidateAction) ||
                                        ActionEnum.RECALL.getKey().equals(candidateAction) ||
                                        ActionEnum.COMMIT.getKey().equals(candidateAction) ||
                                        ActionEnum.ENTER.getKey().equals(candidateAction))) {
                            newaction.put(Common.HIDDEN, true);
                        } else if (Common.MODULE_TASK_STATE_START.equals(taskState) &&
                                (ActionEnum.START.getKey().equals(candidateAction) ||
                                        ActionEnum.RECALL.getKey().equals(candidateAction) ||
                                        (ActionEnum.COMMIT.getKey().equals(candidateAction) &&
                                                Common.MODULE_TASK_LABEL_CLASSFYCODE.equals(classifyCode))||
                                        (ActionEnum.ENTER.getKey().equals(candidateAction) &&
                                                !Common.MODULE_TASK_LABEL_CLASSFYCODE.equals(classifyCode))||
                                        ActionEnum.REVERSE.getKey().equals(candidateAction))) {
                            newaction.put(Common.HIDDEN, true);
                        } else if (Common.MODULE_TASK_STATE_PAUSE.equals(taskState) &&
                                (ActionEnum.PAUSE.getKey().equals(candidateAction) ||
                                        ActionEnum.RECALL.getKey().equals(candidateAction) ||
                                        (ActionEnum.COMMIT.getKey().equals(candidateAction) &&
                                                Common.MODULE_TASK_LABEL_CLASSFYCODE.equals(classifyCode))||
                                        (ActionEnum.ENTER.getKey().equals(candidateAction) &&
                                                !Common.MODULE_TASK_LABEL_CLASSFYCODE.equals(classifyCode))||
                                        ActionEnum.REVERSE.getKey().equals(candidateAction)
                                )) {
                            newaction.put(Common.HIDDEN, true);
                        } else if (Common.MODULE_TASK_STATE_COMPLETE.equals(taskState) &&
                                (ActionEnum.START.getKey().equals(candidateAction) ||
                                        ActionEnum.COMMIT.getKey().equals(candidateAction) ||
                                        (ActionEnum.ENTER.getKey().equals(candidateAction) &&
                                                !Common.MODULE_TASK_LABEL_CLASSFYCODE.equals(classifyCode))||
                                        ActionEnum.PAUSE.getKey().equals(candidateAction)
                                )) {
                            newaction.put(Common.HIDDEN, true);
                        } else if (Common.MODULE_TASK_STATE_CLOSED.equals(taskState) &&
                                (ActionEnum.START.getKey().equals(candidateAction) ||
                                        ActionEnum.PAUSE.getKey().equals(candidateAction) ||
                                        ActionEnum.RECALL.getKey().equals(candidateAction) ||
                                        ActionEnum.COMMIT.getKey().equals(candidateAction) ||
                                        (ActionEnum.ENTER.getKey().equals(candidateAction) &&
                                                !Common.MODULE_TASK_LABEL_CLASSFYCODE.equals(classifyCode))||
                                        ActionEnum.REVERSE.getKey().equals(candidateAction)||
                                        ActionEnum.INVALIDATE.getKey().equals(candidateAction)
                                )) {
                            newaction.put(Common.HIDDEN, true);
                        }
                        selectedActions.add(newaction);

                    } else {
                        if (Common.MODULE_TASK_STATE_COMPLETE.equals(taskState) &&
                                (ActionEnum.START.getKey().equals(candidateAction) ||
                                        ActionEnum.PAUSE.getKey().equals(candidateAction) ||
                                        ActionEnum.COMMIT.getKey().equals(candidateAction) ||
                                        (ActionEnum.ENTER.getKey().equals(candidateAction) &&
                                                !Common.MODULE_TASK_LABEL_CLASSFYCODE.equals(classifyCode))||
                                        ActionEnum.RECALL.getKey().equals(candidateAction)
                                )) {
                            newaction.put(Common.HIDDEN, true);
                        }
                        selectedActions.add(newaction);
                    }

                } else {
                    if (Common.MODULE_TASK_STATE_CLOSED.equals(taskState) &&
                            (ActionEnum.INVALIDATE.getKey().equals(candidateAction)
                            )) {
                        newaction.put(Common.HIDDEN, true);
                    }
                    if (!Common.MODULE_TASK_STATE_CLOSED.equals(taskState) &&
                            ActionEnum.ENTER.getKey().equals(candidateAction)){
                        newaction.put(Common.HIDDEN, true);
                    }
                    selectedActions.add(newaction);
                }

            }
        }
        return selectedActions;
    }

    public static void main(String[] args){

        TaskAuxiliaryCalculation taskAuxiliaryCalculation = new TaskAuxiliaryCalculation();
        List<Map<String,Object>> tasks = new ArrayList<>();
        Map<String,Object> task1 = new HashMap<>();
        String table = Common.MODULE_TASK_TABLE_RELATED;

        task1.put("taskTile","任务1");
        task1.put("performerid","");
        task1.put("taskownerid","2");
        task1.put("taskstate",Common.MODULE_TASK_STATE_CREATE);
        if (Common.MODULE_TASK_TABLE_PUBLIC.equals(table)){

            tasks.add(task1);
        }



        Map<String,Object> task2 = new HashMap<>();
        task2.put("taskTile","任务2");
        task2.put("performerid","1");
        task2.put("taskownerid","2");
        task2.put("taskstate",Common.MODULE_TASK_STATE_RECEIVE);
        if (Common.MODULE_TASK_TABLE_RELATED.equals(table)) {
            tasks.add(task2);
        }


        Map<String,Object> task3 = new HashMap<>();
        task3.put("taskTile","任务3");
        task3.put("performerid","1");
        task3.put("taskownerid","2");
        task3.put("taskstate",Common.MODULE_TASK_STATE_PAUSE);
        if (Common.MODULE_TASK_TABLE_RELATED.equals(table)) {

           tasks.add(task3);
        }

        Map<String,Object> task4 = new HashMap<>();
        task4.put("taskTile","任务4");
        task4.put("performerid","1");
        task4.put("taskownerid","2");
        task4.put("taskstate",Common.MODULE_TASK_STATE_CLOSED);
        if (Common.MODULE_TASK_TABLE_RELATED.equals(table)) {

            tasks.add(task4);
        }


        //List<Map<String,Object>> newtasks = taskAuxiliaryCalculation.getActions(tasks,"2",Common.USER_ROLE_MANAGER,table);
        System.out.println("OK");

    }

}


