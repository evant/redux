package me.tatarka.redux;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ObservableStoreTest {

    @Test
    public void addListener_receives_initial_state() {
        ObservableStore<Object, String> store = new ObservableStore<>("test", Reducers.<Object, String>id());
        TestListener<String> testListener = new TestListener<>();
        store.addListener(testListener);

        assertEquals("test", testListener.state);
    }

    @Test
    public void addListener_receives_updated_state() {
        ObservableStore<Object, String> store = new ObservableStore<>("test1", Reducers.<Object, String>id());
        TestListener<String> testListener = new TestListener<>();
        store.addListener(testListener);
        store.setState("test2");

        assertEquals("test2", testListener.state);
    }

    @Test
    public void removeListener_no_longer_receives_updates() {
        ObservableStore<Object, String> store = new ObservableStore<>("test1", Reducers.<Object, String>id());
        TestListener<String> testListener = new TestListener<>();
        store.addListener(testListener);
        store.removeListener(testListener);
        store.setState("test2");

        assertEquals("test1", testListener.state);
    }

    @Test
    public void subscription_receives_initial_state() {
        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        ObservableStore<Object, String> store = new ObservableStore<>("test1", Reducers.<Object, String>id());
        store.observable().subscribe(testSubscriber);

        testSubscriber.assertValue("test1");
    }

    @Test
    public void subscription_receives_updated_state() {
        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        ObservableStore<Object, String> store = new ObservableStore<>("test1", Reducers.<Object, String>id());
        store.observable().subscribe(testSubscriber);
        store.setState("test2");

        testSubscriber.assertValues("test1", "test2");
    }

    @Test
    public void canceled_subscription_no_longer_receives_updates() {
        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        ObservableStore<Object, String> store = new ObservableStore<>("test1", Reducers.<Object, String>id());
        store.observable().subscribe(testSubscriber).unsubscribe();
        store.setState("test2");

        testSubscriber.assertValue("test1");
    }
    
    @Test
    public void removing_listener_while_dispatching_does_not_cause_error() {
        final ObservableStore<Object, String> store = new ObservableStore<>("test1", Reducers.<Object, String>id());
        TestListener<String> testListener = new TestListener<String>() {
            int count = 0;
            
            @Override
            public void onNewState(String state) {
                super.onNewState(state);
                if (count == 1) {
                    store.removeListener(this);
                }
                count += 1;
            }
        };
        store.addListener(testListener);
        store.setState("test2");

        assertEquals("test2", testListener.state);
    }

    @Test
    public void multithreaded_dispatch_to_subscription_does_not_lose_messages() throws InterruptedException {
        final int JOB_COUNT = 100;
        
        ExecutorService exec = Executors.newFixedThreadPool(JOB_COUNT);
        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        final ObservableStore<Object, String> store = new ObservableStore<>("test", Reducers.<Object, String>id());
        store.observable().subscribe(testSubscriber);

        List<Callable<Void>> jobs = new ArrayList<>(JOB_COUNT);
        for (int i = 0; i < JOB_COUNT; i++) {
            final int index = i;
            jobs.add(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    store.dispatch("test" + index);
                    return null;
                }
            });
        }
        exec.invokeAll(jobs);

        testSubscriber.assertValueCount(JOB_COUNT + 1);
    }

    static class TestListener<S> implements ObservableStore.Listener<S> {
        S state;

        @Override
        public void onNewState(S state) {
            this.state = state;
        }
    }
}
