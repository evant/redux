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
        Thunk<String> thunk = new Thunk<String>() {
            @Override
            public void run(Store<String> store) {
                store.dispatch("action1");
                store.dispatch("action2");
            }
        };
        TestMiddleware<String> testMiddleware = new TestMiddleware<>();
        ObservableStore<String> store = new ObservableStore<>("test1", Reducers.<Object, String>id(), new ThunkMiddleware<String>(), testMiddleware);
        store.dispatch(thunk);

        assertEquals("action1", testMiddleware.actions().get(0));
        assertEquals("action2", testMiddleware.actions().get(1));
    }
}
