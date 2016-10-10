package me.tatarka.redux;

import rx.Emitter;
import rx.Observable;
import rx.Observer;
import rx.functions.Action1;
import rx.functions.Cancellable;
import rx.observers.SerializedObserver;

/**
 * Handles constructing an observable from an {@link ObservableStore}.
 */
class StoreObservable {

    static <S> Observable<S> observable(final ObservableStore<S> store) {
        return Observable.fromEmitter(new Action1<Emitter<S>>() {
            @Override
            public void call(Emitter<S> emitter) {
                store.addListener(new EmitterListener<>(emitter, store));
            }
        }, Emitter.BackpressureMode.LATEST);
    }

    private static class EmitterListener<S> implements ObservableStore.Listener<S>, Cancellable {

        final ObservableStore<S> store;
        final Observer<S> observer;

        EmitterListener(Emitter<S> emitter, ObservableStore<S> store) {
            this.observer = new SerializedObserver<>(emitter);
            this.store = store;
            emitter.setCancellation(this);
        }

        @Override
        public void onNewState(S state) {
            observer.onNext(state);
        }

        @Override
        public void cancel() throws Exception {
            store.removeListener(this);
        }
    }
}
