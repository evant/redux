package me.tatarka.redux.middleware;

import me.tatarka.redux.Store;

/**
 * Middleware allows you to implement cross-cutting concerns like logging, crash reporting, etc. by
 * intercepting every action.
 */
public interface Middleware<S> {

    /**
     * Called when the middleware is applied to the store. This allows you to obtain a reference to
     * get the current state and dispatch actions.
     */
    void create(Store<S> store);

    /**
     * Called when an action is dispatched.
     *
     * @param next   Dispatch to the next middleware or actually update the state if there is none.
     *               You can chose to call this anywhere to see the state before and after it has
     *               changed or not at all to drop the action.
     * @param action This action that was dispatched.
     */
    void dispatch(Next next, Object action);

    interface Next {
        void next(Object action);
    }
}
