package me.tatarka.redux;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import rx.functions.Action0;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class ObservableMiddlewareTest {

    @Test
    public void dispatch_observable_action() {
        Reducer<Object, String> reducer = new Reducer<Object, String>() {
            @Override
            public String reduce(Object action, String state) {
                return action.toString();
            }
        };
        ObservableStore<Object, String> store = new ObservableStore<>("test1", reducer, new ObservableMiddleware<Object, String>());
        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        store.observable().subscribe(testSubscriber);
        store.dispatch(rx.Observable.just("test2"));

        testSubscriber.assertValues("test1", "test2");
    }

    @Test
    public void dispatch_observable_multiple_actions() {
        Reducer<Object, String> reducer = new Reducer<Object, String>() {
            @Override
            public String reduce(Object action, String state) {
                return action.toString();
            }
        };
        ObservableStore<Object, String> store = new ObservableStore<>("test1", reducer, new ObservableMiddleware<Object, String>());
        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        store.observable().subscribe(testSubscriber);
        store.dispatch(rx.Observable.just("test2", "test3"));

        testSubscriber.assertValues("test1", "test2", "test3");
    }

    @Test
    public void dispatch_single_action() {
        Reducer<Object, String> reducer = new Reducer<Object, String>() {
            @Override
            public String reduce(Object action, String state) {
                return action.toString();
            }
        };
        ObservableStore<Object, String> store = new ObservableStore<>("test1", reducer, new ObservableMiddleware<Object, String>());
        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        store.observable().subscribe(testSubscriber);
        store.dispatch(rx.Single.just("test2"));

        testSubscriber.assertValues("test1", "test2");
    }

    @Test
    public void dispatch_completable() {
        Reducer<Object, String> reducer = new Reducer<Object, String>() {
            @Override
            public String reduce(Object action, String state) {
                return action.toString();
            }
        };
        ObservableStore<Object, String> store = new ObservableStore<>("test1", reducer, new ObservableMiddleware<Object, String>());
        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        store.observable().subscribe(testSubscriber);
        final boolean[] completableCalled = new boolean[1];
        store.dispatch(rx.Completable.fromAction(new Action0() {
            @Override
            public void call() {
                completableCalled[0] = true;
            }
        }));

        testSubscriber.assertValues("test1");
        assertTrue(completableCalled[0]);
    }
}
