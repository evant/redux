package me.tatarka.redux;

import me.tatarka.redux.middleware.Middleware;

/**
 * Allows you to easily implement a store that runs a {@link Reducer} and {@link Middleware}. You
 * provide the backing storage for the state, returning it in {@link #state()} and updating it in
 * {@link #setState(Object)}.
 */
public abstract class AbstractStore<A, S> implements Store<A, S> {

    private final Middleware<A, S>[] middleware;
    private final Middleware.Next<A>[] next;
    private Middleware<A, S> end;

    @SafeVarargs
    public AbstractStore(S initialState, final Reducer<A, S> reducer, Middleware<A, S>... middleware) {
        if (reducer == null) {
            throw new NullPointerException("reducer == null");
        }
        this.middleware = new Middleware[middleware.length];
        this.next = new Middleware.Next[middleware.length];
        if (middleware.length > 0) {
            ProxyStore proxyStore = new ProxyStore(initialState);
            for (int i = 0; i < middleware.length; i++) {
                final int index = i;
                Middleware<A, S> m = middleware[index];
                m.create(proxyStore);
                this.middleware[index] = m;
                this.next[index] = new Middleware.Next<A>() {
                    @Override
                    public void next(A action) {
                        dispatch(action, index + 1);
                    }
                };
            }
            proxyStore.inConstructor = false;
        }
        end = new Middleware<A, S>() {
            @Override
            public void create(Store<A, S> store) {

            }

            @Override
            public void dispatch(Next<A> next, A action) {
                setState(reducer.reduce(action, state()));
            }
        };
    }

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
            middleware[index].dispatch(next[index], action);
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

