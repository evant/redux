package me.tatarka.redux.rx2;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Cancellable;

import me.tatarka.redux.SimpleStore;

/**
 * Handles constructing a flowable from an {@link SimpleStore}.
 */
public class ObserveStore {

    public static <S> Flowable<S> flowable(final SimpleStore<S> store) {
        return Flowable.create(new FlowableOnSubscribe<S>() {
            @Override
            public void subscribe(FlowableEmitter<S> emitter) throws Exception {
                store.addListener(new EmitterListener<>(emitter, store));
            }
        }, BackpressureStrategy.LATEST);
    }

    private static class EmitterListener<S> implements SimpleStore.Listener<S>, Cancellable {
        final SimpleStore<S> store;
        final FlowableEmitter<S> emitter;

        EmitterListener(FlowableEmitter<S> emitter, SimpleStore<S> store) {
            this.emitter = emitter.serialize();
            this.store = store;
            emitter.setCancellable(this);
        }

        @Override
        public void onNewState(S state) {
            emitter.onNext(state);
        }

        @Override
        public void cancel() throws Exception {
            store.removeListener(this);
        }
    }
}
