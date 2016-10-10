package me.tatarka.redux;

import me.tatarka.redux.middleware.Middleware;

public class ThunkMiddleware<A, S> implements Middleware<A, S> {

    private Store<A, S> store;

    @Override
    public void create(final Store<A, S> store) {
        this.store = store;
    }

    @Override
    public void dispatch(Next<A> next, A action) {
        if (action instanceof Thunk) {
            Thunk<A, S> thunk = (Thunk<A, S>) action;
            thunk.run(store);
        } else {
            next.next(action);
        }
    }
}
