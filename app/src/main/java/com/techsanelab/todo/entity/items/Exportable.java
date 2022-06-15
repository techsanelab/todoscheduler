package com.techsanelab.todo.entity.items;

public interface Exportable {
    Object getContent();
    void setContent(Object content);
    String getDescription();
}
