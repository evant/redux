package com.example.sample_android.store;

import android.content.Context;

import com.example.sample_android.Datastore;
import com.example.sample_android.action.Action;
import com.example.sample_android.middleware.PersistenceMiddleware;
import com.example.sample_android.reducer.TodoListReducers;
import com.example.sample_android.state.TodoList;

import me.tatarka.redux.*;
import me.tatarka.redux.android.LogMiddleware;
import me.tatarka.redux.monitor.MonitorMiddleware;

/**
 * To keep everything together, you can subclass {@link SimpleStore} and add your own {@code dispatch()} methods to it.
 */
public class MainStore extends SimpleStore<TodoList> {

    private final Dispatcher<Action, Action> dispatcher;
    private final Dispatcher<Thunk<Action, Action>, Void> thunkDispatcher;
    private final ReplayMiddleware<TodoList, Action, Action> replayMiddleware;
    private final MonitorMiddleware<TodoList, Action, Action> monitorMiddleware;

    public MainStore(Context context) {
        super(TodoList.initial());
        Reducer<TodoList, Action> reducer = TodoListReducers.reducer();
        replayMiddleware = new ReplayMiddleware<>(this, reducer);
        monitorMiddleware = new MonitorMiddleware<>(this, new MonitorMiddleware.Config("10.0.2.2", 8000));
        dispatcher = Dispatcher.forStore(this, reducer)
                .chain(new LogMiddleware<Action, Action>("ACTION"),
                        replayMiddleware,
                        monitorMiddleware,
                        new PersistenceMiddleware<Action, Action>(this, new Datastore(context)));
        thunkDispatcher = new ThunkDispatcher<>(dispatcher)
                .chain(new LogMiddleware<Thunk<Action, Action>, Void>("THUNK_ACTION"));
    }

    public Action dispatch(Action action) {
        return dispatcher.dispatch(action);
    }

    public void dispatch(Thunk<Action, Action> thunk) {
        thunkDispatcher.dispatch(thunk);
    }

    public ReplayMiddleware<TodoList, Action, Action> getReplayMiddleware() {
        return replayMiddleware;
    }
}
