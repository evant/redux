package com.example.sample_android;

import android.content.Context;
import com.example.sample_android.action.Action;
import com.example.sample_android.state.TodoList;
import com.example.sample_android.store.MainStore;
import me.tatarka.redux.ReplayMiddleware;
import me.tatarka.redux.StateLoader;

public class TodoStateLoader extends StateLoader<TodoList, MainStore> {

    public TodoStateLoader(Context context) {
        super(context);
    }

    @Override
    protected MainStore onCreateStore() {
        return new MainStore(getContext());
    }

    public ReplayMiddleware<TodoList, Action, Action> replayMiddleware() {
        // ensure store is created.
        MainStore store = store();
        return store.replayMiddleware();
    }
}
