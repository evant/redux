package com.example.sample_android.reducer;

import com.example.sample_android.action.Remove;
import com.example.sample_android.state.TodoItem;
import com.example.sample_android.state.TodoList;

import java.util.ArrayList;
import java.util.List;

import me.tatarka.redux.Reducer;

public class RemoveReducer implements Reducer<TodoList, Remove> {
    @Override
    public TodoList reduce(TodoList state, Remove action) {
        List<TodoItem> items = new ArrayList<>(state.items());
        for (int i = 0; i < items.size(); i++) {
            TodoItem item = items.get(i);
            if (item.id() == action.id()) {
                items.remove(i);
                break;
            }
        }
        return TodoList.create(state.loading(), items);
    }
}
