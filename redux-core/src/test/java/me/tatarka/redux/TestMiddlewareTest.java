package me.tatarka.redux;

import me.tatarka.redux.middleware.TestMiddleware;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class TestMiddlewareTest {

    @Test
    public void states_includes_initial_state() {
        TestMiddleware<Object, String> testMiddleware = new TestMiddleware<>();
        ListenerStore.create("test", Reducers.<Object, String>id(), testMiddleware);

        assertEquals("test", testMiddleware.states().get(0));
    }

    @Test
    public void state_includes_state_after_action() {
        Reducer<Object, String> reducer = new Reducer<Object, String>() {
            @Override
            public String reduce(Object action, String state) {
                return "test2";
            }
        };
        TestMiddleware<Object, String> testMiddleware = new TestMiddleware<>();
        ListenerStore<Object, String> store = ListenerStore.create("test1", reducer, testMiddleware);
        store.dispatch("action");

        assertEquals("test2", testMiddleware.states().get(1));
    }

    @Test
    public void action_after_dispatch() {
        TestMiddleware<Object, String> testMiddleware = new TestMiddleware<>();
        ListenerStore<Object, String> store = ListenerStore.create("test", Reducers.<Object, String>id(), testMiddleware);
        store.dispatch("action");

        assertEquals("action", testMiddleware.actions().get(0));
    }
}
