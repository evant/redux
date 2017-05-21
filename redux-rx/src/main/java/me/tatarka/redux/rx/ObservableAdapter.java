package me.tatarka.redux.rx;

import me.tatarka.redux.SimpleStore;

import rx.Emitter;
import rx.Observable;
import rx.Observer;
import rx.functions.Action1;
import rx.functions.Cancellable;
import rx.observers.SerializedObserver;

/**
 * Handles constructing an observable from an {@link SimpleStore}.
 */
public class ObservableAdapter {

    public static <S> Observable<S> observable(final SimpleStore<S> store) {
        return Observable.fromEmitter(new Action1<Emitter<S>>() {
            @Override
            public void call(Emitter<S> emitter) {
                store.addListener(new EmitterListener<>(emitter, store));
            }
        }, Emitter.BackpressureMode.LATEST);
    }

    private static class EmitterListener<S> implements SimpleStore.Listener<S>, Cancellable {
        final SimpleStore<S> store;
        final Observer<S> observer;

        EmitterListener(Emitter<S> emitter, SimpleStore<S> store) {
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
