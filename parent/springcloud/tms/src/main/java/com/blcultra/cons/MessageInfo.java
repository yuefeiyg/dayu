package com.blcultra.cons;

import com.alibaba.fastjson.JSONObject;

public class MessageInfo {

    public static String actionInfo(String action, String stateCode){

        StringBuilder message = new StringBuilder(action);
        if (Messageconstant.REQUEST_SUCCESSED_CODE.equals(stateCode)){
            message.append(Messageconstant.ACTION_SUCCESSED_MESSAGE);
        }else {
            message.append(Messageconstant.ACTION_FAILED_MESSAGE);
        }

        return message.toString();
    }



//    public static String responseInfo(String module,String message,String stateCode){
//
//        if (MessageType.ACTION == type){
//            actionInfo(message,stateCode);
//        }else(MessageType.BUSINESS == type){
//            return null;
//
//
//        }
//
//
//
//
//    }

    public static void main(String[] args){
        JSONObject object = new JSONObject();
        object.put("a","aa");

        System.out.println();

        JSONObject object1 = new JSONObject();
        object1.put("b","bb");
    }




}
