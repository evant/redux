package me.tatarka.redux.middleware;

import me.tatarka.redux.Dispatcher;

/**
 * Middleware allows you to implement cross-cutting concerns like logging, crash reporting, etc. by
 * intercepting every action.
 */
public interface Middleware<A, R> {

    /**
     * Called when an action is dispatched.
     *
     * @param next   Dispatch to the next middleware or actually update the state if there is none.
     *               You can chose to call this anywhere to see the state before and after it has
     *               changed or not at all to drop the action.
     * @param action This action that was dispatched.
     */
    R dispatch(Dispatcher<A, R> dispatcher, Next<A, R> next, A action);

    interface Next<A, R> {
        R next(A action);
    }
}
