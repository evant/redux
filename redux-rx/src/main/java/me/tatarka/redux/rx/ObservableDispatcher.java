package me.tatarka.redux.rx;

import me.tatarka.redux.Dispatcher;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

public class ObservableDispatcher<A> extends Dispatcher<Observable<A>, Subscription> {

    private final Action1<A> dispatchAction;

    public ObservableDispatcher(final Dispatcher<A, ?> dispatcher) {
        dispatchAction = new Action1<A>() {
            @Override
            public void call(A action) {
                dispatcher.dispatch(action);
            }
        };
    }

    @Override
    public Subscription dispatch(Observable<A> action) {
        return action.subscribe(dispatchAction);
    }
}
