package me.tatarka.redux;

import me.tatarka.redux.middleware.Middleware;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Dispatches actions with {@link Reducer}s and updates the state in the {@link Store}. You can chain {@link Middleware}
 * to implement cross-cutting concerns.
 *
 * @param <A> The action type.
 * @param <R> The return type of the dispatcher. This is implementation-defined.
 */
public abstract class Dispatcher<A, R> {

    /**
     * Constructs a {@code Dispatcher} from the given {@link Store} and {@link Reducer}. {@link #dispatch(Object)} will
     * run the action through the reducer and update the store with the resulting state.
     *
     * @param <S> The type of state in the store.
     * @param <A> The type of action.
     * @return the action dispatched.
     */
    public static <S, A> Dispatcher<A, A> forStore(final Store<S> store, final Reducer<A, S> reducer) {
        if (store == null) {
            throw new NullPointerException("store==null");
        }
        if (reducer == null) {
            throw new NullPointerException("reducer==null");
        }
        return new Dispatcher<A, A>() {
            @Override
            public A dispatch(A action) {
                store.setState(reducer.reduce(action, store.getState()));
                return action;
            }
        };
    }

    /**
     * Dispatches the given action.
     */
    public abstract R dispatch(A action);

    /**
     * Returns a new {@code Dispatcher} that runs the given {@link Middleware}.
     */
    public final Dispatcher<A, R> chain(final Middleware<A, R> middleware) {
        if (middleware == null) {
            throw new NullPointerException("middleware==null");
        }
        return new Dispatcher<A, R>() {
            @Override
            public R dispatch(A action) {
                return middleware.dispatch(this, new Middleware.Next<A, R>() {
                    @Override
                    public R next(A action) {
                        return Dispatcher.this.dispatch(action);
                    }
                }, action);
            }
        };
    }

    /**
     * Returns a new {@code Dispatcher} that runs the given collection of {@link Middleware}.
     */
    public final Dispatcher<A, R> chain(Iterable<Middleware<A, R>> middleware) {
        return chain(middleware.iterator());
    }

    /**
     * Returns a new {@code Dispatcher} that runs the given collection of {@link Middleware}.
     */
    @SafeVarargs
    public final Dispatcher<A, R> chain(Middleware<A, R>... middleware) {
        return chain(Arrays.asList(middleware));
    }

    private Dispatcher<A, R> chain(Iterator<Middleware<A, R>> itr) {
        if (!itr.hasNext()) {
            return this;
        }
        return chain(itr.next()).chain(itr);
    }
}
