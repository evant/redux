package me.tatarka.redux.sample;

import java.util.Objects;

import me.tatarka.redux.Store;
import me.tatarka.redux.middleware.Middleware;

public class LogMiddleware<S> implements Middleware<S> {

    private Store<S> store;

    @Override
    public void create(Store<S> store) {
        this.store = store;
    }

    @Override
    public void dispatch(Next next, Object action) {
        String before = Objects.toString(store.state());
        next.next(action);
        String after = Objects.toString(store.state());
        System.out.println(before + " -> " + action + " -> " + after);
    }
}
