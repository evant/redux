package me.tatarka.redux.sample;

import java.util.Objects;

import me.tatarka.redux.Dispatcher;
import me.tatarka.redux.Store;
import me.tatarka.redux.middleware.Middleware;

public class LogMiddleware<S, A, R> implements Middleware<A, R> {

    private final Store<S> store;

    public LogMiddleware(Store<S> store) {
        this.store = store;
    }

    @Override
    public R dispatch(Dispatcher<A, R> dispatcher, Next<A, R> next, A action) {
        String before = Objects.toString(store.getState());
        R result = next.next(action);
        String after = Objects.toString(store.getState());
        System.out.println(before + " -> " + action + " -> " + after);
        return result;
    }
}
