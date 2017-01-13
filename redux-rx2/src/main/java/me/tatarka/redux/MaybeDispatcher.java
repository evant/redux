package me.tatarka.redux;

import io.reactivex.Maybe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MaybeDispatcher<A> extends Dispatcher<Maybe<A>, Disposable> {

    private final Consumer<A> dispatchAction;

    public MaybeDispatcher(final Dispatcher<A, ?> dispatcher) {
        dispatchAction = new Consumer<A>() {
            @Override
            public void accept(A action) throws Exception {
                dispatcher.dispatch(action);
            }
        };
    }

    @Override
    public Disposable dispatch(Maybe<A> action) {
        return action.subscribe(dispatchAction);
    }
}
