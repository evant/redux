package me.tatarka.redux.sample;

import java.util.Objects;

import me.tatarka.redux.Store;
import me.tatarka.redux.middleware.Middleware;

public class LogMiddleware<A, S> implements Middleware<A, S> {

    private Store<A, S> store;

    @Override
    public void create(Store<A, S> store) {
        this.store = store;
    }

    @Override
    public void dispatch(Next<A> next, A action) {
        String before = Objects.toString(store.state());
        next.next(action);
        String after = Objects.toString(store.state());
        System.out.println(before + " -> " + action + " -> " + after);
    }
}
