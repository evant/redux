package me.tatarka.redux;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A thread-safe store that allows you to attach and remove listeners for when the state changes.
 */
public class SimpleStore<S> implements Store<S> {

    private volatile S state;
    private final CopyOnWriteArrayList<Listener<S>> listeners = new CopyOnWriteArrayList<>();

    public SimpleStore(S initialState) {
        setState(initialState);
    }

    @Override
    public S getState() {
        return state;
    }

    @Override
    public void setState(S newState) {
        if (!equals(state, newState)) {
            state = newState;
            for (Listener<S> listener : listeners) {
                listener.onNewState(state);
            }
        }
    }

    /**
     * Registers as listener to receive state changes. The current state will be delivered
     * immediately.
     */
    public void addListener(Listener<S> listener) {
        listeners.add(listener);
        listener.onNewState(state);
    }

    /**
     * Removes the listener so it no longer receives state changes.
     */
    public void removeListener(Listener<S> listener) {
        listeners.remove(listener);
    }

    public interface Listener<S> {
        /**
         * Called when a new state is set. This is called on the same thread as  or {@link #setState(Object)}.
         */
        void onNewState(S state);
    }

    private static boolean equals(Object var0, Object var1) {
        return var0 == var1 || var0 != null && var0.equals(var1);
    }
}

