package me.tatarka.redux;

import me.tatarka.redux.middleware.MiddlewareFactory;
import rx.Emitter;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Cancellable;

public class ObservableStore<A, S> extends ListenerStore<A, S> {

    @SafeVarargs
    public ObservableStore(S initialState, Reducer<A, S> reducer, MiddlewareFactory<A, S>... middleware) {
        super(initialState, reducer, middleware);
    }

    public Observable<S> observable() {
        return Observable.fromEmitter(new Action1<Emitter<S>>() {
            @Override
            public void call(Emitter<S> emitter) {
                ObservableStore.this.addListener(new EmitterListener(emitter));
            }
        }, Emitter.BackpressureMode.LATEST);
    }

    private class EmitterListener implements Listener<S>, Cancellable {

        final Emitter<S> emitter;

        EmitterListener(Emitter<S> emitter) {
            this.emitter = emitter;
            emitter.setCancellation(this);
        }

        @Override
        public void onNewState(S state) {
            emitter.onNext(state);
        }

        @Override
        public void cancel() throws Exception {
            removeListener(this);
        }
    }
}
