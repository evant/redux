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
public class SimpleStoreTest {

    @Test
    public void addListener_receives_initial_state() {
        SimpleStore<String> store = new SimpleStore<>("test");
        TestListener<String> testListener = new TestListener<>();
        store.addListener(testListener);

        assertEquals("test", testListener.state);
    }

    @Test
    public void addListener_receives_updated_state() {
        SimpleStore<String> store = new SimpleStore<>("test1");
        TestListener<String> testListener = new TestListener<>();
        store.addListener(testListener);
        store.setState("test2");

        assertEquals("test2", testListener.state);
    }

    @Test
    public void removeListener_no_longer_receives_updates() {
        SimpleStore<String> store = new SimpleStore<>("test1");
        TestListener<String> testListener = new TestListener<>();
        store.addListener(testListener);
        store.removeListener(testListener);
        store.setState("test2");

        assertEquals("test1", testListener.state);
    }

    @Test
    public void removing_listener_while_dispatching_does_not_cause_error() {
        final SimpleStore<String> store = new SimpleStore<>("test1");
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

    static class TestListener<S> implements SimpleStore.Listener<S> {
        S state;

        @Override
        public void onNewState(S state) {
            this.state = state;
        }
    }
}
