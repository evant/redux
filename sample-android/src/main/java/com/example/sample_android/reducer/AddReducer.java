package com.example.sample_android.reducer;

import com.example.sample_android.action.Add;
import com.example.sample_android.state.TodoItem;
import com.example.sample_android.state.TodoList;

import java.util.ArrayList;
import java.util.List;

import me.tatarka.redux.Reducer;

public class AddReducer implements Reducer<TodoList, Add> {
    @Override
    public TodoList reduce(TodoList state, Add action) {
        List<TodoItem> items = new ArrayList<>(state.items());
        int id = items.isEmpty() ? 0 : items.get(items.size() - 1).id() + 1;
        String text = action.text();
        items.add(TodoItem.create(id, text, /*done=*/false));
        return TodoList.create(state.loading(), items);
    }
}
