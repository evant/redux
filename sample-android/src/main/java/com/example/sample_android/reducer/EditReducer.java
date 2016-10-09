package com.example.sample_android.reducer;

import com.example.sample_android.action.Edit;
import com.example.sample_android.state.TodoItem;

import me.tatarka.redux.Reducer;

public class EditReducer implements Reducer<Edit, TodoItem> {

    @Override
    public TodoItem reduce(Edit action, TodoItem item) {
        return TodoItem.create(item.id(), action.text(), item.done());
    }
}
