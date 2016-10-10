package me.tatarka.redux;

import me.tatarka.redux.middleware.Middleware;
import me.tatarka.redux.middleware.MiddlewareFactory;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class ObservableMiddleware<A, S> implements MiddlewareFactory<A, S>, Subscription {

    private final CompositeSubscription subscription = new CompositeSubscription();

    @Override
    public Middleware<A> create(final Store<A, S> store) {
        final Action1<A> dispatchAction = new Action1<A>() {
            @Override
            public void call(A action) {
                store.dispatch(action);
            }
        };
        return new Middleware<A>() {
            @Override
            @SuppressWarnings("unchecked")
            public void dispatch(Next<A> next, A action) {
                if (action instanceof rx.Observable) {
                    rx.Observable<? extends A> observable = (rx.Observable) action;
                    subscription.add(observable.subscribe(dispatchAction));
                } else if (action instanceof rx.Single) {
                    rx.Single<? extends A> single = (rx.Single) action;
                    subscription.add(single.subscribe(dispatchAction));
                } else if (action instanceof rx.Completable) {
                    rx.Completable completable = (rx.Completable) action;
                    subscription.add(completable.subscribe());
                } else {
                    next.next(action);
                }
            }
        };
    }

    @Override
    public void unsubscribe() {
        subscription.unsubscribe();
    }

    @Override
    public boolean isUnsubscribed() {
        return subscription.isUnsubscribed();
    }
}
