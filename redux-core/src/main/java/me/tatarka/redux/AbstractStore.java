package me.tatarka.redux;

import me.tatarka.redux.middleware.Middleware;
import me.tatarka.redux.middleware.MiddlewareFactory;

/**
 * Allows you to easily implement a store that runs a {@link Reducer} and {@link Middleware}. You
 * provide the backing storage for the state, returning it in {@link #state()} and updating it in
 * {@link #setState(Object)}.
 */
public abstract class AbstractStore<A, S> implements Store<A, S> {

    private final Middleware<A>[] middleware;
    private Middleware<A> end;

    @SafeVarargs
    public AbstractStore(S initialState, final Reducer<A, S> reducer, MiddlewareFactory<A, S>... middleware) {
        if (reducer == null) {
            throw new NullPointerException("reducer == null");
        }
        this.middleware = new Middleware[middleware.length];
        if (middleware.length > 0) {
            ProxyStore proxyStore = new ProxyStore(initialState);
            for (int i = 0; i < middleware.length; i++) {
                MiddlewareFactory<A, S> m = middleware[i];
                this.middleware[i] = m.create(proxyStore);
            }
            proxyStore.inConstructor = false;
        }
        end = new Middleware<A>() {
            @Override
            public void dispatch(Next<A> next, A action) {
                setState(reducer.reduce(action, state()));
            }
        };
    }

    /**
     * Returns the current state of the store.
     */
    @Override
    public abstract S state();

    /**
     * Sets the state of the store. Warning! You should not call this in normal application code,
     * instead preferring to update it through dispatching an action. It is however, useful for
     * tests.
     */
    @Override
    public abstract void setState(S newState);

    /**
     * Dispatch an action to update the store by running the reducer and middleware.
     */
    @Override
    public final void dispatch(A action) {
        if (action == null) {
            throw new NullPointerException("action == null");
        }
        dispatch(action, 0);
    }

    private void dispatch(A action, final int index) {
        if (index >= middleware.length) {
            end.dispatch(null, action);
        } else {
            middleware[index].dispatch(new Middleware.Next<A>() {
                @Override
                public void next(A r) {
                    AbstractStore.this.dispatch(r, index + 1);
                }
            }, action);
        }
    }

    private class ProxyStore implements Store<A, S> {
        final S initialState;
        boolean inConstructor = true;

        ProxyStore(S initialState) {
            this.initialState = initialState;
        }

        @Override
        public S state() {
            return inConstructor ? initialState : AbstractStore.this.state();
        }

        @Override
        public void setState(S state) {
            if (inConstructor) {
                throw new IllegalStateException("cannot set state while store is being constructed");
            }
            AbstractStore.this.setState(state);
        }

        @Override
        public void dispatch(A action) {
            if (inConstructor) {
                throw new IllegalStateException("cannot dispatch while store is being constructed");
            } else {
                AbstractStore.this.dispatch(action);
            }
        }
    }
}

