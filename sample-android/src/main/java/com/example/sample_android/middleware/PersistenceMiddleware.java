package com.example.sample_android.middleware;

import com.example.sample_android.Datastore;
import com.example.sample_android.state.TodoList;

import me.tatarka.redux.Dispatcher;
import me.tatarka.redux.Store;
import me.tatarka.redux.middleware.Middleware;

public class PersistenceMiddleware<A, R> implements Middleware<A, R> {

    private final Store<TodoList> store;
    private final Datastore datastore;

    public PersistenceMiddleware(Store<TodoList> store, Datastore datastore) {
        this.store = store;
        this.datastore = datastore;
    }

    @Override
    public R dispatch(Dispatcher<A, R> dispatcher, Next<A, R> next, A action) {
        R result = next.next(action);
        datastore.store(store.getState().items());
        return result;
    }
}
