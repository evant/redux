package com.example.sample_android.middleware;

import com.example.sample_android.Datastore;
import com.example.sample_android.state.TodoList;

import me.tatarka.redux.Store;
import me.tatarka.redux.middleware.Middleware;
import me.tatarka.redux.middleware.MiddlewareFactory;

public class PersistenceMiddleware implements MiddlewareFactory<Object, TodoList> {

    private final Datastore datastore;

    public PersistenceMiddleware(Datastore datastore) {
        this.datastore = datastore;
    }

    @Override
    public Middleware<Object> create(final Store<Object, TodoList> store) {
        return new Middleware<Object>() {
            @Override
            public void dispatch(Next<Object> next, Object action) {
                next.next(action);
                datastore.store(store.state().items());
            }
        };
    }
}
