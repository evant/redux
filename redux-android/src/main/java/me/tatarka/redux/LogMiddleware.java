package me.tatarka.redux;

import android.util.Log;
import me.tatarka.redux.middleware.Middleware;
import me.tatarka.redux.middleware.MiddlewareFactory;

public class LogMiddleware<A, S> implements MiddlewareFactory<A, S> {

    private final String tag;
    private final int priority;

    public LogMiddleware(String tag) {
        this(tag, Log.DEBUG);
    }

    public LogMiddleware(String tag, int priority) {
        this.tag = tag;
        this.priority = priority;
    }

    @Override
    public Middleware<A> create(Store<A, S> store) {
        return new Middleware<A>() {
            @Override
            public void dispatch(Next<A> next, A action) {
                Log.println(priority, tag, action.toString());
                next.next(action);
            }
        };
    }
}
