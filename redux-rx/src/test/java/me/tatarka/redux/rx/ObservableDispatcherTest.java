package me.tatarka.redux.rx;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.tatarka.redux.Dispatcher;
import me.tatarka.redux.Reducer;
import me.tatarka.redux.SimpleStore;

import rx.Observable;
import rx.Single;
import rx.functions.Action0;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class ObservableDispatcherTest {

    @Test
    public void subscription_receives_initial_state() {
        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        SimpleStore<String> store = new SimpleStore<>("test1");
        ObservableAdapter.observable(store).subscribe(testSubscriber);

        testSubscriber.assertValue("test1");
    }

    @Test
    public void subscription_receives_updated_state() {
        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        SimpleStore<String> store = new SimpleStore<>("test1");
        ObservableAdapter.observable(store).subscribe(testSubscriber);
        store.setState("test2");

        testSubscriber.assertValues("test1", "test2");
    }

    @Test
    public void canceled_subscription_no_longer_receives_updates() {
        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        SimpleStore<String> store = new SimpleStore<>("test1");
        ObservableAdapter.observable(store).subscribe(testSubscriber).unsubscribe();
        store.setState("test2");

        testSubscriber.assertValue("test1");
    }

    @Test
    public void multithreaded_dispatch_to_subscription_does_not_lose_messages() throws InterruptedException {
        final int JOB_COUNT = 100;

        ExecutorService exec = Executors.newFixedThreadPool(JOB_COUNT);
        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        final SimpleStore<String> store = new SimpleStore<>("test");
        final Dispatcher<String, String> dispatcher = Dispatcher.forStore(store, new Reducer<String, String>() {
            @Override
            public String reduce(String state, String action) {
                return action;
            }
        });
        ObservableAdapter.observable(store).subscribe(testSubscriber);

        List<Callable<Void>> jobs = new ArrayList<>(JOB_COUNT);
        for (int i = 0; i < JOB_COUNT; i++) {
            final int index = i;
            jobs.add(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    dispatcher.dispatch("test" + index);
                    return null;
                }
            });
        }
        exec.invokeAll(jobs);

        testSubscriber.assertValueCount(JOB_COUNT + 1);
    }


    @Test
    public void dispatch_observable_action() {
        Reducer<String, String> reducer = new Reducer<String, String>() {
            @Override
            public String reduce(String state, String action) {
                return action;
            }
        };
        SimpleStore<String> store = new SimpleStore<>("test1");
        ObservableDispatcher<String> dispatcher = new ObservableDispatcher<>(Dispatcher.forStore(store, reducer));
        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        ObservableAdapter.observable(store).subscribe(testSubscriber);
        dispatcher.dispatch(Observable.just("test2"));

        testSubscriber.assertValues("test1", "test2");
    }

    @Test
    public void dispatch_observable_multiple_actions() {
        Reducer<String, String> reducer = new Reducer<String, String>() {
            @Override
            public String reduce(String state, String action) {
                return action;
            }
        };
        SimpleStore<String> store = new SimpleStore<>("test1");
        ObservableDispatcher<String> dispatcher = new ObservableDispatcher<>(Dispatcher.forStore(store, reducer));
        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        ObservableAdapter.observable(store).subscribe(testSubscriber);
        dispatcher.dispatch(Observable.just("test2", "test3"));

        testSubscriber.assertValues("test1", "test2", "test3");
    }

    @Test
    public void dispatch_single_action() {
        Reducer<String, String> reducer = new Reducer<String, String>() {
            @Override
            public String reduce(String state, String action) {
                return action;
            }
        };
        SimpleStore<String> store = new SimpleStore<>("test1");
        SingleDispatcher<String> dispatcher = new SingleDispatcher<>(Dispatcher.forStore(store, reducer));
        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        ObservableAdapter.observable(store).subscribe(testSubscriber);
        dispatcher.dispatch(Single.just("test2"));

        testSubscriber.assertValues("test1", "test2");
    }

    @Test
    public void dispatch_completable() {
        SimpleStore<String> store = new SimpleStore<>("test1");
        CompletableDispatcher dispatcher = new CompletableDispatcher();
        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        ObservableAdapter.observable(store).subscribe(testSubscriber);
        final boolean[] completableCalled = new boolean[1];
        dispatcher.dispatch(rx.Completable.fromAction(new Action0() {
            @Override
            public void call() {
                completableCalled[0] = true;
            }
        }));

        testSubscriber.assertValues("test1");
        assertTrue(completableCalled[0]);
    }
}
