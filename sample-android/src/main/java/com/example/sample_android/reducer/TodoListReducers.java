package com.example.sample_android.reducer;

import com.example.sample_android.action.*;
import com.example.sample_android.state.TodoItem;
import com.example.sample_android.state.TodoList;

import java.util.ArrayList;
import java.util.List;

import me.tatarka.redux.Reducer;
import me.tatarka.redux.Reducers;

public class TodoListReducers {

    public static Reducer<TodoList, Action> reducer() {
        return Reducers.<TodoList, Action>matchClass()
                .when(Load.class, new LoadReducer())
                .when(Add.class, new AddReducer())
                .when(Remove.class, new RemoveReducer())
                .when(Edit.class, updateItem(new EditReducer()))
                .when(Check.class, updateItem(new CheckReducer()));
    }

    public static <A extends UpdateItem> Reducer<TodoList, A> updateItem(final Reducer<TodoItem, A> reducer) {
        return new Reducer<TodoList, A>() {
            @Override
            public TodoList reduce(TodoList state, A action) {
                List<TodoItem> items = new ArrayList<>(state.items());
                int index = -1;
                TodoItem item = null;
                for (int i = 0; i < items.size(); i++) {
                    TodoItem todoItem = items.get(i);
                    if (todoItem.id() == action.id()) {
                        item = todoItem;
                        index = i;
                        break;
                    }
                }
                if (item != null) {
                    items.set(index, reducer.reduce(item, action));
                }
                return TodoList.create(state.loading(), items);
            }
        };
    }
}
