package me.tatarka.redux;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.tatarka.redux.middleware.Middleware;

public class ReplayMiddleware<A, S> implements Middleware<A, S> {

    private S initialState;
    private Store<A, S> store;
    private final ArrayList<A> actions = new ArrayList<>();
    private Set<Integer> disabled = new HashSet<>();
    private boolean runningActions;

    @Override
    public void create(Store<A, S> store) {
        this.initialState = store.state();
        this.store = store;
        actions.clear();
        disabled.clear();
    }

    @Override
    public void dispatch(Next<A> next, A action) {
        if (!runningActions) {
            actions.add(action);
        }
        next.next(action);
    }

    public List<A> actions() {
        return Collections.unmodifiableList(actions);
    }

    public void disable(int index) {
        disabled.add(index);
        rerunActions();
    }

    public boolean isDisabled(int index) {
        return disabled.contains(index);
    }

    public void enable(int index) {
        disabled.remove(index);
        rerunActions();
    }

    private void rerunActions() {
        store.setState(initialState);
        runningActions = true;
        for (int i = 0; i < actions.size(); i++) {
            if (!disabled.contains(i)) {
                A action = actions.get(i);
                store.dispatch(action);
            }
        }
        runningActions = false;
    }
}
