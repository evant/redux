package me.tatarka.redux.middleware;

import me.tatarka.redux.Store;

/**
 * A factory to construct a middleware given a store.
 *
 * @param <A> the acton type.
 * @param <S> the state type.
 */
public interface MiddlewareFactory<A, S> {

    /**
     * Called when the store is created.
     *
     * @param store The store. This allows you to obtain the current state with {@link
     *              Store#state()} and dispatch additional actions with {@link
     *              Store#dispatch(Object)}. Actions dispatched here will run through all middleware
     *              again.
     */
    Middleware<A> create(Store<A, S> store);
}
