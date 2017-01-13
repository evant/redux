package me.tatarka.redux;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class ObservableDispatcher<A> extends Dispatcher<Observable<A>, Disposable> {

    private final Consumer<A> dispatchAction;

    public ObservableDispatcher(final Dispatcher<A, ?> dispatcher) {
        dispatchAction = new Consumer<A>() {
            @Override
            public void accept(A action) throws Exception {
                dispatcher.dispatch(action);
            }
        };
    }

    @Override
    public Disposable dispatch(Observable<A> action) {
        return action.subscribe(dispatchAction);
    }
}
