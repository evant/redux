package me.tatarka.redux.rx2;

import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;

import me.tatarka.redux.Dispatcher;

public class CompletableDispatcher extends Dispatcher<Completable, Disposable> {
    @Override
    public Disposable dispatch(Completable action) {
        return action.subscribe();
    }
}
