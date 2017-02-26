package me.tatarka.redux;

import me.tatarka.redux.middleware.TestMiddleware;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ThunkDispatcherTest {

    @Test
    public void thunk_run_when_dispatched() {
        Thunk<String, String> thunk = new Thunk<String, String>() {
            @Override
            public void run(Dispatcher<String, String> dispatcher) {
                dispatcher.dispatch("action1");
                dispatcher.dispatch("action2");
            }
        };
        SimpleStore<String> store = new SimpleStore<>("test1");
        TestMiddleware<String, String, String> testMiddleware = new TestMiddleware<>(store);
        ThunkDispatcher<String, String> dispatcher = new ThunkDispatcher<>(Dispatcher.forStore(store, Reducers.<String, String>id())
                .chain(testMiddleware));
        dispatcher.dispatch(thunk);

        assertEquals("action1", testMiddleware.actions().get(0));
        assertEquals("action2", testMiddleware.actions().get(1));
    }
}
