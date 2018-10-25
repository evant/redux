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
        SimpleStore<String> simpleStore = new SimpleStore<>("test");
        TestMiddleware<String, String, String> testMiddleware = new TestMiddleware<>(simpleStore);

        assertEquals("test", testMiddleware.states().get(0));
    }

    @Test
    public void state_includes_state_after_action() {
        Reducer<String, String> reducer = new Reducer<String, String>() {
            @Override
            public String reduce(String state, String action) {
                return "test2";
            }
        };
        SimpleStore<String> simpleStore = new SimpleStore<>("test");
        TestMiddleware<String, String, String> testMiddleware = new TestMiddleware<>(simpleStore);
        Dispatcher<String, String> dispatcher = Dispatcher.forStore(simpleStore, reducer)
                .chain(testMiddleware);
        dispatcher.dispatch("action");

        assertEquals("test2", testMiddleware.states().get(1));
    }

    @Test
    public void action_after_dispatch() {
        SimpleStore<String> simpleStore = new SimpleStore<>("test");
        TestMiddleware<String, String, String> testMiddleware = new TestMiddleware<>(simpleStore);
        Dispatcher<String, String> dispatcher = Dispatcher.forStore(simpleStore, Reducers.<String, String>id())
                .chain(testMiddleware);
        dispatcher.dispatch("action");

        assertEquals("action", testMiddleware.actions().get(0));
    }
}
