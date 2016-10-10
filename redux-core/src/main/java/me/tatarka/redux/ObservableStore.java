package me.tatarka.redux;

import java.util.concurrent.CopyOnWriteArrayList;

import me.tatarka.redux.middleware.Middleware;
import rx.Observable;

/**
 * A simple store that lets you observe state changes. While this class is thread safe, listener
 * callbacks happen on the thread dispatch was called on so you must be able to handle that.
 */
public class ObservableStore<S> extends AbstractStore<S> {

    private volatile S state;

    private final CopyOnWriteArrayList<Listener<S>> listeners = new CopyOnWriteArrayList<>();

    @SafeVarargs
    public ObservableStore(S initialState, Reducer<Object, S> reducer, Middleware<S>... middleware) {
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
        /**
         * Called when a new state is set. This is called on the same thread as {@link
         * #dispatch(Object)} or {@link #setState(Object)}.
         */
        void onNewState(S state);
    }

    /**
     * Provides an rxjava {@link rx.Observable} to listen to state changes.
     */
    public Observable<S> observable() {
        return StoreObservable.observable(this);
    }
}
