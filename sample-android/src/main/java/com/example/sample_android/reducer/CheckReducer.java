package com.example.sample_android.reducer;

import com.example.sample_android.action.Check;
import com.example.sample_android.state.TodoItem;

import me.tatarka.redux.Reducer;

public class CheckReducer implements Reducer<TodoItem, Check> {

    @Override
    public TodoItem reduce(TodoItem item, Check action) {
        return TodoItem.create(item.id(), item.text(), action.checked());
    }
}
