package me.tatarka.redux;

import me.tatarka.redux.middleware.MiddlewareFactory;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A simple store that lets you add and remove listeners to respond to state changes. While this
 * class is thread safe, listener callbacks happen on the thread dispatch was called on so you
 * must be able to handle that.
 */
public class ListenerStore<A, S> extends AbstractStore<A, S> {

    @SafeVarargs
    public static <A, S> ListenerStore<A, S> create(S initialState, Reducer<A, S> reducer, MiddlewareFactory<A, S>... middleware) {
        return new ListenerStore<>(initialState, reducer, middleware);
    }

    private volatile S state;

    private final CopyOnWriteArrayList<Listener<S>> listeners = new CopyOnWriteArrayList<>();

    @SafeVarargs
    public ListenerStore(S initialState, Reducer<A, S> reducer, MiddlewareFactory<A, S>... middleware) {
        super(initialState, reducer, middleware);
        state = initialState;
    }

    @Override
    public S state() {
        return state;
    }

    @Override
    public void setState(S newState) {
        state = newState;
        for (Listener<S> listener : listeners) {
            listener.onNewState(state);
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
        void onNewState(S state);
    }
}
