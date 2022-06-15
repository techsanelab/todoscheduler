package com.techsanelab.todo;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import com.techsanelab.todo.entity.items.TodoItem;

public class TodoViewModel extends ViewModel {

    private List<TodoItem> todoItems;
    public List<TodoItem> getTodos(DBHelper dbHelper,String date) {
        if (todoItems == null) {
            todoItems = new ArrayList<>();
            loadUsers(dbHelper,date);
        }
        return todoItems;
    }

    private void loadUsers(DBHelper dbHelper,String date) {
        // Do an asynchronous operation to fetch users.
        todoItems = dbHelper.selectTodoByStartDate(date);
    }


}
