package me.tatarka.redux;

import rx.Single;
import rx.Subscription;
import rx.functions.Action1;

public class SingleDispatcher<A> extends Dispatcher<Single<A>, Subscription> {

    private final Action1<A> dispatchAction;

    public SingleDispatcher(final Dispatcher<A, ?> dispatcher) {
        dispatchAction = new Action1<A>() {
            @Override
            public void call(A action) {
                dispatcher.dispatch(action);
            }
        };
    }

    @Override
    public Subscription dispatch(Single<A> action) {
        return action.subscribe(dispatchAction);
    }
}
