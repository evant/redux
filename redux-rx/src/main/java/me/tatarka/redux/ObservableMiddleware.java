package me.tatarka.redux;

import me.tatarka.redux.middleware.Middleware;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class ObservableMiddleware<S> implements Middleware<S>, Subscription {

    private final CompositeSubscription subscription = new CompositeSubscription();
    private Action1<Object> dispatchAction;

    @Override
    public void create(final Store<S> store) {
        dispatchAction = new Action1<Object>() {
            @Override
            public void call(Object action) {
                store.dispatch(action);
            }
        };
    }

    @Override
    public void dispatch(Next next, Object action) {
        if (action instanceof rx.Observable) {
            rx.Observable<?> observable = (rx.Observable) action;
            subscription.add(observable.subscribe(dispatchAction));
        } else if (action instanceof rx.Single) {
            rx.Single<?> single = (rx.Single) action;
            subscription.add(single.subscribe(dispatchAction));
        } else if (action instanceof rx.Completable) {
            rx.Completable completable = (rx.Completable) action;
            subscription.add(completable.subscribe());
        } else {
            next.next(action);
        }
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
