package com.blcultra.service;

import java.util.Map;

public interface TaskInfoNoteService {

    String addTaskNote(Map<String, Object> note);

    String getNotesByTask(Map<String, Object> map);

    String updateNote(Map<String, Object> noteinfo);

    String deleteNote(String noteid);
}
