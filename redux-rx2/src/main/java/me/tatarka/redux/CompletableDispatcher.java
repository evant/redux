package me.tatarka.redux;

import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;

public class CompletableDispatcher extends Dispatcher<Completable, Disposable> {
    @Override
    public Disposable dispatch(Completable action) {
        return action.subscribe();
    }
}
