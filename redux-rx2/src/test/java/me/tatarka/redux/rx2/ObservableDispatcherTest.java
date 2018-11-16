package me.tatarka.redux.rx2;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import io.reactivex.subscribers.TestSubscriber;

import me.tatarka.redux.Dispatcher;
import me.tatarka.redux.Reducer;
import me.tatarka.redux.SimpleStore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class ObservableDispatcherTest {

    @Test
    public void subscription_receives_initial_state() {
        SimpleStore<String> store = new SimpleStore<>("test1");
        TestSubscriber<String> testSubscriber = FlowableAdapter.flowable(store).test();
        testSubscriber.assertValue("test1");
    }

    @Test
    public void subscription_receives_updated_state() {
        SimpleStore<String> store = new SimpleStore<>("test1");
        TestSubscriber<String> testSubscriber = FlowableAdapter.flowable(store).test();
        store.setState("test2");

        testSubscriber.assertValues("test1", "test2");
    }

    @Test
    public void canceled_subscription_no_longer_receives_updates() {
        SimpleStore<String> store = new SimpleStore<>("test1");
        TestSubscriber<String> testSubscriber = FlowableAdapter.flowable(store).test();
        testSubscriber.dispose();
        store.setState("test2");

        testSubscriber.assertValue("test1");
    }

    @Test
    public void multithreaded_dispatch_to_subscription_does_not_lose_messages() throws InterruptedException {
        final int JOB_COUNT = 100;

        ExecutorService exec = Executors.newFixedThreadPool(JOB_COUNT);
        final SimpleStore<String> store = new SimpleStore<>("test");
        final Dispatcher<String, String> dispatcher = Dispatcher.forStore(store, new Reducer<String, String>() {
            @Override
            public String reduce(String state, String action) {
                return action;
            }
        });
        TestSubscriber<String> testSubscriber = FlowableAdapter.flowable(store).test();

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
        TestSubscriber<String> testSubscriber = FlowableAdapter.flowable(store).test();
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
        TestSubscriber<String> testSubscriber = FlowableAdapter.flowable(store).test();
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
        TestSubscriber<String> testSubscriber = FlowableAdapter.flowable(store).test();
        dispatcher.dispatch(Single.just("test2"));

        testSubscriber.assertValues("test1", "test2");
    }

    @Test
    public void dispatch_completable() {
        SimpleStore<String> store = new SimpleStore<>("test1");
        CompletableDispatcher dispatcher = new CompletableDispatcher();
        TestSubscriber<String> testSubscriber = FlowableAdapter.flowable(store).test();
        final boolean[] completableCalled = new boolean[1];
        dispatcher.dispatch(Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                completableCalled[0] = true;
            }
        }));

        testSubscriber.assertValues("test1");
        assertTrue(completableCalled[0]);
    }

    @Test
    public void dispatch_empty_maybe() {
        Reducer<String, String> reducer = new Reducer<String, String>() {
            @Override
            public String reduce(String state, String action) {
                return action;
            }
        };
        SimpleStore<String> store = new SimpleStore<>("test1");
        MaybeDispatcher<String> dispatcher = new MaybeDispatcher<>(Dispatcher.forStore(store, reducer));
        TestSubscriber<String> testSubscriber = FlowableAdapter.flowable(store).test();
        dispatcher.dispatch(Maybe.<String>empty());
        testSubscriber.assertValues("test1");
    }

    @Test
    public void dispatch_non_empty_maybe() {
        Reducer<String, String> reducer = new Reducer<String, String>() {
            @Override
            public String reduce(String state, String action) {
                return action;
            }
        };
        SimpleStore<String> store = new SimpleStore<>("test1");
        MaybeDispatcher<String> dispatcher = new MaybeDispatcher<>(Dispatcher.forStore(store, reducer));
        TestSubscriber<String> testSubscriber = FlowableAdapter.flowable(store).test();
        dispatcher.dispatch(Maybe.just("test2"));
        testSubscriber.assertValues("test1", "test2");
    }
}
