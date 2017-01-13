package me.tatarka.redux;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class FlowableDispatcher<A> extends Dispatcher<Flowable<A>, Disposable> {

    private final Consumer<A> dispatchAction;

    public FlowableDispatcher(final Dispatcher<A, ?> dispatcher) {
        dispatchAction = new Consumer<A>() {
            @Override
            public void accept(A action) throws Exception {
                dispatcher.dispatch(action);
            }
        };
    }

    @Override
    public Disposable dispatch(Flowable<A> action) {
        return action.subscribe(dispatchAction);
    }
}
