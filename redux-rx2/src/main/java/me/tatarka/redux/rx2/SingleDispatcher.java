package me.tatarka.redux.rx2;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import me.tatarka.redux.Dispatcher;

public class SingleDispatcher<A> extends Dispatcher<Single<A>, Disposable> {

    private final Consumer<A> dispatchAction;

    public SingleDispatcher(final Dispatcher<A, ?> dispatcher) {
        dispatchAction = new Consumer<A>() {
            @Override
            public void accept(A action) throws Exception {
                dispatcher.dispatch(action);
            }
        };
    }

    @Override
    public Disposable dispatch(Single<A> action) {
        return action.subscribe(dispatchAction);
    }
}
