package me.tatarka.redux;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ListenerStoreTest {

    @Test
    public void addListener_receives_initial_state() {
        ListenerStore<Object, String> store = ListenerStore.create("test", Reducers.<Object, String>id());
        TestListener<String> testListener = new TestListener<>();
        store.addListener(testListener);

        assertEquals("test", testListener.state);
    }

    @Test
    public void addListener_receives_updated_state() {
        ListenerStore<Object, String> store = ListenerStore.create("test1", Reducers.<Object, String>id());
        TestListener<String> testListener = new TestListener<>();
        store.addListener(testListener);
        store.setState("test2");

        assertEquals("test2", testListener.state);
    }

    @Test
    public void removeListener_no_longer_receives_updates() {
        ListenerStore<Object, String> store = ListenerStore.create("test1", Reducers.<Object, String>id());
        TestListener<String> testListener = new TestListener<>();
        store.addListener(testListener);
        store.removeListener(testListener);
        store.setState("test2");

        assertEquals("test1", testListener.state);
    }

    static class TestListener<S> implements ListenerStore.Listener<S> {
        S state;

        @Override
        public void onNewState(S state) {
            this.state = state;
        }
    }
}
