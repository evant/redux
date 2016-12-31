package com.example.sample_android.reducer;

import com.example.sample_android.action.*;
import com.example.sample_android.state.TodoItem;
import com.example.sample_android.state.TodoList;

import java.util.ArrayList;
import java.util.List;

import me.tatarka.redux.Reducer;
import me.tatarka.redux.Reducers;

public class TodoListReducers {

    public static Reducer<Action, TodoList> reducer() {
        return Reducers.<Action, TodoList>matchClass()
                .when(Load.class, new LoadReducer())
                .when(Add.class, new AddReducer())
                .when(Remove.class, new RemoveReducer())
                .when(Edit.class, updateItem(new EditReducer()))
                .when(Check.class, updateItem(new CheckReducer()));
    }

    public static <A extends UpdateItem> Reducer<A, TodoList> updateItem(final Reducer<A, TodoItem> reducer) {
        return new Reducer<A, TodoList>() {
            @Override
            public TodoList reduce(A action, TodoList state) {
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
                    items.set(index, reducer.reduce(action, item));
                }
                return TodoList.create(state.loading(), items);
            }
        };
    }
}
