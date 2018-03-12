package me.tatarka.redux.middleware;

import me.tatarka.redux.Dispatcher;
import me.tatarka.redux.Store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A middleware that records all actions and states, allowing you to easily check them in tests.
 */
public class TestMiddleware<S, A, R> implements Middleware<A, R> {

    private final Store<S> store;
    private final List<A> actions = new ArrayList<>();
    private final List<S> states = new ArrayList<>();

    public TestMiddleware(Store<S> store) {
        this.store = store;
        states.add(store.getState());
    }

    @Override
    public R dispatch(Dispatcher<A, R> dispatcher, Next<A, R> next, A action) {
        actions.add(action);
        R result = next.next(action);
        states.add(store.getState());
        return result;
    }

    /**
     * Returns all actions that have been dispatched.
     */
    public List<A> actions() {
        return Collections.unmodifiableList(actions);
    }

    /**
     * Returns all states. The first state is before any action has been dispatched. Each
     * subsequent
     * state is the state after each action.
     */
    public List<S> states() {
        return Collections.unmodifiableList(states);
    }

    /**
     * Resets the middleware to only contain the current state and no actions.
     */
    public void reset() {
        states.subList(0, states.size() - 1).clear();
        actions.clear();
    }
}
