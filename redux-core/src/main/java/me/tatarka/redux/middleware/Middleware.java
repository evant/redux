package me.tatarka.redux.middleware;

/**
 * Middleware allows you to implement cross-cutting concerns like logging, crash reporting, etc. by
 * intercepting every action.
 *
 * @param <A> the action type.
 */
public interface Middleware<A> {

    /**
     * Called when an action is dispatched.
     *
     * @param next   Dispatch to the next middleware or actually update the state if there is none.
     *               You can chose to call this anywhere to see the state before and after it has
     *               changed or not at all to drop the action.
     * @param action This action that was dispatched.
     */
    void dispatch(Next<A> next, A action);

    interface Next<A> {
        void next(A action);
    }
}
