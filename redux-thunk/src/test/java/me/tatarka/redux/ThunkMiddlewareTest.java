package me.tatarka.redux;

import me.tatarka.redux.middleware.TestMiddleware;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ThunkMiddlewareTest {

    @Test
    public void thunk_run_when_dispatched() {
        Thunk<String, String> thunk = new Thunk<String, String>() {
            @Override
            public void run(Store<String, String> store) {
                store.dispatch("action1");
                store.dispatch("action2");
            }
        };
        TestMiddleware<Object, String> testMiddleware = new TestMiddleware<>();
        ListenerStore<Object, String> store = ListenerStore.create("test1", Reducers.<Object, String>id(), new ThunkMiddleware<Object, String>(), testMiddleware);
        store.dispatch(thunk);

        assertEquals("action1", testMiddleware.actions().get(0));
        assertEquals("action2", testMiddleware.actions().get(1));
    }
}
