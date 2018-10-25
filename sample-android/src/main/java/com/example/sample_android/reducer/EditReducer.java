package com.example.sample_android.reducer;

import com.example.sample_android.action.Edit;
import com.example.sample_android.state.TodoItem;

import me.tatarka.redux.Reducer;

public class EditReducer implements Reducer<TodoItem, Edit> {

    @Override
    public TodoItem reduce(TodoItem item, Edit action) {
        return TodoItem.create(item.id(), action.text(), item.done());
    }
}
