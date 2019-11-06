package com.blcultra.service;

import java.util.Map;

public interface MessageService {

    String getUnreadNum();

    String getUnreadNotes();

    String readMessage(Map<String, Object> map);
}
