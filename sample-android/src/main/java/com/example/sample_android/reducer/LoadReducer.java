package com.example.sample_android.reducer;

import com.example.sample_android.action.Load;
import com.example.sample_android.state.TodoList;

import me.tatarka.redux.Reducer;

public class LoadReducer implements Reducer<Load, TodoList> {
    @Override
    public TodoList reduce(Load action, TodoList state) {
        return TodoList.create(false, action.items());
    }
}
