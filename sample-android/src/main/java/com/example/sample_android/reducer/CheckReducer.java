package com.example.sample_android.reducer;

import com.example.sample_android.action.Check;
import com.example.sample_android.state.TodoItem;

import me.tatarka.redux.Reducer;

public class CheckReducer implements Reducer<Check, TodoItem> {

    @Override
    public TodoItem reduce(Check action, TodoItem item) {
        return TodoItem.create(item.id(), item.text(), action.checked());
    }
}
