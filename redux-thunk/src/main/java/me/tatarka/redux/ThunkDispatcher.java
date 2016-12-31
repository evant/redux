package me.tatarka.redux;

public class ThunkDispatcher<A, R> extends Dispatcher<Thunk<A, R>, Void> {

    private final Dispatcher<A, R> dispatcher;

    public ThunkDispatcher(Dispatcher<A, R> dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public Void dispatch(Thunk<A, R> action) {
        action.run(dispatcher);
        return null;
    }
}
