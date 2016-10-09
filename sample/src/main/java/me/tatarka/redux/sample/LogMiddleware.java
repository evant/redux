package me.tatarka.redux.sample;

import me.tatarka.redux.Store;
import me.tatarka.redux.middleware.Middleware;
import me.tatarka.redux.middleware.MiddlewareFactory;

import java.util.Objects;

public class LogMiddleware<A, S> implements MiddlewareFactory<A, S> {

    @Override
    public Middleware<A> create(Store<A, S> store) {
        return (next, action) -> {
            String before = Objects.toString(store.state());
            next.next(action);
            String after = Objects.toString(store.state());
            System.out.println(before + " -> " + action + " -> " + after);
        };
    }
}
