package me.tatarka.redux;

/**
 * Stores your immutable state and allows you to dispatch actions to change it. Note: if you plan
 * on dispatching from multiple threads, {@link #getState()} and {@link #setState(Object)} must be
 * thread-safe.
 */
public interface Store<S> {

    /**
     * Returns the current state of the store.
     */
    S getState();

    /**
     * Sets the state of the store. Warning! You should not call this in normal application code,
     * instead preferring to update it through dispatching an action. It is however, useful for
     * tests.
     */
    void setState(S state);
}
