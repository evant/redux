package me.tatarka.redux;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.tatarka.redux.middleware.Middleware;

public class ReplayMiddleware<S> implements Middleware<S> {

    private S initialState;
    private Store<S> store;
    private final ArrayList<Object> actions = new ArrayList<>();
    private Set<Integer> disabled = new HashSet<>();
    private boolean runningActions;

    @Override
    public void create(Store<S> store) {
        this.initialState = store.state();
        this.store = store;
        actions.clear();
        disabled.clear();
    }

    @Override
    public void dispatch(Next next, Object action) {
        if (!runningActions) {
            actions.add(action);
        }
        next.next(action);
    }

    public List<Object> actions() {
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
    
    public void replace(int index, Object newAction) {
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
                Object action = actions.get(i);
                store.dispatch(action);
            }
        }
        runningActions = false;
    }
}
