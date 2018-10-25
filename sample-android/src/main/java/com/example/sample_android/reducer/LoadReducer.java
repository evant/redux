package com.example.sample_android.reducer;

import com.example.sample_android.action.Load;
import com.example.sample_android.state.TodoList;

import me.tatarka.redux.Reducer;

public class LoadReducer implements Reducer<TodoList, Load> {
    @Override
    public TodoList reduce(TodoList state, Load action) {
        return TodoList.create(false, action.items());
    }
}
