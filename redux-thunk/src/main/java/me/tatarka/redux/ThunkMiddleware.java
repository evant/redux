package me.tatarka.redux;

import me.tatarka.redux.middleware.Middleware;
import me.tatarka.redux.middleware.MiddlewareFactory;

public class ThunkMiddleware<A, S> implements MiddlewareFactory<A, S> {

    @Override
    public Middleware<A> create(final Store<A, S> store) {
        return new Middleware<A>() {
            @Override
            public void dispatch(Next<A> next, A action) {
                if (action instanceof Thunk) {
                    Thunk<A, S> thunk = (Thunk<A, S>) action;
                    thunk.run(store);
                } else {
                    next.next(action);
                }
            }
        };
    }
}
