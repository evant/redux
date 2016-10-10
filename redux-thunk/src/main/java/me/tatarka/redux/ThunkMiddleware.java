package me.tatarka.redux;

import me.tatarka.redux.middleware.Middleware;

public class ThunkMiddleware<S> implements Middleware<S> {

    private Store<S> store;

    @Override
    public void create(final Store<S> store) {
        this.store = store;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void dispatch(Next next, Object action) {
        if (action instanceof Thunk) {
            Thunk<S> thunk = (Thunk) action;
            thunk.run(store);
        } else {
            next.next(action);
        }
    }
}
