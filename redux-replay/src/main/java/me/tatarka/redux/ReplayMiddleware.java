package me.tatarka.redux;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.tatarka.redux.middleware.Middleware;

public class ReplayMiddleware<S, A, R> implements Middleware<A, R> {

    private final S initialState;
    private final Store<S> store;
    private final Dispatcher<A, A> dispatcher;
    private final ArrayList<A> actions = new ArrayList<>();
    private final Set<Integer> disabled = new HashSet<>();
    private boolean runningActions;

    public ReplayMiddleware(Store<S> store, Reducer<A, S> reducer) {
        this.initialState = store.getState();
        this.store = store;
        this.dispatcher = Dispatcher.forStore(store, reducer);
    }

    @Override
    public R dispatch(Dispatcher<A, R> dispatcher, Next<A, R> next, A action) {
        if (!runningActions) {
            actions.add(action);
        }
        return next.next(action);
    }

    public List<A> actions() {
        return Collections.unmodifiableList(actions);
    }

    public boolean isDisabled(int index) {
        return disabled.contains(index);
    }

    public void disable(int index) {
        disabled.add(index);
        rerunActions();
    }

    public void enable(int index) {
        disabled.remove(index);
        rerunActions();
    }

    public void replace(int index, A newAction) {
        actions.set(index, newAction);
        rerunActions();
    }

    public void remove(int index) {
        actions.remove(index);
        disabled.remove(index);
        rerunActions();
    }

    private void rerunActions() {
        store.setState(initialState);
        runningActions = true;
        for (int i = 0; i < actions.size(); i++) {
            if (!disabled.contains(i)) {
                A action = actions.get(i);
                dispatcher.dispatch(action);
            }
        }
        runningActions = false;
    }
}
