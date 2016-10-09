package com.example.sample_android;

import android.content.Context;

import com.example.sample_android.middleware.PersistenceMiddleware;
import com.example.sample_android.reducer.TodoListReducers;
import com.example.sample_android.state.TodoList;

import me.tatarka.redux.ListenerStore;
import me.tatarka.redux.LogMiddleware;
import me.tatarka.redux.ReplayMiddleware;
import me.tatarka.redux.StateLoader;
import me.tatarka.redux.Store;
import me.tatarka.redux.ThunkMiddleware;

public class TodoStateLoader extends StateLoader<TodoList> {

    private static final ReplayMiddleware<Object, TodoList> REPLAY_MIDDLEWARE = new ReplayMiddleware<>();

    public TodoStateLoader(Context context) {
        super(context, ListenerStore.create(
                TodoList.initial(),
                TodoListReducers.reducer(),
                new ThunkMiddleware<Object, TodoList>(),
                new LogMiddleware<Object, TodoList>("ACTION"),
                new PersistenceMiddleware(new Datastore(context)),
                REPLAY_MIDDLEWARE
        ));
    }

    public Store<Object, TodoList> store() {
        return (Store<Object, TodoList>) store;
    }

    public ReplayMiddleware<Object, TodoList> replayMiddleware() {
        return REPLAY_MIDDLEWARE;
    }
}
