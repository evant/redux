package com.example.sample_android;

import android.content.Context;

import com.example.sample_android.middleware.PersistenceMiddleware;
import com.example.sample_android.reducer.TodoListReducers;
import com.example.sample_android.state.TodoList;

import me.tatarka.redux.LogMiddleware;
import me.tatarka.redux.ObservableStore;
import me.tatarka.redux.ReplayMiddleware;
import me.tatarka.redux.StateLoader;
import me.tatarka.redux.ThunkMiddleware;

public class TodoStateLoader extends StateLoader<TodoList> {

    private final ReplayMiddleware<TodoList> replayMiddleware = new ReplayMiddleware<>();

    public TodoStateLoader(Context context) {
        super(context);
    }

    @Override
    protected ObservableStore<TodoList> onCreateStore() {
        return new ObservableStore<>(
                TodoList.initial(),
                TodoListReducers.reducer(),
                new ThunkMiddleware<TodoList>(),
                new LogMiddleware<TodoList>("ACTION"),
                new PersistenceMiddleware(new Datastore(getContext())),
                replayMiddleware
        );
    }

    public ReplayMiddleware<TodoList> replayMiddleware() {
        // ensure store is created.
        store();
        return replayMiddleware;
    }
}
