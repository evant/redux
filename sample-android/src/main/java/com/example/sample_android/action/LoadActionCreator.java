package com.example.sample_android.action;

import com.example.sample_android.Datastore;
import com.example.sample_android.state.TodoItem;
import com.example.sample_android.state.TodoList;

import java.util.List;

import me.tatarka.redux.Store;
import me.tatarka.redux.Thunk;

public class LoadActionCreator {

    private final Datastore datastore;

    public LoadActionCreator(Datastore datastore) {
        this.datastore = datastore;
    }

    public Thunk<TodoList> load() {
        return new Thunk<TodoList>() {
            @Override
            public void run(final Store<TodoList> store) {
                datastore.get(new Datastore.Callback() {
                    @Override
                    public void onList(List<TodoItem> items) {
                        store.dispatch(Load.create(items));
                    }
                });
            }
        };
    }
}
